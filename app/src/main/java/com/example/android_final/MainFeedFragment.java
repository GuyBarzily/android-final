package com.example.android_final;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.android_final.databinding.FragmentMainFeedBinding;
import com.example.android_final.databinding.FragmentSignInBinding;
import com.example.android_final.model.Model;
import com.example.android_final.model.Post;
import com.example.android_final.model.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainFeedFragment extends Fragment {

    FragmentMainFeedBinding binding;
    PostRecyclerAdapter adapter;
    PostsListFragmentViewModel viewModel;
    UserViewModel userViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentMainFeedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        binding.postsRecyclerList.setHasFixedSize(true);
        binding.postsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostRecyclerAdapter(getLayoutInflater(), viewModel.getData().getValue(), "MainFeedFragment");
        binding.postsRecyclerList.setAdapter(adapter);

        adapter.setOnItemClickListener(new PostRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Log.d("TAG", "Row was clicked: " + pos);
                Post post = viewModel.getData().getValue().get(pos);
                Log.d("TAG", "onItemClick: " + post.getPostTextContent());
            }
        });
        View addButton = view.findViewById(R.id.main_feedFrag_add_btn);
        addButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainFeedFragment_to_addPostFragment));
        binding.progressBar.setVisibility(View.GONE);
        viewModel.getData().observe(getViewLifecycleOwner(), list -> {
            adapter.setData(list);

        });

        Model.instance().EventPostListLoadingState.observe(getViewLifecycleOwner(), status -> {
            binding.swipeRefresh.setRefreshing(status == Model.LoadingState.LOADING);
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            reloadData();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(PostsListFragmentViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable the back arrow in the action bar

    }

    void reloadData() {
        Model.instance().refreshAllPosts();

    }
}