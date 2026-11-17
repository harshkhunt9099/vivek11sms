package com.zone.messanger.text.PANDA_text.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Required component for default-SMS-app eligibility. Full MMS download
 * (fetching the message from the carrier's MMSC over the mobile data APN)
 * is a large amount of carrier-specific plumbing that is out of scope here;
 * this receiver simply acknowledges the push so the framework doesn't retry
 * indefinitely. Plain SMS text messaging (the app's core feature) is fully
 * implemented in MessageReceiver / SmsRepository.
 */
public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // No-op: see class doc.
    }
}
