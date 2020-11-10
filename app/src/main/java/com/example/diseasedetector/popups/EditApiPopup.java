package com.example.diseasedetector.popups;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.diseasedetector.R;
import com.example.diseasedetector.adapters.RestAdapter;
import com.example.diseasedetector.ui.gallery.GalleryFragment;
import com.google.android.material.textfield.TextInputEditText;

public class EditApiPopup extends DialogFragment {

    View view;
    TextInputEditText api;
    Button save;
    SharedPreferences data;
    SharedPreferences.Editor editor;
    GalleryFragment galleryFragment;

    public EditApiPopup(GalleryFragment galleryFragment) {
        this.galleryFragment = galleryFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.edit_api_popup, container, false);

        init();

        return view;
    }

    private void init(){
        api = view.findViewById(R.id.api_url_et);
        save = view.findViewById(R.id.save_url);
        data = this.getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = data.edit();
        listeners();
    }

    private void listeners(){

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(api.getText().toString().isEmpty()){
                    YoYo.with(Techniques.Shake)
                            .duration(2000)
                            .playOn(api);
                    api.setError("Cannot be Empty");
                    return;
                }
                RestAdapter.setUrl(api.getText().toString().trim());
                editor.putString("url",api.getText().toString().trim());
                editor.apply();
                galleryFragment.setApiLink(api.getText().toString().trim());
                getDialog().dismiss();
            }
        });
    }
}
