package com.example.android_final;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android_final.model.Model;
import com.example.android_final.model.User;

public class UserViewModel extends ViewModel {
    private LiveData<User> user = Model.instance().getUser();

    public LiveData<User> getCurrentUser(){
        return user;
    }
}
