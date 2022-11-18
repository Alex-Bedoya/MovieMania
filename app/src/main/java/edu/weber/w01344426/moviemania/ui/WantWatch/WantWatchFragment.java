package edu.weber.w01344426.moviemania.ui.WantWatch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import edu.weber.w01344426.moviemania.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.weber.w01344426.moviemania.R;
import edu.weber.w01344426.moviemania.databinding.FragmentMasterBinding;
import edu.weber.w01344426.moviemania.models.Movie;
import edu.weber.w01344426.moviemania.models.MovieRecyclerAdapter;

public class WantWatchFragment extends Fragment {


    private WantWatchViewModel wantWatchViewModel;
    private FragmentMasterBinding binding;
    private MovieRecyclerAdapter movWatchAdapter;

    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return root = inflater.inflate(R.layout.fragment_want_watch, container, false);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.wantwatch, menu);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.mnuitRandomMovie:
                movWatchAdapter.showRandomMovie();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();


        RecyclerView rv = root.findViewById(R.id.rvWantWatch);

        movWatchAdapter = new MovieRecyclerAdapter(new ArrayList<Movie>());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
        {
            return;
        }

        String userID = user.getUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(userID).child("MasterList");//.child("title").orderByChild("title");


        if (rv instanceof RecyclerView)
        {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
            rv.setAdapter(movWatchAdapter);
            rv.setHasFixedSize(false);

            ArrayList<Movie> movWatchedList = new ArrayList<>();


            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    movWatchedList.clear();

                    for (DataSnapshot shot: snapshot.getChildren()) {
                        Movie mov = shot.getValue(Movie.class);
                        if (mov != null) {
                            if (mov.isbHaveWatched()) {
                                movWatchedList.add(mov);
                            }
                        }
                    }

                    Collections.sort(movWatchedList, new Comparator<Movie>() {
                        @Override
                        public int compare(Movie movie, Movie t1) {
                            if (movie.getTitle() != null && t1.getTitle() != null) {
                                return movie.getTitle().compareTo(t1.getTitle());
                            }
                            return 0;
                        }
                    });

                    movWatchAdapter.addMovie(movWatchedList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("RecView", "The read failed, Error Code: " + error.getMessage());
                }
            });
        }
    }//end of onResume
}