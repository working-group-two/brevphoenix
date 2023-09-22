<template id="page-welcome">
  <div class="page-welcome h-full w-full text-gray-300">
    <nav class="flex flex-col h-full">
      <h1 class="text-2xl text-orange-600 p-4">
        <button @click="activeConversationMsisdn = null">BrevPhoenix</button>
      </h1>
      <form @submit.prevent="createNewConversation" class="p-4">
        <input v-model.trim="newConversation" type="tel"
               class="bg-orange-400 outline-amber-400 placeholder-amber-800 text-amber-950 rounded p-2 block w-full"
               placeholder="New conversation">
      </form>
      <conversation-item
          v-for="(conversation, msisdnOrText) in msisdnToSmsMap"
          @click="setActive(msisdnOrText)"
          :key="msisdnOrText"
          :msisdn="msisdnOrText"
          :last-message="conversation.at(-1)"
          :active="isActive(msisdnOrText)"
      ></conversation-item>
    </nav>
    <main class="flex-grow bg-gradient-to-tl to-black from-amber-700">
      <div v-if="activeConversationMsisdn == null"
           class="flex flex-col items-center justify-center overflow-y-auto h-full text-orange-700"
           style="background: hsl(var(--bg-color-deg) 50% 2% / 1);">
        <div class="flex flex-col max-w-lg items-center justify-center">
          <img src="/favicon.jpeg" alt="decorative phoenix" width="1024" height="1024"
               class="px-2 w-lg max-w-full rounded-3xl block">
          <h2 class="text-3xl mt-4">SMS has rerisen</h2>
          <p class="text-lg text-orange-600">Select a conversation or create a new one</p>
        </div>
      </div>
      <div v-else class="flex flex-col h-full text-orange-100">
        <h2 class="text-2xl p-4 text-amber-600">{{ activeConversationMsisdn }}<span
            v-if="activeConversationName != null"> ({{ activeConversationName }})</span></h2>
        <div class="flex-grow overflow-y-auto flex flex-col h-full" ref="messages">
          <div v-for="(m, i) in activeConversation" class="flex flex-col p-4" :class="{ 'mt-auto': i === 0 }">
            <div v-if="m.direction === 'FROM_SUBSCRIBER'" class="flex flex-row-reverse">
              <div class="flex flex-col">
                <div class="text-orange-100 text-xs max-w self-end">{{
                    new Date(m.timestamp).toLocaleTimeString()
                  }}
                </div>
                <div class="bg-orange-950 text-orange-100 p-2 rounded max-w-prose">{{ m.content }}</div>
              </div>
            </div>
            <div v-else class="flex flex-row">
              <div class="flex flex-col">
                <div class="text-orange-100 text-xs">{{ new Date(m.timestamp).toLocaleTimeString() }}</div>
                <div class="bg-gray-800 text-orange-100 p-2 rounded max-w-prose">{{ m.content }}</div>
              </div>
            </div>
          </div>
        </div>
        <form @submit.prevent="sendMessage"
              class="flex flex-row p-4 gap-1">

          <textarea v-model="message" ref="message" type="text" @keydown.enter.shift.exact.prevent="message += '\n'"
                    @keydown.prevent.ctrl.enter="sendMessage"
                    @keydown.prevent.meta.enter="sendMessage"
                    class="message flex-grow bg-gray-800 text-orange-100 p-2 rounded"
                    placeholder="Type a message..."
          ></textarea>
          <button class="bg-gray-800 text-gray-300 p-2 rounded">Send</button>
        </form>
      </div>
    </main>
  </div>
</template>
<script>
app.component("page-welcome", {
  template: "#page-welcome",
  data() {
    return {
      message: "",
      newConversation: "",
      activeConversationMsisdn: null,
      msisdnToSmsMap: {},
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
      this.$nextTick(() => {
        this.$refs["message"].focus()
      });
    },
    registerWs() {
      const ws = new WebSocket(`ws://${window.location.host}/api/sms`);
      // const ws = {};
      ws.onmessage = e => {
        const sms = JSON.parse(e.data);
        const conversationMsisdn = this.getConversationMsisdn(sms);
        const isActiveConversation = this.activeConversationMsisdn === conversationMsisdn;
        const isFromSubscriber = sms.direction === "FROM_SUBSCRIBER";
        if (isActiveConversation && isFromSubscriber) {
          return;
        }
        this.msisdnToSmsMap[conversationMsisdn] = [...(this.msisdnToSmsMap[conversationMsisdn] ?? []), sms];
        if (this.activeConversationMsisdn === conversationMsisdn) {
          this.scrollToBottomOfMessages();
        }
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
      this.$nextTick(() => {
        this.$refs.messages.scrollTop = this.$refs.messages.scrollHeight;
      }, 0);
    },
    sendMessage() {
      if (this.message.trim() === "") {
        return;
      }
      const id = window.crypto.randomUUID();
      const to = this.activeConversationMsisdn;
      const content = this.message;
      this.activeConversation.push({
        id,
        timestamp: new Date().toISOString(),
        content,
        direction: "FROM_SUBSCRIBER",
      });
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
  background: hsl(var(--bg-color-deg) 30% 1% / .5);
  border-radius: 5px;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>
