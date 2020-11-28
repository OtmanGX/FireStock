package com.gxma.foodoc.auth.admin;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.CategoryHelper;
import com.gxma.foodoc.api.StorageHelper;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.base.BaseActivity;
import com.gxma.foodoc.models.Category;
import com.gxma.foodoc.models.User;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class AddCategoryActivity extends BaseActivity {

    @BindView(R.id.cat_nom) EditText cat_nom;
    @BindView(R.id.cat_desc) EditText cat_desc;
    @BindView(R.id.button_upload) Button button_upload;
    @BindView(R.id.button_submit) Button button_submit;
    @BindView(R.id.image_preview) ImageView image_preview;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    // FOR DATA
    @Nullable
    private Uri uriImageSelected;

    // STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private Category category;
    private String idCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this); //Configure Butterknifef
        configureToolbar();
        if (getIntent().hasExtra("idCategory")) {
            idCategory=getIntent().getStringExtra("idCategory");
            category = (Category) getIntent().getSerializableExtra("category");
            cat_nom.setText(category.getName());
            cat_desc.setText(category.getDescription());
            Glide.with(this) //SHOWING PREVIEW OF IMAGE
                    .load(category.getUrlImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(this.image_preview);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }



    // --------------------
    // REST REQUESTS
    // --------------------

    @OnClick(R.id.button_upload)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickUploadFile() {
        this.chooseImageFromPhone();
    }



    // --------------------
    // FILE MANAGEMENT
    // --------------------

    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.image_preview);
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validate() {
        boolean valid = true;
        String name = cat_nom.getText().toString();
        if (name.isEmpty() || name.length()<3) {
            cat_nom.setError("au moins 3 caracteres");
            valid = false;
        } else cat_nom.setError(null);

        return valid;
    }


    @OnClick(R.id.button_submit)
    public void uploadPhotoInFirebaseAndSaveCategory() {
        if (!validate())
            return;
        progressBar.setVisibility(View.VISIBLE);
        final String name = cat_nom.getText().toString();
        final String desc = cat_desc.getText().toString();
        if (idCategory==null) category = new Category();
        category.setName(name);
        category.setDescription(desc);

        if(uriImageSelected!=null) {
            String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
            // A - UPLOAD TO GCS
            final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
            mImageRef.putFile(this.uriImageSelected)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // B - SAVE CATEGORY IN FIRESTORE
                            mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (idCategory!=null) {
                                        if(category.getUrlImage()!=null) StorageHelper.deleteFile(category.getUrlImage());
                                        category.setUrlImage(uri.toString());
                                        CategoryHelper.updateCategory(idCategory, category);
                                    }
                                    else {
                                        category.setUrlImage(uri.toString());
                                        CategoryHelper.createCategory(category);
                                    }
                                }
                            });

                            progressBar.setVisibility(View.GONE);
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .addOnFailureListener(this.onFailureListener());
        } else {
            if (idCategory!=null) CategoryHelper.updateCategory(idCategory, category);
            else CategoryHelper.createCategory(category).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setResult(RESULT_OK);
                    }
            });
            progressBar.setVisibility(View.GONE);
            finish();
        }

    }

}
