package com.gxma.foodoc.auth.client;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gxma.foodoc.R;
import com.gxma.foodoc.models.Category;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyCategoryRecyclerViewAdapter extends FirestoreRecyclerAdapter<Category, MyCategoryRecyclerViewAdapter.ViewHolder> {
    //FOR DATA
    private final RequestManager glide;
    private final CategoryFragment.OnCategoryFragmentListener mListener;

    public MyCategoryRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Category> options,
                                         CategoryFragment.OnCategoryFragmentListener listener,
                                         //FOR DATA
                                         RequestManager glide) {
        super(options);
        mListener = listener;
        this.glide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category_client, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Category model) {
        holder.title.setText(model.getName());
        holder.description.setText(model.getDescription());
        if (model.getUrlImage()!=null) {
            Log.i("image is downloaded", "downloaded");
            glide.load(model.getUrlImage()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        } else holder.image.setVisibility(View.GONE);
        setFadeAnimation(holder.itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startProductFragment(position);
            }
        });

    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1);
        view.startAnimation(anim);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final View mView;
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView description;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
