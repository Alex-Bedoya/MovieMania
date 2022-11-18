package edu.weber.w01344426.moviemania.ui.master;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import edu.weber.w01344426.moviemania.databinding.FragmentHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import edu.weber.w01344426.moviemania.AddMovieFragment;
import edu.weber.w01344426.moviemania.R;
import edu.weber.w01344426.moviemania.databinding.FragmentMasterBinding;
import edu.weber.w01344426.moviemania.models.Movie;
import edu.weber.w01344426.moviemania.models.MovieRecyclerAdapter;

public class masterFragment extends Fragment {

    private masterViewModel masterViewModel;
    private FragmentMasterBinding binding;

    private View root;

    private MovieRecyclerAdapter movAdapter;

    private FloatingActionButton fabAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //android.view.Display dis = ((android.view.WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay());

        return root = inflater.inflate(R.layout.fragment_master, container, false);


        //masterViewModel =
        //        new ViewModelProvider(this).get(masterViewModel.class);
//
        //binding = FragmentMasterBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();
//
        ////final TextView textView = binding.textHome;
        ////masterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        ////    @Override
        ////    public void onChanged(@Nullable String s) {
        ////        textView.setText(s);
        ////    }
        ////});
        //return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.mnuitSearchMovie:
                AddMovieFragment addMovieFrag = new AddMovieFragment();
                addMovieFrag.show(getActivity().getSupportFragmentManager(), "AddMovieDialog");
                return true;
        }
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView rv = root.findViewById(R.id.rvMasterList);
        fabAdd = root.findViewById(R.id.fabAddMovie);

        MovieRecyclerAdapter movAdapter = new MovieRecyclerAdapter(new ArrayList<Movie>());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
        {
            return;
        }

        String userID = user.getUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(userID).child("MasterList");//.orderByValue("title");

//--------------

        if (rv instanceof RecyclerView)
        {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 3));

            rv.setAdapter(movAdapter);
            rv.setHasFixedSize(false);

            ArrayList<Movie> movList = new ArrayList<>();


            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    movList.clear();

                    for (DataSnapshot shot: snapshot.getChildren()) {

                        Movie mov = shot.getValue(Movie.class);
                        if (mov != null) {
                            movList.add(mov);
                        }
                    }


                    Collections.sort(movList, new Comparator<Movie>() {
                        @Override
                        public int compare(Movie movie, Movie t1) {
                            if (movie.getTitle() != null && t1.getTitle() != null) {
                                return movie.getTitle().compareTo(t1.getTitle());
                            }
                            return 0;
                        }
                    });

                    movAdapter.addMovie(movList);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("RecView", "The read failed, Error Code: " + error.getMessage());

                }
            });
        }


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMovieFragment addMovieFrag = new AddMovieFragment();
                addMovieFrag.show(getActivity().getSupportFragmentManager(), "AddMovieDialog");
            }
        });



    }//end of onResume


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}