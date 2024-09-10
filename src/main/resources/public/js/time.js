const dateFormat = new Intl.DateTimeFormat("en-US", { day: "numeric", month: "short" });
const dateFormatWithYear = new Intl.DateTimeFormat("en-US", { day: "numeric", month: "short", year: "numeric" });
const timeFormat = new Intl.DateTimeFormat("en-US", { hour: "numeric", minute: "numeric", hour12: false });

export function humanDate(date) {
    const today = new Date();
    const yesterday = new Date().setDate(today.getDate() - 1);
    if (date.getFullYear() !== today.getFullYear()) return dateFormatWithYear.format(date);
    const formattedDate = dateFormat.format(date);
    if (formattedDate === dateFormat.format(today)) return "Today";
    if (formattedDate === dateFormat.format(yesterday)) return "Yesterday";
    return formattedDate;
}

export function humanTime(date) {
    return timeFormat.format(date);
}