package com.example.hamzah.modulediary.Settings;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.hamzah.modulediary.R;
import com.example.hamzah.modulediary.RegisterActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS_LIST;

/**
 * Contains the sing out and delete user options
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.llSettingsSignOut) LinearLayout llSignOut;
    @BindView(R.id.llSettingsDeleteAccount) LinearLayout llDeleteAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);
        llSignOut.setOnClickListener(this);
        llDeleteAccount.setOnClickListener(this);
        return rootView;
    }

    //user sign out
    private void signOut() {
        //alert dialog to ask the user again
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sign out")
                .setMessage("Are you sure you want to sign out of your account?")
                .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AuthUI.getInstance()
                                .signOut(getActivity())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        startActivity(new Intent(getActivity(), RegisterActivity.class));
                                        getActivity().finish();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.llSettingsSignOut:
                signOut();
                break;
            case R.id.llSettingsDeleteAccount:
                deleteUser();
                break;
            default:
                break;
        }
    }

    private void deleteUser() {
        //alert dialog lets user back out if they don't want to delete their account
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        String uID = user.getUid();
                        //deletes user from the database nodes associated with their user ID
                        mDatabase.child(USERS)
                                .child(uID).setValue(null);
                        mDatabase.child(USERS_LIST).child(uID).setValue(null);
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getActivity(), RegisterActivity.class));
                                            getActivity().finish();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }
}
