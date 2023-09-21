<template id="page-welcome">
  <div class="page-welcome h-full w-full text-gray-300">
    <nav class="flex flex-col h-full">
      <h1 class="text-2xl p-4"><button @click="activeConversationMsisdn = null">BrevPhoenix ✉️🕊🔥</button></h1>
      <conversation-item
          v-for="(conversation, msisdnOrText) in msisdnToSmsMap"
          @click="setActive(msisdnOrText)"
          :key="msisdnOrText"
          :msisdn="msisdnOrText"
          :last-message="conversation.at(-1)"
          :active="isActive(msisdnOrText)"
      ></conversation-item>
    </nav>
    <main class="flex-grow bg-gradient-to-tr from-orange-900 to-amber-600">
      <div v-if="activeConversationMsisdn == null" class="flex flex-col items-center overflow-y-auto justify-center h-full" style="background: hsl(var(--bg-color-deg) 50% 2% / 1);">
        <img src="/favicon.jpeg" alt="decorative phoenix" width="1024" height="1024" class="max-w-lg rounded-3xl block">
        <h2 class="text-3xl mt-4">Welcome to BrevPhoenix</h2>
        <p class="text-gray-400"><strong>SMS is back.</strong> Select a conversation to get started.</p>
      </div>
      <div v-else class="flex flex-col h-full">
        <h2 class="text-2xl p-4">{{ activeConversation.msisdn }}<span v-if="activeConversation.name != null"> ({{ activeConversation.name }})</span></h2>
        <div class="flex-grow overflow-y-auto flex flex-col h-full" ref="messages">
          <div v-for="(m, i) in activeConversation" class="flex flex-col p-4" :class="{ 'mt-auto': i === 0 }">
            <div v-if="m.direction === 'FROM_SUBSCRIBER'" class="flex flex-row-reverse">
              <div class="flex flex-col">
                <div class="text-gray-200 text-xs max-w self-end">{{ new Date(m.timestamp).toLocaleTimeString() }}</div>
                <div class="bg-orange-950 text-gray-300 p-2 rounded max-w-prose">{{ m.content }}</div>
              </div>
            </div>
            <div v-else class="flex flex-row">
              <div class="flex flex-col">
                <div class="text-gray-200 text-xs">{{ new Date(m.timestamp).toLocaleTimeString() }}</div>
                <div class="bg-gray-800 text-gray-300 p-2 rounded max-w-prose">{{ m.content }}</div>
              </div>
            </div>
          </div>
        </div>
        <form @submit.prevent="sendMessage" class="flex flex-row p-4 gap-1">
          <input v-model="message" type="text" class="flex-grow bg-gray-800 text-gray-300 p-2 rounded" placeholder="Type a message...">
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
  },
  methods: {
    registerWs() {
      const ws = new WebSocket(`ws://${window.location.host}/api/sms`);
      ws.onmessage = e => {
        const sms = JSON.parse(e.data);
        const msisdn = this.getMsisdn(sms);
        if (this.msisdnToSmsMap[msisdn] == null) {
          this.msisdnToSmsMap[msisdn] = [];
        }
        const isActiveConversation = this.activeConversationMsisdn === msisdn;
        const isFromSubscriber = sms.direction === "FROM_SUBSCRIBER";
        if (isActiveConversation && isFromSubscriber) {
          return;
        }
        this.msisdnToSmsMap[msisdn].push(sms);
        if (this.activeConversationMsisdn === msisdn) {
          setTimeout(() => { this.$refs.messages.scrollTop = this.$refs.messages.scrollHeight; }, 0);
        }
      };
      ws.onclose = e => {
        console.log("ws closed", e);
        setTimeout(() => { this.registerWs(); }, 1000);
      };
    },
    getMsisdn(sms) {
      if (sms.direction === "FROM_SUBSCRIBER") {
        return sms.from.phoneNumber.e164;
      }
      return sms.to.phoneNumber.e164;
    },
    isActive(msisdn) {
      return this.activeConversationMsisdn === msisdn;
    },
    setActive(msisdn) {
      this.activeConversationMsisdn = msisdn;
      setTimeout(() => { this.$refs.messages.scrollTop = this.$refs.messages.scrollHeight; }, 0);
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
      this.message = "";
      axios.post(`/api/sms/${to}`, content)
          .then(() => {})
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

main {
}
</style>
