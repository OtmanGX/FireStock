package com.gxma.foodoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.auth.admin.AdminProfileActivity;
import com.gxma.foodoc.auth.client.ProfileClientActivity;
import com.gxma.foodoc.base.BaseActivity;
import com.gxma.foodoc.models.User;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    //FOR DATA

    // 1 - Identifier for Sign-In Activity

    private static final int RC_SIGN_IN = 123;
    private User currentUser;
    @BindView(R.id.main_activity_button_login) Button btnLogin;
    @BindView(R.id.main_activity_logout) Button btnLogout;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    CoordinatorLayout coordinatorLayout;
    // --------------------

    // ACTIONS

    // --------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this); //Configure Butterknifef
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = findViewById(R.id.main_activity_coordinator_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateUIWhenResuming();
    }


//    @OnClick(R.id.main_activity_button_login)
    public void onClickLoginButton(View view) {

        // 3 - Launch Sign-In Activity when user clicked on Login Button
        if(this.isCurrentUserLogged()) {
            startProfileActivity();
        } else
            this.startSignInActivity();

    }

    @OnClick(R.id.main_activity_logout)
    public void onClickLogoutButton() {
        AuthUI.getInstance()
                .signOut(getApplicationContext())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUIWhenResuming();
                    }
                });

    }

    // 3 - Launching Profile Activity

    private void startProfileActivity(){
        progressBar.setVisibility(View.VISIBLE);
        UserHelper.getUser(getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    currentUser = documentSnapshot.toObject(User.class);
                    Intent intent;

                    if (currentUser.getIsAdmin()) { // Admin
                        intent = new Intent(getApplicationContext(), AdminProfileActivity.class);
                    } else
                        intent = new Intent(getApplicationContext(), ProfileClientActivity.class);
                    progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getString(R.string.error_user_not_activated), Toast.LENGTH_LONG).show();
                }
//                intent.putExtra("user", currentUser);

            }

        }).addOnFailureListener(this.onFailureListener());



    }

    // 2 - Update UI when activity is resuming

    private void updateUIWhenResuming(){

        this.btnLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
        this.btnLogout.setVisibility(this.isCurrentUserLogged() ? View.VISIBLE:View.INVISIBLE);
    }

    // --------------------

    // NAVIGATION

    // --------------------


    // 2 - Launch Sign-In Activity

    private void startSignInActivity(){

        startActivityForResult(


                AuthUI.getInstance()

                        .createSignInIntentBuilder()

                        .setTheme(R.style.LoginTheme)

                        .setAvailableProviders(

                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))

                        .setIsSmartLockEnabled(false, true)

                        .setLogo(R.drawable.common_google_signin_btn_icon_dark)

                        .build(),

                RC_SIGN_IN);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // 4 - Handle SignIn Activity response on activity result

        this.handleResponseAfterSignIn(requestCode, resultCode, data);

    }


    // 2 - Show Snack Bar with a message

    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();

    }



    // --------------------

    // UTILS

    // --------------------


    // 3 - Method that handles response after SignIn Activity close


    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){


        IdpResponse response = IdpResponse.fromResultIntent(data);


        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) { // SUCCESS

                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));

            } else { // ERRORS

                if (response == null) {

                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));

                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {

                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));

                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {

                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));

                }

            }

        }

    }

}
