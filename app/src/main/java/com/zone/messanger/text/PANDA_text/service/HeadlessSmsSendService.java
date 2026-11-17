package com.zone.messanger.text.PANDA_text.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.zone.messanger.text.Repository.SmsRepository;


/**
 * Handles ACTION_RESPOND_VIA_MESSAGE, e.g. "quick reply" from the dialer's
 * incoming-call screen. Required for the system to offer this app as the
 * default SMS handler.
 */
public class HeadlessSmsSendService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Intent.ACTION_SENDTO.equals(intent.getAction())) {
            String address = intent.getData() != null ? intent.getData().getSchemeSpecificPart() : null;
            String body = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (address != null && body != null) {
                SmsRepository repository = new SmsRepository(getApplicationContext());
                long threadId = repository.getOrCreateThreadId(address);
                repository.sendSms(address, body, threadId);
            }
        }
        stopSelf();
        return START_NOT_STICKY;
    }
}
