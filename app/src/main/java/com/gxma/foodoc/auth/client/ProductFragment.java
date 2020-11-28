package com.gxma.foodoc.auth.client;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.CategoryHelper;
import com.gxma.foodoc.api.ProductHelper;
import com.gxma.foodoc.base.BaseFragment;
import com.gxma.foodoc.models.Category;
import com.gxma.foodoc.models.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    final static String DEFAULT_CATEGORY="toutes les catégories";
    final static String DEFAULT_ORDER = "dateCreated";
    // TODO: Customize parameters
    final static ArrayList<String> spinnerArray = new ArrayList<String>();
    private int mColumnCount = 1;
    private OnProductFragmentListener mListener;
    private MyProductRecyclerViewAdapter adapter;
    public static String category = DEFAULT_CATEGORY;
    public static String order = DEFAULT_ORDER;
    int first=0;
    @BindView(R.id.list) RecyclerView recyclerView;
    @BindView(R.id.spinner1) Spinner spinner1;
    @BindView(R.id.spinner2) Spinner spinner2;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public ProductFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ProductFragment newInstance(int columnCount) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list_client, container, false);
        ButterKnife.bind(this, view);
        progressBar.setVisibility(View.VISIBLE);
        // Set the adapter

            Context context = view.getContext();
             if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            getCategoriesfromFirestore();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProductFragmentListener) {
            mListener = (OnProductFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListUsersListener");
        }
    }

    public void configureSpinner() {
        spinnerArray.clear();
        spinnerArray.add(DEFAULT_CATEGORY);
        for(DocumentSnapshot document : CategoryHelper.categories) {
            Category category = document.toObject(Category.class);
            spinnerArray.add(category.getName());
            Log.i("category",category.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0) category = CategoryHelper.categories.get(i-1).getId();
                configureRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner1.setSelection(0);
            }

        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) order=DEFAULT_ORDER;
                else if(i==1) order = "name";
                else order = "price";
                configureRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void configureRecyclerView() {
        if(spinner1.getSelectedItemPosition()==0)
        adapter = new MyProductRecyclerViewAdapter(generateOptionsForAdapter(
                ProductHelper.getAllProducts().orderBy(order)), mListener, Glide.with(this));
        else {
            adapter= new MyProductRecyclerViewAdapter(generateOptionsForAdapter
                    (ProductHelper.getAllProductsOfCategory(category).orderBy(order))
                    ,mListener,Glide.with(this));
        }
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    private FirestoreRecyclerOptions<Product> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void getCategoriesfromFirestore() {
        CategoryHelper.categories = new ArrayList<DocumentSnapshot>();
        CollectionReference collection = CategoryHelper.getAllCategories();

        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("getcategories","success");
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        documentSnapshot.getReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                CategoryHelper.categories.add(documentSnapshot);
                                if (CategoryHelper.categories.size()==task.getResult().size()) {
//                                    category = CategoryHelper.categories.get(1).getString("name");
                                    configureSpinner();
//                                    configureRecyclerView();
                                }

                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnProductFragmentListener {
        // TODO: Update argument type and name
        void addOrderFragment(Product product);
    }

}