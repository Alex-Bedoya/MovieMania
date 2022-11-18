package edu.weber.w01344426.moviemania;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeleteMovieDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteMovieDialog extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private deleteMovie mCallback;

    public interface deleteMovie{
        public void deleteSelectedMovie();
    }

    public DeleteMovieDialog() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeleteMovieDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static DeleteMovieDialog newInstance(String param1, String param2) {
        DeleteMovieDialog fragment = new DeleteMovieDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (DeleteMovieDialog.deleteMovie) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException( activity.toString()
                    + " Must implement DeleteMovieDialog.deleteMovie");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder dBuilder = new AlertDialog.Builder(getActivity());
        dBuilder.setMessage("Are you sure you want to delete this movie and all its data from the Master List and all other lists?");
        dBuilder.setTitle("Delete Movie");

        dBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCallback.deleteSelectedMovie();
            }
        });

        dBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return dBuilder.create();
    }
}