package com.floo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.floo.database.DBHandler;

import com.floo.mpm_survey.AsyncResponse;
import com.floo.mpm_survey.Data;
import com.floo.mpm_survey.DataFetcherTask;
import com.floo.mpm_survey.MainActivity;
import com.floo.mpm_survey.NetworkUtils;
import com.floo.mpm_survey.R;
import com.floo.mpm_survey.RespondenceActivity;
import com.floo.mpm_survey.Survey;
import com.floo.mpm_survey.Survey_Adapter;

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

public class Fragment_Home extends Fragment implements AsyncResponse {
    ListView listSurvey;
    Survey_Adapter adapter;
    DBHandler handler;
    DataFetcherTask dataFetcherTask;

    Context context;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity().getApplicationContext();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Survey");

        listSurvey = (ListView) v.findViewById(R.id.list_isi);
        dataFetcherTask = new DataFetcherTask(context);
        dataFetcherTask.delegate = this;

        listSurvey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ab=new Intent(context, RespondenceActivity.class);
                String idSurvey = ((TextView) view.findViewById(R.id.txt_IDPARSING)).getText().toString();
                ab.putExtra("SURVEY_ID", idSurvey);
                startActivity(ab);


            }
        });

        handler = new DBHandler(context);
        NetworkUtils utils = new NetworkUtils(context);
        if(handler.getSurveyCount() == 0 && utils.isConnectingToInternet())
        {
            dataFetcherTask.execute();
            progressDialog = ProgressDialog.show(getActivity(), "Mohon Tunggu",
                    "Inisialisasi Data.....", true);
        }
        else
        {
            /*
            ArrayList<Survey> surveyList = handler.getAllSurvey();

            adapter = new Survey_Adapter(context,surveyList);
            listSurvey.setAdapter(adapter);
            */
            setListSurvey();
        }
        return v;
    }

    @Override
    public void processFinish(String output) {

        if(output!=null){
            /*
            ArrayList<Survey> surveyList = handler.getAllSurvey();
            adapter = new Survey_Adapter(context,surveyList);
            listSurvey.setAdapter(adapter);
            */
            setListSurvey();
        }
        else{
            Toast.makeText(context,"Ooopppss terjadi kesalahan",Toast.LENGTH_LONG).show();
        }
        progressDialog.dismiss();
    }

    private void setListSurvey(){
        ArrayList<Survey> surveyList = handler.getAllSurvey();
        for(Survey survey:surveyList){
            survey.setUploadedCount(handler.getUploadedRespondenCount(survey.getSurvey_id()));
            survey.setTotalRespondenCount(handler.getRespondenCount(survey.getSurvey_id()));
        }
        adapter = new Survey_Adapter(context,surveyList);
        listSurvey.setAdapter(adapter);

    }
}
