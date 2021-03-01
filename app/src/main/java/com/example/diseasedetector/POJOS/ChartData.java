package com.example.diseasedetector.POJOS;

public class ChartData {
    String diseaseName;
    float valueData;

    public ChartData(String diseaseName, float valueData) {
        this.diseaseName = diseaseName;
        this.valueData = valueData;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public float getValueData() {
        return valueData;
    }

    public void setValueData(float valueData) {
        this.valueData = valueData;
    }
}
