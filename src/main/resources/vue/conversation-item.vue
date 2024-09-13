<template id="conversation-item">
  <button class="conversation-item grid text-orange-200 p-4 hover:bg-stone-600" :class="{ 'bg-orange-800': active }" @click="$emit('click', msisdn)">
    <span class="msisdn">{{ msisdn }}<span v-if="name != null" class="text-gray-400 text-sm"> ({{ name }})</span></span>
    <span v-if="lastMessage != null" class="time justify-self-end text-xs text-gray-400">{{ timeAgo }}</span>
    <span v-if="lastMessage != null" class="message"><template v-if="lastMessage.direction === 'FROM_SUBSCRIBER'">You: </template>{{ lastMessage.content }}</span>
  </button>
</template>
<script type="module">
import { humanDate, humanTime } from "/js/time.js";

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
      const lastMessageDate = new Date(this.lastMessage.timestamp);
      const diff = new Date() - lastMessageDate;
      if (diff < 1000 * 60 * 60 * 24) {
        return humanTime(lastMessageDate);
      }
      return humanDate(lastMessageDate);
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
