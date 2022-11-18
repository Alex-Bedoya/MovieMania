package edu.weber.w01344426.moviemania.models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.weber.w01344426.moviemania.MovieDetailsFragment;
import edu.weber.w01344426.moviemania.R;

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.ViewHolder> {//FirebaseRecyclerAdapter<Movie, MovieRecyclerAdapter.ViewHolder>{//

    private ArrayList<Movie> masterList;
    private onMovieClick mCallback;
    private Context context;

    public MovieRecyclerAdapter(ArrayList<Movie> masterList) {
        this.masterList = masterList;
    }

    public interface onMovieClick {
        public void openMovieDetails(Movie selectedMovie);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_view, parent, false);

        try {
            mCallback = (MovieRecyclerAdapter.onMovieClick) view.getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(view.toString()
                    + " Must implement MovieRecyclerAdapter.onMovieClick");
        }

        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();//this is just for the error message if the user doesn't have any movies inside the want to watch list
    }


//---------------

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie selected = masterList.get(position);

        if (selected != null && selected.getPoster_path() != null) {

            ImageView img = holder.movPoster;

            int width = (holder.view.getResources().getDisplayMetrics().widthPixels / 3);
            int height = ((width * 3) / 2);

            img.setMaxHeight(height);
            img.setMaxWidth(width);

            img.setLayoutParams(new RelativeLayout.LayoutParams(width, height));



            if (selected.getPoster_path().equals("")) {
                Picasso.get()
                        .load("https://www.prokerala.com/movies/assets/img/no-poster-available.jpg")
                        .into(holder.movPoster);
            }
            else {
                String imgUrl = selected.getPoster_path();

                Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + imgUrl)
                        .into(holder.movPoster);
            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.openMovieDetails(selected);
                }
            });
        }
    }//end of onBindViewHolder


    @Override
    public int getItemCount() {
        return masterList.size();
    }

    public void addMovie(List<Movie> newList) {
        masterList.clear();
        masterList.addAll(newList);

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView movPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            movPoster = view.findViewById(R.id.imgMoviePoster);
        }
    }//end of viewholder


    public void showRandomMovie() {
        if (masterList.size() == 0) {
            Toast.makeText(context, "There are no movies in the Want to Watch List", Toast.LENGTH_SHORT).show();
            return;
        }
        Random randomNumGenerator = new Random();
        int upperBound = masterList.size();
        int randNum = randomNumGenerator.nextInt(upperBound);
        mCallback.openMovieDetails(masterList.get(randNum));
    }

}
