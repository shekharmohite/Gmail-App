package com.example.gmail.util;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class Constants {

    public static final int RC_SIGN_IN = 9001;
    // Bundle key for account object
    public static final String KEY_ACCOUNT = "key_account";
    // Global instance of the HTTP transport
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    // Global instance of the JSON factory
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final Long PAGE_SIZE = 10L;
}
