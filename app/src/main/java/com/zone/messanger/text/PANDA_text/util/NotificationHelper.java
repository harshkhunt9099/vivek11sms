package com.zone.messanger.text.PANDA_text.util;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zone.messanger.text.R;
import com.zone.messanger.text.PANDA_ads.MyApplication;
import com.zone.messanger.text.PANDA_text.activity.ChatActivity;

public final class NotificationHelper {

    private NotificationHelper() {
    }

    public static void showIncomingMessage(Context context, String address, String body, long threadId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String name = ContactUtils.resolveName(context, address);

        Intent openChat = new Intent(context, ChatActivity.class);
        openChat.putExtra(ChatActivity.EXTRA_THREAD_ID, threadId);
        openChat.putExtra(ChatActivity.EXTRA_ADDRESS, address);
        openChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, (int) threadId, openChat, flags);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, MyApplication.CHANNEL_ID_MESSAGES)
                        .setSmallIcon(R.drawable.ic_message_bubble)
                        .setContentTitle(name)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        try {
            NotificationManagerCompat.from(context).notify((int) threadId, builder.build());
        } catch (SecurityException ignored) {
            // Notification permission revoked between the check above and here.
        }
    }
}
