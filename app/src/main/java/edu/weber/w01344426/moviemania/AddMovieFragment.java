package edu.weber.w01344426.moviemania;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.weber.w01344426.moviemania.models.Movie;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddMovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMovieFragment extends DialogFragment implements SearchMoviesTask.SearchMoviesCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View root;
    private ListView lv;
    private ArrayList<String> showMovies;
    private TextInputEditText searchBox;

    public AddMovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddMovieFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMovieFragment newInstance(String param1, String param2) {
        AddMovieFragment fragment = new AddMovieFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_MovieMania_Dialog_Fullscreen);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_add_movie, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().setWindowAnimations(R.style.Theme_MovieMania_DialogAnimation);

        Toolbar tb = root.findViewById(R.id.tbAddMovie);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tb.setTitle(R.string.addMovie);
        tb.setNavigationIcon(R.drawable.ic_x);
    }//end of onViewCreated


    @Override
    public void onResume() {
        super.onResume();

        lv = root.findViewById(R.id.listSearched);
        showMovies = new ArrayList<String>();



        searchBox = root.findViewById(R.id.txtSearch);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                input = input.trim();

                if (!input.equals("")) {
                    SearchMoviesTask searchTask = new SearchMoviesTask();
                    String search = editable.toString().replace(' ', '+');

                    searchTask.setSearch(search);

                    searchTask.execute(AddMovieFragment.this);
                }
                else
                {

                    updateSearchList(new ArrayList<Movie>());
                }
            }
        });
    }//end of onResume


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.Theme_MovieMania_DialogAnimation);

        return dialog;
    }


    @Override
    public void updateSearchList(ArrayList<Movie> movList)
    {
        showMovies.clear();

        if (searchBox.getText().equals("")){
            return;
        }

        for (Movie mov: movList) {
            if (mov.getRelease_date() != null && !mov.getRelease_date().equals("") ) {
                showMovies.add(mov.getTitle() + " (" + mov.getRelease_date().substring(0,4) + ")");
            }
            else {
                showMovies.add(mov.getTitle());
            }
        }

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.search_movie_view, R.id.lblMovieSearchedTitleYear, showMovies);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    int selectedMovieID = movList.get(i).getId();

                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference mDatabase;
                    mDatabase = FirebaseDatabase.getInstance().getReference(userID).child("MasterList").child("" + selectedMovieID);

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Toast.makeText(getContext(), movList.get(i).getTitle() + " already exists in the Master List", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                GetMovieTask getMovieTask = new GetMovieTask();
                                getMovieTask.setMovieID(selectedMovieID);
                                getMovieTask.execute(getContext());
                                dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
        }
        catch (Exception e)
        {
            //Log.d("Add", "updateSearchList: " + e.getMessage());
        }
    }//end of updateSearchList
}