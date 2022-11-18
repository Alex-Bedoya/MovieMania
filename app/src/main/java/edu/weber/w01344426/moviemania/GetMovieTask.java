package edu.weber.w01344426.moviemania;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.unusedapprestrictions.IUnusedAppRestrictionsBackportCallback;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import edu.weber.w01344426.moviemania.models.Genre;
import edu.weber.w01344426.moviemania.models.Movie;
import kotlin.reflect.KVariance;


public class GetMovieTask extends AsyncTask<Context, Integer, String> {

    private String rawJson = "";
    private Context context;
    private int movieID;

    private String key;




    @Override
    protected String doInBackground(Context... contexts) {

        this.context = contexts[0];
        key = "4df05776b2d204dfb4232c63b4ad614e";

        try{
            URL url = new URL ("https://api.themoviedb.org/3/movie/" + movieID + "?api_key=" + key + "&append_to_response=release_dates,videos,credits");

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();

            int status = connection.getResponseCode();

            //Log.d("Task", "Connection Result: " + status);

            switch (status)
            {
                case 200:
                case 201:
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );

                    rawJson = reader.readLine();
                    return rawJson;

                default:
                    //TODO: handle a failure
                    Log.d("Task", "Failed to connect and read the data.");
            }


        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("Task", "URL Malformed: " + e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();//this is for the url.openconnection
            Log.d("Task", "IO Exception opening HTTPS Connection: " + e.getMessage());
        }


        return null;
    }//end of doInBackground


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Movie newMovie = jsonParse(s);

        if (newMovie == null) {
            Toast.makeText(context, "Unable to Add that movie to the list ", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference(userID).child("MasterList");

        mDatabase.child("" + newMovie.getId()).setValue(newMovie);

        Toast.makeText(context, newMovie.getTitle() + " has been added to the master list", Toast.LENGTH_SHORT).show();


    }//end of onPostExecute





//------------

    private Movie jsonParse(String json)
    {
        GsonBuilder gsonBuild = new GsonBuilder();
        Gson gson = gsonBuild.create();

        Movie newMovie = null;

        try{
            String title, vote_average, overview, poster_path, release_date, trailerURL, director, composer, certification;
            int runtime;
            ArrayList<Genre> genres;
            ArrayList<String> cast = new ArrayList<>();


            //get basic movie data
            JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
            title = jsonObj.get("title").getAsString();
            vote_average = jsonObj.get("vote_average").getAsString();
            overview = jsonObj.get("overview").getAsString();
            if (jsonObj.get("poster_path") != null) {
                poster_path = jsonObj.get("poster_path").getAsString();
            } else {
                poster_path = "";
            }
            release_date = jsonObj.get("release_date").getAsString();
            runtime = jsonObj.get("runtime").getAsInt();

            //get list of genres
            genres = gson.fromJson(jsonObj.get("genres").getAsJsonArray(), new TypeToken<List<Genre>>(){}.getType());

            //get the first trailer url from a list of all trailers
            trailerURL = "";

            JsonObject temp = gson.fromJson(jsonObj.get("videos"), JsonObject.class);
            ArrayList<JsonObject> tempTrailerList = gson.fromJson( temp.get("results").getAsJsonArray(), new TypeToken<List<JsonObject>>(){}.getType());
            if (tempTrailerList != null && tempTrailerList.size() != 0) {
                trailerURL = tempTrailerList.get(0).get("key").getAsString();
            }

            //get a list of first 3 cast members and find the director and composer.
            JsonObject temp2 = gson.fromJson(jsonObj.get("credits"), JsonObject.class);
            ArrayList<JsonObject> tempCastList = gson.fromJson( temp2.get("cast").getAsJsonArray(), new TypeToken<List<JsonObject>>(){}.getType());
            ArrayList<JsonObject> tempCrewList = gson.fromJson( temp2.get("crew").getAsJsonArray(), new TypeToken<List<JsonObject>>(){}.getType());

            if (tempCastList != null && tempCastList.size() != 0) {
                int size = 5;
                if (tempCastList.size() < size) {
                    size = tempCastList.size();
                }
                for (int i = 0; i < size; i++) {
                    if (tempCastList.get(i) != null) {
                        cast.add(tempCastList.get(i).get("name").getAsString());
                    }
                }
            }

            director = "";
            composer = "";

            if (tempCrewList != null && tempCrewList.size() != 0) {
                for (int i = 0; i < tempCrewList.size(); i++) {
                    if (tempCrewList.get(i) != null) {
                        if (tempCrewList.get(i).get("job").getAsString().equals("Director")) {
                            director = tempCrewList.get(i).get("name").getAsString();
                        }
                        if (tempCrewList.get(i).get("job").getAsString().equals("Original Music Composer") ||
                                tempCrewList.get(i).get("job").getAsString().equals("Music")) {
                            composer = tempCrewList.get(i).get("name").getAsString();
                        }
                    }
                    if (!director.equals("") && !composer.equals("")) {
                        break;
                    }
                }//end of for loop
            }



            JsonObject temp3 = gson.fromJson(jsonObj.get("release_dates"), JsonObject.class);
            ArrayList<JsonObject> tempRatingsList = gson.fromJson( temp3.get("results").getAsJsonArray(), new TypeToken<List<JsonObject>>(){}.getType());

            certification = "Not Rated";

            if (tempRatingsList != null && tempRatingsList.size() != 0) {
                for (int i = 0; i < tempRatingsList.size(); i++) {
                    if (tempRatingsList.get(i) != null) {
                        if (tempRatingsList.get(i).get("iso_3166_1").getAsString().equals("US")) {
                            ArrayList<JsonObject> tempRatings = gson.fromJson(tempRatingsList.get(i).get("release_dates").getAsJsonArray(), new TypeToken<List<JsonObject>>() {
                            }.getType());
                            for (int j = 0; j < tempRatings.size(); j++) {
                                if (tempRatings.get(j) != null) {
                                    if (!tempRatings.get(j).get("certification").getAsString().equals("")) {
                                    //if (tempRatings.get(j).get("type").getAsInt() == 3 || tempRatings.get(j).get("type").getAsInt() == 1) {
                                        certification = tempRatings.get(j).get("certification").getAsString();
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }

            newMovie = new Movie(movieID, title, vote_average, certification, runtime, overview, genres, poster_path, release_date, false, false, false, "", 0, trailerURL, director, composer, cast);

        }
        catch (Exception e)
        {
            Log.d("Task", "Error converting the json File: " + e.getMessage());
        }

        return newMovie;

    }//end of jsonParse


//------------


    public void setMovieID(int movID) {
        movieID = movID;
    }
}

