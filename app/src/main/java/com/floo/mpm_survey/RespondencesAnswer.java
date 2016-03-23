package com.floo.mpm_survey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dimas on 2/20/2016.
 */
public class RespondencesAnswer {
    SharedPreferences answerData;
    Context context;


    public RespondencesAnswer(Context context){
        this.context = context.getApplicationContext();
        this.answerData = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     *
     * @param surveyID is the survey ID
     * @param respondenceID is the respondence ID
     * @param respondenceName is the respondence name
     * @param answeredQuestion is the list of answered question
     * @param notAnsweredQuestionID is the list of unanswered questions ID
     */
    public void saveAnswerData(String surveyID,String respondenceID,String respondenceName,
                               List<Question>answeredQuestion,List<String>notAnsweredQuestionID){
        String key = surveyID+"**"+respondenceID;
        JSONObject data = new JSONObject();
        try {
            data.put("SURVEY_ID",surveyID);
            data.put("RESPONDENCE_ID",respondenceID);
            data.put("RESPONDENCE_NAME",respondenceName);
            JSONArray answeredArr = new JSONArray();
            for(Question question:answeredQuestion){
                JSONObject item = new JSONObject();
                item.put("PERTANYAAN_ID",question.getPertanyaan_id());
                item.put("JENIS_JAWABAN",question.getJenis_jawaban());
                JSONArray tempArray = new JSONArray();
                for(Option answerdOption:question.getOPTIONS()){
                    JSONObject option = new JSONObject();
                    option.put("TEXT", answerdOption.getTEXT());
                    if(Integer.parseInt(question.getJenis_jawaban())==Question.CHOICE||
                            Integer.parseInt(question.getJenis_jawaban())==Question.MULTIPLE) {
                        option.put("OPTION_ID", answerdOption.getOPTION_ID());
                        option.put("BOBOT", answerdOption.getBOBOT());
                        option.put("URUTAN", answerdOption.getURUTAN());
                    }
                    else{
                        option.put("OPTION_ID", "");
                        option.put("BOBOT", "");
                        option.put("URUTAN", "");
                    }
                    tempArray.put(option);
                }
                item.put("OPTION",tempArray);
                answeredArr.put(item);
            }
            data.put("ANSWER",answeredArr);

            JSONArray unAnsweredQuestionID = new JSONArray();
            for(String questionID:notAnsweredQuestionID){
                unAnsweredQuestionID.put(questionID);
            }

            data.put("UNANSWERED_QUESTION_ID",unAnsweredQuestionID);

            SharedPreferences.Editor editor = answerData.edit();
            editor.putString(key,data.toString());
            editor.commit();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getAnswerData(String surveyID,String respondenceID){
        JSONObject result;
        String key = surveyID+"**"+respondenceID;
        String data = answerData.getString(key,null);
        try {
            result = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    public void deleteAnswerData(String surveyID,String respondenceID){
        SharedPreferences.Editor editor = answerData.edit();
        String key = surveyID+"**"+respondenceID;
        editor.remove(key);
        editor.commit();

    }
}
