package com.gxma.foodoc.auth.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.CategoryHelper;
import com.gxma.foodoc.api.ProductHelper;
import com.gxma.foodoc.api.StorageHelper;
import com.gxma.foodoc.auth.DialogConfirmation;
import com.gxma.foodoc.auth.admin.ProductFragment.OnProductFragmentListener;
import com.gxma.foodoc.models.Product;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.TAG;

/**
 * specified {@link OnProductFragmentListener}.
 * TODO: Replace the implementation with code for your data type.
 */
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
                .inflate(R.layout.fragment_product, parent, false);
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
        holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                MenuBuilder menuBuilder =new MenuBuilder(view.getContext());
                MenuInflater inflater = new MenuInflater(view.getContext());
                inflater.inflate(R.menu.category_options_menu, menuBuilder);
                MenuPopupHelper optionsMenu = new MenuPopupHelper(view.getContext(),
                        menuBuilder, view);
                optionsMenu.setForceShowIcon(true);

                // Set Item Click Listener
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit: // Handle option1 Click
//                                mListener.startAddProductActivity();
                                mListener.startModifyProductActivity(
                                        getSnapshots().getSnapshot(position).getId(),
                                        model);
                                return true;


                            case R.id.del: // Handle option2 Click
                                removeItem(holder.itemView.getContext(),position);
                                return true;

                            default:
                                return false;
                        }

                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {}
                });

                //displaying the popup
                optionsMenu.show();
            }
        });
    }

    public void removeItem(Context context, final int position) {
        DialogConfirmation dialog = new DialogConfirmation(context) {
            @Override
            public void ok(){
        final DocumentSnapshot snapshot = (DocumentSnapshot)
                getSnapshots().getSnapshot(position);
        ProductHelper.deleteProduct(snapshot.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted product");
                String urlImage = snapshot.getString("urlImage") ;
                if (urlImage!=null)
                    StorageHelper.deleteFile(snapshot.getString("urlImage"));
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.category) TextView category;
        @BindView(R.id.options) TextView buttonOptions;
        @BindView(R.id.price) TextView price;

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
