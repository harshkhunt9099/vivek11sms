package com.zone.messanger.text.PANDA_text.model;

public class Message {

    public static final int TYPE_RECEIVED = 1;
    public static final int TYPE_SENT = 2;
    public static final int TYPE_DRAFT = 3;
    public static final int TYPE_FAILED = 5;
    public static final int TYPE_OUTBOX = 4;
    public static final int TYPE_QUEUED = 6;

    private final long id;
    private final long threadId;
    private final String address;
    private final String body;
    private final long date;
    private final int type;
    private final boolean read;

    public Message(long id, long threadId, String address, String body,
                    long date, int type, boolean read) {
        this.id = id;
        this.threadId = threadId;
        this.address = address;
        this.body = body;
        this.date = date;
        this.type = type;
        this.read = read;
    }

    public long getId() {
        return id;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public long getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isOutgoing() {
        return type != TYPE_RECEIVED;
    }

    public boolean isFailed() {
        return type == TYPE_FAILED;
    }
}
