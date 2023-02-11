package com.example.user.teachme;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.TextInputLayout;
//import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnMainFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    public static final String TAG = "MainFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mRootview, mRootviewCard;
    private OnMainFragmentInteractionListener mListener;
    private DatabaseReference mDatabase,mDatabaseUsers,mDatabaseFavUsers;
    private Button bChooseProfession,bOurTeacher,bAboutUs,bFavorite,bBackMain;
    private Thread Date;
    private String[] item;
    private TextView tvHello,tvDate;
    private TableLayout mLayoutProfessions;
    private ListView mLayoutAboutUs,mLayoutFavorites;
    private RecyclerView mLayoutOurTeacher;
    private CardView TeachersCard;
    private ArrayList<User> Teachers;
    private LinearLayoutManager layoutManager;
    private String TeacherProfessions="";
    private LocationListener locationListener;
    private double longitude,latitude;
    Date d = new Date(new Date().getTime()+28800000);
    SimpleDateFormat DateFormat = new SimpleDateFormat("dd.MM.yy ' ' hh:mm:ss ");
    String CurrentDate;
    private RadioButton mTypeRadioRadius;
    private RadioButton mTypeRadioCity;
    private TextInputLayout txtInputSearch;
    private Button mButtonSearch;
    private Boolean cancel=false;
    private ArrayList<String> ProfessionsChecked;
    private CheckBox[] mCheckBoxProfession;
    private ResultFragment mResultFragment;
    private ArrayList<DataModel> dataSet;
    private CustomAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        // Inflate the layout for this fragment
        mRootview = inflater.inflate(R.layout.fragment_main, container, false);
        mRootviewCard = inflater.inflate(R.layout.cardteachers, container, false);

        layoutManager = new LinearLayoutManager(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvHello = (TextView) mRootview.findViewById(R.id.textViewHello);
        tvDate = (TextView) mRootview.findViewById(R.id.textViewDate);
        bChooseProfession = (Button)mRootview.findViewById(R.id.ButtonChooseProfession);
        bOurTeacher = (Button) mRootview.findViewById(R.id.ButtonOurTeacher);
        bAboutUs = (Button) mRootview.findViewById(R.id.ButtonAboutUs);
        bFavorite = (Button) mRootview.findViewById(R.id.ButtonFavorites);
        bBackMain = (Button) mRootview.findViewById(R.id.ButtonBackMain);
        mLayoutProfessions=(TableLayout) mRootview.findViewById(R.id.LayoutChoseProfession);
        mLayoutOurTeacher = (RecyclerView) mRootview.findViewById(R.id.OurTeacherLayout);
        mLayoutAboutUs = (ListView) mRootview.findViewById(R.id.AboutUsLayout);
        mLayoutFavorites = (ListView) mRootview.findViewById(R.id.FavoritesLayout);
        TeachersCard = (CardView) mRootviewCard.findViewById(R.id.teacherCards);
        bChooseProfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfessions();
            }
        });
        bOurTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOurTeacher();
            }
        });
        bAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutUs();
            }
        });
        bFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFavorites();
            }
        });
        bBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAll();
            }
        });
        mTypeRadioRadius = (RadioButton) mRootview.findViewById(R.id.radioButtonGps);
        mTypeRadioCity = (RadioButton) mRootview.findViewById(R.id.radioButtonCity);
        mTypeRadioRadius.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchTeacher();
            }
        });
        searchTeacher();
        mButtonSearch=(Button)mRootview.findViewById(R.id.ButtonSearch);
        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkResult()){
                    CheckBoxProfession();
                    if(mTypeRadioRadius.isChecked()) {
                        searchTeacherByRadius();
                    }
                    else if(mTypeRadioCity.isChecked()){
                        searchTeacherByCity();
                    }

                        getActivity().getSupportFragmentManager().
                                beginTransaction()
                                .replace(R.id.fragment_container,mResultFragment,"ResultFragment")
                                .commit();
                }
            }
        });
        mLayoutFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = mLayoutFavorites.getItemAtPosition(mLayoutFavorites.getPositionForView(view)).toString().split(" ");
                //Log.d(TAG, "item[9]:"+ item[9]+ ", item:"+ item);
                dialContactPhone(item[9]);
            }
        });
        //  TakeCurrentLocation();
        initDatabaseListener();
        showDate();
        return mRootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMainFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showDate()
    {
        try {
            Date=new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            if(getActivity() == null)
                                return;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CurrentDate = DateFormat.format(new Date());
                                    tvDate.setText(CurrentDate);
                                }
                            });
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Date.start();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void showProfessions(){
        mLayoutProfessions.setVisibility(View.VISIBLE);
        bFavorite.setVisibility(View.GONE);
        bAboutUs.setVisibility(View.GONE);
        bOurTeacher.setVisibility(View.GONE);
        mLayoutOurTeacher.setVisibility(View.GONE);
        mLayoutAboutUs.setVisibility(View.GONE);
        mLayoutFavorites.setVisibility(View.GONE);
        bBackMain.setVisibility(View.VISIBLE);
    }
    public void showOurTeacher(){
        bChooseProfession.setVisibility(View.GONE);
        bFavorite.setVisibility(View.GONE);
        bAboutUs.setVisibility(View.GONE);
        mLayoutOurTeacher.setVisibility(View.VISIBLE);
        mLayoutAboutUs.setVisibility(View.GONE);
        mLayoutFavorites.setVisibility(View.GONE);
        bBackMain.setVisibility(View.VISIBLE);
        ShowTeachers();
    }
    public void showAboutUs(){
        bChooseProfession.setVisibility(View.GONE);
        bOurTeacher.setVisibility(View.GONE);
        bFavorite.setVisibility(View.GONE);
        mLayoutOurTeacher.setVisibility(View.GONE);
        mLayoutAboutUs.setVisibility(View.VISIBLE);
        mLayoutFavorites.setVisibility(View.GONE);
        bBackMain.setVisibility(View.VISIBLE);
        showAboutUsDetails();
    }
    public void showFavorites(){
        bChooseProfession.setVisibility(View.GONE);
        bOurTeacher.setVisibility(View.GONE);
        bAboutUs.setVisibility(View.GONE);
        mLayoutOurTeacher.setVisibility(View.GONE);
        mLayoutAboutUs.setVisibility(View.GONE);
        mLayoutFavorites.setVisibility(View.VISIBLE);
        bBackMain.setVisibility(View.VISIBLE);
        showFavoritesDetails();

    }

    private void showFavoritesDetails() {

        ArrayList<String> FavoritesTeachers = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item, FavoritesTeachers);
        ArrayList<String> userFavs = new ArrayList<String>();
        mDatabaseFavUsers=mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile").child("favoriteTeachers");
        mDatabaseFavUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        userFavs.add(d.getKey());
                    }
                }
                Log.d(TAG, "users:"+ userFavs);
            }//onDataChange

            @Override
            public void onCancelled(DatabaseError error) {

            }//onCancelled
        });

        mDatabaseUsers=mDatabase.child("users");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() != null) {
                    ArrayList<String> Teachers = new ArrayList<String>();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item, Teachers);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        User users = ds.child("profile").getValue(User.class);
                        if (userFavs.contains(ds.getKey())) {
                            makeTeacherProfession(users.Profession);
                            Teachers.add("שם המורה : " + users.firstName + " " + users.lastName + "\n" + "עיר מגורים : " + users.City + "\n" + "פלאפון : " + users.getPhone() + "\n" + "כתובת מייל : " + users.Email + "\n" + "מקצועות לימוד : " + TeacherProfessions);
                        }
                    }
                    mLayoutFavorites.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//    }
    }

    public void showAll(){
        bChooseProfession.setVisibility(View.VISIBLE);
        bOurTeacher.setVisibility(View.VISIBLE);
        bAboutUs.setVisibility(View.VISIBLE);
        bFavorite.setVisibility(View.VISIBLE);
        mLayoutProfessions.setVisibility(View.GONE);
        mLayoutOurTeacher.setVisibility(View.GONE);
        mLayoutAboutUs.setVisibility(View.GONE);
        mLayoutFavorites.setVisibility(View.GONE);
        bBackMain.setVisibility(View.GONE);
    }
    private void initDatabaseListener() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                User user = dataSnapshot.child("users").child(userid).child("profile").getValue(User.class);
                tvHello.setText(user.firstName+" "+user.lastName+" שלום ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void TakeCurrentLocation(){

         final LocationListener locationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
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

    public void ShowTeachers()
    {
        mDatabaseUsers=mDatabase.child("users");
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() != null) {
                    mLayoutOurTeacher.setLayoutManager(layoutManager);
                    dataSet = new ArrayList<DataModel>();


                    //ArrayList<CardView> TeachersCards = new ArrayList<CardView>();
                    //ArrayAdapter<CardView> adapter = new ArrayAdapter<CardView>(getContext(), android.R.layout.test_list_item, TeachersCards);

                    ArrayList<String> Teachers = new ArrayList<String>();
                    //ArrayList<CardView> TeachersCards = new ArrayList<CardView>();
                    //ArrayAdapter<CardView> adapterCards = new ArrayAdapter<CardView>(getContext(), android.R.layout.test_list_item, TeachersCards);

                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item, Teachers);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        User users = ds.child("profile").getValue(User.class);
                        if (users.Type != null) {
                            makeTeacherProfession(users.Profession);
                            if (users.Type.equals("Teacher")) {
                                dataSet.add(new DataModel(("שם המורה : " + users.firstName + " " + users.lastName + "\n" + "עיר מגורים : " + users.City + "\n" + "פלאפון : " + users.getPhone() + "\n" + "כתובת מייל : " + users.Email + "\n" + "מקצועות לימוד : " + TeacherProfessions), ds.getKey()));
//                                Toast.makeText(getContext(), ds.getKey(), Toast.LENGTH_SHORT).show();

                                //TextView textView = (TextView) mRootviewCard.findViewById(R.id.textViewTeacherCard);
                                //textView.setText("שם המורה : " + users.firstName + " " + users.lastName + "\n" + "עיר מגורים : " + users.City + "\n" + "פלאפון : " + users.getPhone() + "\n" + "כתובת מייל : " + users.Email + "\n" + "מקצועות לימוד : " + TeacherProfessions);
                                //System.out.println(textView);
                                //Teachers.add("שם המורה : " + users.firstName + " " + users.lastName + "\n" + "עיר מגורים : " + users.City + "\n" + "פלאפון : " + users.getPhone() + "\n" + "כתובת מייל : " + users.Email + "\n" + "מקצועות לימוד : " + TeacherProfessions);
                                // Teachers.add
                                // TeachersCards.add(TeachersCard);
                                //mLayoutOurTeacher.setAdapter(adapter);
                            }
                        }
                    }
                    adapter = new CustomAdapter(dataSet);
                    mLayoutOurTeacher.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void showAboutUsDetails(){
        ArrayList<String> AboutUs = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.test_list_item,AboutUs);
        AboutUs.add("שם החברה : Teach Me"+"\n\n"+"כתובת : יגאל אלון 65 תל אביב"+"\n\n"+"אימייל : support@TeachMe.com"+"\n\n"+"מספר טלפון : 08-9588455"+"\n\n"+"פקס : 08-9588456");
        AboutUs.add("\n"+"החברה TeachMe נוסדה על מנת להקל על התלמידים במציאת מורים פרטיים באזור מגוריהם."+"\n");
        mLayoutAboutUs.setAdapter(adapter);
    }
    public void makeTeacherProfession(ArrayList user){
        TeacherProfessions="";
        if( user != null)
            for(int i=0; i<user.size(); i++)
                TeacherProfessions += user.get(i) + " ";
    }
    public void searchTeacher(){
        if(mTypeRadioRadius.isChecked()) {
            mRootview.findViewById(R.id.txtCity).setVisibility(View.GONE);
            mRootview.findViewById(R.id.txtRadius).setVisibility(View.VISIBLE);
            txtInputSearch=(TextInputLayout) mRootview.findViewById(R.id.txtRadius);
        }
        else if(mTypeRadioCity.isChecked()){
            mRootview.findViewById(R.id.txtRadius).setVisibility(View.GONE);
            mRootview.findViewById(R.id.txtCity).setVisibility(View.VISIBLE);
            txtInputSearch=(TextInputLayout) mRootview.findViewById(R.id.txtCity);

        }
    }
    public void searchTeacherByCity(){
        mResultFragment=ResultFragment.newInstance(txtInputSearch.getEditText().getText().toString(),"fgdfd");
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ProfessionsChecked",ProfessionsChecked);
        bundle.putString("City",txtInputSearch.getEditText().getText().toString());
        mResultFragment.setArguments(bundle);
    }
    public void searchTeacherByRadius(){
        mResultFragment=ResultFragment.newInstance(txtInputSearch.getEditText().getText().toString(),"fgdfd");
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ProfessionsChecked",ProfessionsChecked);
        bundle.putString("Radius",txtInputSearch.getEditText().getText().toString());
        mResultFragment.setArguments(bundle);
    }
    public boolean checkResult(){
        if(TextUtils.isEmpty(txtInputSearch.getEditText().getText().toString())){
            //txtInputSearch.setError("Field Is Required");
            return true;
        }
        return false;
    }

    public void CheckBoxProfession(){
        initCheckBoxProfessionArray();
        ProfessionsChecked=new ArrayList<String>();
        for(int i=0;i<mCheckBoxProfession.length;i++) {
            if (mCheckBoxProfession[i] != null) {
                if (mCheckBoxProfession[i].isChecked()) {
                    ProfessionsChecked.add(mCheckBoxProfession[i].getText().toString());
                }
            }
        }


    }
    public void initCheckBoxProfessionArray(){
        mCheckBoxProfession= new CheckBox[9];
        mCheckBoxProfession[0] = (CheckBox) mRootview.findViewById(R.id.checkBoxMath2);
        mCheckBoxProfession[1] = (CheckBox) mRootview.findViewById(R.id.checkBoxEnglish2);
        mCheckBoxProfession[2] = (CheckBox) mRootview.findViewById(R.id.checkBoxHebrew2);
        mCheckBoxProfession[3] = (CheckBox) mRootview.findViewById(R.id.checkBoxComputer2);
        mCheckBoxProfession[4] = (CheckBox) mRootview.findViewById(R.id.checkBoxPhysics2);
        mCheckBoxProfession[5] = (CheckBox) mRootview.findViewById(R.id.checkBoxChemistry2);
        mCheckBoxProfession[6] = (CheckBox) mRootview.findViewById(R.id.checkBoxBiology2);
        mCheckBoxProfession[7] = (CheckBox) mRootview.findViewById(R.id.checkBoxHistory2);
        mCheckBoxProfession[8] = (CheckBox) mRootview.findViewById(R.id.checkBoxLiterature2);
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
    public interface OnMainFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMainFragmentInteraction(Uri uri);
    }
}
