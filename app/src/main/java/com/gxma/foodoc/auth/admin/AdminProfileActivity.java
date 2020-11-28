package com.gxma.foodoc.auth.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.gxma.foodoc.R;
import com.gxma.foodoc.auth.ProfileFragment;
import com.gxma.foodoc.base.BaseActivity;
import com.gxma.foodoc.models.Category;
import com.gxma.foodoc.models.Order;
import com.gxma.foodoc.models.Product;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AdminProfileActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , CategoryFragment.OnCategoryFragmentListener, ProductFragment.OnProductFragmentListener,
        UsersFragment.OnListUsersListener, OrderFragment.OnOrderFragmentListener {
    // Request Codes
    private final int ADDPRODUCT_CODE = 10;
    private final int ADDCATEGORY_CODE = 11 ;

    Fragment categoryFragment;
    Fragment profileFragment;
    Fragment productFragment;
    Fragment usersFragment;
    Fragment ordersFragment;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        ButterKnife.bind(this); //Configure ButterKnife
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        showFirstFragment();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_about) {
            new AlertDialog.Builder(this).
                    setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setMessage(getString(R.string.app_name)).create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.idcategories) {
            if(categoryFragment==null) categoryFragment=CategoryFragment.newInstance(1);
            startFragment(categoryFragment);

        } else if (id == R.id.idprofile) {
            if(profileFragment==null) profileFragment=ProfileFragment.newInstance();
            startFragment(profileFragment);
        } else if (id == R.id.idproducts) {
            if(productFragment==null) productFragment=ProductFragment.newInstance(1);
            startFragment(productFragment);
        } else if (id == R.id.idusers) {
            if(usersFragment==null) usersFragment = UsersFragment.newInstance(1);
            startFragment(usersFragment);
        } else if (id == R.id.idorder){
            if(ordersFragment==null) ordersFragment = OrderFragment.newInstance(1);
            startFragment(ordersFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFragment(Fragment fragment) {
        if(!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout,fragment).commit();
        }
    }


    // Show first fragment when activity is created

    private void showFirstFragment(){

        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.framelayout);

        if (visibleFragment == null){

            // 1.1 - Show News Fragment

            if(profileFragment==null) profileFragment=ProfileFragment.newInstance();
            startFragment(profileFragment);

            // 1.2 - Mark as selected the menu item corresponding to NewsFragment

            navigationView.getMenu().getItem(0).setChecked(true);

        }

    }


    @Override
    public void startProductFragment() {
        productFragment = ProductFragment.newInstance(1);
        startFragment(productFragment);
    }

    @Override
    public void startAddProductActivity() {
        Intent intent = new Intent(this, AddProductActivity.class);

        startActivityForResult(intent, ADDPRODUCT_CODE);
    }


    @Override
    public void startModifyProductActivity(String idProduct, Product product) {
        Intent intent = new Intent(this, AddProductActivity.class);
        intent.putExtra("product",product);
        intent.putExtra("idproduct",idProduct);
        startActivityForResult(intent, ADDPRODUCT_CODE);
    }

    @Override
    public void startAddCategoryActivity(Category category) {
        Intent intent = new Intent(this,AddCategoryActivity.class);
        if(category!=null) intent.putExtra("category",category);
        startActivityForResult(intent, ADDCATEGORY_CODE);
    }


    @Override
    public void startModifyCategoryActivity(String idCategory, Category category) {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        intent.putExtra("category",category);
        intent.putExtra("idCategory",idCategory);
        startActivityForResult(intent, ADDCATEGORY_CODE);
    }

    @Override
    public void startAddUserFragment() {
        AddUserFragment fragment = new AddUserFragment();
        fragment.show(getSupportFragmentManager(),"AddUserFragment");
    }

    @Override
    public void startModifyUserFragment(String username, String email) {
        ModifyUserFragment fragment = new ModifyUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("email",email);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ModifyUserFragment");
    }

    @Override
    public void startResponseFragment(String idOrder, Order order) {
        OrderResponseFragment fragment = new OrderResponseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("idOrder", idOrder);
        bundle.putSerializable("order", order);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "OrderResponseFragment");
    }

    @Override
    public void startDetailOrderFragment(Order order) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "OrderDetailFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDPRODUCT_CODE || requestCode == ADDCATEGORY_CODE) {
            if (resultCode == RESULT_OK) {
                successNotification();
            } else {

            }
        }
    }
}
