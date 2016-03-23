package com.floo.mpm_survey;

/**
 * Created by Floo on 2/3/2016.
 */
public class Option {
    private String OPTION_ID;
    private String TEXT;
    private String BOBOT;
    private String URUTAN;
    private String PERTANYAAN_ID;





    public Option(){

    }

    public Option(String OPTION_ID, String TEXT, String BOBOT, String URUTAN,String PERTANYAAN_ID){
        this.OPTION_ID = OPTION_ID;
        this.TEXT = TEXT;
        this.BOBOT = BOBOT;
        this.URUTAN = URUTAN;
        this.PERTANYAAN_ID = PERTANYAAN_ID;
    }

    public String getOPTION_ID(){
        return OPTION_ID;
    }

    public void setOPTION_ID(String OPTION_ID){
        this.OPTION_ID = OPTION_ID;
    }

    public String getTEXT(){
        return TEXT;
    }

    public void setTEXT(String TEXT){
        this.TEXT = TEXT;
    }

    public String getBOBOT(){
        return BOBOT;
    }

    public void setBOBOT(String BOBOT){
        this.BOBOT = BOBOT;
    }

    public String getURUTAN(){
        return URUTAN;
    }

    public void setURUTAN(String URUTAN){
        this.URUTAN = URUTAN;
    }
    public String getPERTANYAAN_ID() {
        return PERTANYAAN_ID;
    }

    public void setPERTANYAAN_ID(String PERTANYAAN_ID) {
        this.PERTANYAAN_ID = PERTANYAAN_ID;
    }

}
