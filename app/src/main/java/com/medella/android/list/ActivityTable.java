package com.medella.android.list;

/**
 * Represents an item in an ActivityTable list
 */
public class ActivityTable {

    @com.google.gson.annotations.SerializedName("activity_title")
    private String mActivityTitle;

    @com.google.gson.annotations.SerializedName("pain_intensity")
    private float mPainIntensity;

    @com.google.gson.annotations.SerializedName("bodytemperature_celsius")
    private float mBodyTemperatureCelsius;

    @com.google.gson.annotations.SerializedName("bodytemperature_fahrenheit")
    private float mBodyTemperatureFahrenheit;

    @com.google.gson.annotations.SerializedName("activity_description")
    private String mDescription;

    @com.google.gson.annotations.SerializedName("weight_lbs")
    private float mWeightLbs;

    @com.google.gson.annotations.SerializedName("weight_kg")
    private float mWeightKg;

    @com.google.gson.annotations.SerializedName("medication_brand")
    private String mMedicationBrand;

    @com.google.gson.annotations.SerializedName("medication_dosage")
    private String mMedicationDosage;

    @com.google.gson.annotations.SerializedName("systolic")
    private float mSystolic;

    @com.google.gson.annotations.SerializedName("diastolic")
    private float mDiastolic;

    @com.google.gson.annotations.SerializedName("heart_rate")
    private float mHeartRate;

    @com.google.gson.annotations.SerializedName("bmi")
    private float mBmi;

    @com.google.gson.annotations.SerializedName("activity_location")
    private String mLocation;

    @com.google.gson.annotations.SerializedName("createdAt")
    private String mCreatedAt;

    @com.google.gson.annotations.SerializedName("deleted")
    private boolean mDeleted;

    /**
     * Activity id
     * NEEDS CONFIGURATION AND RESEARCH
     */
    @com.google.gson.annotations.SerializedName("activity_id")
    private float mActivityId;

    /**
     * Profile id
     * NEEDS CONFIGURATION AND RESEARCH
     */
    @com.google.gson.annotations.SerializedName("profile_id")
    private float mProfileId;

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    public ActivityTable() {

    }

    public ActivityTable(String title, float painLevel,
                         float temperatureCelsius, float temperatureFahrenheit,
                         String description, String location,
                         float weightLbs, float weightKg, float bmi,
                         String medBrand, String medDosage,
                         float systolic, float diastolic,
                         float hrate, String id) {
        this.setActivityTitle(title);
        this.setPainIntensity(painLevel);
        this.setBodyTemperatureCelsius(temperatureCelsius);
        this.setBodyTemperatureFahrenheit(temperatureFahrenheit);
        this.setDescription(description);
        this.setLocation(location);
        this.setWeightLbs(weightLbs);
        this.setWeightKg(weightKg);
        this.setBmi(bmi);
        this.setMedicationBrand(medBrand);
        this.setMedicationDosage(medDosage);
        this.setSystolic(systolic);
        this.setDiastolic(diastolic);
        this.setHeartRate(hrate);
        this.setId(id);
    }

    public String getActivityTitle(){
        return mActivityTitle;
    }
    public final void setActivityTitle(String title){
        mActivityTitle = title;
    }
    public float getPainIntensity() { return mPainIntensity; }
    public final void setPainIntensity(float pInt) { mPainIntensity = pInt; }
    public float getBodyTemperatureCelsius(){ return mBodyTemperatureCelsius; }
    public final void setBodyTemperatureCelsius(float bodyTempCelsius){ mBodyTemperatureCelsius = bodyTempCelsius; }
    public float getBodyTemperatureFahrenheit(){ return mBodyTemperatureFahrenheit; }
    public final void setBodyTemperatureFahrenheit(float bodyTempFah){ mBodyTemperatureFahrenheit = bodyTempFah; }
    public String getDescription(){
        return mDescription;
    }
    public final void setDescription(String description){ mDescription = description; }
    public String getLocation() { return mLocation; }
    public final void setLocation(String location) { mLocation = location; }
    public float getWeightLbs(){
        return mWeightLbs;
    }
    public final void setWeightLbs(float weightLbs){
        mWeightLbs = weightLbs;
    }
    public float getWeightKg(){ return mWeightKg; }
    public final void setWeightKg(float weightKg){ mWeightKg = weightKg; }
    public String getMedicationBrand() { return mMedicationBrand; }
    public final void setMedicationBrand(String medBrand){ mMedicationBrand = medBrand; }
    public String getMedicationDosage(){ return mMedicationDosage; }
    public final void setMedicationDosage(String medDosage){ mMedicationDosage = medDosage; }
    public float getSystolic(){ return mSystolic; }
    public final void setSystolic(float systolic){ mSystolic = systolic; }
    public float getDiastolic(){ return mDiastolic; }
    public final void setDiastolic(float diastolic){ mDiastolic = diastolic; }
    //Activity id here
    //Profile id here
    public float getHeartRate(){ return mHeartRate; }
    public final void setHeartRate(float heartRate){ mHeartRate = heartRate; }
    public float getBmi(){ return mBmi; }
    public final void setBmi(float bmi){ mBmi = bmi; }
    public String getId() {
        return mId;
    }
    public final void setId(String id) {
        mId = id;
    }
    public String getCreatedAt(){ return mCreatedAt; }
    public boolean isDeleted(){ return mDeleted; }
    public final void setDelete(boolean deleted){ mDeleted = deleted; }

    @Override
    public boolean equals(Object o) {
        return o instanceof ActivityTable && ((ActivityTable) o).mId == mId;
    }
}