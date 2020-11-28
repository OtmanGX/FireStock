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

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.gxma.foodoc.R;
import com.gxma.foodoc.api.CategoryHelper;
import com.gxma.foodoc.base.BaseFragment;
import com.gxma.foodoc.models.Category;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnCategoryFragmentListener mListener;
    private MyCategoryRecyclerViewAdapter adapter;
    @BindView(R.id.list) RecyclerView recyclerView;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance(int columnCount) {
        CategoryFragment fragment = new CategoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        ButterKnife.bind(this, view);
        // Set the adapter
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        configureRecyclerView();


        return view;
    }

    @OnClick(R.id.floatingAddButton)
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.startAddCategoryActivity(null);
        }
    }

    private void configureRecyclerView() {
        adapter = new MyCategoryRecyclerViewAdapter(generateOptionsForAdapter(
                CategoryHelper.getAllCategories()), mListener, Glide.with(this));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private FirestoreRecyclerOptions<Category> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryFragmentListener) {
            mListener = (OnCategoryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListUsersListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCategoryFragmentListener {
        void startAddCategoryActivity(Category category);
        void startModifyCategoryActivity(String idCategory, Category category);
        void startProductFragment();
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        private MyCategoryRecyclerViewAdapter mAdapter;
        private Drawable icon;
        private final ColorDrawable background;

        public SwipeToDeleteCallback(MyCategoryRecyclerViewAdapter adapter) {
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
