export function burn(ms = 400) {
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

    const overflow = document.documentElement.style.overflow;
    document.documentElement.style.overflow = "hidden";
    document.documentElement.append(el);
    el.animate(keyframes, timing);
    setTimeout(() => {
        el.remove();
        document.documentElement.style.overflow = overflow;
    }, 1.5 * ms);
}