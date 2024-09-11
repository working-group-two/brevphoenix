export function burn(ms = 400) {
    const wrapper = document.createElement("div");
    wrapper.style.cssText = `
        margin: 0;
        padding: 0;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        overflow: hidden;
    `;
    const el = document.createElement("div");
    el.style.cssText = `
        position: absolute;
        top: 0;
        left: 0;
        width: 300svw;
        height: 300svh;
        background: radial-gradient(circle, rgba(255, 0, 0, 0.2) 20%, rgba(255, 165, 0, 0.2) 50%, transparent 70%);
        transform: translate(100%, 100%);
    `;

    const keyframes = [
        { transform: "translate(50%, 150%)" },
        { transform: "translate(-50%, -100%)" },
    ];
    const timing = { duration: ms, iterations: 1 };

    wrapper.append(el);
    document.documentElement.append(wrapper);
    el.animate(keyframes, timing);
    setTimeout(() => wrapper.remove(), 1.5 * ms);
}