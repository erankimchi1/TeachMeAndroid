package com.example.user.teachme;

import static android.content.Context.LOCATION_SERVICE;

import static com.example.user.teachme.MainFragment.TAG;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultFragment.OnResultFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnResultFragmentInteractionListener mListener;
    private View mRootView;
    private DatabaseReference mDatabase, mDatabaseUsers;
    private String City;
    private String Radius;
    private String Phone;
    private String[] item;
    private String TeacherProfessions = "";
    private ListView mResultTeacherLayout;
    private ArrayList<String> ProfessionsChecked;
    private Button ButtonBackMainFromResult;
    private MainFragment mMainFragment;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private double myLongitude, myLatitude;
    private Location location;


    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_result, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mResultTeacherLayout = (ListView) mRootView.findViewById(R.id.ResultTeacherLayout);
        City = getArguments().getString("City");
        Radius = getArguments().getString("Radius");
        ProfessionsChecked = getArguments().getStringArrayList("ProfessionsChecked");
        mMainFragment = MainFragment.newInstance("gfgf", "fgfgf");
        TakeCurrentLocation();
        ButtonBackMainFromResult = (Button) mRootView.findViewById(R.id.ButtonBackMainFromResult);
        ButtonBackMainFromResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().
                        beginTransaction()
                        .replace(R.id.fragment_container, mMainFragment, "MainFragment")
                        .commit();
            }
        });
        if (City == null)
            ShowCorrectTeachersByRadius();
        else if (Radius == null)
            ShowCorrectTeachersByCity();
        mResultTeacherLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = mResultTeacherLayout.getItemAtPosition(mResultTeacherLayout.getPositionForView(view)).toString().split(" ");
                dialContactPhone(item[9]);
            }
        });

        return mRootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onResultFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResultFragmentInteractionListener) {
            mListener = (OnResultFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnResultFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void ShowCorrectTeachersByCity() {
        mDatabaseUsers = mDatabase.child("users");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() != null) {
                    ArrayList<String> Teachers = new ArrayList<String>();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item, Teachers);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final User users = ds.child("profile").getValue(User.class);
                        if (users.Type != null && users.City != null && users.Profession != null) {
                            makeTeacherProfession(users.Profession);
                            if (users.City.equals(City) && checkIfTeacherProfession(users.Profession)) {
                                Teachers.add("שם המורה : " + users.firstName + " " + users.lastName + "\n" + "עיר מגורים : " + users.City + "\n" + "פלאפון : " + users.getPhone() + " \n" + "כתובת מייל : " + users.Email + "\n" + "מקצועות לימוד : " + TeacherProfessions);
                                mResultTeacherLayout.setAdapter(adapter);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void ShowCorrectTeachersByRadius() {
        final float[] results=null;

        mDatabaseUsers = mDatabase.child("users");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() != null) {
                    ArrayList<String> Teachers = new ArrayList<String>();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item, Teachers);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final User users = ds.child("profile").getValue(User.class);
                        User usersGps = ds.child("GPS Coordinates").getValue(User.class);
                        if (users.Type != null && users.City != null && users.Profession != null) {
                            makeTeacherProfession(users.Profession);
                            Toast.makeText(getContext(), "My lat:" + myLatitude + " , My Long:" + myLongitude, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "teacher lat:" + usersGps.Latitude + " , teacher Long:" +usersGps.Longitude, Toast.LENGTH_SHORT).show();
                            Location myLocation = new Location("point A");
                            myLocation.setLatitude(myLatitude);
                            myLocation.setLongitude(myLongitude);
                            Location teacherLocation = new Location("point B");
                            teacherLocation.setLatitude(usersGps.Latitude);
                            teacherLocation.setLongitude(usersGps.Longitude);
                            double distance = myLocation.distanceTo(teacherLocation)/1000;
                            Toast.makeText(getContext(), "distance:"+distance, Toast.LENGTH_SHORT).show();
                            if (Integer.parseInt(Radius)>=distance && checkIfTeacherProfession(users.Profession)) {
                                Teachers.add("שם המורה : " + users.firstName + " " + users.lastName + "\n" + "עיר מגורים : " + users.City + "\n" + "פלאפון : " + users.getPhone() + " \n" + "כתובת מייל : " + users.Email + "\n" + "מקצועות לימוד : " + TeacherProfessions);
                                mResultTeacherLayout.setAdapter(adapter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static double caluclateDistance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    public void makeTeacherProfession(ArrayList user) {
        TeacherProfessions = "";
        if (user != null)
            for (int i = 0; i < user.size(); i++)
                TeacherProfessions += user.get(i) + " ";
    }

    public boolean checkIfTeacherProfession(ArrayList<String> professionsLearn) {
        boolean correct = false;
        if (professionsLearn != null) {
            for (String profession : professionsLearn) {
                for (String professionChecked : ProfessionsChecked) {
                    if (professionChecked.equals(profession))
                        correct = true;
                }
            }
        }
        return correct;
    }

    public void TakeCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastLocation != null) {
            myLongitude = lastLocation.getLongitude();
            myLatitude = lastLocation.getLatitude();
            Log.d(TAG, "Retrived location: long:"+myLongitude+" , lati:"+myLatitude);
        }
        final LocationListener locationListenerNetwork = new LocationListener() {

            public void onLocationChanged(Location location) {
                myLongitude = location.getLongitude();
                myLatitude = location.getLatitude();
                //   Log.d(TAG, "onLocationChanged: long:"+longitude+" , lati:"+latitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

    }
    private void dialContactPhone(final String phoneNumber) {
        //Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(intent);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnResultFragmentInteractionListener {
        // TODO: Update argument type and name
        void onResultFragmentInteraction(Uri uri);
    }

}
