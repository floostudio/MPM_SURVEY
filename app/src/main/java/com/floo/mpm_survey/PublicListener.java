package com.floo.mpm_survey;

import java.util.ArrayList;

/**
 * Created by PP on 6/14/2015.
 */
public interface PublicListener {

    /*----------S U R V E Y----------*/
    public void addSurvey(Survey survey);
    public ArrayList<Survey> getAllSurvey();
    public int getSurveyCount();

    /*----------P E R T A N Y A A N----------*/
    public void addQuestion(Question question);
    public ArrayList<Question> getAllQuestion(String id_survey);
    public int getQuestionCount(String id_survey);

    /*----------R E S P O N D E N----------*/
    public ArrayList<Responden> getAllResponden(String id_survey);
    public void getDeleteResponden(String id_respondence);
    public void getLockResponden(String id_respondence);
    public void getUnlockResponden(String id_respondence);

}
