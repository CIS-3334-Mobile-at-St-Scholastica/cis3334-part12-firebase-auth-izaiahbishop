package css.cis3334.firebaseauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private TextView textViewStatus;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonGoogleLogin;
    private Button buttonCreateLogin;
    private Button buttonSignOut;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /*
    * OnCreate runs our program when the application starts up. Defines text views, edit texts,
    * Firebase authentication instances, buttons, and method calls to make the buttons function.
    * Needed to run the application and Firebase authentication.
    *
    * @param A state that is a reference to a bundle object
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                 //Call onCreate using the bundle object parameter passed to onCreate
        setContentView(R.layout.activity_main);                             //Set the content of the main application to out activity_main

        mAuth = FirebaseAuth.getInstance();                                 //set mAuth to the object FirebaseAuth with the method call getInstance()
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);      //set textViewStatus to the textViewStatus in our activity_main.xml
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);        //set editTextEmail to the editTextEmail in our activity_main.xml
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);  //set editTextPassword to the editTextPassword in our activity_main.xml
        buttonLogin = (Button) findViewById(R.id.buttonLogin);              //set buttonLogin to the buttonLogin in our activity_main.xml
        buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);  //set buttonGoogleLogin to the buttonGoogleLogin in our activity_main.xml
        buttonCreateLogin = (Button) findViewById(R.id.buttonCreateLogin);  //set buttonCreateLogin to the buttonCreateLogin in our activity_main.xml
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);          //set buttonSignOut to the buttonSignOut in our activity_main.xml

        buttonLogin.setOnClickListener(new View.OnClickListener() {         //Set the onClickListener for the login button
            public void onClick(View v) {                                   //Define action for button for normal login
                signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());  //Call signIn with the contents from the editTextEmail and editTextPassword
                textViewStatus.setText("Logging in");
            }
        });

        buttonCreateLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                       //Set the onClickListener for the create login button
                createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());   //Call createAccount with the contents of editTextEmail and editTextPassword
                textViewStatus.setText("Creating a new account");
            }
        });

        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                       //Set the onClickListener for the Google login button
                googleSignIn();                                             // Call the googleSignIn method
                textViewStatus.setText("Signing in with Google Account");
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                           //Set the onClickListener for the SignOut button
                signOut();                                                  //Call the signOut method
                textViewStatus.setText("Signed out");
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {              //Set mAuthListener to a new FirebaseAuth object with the method call AuthStateListener()

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();          //create a new FirebaseUser object for updating textViewStatus
                if (user != null) {
                    textViewStatus.setText("User is signed in");            // User is signed in
                } else {
                    textViewStatus.setText("User is signed out");           // User is signed out
                }
            }
        };


    }

    /*
    * Used when the application starts. A call is made to addAuthStateListener to register or unregister listeners.
    */
    @Override
    public void onStart() {
        super.onStart();                                    //Activity is now being displayed to the user (again)
        mAuth.addAuthStateListener(mAuthListener);          //Used to register (add) listeners. Listeners are called when there is a change to the authentication state.
    }

    /*
    * This method is called to no longer display the activity (not visible) to the user.
    */
    @Override
    public void onStop() {
        super.onStop();                                     //Activity no longer visible to users
        if (mAuthListener != null) {                        //Listening for an authentication state - If null then do below
            mAuth.removeAuthStateListener(mAuthListener);   //Used to unregister (remove) listeners. Listeners are called when there is a change to the authentication state.
        }
    }

    /*
    * Used to create an account of Firebase using an email and password
    *
    * @param the email address to be used for account creation
    * @param the password to be used for account creation
    */
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)                                       //Create a new user with email and password and make method call to addOnCompleteListener (adds a listener that is called when the task completes)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    /*
                    * Method used when the task of creating a user has completed
                    *
                    * @param the authentication result of the completion of the task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        textViewStatus.setText("User has been created");                            // User successfully created
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {                                                 // If user is not created successfully
                            textViewStatus.setText("Authentication Failed");
                        }

                        // ...
                    }
                });
    }

    /*
    * Used to sign in with preexisting credentials.
    *
    * @param the email address to be used to sign in
    * @param the password to be used to sign in
    */
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)                                           //Sign in user with email and password and make method call to addOnCompleteListener (adds a listener that is called when the task completes)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    /*
                    * Method used when the task of signing in has completed
                    *
                    * @param the authentication result of the completion of the task
                    */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        textViewStatus.setText("User Successfully logged in");                      // User logs in successfully

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            textViewStatus.setText("Authentication Failed");                        // If user is not signed in successfully
                        }

                        // ...
                    }
                });
    }

    /*
    * Method is used for user sign out (Firebase)
    */
    private void signOut () {
        mAuth.signOut();
    }

    /*
    * Method would contain code to sign in using Google credentials.
    */
    private void googleSignIn() {

    }


}
