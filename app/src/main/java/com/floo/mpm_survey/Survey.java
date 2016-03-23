package com.floo.mpm_survey;

import java.io.Serializable;

/**
 * Created by Floo on 2/3/2016.
 */
public class Survey{
    private String SURVEY_ID;
    private String SURVEY_NAMA;
    private String TANGGAL_AKTIF;
    private String DIVISI;
    private String IS_AKTIF;
    private String JENIS_RESPONDEN;
    private String LAST_MODIFIED;
    private int uploadedCount=0;
    private int totalRespondenCount=0;

    public Survey() {

    }

    public Survey(String SURVEY_ID, String SURVEY_NAMA, String TANGGAL_AKTIF, String DIVISI,
                  String IS_AKTIF, String JENIS_RESPONDEN, String LAST_MODIFIED){
        this.SURVEY_ID = SURVEY_ID;
        this.SURVEY_NAMA = SURVEY_NAMA;
        this.TANGGAL_AKTIF = TANGGAL_AKTIF;
        this.DIVISI = DIVISI;
        this.IS_AKTIF = IS_AKTIF;
        this.JENIS_RESPONDEN = JENIS_RESPONDEN;
        this.LAST_MODIFIED = LAST_MODIFIED;
    }

    public int getUploadedCount() {
        return uploadedCount;
    }

    public void setUploadedCount(int uploadedCount) {
        this.uploadedCount = uploadedCount;
    }

    public int getTotalRespondenCount() {
        return totalRespondenCount;
    }

    public void setTotalRespondenCount(int totalRespondenCount) {
        this.totalRespondenCount = totalRespondenCount;
    }

    public String getSurvey_id(){
        return SURVEY_ID;
    }

    public void setSurvey_id(String SURVEY_ID){
        this.SURVEY_ID = SURVEY_ID;
    }

    public String getSurvey_nama(){
        return SURVEY_NAMA;
    }

    public void setSurvey_nama(String SURVEY_NAMA){
        this.SURVEY_NAMA= SURVEY_NAMA;
    }

    public String getTanggal_aktif(){
        return TANGGAL_AKTIF;
    }

    public void setTanggal_aktif(String TANGGAL_AKTIF){
        this.TANGGAL_AKTIF= TANGGAL_AKTIF;
    }

    public String getDivisi(){
        return DIVISI;
    }

    public void setDivisi(String DIVISI){
        this.DIVISI = DIVISI;
    }

    public String getIs_aktif(){
        return IS_AKTIF;
    }

    public void setIs_aktif(String IS_AKTIF){
        this.IS_AKTIF= IS_AKTIF;
    }

    public String getJenis_respondence(){
        return JENIS_RESPONDEN;
    }

    public void setJenis_respondence(String JENIS_RESPONDEN){
        this.JENIS_RESPONDEN = JENIS_RESPONDEN;
    }

    public String getLast_modified(){
        return LAST_MODIFIED;
    }

    public void setLast_modified(String LAST_MODIFIED){
        this.LAST_MODIFIED = LAST_MODIFIED;
    }

}
