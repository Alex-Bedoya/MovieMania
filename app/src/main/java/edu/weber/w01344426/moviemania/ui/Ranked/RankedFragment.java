package edu.weber.w01344426.moviemania.ui.Ranked;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import edu.weber.w01344426.moviemania.AddMovieFragment;
import edu.weber.w01344426.moviemania.R;
import edu.weber.w01344426.moviemania.models.Movie;
import edu.weber.w01344426.moviemania.models.MovieRecyclerAdapter;
import edu.weber.w01344426.moviemania.models.RankedMovieRecyclerAdapter;

public class RankedFragment extends Fragment {

    private RankedViewModel mViewModel;

    private FirebaseUser user;
    private DatabaseReference dbRef;

    private View root;

    private ArrayList<Movie> movList;

    public static RankedFragment newInstance() {
        return new RankedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return root = inflater.inflate(R.layout.fragment_ranked, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ranked, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RankedViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.mnuitShare:
                shareRanked();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        RecyclerView rv = root.findViewById(R.id.rvRanked);
        movList = new ArrayList<>();
        RankedMovieRecyclerAdapter rankedAdapter = new RankedMovieRecyclerAdapter(new ArrayList<Movie>());

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
        {
            return;
        }

        String userID = user.getUid();

        dbRef = FirebaseDatabase.getInstance().getReference(userID).child("MasterList");



        if (rv instanceof RecyclerView)
        {
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(rankedAdapter);
            rv.setHasFixedSize(false);


//------------
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP, 0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    int start = viewHolder.getBindingAdapterPosition();
                    int tar = target.getBindingAdapterPosition();

                    Collections.swap(movList, start, tar);
                    rankedAdapter.notifyItemMoved(start, tar);
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                }

                @Override
                public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);

                    rankedAdapter.addMovie(movList);

                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(rv);

//-----------


            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    movList.clear();

                    for (DataSnapshot shot: snapshot.getChildren()) {
                        Movie mov = shot.getValue(Movie.class);
                        if (mov != null) {
                            if (mov.isbRanked()) {
                                movList.add(mov);
                            }
                        }
                    }

                    Collections.sort(movList, new Comparator<Movie>() {
                        @Override
                        public int compare(Movie m1, Movie m2) {
                            return m1.getUserRanking() - m2.getUserRanking();
                        }
                    });

                    rankedAdapter.addMovie(movList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("RecView", "The read failed, Error Code: " + error.getMessage());
                }
            });
        }
    }//end of onResume

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < movList.size(); i++) {
            dbRef.child(""+(movList.get(i).getId())).child("userRanking").setValue(i + 1);
        }
    }


//----------------------------

    public void shareRanked()
    {
        String shareString = "My Top 10 Movies:";

        int maxShare = 10;
        if (maxShare > movList.size()) {
            maxShare = movList.size();
        }

        for (int i = 0; i < maxShare; i++) {
            shareString += "\n" + (i+1) + ". " + movList.get(i).getTitle();
        }

        Intent shareIntent  = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        shareIntent.setType("text/plain");

        Intent shareActivity = Intent.createChooser(shareIntent, null);
        startActivity(shareActivity);
    }

}