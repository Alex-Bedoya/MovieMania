package edu.weber.w01344426.moviemania.ui.master;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class masterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public masterViewModel() {
        mText = new MutableLiveData<>();
       // mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}