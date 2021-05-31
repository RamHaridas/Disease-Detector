package com.example.diseasedetector.popups;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.diseasedetector.R;


public class LogoutPopup extends DialogFragment {

    Button yes,no;
    TextView textView;
    View view;
    Dialog dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.deletepopup, container, false);

        dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        init();
        return view;
    }

    private void init(){
        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);
        listeners();
    }

    private void listeners(){
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
                getDialog().dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
    }
}
