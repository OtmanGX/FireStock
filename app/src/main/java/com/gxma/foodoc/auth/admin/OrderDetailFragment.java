package com.gxma.foodoc.auth.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.gxma.foodoc.R;
import com.gxma.foodoc.base.BaseDialogFragment;
import com.gxma.foodoc.models.Order;

public class OrderDetailFragment extends BaseDialogFragment {

    Order order;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        if (getArguments() != null) {
            order= (Order) getArguments().getSerializable("order");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_order_detail, null);
        String state ="En suspens" ;
        if(order.getState()==1) state = getString(R.string.order_accepted);
        else if(order.getState()==2) state = getString(R.string.order_refused);
        ((TextView)dialogView.findViewById(R.id.product)).setText(order.getProduct().getName());
        ((TextView)dialogView.findViewById(R.id.amount)).setText(String.valueOf(order.getQuantity()));
        ((TextView)dialogView.findViewById(R.id.priceht)).
                setText(String.valueOf(order.getProduct().getPrice()*order.getQuantity())+ "DH");
        ((TextView)dialogView.findViewById(R.id.pricettc)).
                setText(String.valueOf(order.getPricettc())+ "DH");
        ((TextView)dialogView.findViewById(R.id.urgent)).
                setText(order.isUrgent()?"Oui":"Non");
        ((TextView)dialogView.findViewById(R.id.bill)).
                setText(order.isWithBill()?"Oui":"Non");
        ((TextView)dialogView.findViewById(R.id.orderState)).
                setText(state);
        ((TextView)dialogView.findViewById(R.id.description)).setText(order.getDescription());
        ((TextView)dialogView.findViewById(R.id.order_response)).setText(order.getResponse());
        ((TextView)dialogView.findViewById(R.id.username)).setText(order.getUser().getUsername());
        ((TextView)dialogView.findViewById(R.id.email)).setText(order.getUser().getEmail());


        builder.setView(dialogView)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }


}