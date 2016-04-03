package com.floo.mpm_survey;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.floo.database.DBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class QuestionActivity extends AppCompatActivity {
    static  String LIST_INSTANCE_STATE = "SAVED_LIST_INSTANCE";
    TextView txtName;
    DBHandler handler;
    Button btnSave, btnCancel;
    JSONObject savedAnswer;
    static long respondenceID;
    static String idSurvey;
    String strLatitude, strLongtitude, respondenceName;
    boolean isEditing = false;
    RespondencesAnswer respondencesAnswer;
    List<String> unAnsweredQuestionID;
    List<Question> answeredQuestion;

    public static int CHOOSE_IMAGE_CODE = 988;

    //PERTANYAAN
    ArrayList<Question> questionList;
    QuestionListAdapter adapter;
    /*TextView txt_no1, txt_no2, txt_no3, txt_no4, txt_no5, txt_no6;
    TextView txt_question1, txt_question2, txt_question3, txt_question4, txt_question5, txt_question6;
*/
    ListView listPertanyaan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_survey_detail);
        if(getIntent().getExtras()!=null) {
            Intent a = getIntent();
            idSurvey = a.getStringExtra("SURVEY_ID");
            isEditing = a.getBooleanExtra("isEditing",false);
            if(isEditing)
                respondenceID = Long.parseLong(a.getStringExtra("RESPONDENCE_ID"));
        }
        /*initView();*/

        respondencesAnswer = new RespondencesAnswer(QuestionActivity.this);
        listPertanyaan = (ListView) findViewById(R.id.list_pertanyaan);
        txtName = (TextView) findViewById(R.id.textViewNama);
        btnSave = (Button) findViewById(R.id.btn_Save);
        btnCancel = (Button) findViewById(R.id.btn_Cancel);
        unAnsweredQuestionID = new ArrayList<>();
        answeredQuestion = new ArrayList<>();

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(!isEditing) {
            showInputUser();
            btnSave.setText("SAVE");
        }
        else{
            btnSave.setText("UPDATE");
            Log.e("param",idSurvey+" resp id "+respondenceID);
            savedAnswer = respondencesAnswer.getAnswerData(idSurvey,respondenceID+"");
            try {
                respondenceName = savedAnswer.getString("RESPONDENCE_NAME");
                txtName.setText("Survey Responden\n"+respondenceName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        handler = new DBHandler(this);
        GPSTracker gpsTracker = new GPSTracker(this);
        strLatitude = String.valueOf(gpsTracker.latitude);
        strLongtitude = String.valueOf(gpsTracker.longitude);

        NetworkUtils utils = new NetworkUtils(QuestionActivity.this);
/*
        if(handler.getQuestionCount(idSurvey) == 0 && utils.isConnectingToInternet())
        {
            new DataPertanyaanTask().execute();
        }
        else
        {
            //ArrayList<Question> tanyaList = handler.getAllQuestion(idSurvey);
            questionList = handler.getAllQuestion(idSurvey);
            adapter = new QuestionListAdapter(this,questionList);
            listPertanyaan.setAdapter(adapter);
            //adapter = new RespondenAdapter(RespondenceActivity.this,tanyaList);
            //lis_page_3.setAdapter(adapter);
        }*/
        questionList = handler.getAllQuestion(idSurvey);
        for(Question question:questionList){
            question.setOPTIONS(handler.getOptionsByPertanyaanID(question.getPertanyaan_id()));
            /*Log.e("q","question "+question.getPertanyaan_id() + " "+question.getPertanyaan());
            for(Option opt:question.getOPTIONS()){
                Log.e("opt","option "+opt.getOPTION_ID()+ " "+opt.getTEXT()+" "+opt.getPERTANYAAN_ID());
            }*/
        }
        if (savedInstanceState != null
                && savedInstanceState.containsKey(LIST_INSTANCE_STATE)) {
            listPertanyaan.onRestoreInstanceState(savedInstanceState
                    .getParcelable(LIST_INSTANCE_STATE));
            adapter = (QuestionListAdapter)listPertanyaan.getAdapter();
        }else {
            if (isEditing){
                try {
                    JSONArray unAnswered = savedAnswer.getJSONArray("UNANSWERED_QUESTION_ID");
                    for(Question question:questionList){
                        for(int i=0;i<unAnswered.length();i++){
                            if(unAnswered.getString(i).equals(question.getPertanyaan_id())){
                                question.setAnswered(false);
                                break;
                            }
                        }
                    }
                    adapter = new QuestionListAdapter(this, questionList, isEditing, savedAnswer.getJSONArray("ANSWER"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                adapter = new QuestionListAdapter(this, questionList, isEditing, new JSONArray());
            }
            listPertanyaan.setAdapter(adapter);
            listPertanyaan.smoothScrollToPosition(0);
        }
        /*listPertanyaan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Question item = (Question)listPertanyaan.getItemAtPosition(i);
                Log.e("q","question "+item.getPertanyaan_id() + " "+item.getPertanyaan());
                for(Option opt:item.getOPTIONS()){
                    Log.e("opt","option "+opt.getOPTION_ID()+ " "+opt.getTEXT()+" "+opt.getPERTANYAAN_ID());
                }
            }
        });*/

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Option> surveyResult = adapter.getSurveyAnswer();
                String title;
                String message;
                if(!isEditing) {
                    title = "Konfirmasi";


                    for (Question question : questionList) {
                        List<Option> options = searchResultAnswerBy(question.getPertanyaan_id(), surveyResult);
                        if (options.size() > 0) {
                            Question temp = new Question(question.getPertanyaan_id(), question.getPertanyaan(), question.getJenis_jawaban(), question.getUrutan(), idSurvey);
                            temp.setOPTIONS(options);
                            answeredQuestion.add(temp);
                        } else {
                            unAnsweredQuestionID.add(question.getPertanyaan_id());
                        }
                    }

                    if(unAnsweredQuestionID.size()>0) {
                        message = "Hey beberapa pertanyaan belum terisi. Namun, Anda dapat mengubahnya kembali.\n" +
                                "Simpan?";
                        saveConfirmation(title,message);
                    }
                    else{
                        message = "Simpan?";
                        saveConfirmation(title,message);
                    }
                }
                else{
                    title = "Konfirmasi";
                    for (Question question : questionList) {
                        List<Option> options = searchResultAnswerBy(question.getPertanyaan_id(), surveyResult);
                        if (options.size() > 0) {
                            Question temp = new Question(question.getPertanyaan_id(), question.getPertanyaan(), question.getJenis_jawaban(), question.getUrutan(), idSurvey);
                            temp.setOPTIONS(options);
                            answeredQuestion.add(temp);
                            //Log.e("answered id",temp.getPertanyaan_id());
                        } else {
                            unAnsweredQuestionID.add(question.getPertanyaan_id());
                            //Log.e("un answered id", question.getPertanyaan_id());
                        }
                    }
                    if(unAnsweredQuestionID.size()>0) {
                        message = "Hey beberapa pertanyaan belum terisi. Namun, Anda dapat mengubahnya kembali.\nSimpan?";
                        updateConfirmation(title,message);
                    }
                    else{
                        message="Apakah Anda yakin untuk menyimpannya?";
                        updateConfirmation(title,message);
                    }

                }
                //Log.e("saved text", respondencesAnswer.getAnswerData(idSurvey, respondenceID + "").toString());



            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitConfirmation();
            }
        });
    }

    private void exitConfirmation(){
        new AlertDialog.Builder(QuestionActivity.this)
                .setIcon(R.mipmap.icon)
                .setCancelable(false)
                .setTitle("Konfrmasi")
                .setMessage("Hey, Semua perubahan yang terjadi tidak akan tersimpan.\nApakah Anda yakin untuk keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RespondenceActivity.ra.RefreshList();
                        finish();
                    }

                })
                .setNegativeButton("Tidak", null)
                .show();

    }
    private void saveConfirmation(String title,String message){
        new AlertDialog.Builder(QuestionActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Responden respondence = new Responden(respondenceName, false, getDateTime(), getDateTime(), strLatitude, strLongtitude,false,false);
                        respondenceID = handler.addResponden(respondence, idSurvey);
                        respondencesAnswer.saveAnswerData(idSurvey, respondenceID + "", respondenceName, answeredQuestion, unAnsweredQuestionID);
                        dialog.dismiss();
                        RespondenceActivity.ra.RefreshList();
                        finish();

                    }

                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unAnsweredQuestionID.clear();
                        answeredQuestion.clear();
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Ya & Final", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Responden respondence = new Responden(respondenceName, false, getDateTime(), getDateTime(), strLatitude, strLongtitude,true,false);
                        respondenceID = handler.addResponden(respondence, idSurvey);
                        respondencesAnswer.saveAnswerData(idSurvey, respondenceID + "", respondenceName, answeredQuestion, unAnsweredQuestionID);
                        dialog.dismiss();
                        RespondenceActivity.ra.RefreshList();
                        finish();
                    }
                })
                .show();
    }
    private void updateConfirmation(String title,String message){
        new AlertDialog.Builder(QuestionActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.updateRespondenceLastModified(respondenceID+"",getDateTime(),strLatitude,strLongtitude);
                        respondencesAnswer.saveAnswerData(idSurvey, respondenceID + "", respondenceName, answeredQuestion, unAnsweredQuestionID);
                        dialog.dismiss();
                        unAnsweredQuestionID.clear();
                        answeredQuestion.clear();
                        RespondenceActivity.ra.RefreshList();
                        finish();
                    }

                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener(){


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        unAnsweredQuestionID.clear();
                        answeredQuestion.clear();
                    }
                })
                .setNeutralButton("Ya & Final", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.setFinalResponden(respondenceID+"",true);
                        handler.updateRespondenceLastModified(respondenceID+"",getDateTime(),strLatitude,strLongtitude);
                        respondencesAnswer.saveAnswerData(idSurvey, respondenceID + "", respondenceName, answeredQuestion, unAnsweredQuestionID);
                        dialog.dismiss();
                        unAnsweredQuestionID.clear();
                        answeredQuestion.clear();
                        RespondenceActivity.ra.RefreshList();
                        finish();
                    }
                })
                .show();

    }

    private List<Option> searchResultAnswerBy(String pertanyaanID,HashMap<String,Option> surveyResult){
        List<Option> options = new ArrayList<>();

        for (Object obj : surveyResult.entrySet()) {
            HashMap.Entry pair = (HashMap.Entry) obj;
            String splitSign = Pattern.quote("**");
            String key = (String) pair.getKey();//the key raw format is question ID**X if x is number then it's multiple answer
            Option answer = (Option) pair.getValue();
            String questionID  = key.split(splitSign)[0];

            if(pertanyaanID.equals(questionID)){
                options.add(answer);
            }
        }

        return  options;
    }

    @Override
    public void onBackPressed() {
       exitConfirmation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(adapter!=null) {
            adapter.onActivityResult(requestCode, resultCode, data);
        }
        else{
            Toast.makeText(this,"Oooppsss telah terjadi sesuatu, mohon coba lagi",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_INSTANCE_STATE, listPertanyaan.onSaveInstanceState());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listPertanyaan.onRestoreInstanceState(savedInstanceState.getParcelable(LIST_INSTANCE_STATE));
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_survey, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
               exitConfirmation();
                return true;
            case R.id.option_cek:
                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("Hasil..")
                        //.setMessage("Hey some question haven't filled yet. But you can edit it later.\nDo you want to save anyway?")
                        .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                unAnsweredQuestionID.clear();
                                answeredQuestion.clear();
                                dialog.dismiss();
                            }

                        });
                // your action goes here
                HashMap<String,Option> surveyResult = adapter.getSurveyAnswer();
                for (Question question : questionList) {
                    List<Option> options = searchResultAnswerBy(question.getPertanyaan_id(), surveyResult);
                    if (options.size() > 0) {
                        Question temp = new Question(question.getPertanyaan_id(), question.getPertanyaan(), question.getJenis_jawaban(), question.getUrutan(), idSurvey);
                        temp.setOPTIONS(options);
                        answeredQuestion.add(temp);
                        //Log.e("answered id",temp.getPertanyaan_id());
                    } else {
                        unAnsweredQuestionID.add(question.getPertanyaan_id());
                        //Log.e("un answered id", question.getPertanyaan_id());
                    }
                }
                if(unAnsweredQuestionID.size()>0) {
                    List<Question> questions= adapter.getQuestionList();
                    for(Question question:questions){
                        for(String questionID:unAnsweredQuestionID){
                            if(question.getPertanyaan_id().equals(questionID)){
                                question.setAnswered(false);
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    builder.setMessage("Ada beberapa pertanyaan yang belum terisi");
                    builder.show();
                }
                else{

                    builder.setMessage("Semua pertanyaan telah terisi");
                    builder.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInputUser() {
        LayoutInflater mInflater = LayoutInflater.from(QuestionActivity.this);
        View v = mInflater.inflate(R.layout.activity_masukkannama, null);

        final AlertDialog dialog = new AlertDialog.Builder(QuestionActivity.this).create();

        dialog.setView(v);
        dialog.setTitle("Ketikkan Nama");
        //dialog.setIcon(R.mipmap.icon);
        dialog.setCancelable(false);

        final Button btnOk = (Button) v.findViewById(R.id.buttonOK);
        final EditText inputUser = (EditText) v.findViewById(R.id.editTextNama);

        btnOk.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(inputUser.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Masukkan nama responden", Toast.LENGTH_LONG).show();
                }else{
                    respondenceName = inputUser.getText().toString();
                    txtName.setText("Survey Responden\n"+respondenceName);
                    dialog.dismiss();
                    //RefreshQuestion();
                }
            }
        });
        dialog.show();
    }


}


