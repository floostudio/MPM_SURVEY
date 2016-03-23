package com.floo.mpm_survey;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by SONY_VAIO on 15-Mar-16.
 * this is universal class for handling get request for vendor login,
 * surveyor login, survey for surveyor
 */
public class Login extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate=null;


    @Override
    protected String doInBackground(String... params) {
        String result = "";

        String headerKey = DataFetcherTask.getHeaderKey();
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 5000);
        HttpConnectionParams.setSoTimeout(myParams, 5000);
        HttpClient httpclient = new DefaultHttpClient(myParams);
        String url = params[0];

        Log.e("url", url);

        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader(Data.header,headerKey);

            HttpResponse response = httpclient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity());
            Log.d("tag", result);


        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "";
        } catch (IOException e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(delegate!=null)
            delegate.processFinish(result);
    }
}
