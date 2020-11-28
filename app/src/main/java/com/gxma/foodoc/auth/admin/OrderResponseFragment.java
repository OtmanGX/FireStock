package com.gxma.foodoc.auth.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.OrderHelper;
import com.gxma.foodoc.models.Order;

public class OrderResponseFragment extends DialogFragment {
    Switch switch1;
    EditText descriptionEditText;
    EditText ttcEditText;
    Context context;
    Order order;
    String idOrder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        if (getArguments() != null) {
            idOrder = getArguments().getString("idOrder");
            order = (Order) getArguments().getSerializable("order");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.order_response_layout, null);
        switch1 = (Switch) dialogView.findViewById(R.id.switch1);
        descriptionEditText = (EditText)dialogView.findViewById(R.id.description);
        ttcEditText = (EditText)dialogView.findViewById(R.id.ttc);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch1.setText(b? getString(R.string.order_accepted):
                        getString(R.string.order_refused));
            }
        });

        builder.setView(dialogView)
//                .setTitle(R.string.add_user_fragment_title)
                // Add action buttons
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String response = descriptionEditText.getText().toString();
                        int state = switch1.isChecked() ? 1 : 2;
                        order.setResponse(response);
                        order.setState(state);
                        order.setPricettc(Float.parseFloat(ttcEditText.getText().toString()));
                        OrderHelper.updateOrder(idOrder, order).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

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


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            this.context = context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement OrderResponseFragment");
        }
    }


}