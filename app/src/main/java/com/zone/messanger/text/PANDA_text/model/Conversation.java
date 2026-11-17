package com.zone.messanger.text.PANDA_text.model;

/**
 * One row in the conversation (chat) list: a thread grouped by the other
 * party's phone number, with the most recent message's preview.
 */
public class Conversation {

    private final long threadId;
    private final String address;      // phone number of the other party
    private final String displayName;  // contact name, or the raw number if unknown
    private final String snippet;      // preview of the last message
    private final long timestamp;      // millis of the last message
    private final int unreadCount;
    private final boolean isRead;

    public Conversation(long threadId, String address, String displayName,
                         String snippet, long timestamp, int unreadCount, boolean isRead) {
        this.threadId = threadId;
        this.address = address;
        this.displayName = displayName;
        this.snippet = snippet;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.isRead = isRead;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getAddress() {
        return address;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSnippet() {
        return snippet;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public boolean isRead() {
        return isRead;
    }

    /** Very light heuristic used by the Personal / Transaction filter chips. */
    public boolean looksTransactional() {
        if (address == null) return false;
        boolean alphaSender = address.chars().anyMatch(Character::isLetter);
        String lower = (displayName + " " + snippet).toLowerCase();
        return alphaSender || lower.contains("otp") || lower.contains("credited")
                || lower.contains("debited") || lower.contains("a/c")
                || lower.contains("bank") || lower.contains("txn")
                || lower.contains("rs.") || lower.contains("rs ");
    }
}
