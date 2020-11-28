package com.gxma.foodoc.auth.client;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class CategoryFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnCategoryFragmentListener mListener;
    private MyCategoryRecyclerViewAdapter adapter;
    @BindView(R.id.list)
    RecyclerView recyclerView;

    public CategoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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
        View view = inflater.inflate(R.layout.fragment_category_list_client, container, false);
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

    private void configureRecyclerView() {
        adapter = new MyCategoryRecyclerViewAdapter(generateOptionsForAdapter(
                CategoryHelper.getAllCategories()), mListener, Glide.with(this));
        recyclerView.setAdapter(adapter);
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
        void startProductFragment(int category);
    }

}
