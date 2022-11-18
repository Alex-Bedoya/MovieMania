package edu.weber.w01344426.moviemania;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import edu.weber.w01344426.moviemania.databinding.ActivityMainBinding;
import edu.weber.w01344426.moviemania.models.Movie;
import edu.weber.w01344426.moviemania.models.MovieRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements SearchMoviesTask.SearchMoviesCallback,
                                                               MovieRecyclerAdapter.onMovieClick,
                                                               MovieDetailsFragment.deleteCourseDialog,
                                                               DeleteMovieDialog.deleteMovie {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private AddMovieFragment addMovieFragment;
    private MovieDetailsFragment movDetailsDialog;

    private FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar));
        //setSupportActionBar(binding.appBarMain.toolbar);

        //hamburger layout
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_master_list, R.id.nav_wanttowatch_list, R.id.nav_owned_list, R.id.nav_ranked_list, R.id.nav_account_info)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //end of hamburger layout stuff


        fm = getSupportFragmentManager();

        FirebaseAuth fbAuth = FirebaseAuth.getInstance();

        if (fbAuth.getCurrentUser() == null) {

            initiateSignIn();
//
        //    // Choose authentication providers
        //    List<AuthUI.IdpConfig> providers = Arrays.asList(
        //            new AuthUI.IdpConfig.EmailBuilder().build());//,
        //            //new AuthUI.IdpConfig.GoogleBuilder().build());
//
        //    // Create and launch sign-in intent
        //    Intent signInIntent = AuthUI.getInstance()
        //            .createSignInIntentBuilder()
        //            .setAvailableProviders(providers)
        //            .build();
        //    signInLauncher.launch(signInIntent);

        }

    }//end of onCreate



    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void initiateSignIn()
    {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        //new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);


    }


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Toast.makeText(getApplicationContext(), "Successfully signed in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
            //Log.d("Firebase", "User signed in");
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            if (response == null)
            {
                Toast.makeText(getApplicationContext(), "The Login Was Cancelled, please try again", Toast.LENGTH_SHORT).show();
                initiateSignIn();
                //Log.d("Authxx", "Login Cancelled, response is null");
            }
            else
            {

                Toast.makeText(getApplicationContext(), "Login Failed, please try again", Toast.LENGTH_SHORT).show();
                initiateSignIn();
                //Log.d("Authxx", "Login Failed: " + response.getError().getMessage());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void updateSearchList(ArrayList<Movie> searchedList) {
        if (addMovieFragment == null) {
            addMovieFragment = (AddMovieFragment) fm.findFragmentByTag("AddMovieDialog");
        }
        if (addMovieFragment != null) {
            addMovieFragment.updateSearchList(searchedList);
        }
    }


    @Override
    public void openMovieDetails(Movie selectedMovie) {
        movDetailsDialog = new MovieDetailsFragment();
        movDetailsDialog.setMovie(selectedMovie);
        movDetailsDialog.show(getSupportFragmentManager(), "MovieDetailsDialog");
    }

    @Override
    public void deleteCourseDialog() {
        DeleteMovieDialog delDialog = new DeleteMovieDialog();
        delDialog.setCancelable(true);
        delDialog.show(getSupportFragmentManager(), "DeleteDialog");
    }

    @Override
    public void deleteSelectedMovie() {
        if (movDetailsDialog == null){
            movDetailsDialog = (MovieDetailsFragment) fm.findFragmentByTag("MovieDetailsDialog");
        }
        if (movDetailsDialog != null) {
            movDetailsDialog.deleteMovie();
        }
    }
}