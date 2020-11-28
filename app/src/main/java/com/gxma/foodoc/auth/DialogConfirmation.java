package com.gxma.foodoc.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gxma.foodoc.R;


public class DialogConfirmation extends AlertDialog.Builder {

    public DialogConfirmation(Context context) {
        super(context);
        setMessage(R.string.dialog_msg);
        setPositiveButton("oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ok();
            }
        })
                .setNegativeButton("annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancel();
                    }
                });
    }

    public void ok() {

    }

    public void cancel() {

    }

}
