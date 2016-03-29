package com.floo.mpm_survey;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.floo.database.DBHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONY_VAIO on 15-Mar-16.
 * Submit all survey result to server
 */
public class SubmitSurvey extends AsyncTask<String, Void, String> {
    List<JSONObject> listSavedAnswers;
    Context context;
    ProgressDialog progressDialog;
    String activeSurveyorUsername="";
    String activeSurveyorName="";

    public SubmitSurvey(Context context){
        this.context = context;
        listSavedAnswers= new ArrayList<>();
        LocalLoginData localLoginData = new LocalLoginData(context);
        activeSurveyorUsername = localLoginData.getPreference(LocalLoginData.keyActiveSurveyorUsername);
        activeSurveyorName = localLoginData.getPreference(LocalLoginData.keyActiveSurveyorNama);
    }
    public void addDataToUpload(JSONObject savedAnswer){
        if(listSavedAnswers.size()==0)
            listSavedAnswers.add(savedAnswer);
        else{
            boolean exist = false;
            for(JSONObject object:listSavedAnswers){
                try {
                    if(object.getString("SURVEY_ID").equals(savedAnswer.getString("SURVEY_ID")) &&
                            object.getString("RESPONDENCE_ID").equals(savedAnswer.getString("RESPONDENCE_ID"))){
                        exist = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!exist){
                listSavedAnswers.add(savedAnswer);
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Mohon Tunggu",
                "Mengirim Data.....", true);
    }

    @Override
    protected String doInBackground(String... strings) {
        JSONArray dataToPost = new JSONArray();
        String result = "";
        try {
            for(JSONObject savedAnswer:listSavedAnswers) {
                Log.e("post data",savedAnswer.toString());
                JSONObject dataRowToPost = new JSONObject();
                JSONArray jawaban = new JSONArray();

                dataRowToPost.put("id_surveyor", activeSurveyorUsername);
                dataRowToPost.put("id_survey", savedAnswer.getString("SURVEY_ID"));
                dataRowToPost.put("id_responden_temp", savedAnswer.getString("RESPONDENCE_ID"));
                //change nama_responden content to surveyor name
                //dataRowToPost.put("nama_responden", savedAnswer.getString("RESPONDENCE_NAME"));
                //// TODO: 30-Mar-16 check which one is correct
                dataRowToPost.put("nama_responden", activeSurveyorName);
                //or
                //dataRowToPost.put("nama_surveyor", activeSurveyorName);




                JSONArray answer = savedAnswer.getJSONArray("ANSWER");
                for (int i = 0; i < answer.length(); i++) {
                    JSONObject rowJawaban = new JSONObject();
                    JSONObject rowAnswer = answer.getJSONObject(i);
                    int questionType = Integer.parseInt(rowAnswer.getString("JENIS_JAWABAN"));
                    String pertanyaanID = rowAnswer.getString("PERTANYAAN_ID");
                    rowJawaban.put("id_pertanyaan", pertanyaanID);

                    if (questionType == Question.CHOICE) {
                        rowJawaban.put("id_master_option", rowAnswer.getJSONArray("OPTION").getJSONObject(0).getString("OPTION_ID"));
                        rowJawaban.put("answer_text", "");
                        rowJawaban.put("answer_foto", "");
                    }
                    else if (questionType == Question.MULTIPLE) {
                        JSONArray options = rowAnswer.getJSONArray("OPTION");
                        for(int j=0;j<options.length();j++){
                            rowJawaban.put("id_master_option", options.getJSONObject(j).getString("OPTION_ID"));
                            rowJawaban.put("answer_text", "");
                            rowJawaban.put("answer_foto", "");
                        }
                    }
                    else if (questionType == Question.INPUT_NUMBER || questionType == Question.INPUT_TEXT) {
                        rowJawaban.put("id_master_option", "");
                        rowJawaban.put("answer_text", rowAnswer.getJSONArray("OPTION").getJSONObject(0).getString("TEXT"));
                        rowJawaban.put("answer_foto", "");
                    } else if (questionType == Question.UPLOAD) {
                        rowJawaban.put("id_master_option", "");
                        rowJawaban.put("answer_text", "");
                        String imagePath = rowAnswer.getJSONArray("OPTION").getJSONObject(0).getString("TEXT");
                        Uri uri = Uri.parse(imagePath);
                        File imgFile = new File(uri.getPath());
                        if (imgFile.exists()) {
                            Bitmap bitmap = null;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
                                try {
                                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                                    Log.e("image handle", "Decoded successfully for sampleSize " + options.inSampleSize);
                                    break;
                                } catch (OutOfMemoryError outOfMemoryError) {
                                    // If an OutOfMemoryError occurred, we continue with for loop and next inSampleSize value
                                    Log.e("image handle", "outOfMemoryError while reading file for sampleSize " + options.inSampleSize
                                            + " retrying with higher value");
                                }
                            }
                            ByteArrayOutputStream bao = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                            bitmap.recycle();
                            byte[] ba = bao.toByteArray();
                            String base64encode = Base64.encodeToString(ba, Base64.DEFAULT);
                            bao.close();
                            String filenameArray[] = imagePath.split("\\.");
                            String extension = filenameArray[filenameArray.length-1];
                            JSONObject imageJSON = new JSONObject();
                            imageJSON.put("image_byte",base64encode);
                            imageJSON.put("image_ext",extension);

                            String headerKey = DataFetcherTask.getHeaderKey();
                            String fileName = getFileName(headerKey,imageJSON);
                            rowJawaban.put("answer_foto", fileName);
                            //Log.e("upload foto","100%");


                        } else {
                            rowJawaban.put("answer_foto", "");
                        }
                    }
                    jawaban.put(rowJawaban);
                }
                dataRowToPost.put("jawaban", jawaban);
                dataToPost.put(dataRowToPost);
            }

            String headerKey = DataFetcherTask.getHeaderKey();
            result = postJSON(headerKey,dataToPost);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        Log.e("push result", result);
        try {
            JSONObject resultData = new JSONObject(result);
            if(resultData.getString("Status").equalsIgnoreCase("berhasil")){//ketika seluruh data submit berhasil maka di dalam objek keterangan kososng
                DBHandler handler = new DBHandler(context);
                JSONArray respondError = resultData.getJSONArray("RESPOND_ANSWER_ERROR");

                for(int i=0;i<respondError.length();i++){
                    handler.setUploadedResponden(respondError.getJSONObject(i).getString("ID_RESPONDEN_TEMP"));
                }
                Toast.makeText(context,"Upload Berhasil",Toast.LENGTH_LONG).show();
                RespondenceActivity.ra.RefreshList();
            }
            else if(resultData.getString("Status").equalsIgnoreCase("gagal")){
                Toast.makeText(context,resultData.getString("Keterangan"),Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String postJSON(String headerKey,JSONArray dataToServer){
        String result ="";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Data.postUrl);
        httppost.setHeader("Content-type", "application/json");
        httppost.addHeader(Data.header, headerKey);

        StringEntity se = null;
        try {
            JSONObject object = new JSONObject();
            object.put("respond_answer",dataToServer);
            Log.e("post data",object.toString());

            se = new StringEntity(object.toString());
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            result = EntityUtils.toString(response.getEntity());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("pushdata", result);
        return result;
    }

    private String getFileName(String headerKey,JSONObject imageJSON){
        String result ="";
        String fileName="";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Data.uploadImageUrl);
        httppost.setHeader("Content-type", "application/json");
        httppost.addHeader(Data.header, headerKey);

        StringEntity se = null;
        try {
            se = new StringEntity(imageJSON.toString());
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            result = EntityUtils.toString(response.getEntity());

            JSONObject object = new JSONObject(result);
            fileName = object.getString("Data");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("image result", result);
        Log.e("image name", fileName);

        return fileName;
    }


}
