package com.example.musicapp.util;

import android.app.AlertDialog;
import android.content.Context;

import com.example.musicapp.MainActivity;

public class DialogUtil {
    public static void showDialog(Context context, String title ,String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).create().show();
    }
}
