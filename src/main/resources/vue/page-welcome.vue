<template id="page-welcome">
  <div class="page-welcome h-full w-full text-gray-300">
    <nav class="flex flex-col h-full">
      <h1 class="text-2xl p-4"><button @click="activeConversationMsisdn = null">BrevPhoenix ✉️🕊🔥</button></h1>
      <conversation-item
          v-for="(c, msisdn, i) in conversationsMap"
          @click="setActive(msisdn)"
          :key="msisdn"
          :msisdn="msisdn"
          :name="c.name"
          :last-message="c.messages.at(-1)"
          :active="isActive(msisdn, i)"
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
          <div v-for="(m, i) in activeConversation.messages" class="flex flex-col p-4" :class="{ 'mt-auto': i === 0 }">
            <div v-if="m.direction === 'out'" class="flex flex-row-reverse">
              <div class="flex flex-col">
                <div class="text-gray-200 text-xs max-w self-end">{{ m.time.toLocaleTimeString() }}</div>
                <div class="bg-orange-950 text-gray-300 p-2 rounded max-w-prose">{{ m.message }}</div>
              </div>
            </div>
            <div v-else class="flex flex-row">
              <div class="flex flex-col">
                <div class="text-gray-200 text-xs">{{ m.time.toLocaleTimeString() }}</div>
                <div class="bg-gray-800 text-gray-300 p-2 rounded max-w-prose">{{ m.message }}</div>
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
      conversationsMap: {
        "Posten": {
          msisdn: "Posten",
          messages: [{
            time: new Date(),
            message: "Hei! Din leveranse fra Vetzoo.no er på vei. Den blir levert hjem. Se planlagt leveringstidspunkt her: https://youtu.be/dQw4w9WgXcQ"
          },],
        },
        "+4798653759": {
          msisdn: "+4798653759",
          name: "Jørund Fagerjord",
          messages: [
            {time: new Date("2021-12-10T12:00:03+01:00"), message: "More testing 🤠", direction: "out"},
            {time: new Date("2021-12-10T12:00:05+01:00"), message: "More testing 🤠", direction: "in"},
            {time: new Date("2021-01-01T00:00:01+01:00"), message: "Happy new year testing! 🥳", direction: "out"},
            {time: new Date("2021-01-01T00:00:01+03:00"), message: "Happy new year testing! 🥳", direction: "in"},
          ],
        },
      },
    }
  },
  computed: {
    activeConversation() {
      return this.conversationsMap[this.activeConversationMsisdn];
    },
  },
  methods: {
    isActive(msisdn) {
      return this.activeConversationMsisdn === msisdn;
    },
    setActive(msisdn) {
      this.activeConversationMsisdn = msisdn;
      setTimeout(() => { this.$refs.messages.scrollTop = this.$refs.messages.scrollHeight; }, 0);
    },
    sendMessage(e) {
      if (this.message.trim() === "") {
        return;
      }
      this.activeConversation.messages.push({
        time: new Date(),
        message: this.message,
        direction: "out",
      });
      this.message = "";
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
