package com.example.android_final;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_final.databinding.FragmentUserAllPostsBinding;
import com.example.android_final.model.Model;
import com.example.android_final.model.Post;

import java.util.List;


public class user_all_posts extends Fragment {

    FragmentUserAllPostsBinding binding;
    PostRecyclerAdapter adapter;
    PostsListFragmentViewModel viewModel;
    UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserAllPostsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.userAllPostsPostsRecyclerList.setHasFixedSize(true);
        binding.userAllPostsPostsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostRecyclerAdapter(getLayoutInflater(), viewModel.getData().getValue());
        binding.userAllPostsPostsRecyclerList.setAdapter(adapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(),user -> {
            if (user != null) {
                viewModel.getData().observe(getViewLifecycleOwner(), list -> {
                    Model.instance().getAllUserPosts(userViewModel.getCurrentUser().getValue().getUserFirebaseID(), posts -> {
                        adapter.setData(posts);
                    });
                });
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(PostsListFragmentViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}


