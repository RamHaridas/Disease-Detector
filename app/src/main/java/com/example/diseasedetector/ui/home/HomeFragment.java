package com.example.diseasedetector.ui.home;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.diseasedetector.POJOS.CovidData;
import com.example.diseasedetector.R;
import com.example.diseasedetector.adapters.RestAdapter;
import com.example.diseasedetector.placeholder.DiseasePlaceHolder;
import com.example.diseasedetector.utils.Dialog_Get_ImageFragment;
import com.example.diseasedetector.utils.FileUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements Dialog_Get_ImageFragment.MyDialogCloseListener,Dialog_Get_ImageFragment.onPhotoSelectedListener {

    private LottieAnimationView covid,progress;
    private View root;
    private ImageView xray;
    private Dialog_Get_ImageFragment dg;
    private Uri general,imageuri;
    private Bitmap imagebitmap;
    private Button predict;
    private CardView covidCard;
    private TextView covidResult,covidAcc;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        init();
        return root;
    }

    private void init(){
        covid = root.findViewById(R.id.covidAnim);
        xray = root.findViewById(R.id.xrayImage);
        predict = root.findViewById(R.id.predict);
        covidCard = root.findViewById(R.id.covidCard);
        covidCard.setVisibility(View.INVISIBLE);
        covidResult = root.findViewById(R.id.covidtxt);
        covidAcc = root.findViewById(R.id.covidAcc);
        progress = root.findViewById(R.id.progress);

        allListeners();
    }

    private void allListeners(){
        xray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling image dialog fragment
                dg = new Dialog_Get_ImageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("curr",1);
                dg.setArguments(bundle);
                dg.setTargetFragment(HomeFragment.this,1);
                dg.show(getFragmentManager(),"image");
            }
        });
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runPrediction();
            }
        });
    }

    @Override
    public void getImagePath(Uri imagePath) {
        general = imagePath;
        imagebitmap = null;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        imagebitmap = bitmap;
        general = getImageUri(root.getContext(),bitmap);
    }

    @Override
    public void handleDialogClose(int num) {
        if(general != null){
            xray.setImageURI(general);
        }else{
            Toast.makeText(root.getContext(), "No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    private void runPrediction(){
        if(general == null){
            Toast.makeText(getContext(), "Please Add X ray Image First", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = null;
        //trying to create image file
        try {
            file = FileUtil.from(root.getContext(),general);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(file == null){
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        progress.setVisibility(View.VISIBLE);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        DiseasePlaceHolder dp = RestAdapter.createAPI();
        Call<CovidData> call = dp.checkCovid(body);
        call.enqueue(new Callback<CovidData>() {
            @Override
            public void onResponse(Call<CovidData> call, Response<CovidData> response) {
                if(response.isSuccessful()){
                    progress.setVisibility(View.INVISIBLE);
                    CovidData cd = response.body();
                    covid.setVisibility(View.VISIBLE);
                    covidCard.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeInUp)
                            .duration(2000)
                            .playOn(covidCard);

                    if(cd.getResult().equals("Covid19 Negative")){
                        covid.setAnimation(R.raw.covid_negative);
                        covid.playAnimation();
                    }else{
                        covid.setAnimation(R.raw.covid_positive);
                        covid.playAnimation();
                    }
                    covidResult.setText(cd.getResult());
                    covidAcc.setText(cd.getAccuracy());
                }else{
                    Toast.makeText(getContext(), "Internet not available", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CovidData> call, Throwable t) {
                Toast.makeText(getContext(), "Internet not available", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }
}