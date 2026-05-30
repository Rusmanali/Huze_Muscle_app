package com.example.huzemuscle.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrationViewModel extends ViewModel {
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    public final MutableLiveData<String> weight = new MutableLiveData<>("");
    public final MutableLiveData<String> gender = new MutableLiveData<>("");
    public final MutableLiveData<String> goal = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");
}
