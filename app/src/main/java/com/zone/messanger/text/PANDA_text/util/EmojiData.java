package com.zone.messanger.text.PANDA_text.util;

/**
 * Plain-Unicode emoji grouped into the categories shown as tabs in the emoji
 * picker. No network or external emoji font is required — these render with
 * whatever emoji font the device already ships.
 */
public final class EmojiData {

    private EmojiData() {
    }

    public static final String[] SMILEYS = {
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "🙂", "🙃",
            "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😙",
            "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔",
            "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "🤐", "😴", "😪",
            "😌", "😔", "😕", "🙁", "☹️", "😟", "😤", "😢", "😭", "😦",
            "😧", "😨", "😩", "🤯", "😬", "😰", "😱", "🥵", "🥶", "😳",
            "🤪", "😵", "🥴", "😠", "😡", "🤬", "😷", "🤒", "🤕", "🤢"
    };

    public static final String[] PEOPLE = {
            "👍", "👎", "👊", "✊", "🤛", "🤜", "🤞", "✌️", "🤟", "🤘",
            "👌", "🤌", "🤏", "👈", "👉", "👆", "👇", "☝️", "✋", "🤚",
            "🖐️", "🖖", "👋", "🤙", "💪", "🙏", "✍️", "💅", "🤳", "👏",
            "🙌", "👐", "🤲", "🫶", "❤️", "👀", "👶", "🧑", "👦", "👧",
            "👩", "👨", "🧓", "👴", "👵", "😎", "🤓", "🥳", "😺", "😹"
    };

    public static final String[] ANIMALS_NATURE = {
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨",
            "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐔",
            "🐧", "🐦", "🐤", "🦆", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄",
            "🐝", "🐛", "🦋", "🐌", "🐞", "🐢", "🐍", "🦖", "🐬", "🐳",
            "🌵", "🌲", "🌳", "🌴", "🌸", "🌻", "🌹", "🍀", "🌈", "☀️"
    };

    public static final String[] FOOD = {
            "🍏", "🍎", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈",
            "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦",
            "🌽", "🥕", "🍞", "🥐", "🧀", "🍗", "🍔", "🍟", "🍕", "🌮",
            "🌯", "🍜", "🍛", "🍣", "🍩", "🍪", "🎂", "🍰", "🍫", "🍿",
            "🍵", "☕", "🥤", "🧋", "🍺", "🍷", "🍦", "🍨", "🍭", "🍬"
    };

    public static final String[] ACTIVITIES = {
            "⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🏉", "🎱", "🏓", "🏸",
            "🥊", "🥋", "⛳", "🏹", "🎣", "🥏", "🛹", "🏄", "🏊", "🚴",
            "🏆", "🥇", "🥈", "🥉", "🎖️", "🎗️", "🎮", "🎲", "🎯", "🎳",
            "🎸", "🎹", "🥁", "🎺", "🎻", "🎨", "🎬", "🎤", "🎧", "📷",
            "🚗", "✈️", "🚀", "⛵", "🚲", "🏍️", "🎡", "🎢", "🎪", "🎉"
    };

    public static final String[] SYMBOLS = {
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
            "✝️", "☪️", "🕉️", "☸️", "✡️", "🔯", "🕎", "☯️", "✔️", "✅",
            "❌", "❎", "➕", "➖", "➗", "✖️", "♾️", "💯", "🔥", "✨",
            "⭐", "🌟", "💫", "💥", "💤", "💬", "🔔", "🔕", "📌", "🔒"
    };

    public static String[] forCategory(int index) {
        switch (index) {
            case 0:
                return SMILEYS;
            case 1:
                return PEOPLE;
            case 2:
                return ANIMALS_NATURE;
            case 3:
                return FOOD;
            case 4:
                return ACTIVITIES;
            case 5:
                return SYMBOLS;
            default:
                return SMILEYS;
        }
    }
}
