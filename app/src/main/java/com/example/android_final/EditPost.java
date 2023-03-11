package com.example.android_final;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_final.databinding.FragmentAddPostBinding;
import com.example.android_final.databinding.FragmentEditPostBinding;
import com.example.android_final.model.Model;
import com.example.android_final.model.Post;


public class EditPost extends Fragment {

    FragmentEditPostBinding binding;
    EditPostArgs args;
    UserViewModel userViewModel;
    ActivityResultLauncher<Void> cameraLauncher;
    ActivityResultLauncher<String> galleryLauncher;

    Boolean isAvatarSelected = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                if (result != null) {
                    binding.editPostAvatarImg.setImageBitmap(result);
                    isAvatarSelected = true;
                }
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    binding.editPostAvatarImg.setImageURI(result);
                    isAvatarSelected = true;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditPostBinding.inflate(inflater, container, false);
        args = EditPostArgs.fromBundle(getArguments());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.editPostSave.setOnClickListener(v -> {
                    String postText = binding.editPostPostContext.getEditText().getText().toString();
                    Post post = new Post(user.getUserName(), postText);
                    post.setAvatarUrl(user.getUserPet().getPetImageUrl());
                    post.setPostId(args.getPostId());
                    if (isAvatarSelected){
                        binding.editPostAvatarImg.setDrawingCacheEnabled(true);
                        binding.editPostAvatarImg.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) binding.editPostAvatarImg.getDrawable()).getBitmap();
                        Model.instance().uploadImage(user.getUserFirebaseID(), bitmap, url->{
                            post.setImageUrl(url.toString());
                            Model.instance().addPost(post, (Post) -> {
                                Log.d("TAG", "onCreateView: " + post.getPostId());
                            });
                            Model.instance().addPostToUser(user.getUserFirebaseID(), post, (Post) -> {
                                Navigation.findNavController(v).popBackStack();
                            });
                        });
                    }
                    else {
                        Model.instance().addPost(post, (Post) -> {
                            Log.d("TAG", "onCreateView: " + post.getPostId());
                        });
                        Model.instance().addPostToUser(user.getUserFirebaseID(), post, (Post) -> {
                            Navigation.findNavController(v).popBackStack();
                        });
                    }
                });

            }
        });

        binding.editPostCancel.setOnClickListener(view1-> Navigation.findNavController(view1).popBackStack());

        binding.cameraButton.setOnClickListener(view1->{
            cameraLauncher.launch(null);
        });

        binding.galleryButton.setOnClickListener(view1->{
            galleryLauncher.launch("image/*");
        });
        return binding.getRoot();

    }
}