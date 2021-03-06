package com.example.diseasedetector.POJOS;

import com.google.gson.annotations.SerializedName;

public class RootDiseaseData {

    public DiseaseData predictions;
    @SerializedName("Higest Value")
    public String higestValue;
    public String url;

    public DiseaseData getPredictions() {
        return predictions;
    }

    public void setPredictions(DiseaseData predictions) {
        this.predictions = predictions;
    }

    public String getHigestValue() {
        return higestValue;
    }

    public void setHigestValue(String higestValue) {
        this.higestValue = higestValue;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
