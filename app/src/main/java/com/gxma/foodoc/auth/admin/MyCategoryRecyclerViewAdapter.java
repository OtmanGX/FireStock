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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.CategoryHelper;
import com.gxma.foodoc.api.ProductHelper;
import com.gxma.foodoc.api.StorageHelper;
import com.gxma.foodoc.auth.DialogConfirmation;
import com.gxma.foodoc.models.Category;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.TAG;


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
                .inflate(R.layout.fragment_category, parent, false);
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
                mListener.startProductFragment();
            }
        });

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
                                mListener.startModifyCategoryActivity(
                                        getSnapshots().getSnapshot(position).getId(),
                                        model);
                                return true;
                            case R.id.del: // Handle option2 Click
                                removeItem(holder.itemView.getContext(), position);
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


    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1);
        view.startAnimation(anim);
    }

    public void removeItem(Context context, final int position) {
        DialogConfirmation dialog = new DialogConfirmation(context) {
            @Override
            public void ok() {
                final DocumentSnapshot snapshot = (DocumentSnapshot)
                        getSnapshots().getSnapshot(position);
                CategoryHelper.deleteCategory(snapshot.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String urlImage = snapshot.getString("urlImage") ;
                        if (urlImage!=null)
                            StorageHelper.deleteFile(snapshot.getString("urlImage"));
                        Query query = ProductHelper.getAllProducts()
                                .whereEqualTo("category", snapshot.getId());
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        final String urlImage = document.getString("urlImage");
                                        ProductHelper.deleteProduct(document.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (urlImage != null)
                                                        StorageHelper.deleteFile(urlImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                    Log.d(TAG, "onSuccess: deleted file");
                                                                else
                                                                    Log.d(TAG, "OnFailed: deleted file");
                                                            }
                                                        });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                        Log.d(TAG, "onSuccess: Removed list item");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
                notifyItemRemoved(position);
            }
            @Override public void cancel() {
                notifyDataSetChanged();
            }
        };
        dialog.show();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView description;
        @BindView(R.id.options) TextView buttonOptions;

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
