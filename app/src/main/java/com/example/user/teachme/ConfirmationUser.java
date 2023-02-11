package com.example.user.teachme;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.TabLayout;
//import android.support.design.widget.TextInputLayout;
//import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.user.teachme.MainFragment.TAG;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfirmationUser.OnFirstFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfirmationUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmationUser extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    

    private View mRootView;

    private OnFirstFragmentInteractionListener mListener;
    private AutoCompleteTextView mFirstNameView;
    private AutoCompleteTextView mLastNameView;
    private AutoCompleteTextView mPhoneView;
    private TableLayout mProfession;
    private AutoCompleteTextView mEmail;
    private RadioButton mTypeRadioStudent,mTypeRadioTeacher;
    private CheckBox[] mCheckBoxProfession;
    private String mType;
    private String Email;
    private TextView mTextViewTeacherLearn;
    private Spinner spinner;
    private ArrayList<String> ProfessionsChecked;
    String json_string;
    JSONObject jsonObj;
    JSONArray jsonArray;
    ArrayList<String> CityList = new ArrayList<String>();

    public ConfirmationUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmationUser.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmationUser newInstance(String param1, String param2) {
        ConfirmationUser fragment = new ConfirmationUser();
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
        mRootView = inflater.inflate(R.layout.fragment_confirmation_user, container, false);
        mFirstNameView = (AutoCompleteTextView) mRootView.findViewById(R.id.txtFirstName);
        mLastNameView = (AutoCompleteTextView) mRootView.findViewById(R.id.txtLastName);
        mPhoneView = (AutoCompleteTextView) mRootView.findViewById(R.id.txtPhone);
        mProfession = (TableLayout) mRootView.findViewById(R.id.LayoutChoseProfession);
        mTypeRadioStudent = (RadioButton) mRootView.findViewById(R.id.radioButtonStudent);
        mTypeRadioTeacher = (RadioButton) mRootView.findViewById(R.id.radioButtonTeacher);
        mTextViewTeacherLearn = (TextView) mRootView.findViewById(R.id.TextViewTeacherLearn);
        spinner = (Spinner)mRootView.findViewById(R.id.SpinnerCities);
        json_string = loadJSONFromAsset();
        getStringArrayJson();
        CheckFieldToShow();
        initSubmitListener();
        return mRootView;
    }


    @Nullable
    @Override
    public View getView() {
        return mRootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFirstFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFirstFragmentInteractionListener) {
            mListener = (OnFirstFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFirstFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void send(){
        if(isValid()){
            CheckType();
            CheckBoxProfession();
            mListener.addUser(mListener.getEmail(),mFirstNameView.getText().toString(),mLastNameView.getText().toString(),mPhoneView.getText().toString(),spinner.getSelectedItem().toString(),mType,ProfessionsChecked,true);
            Toast.makeText(getActivity(), "Details Updated Successfully", Toast.LENGTH_SHORT).show();
            MainFragment mMainFragment = MainFragment.newInstance("fdgdfgdf","fgdfd");
            getActivity().getSupportFragmentManager().
                    beginTransaction()
                    .replace(R.id.fragment_container, mMainFragment, MainFragment.TAG)
                    .commit();
        }

    }

    public Boolean isValid(){
        Boolean cancel=false;
        if(!mTypeRadioStudent.isChecked() && !mTypeRadioTeacher.isChecked()) {
            mTypeRadioTeacher.setError("Type Field Is Required");
            cancel = true;
        }
        else{
          mTypeRadioTeacher.setError(null);
        }

        if(TextUtils.isEmpty(mFirstNameView.getText().toString())){
            mFirstNameView.setError("First Name Field Is Required");
            cancel = true;
        }
        if(TextUtils.isEmpty(mLastNameView.getText().toString())){
            mLastNameView.setError("Last Name Field Is Required");
            cancel = true;
        }
        if(TextUtils.isEmpty(mPhoneView.getText().toString())){
            mPhoneView.setError("Phone Field Is Required");
            cancel = true;
        }
        if(cancel)
        {
            return false;
        }
        else
            return true;
    }

    public void initSubmitListener(){
        Button mSubmit=(Button)mRootView.findViewById(R.id.ButtonSumbit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }
    public void CheckBoxProfession(){
        initCheckBoxProfessionArray();
        ProfessionsChecked=new ArrayList<String>();
        int j=0;
        for(int i=0;i<mCheckBoxProfession.length;i++)
        {
            if(mCheckBoxProfession[i].isChecked())
            {
                ProfessionsChecked.add(mCheckBoxProfession[i].getText().toString());
                j++;
                Log.d(TAG, mCheckBoxProfession[i].getText().toString()+" Checked");
            }
            else
                Log.d(TAG, mCheckBoxProfession[i].getText().toString()+" not Checked");

        }


    }

    public void initCheckBoxProfessionArray(){
        mCheckBoxProfession= new CheckBox[9];
        mCheckBoxProfession[0] = (CheckBox) mRootView.findViewById(R.id.checkBoxMath);
        mCheckBoxProfession[1] = (CheckBox) mRootView.findViewById(R.id.checkBoxEnglish);
        mCheckBoxProfession[2] = (CheckBox) mRootView.findViewById(R.id.checkBoxHebrew);
        mCheckBoxProfession[3] = (CheckBox) mRootView.findViewById(R.id.checkBoxComputer);
        mCheckBoxProfession[4] = (CheckBox) mRootView.findViewById(R.id.checkBoxPhysics);
        mCheckBoxProfession[5] = (CheckBox) mRootView.findViewById(R.id.checkBoxChemistry);
        mCheckBoxProfession[6] = (CheckBox) mRootView.findViewById(R.id.checkBoxBiology);
        mCheckBoxProfession[7] = (CheckBox) mRootView.findViewById(R.id.checkBoxHistory);
        mCheckBoxProfession[8] = (CheckBox) mRootView.findViewById(R.id.checkBoxLiterature);
    }
    public void CheckTeacherField(){
        if(mTypeRadioStudent.isChecked())
        {
            mProfession.setVisibility(View.GONE);

            Log.d(TAG, "CheckTeacherField: Student checked ");
        }
        else
        {
            mProfession.setVisibility(View.VISIBLE);
            Log.d(TAG, "CheckTeacherField: Teacher checked ");
        }
    }
    public void CheckType(){
        if(mTypeRadioStudent.isChecked()) {
            mType = "Student";
            mProfession.setVisibility(View.GONE);
        }
        else{
            mType = "Teacher";
            mProfession.setVisibility(View.VISIBLE);
        }
    }
    public void CheckFieldToShow(){
        mTypeRadioStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mTypeRadioStudent.isChecked()){
                    mProfession.setVisibility(View.GONE);
                    mTextViewTeacherLearn.setVisibility(View.GONE);
                }
                else {
                    mProfession.setVisibility(View.VISIBLE);
                    mTextViewTeacherLearn.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("IsraelCities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public void getStringArrayJson(){
        try {
            //jsonObj =new JSONObject(json_string);
            jsonArray= new JSONArray(json_string);
           // jsonArray =jsonObj.getJSONArray("");
            String city;
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jObj = jsonArray.getJSONObject(i);
                city= jObj.getString("name");
                CityList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, CityList);
        spinner.setAdapter(adapter);
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
    public interface OnFirstFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFirstFragmentInteraction(Uri uri);
        void addUser(String Email,String firstName, String lastName, String Phone,String City, String Type,ArrayList<String> Professions,Boolean Confirm);
        String getEmail();
    }

}
