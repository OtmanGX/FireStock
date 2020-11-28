package com.gxma.foodoc.auth.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.gxma.foodoc.R;
import com.gxma.foodoc.base.BaseDialogFragment;


public class ContactDialogFragment extends BaseDialogFragment {

    Button callButton, emailButton;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_contact, null);
        callButton = dialogView.findViewById(R.id.callButton);
        emailButton = dialogView.findViewById(R.id.emailButton);

        builder.setView(dialogView)
//                .setTitle(R.string.add_user_fragment_title)
                // Add action buttons
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });


        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri tel = Uri.parse("tel:"+((Button)v).getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, tel);
                startActivity(callIntent);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(view);
            }
        });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void sendEmail(View view) {
        String[] TO = {emailButton.getText().toString()};
//        String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//        emailIntent.putExtra(Intent.EXTRA_CC, CC);
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
