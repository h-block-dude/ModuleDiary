package com.example.hamzah.modulediary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.hamzah.modulediary.SetUp.UniversityPickerActivity;
import com.example.hamzah.modulediary.User.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS_LIST;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private static final int RC_SIGN_IN = 123;
    private boolean userExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //check if user is already signed in
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            //otherwise launch firebase auth UI to register user
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().build(),
                    RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                //check to see if user exists in database
                checkUser(response.getEmail());

                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
//                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

        }
    }

    //if user doesn't exist, must register
    // otherwise sign in
    private void checkUser(final String email) {
        mDatabase.child(USERS_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0){
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                        if (postSnapShot.getValue(String.class).equalsIgnoreCase(email)){
                            userExists = true;
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                    if(!userExists){
                        addUser();
                        Intent intent = new Intent(RegisterActivity.this, UniversityPickerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    addUser();
                    Intent intent = new Intent(RegisterActivity.this, UniversityPickerActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //adds new user to list of users
    private void addUser() {
        //get profile info
        String name, email,uID;
        name = firebaseAuth.getCurrentUser().getDisplayName();
        email = firebaseAuth.getCurrentUser().getEmail();
        uID = firebaseAuth.getCurrentUser().getUid();
        //create object
        User user = new User(name, email);
        //write to firebase
        mDatabase.child(USERS).child(uID).setValue(user);
        mDatabase.child(USERS_LIST).child(uID).setValue(email);
    }
}