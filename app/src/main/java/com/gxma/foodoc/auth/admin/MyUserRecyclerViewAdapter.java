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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.auth.DialogConfirmation;
import com.gxma.foodoc.auth.admin.UsersFragment.OnListUsersListener;
import com.gxma.foodoc.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.TAG;


public class MyUserRecyclerViewAdapter extends FirestoreRecyclerAdapter<User, MyUserRecyclerViewAdapter.ViewHolder> {

    private final RequestManager glide;
    private final OnListUsersListener mListener;

    public MyUserRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<User> options,
                                     OnListUsersListener listener,
                                     //FOR DATA
                                     RequestManager glide) {
        super(options);
        mListener = listener;
        this.glide = glide;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_users, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final User model) {
        holder.email.setText(model.getEmail());
        holder.username.setText(model.getUsername());
        if (model.getUrlPicture() != null) {
            glide.load(model.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.image);
        }

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
                                mListener.startModifyUserFragment(model.getUsername(), model.getEmail());
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
                UserHelper.deleteUser(snapshot.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted product");
//                        notifyItemRemoved(position);
                        notifyDataSetChanged();
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
        @BindView(R.id.email) TextView email;
        @BindView(R.id.username) TextView username;
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.options) TextView buttonOptions;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + email.getText() + "'";
        }
    }
}
