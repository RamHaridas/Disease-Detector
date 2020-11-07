package com.example.diseasedetector.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.diseasedetector.R;
import com.example.diseasedetector.utils.Dialog_Get_ImageFragment;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class HomeFragment extends Fragment implements Dialog_Get_ImageFragment.MyDialogCloseListener,Dialog_Get_ImageFragment.onPhotoSelectedListener {

    private LottieAnimationView covid;
    private View root;
    private ImageView xray;
    Dialog_Get_ImageFragment dg;
    private Uri general,imageuri;
    Bitmap imagebitmap;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        init();
        return root;
    }

    private void init(){
        covid = root.findViewById(R.id.covidAnim);
        xray = root.findViewById(R.id.xrayImage);

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
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }
}