package com.example.diseasedetector.placeholder;

import com.example.diseasedetector.POJOS.Check;
import com.example.diseasedetector.POJOS.CovidData;
import com.example.diseasedetector.POJOS.RootDiseaseData;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface DiseasePlaceHolder {

    //declare all methods here

    @GET("detect")
    Call<Check> checkAPI();

    @Multipart
    @PUT("detect")
    Call<CovidData> checkCovid(@Part MultipartBody.Part file);


    @Multipart
    @POST("detect")
    Call<RootDiseaseData> diseaseClassifier(@Part MultipartBody.Part file);

}
