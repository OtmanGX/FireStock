package com.gxma.foodoc.auth.client;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.CategoryHelper;
import com.gxma.foodoc.auth.client.ProductFragment.OnProductFragmentListener;
import com.gxma.foodoc.models.Product;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyProductRecyclerViewAdapter extends FirestoreRecyclerAdapter<Product, MyProductRecyclerViewAdapter.ViewHolder> {

    //FOR DATA
    private final RequestManager glide;
    private final OnProductFragmentListener mListener;

    public MyProductRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Product> options,
                                        OnProductFragmentListener listener,
                                        RequestManager glide) {
        super(options);
        mListener = listener;
        this.glide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_product_client, parent, false);
        return new ViewHolder(view);
    }


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1);
        view.startAnimation(anim);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Product model) {
        holder.title.setText(model.getName());
        CategoryHelper.getCategory(model.getCategory()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.category.setText(documentSnapshot.getString("name"));
            }
        });
        holder.price.setText(String.valueOf(model.getPrice())+" DH");
        if (model.getUrlImage()!=null) {
            glide.load(model.getUrlImage()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        } else holder.image.setVisibility(View.GONE);

        setFadeAnimation(holder.itemView);
        holder.buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addOrderFragment(model);
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.category) TextView category;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.buttonOrder)
        Button buttonOrder;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }

        @OnClick(R.id.buttonOrder)
        public void onButtonPressed() {
            Log.d("buttonOrder","clicked");
        }
    }
}
