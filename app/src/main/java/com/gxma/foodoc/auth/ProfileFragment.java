package com.gxma.foodoc.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.base.BaseFragment;
import com.gxma.foodoc.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //FOR DESIGN
    @BindView(R.id.profile_activity_imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username)
    TextInputEditText textInputEditTextUsername;
    @BindView(R.id.profile_activity_text_view_email)
    TextView textViewEmail;
    @BindView(R.id.profile_activity_progress_bar)
    ProgressBar progressBar;

    //FOR DATA
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;
    private User currentUser;


    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "");
        args.putString(ARG_PARAM2, "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        this.updateUIWhenCreating();
        return view;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        this.updateUIWhenCreating();
//    }


    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {
        this.updateUsernameInFirebase();
    }

    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
    }

    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();
    }




    // --------------------
    // REST REQUESTS
    // --------------------

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnSuccessListener(getActivity(), this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {

            //4 - We also delete user from firestore storage
            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());

            AuthUI.getInstance()
                    .delete(getContext())
                    .addOnSuccessListener(getActivity(), this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    // 3 - Update User Username
    private void updateUsernameInFirebase(){

        String username = this.textInputEditTextUsername.getText().toString();

        if (this.getCurrentUser() != null){
            if (!username.isEmpty() &&  !username.equals(getString(R.string.info_no_username_found))){
                UserHelper.updateUsername(username, this.getCurrentUser().getEmail()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    private void configureUser() {

        UserHelper.getUser(getCurrentUser().getEmail()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), getString(R.string.error_user_not_activated), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                if(currentUser.getUid()==null) {
                    currentUser.setUid(getCurrentUser().getUid());
                    currentUser.setUrlPicture(getCurrentUser().getPhotoUrl()!=null?getCurrentUser().getPhotoUrl().toString():null);
                    currentUser.setUsername(getCurrentUser().getDisplayName());
                    UserHelper.updateUser(currentUser);
                }
                //Get email & username from Firebase
                String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : getCurrentUser().getEmail();

                //Update views with data
                textViewEmail.setText(email);


                String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                textInputEditTextUsername.setText(username);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    // --------------------
    // UI
    // --------------------

    private void updateUIWhenCreating(){
        this.progressBar.setVisibility(View.VISIBLE);
        if (this.isCurrentUserLogged()) {
            configureUser();

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewProfile);
            }




        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case UPDATE_USERNAME:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case SIGN_OUT_TASK:
                        getActivity().finish();
                        break;
                    case DELETE_USER_TASK:
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
//        else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnCategoryFragmentListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
