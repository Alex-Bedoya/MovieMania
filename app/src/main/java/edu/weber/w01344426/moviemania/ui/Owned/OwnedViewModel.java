package edu.weber.w01344426.moviemania.ui.Owned;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OwnedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OwnedViewModel() {
        mText = new MutableLiveData<>();
       // mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}