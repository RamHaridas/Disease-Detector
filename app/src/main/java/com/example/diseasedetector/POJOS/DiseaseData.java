package com.example.diseasedetector.POJOS;

import com.google.gson.annotations.SerializedName;

public class DiseaseData {
    
    @SerializedName("Atelectasis")
    public double atelectasis;
    @SerializedName("Cardiomegaly")
    public double cardiomegaly;
    @SerializedName("Consolidation")
    public double consolidation;
    @SerializedName("Edema")
    public double edema;
    @SerializedName("Effusion")
    public double effusion;
    @SerializedName("Emphysema")
    public double emphysema;
    @SerializedName("Fibrosis")
    public double fibrosis;
    @SerializedName("Hernia")
    public double hernia;
    @SerializedName("Infiltration")
    public double infiltration;
    @SerializedName("Mass")
    public double mass;
    @SerializedName("Nodule")
    public double nodule;
    @SerializedName("Pleural_Thickening")
    public double pleural_Thickening;
    @SerializedName("Pneumonia")
    public double pneumonia;
    @SerializedName("Pneumothorax")
    public double pneumothorax;

    public double getAtelectasis() {
        return atelectasis;
    }

    public double getCardiomegaly() {
        return cardiomegaly;
    }

    public double getConsolidation() {
        return consolidation;
    }

    public double getEdema() {
        return edema;
    }

    public double getEffusion() {
        return effusion;
    }

    public double getEmphysema() {
        return emphysema;
    }

    public double getFibrosis() {
        return fibrosis;
    }

    public double getHernia() {
        return hernia;
    }

    public double getInfiltration() {
        return infiltration;
    }

    public double getMass() {
        return mass;
    }

    public double getNodule() {
        return nodule;
    }

    public double getPleural_Thickening() {
        return pleural_Thickening;
    }

    public double getPneumonia() {
        return pneumonia;
    }

    public double getPneumothorax() {
        return pneumothorax;
    }
}
