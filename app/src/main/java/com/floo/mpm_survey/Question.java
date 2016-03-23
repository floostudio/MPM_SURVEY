package com.floo.mpm_survey;

import java.util.List;

/**
 * Created by Floo on 2/3/2016.
 */
public class Question {

    public static int CHOICE = 1;
    public static int MULTIPLE = 2;
    public static int INPUT_NUMBER= 3;
    public static int INPUT_TEXT = 4;
    public static int UPLOAD = 5;


    private String PERTANYAAN_ID;
    private String PERTANYAAN;
    private String JENIS_JAWABAN;
    private String URUTAN;
    private String SURVEY_ID;
    private List<Option> OPTIONS;
    private boolean answered = true;

    public List<Option> getOPTIONS() {
        return OPTIONS;
    }

    public void setOPTIONS(List<Option> OPTIONS) {
        this.OPTIONS = OPTIONS;
    }

    public Question(String PERTANYAAN_ID, String PERTANYAAN, String JENIS_JAWABAN, String URUTAN, String SURVEY_ID){
        this.PERTANYAAN_ID = PERTANYAAN_ID;
        this.PERTANYAAN = PERTANYAAN;
        this.JENIS_JAWABAN = JENIS_JAWABAN;
        this.URUTAN = URUTAN;
        this.SURVEY_ID = SURVEY_ID;
    }

    public Question() {

    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getPertanyaan_id(){
        return PERTANYAAN_ID;
    }

    public void setPertanyaan_id(String PERTANYAAN_ID){
        this.PERTANYAAN_ID = PERTANYAAN_ID;
    }

    public String getPertanyaan(){
        return PERTANYAAN;
    }

    public void setPertanyaan(String PERTANYAAN){
        this.PERTANYAAN = PERTANYAAN;
    }

    public String getJenis_jawaban(){
        return JENIS_JAWABAN;
    }

    public void setJenis_jawaban(String JENIS_JAWABAN){
        this.JENIS_JAWABAN = JENIS_JAWABAN;
    }

    public String getUrutan(){
        return URUTAN;
    }

    public void setUrutan(String URUTAN){
        this.URUTAN = URUTAN;
    }

    public String getSurvey_id(){
        return SURVEY_ID;
    }

    public void setSurvey_id(String SURVEY_ID){
        this.SURVEY_ID = SURVEY_ID;
    }

}
