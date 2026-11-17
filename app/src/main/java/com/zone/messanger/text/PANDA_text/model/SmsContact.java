package com.zone.messanger.text.PANDA_text.model;

public class SmsContact {

    private final String name;
    private final String number;
    private final String photoUri;

    public SmsContact(String name, String number, String photoUri) {
        this.name = name;
        this.number = number;
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}
