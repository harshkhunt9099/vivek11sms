package com.zone.messanger.text.PANDA_text.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.zone.messanger.text.PANDA_text.model.SmsContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Small helper around the Contacts provider: resolve a phone number to a
 * saved contact's name/photo, and list every contact with a phone number so
 * the "New Chat" screen can offer them as recipients.
 */
public final class ContactUtils {

    private ContactUtils() {
    }

    /** Returns the saved contact name for a number, or the number itself if unknown. */
    public static String resolveName(Context context, String number) {
        if (TextUtils.isEmpty(number)) return "Unknown";
        // Short codes / sender IDs (alphanumeric, e.g. "JK-BOBSMS-S") are not phone numbers.
        if (number.chars().anyMatch(Character::isLetter)) {
            return number;
        }
        try {
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
            try (Cursor cursor = context.getContentResolver()
                    .query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(0);
                    if (!TextUtils.isEmpty(name)) return name;
                }
            }
        } catch (SecurityException ignored) {
            // No READ_CONTACTS permission yet.
        }
        return number;
    }

    public static List<SmsContact> listContacts(Context context) {
        List<SmsContact> result = new ArrayList<>();
        try {
            String[] projection = {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            };
            try (Cursor cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection, null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")) {
                if (cursor != null) {
                    java.util.Set<String> seen = new java.util.HashSet<>();
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(0);
                        String number = cursor.getString(1);
                        String photo = cursor.getString(2);
                        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(name)) continue;
                        String key = name + "|" + number.replaceAll("\\s+", "");
                        if (!seen.add(key)) continue;
                        result.add(new SmsContact(name, number, photo));
                    }
                }
            }
        } catch (SecurityException ignored) {
            // No READ_CONTACTS permission yet.
        }
        return result;
    }

    /** Stable, pleasant avatar color chosen from the name/number so it never flickers. */
    public static int avatarColorFor(Context context, String key) {
        int[] palette = {
                com.zone.messanger.text.R.color.avatar_1,
                com.zone.messanger.text.R.color.avatar_2,
                com.zone.messanger.text.R.color.avatar_3,
                com.zone.messanger.text.R.color.avatar_4,
                com.zone.messanger.text.R.color.avatar_5,
                com.zone.messanger.text.R.color.avatar_6,
                com.zone.messanger.text.R.color.avatar_7,
                com.zone.messanger.text.R.color.avatar_8,
        };
        int hash = key == null ? 0 : Math.abs(key.hashCode());
        int colorRes = palette[hash % palette.length];
        return androidx.core.content.ContextCompat.getColor(context, colorRes);
    }

    public static String initialFor(String displayName) {
        if (TextUtils.isEmpty(displayName)) return "#";
        char c = displayName.trim().charAt(0);
        return String.valueOf(c).toUpperCase(Locale.getDefault());
    }
}
