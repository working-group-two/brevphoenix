<template id="page-sign-in">
  <div class="page-sign-in h-full w-full grid place-content-center bg-amber-950 text-orange-100">
    <img src="/favicon.jpeg" alt="decorative phoenix" width="1024" height="1024" class="max-w-lg rounded-3xl block">
    <h1 class="text-2xl p-4 text-center">Sign in to BrevPhoenix—SMS is back</h1>
    <form v-if="!pinSent" class="flex flex-col gap-2">
      <input
          class="rounded p-2 text-gray-950 bg-amber-100"
          v-model="phoneNumber"
          placeholder="999 12 345"
          type="tel"
          autocomplete="tel"
          key="phone"
      >
      <button :disabled="sendingPin" @click="sendPin" class="rounded p-2 bg-amber-800">Send pin</button>
    </form>
    <div v-if="pinSent">
      <p>PIN sent to {{ phoneNumber }}</p>
      <input
          v-model="pin"
          placeholder="1234"
          type="tel"
          autocomplete="off"
          key="pin"
          maxlength="4"
      >
      <input>
      <button :disabled="validatingPin" @click="validatePin">Verify</button>
      <button @click="resetForm">Go back</button>
    </div>
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
            : "Failed to send PIN to your number, please try again.";
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
        alert("Failed to validate your PIN, please start over.")
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
