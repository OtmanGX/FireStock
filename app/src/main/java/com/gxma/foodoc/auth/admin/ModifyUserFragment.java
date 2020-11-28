package com.gxma.foodoc.auth.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.base.BaseActivity;


public class ModifyUserFragment extends DialogFragment {

    EditText emailEditText;
    EditText usernameEditText;
    String username;
    String email;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        if (getArguments() != null) {
            username = getArguments().getString("username");
            email = getArguments().getString("email");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_user_layout, null);
        emailEditText = (EditText)dialogView.findViewById(R.id.email);
        emailEditText.setText(email);
        emailEditText.setEnabled(false);
//        emailEditText.setVisibility(View.INVISIBLE);
        usernameEditText = (EditText)dialogView.findViewById(R.id.username);
        if (username!=null) usernameEditText.setText(username);
        builder.setView(dialogView)
                .setPositiveButton(R.string.modify_user, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String username = usernameEditText.getText().toString();
                        UserHelper.updateUsername(username, email);
                        ((BaseActivity)getActivity()).successNotification();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}