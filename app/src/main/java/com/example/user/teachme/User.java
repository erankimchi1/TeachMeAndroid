package com.example.user.teachme;

import java.util.ArrayList;

/**
 * Created by user on 22/01/2018.
 */

public class User {
    public String Email;
    public String firstName;
    public String lastName;
    public String Phone;
    public String City;
    public String Type;
    public Boolean Confirm;
    public ArrayList<String> Profession;
    public double Longitude,Latitude;

    public User()
    {

    }
    public User(String Email,String firstName,String lastName,String Phone,String City,String Type,ArrayList<String> Profession,Boolean Confirm)
    {
        this.Email=Email;
        this.Type=Type;
        this.firstName=firstName;
        this.lastName=lastName;
        this.Phone=Phone;
        this.City=City;
        this.Confirm=Confirm;
        this.Profession=Profession;
    }
    public User(double Longitude,double Latitude)
    {
        this.Longitude=Longitude;
        this.Latitude=Latitude;
    }

    String getEmail(){
        return Email;
    }
    String getFirstName(){
        return firstName;
    }
    String getLastName(){
        return lastName;
    }
    String getPhone(){return Phone;}
    String getType(){return Type;}



}
