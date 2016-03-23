package com.floo.mpm_survey;

/**
 * Created by Floo on 2/10/2016.
 */
public class Responden {
    private String ID_RESPONDENCE;
    private String NAMA_RESPONDENCE;
    private boolean LOCKED;
    private String CRETAED_AT;
    private String LAST_MODIFIED;
    private String LATITUDE;
    private String LONGITUDE;
    private boolean FINAL;
    private boolean UPLOADED;

    public Responden(){

    }

    public Responden(String ID_RESPONDENCE, String NAMA_RESPONDENCE, boolean LOCKED, String CRETAED_AT,
                     String LAST_MODIFIED, String LATITUDE, String LONGITUDE,boolean FINAL,boolean UPLOADED){
        this.ID_RESPONDENCE = ID_RESPONDENCE;
        this.NAMA_RESPONDENCE = NAMA_RESPONDENCE;
        this.LOCKED = LOCKED;
        this.CRETAED_AT = CRETAED_AT;
        this.LAST_MODIFIED = LAST_MODIFIED;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
        this.FINAL = FINAL;
        this.UPLOADED = UPLOADED;
    }
    public Responden(String NAMA_RESPONDENCE, boolean LOCKED, String CRETAED_AT, String LAST_MODIFIED,
                     String LATITUDE, String LONGITUDE,boolean FINAL,boolean UPLOADED){
        this.NAMA_RESPONDENCE = NAMA_RESPONDENCE;
        this.LOCKED = LOCKED;
        this.CRETAED_AT = CRETAED_AT;
        this.LAST_MODIFIED = LAST_MODIFIED;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
        this.FINAL = FINAL;
        this.UPLOADED = UPLOADED;
    }


    public String getID_RESPONDENCE(){
        return ID_RESPONDENCE;
    }

    public void setID_RESPONDENCE(String ID_RESPONDENCE){
        this.ID_RESPONDENCE = ID_RESPONDENCE;
    }

    public String getNAMA_RESPONDENCE(){
        return NAMA_RESPONDENCE;
    }

    public void setNAMA_RESPONDENCE(String NAMA_RESPONDENCE){
        this.NAMA_RESPONDENCE = NAMA_RESPONDENCE;
    }

    public boolean isLOCKED() {
        return LOCKED;
    }

    public void setLOCKED(boolean LOCKED) {
        this.LOCKED = LOCKED;
    }

    public boolean isUPLOADED() {
        return UPLOADED;
    }

    public void setUPLOADED(boolean UPLOADED) {
        this.UPLOADED = UPLOADED;
    }

    public boolean isFINAL() {
        return FINAL;
    }

    public void setFINAL(boolean FINAL) {
        this.FINAL = FINAL;
    }

    public String getCRETAED_AT(){
        return CRETAED_AT;
    }

    public void setCRETAED_AT(String CRETAED_AT){
        this.CRETAED_AT = CRETAED_AT;
    }

    public String getLAST_MODIFIED(){
        return LAST_MODIFIED;
    }

    public void setLAST_MODIFIED(String LAST_MODIFIED){
        this.LAST_MODIFIED = LAST_MODIFIED;
    }

    public String getLATITUDE(){
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE){
        this.LATITUDE = LATITUDE;
    }

    public String getLONGITUDE(){
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE){
        this.LONGITUDE = LONGITUDE;
    }
}
