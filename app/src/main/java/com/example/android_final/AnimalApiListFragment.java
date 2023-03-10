package com.example.android_final;


import android.content.Context;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_final.databinding.FragmentAnimalApiListBinding;
import com.example.android_final.model.Animal;
import com.example.android_final.model.AnimalModel;


import java.util.Objects;


public class AnimalApiListFragment extends Fragment {
    FragmentAnimalApiListBinding binding;
    AnimalRecyclerAdapter adapter;
    AnimalApiFragmentViewModel viewModel;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(AnimalApiFragmentViewModel.class);
        FragmentActivity parentActivity = getActivity();
        assert parentActivity != null;
        parentActivity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.findItem(R.id.animalApiListFragment).setVisible(false);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        },this, Lifecycle.State.RESUMED);
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimalApiListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.animalApiRecyclerView.setHasFixedSize(true);
        binding.animalApiRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnimalRecyclerAdapter(getLayoutInflater(),viewModel.getData().getValue());
        binding.animalApiRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(pos -> {
            Log.d("TAG", "Row was clicked " + pos);
            Animal animal = Objects.requireNonNull(viewModel.getData().getValue()).get(pos);
        });

        viewModel.getData().observe(getViewLifecycleOwner(), list->{
                adapter.setData(list);
        });

        AnimalModel.instance().EventAnimalListLoadingState.observe(getViewLifecycleOwner(),status->{
            binding.swipeRefresh.setRefreshing(status == AnimalModel.LoadingState.LOADING);
        });

        binding.swipeRefresh.setOnRefreshListener(()-> {
         binding.swipeRefresh.setRefreshing(false);
        });


    return view;
    }


}