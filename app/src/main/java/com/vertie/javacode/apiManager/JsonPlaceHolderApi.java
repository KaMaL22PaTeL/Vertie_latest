package com.vertie.javacode.apiManager;


import com.vertie.Constants;
import com.vertie.FamilyMemberData;
import com.vertie.javacode.models.MenualEntryObj;
import com.vertie.javacode.models.UserById;
import com.vertie.javacode.utility.Constant;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {

    @POST(Constants.API_LOGIN)
    Call<Object> login(@Body HashMap<String, String> jsonPost);

    @Headers("Content-Type: application/json")
    @POST(Constants.API_AddMedicalRecords)
    Call<Object> addMedicalRecords(@Body HashMap<String, Object> jsonPost);

    @Headers("Content-Type: application/json")
    @POST(Constants.GET_Medical_Records_By_Date_Type)
    Call<Object> getMedicalRecordsByDateType(@Body HashMap<String, Object> jsonPost);

    @Headers("Content-Type: application/json")
    @POST(Constants.API_AddStepOne)
    Call<Object> saveNumber(@Body HashMap<String, Object> jsonPost);

    @Headers("Content-Type: application/json")
    @POST(Constants.API_AddMedicalRecords)
    Call<Object> addMedicalRecords2(@Body MenualEntryObj jsonPost);

    @POST(Constants.API_GET_ALL_RECORDS)
    Call<Object> reset_password(@Body HashMap<String, String> jsonPost);

    @POST(Constants.API_ForgotPassword)
    Call<Object> forgotpassword(@Body HashMap<String, String> jsonPost);

    @GET(Constants.API_GetUserById+"{id}")
    Call<Object> getUserById(@Path("id")String id);

    @GET(Constants.API_GetUserById+"{id}")
    Call<Object> getUserById2(@Path("id")String id);

    @GET(Constants.API_GetFamilyMemberById+"{id}")
    Call<FamilyMemberData> getFamilyMemberById(@Path("id")String id);

}