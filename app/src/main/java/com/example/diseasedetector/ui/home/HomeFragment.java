package com.example.diseasedetector.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.diseasedetector.POJOS.ChartData;
import com.example.diseasedetector.POJOS.CovidData;
import com.example.diseasedetector.POJOS.DiseaseData;
import com.example.diseasedetector.POJOS.RootDiseaseData;
import com.example.diseasedetector.R;
import com.example.diseasedetector.adapters.RestAdapter;
import com.example.diseasedetector.placeholder.DiseasePlaceHolder;
import com.example.diseasedetector.utils.Dialog_Get_ImageFragment;
import com.example.diseasedetector.utils.FileUtil;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TooManyListenersException;

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
    private Button predict,classify,covidReport,diseaseReport;
    private CardView covidCard, classifier;
    private TextView covidResult,covidAcc,highDisease;
    private ViewGroup container;
    private PieChart barChart;
    private ProgressBar progressBar;
    private String covidUrl,allUrl;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        this.container = container;
        init();
        return root;
    }

    private void init(){

        diseaseReport = root.findViewById(R.id.diseaseReport);
        covidReport = root.findViewById(R.id.covidReport);
        classify = root.findViewById(R.id.predictDiseases);
        highDisease = root.findViewById(R.id.headText);
        progressBar = root.findViewById(R.id.classProgress);
        classifier = root.findViewById(R.id.classifier);
        barChart = root.findViewById(R.id.pieChart);
        barChart.getLegend().setWordWrapEnabled(true);
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

        diseaseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getAllUrl().isEmpty()){
                    Toast.makeText(view.getContext(), "Not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(getAllUrl())));
            }
        });

        covidReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCovidUrl().isEmpty()){
                    Toast.makeText(view.getContext(), "Not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(getCovidUrl())));
            }
        });

        xray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling image dialog fragment
                covidCard.setVisibility(View.GONE);
                classifier.setVisibility(View.GONE);
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

        classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runDiseaseClassifier();
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
            Snackbar.make(container.getRootView(), "Please Add X ray Image First", Snackbar.LENGTH_SHORT).show();
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
                    setCovidUrl(cd.getUrl() != null ? cd.getUrl() : "");
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

    private void runDiseaseClassifier(){
        if(general == null){
            Snackbar.make(container.getRootView(), "Please Add X ray Image First", Snackbar.LENGTH_SHORT).show();
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
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        DiseasePlaceHolder dp = RestAdapter.createAPI();

        Call<RootDiseaseData> call = dp.diseaseClassifier(body);
        progress.setVisibility(View.VISIBLE);
        classifier.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<RootDiseaseData>() {
            @Override
            public void onResponse(Call<RootDiseaseData> call, Response<RootDiseaseData> response) {
                if(response.isSuccessful()){
                    progress.setVisibility(View.INVISIBLE);
                    RootDiseaseData rd = response.body();
                    setAllUrl(rd.getUrl() != null ? rd.getUrl() : "");
                    setText(rd.getHigestValue(),highDisease);
                    setPieChart(getTopDisease(rd.getPredictions()));
                }else{
                    progress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<RootDiseaseData> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private ChartData[] getTopDisease(DiseaseData diseaseData){
        ChartData[] diseaseList = new ChartData[14];
        diseaseList[0] = new ChartData("Atelectasis",(float)diseaseData.getAtelectasis());
        diseaseList[1] = new ChartData("Atelectasis", (float)diseaseData.getCardiomegaly());
        diseaseList[2] = new ChartData("Consolidatation", (float)diseaseData.getConsolidation());
        diseaseList[3] = new ChartData("Edama",(float)diseaseData.getEdema());
        diseaseList[4] = new ChartData("Effusion", (float)diseaseData.getEffusion());
        diseaseList[5] = new ChartData("Emphysema",(float)diseaseData.getEmphysema());
        diseaseList[6] = new ChartData("Fibrosis",(float)diseaseData.getFibrosis());
        diseaseList[7] = new ChartData("Hernia",(float)diseaseData.getHernia());
        diseaseList[8] = new ChartData("Infiltration",(float)diseaseData.getInfiltration());
        diseaseList[9] = new ChartData("Mass", (float)diseaseData.getMass());
        diseaseList[10] = new ChartData("Nodule", (float)diseaseData.getNodule());
        diseaseList[11] = new ChartData("Plerul_Thickening", (float)diseaseData.getPleural_Thickening());
        diseaseList[12] = new ChartData("Pneumonia", (float)diseaseData.getPneumonia());
        diseaseList[13] = new ChartData("Pneumothorax",(float)diseaseData.getPneumothorax());

        for(int i = 0;i < 14; i++){
            for(int j = i;j < 14;j++){
                if(diseaseList[i].getValueData() < diseaseList[j].getValueData()){
                    ChartData temp = diseaseList[i];
                    diseaseList[i] = diseaseList[j];
                    diseaseList[j] = temp;
                }
            }
        }

        return diseaseList;
    }

    private void setPieChart(ChartData[] diseaselist){

        barChart.setRotationEnabled(true);
        classifier.setVisibility(View.VISIBLE);
        List<PieEntry> diseases = new ArrayList<>();
/*        diseases.add(new PieEntry((float)diseaseData.getAtelectasis(),"Atelectasis"));
        diseases.add(new PieEntry((float)diseaseData.getCardiomegaly(),"Cardiomegaly"));
        diseases.add(new PieEntry((float)diseaseData.getConsolidation(),"Consolidation"));
        diseases.add(new PieEntry((float)diseaseData.getEdema(),"Edema"));
        diseases.add(new PieEntry((float)diseaseData.getEffusion(),"Effusion"));
        diseases.add(new PieEntry((float)diseaseData.getEmphysema(),"Emphysema"));
        diseases.add(new PieEntry((float)diseaseData.getFibrosis(),"Fibrosis"));
        diseases.add(new PieEntry((float)diseaseData.getHernia(),"Hernia"));
        diseases.add(new PieEntry((float)diseaseData.getInfiltration(),"Infiltration"));
        diseases.add(new PieEntry((float)diseaseData.getMass(),"Mass"));
        diseases.add(new PieEntry((float)diseaseData.getNodule(),"Nodule"));
        diseases.add(new PieEntry((float)diseaseData.getPleural_Thickening(),"Pleural_Thickening"));
        diseases.add(new PieEntry((float)diseaseData.getPneumonia(),"Pneumonia"));
        diseases.add(new PieEntry((float)diseaseData.getPneumothorax(),"Pneumothorax"));*/
        for(int i = 0 ;i<14 ;i++){
            diseases.add(new PieEntry((float)diseaselist[i].getValueData(),diseaselist[i].getDiseaseName()));
        }

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#E02323"));//red
        colors.add(Color.parseColor("#3523E0"));//blue
        colors.add(Color.parseColor("#2EF219"));//light green
        colors.add(Color.parseColor("#19E7F2"));//cyan
        colors.add(Color.parseColor("#F219EB"));//pink
        colors.add(Color.parseColor("#F2D419"));//yellow
        colors.add(Color.parseColor("#19F2AB"));//kinda green and blue
        colors.add(Color.parseColor("#102577"));//dark blue
        colors.add(Color.parseColor("#771269"));//dark pink
        colors.add(Color.parseColor("#F1F77B"));//light yellow
        colors.add(Color.parseColor("#A8A2A7"));//grey
        colors.add(Color.parseColor("#FA6D66"));//kinda red
        colors.add(Color.parseColor("#176E1C"));//dark green
        colors.add(Color.parseColor("#53176E"));//prussian blue maybe

        PieDataSet pieDataSet = new PieDataSet(diseases,"Diseases");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(16f);


        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);

        barChart.setData(pieData);
        barChart.setDrawEntryLabels(false);
        barChart.getDescription().setEnabled(false);
        barChart.setCenterText("Diseases");
        barChart.animateX(1500);
        barChart.animateX(1500);
        barChart.invalidate();
        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);
        legend.setEnabled(true);

        List<LegendEntry> list = new ArrayList<>();
        int c = 0;
        for(PieEntry pieEntry : diseases){
            LegendEntry legendEntry = new LegendEntry();
            legendEntry.formColor = colors.get(c++);
            legendEntry.label = pieEntry.getLabel();
            list.add(legendEntry);
        }
        legend.setCustom(list);
    }

    private void setText(String text,TextView textView){
        try {
            textView.setText(text);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getCovidUrl() {
        return covidUrl;
    }

    public void setCovidUrl(String covidUrl) {
        this.covidUrl = covidUrl;
    }

    public String getAllUrl() {
        return allUrl;
    }

    public void setAllUrl(String allUrl) {
        this.allUrl = allUrl;
    }
}