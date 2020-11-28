package com.gxma.foodoc.auth.admin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.OrderHelper;
import com.gxma.foodoc.auth.DialogConfirmation;
import com.gxma.foodoc.auth.client.ProductFragment.OnProductFragmentListener;
import com.gxma.foodoc.models.Order;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.TAG;


public class MyOrderRecyclerViewAdapter extends FirestoreRecyclerAdapter<Order, MyOrderRecyclerViewAdapter.ViewHolder> {

    //FOR DATA
    private final RequestManager glide;
    private final OrderFragment.OnOrderFragmentListener mListener;

    public MyOrderRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Order> options,
                                      OrderFragment.OnOrderFragmentListener listener,
                                      RequestManager glide) {
        super(options);
        mListener = listener;
        this.glide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_admin, parent, false);
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
        holder.qte.setText(holder.qte.getText()+String.valueOf(model.getQuantity()));
        holder.user.setText(holder.user.getText()+model.getUser().getUsername());
        holder.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startResponseFragment
                        (getSnapshots().getSnapshot(position).getId(), model);
            }
        });
        holder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startDetailOrderFragment(model);
            }
        });
        setFadeAnimation(holder.itemView);

    }

    public void removeItem(Context context, final int position) {
        DialogConfirmation dialog = new DialogConfirmation(context) {
            @Override
            public void ok(){
                final DocumentSnapshot snapshot = (DocumentSnapshot)
                        getSnapshots().getSnapshot(position);
                OrderHelper.deleteOrder(snapshot.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted order");
                        notifyItemRemoved(position);
                    }}
                );
            }

            @Override public void cancel() {
                notifyDataSetChanged();
            }
        };
        dialog.show();

    }

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dfTime.format(date);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product) TextView product;
        @BindView(R.id.date) TextView date;
        @BindView(R.id.qte) TextView qte;
        @BindView(R.id.user) TextView user;
        @BindView(R.id.answerButton) Button answerButton;
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
