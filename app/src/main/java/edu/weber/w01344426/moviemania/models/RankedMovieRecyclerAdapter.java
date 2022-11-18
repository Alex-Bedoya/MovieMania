package edu.weber.w01344426.moviemania.models;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.weber.w01344426.moviemania.MovieDetailsFragment;
import edu.weber.w01344426.moviemania.R;

public class RankedMovieRecyclerAdapter extends RecyclerView.Adapter<RankedMovieRecyclerAdapter.ViewHolder> {//FirebaseRecyclerAdapter<Movie, MovieRecyclerAdapter.ViewHolder>{//

    private ArrayList<Movie> rankedList;
    //private onMovieClick mCallback;

    public RankedMovieRecyclerAdapter(ArrayList<Movie> masterList) {
        this.rankedList = masterList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.ranked_view, parent, false);

        try {
            //mCallback = (RankedMovieRecyclerAdapter.onMovieClick) view.getContext();
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException( view.toString()
                    + " Must implement MovieRecyclerAdapter.onMovieClick");
        }

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Movie selected = rankedList.get(position);

        if (selected != null) {

            String movTitleYear = selected.getTitle() +
                    " (" + selected.getRelease_date().substring(0,4) + ")";

            holder.movYearName.setText(movTitleYear);

            holder.rankNum.setText("#" + (position + 1));


            int width = (holder.view.getResources().getDisplayMetrics().widthPixels / 4);
            int height = ((width * 3) / 2);

            holder.movPoster.getLayoutParams().height = height;
            holder.movPoster.getLayoutParams().width = width;


            String imgUrl = selected.getPoster_path();

            if (selected.getPoster_path() != null) {
                Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + imgUrl)
                        .into(holder.movPoster);
            }
        }
    }//end of onBindViewHolder


    @Override
    public int getItemCount() {
        return rankedList.size();
    }

    public void addMovie(List<Movie> newList)
    {
        rankedList.clear();
        rankedList.addAll(newList);

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView movYearName;
        public TextView rankNum;
        public ImageView movPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            movYearName = view.findViewById(R.id.lblRankedTitleYear);
            rankNum = view.findViewById(R.id.lblRankNum);
            movPoster = view.findViewById(R.id.imgRankedPoster);
        }
    }
}
