package edu.weber.w01344426.moviemania;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import edu.weber.w01344426.moviemania.models.Genre;
import edu.weber.w01344426.moviemania.models.Movie;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View root;
    private TextView lblTitle, lblRating, lblScore, lblRuntime, lblOverview,
                     lblGenres, lblComposer, lblDirector, lblActorsList;
    private ImageView imgPoster;
    private CheckBox chckOwned, chckWatched, chckRanked;
    private ImageButton btnYoutube;
    private EditText txtUserComments;
    private RatingBar rbUserRating;

    private Movie selectedMovie;

    private DatabaseReference dbRef;

    private deleteCourseDialog mCallback;


    public interface deleteCourseDialog
    {
        public void deleteCourseDialog();
    }

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieDetailedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieDetailsFragment newInstance(String param1, String param2) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
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

        setHasOptionsMenu(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_MovieMania_Dialog_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_movie_details, container, false);
    }


    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (MovieDetailsFragment.deleteCourseDialog) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException( activity.toString()
                    + " Must implement MovieDetailsFragment.deleteCourseDialog");
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflater.inflate(R.menu.details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().setWindowAnimations(R.style.Theme_MovieMania_DialogAnimation);

        Toolbar tb = root.findViewById(R.id.tbMovieDetails);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tb.setTitle(R.string.movie_details);
        tb.setNavigationIcon(R.drawable.ic_x);
        tb.inflateMenu(R.menu.details);

        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.mnuitDeleteMovie:
                        mCallback.deleteCourseDialog();
                        //deleteMovie();
                        //dismiss();
                        return true;
                }
                return false;
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();


        lblTitle = root.findViewById(R.id.lblDetTitle);
        lblRating = root.findViewById(R.id.lblDetRating);
        lblRuntime = root.findViewById(R.id.lblDetRuntime);
        lblScore = root.findViewById(R.id.lblDetVoteAvg);
        lblOverview = root.findViewById(R.id.lblDetOverview);
        lblGenres = root.findViewById(R.id.lblDetGenres);
        lblDirector = root.findViewById(R.id.lblDetDirector);
        lblComposer = root.findViewById(R.id.lblDetComposer);
        lblActorsList = root.findViewById(R.id.lblDetActorList);

        txtUserComments = root.findViewById(R.id.txtDetUserComments);

        imgPoster = root.findViewById(R.id.imgDetPoster);

        btnYoutube = root.findViewById(R.id.btnTrailer);

        rbUserRating = root.findViewById(R.id.rbDetUserRating);

        chckOwned = root.findViewById(R.id.chckbxOwned);
        chckWatched = root.findViewById(R.id.chckbxHaveWatched);
        chckRanked = root.findViewById(R.id.chckbxRankedList);


        FirebaseUser dbUser = FirebaseAuth.getInstance().getCurrentUser();

        if (dbUser == null)
        {
            return;
        }

        String userID = dbUser.getUid();


        if (selectedMovie != null) {

            dbRef = FirebaseDatabase.getInstance().getReference(userID).child("MasterList").child("" + selectedMovie.getId());


            String title = selectedMovie.getTitle();
            String releaseYear = "";
            if (!selectedMovie.getRelease_date().equals("")) {
                releaseYear = selectedMovie.getRelease_date().substring(0, 4);
            }
            String rating = selectedMovie.getCertification();
            String runtime = selectedMovie.getRuntime() + " mins";
            String score = "Average Rating: " + selectedMovie.getVote_average() + "/10";
            String overview = selectedMovie.getOverview();
            String genres = "";
            String director = "Director: \n " + selectedMovie.getDirector();
            String composer = "Composer: \n " + selectedMovie.getComposer();
            String imgURL = selectedMovie.getPoster_path();
            String actors = "";
            String userComments = selectedMovie.getUserComments();
            float userRating = selectedMovie.getUserRating();


            if (selectedMovie.getGenres() != null) {
                for (Genre g : selectedMovie.getGenres()) {
                    genres += " | " + g.getName() + "";
                }
                genres += " | ";
            }

            if (title != null) {
                if (releaseYear != null) {
                    lblTitle.setText(title + " (" + releaseYear + ")");
                }
                else {
                    lblTitle.setText(title);
                }
            }

            lblRating.setText(rating);
            lblRuntime.setText(runtime);
            lblScore.setText(score);
            lblOverview.setText(overview);
            lblGenres.setText(genres);
            lblDirector.setText(director);
            lblComposer.setText(composer);
            txtUserComments.setText(userComments);
            rbUserRating.setRating(userRating);


            if (imgURL != null) {
                Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + imgURL)
                        .into(imgPoster);
            }

            chckWatched.setChecked(selectedMovie.isbHaveWatched());
            chckOwned.setChecked(selectedMovie.isbOwned());
            chckRanked.setChecked(selectedMovie.isbRanked());


            if(selectedMovie.getCast() != null) {
                for (String actor : selectedMovie.getCast()) {
                    actors += actor + "\n";
                }
            }
            lblActorsList.setText(actors);


            chckRanked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    selectedMovie.setbRanked(b);
                    selectedMovie.setUserRanking(selectedMovie.getId());
                }
            });

            btnYoutube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!selectedMovie.getTrailerURL().equals(""))
                    {
                        openTrailer(selectedMovie.getTrailerURL());
                    }
                }
            });
        }
        else {
            Toast.makeText(getContext(), "Error, There was a problem loading the movie", Toast.LENGTH_LONG).show();
            dismiss();
            //lblTitle.setText("Error, there was a problem loading the movie");
        }
    }//end of onResume


    @Override
    public void onPause() {
        super.onPause();
        if (selectedMovie != null) {
            dbRef.child("bHaveWatched").setValue(chckWatched.isChecked());
            dbRef.child("bOwned").setValue(chckOwned.isChecked());
            dbRef.child("bRanked").setValue(chckRanked.isChecked());
            dbRef.child("userRanking").setValue(selectedMovie.getUserRanking());
            dbRef.child("userComments").setValue(txtUserComments.getText().toString());
            dbRef.child("userRating").setValue(rbUserRating.getRating());
        }
    }


    public void deleteMovie()
    {
        selectedMovie = null;
        dbRef.removeValue();
        dismiss();
    }


    public void setMovie(Movie mov)
    {
        selectedMovie = mov;
    }




//---------------------


    public void openTrailer(String url) {
        Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}