<template id="conversation-item">
  <button class="conversation-item grid text-gray-300 p-4 hover:bg-orange-950" :class="{ active }" @click="$emit('click', msisdn)">
    <span class="msisdn">{{ msisdn }}<span v-if="name != null" class="text-gray-400 text-sm"> ({{ name }})</span></span>
    <span v-if="lastMessage != null" class="time justify-self-end text-xs text-gray-400">{{ timeAgo }}</span>
    <span v-if="lastMessage != null" class="message"><template v-if="lastMessage.direction === 'out'">You: </template>{{ lastMessage.message }}</span>
  </button>
</template>
<script>
app.component("conversation-item", {
  template: "#conversation-item",
  props: {
    msisdn: {
      type: String,
      required: true
    },
    name: {
      type: String,
      required: false,
    },
    lastMessage: {
      type: Object,
      required: false,
    },
    active: {
      type: Boolean,
      required: false,
      default: false,
    }

  },
  computed: {
    timeAgo() {
      if (this.lastMessage == null) {
        return null;
      }
      const diff = new Date() - this.lastMessage.time;
      if (diff < 1000 * 60 * 60 * 24) {
        return this.lastMessage.time.toLocaleTimeString();
      }
      return this.lastMessage.time.toLocaleDateString();
    }
  },
});
</script>
<style>
.conversation-item {
  grid-template-columns: 1fr 100px;
  grid-template-rows: 1fr 1fr;
  grid-template-areas:
        "msisdn time"
        "message time";
  text-align: left;
}

.conversation-item.active {
  background: hsl(var(--bg-color-deg) 50% 20%);
}

.conversation-item > * {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-item .msisdn {
  grid-area: msisdn;
}

.conversation-item .time {
  grid-area: time;
}

.conversation-item .message {
  grid-area: message;
}
</style>
