package com.floo.mpm_survey;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.floo.database.DBHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dimas on 2/17/2016.
 */
public class DataFetcherTask extends AsyncTask<Void,Void,String> {

    public AsyncResponse delegate = null;
    Context context;
    DBHandler handler;
    String activeSurveyorUsername="";

    public DataFetcherTask(Context context){
        this.context = context;
        handler = new DBHandler(context);
        LocalLoginData localLoginData = new LocalLoginData(context);
        activeSurveyorUsername = localLoginData.getPreference(LocalLoginData.keyActiveSurveyorUsername);
    }

    @Override
    protected void onPostExecute(String result) {
        if(delegate!=null)
            delegate.processFinish(result);
    }

    public static String getHeaderKey(){
        String headerKey="";
        String dat = "";
        DefaultHttpClient httpC = new DefaultHttpClient();
        HttpGet http = new HttpGet(Data.logUrl);
        try{
            HttpResponse htpr= httpC.execute(http);
            HttpEntity httpE = htpr.getEntity();
            dat = EntityUtils.toString(httpE);
            Log.e("response", dat);
        }catch (ClientProtocolException e){
            e.printStackTrace();
            return null;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        try{
            JSONObject jsonObject2 = new JSONObject(dat);
            headerKey = jsonObject2.getString("Data");
        }catch (Exception e){
            e.printStackTrace();
        }

        return headerKey;
    }

    @Override
    protected String doInBackground(Void... params) {


        String headerKey = getHeaderKey();
        String serverData = "";// String object to store fetched data from server
        // Http Request Code start
        Log.e("username",activeSurveyorUsername);
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(Data.dataUrl+activeSurveyorUsername);
        httpGet.setHeader(Data.header, headerKey);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            serverData = EntityUtils.toString(httpEntity);
            Log.e("response", serverData);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Http Request Code end
        // Json Parsing Code Start
        try {
            Log.e("response",serverData);
            JSONObject jsonObject = new JSONObject(serverData);
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObjectCity = jsonArray.getJSONObject(i);
                String surveyid = jsonObjectCity.getString("SURVEY_ID");
                String surveyname = jsonObjectCity.getString("SURVEY_NAMA");
                String dateaktif = jsonObjectCity.getString("TANGGAL_AKTIF");
                String divisi = jsonObjectCity.getString("DIVISI");
                String isaktif = jsonObjectCity.getString("IS_AKTIF");
                String jenisresponc = jsonObjectCity.getString("JENIS_RESPONDEN");
                String lastmofied = jsonObjectCity.getString("LAST_MODIFIED");
                Survey survey = new Survey();
                survey.setSurvey_id(surveyid);
                survey.setSurvey_nama(surveyname);
                survey.setTanggal_aktif(dateaktif);
                survey.setDivisi(divisi);
                survey.setIs_aktif(isaktif);
                survey.setJenis_respondence(jenisresponc);
                survey.setLast_modified(lastmofied);
                handler.addSurvey(survey);// Inserting into DB

                JSONArray respon1 = jsonObjectCity.getJSONArray("PERTANYAAN");

                for (int a=0;a<respon1.length();a++){
                    JSONObject objektanya = respon1.getJSONObject(a);
                    String idtanya = objektanya.getString("PERTANYAAN_ID");
                    String tanya = objektanya.getString("PERTANYAAN");
                    String jenisjwb = objektanya.getString("JENIS_JAWABAN");
                    String urutan = objektanya.getString("URUTAN");

                    Question question = new Question();
                    question.setPertanyaan_id(idtanya);
                    question.setPertanyaan(tanya);
                    question.setJenis_jawaban(jenisjwb);
                    question.setUrutan(urutan);
                    question.setSurvey_id(surveyid);
                    handler.addQuestion(question);// Inserting into DB

                    JSONArray options = objektanya.getJSONArray("OPTION");
                    //Log.e("options",options.toString());
                    for(int j =0;j<options.length();j++){
                        JSONObject optionObj = options.getJSONObject(j);
                        if(!optionObj.getString("OPTION_ID").equals("")) {
                            //Log.e("obj",optionObj.toString());
                            Option option = new Option();
                            option.setOPTION_ID(optionObj.getString("OPTION_ID"));
                            option.setBOBOT(optionObj.getString("BOBOT"));
                            option.setTEXT(optionObj.getString("TEXT"));
                            option.setURUTAN(optionObj.getString("URUTAN"));
                            option.setPERTANYAAN_ID(idtanya);
                            handler.addOption(option);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return "executed";
    }
}
