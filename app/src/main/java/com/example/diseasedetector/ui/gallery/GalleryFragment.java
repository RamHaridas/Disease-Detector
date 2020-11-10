package com.example.diseasedetector.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.diseasedetector.R;
import com.example.diseasedetector.adapters.RestAdapter;
import com.example.diseasedetector.popups.EditApiPopup;

public class GalleryFragment extends Fragment {

    private View root;
    private TextView api_link;
    Button edit;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_gallery, container, false);

        init();
        return root;
    }

    private void init(){
        edit = root.findViewById(R.id.change_url);
        api_link = root.findViewById(R.id.api_link);
        String url = null;
        url = RestAdapter.url;
        if(url != null || !url.isEmpty()){
            api_link.setText(url);
        }
        listeners();
    }

    private void listeners(){
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditApiPopup ep = new EditApiPopup(GalleryFragment.this);
                ep.show(getActivity().getSupportFragmentManager(),"edit");
            }
        });
    }

    public void setApiLink(String link){
        api_link.setText(link);
    }
}