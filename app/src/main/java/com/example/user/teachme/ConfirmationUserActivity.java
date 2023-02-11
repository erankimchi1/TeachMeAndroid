package com.example.user.teachme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ConfirmationUserActivity extends AppCompatActivity implements ConfirmationUser.OnFirstFragmentInteractionListener,MainFragment.OnMainFragmentInteractionListener,ResultFragment.OnResultFragmentInteractionListener{
    private FirebaseAuth mAuth;
    private String Email;
    private View mProgressView;
    private DatabaseReference mDatabase;
    private ConfirmationUser mConfirmFragment;
    private MainFragment mMainFragment;
    private Boolean Confirm;
    private Boolean flagContinue = true;
    LocationManager locationManager;
    Location location;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_user);
        Email = getIntent().getExtras().getString("Email").toString();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initDatabaseListener();
    }

    private void initDatabaseListener() {
        if(this == null)
            return;
        mDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                User user = dataSnapshot.child("users").child(userid).child("profile").getValue(User.class);
                setBoolean(user.Confirm);
                GpsTracker();
                Intent intent = new Intent(ConfirmationUserActivity.this,MainActivity.class);
                intent.putExtra("Confirm",Confirm);
                intent.putExtra("Email",Email);
                if(flagContinue) {
                    startActivity(intent);
                    flagContinue=false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void addUser(String Email, String firstName, String lastName, String Phone,String City, String Type, ArrayList<String> Professions, Boolean Confirm){
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user = new User(Email,firstName , lastName,Phone,City,Type,Professions,Confirm);//push after profile
        FirebaseDatabase.getInstance().getReference("users").child(userid).child("profile").setValue(user);
    }

    public void setBoolean(boolean status){
        Confirm = status;
    }

    public Boolean CheckConfirm(){

        if(Confirm) {
            return true;
        }
        else
        {
            return false;
        }
      }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GpsTracker() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                addGpsCoordinates(location.getLongitude(),location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                //Toast.makeText(ConfirmationUserActivity.this, "enabled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(ConfirmationUserActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.INTERNET},10);
            return;
        }
        else
            setLocation();
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        if (locationManager != null) {
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 5000, 5, locationListener);
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location != null) {
                addGpsCoordinates(location.getLongitude(),location.getLatitude());
            } else
                Toast.makeText(ConfirmationUserActivity.this, "location = null", Toast.LENGTH_LONG).show();
        }
    }
    public void addGpsCoordinates(double Longitude,double Latitude){
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user = new User(Longitude,Latitude);//push after profile
        FirebaseDatabase.getInstance().getReference("users").child(userid).child("GPS Coordinates").setValue(user);
    }

    @Override
    public void onFirstFragmentInteraction(Uri uri) {

    }
    @Override
    public String getEmail(){
        return Email;
    }
    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onMainFragmentInteraction(Uri uri) {

    }

    @Override
    public void onResultFragmentInteraction(Uri uri) {

    }
}
