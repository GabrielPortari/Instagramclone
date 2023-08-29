package com.example.instagramclone.helper;

import android.util.Base64;

public class Base64Custom {
    public static String codeBase64(String s){
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT).replaceAll("\\n|\\r", "");
    }
}
