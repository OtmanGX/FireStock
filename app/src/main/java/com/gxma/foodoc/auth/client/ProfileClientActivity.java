package com.gxma.foodoc.auth.client;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.UserHelper;
import com.gxma.foodoc.auth.ProfileFragment;
import com.gxma.foodoc.auth.admin.OrderDetailFragment;
import com.gxma.foodoc.base.BaseActivity;
import com.gxma.foodoc.models.Order;
import com.gxma.foodoc.models.Product;
import com.gxma.foodoc.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileClientActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnCategoryFragmentListener, ProductFragment.OnProductFragmentListener,
        OrderFragment.OnOrderFragmentListener
{

    Fragment categoryFragment;
    Fragment productFragment;
    Fragment profileFragment;
    Fragment orderFragment;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_client);
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
        getMenuInflater().inflate(R.menu.profile_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        } else if (id == R.id.idorder) {
            if(orderFragment==null) orderFragment = OrderFragment.newInstance(1);
            startFragment(orderFragment);
        } else if (id == R.id.nav_contact) {
            ContactDialogFragment fragment = new ContactDialogFragment();
            startDialogFragment(fragment);
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

    private  void startDialogFragment(DialogFragment fragment) {
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
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
    public void startProductFragment(int category) {
        productFragment = ProductFragment.newInstance(1);
        Bundle bundle = new Bundle();
        bundle.putInt("category",category+1);
        productFragment.setArguments(bundle);
        startFragment(productFragment);
    }

    @Override
    public void addOrderFragment(final Product product) {
        UserHelper.getUser(getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                AddOrderFragment fragment = new AddOrderFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                bundle.putSerializable("user", user);
                fragment.setArguments(bundle);
                startDialogFragment(fragment);
            }
        });

    }

    @Override
    public void startDetailOrderFragment(Order order) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        fragment.setArguments(bundle);
        startDialogFragment(fragment);
    }
}
