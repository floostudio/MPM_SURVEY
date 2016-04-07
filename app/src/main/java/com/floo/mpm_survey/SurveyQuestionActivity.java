package com.floo.mpm_survey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.floo.database.DBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class SurveyQuestionActivity extends AppCompatActivity {
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
    //ListView listPertanyaan;
    LinearLayout listPertanyaan;
    public static int CHOOSE_IMAGE_CODE = 988;
    //PERTANYAAN
    ArrayList<Question> questionList;
    QuestionListAdapter adapter;

    static Button lastClickedUploadButton;
    static TextView lastTextNeedChange;
    static ImageView lastImageNeedChange;

    HashMap<String, Option> surveyAnswer;

    List<LinearLayout>soalLayoutHolders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_questions);
        if(getIntent().getExtras()!=null) {
            Intent a = getIntent();
            idSurvey = a.getStringExtra("SURVEY_ID");
            isEditing = a.getBooleanExtra("isEditing",false);
            if(isEditing)
                respondenceID = Long.parseLong(a.getStringExtra("RESPONDENCE_ID"));
        }
        /*initView();*/
        surveyAnswer = new HashMap<>();
        soalLayoutHolders = new ArrayList<>();

        respondencesAnswer = new RespondencesAnswer(SurveyQuestionActivity.this);
        //listPertanyaan = (ListView) findViewById(R.id.list_pertanyaan);
        listPertanyaan = (LinearLayout) findViewById(R.id.questionHolder);
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

        NetworkUtils utils = new NetworkUtils(SurveyQuestionActivity.this);
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
        }
        /*if (savedInstanceState != null
                && savedInstanceState.containsKey(LIST_INSTANCE_STATE)) {
            listPertanyaan.onRestoreInstanceState(savedInstanceState
                    .getParcelable(LIST_INSTANCE_STATE));
            adapter = (QuestionListAdapter)listPertanyaan.getAdapter();
        }else {*/
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
                setEditingData(savedAnswer.getJSONArray("ANSWER"));
                generateSurvey();
                //adapter = new QuestionListAdapter(this, questionList, isEditing, savedAnswer.getJSONArray("ANSWER"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            generateSurvey();
            //adapter = new QuestionListAdapter(this, questionList, isEditing, new JSONArray());
        }
        //listPertanyaan.setAdapter(adapter);
        //listPertanyaan.smoothScrollToPosition(0);
        //}

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HashMap<String, Option> surveyResult = adapter.getSurveyAnswer();
                String title;
                String message;
                if(!isEditing) {
                    title = "Konfirmasi";


                    for (Question question : questionList) {
                        List<Option> options = searchResultAnswerBy(question.getPertanyaan_id(), surveyAnswer);
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
                        List<Option> options = searchResultAnswerBy(question.getPertanyaan_id(), surveyAnswer);
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
    private void setEditingData(JSONArray prevAnswer){
        for(int i=0;i<prevAnswer.length();i++)
        {
            try {
                JSONObject obj = prevAnswer.getJSONObject(i);
                String questionID = obj.getString("PERTANYAAN_ID");
                int questionType = Integer.parseInt(obj.getString("JENIS_JAWABAN"));
                JSONArray options = obj.getJSONArray("OPTION");
                if(questionType==Question.CHOICE||questionType==Question.UPLOAD||
                        questionType==Question.INPUT_NUMBER||questionType==Question.INPUT_TEXT){
                    String tag = questionID+"**-";
                    Option option = new Option();
                    option.setTEXT(options.getJSONObject(0).getString("TEXT"));
                    option.setBOBOT(options.getJSONObject(0).getString("BOBOT"));
                    option.setURUTAN(options.getJSONObject(0).getString("URUTAN"));
                    option.setOPTION_ID(options.getJSONObject(0).getString("OPTION_ID"));
                    surveyAnswer.put(tag,option);
                }
                else if(questionType==Question.MULTIPLE) {
                    for (int j = 0; j < options.length(); j++) {
                        Option option = new Option();
                        option.setTEXT(options.getJSONObject(j).getString("TEXT"));
                        option.setBOBOT(options.getJSONObject(j).getString("BOBOT"));
                        option.setURUTAN(options.getJSONObject(j).getString("URUTAN"));
                        option.setOPTION_ID(options.getJSONObject(j).getString("OPTION_ID"));
                        String tag = questionID+"**"+options.getJSONObject(j).getString("OPTION_ID");
                        surveyAnswer.put(tag,option);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void exitConfirmation(){
        new AlertDialog.Builder(SurveyQuestionActivity.this)
                .setIcon(R.mipmap.icon)
                .setCancelable(false)
                .setTitle("Konfirmasi")
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
        new AlertDialog.Builder(SurveyQuestionActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Responden respondence = new Responden(respondenceName, false, getDateTime(), getDateTime(), strLatitude, strLongtitude, false, false);
                        respondenceID = handler.addResponden(respondence, idSurvey);
                        respondencesAnswer.saveAnswerData(idSurvey, respondenceID + "", respondenceName, answeredQuestion, unAnsweredQuestionID);
                        dialog.dismiss();
                        RespondenceActivity.ra.RefreshList();
                        finish();

                    }

                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

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
                        Responden respondence = new Responden(respondenceName, false, getDateTime(), getDateTime(), strLatitude, strLongtitude, true, false);
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
        new AlertDialog.Builder(SurveyQuestionActivity.this)
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
/*
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
    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_INSTANCE_STATE, listPertanyaan.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listPertanyaan.onRestoreInstanceState(savedInstanceState.getParcelable(LIST_INSTANCE_STATE));
    }*/

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    private int dpToPx(int dp){
        Resources r = this.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }
    private List<Option> searchAnswerBy(String questionID, JSONArray prevAnswer){
        List<Option> result = new ArrayList<>();
        for(int i=0;i<prevAnswer.length();i++)
        {
            try {
                JSONObject obj = prevAnswer.getJSONObject(i);
                if(obj.getString("PERTANYAAN_ID").equals(questionID)){
                    JSONArray options = obj.getJSONArray("OPTION");
                    for(int j=0;j<options.length();j++){
                        Option option = new Option();
                        option.setTEXT(options.getJSONObject(j).getString("TEXT"));
                        option.setBOBOT(options.getJSONObject(j).getString("BOBOT"));
                        option.setURUTAN(options.getJSONObject(j).getString("URUTAN"));
                        option.setOPTION_ID(options.getJSONObject(j).getString("OPTION_ID"));
                        result.add(option);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void generateSurvey(){
        LayoutInflater inflater = LayoutInflater.from(this);
        int questionNumber=0;
        for(Question question:questionList){
            int questionType = Integer.parseInt(question.getJenis_jawaban());
            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.component_soal, null, false);
            LinearLayout soalLayout = (LinearLayout)row.findViewById(R.id.soalLayout);
            soalLayoutHolders.add(soalLayout);
            TextView pertanyaan = (TextView)row.findViewById(R.id.textPertanyaan);
            TextView nomorSoal = (TextView)row.findViewById(R.id.nomorSoal);
            pertanyaan.setText(question.getPertanyaan());
            nomorSoal.setText((++questionNumber)+"");
            if(!question.isAnswered())
                soalLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));
            else
                soalLayout.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams soalParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            soalParams.setMargins(dpToPx(21),dpToPx(5),dpToPx(35),dpToPx(2));
            List<Option>options = question.getOPTIONS();


            if(questionType==Question.CHOICE){
                RadioGroup.LayoutParams radioParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                RadioGroup radioGroup = (RadioGroup) inflater.inflate(R.layout.component_radiogroup,null);
                radioParams.setMargins(0, 0, 0, dpToPx(2));
                radioGroup.setId(Integer.parseInt(question.getPertanyaan_id()));
                for (int i = 0; i < options.size(); i++) {
                    RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.component_radiobutton,null);
                    radioButton.setId(Integer.parseInt(options.get(i).getOPTION_ID()));
                    radioButton.setTag(options.get(i));
                    radioButton.setText(options.get(i).getTEXT());
                    radioGroup.addView(radioButton,radioParams);
                }
                radioGroup.setTag(question.getPertanyaan_id() + "**-");
                List<Option>userAnswer = new ArrayList<>();
                if(isEditing) {
                    String initialTag = (String) radioGroup.getTag();
                    String questionID = initialTag.split(Pattern.quote("**"))[0];
                    try {
                        userAnswer = searchAnswerBy(questionID,savedAnswer.getJSONArray("ANSWER"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (userAnswer.size() > 0) {
                        int childCount = radioGroup.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View o = radioGroup.getChildAt(i);
                            if (o instanceof RadioButton) {
                                RadioButton radioButton = (RadioButton) o;
                                Option tagOption = (Option) radioButton.getTag();
                                if (userAnswer.get(0).getOPTION_ID().equals(tagOption.getOPTION_ID())) {
                                    radioButton.setChecked(true);
                                }
                            }
                        }

                    }
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroupChangedListener(radioGroup));
                soalLayout.addView(radioGroup, soalParams);


            }
            else if(questionType==Question.INPUT_NUMBER){
                EditText inputNumber = (EditText)inflater.inflate(R.layout.component_inputnumber,null);
                inputNumber.setId(Integer.parseInt(question.getPertanyaan_id()));
                inputNumber.setPadding(dpToPx(3),dpToPx(3),dpToPx(3),dpToPx(3));
                inputNumber.setTag(question.getPertanyaan_id() + "**-");
                String tag = (String)inputNumber.getTag();
                String questionID = tag.split(Pattern.quote("**"))[0];
                if(isEditing){
                    List<Option>temp = new ArrayList<>();
                    try {
                        temp = searchAnswerBy(questionID,savedAnswer.getJSONArray("ANSWER"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(temp.size()>0){
                        inputNumber.setText(temp.get(0).getTEXT(),TextView.BufferType.EDITABLE);
                    }
                }
                inputNumber.addTextChangedListener(new GenericTextWatcher(inputNumber));
                soalLayout.addView(inputNumber, soalParams);


            }
            else if(questionType==Question.INPUT_TEXT){
                EditText inputText = (EditText)inflater.inflate(R.layout.component_inputtext,null);
                inputText.setId(Integer.parseInt(question.getPertanyaan_id()));
                inputText.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
                inputText.setTag(question.getPertanyaan_id() + "**-");
                String tag = (String)inputText.getTag();
                String questionID = tag.split(Pattern.quote("**"))[0];
                if(isEditing){
                    List<Option>temp = new ArrayList<>();
                    try {
                        temp = searchAnswerBy(questionID,savedAnswer.getJSONArray("ANSWER"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(temp.size()>0){
                        inputText.setText(temp.get(0).getTEXT(),TextView.BufferType.EDITABLE);
                    }
                }
                inputText.addTextChangedListener(new GenericTextWatcher(inputText));
                soalLayout.addView(inputText, soalParams);



            }
            else if(questionType==Question.MULTIPLE){
                for(int i=0;i<options.size();i++){
                    CheckBox checkBox = (CheckBox)inflater.inflate(R.layout.component_checkbox,null);
                    checkBox.setId(Integer.parseInt(options.get(i).getOPTION_ID()));
                    checkBox.setTag(R.id.tagData,options.get(i));
                    checkBox.setText(options.get(i).getTEXT());
                    checkBox.setTag(R.id.tagName,question.getPertanyaan_id()+"**"+options.get(i).getOPTION_ID());
                    //this.view = (CompoundButton)view;
                    List<Option>userAnswer =new ArrayList<>();
                    if(isEditing){
                        String initialTag = (String)checkBox.getTag(R.id.tagName);
                        String questionID = initialTag.split(Pattern.quote("**"))[0];
                        try {
                            userAnswer = searchAnswerBy(questionID,savedAnswer.getJSONArray("ANSWER"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(userAnswer.size()>0)
                        {
                            for(int j =0;j<userAnswer.size();j++){
                                Option tagOption = (Option)checkBox.getTag(R.id.tagData);
                                if (userAnswer.get(j).getOPTION_ID().equals(tagOption.getOPTION_ID())) {
                                    checkBox.setChecked(true);
                                }
                            }

                        }
                    }
                    checkBox.setOnCheckedChangeListener(new CheckBoxCheckListener(checkBox));
                    soalLayout.addView(checkBox, soalParams);
                    //checkBoxes.add(checkBox);
                }

            }
            else if(questionType==Question.UPLOAD){
                TableLayout tableLayout = (TableLayout)inflater.inflate(R.layout.component_upload,null);
                tableLayout.setPadding(0, 0, 0, dpToPx(5));
                soalLayout.addView(tableLayout, soalParams);
                Button chooseFile = (Button)row.findViewById(R.id.btn_Chose);
                TextView fileName = (TextView)row.findViewById(R.id.txt_File);
                ImageView imageView = (ImageView)row.findViewById(R.id.imageViewer);

                chooseFile.setTag(question.getPertanyaan_id() + "**-");
                String localTag = (String)chooseFile.getTag();

                String questionID = localTag.split(Pattern.quote("**"))[0];
                if(isEditing){
                    List<Option>tempOptions = new ArrayList<>();
                    try {
                        tempOptions = searchAnswerBy(questionID,savedAnswer.getJSONArray("ANSWER"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(tempOptions.size()>0){
                        String[] fullPath = tempOptions.get(0).getTEXT().split("/");
                        String tempFileName = fullPath[fullPath.length-1];
                        fileName.setText(tempFileName);
                        setImageViewFromPath(tempOptions.get(0).getTEXT(),imageView);
                    }
                }
                chooseFile.setOnClickListener(new ButtonClickListener(chooseFile, fileName,imageView));

            }
            listPertanyaan.addView(row);
        }
        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0,0);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(SurveyQuestionActivity.this)
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
                //HashMap<String,Option> surveyResult = adapter.getSurveyAnswer();
                int pos = 0;
                for (Question question : questionList) {
                    List<Option> options = searchResultAnswerBy(question.getPertanyaan_id(), surveyAnswer);
                    LinearLayout row = soalLayoutHolders.get(pos);
                    //Log.e("row",row.getId()+"");
                    if (options.size() > 0) {
                        Question temp = new Question(question.getPertanyaan_id(), question.getPertanyaan(), question.getJenis_jawaban(), question.getUrutan(), idSurvey);
                        temp.setOPTIONS(options);
                        answeredQuestion.add(temp);
                        question.setAnswered(true);
                        row.setBackgroundColor(Color.WHITE);

                        //Log.e("answered id",temp.getPertanyaan_id());
                    } else {
                        unAnsweredQuestionID.add(question.getPertanyaan_id());
                        question.setAnswered(false);
                        row.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));

                        //Log.e("un answered id", question.getPertanyaan_id());
                    }

                    pos++;
                }
                if(unAnsweredQuestionID.size()>0) {
                    //List<Question> questions= adapter.getQuestionList();
                    /*for(Question question:questionList){
                        for(String questionID:unAnsweredQuestionID){
                            if(question.getPertanyaan_id().equals(questionID)){
                                question.setAnswered(false);
                                break;
                            }
                        }
                    }*/
                    //adapter.notifyDataSetChanged();
                    builder.setMessage("Ada beberapa pertanyaan yang belum terisi");
                    builder.show();
                }
                else{
                    /*
                    for(Question question:questionList){
                        for(String questionID:unAnsweredQuestionID){
                            if(question.getPertanyaan_id().equals(questionID)){
                                question.setAnswered(true);
                                break;
                            }
                        }
                    }
                    */

                    builder.setMessage("Semua pertanyaan telah terisi");
                    builder.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInputUser() {
        LayoutInflater mInflater = LayoutInflater.from(SurveyQuestionActivity.this);
        View v = mInflater.inflate(R.layout.activity_masukkannama, null);

        final AlertDialog dialog = new AlertDialog.Builder(SurveyQuestionActivity.this).create();

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

    private void setImageViewFromPath(String fullpath,ImageView imageView){
        Uri uri = Uri.parse(fullpath);
        File imgFile = new  File(uri.getPath());

        if(imgFile.exists()){
            Log.e("image file","exist");
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
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);

        }
        else{
            imageView.setVisibility(View.GONE);
            Log.e("image file", "not exist");
        }

    }

    private class RadioGroupChangedListener implements RadioGroup.OnCheckedChangeListener{

        private View view;
        private String tag;
        private Option answer = null;

        public RadioGroupChangedListener(View view) {
            this.view = view;
            //tag = (String)this.view.getTag();
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
            tag = (String)group.getTag();
            // This puts the value (true/false) into the variable
            boolean isChecked = checkedRadioButton.isChecked();
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked)
            {
                answer = (Option)checkedRadioButton.getTag();
            }
            //save the value for the given tag :
            surveyAnswer.put(tag, answer);


        }
    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;
        String tag;


        private GenericTextWatcher(View view) {
            this.view = view;
            tag = (String) this.view.getTag();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {

            String text = editable.toString();
            Option answer = new Option();
            if(!text.equals(""))
            {
                answer.setTEXT(text);
                //save the value for the given tag :
                surveyAnswer.put(tag, answer);
            }
            else{
                if(surveyAnswer.get(tag)!=null)
                    surveyAnswer.remove(tag);
            }

        }
    }

    private class CheckBoxCheckListener implements CompoundButton.OnCheckedChangeListener{

        private View view;
        private String tag;
        private Option answer = null;
        public CheckBoxCheckListener(View view) {
            this.view = view;

            //this.tag = (String)this.view.getTag(R.id.tagName);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            this.tag = (String)buttonView.getTag(R.id.tagName);
            if (buttonView.isChecked()) {
                answer = (Option)buttonView.getTag(R.id.tagData);
                //save the value for the given tag :
                surveyAnswer.put(tag, answer);
            }
            else {
                if(surveyAnswer.get(tag)!=null)
                    surveyAnswer.remove(tag);
            }


        }
    }

    private class ButtonClickListener implements Button.OnClickListener{
        View view;
        TextView fileTextView;
        ImageView imageView;
        String tag;
        public ButtonClickListener(View view,TextView fileTextView,ImageView imageView) {
            this.view = view;
            //this.tag = (String)this.view.getTag();
            this.fileTextView = fileTextView;
            this.imageView = imageView;

        }

        @Override
        public void onClick(View v) {
            this.tag = (String)v.getTag();
            lastClickedUploadButton = (Button)v;
            lastTextNeedChange = fileTextView;
            lastImageNeedChange = imageView;
            openImageIntent();

        }
    }

    Uri outputFileUri;
    private void openImageIntent() {
// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";//Utils.getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = this.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SurveyQuestionActivity.CHOOSE_IMAGE_CODE);
    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("picUri", outputFileUri);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        outputFileUri= savedInstanceState.getParcelable("picUri");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            Log.e("activity result",requestCode+"");
            if (requestCode == SurveyQuestionActivity.CHOOSE_IMAGE_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if(action!=null)
                        Log.e("cameran intent",action);
                    else
                        Log.e("cameran intent","null");

                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }
                Uri selectedImageUri;
                String filePath="";
                if (isCamera) {
                    if(data!=null)
                        Log.e("data intent",data.getDataString());
                    selectedImageUri = outputFileUri;
                    filePath=selectedImageUri.toString();
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
                        filePath = RealPathUtil.getRealPathFromURI_API19(SurveyQuestionActivity.this,selectedImageUri);
                    }
                    else if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
                        filePath = RealPathUtil.getRealPathFromURI_API11to18(SurveyQuestionActivity.this,selectedImageUri);
                    }
                    else if(Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB){
                        filePath = RealPathUtil.getRealPathFromURI_BelowAPI11(SurveyQuestionActivity.this,selectedImageUri);
                    }*/


                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(selectedImageUri,filePathColumn,null,null,null);
                    if(cursor!=null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();
                    }
                    else{
                        Log.e("activity result","cursor null: ");
                        filePath = selectedImageUri.getPath();
                    }
                }
                try {
                    Log.e("activity result", "path: " + filePath);
                    String buttonTag = (String) lastClickedUploadButton.getTag();
                    Log.e("activity result", "buttonTag event " + buttonTag);
                    if(filePath==null)
                        filePath = selectedImageUri.getPath();
                    Log.e("filepath",filePath);
                    String[] splitText = filePath.split("/");
                    lastTextNeedChange.setText(splitText[splitText.length - 1]);
                    setImageViewFromPath(filePath, lastImageNeedChange);
                    Option dummy = new Option();
                    dummy.setTEXT(filePath);
                    surveyAnswer.put(buttonTag, dummy);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this,"Ooopppss telah terjadi sesuatu, mohon coba lagi",Toast.LENGTH_LONG).show();
                }
            }

        }
    }


}


