package com.floo.mpm_survey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SONY_VAIO on 16-Mar-16.
 */
public class LocalLoginData {
    private SharedPreferences localLoginData;
    private Context context;
    String key = "localLoginData";
    public static String keyActiveSurveyorUsername="activeSurveyorUsername";
    public static String keyActiveSurveyorPassword="activeSurveyorPassword";
    public static String keyActiveSurveyorBagian="activeSurveyorBagian";
    public static String keyActiveSurveyorNama="activeSurveyorNama";
    public static String keyActiveSurveyorLastModified="activeSurveyorLastModified";


    public LocalLoginData(Context context){
        this.context = context;
        this.localLoginData = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * author: haqiqi
     * this function is to save login data to local storage
     * if not exist it will append and if exist,it only update object
     * @param object is result from server data.get(0)
     */
    public void saveLoginData(JSONObject object){
        JSONArray savedLogin = getLoginData();
        String activeSurveyorUsername="";
        String activeSurveyorPassword="";
        String activeSurveyorBagian="";
        String activeSurveyorNama="";
        String activeSurveyorLastModified="";
        try {
            activeSurveyorUsername = object.getString("SURVEYOR_USERNAME");
            activeSurveyorBagian = object.getString("SURVEYOR_BAGIAN");
            activeSurveyorPassword = object.getString("SURVEYOR_PASSWORD");
            activeSurveyorNama = object.getString("SURVEYOR_NAMA");
            activeSurveyorLastModified = object.getString("LAST_MODIFIED");

            savePreferences(keyActiveSurveyorUsername,activeSurveyorUsername);
            savePreferences(keyActiveSurveyorBagian,activeSurveyorBagian);
            savePreferences(keyActiveSurveyorLastModified,activeSurveyorLastModified);
            savePreferences(keyActiveSurveyorNama,activeSurveyorNama);
            savePreferences(keyActiveSurveyorPassword,activeSurveyorPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(savedLogin!=null){
            try {
                boolean isExist =false;
                for(int i =0;i<savedLogin.length();i++){
                    if(savedLogin.getJSONObject(i).getString("SURVEYOR_USERNAME").equals(object.getString("SURVEYOR_USERNAME"))){
                        isExist = true;
                        savedLogin.put(i,object);
                        break;
                    }
                }
                if(!isExist){
                    savedLogin.put(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            savedLogin = new JSONArray();
            if(object!=null){
                savedLogin.put(object);
            }
        }

        savePreferences(key,savedLogin.toString());
    }

    private void savePreferences(String key,String data){
        SharedPreferences.Editor editor = localLoginData.edit();
        editor.putString(key,data);
        editor.apply();
    }
    public JSONArray getLoginData(){
        JSONArray result = null;
        String data = localLoginData.getString(key,null);
        try {
            if(data!=null)
                result = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String getPreference(String key){
        String data = localLoginData.getString(key,null);

        return data;
    }

    public boolean doLocalLogin(String username,String password) {
        boolean isExist = false;
        JSONArray savedLogin = getLoginData();
        String activeSurveyorUsername="";
        String activeSurveyorPassword="";
        String activeSurveyorBagian="";
        String activeSurveyorNama="";
        String activeSurveyorLastModified="";

        if (savedLogin != null) {
            try {
                for (int i = 0; i < savedLogin.length(); i++) {

                    if (savedLogin.getJSONObject(i).getString("SURVEYOR_USERNAME").toLowerCase().equals(username.toLowerCase()) &&
                            savedLogin.getJSONObject(i).getString("SURVEYOR_PASSWORD").equals(password)) {
                        isExist = true;
                        JSONObject object = savedLogin.getJSONObject(i);
                        activeSurveyorUsername = object.getString("SURVEYOR_USERNAME");
                        activeSurveyorBagian = object.getString("SURVEYOR_BAGIAN");
                        activeSurveyorPassword = object.getString("SURVEYOR_PASSWORD");
                        activeSurveyorNama = object.getString("SURVEYOR_NAMA");
                        activeSurveyorLastModified = object.getString("LAST_MODIFIED");
                        savePreferences(keyActiveSurveyorUsername,activeSurveyorUsername);
                        savePreferences(keyActiveSurveyorBagian,activeSurveyorBagian);
                        savePreferences(keyActiveSurveyorLastModified,activeSurveyorLastModified);
                        savePreferences(keyActiveSurveyorNama,activeSurveyorNama);
                        savePreferences(keyActiveSurveyorPassword,activeSurveyorPassword);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isExist;
    }


}
