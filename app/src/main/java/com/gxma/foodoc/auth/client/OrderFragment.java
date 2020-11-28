package com.gxma.foodoc.auth.client;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.OrderHelper;
import com.gxma.foodoc.base.BaseFragment;
import com.gxma.foodoc.models.Order;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OrderFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    final static String DEFAULT_CATEGORY="toutes les catégories";
    final static String DEFAULT_ORDER = "dateCreated";
    // TODO: Customize parameters
    final static ArrayList<String> spinnerArray = new ArrayList<String>();
    private int mColumnCount = 1;
    private OnOrderFragmentListener mListener;
    private MyOrderRecyclerViewAdapter adapter;
    public static String category = DEFAULT_CATEGORY;
    public static String order = DEFAULT_ORDER;
    int first=0;
    @BindView(R.id.list) RecyclerView recyclerView;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    public OrderFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OrderFragment newInstance(int columnCount) {
        OrderFragment fragment = new OrderFragment();
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
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.bind(this, view);
        progressBar.setVisibility(View.VISIBLE);
        // Set the adapter

            Context context = view.getContext();
             if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            configureSpinner();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrderFragmentListener) {
            mListener = (OnOrderFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListUsersListener");
        }
    }

    public void configureSpinner() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                configureRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner.setSelection(0);
            }
        });

    }


    private void configureRecyclerView() {
        adapter = new MyOrderRecyclerViewAdapter(generateOptionsForAdapter(
                OrderHelper.getOrdersbyState(spinner.getSelectedItemPosition())
                .whereEqualTo("user.uid",getCurrentUser().getUid()))
                , mListener, Glide.with(this));

        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    private FirestoreRecyclerOptions<Order> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnOrderFragmentListener {
        void startDetailOrderFragment(Order order);
    }

}