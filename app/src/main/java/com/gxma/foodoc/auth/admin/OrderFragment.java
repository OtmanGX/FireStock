package com.gxma.foodoc.auth.admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

    private static final String ARG_COLUMN_COUNT = "column-count";
    final static String DEFAULT_CATEGORY="toutes les catégories";
    final static String DEFAULT_ORDER = "dateCreated";
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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderFragment() {
    }

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
                OrderHelper.getAllOrders().whereEqualTo("state",spinner.getSelectedItemPosition())), mListener, Glide.with(this));

        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
        void startResponseFragment(String idOrder, Order order);
        void startDetailOrderFragment(Order order);
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        private MyOrderRecyclerViewAdapter mAdapter;
        private Drawable icon;
        private final ColorDrawable background;

        public SwipeToDeleteCallback(MyOrderRecyclerViewAdapter adapter) {
            super(0, ItemTouchHelper.LEFT);
            mAdapter = adapter;
            icon = ContextCompat.getDrawable(getContext(),
                    R.drawable.ic_delete);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);

        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.removeItem(getContext(), viewHolder.getAdapterPosition());
        }

    }


}