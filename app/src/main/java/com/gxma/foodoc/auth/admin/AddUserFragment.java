package com.gxma.foodoc.auth.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.base.BaseActivity;
import com.gxma.foodoc.base.BaseDialogFragment;
import com.gxma.foodoc.models.User;

public class AddUserFragment extends BaseDialogFragment {

    EditText emailEditText;
    EditText usernameEditText;
    boolean valid = true;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        if (getArguments() != null) {
            getArguments().getString("username");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_user_layout, null);
        emailEditText = (EditText)dialogView.findViewById(R.id.email);
        usernameEditText = (EditText)dialogView.findViewById(R.id.username);
        if (!valid)
            emailEditText.setError("Entrer une adresse email valide");
        builder.setView(dialogView)
                .setPositiveButton(R.string.add_user, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!validate()) return;
                        String email = emailEditText.getText().toString();
                        String username = usernameEditText.getText().toString();
                        User user = new User();
                        user.setEmail(email);
                        user.setUsername(username);
                        UserHelper.createUser(user);
                        ((BaseActivity)getActivity()).successNotification();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
        onCreateDialog(getArguments()).show();
    }

    private boolean validate() {
        valid = true;
        String email = emailEditText.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
        } else emailEditText.setError(null);

        return valid;
    }

}