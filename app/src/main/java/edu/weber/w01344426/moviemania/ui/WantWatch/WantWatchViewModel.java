package edu.weber.w01344426.moviemania.ui.WantWatch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WantWatchViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WantWatchViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}