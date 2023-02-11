package com.example.user.teachme;

import android.net.Uri;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ConfirmationUser.OnFirstFragmentInteractionListener,MainFragment.OnMainFragmentInteractionListener,ResultFragment.OnResultFragmentInteractionListener {
    String Email;
    Boolean Confirm;
    private ConfirmationUser mConfirmFragment;
    private MainFragment mMainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Confirm=getIntent().getExtras().getBoolean("Confirm");
        Email=getIntent().getExtras().getString("Email").toString();
        mConfirmFragment=ConfirmationUser.newInstance("fdfd","DFdf");
        mMainFragment=MainFragment.newInstance("fdgdfgdf","fgdfd");

        if (!Confirm) {
            getSupportFragmentManager().
                    beginTransaction()
                    .replace(R.id.fragment_container, mConfirmFragment, "ConfirmFragment")
                    .commit();
        } else {
            // if(!isFinishing()) {
            getSupportFragmentManager().
                    beginTransaction()
                    .replace(R.id.fragment_container, mMainFragment, MainFragment.TAG)
                    .commit();
            // }
        }


    }


    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onResultFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFirstFragmentInteraction(Uri uri) {

    }

    @Override
    public void addUser(String Email, String firstName, String lastName, String Phone, String City, String Type, ArrayList<String> Professions, Boolean Confirm) {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user = new User(Email,firstName , lastName,Phone,City,Type,Professions,Confirm);//push after profile
        FirebaseDatabase.getInstance().getReference("users").child(userid).child("profile").setValue(user);
    }

    @Override
    public String getEmail() {
        return Email;
    }

    @Override
    public void onMainFragmentInteraction(Uri uri) {

    }
}
