package com.example.user.teachme;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;
    private DatabaseReference mDatabase,mDatabaseUsers;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTeacher;
        Button btnFav;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTeacher = (TextView) itemView.findViewById(R.id.textViewTeacherCard);
            this.btnFav = (Button) itemView.findViewById(R.id.buttonTeacherCard);
        }
    }

    public CustomAdapter(ArrayList<DataModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardteachers, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        TextView textViewTeacher = holder.textViewTeacher;
        TextView btnFav = holder.btnFav;
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), dataSet.get(listPosition).getId(), Toast.LENGTH_SHORT).show();

                FirebaseDatabase.getInstance().getReference("users").child(userid).child("profile").child("favoriteTeachers").child(dataSet.get(listPosition).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // TODO: handle the case where the data already exists
                            Toast.makeText(v.getContext(), "Removed From Favorites", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference("users").child(userid).child("profile").child("favoriteTeachers").child(dataSet.get(listPosition).getId()).removeValue();
                        }
                        else {
                            // TODO: handle the case where the data does not yet exist
                            Toast.makeText(v.getContext(), "Great Added to Favorites", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference("users").child(userid).child("profile").child("favoriteTeachers").child(dataSet.get(listPosition).getId()).setValue("");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        textViewTeacher.setText(dataSet.get(listPosition).getTeacher());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}