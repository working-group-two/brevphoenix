<template id="page-welcome">
  <div class="page-welcome h-svh w-full text-gray-300">
    <nav class="flex flex-col h-full">
      <h1 class="text-2xl text-orange-600 p-4">
        <button @click="activeConversationMsisdn = null">BrevPhoenix</button>
      </h1>
      <form @submit.prevent="createNewConversation" class="p-4 flex flex-row gap-2">
        <input v-model.trim="newConversation" type="tel"
               class="bg-orange-100 outline-amber-400 placeholder-stone-400 text-amber-950 rounded p-2 block w-full"
               placeholder="+47 999 00 111">
        <button class="bg-orange-800 text-orange-100 p-2 rounded">Create</button>
      </form>
      <div class="overflow-y-auto flex flex-col">
        <transition-group name="conversation-list">
          <conversation-item
              v-for="[msisdnOrText, conversation] in sortedConversations"
              :key="msisdnOrText"
              :msisdn="msisdnOrText"
              :last-message="conversation.at(-1)"
              :active="isActive(msisdnOrText)"
              @click="setActive(msisdnOrText)"
          ></conversation-item>
        </transition-group>
      </div>
    </nav>
    <main class="flex-grow bg-gradient-to-tl from-black to-stone-700 h-svh">
      <conversation-empty-state v-if="activeConversationMsisdn == null"></conversation-empty-state>
      <div v-else class="flex flex-col h-full text-orange-100">
        <h2 class="text-2xl p-4 text-orange-600">{{ activeConversationMsisdn }}<span
            v-if="activeConversationName != null"> ({{ activeConversationName }})</span></h2>
        <div class="flex-grow overflow-y-auto flex flex-col h-full justify-end" ref="messages">
          <conversation :messages="activeConversation"></conversation>
        </div>
        <form @submit.prevent="sendMessage"
              class="flex flex-row p-4 gap-2">
          <textarea v-model="message" ref="message" type="text"
                    @keydown.enter.shift.exact.prevent="message += '\n'"
                    @keydown.prevent.ctrl.enter="sendMessage"
                    @keydown.prevent.meta.enter="sendMessage"
                    @keydown.prevent.exact.enter="sendMessage"
                    class="message flex-grow bg-stone-600 text-orange-100 placeholder:text-orange-50 p-2 rounded"
                    placeholder="Text message"
          ></textarea>
          <button class="bg-stone-600 text-gray-300 p-2 rounded h-full">Send</button>
        </form>
      </div>
    </main>
  </div>
</template>
<script type="module">
import {burn} from "/js/fire.js";

app.component("page-welcome", {
  template: "#page-welcome",
  data() {
    return {
      message: "",
      newConversation: "",
      activeConversationMsisdn: null,
      msisdnToSmsMap: {},
      audioNotification: new Audio("/bip.mp3"),
      lastSentMessage: null,
    }
  },
  created() {
    this.getSms();
    this.registerWs();
  },
  computed: {
    activeConversation() {
      return this.msisdnToSmsMap[this.activeConversationMsisdn];
    },
    activeConversationName() {
      return null;
    },
    normalizedNewConversationMsisdn() {
      return "+" + this.newConversation.replace(/[^0-9]/g, "");
    },
    sortedConversations() {
      return Object.entries(this.msisdnToSmsMap)
          .toSorted(([aMsisdn, aMessages], [bMsisdn, bMessages]) => (
              bMessages.at(-1).timestamp.localeCompare(aMessages.at(-1).timestamp))
          );
    },
  },
  methods: {
    createNewConversation() {
      const msisdn = this.normalizedNewConversationMsisdn;
      if (msisdn === "+") {
        return;
      }
      this.newConversation = "";
      this.msisdnToSmsMap[msisdn] = this.msisdnToSmsMap[msisdn] ?? [];
      this.setActive(msisdn);
      setTimeout(() => {
        this.$refs["message"].focus()
      }, 0)
    },
    registerWs() {
      const secureWebsocketOrNot = location.protocol === 'https:' ? 'wss' : 'ws';
      const ws = new WebSocket(`${secureWebsocketOrNot}://${window.location.host}/api/sms`);
      // const ws = {};
      ws.onmessage = e => {
        const sms = JSON.parse(e.data);
        const conversationMsisdn = this.getConversationMsisdn(sms);
        const isActiveConversation = this.activeConversationMsisdn === conversationMsisdn;
        const isFromSubscriber = sms.direction === "FROM_SUBSCRIBER";
        const lastSmsInConversation = this.msisdnToSmsMap[conversationMsisdn].at(-1);
        const contentIsTheSame = lastSmsInConversation?.content === sms.content;
        const isWithinLastFewSeconds = lastSmsInConversation != null && new Date(sms.timestamp) - new Date(lastSmsInConversation.timestamp) < 4000;
        if (isActiveConversation && isFromSubscriber && contentIsTheSame && isWithinLastFewSeconds) {
          return;
        }
        this.msisdnToSmsMap[conversationMsisdn] = [...(this.msisdnToSmsMap[conversationMsisdn] ?? []), sms];
        if (this.activeConversationMsisdn === conversationMsisdn) {
          this.scrollToBottomOfMessages();
        }

        this.audioNotification.play().catch(e => {
          console.error("Failed to play audio notification, probably missing user interaction so far.", e);
        });
        burn();
      };
      ws.onclose = e => {
        setTimeout(() => {
          this.registerWs();
        }, 1000);
      };
    },
    getConversationMsisdn(sms) {
      if (sms.direction === "FROM_SUBSCRIBER") {
        return sms.to.phoneNumber.e164;
      }
      return sms.from.phoneNumber.e164;
    },
    isActive(msisdn) {
      return this.activeConversationMsisdn === msisdn;
    },
    setActive(msisdn) {
      this.activeConversationMsisdn = msisdn;
      this.scrollToBottomOfMessages();
    },
    scrollToBottomOfMessages() {
      const method = () => {
        this.$refs.messages.scrollTop = this.$refs.messages.scrollHeight;
      };
      this.$nextTick(method);
      setTimeout(method, 10);
    },
    sendMessage() {
      if (this.message.trim() === "") {
        return;
      }
      const id = window.crypto.randomUUID();
      const to = this.activeConversationMsisdn;
      const content = this.message;
      this.lastSentMessage = {
        id,
        timestamp: new Date().toISOString(),
        content,
        direction: "FROM_SUBSCRIBER",
      };
      this.activeConversation.push(this.lastSentMessage);
      this.scrollToBottomOfMessages();
      this.message = "";
      axios.post(`/api/sms/${to}`, content)
          .then(() => {
          })
          .catch(e => {
            console.error(e);
            alert(`Failed to send message, please try again later. Manually save your message to ${to} if you want to keep it:\n` + content);
            this.msisdnToSmsMap[to] = this.msisdnToSmsMap[to].filter(m => m.id !== id);
          });
    },
    getSms() {
      axios.get("/api/sms").then(res => {
        this.msisdnToSmsMap = res.data;
        const firstConversation = this.sortedConversations[0];
        if (firstConversation != null) {
          this.setActive(firstConversation[0]);
        }
      });
    },
  },
});
</script>
<style>
.page-welcome {
  --bg-color-deg: 20deg;
  background: hsl(var(--bg-color-deg) 30% 2% / 1);
  display: flex;
  flex-direction: row;
}

nav {
  flex: 0 0 370px;
  background: hsl(var(--bg-color-deg) 30% 3% / 1);
}

h1 {
  color: hsl(var(--bg-color-deg) 50% 80% / 1);
}

.conversation-list-enter-active,
.conversation-list-leave-active,
.conversation-list-move {
  transition: all 0.5s ease;
}

.conversation-list-leave-active {
  position: absolute;
}

.conversation-list-enter-from,
.conversation-list-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}

::-webkit-scrollbar {
  width: 8px;
  overflow: hidden;
}

/* Track */
::-webkit-scrollbar-track {
  background: hsl(var(--bg-color-deg) 30% 3% / .5);
  border-radius: 4px;
}

/* Handle */
::-webkit-scrollbar-thumb {
  background: #555;
  border-radius: 5px;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: #777;
}
</style>
