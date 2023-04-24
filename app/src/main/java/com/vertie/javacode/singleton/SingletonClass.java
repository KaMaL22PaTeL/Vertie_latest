package com.vertie.javacode.singleton;

import com.vertie.javacode.models.QuestionsObj;

import java.util.ArrayList;

public class SingletonClass {

    private static SingletonClass mInstance;
    public String token;
    public String selectedWorkRadius;
    public String userId="";
    public String email="";
    public String mesurmentType="0";
    public ArrayList<QuestionsObj> questionsArrListData =null;
    private SingletonClass() {}
    public String selectedStep4="";
    public String selectedStep5="";
    public String selectedStep7="";
    public static SingletonClass getInstance(){
        if(mInstance == null)
            mInstance = new SingletonClass();
        return mInstance;
    }

}