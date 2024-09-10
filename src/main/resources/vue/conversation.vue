<template id="conversation">
  <article>
    <div v-for="(message, index) in messages" class="flex flex-col p-4" :class="{ 'mt-auto': index === 0 }">
      <div v-if="message.direction === 'FROM_SUBSCRIBER'" class="flex flex-row-reverse">
        <div class="flex flex-col">
          <div class="text-orange-100 text-xs max-w self-end mb-1">{{ dateTime(message.timestamp) }}</div>
          <div class="bg-orange-950 text-orange-100 p-3 rounded-lg max-w-prose whitespace-pre leading-4">
            {{ message.content }}
          </div>
        </div>
      </div>
      <div v-else class="flex flex-row">
        <div class="flex flex-col">
          <div class="text-orange-100 text-xs mb-1">{{ dateTime(message.timestamp) }}</div>
          <div class="bg-gray-800 text-orange-100 p-3 rounded-lg max-w-prose whitespace-pre leading-4">
            {{ message.content }}
          </div>
        </div>
      </div>
    </div>
  </article>
</template>
<script type="module">
import { humanDate, humanTime } from "/js/time.js";

app.component("conversation", {
  props: {
    messages: { type: Array, required: true },
  },
  methods: {
    dateTime(timestamp) {
      const date = new Date(timestamp);
      return `${humanDate(date)} at ${humanTime(date)}`;
    },
  },
  template: "#conversation",
});
</script>