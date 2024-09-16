<template id="page-sign-in">
  <div class="page-sign-in min-h-svh flex items-center sm:justify-center to-stone-900 from-amber-950 bg-gradient-to-t text-orange-100 sm:p-2 p-10 flex-col sm:flex-row sm:gap-20 gap-10">
    <div class="max-w-sm sm:h-auto min-h-80 flex flex-col gap-2">
      <img src="/favicon.jpeg" alt="decorative phoenix" width="1024" height="1024" class="max-w-full rounded-3xl block">
      <h1 class="text-4xl font-bold">BrevPhoenix</h1>
    </div>
    <form class="flex flex-col gap-4 grow-0 sm:w-56 w-full" @submit.prevent="() => { !pinSent ? sendPin() : validatePin()}">
      <h2 class="text-2xl">Sign in</h2>
      <p v-if="pinSent">Code sent to {{ phoneNumber }}</p>
      <input
          v-if="!pinSent"
          class="rounded p-2 text-gray-950 bg-amber-50"
          v-model="phoneNumber"
          placeholder="+47 999 12 345"
          type="tel"
          autocomplete="tel"
          required
          key="phone"
      >
      <input
          v-if="pinSent"
          class="rounded p-2 text-gray-950 bg-amber-50"
          v-model="pin"
          placeholder="1234"
          type="tel"
          autocomplete="off"
          key="pin"
          maxlength="4"
          required
          :disabled="validatingPin"
      >
      <button v-if="!pinSent" :disabled="sendingPin"
              class="rounded p-2 bg-orange-700 disabled:bg-slate-50 disabled:text-slate-500 disabled:border-slate-200 disabled:shadow-none">
        Send code
      </button>
      <button v-if="pinSent" :disabled="validatingPin"
              class="rounded p-2 bg-orange-700 disabled:bg-slate-50 disabled:text-slate-500 disabled:border-slate-200 disabled:shadow-none">
        Verify
      </button>
      <button v-if="pinSent" @click="resetForm" :disabled="validatingPin" class="">Go back</button>
    </form>
  </div>
</template>
<script>
app.component("page-sign-in", {
  template: "#page-sign-in",
  data: () => ({
    phoneNumber: "",
    sendingPin: false,
    pinSent: false,
    pin: "",
    validatingPin: false,
  }),
  methods: {
    sendPin() {
      this.sendingPin = true;
      axios.post("/api/auth/send-pin?phoneNumber=" + this.phoneNumber).then(() => {
        this.pinSent = true;
        setTimeout(() => this.$el.querySelector("input").focus(), 0);
      }).catch(err => {
        const message = err.response.status === 401
            ? "You are not authorized to sign in"
            : "Failed to send code to your number, please try again.";
        alert(message);
      }).finally(() => this.sendingPin = false)
    },
    validatePin() {
      this.validatingPin = true;
      axios.post("/api/auth/validate-pin?pin=" + this.pin).then(() => {
        // redirect after 1s delay
        setTimeout(() => location.href = "/", 1000);
      }).catch(() => {
        this.validatingPin = false;
        alert("Failed to validate your code, please start over.")
        this.resetForm();
      });
    },
    resetForm() {
      this.phoneNumber = "";
      this.sendingPin = false;
      this.pinSent = false;
      this.pin = "";
      this.validatingPin = false;
      setTimeout(() => this.$el.querySelector("input").focus(), 0);
    },
  },
});
</script>
