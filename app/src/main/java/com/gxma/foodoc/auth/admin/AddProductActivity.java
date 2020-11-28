package com.gxma.foodoc.auth.admin;

import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.gxma.foodoc.api.ProductHelper;
import com.gxma.foodoc.api.StorageHelper;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.base.BaseActivity;
import com.gxma.foodoc.models.Product;
import com.gxma.foodoc.models.User;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.gxma.foodoc.auth.admin.ProductFragment.spinnerArray;

public class AddProductActivity extends BaseActivity {

    @BindView(R.id.productName) EditText productName;
    @BindView(R.id.productPrice) EditText productPrice;
    @BindView(R.id.productStock) EditText productStock;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.image_preview) ImageView image_preview;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    // FOR DATA
    @Nullable
    private Uri uriImageSelected;

    // STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private Product product;
    private String idProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this); //Configure Butterknifef
        configureToolbar();
        if(getIntent().hasExtra("idproduct")) {
            idProduct = getIntent().getStringExtra("idproduct");
            product = (Product) getIntent().getSerializableExtra("product");
            spinner.setVisibility(View.INVISIBLE);
            productName.setText(product.getName());
            productPrice.setText(String.valueOf(product.getPrice()));
            productStock.setText(String.valueOf(product.getStock()));
            Glide.with(this) //SHOWING PREVIEW OF IMAGE
                    .load(product.getUrlImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(this.image_preview);
        }
        configureSpinner();
    }

    private void configureSpinner() {
        ArrayList<String> spinnerArr = (ArrayList<String>) spinnerArray.clone();
        spinnerArr.remove(0);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), spinnerArray.get(i),Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.setSelection(0);
            }

        });
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

    @OnClick(R.id.uploadButton)
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


    @OnClick(R.id.button_submit)
    public void uploadPhotoinFirebaseandsaveProduct() {
        if (!validate())
            return;
        progressBar.setVisibility(View.VISIBLE);
        final String name = productName.getText().toString();
        final float price = Float.parseFloat(productPrice.getText().toString());
        final int stock = Integer.parseInt(productStock.getText().toString());

        if (idProduct==null) {
            DocumentSnapshot document = CategoryHelper.categories.get(spinner.getSelectedItemPosition());
            String category=
                    document.getId();
            product = new Product(name, price, stock, category);
        } else {
            product.setName(name);
            product.setPrice(price);
            product.setStock(stock);
        }

        if(uriImageSelected!=null) {
            String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
            // A - UPLOAD TO GCS
            final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
            mImageRef.putFile(this.uriImageSelected)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // B - SAVE Product IN FIRESTORE
                            mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                    if(idProduct!=null) {
                                        if (product.getUrlImage()!=null) StorageHelper.deleteFile(product.getUrlImage());
                                        product.setUrlImage(uri.toString());
                                        ProductHelper.modifyProduct(idProduct,product);
                                    }
                                    else {
                                        product.setUrlImage(uri.toString());
                                        ProductHelper.createProduct(product);

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
            if(idProduct!=null) ProductHelper.modifyProduct(idProduct, product).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            setResult(RESULT_OK);
                        }
                    });
            else ProductHelper.createProduct(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setResult(RESULT_OK);
                }
            });
            progressBar.setVisibility(View.GONE);
            finish();
        }
    }


    private boolean validate() {
        boolean valid = true;
        String name = productName.getText().toString();
        String price = productPrice.getText().toString();
        if (name.isEmpty() || name.length()<3) {
            productName.setError("au moins 3 caracteres");
            valid = false;
        } else productName.setError(null);

        if (price.isEmpty() || Float.parseFloat(price)<1)
        {
            productPrice.setError("Le nombre est invalide, il doit être supérieur à 0");
            valid = false;
        } else productPrice.setError(null);

        return valid;
    }

}
