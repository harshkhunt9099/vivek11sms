package com.zone.messanger.text.PANDA_text.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Telephony;

import com.zone.messanger.text.PANDA_text.util.NotificationHelper;

/**
 * Receives SMS_DELIVER, which is only sent to the current default SMS app.
 * It is responsible for writing the incoming message into the SMS provider
 * itself (the system does not do this for the default app) and for letting
 * the user know a new message arrived.
 */
public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_DELIVER_ACTION.equals(intent.getAction())) {
            return;
        }

        android.telephony.SmsMessage[] messages =
                Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages == null || messages.length == 0) return;

        String address = messages[0].getOriginatingAddress();
        StringBuilder bodyBuilder = new StringBuilder();
        long timestamp = messages[0].getTimestampMillis();
        for (android.telephony.SmsMessage m : messages) {
            if (m.getMessageBody() != null) bodyBuilder.append(m.getMessageBody());
        }
        String body = bodyBuilder.toString();
        if (address == null) return;

        long threadId = Telephony.Threads.getOrCreateThreadId(context, address);

        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, address);
        values.put(Telephony.Sms.BODY, body);
        values.put(Telephony.Sms.DATE, timestamp);
        values.put(Telephony.Sms.READ, 0);
        values.put(Telephony.Sms.SEEN, 0);
        values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);
        values.put(Telephony.Sms.THREAD_ID, threadId);

        Uri inserted = context.getContentResolver().insert(Telephony.Sms.CONTENT_URI, values);

        NotificationHelper.showIncomingMessage(context, address, body, threadId);

        Intent broadcast = new Intent("com.zone.messanger.text.text.MESSAGE_RECEIVED");
        broadcast.setPackage(context.getPackageName());
        broadcast.putExtra("threadId", threadId);
        context.sendBroadcast(broadcast);
    }
}
