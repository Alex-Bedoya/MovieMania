package edu.weber.w01344426.moviemania.models;

import android.util.Log;

import java.util.ArrayList;

public class Movie {

    private int id;

    private String title;
    private String vote_average;
    private String certification;
    private int runtime;
    private String overview;

    private ArrayList<Genre> genres;

    private String poster_path;
    private String release_date;

    private boolean bHaveWatched;
    private boolean bOwned;
    private boolean bRanked;

    private String userComments;
    private float userRating;

    private String trailerURL;

    private int userRanking;

    //private static int totalRanked;

    private String director;
    private String composer;
    private ArrayList<String> cast;


    @Override
    public String toString() {
        return "\nMovie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", vote_average='" + vote_average + '\'' +
                ", runtime=" + runtime +
                ", overview='" + overview + '\'' +
                ", genres=" + genres +
                ", poster_path='" + poster_path + '\'' +
                '}';
    }


    public Movie()
    {
        bHaveWatched = false;
        bOwned = false;
        bRanked = false;
        //totalRanked = 0;
    }



    public Movie(int id, String title, String vote_average, String certification, int runtime,
                 String overview, ArrayList<Genre> genres, String poster_path, String release_date,
                 boolean bHaveWatched, boolean bOwned, boolean bRanked, String userComments, float userRating,
                 String trailerURL, String director, String composer, ArrayList<String> cast) {
        this.id = id;
        this.title = title;
        this.vote_average = vote_average;
        this.certification = certification;
        this.runtime = runtime;
        this.overview = overview;
        this.genres = genres;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.bHaveWatched = bHaveWatched;
        this.bOwned = bOwned;
        this.bRanked = bRanked;
        this.userComments = userComments;
        this.userRating = userRating;
        this.trailerURL = trailerURL;
        this.director = director;
        this.composer = composer;
        this.cast = cast;
    }


    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public boolean isbHaveWatched() {
        return bHaveWatched;
    }

    public void setbHaveWatched(boolean bHaveWatched) {
        this.bHaveWatched = bHaveWatched;
    }

    public boolean isbOwned() {
        return bOwned;
    }

    public void setbOwned(boolean bOwned) {
        this.bOwned = bOwned;
    }

    public String getTrailerURL() {
        return trailerURL;
    }

    public void setTrailerURL(String trailerURL) {
        this.trailerURL = trailerURL;
    }

    public boolean isbRanked() {
        return bRanked;
    }

    public void setbRanked(boolean bRanked) {
        this.bRanked = bRanked;
    }

    //public static int getTotalRanked() {
    //    return totalRanked;
    //}
//
    //public static void setTotalRanked(int totalRanked) {
    //    Movie.totalRanked = totalRanked;
    //}
//
    public String getUserComments() {
        return userComments;
    }

    public void setUserComments(String userComments) {
        this.userComments = userComments;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    public int getUserRanking() {
        return userRanking;
    }

    public void setUserRanking(int userRanking) {
        this.userRanking = userRanking;
    }

    /*
    public void addRemoveRankedMovie(boolean isRanked)
    {
        if (this.bRanked == isRanked){
            return;
        }
        this.bRanked = isRanked;
        if (isRanked) {
            totalRanked++;
        }
        else {
            totalRanked--;
        }

        Log.d("Ranked", "TotalRanked: " + totalRanked);
    }
     */
}


