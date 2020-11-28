package com.gxma.foodoc.auth.client;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.OrderHelper;
import com.gxma.foodoc.base.BaseActivity;
import com.gxma.foodoc.base.BaseDialogFragment;
import com.gxma.foodoc.models.Order;
import com.gxma.foodoc.models.Product;
import com.gxma.foodoc.models.User;

public class AddOrderFragment extends BaseDialogFragment {

    Product product;
    User user;

    //widgets
    EditText qteEditText;
    EditText descriptionEditText;
    CheckBox checkBox1, checkBox2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
            user = (User) getArguments().getSerializable("user");
            }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_order_layout, null);
        qteEditText = (EditText)dialogView.findViewById(R.id.qte);
        descriptionEditText = (EditText)dialogView.findViewById(R.id.description);
        checkBox1 = (CheckBox) dialogView.findViewById(R.id.factureCheckBox) ;
        checkBox2 = (CheckBox) dialogView.findViewById(R.id.UrgentCheckBox) ;

        builder.setView(dialogView)
                .setPositiveButton(R.string.order, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int qte = Integer.parseInt(qteEditText.getText().toString());
                        String description = descriptionEditText.getText().toString();
                        Order order = new Order(product, qte, user);
                        order.setDescription(description);
                        order.setUrgent(checkBox2.isChecked());
                        order.setWithBill(checkBox1.isChecked());
                        OrderHelper.createOrder(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
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


    private boolean validate() {
        boolean valid = true;
        String qte = qteEditText.getText().toString();
        if (qte.isEmpty() || Float.parseFloat(qte)<1) {
            valid = false;
        }

        return valid;
    }

}
