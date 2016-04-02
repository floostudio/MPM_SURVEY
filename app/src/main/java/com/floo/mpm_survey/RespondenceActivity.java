package com.floo.mpm_survey;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.floo.database.DBHandler;
import com.floo.mpm_survey.respon.ActionItem;
import com.floo.mpm_survey.respon.QuickAction;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RespondenceActivity extends AppCompatActivity{
    ListView listResponden;
    Button btnNew, btnUpload;

    //menu
    private static final int IDLOCK   = 1;
    private static final int IDUPLOAD = 2;
    private static final int IDTRASH  = 3;
    private static final int IDEDIT = 4;
    private static final int IDFINAL = 5;

    QuickAction quickAction;

    ActionItem lockItem;
    ActionItem editItem;
    ActionItem trashItem;
    ActionItem uploadItem;
    ActionItem finalItem;

    DBHandler handler;
    static  String idSurvey,respondenName;

    public static RespondenceActivity ra;

    RespondenAdapter respondenAdapter;
    boolean lockAll;

    @Override
    protected void onResume() {
        super.onResume();
        RefreshList();
        if(respondenAdapter!=null){
            respondenAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respondence);
        Intent i = getIntent();
        idSurvey = i.getStringExtra("SURVEY_ID");
        handler = new DBHandler(this);

        listResponden = (ListView) findViewById(R.id.list_page_3);
        listResponden.setItemsCanFocus(true);
        btnNew = (Button) findViewById(R.id.btn_Res_Start);
        btnUpload = (Button) findViewById(R.id.btn_Res_Upload);

        RefreshList();

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent aa=new Intent(RespondenceActivity.this, QuestionActivity.class);
                aa.putExtra("SURVEY_ID", idSurvey);
                startActivity(aa);*/
                Intent aa=new Intent(RespondenceActivity.this, SurveyQuestionActivity.class);
                aa.putExtra("SURVEY_ID", idSurvey);
                startActivity(aa);

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Responden>lockedResponden = handler.getAllLockedResponden(idSurvey);
                RespondencesAnswer lockedRespondenAnswer = new RespondencesAnswer(RespondenceActivity.this);
                SubmitSurvey submitSurvey = new SubmitSurvey(RespondenceActivity.this);
                for(Responden responden:lockedResponden){
                    String respondenID = responden.getID_RESPONDENCE();
                    JSONObject savedAnswer = lockedRespondenAnswer.getAnswerData(idSurvey,respondenID);
                    submitSurvey.addDataToUpload(savedAnswer);
                    //handler.setUploadedResponden(respondenID);
                }
                if(lockedResponden.size()>0){
                    submitSurvey.execute();
                    //RefreshList();
                }

            }
        });

        quickAction = new QuickAction(this, QuickAction.VERTICAL);

        //Menu
        lockItem = new ActionItem(IDLOCK, "Lock", ContextCompat.getDrawable(this, R.drawable.menu_info));
        uploadItem = new ActionItem(IDUPLOAD, "Upload",  ContextCompat.getDrawable(this, R.drawable.menu_ok));
        editItem = new ActionItem(IDEDIT, " Edit", ContextCompat.getDrawable(this, R.drawable.menu_edit));
        trashItem = new ActionItem(IDTRASH, "Trash",  ContextCompat.getDrawable(this, R.drawable.menu_eraser));
        finalItem = new ActionItem(IDFINAL,"Final", ContextCompat.getDrawable(this, R.drawable.menu_check));
        ra = this;

        listResponden.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idRespon = ((TextView) view.findViewById(R.id.txt_idParsingResponden)).getText().toString();
                //String statusRespon = ((TextView) view.findViewById(R.id.txt_statusParsingResponden)).getText().toString();
                Responden responden = (Responden) listResponden.getItemAtPosition(position);
                respondenName = responden.getNAMA_RESPONDENCE();

                initQuickAction(idRespon,responden.isLOCKED(),responden.isFINAL());
                quickAction.show(view);
            }
        });
    }
    private void initQuickAction(String respondenID, final boolean isLocked,final boolean isFinal){
        quickAction = new QuickAction(RespondenceActivity.this, QuickAction.VERTICAL);
        quickAction.RESPONDEN_ID = respondenID;
        if(isFinal)
            finalItem.setTitle("Not Final");
        else
            finalItem.setTitle("Final");
        if(isLocked){
            lockItem.setTitle("Unlock");
            quickAction.addActionItem(lockItem);
            quickAction.addActionItem(finalItem);
            quickAction.addActionItem(uploadItem);
        }
        else{
            lockItem.setTitle("Lock");
            quickAction.addActionItem(lockItem);
            quickAction.addActionItem(finalItem);
            quickAction.addActionItem(editItem);
            quickAction.addActionItem(trashItem);
        }
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);

                //here we can filter which action item was clicked with pos or actionId parameter
                if (actionId == IDLOCK) {
                    handler.setLockResponden(quickAction.RESPONDEN_ID, !isLocked);
                } else if (actionId == IDUPLOAD) {
                    Toast.makeText(getApplicationContext(), "files uploaded...!!! But not real", Toast.LENGTH_SHORT).show();
                    RespondencesAnswer respondencesAnswer = new RespondencesAnswer(RespondenceActivity.this);
                    SubmitSurvey submitSurvey = new SubmitSurvey(RespondenceActivity.this);
                    submitSurvey.addDataToUpload(respondencesAnswer.getAnswerData(idSurvey, quickAction.RESPONDEN_ID));
                    submitSurvey.execute();
                    //handler.setUploadedResponden(quickAction.RESPONDEN_ID);
                    //RefreshList();

                } else if (actionId == IDTRASH) {
                    new AlertDialog.Builder(RespondenceActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah Anda yakin akan menghapus survey untuk Responden: " + respondenName + "?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handler.getDeleteResponden(quickAction.RESPONDEN_ID);
                                    RespondencesAnswer respondencesAnswer = new RespondencesAnswer(getApplicationContext());
                                    respondencesAnswer.deleteAnswerData(idSurvey, quickAction.RESPONDEN_ID);
                                    Toast.makeText(getApplicationContext(), "RESPONDEN  " + respondenName + " DELETED", Toast.LENGTH_SHORT).show();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                } else if (actionId == IDEDIT) {
                    Intent intent = new Intent(RespondenceActivity.this, QuestionActivity.class);
                    intent.putExtra("SURVEY_ID", idSurvey);
                    intent.putExtra("RESPONDENCE_ID", quickAction.RESPONDEN_ID);
                    intent.putExtra("isEditing", true);
                    startActivity(intent);

                    //Toast.makeText(getApplicationContext(), "files Edit...!!!", Toast.LENGTH_SHORT).show();
                } else if (actionId == IDFINAL) {
                    handler.setFinalResponden(quickAction.RESPONDEN_ID, !isFinal);
                }
            }
        });

    }

    public void RefreshList(){
        ArrayList<Responden> respList = handler.getAllResponden(idSurvey);
        respondenAdapter = new RespondenAdapter(RespondenceActivity.this, respList);
        respondenAdapter.notifyDataSetChanged();
        listResponden.setAdapter(respondenAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_responden,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_lock:
                ArrayList<Responden> respList = handler.getAllResponden(idSurvey);
                if(lockAll)
                    item.setTitle("Lock All");
                else
                    item.setTitle("Unlock All");
                lockAll=!lockAll;
                for(Responden responden:respList){
                    handler.setLockResponden(responden.getID_RESPONDENCE(),lockAll);
                    responden.setLOCKED(lockAll);
                    //Log.e("event","ID "+responden.getID_RESPONDENCE()+" stat"+responden.getIS_LOCK());
                }
                respondenAdapter.getListData().clear();
                respondenAdapter.getListData().addAll(respList);
                respondenAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
