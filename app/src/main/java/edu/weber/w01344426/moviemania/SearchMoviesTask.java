package edu.weber.w01344426.moviemania;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.unusedapprestrictions.IUnusedAppRestrictionsBackportCallback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import edu.weber.w01344426.moviemania.models.Movie;


public class SearchMoviesTask extends AsyncTask<SearchMoviesTask.SearchMoviesCallback, Integer, String> {

    private String rawJson = "";
    private String searchFor;

    private SearchMoviesCallback mCallBack;

    public interface SearchMoviesCallback
    {
        public void updateSearchList(ArrayList<Movie> searchedList);
    }


    @Override
    protected String doInBackground(SearchMoviesCallback... contexts) {

        this.mCallBack = contexts[0];
        String key = "4df05776b2d204dfb4232c63b4ad614e";

        try{
            URL url = new URL ("https://api.themoviedb.org/3/search/movie?api_key=" + key + "&query=" + searchFor);

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
                    //Toast.makeText(context, "The Login Was Cancelled", Toast.LENGTH_SHORT).show();
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

        ArrayList<Movie> movies = jsonParse(s);

        mCallBack.updateSearchList(movies);

    }//end of onPostExecute


//-----------


    private ArrayList<Movie> jsonParse(String json)
    {
        GsonBuilder gsonBuild = new GsonBuilder();
        Gson gson = gsonBuild.create();

        ArrayList<Movie> movs = null;


        //modify the json to work better
        StringBuffer buffer = new StringBuffer(json);
        while(buffer.charAt(0) != '[')
        {
            buffer.delete(0,1);
        }
        String newJson = buffer.toString();
        buffer = new StringBuffer(newJson);

        int end = buffer.length() - 1;

        while(buffer.charAt(end) != ']')
        {
            buffer.delete(end, end+1);
            end--;
        }
        newJson = buffer.toString();

        json = newJson;

        try{
            movs = gson.fromJson(json, new TypeToken<List<Movie>>(){}.getType());
        }
        catch (Exception e)
        {
            Log.d("Task", "Error converting the json File: " + e.getMessage());
        }

        return movs;
    }//end of jsonParse

    public void setSearch(String search)
    {
        searchFor = search;
    }
}