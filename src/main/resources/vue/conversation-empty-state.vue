<template id="conversation-empty-state">
  <div
      class="flex flex-col items-center justify-center overflow-y-auto h-full text-stone-400"
      style="background: hsl(var(--bg-color-deg) 50% 2% / 1);"
  >
    <div class="flex flex-col max-w-lg items-center justify-center">
      <img src="/favicon.jpeg" alt="decorative phoenix" width="1024" height="1024"
           class="px-2 w-lg max-w-full rounded-3xl block">
      <h2 class="text-3xl mt-4">SMS has risen from the ashes</h2>
      <p class="text-lg">Select a conversation or create a new one</p>
    </div>
  </div>
</template>
<script>
app.component("conversation-empty-state", {
  template: "#conversation-empty-state",
});
</script>