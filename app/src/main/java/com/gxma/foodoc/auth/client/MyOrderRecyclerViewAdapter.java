package com.gxma.foodoc.auth.client;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gxma.foodoc.R;
import com.gxma.foodoc.auth.client.ProductFragment.OnProductFragmentListener;
import com.gxma.foodoc.models.Order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyOrderRecyclerViewAdapter extends FirestoreRecyclerAdapter<Order, MyOrderRecyclerViewAdapter.ViewHolder> {

    //FOR DATA
    private final RequestManager glide;
    private final OrderFragment.OnOrderFragmentListener mListener;

    public MyOrderRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Order> options,
                                      OrderFragment.OnOrderFragmentListener listener,
                                      RequestManager glide ) {
        super(options);
        mListener = listener;
        this.glide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1);
        view.startAnimation(anim);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Order model) {
        holder.product.setText(model.getProduct().getName());
        holder.date.setText(convertDateToHour(model.getDateCreated()));
        holder.qte.setText("Quantité: "+String.valueOf(model.getQuantity()));
        holder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startDetailOrderFragment(model);
            }
        });
        setFadeAnimation(holder.itemView);

    }

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dfTime.format(date);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product) TextView product;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.qte) TextView qte;
        @BindView(R.id.detailButton) Button detailButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + product.getText() + "'";
        }
    }
}
