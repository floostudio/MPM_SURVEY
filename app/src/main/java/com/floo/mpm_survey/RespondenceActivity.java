package com.floo.mpm_survey;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.floo.mpm_survey.respon.ActionItem;
import com.floo.mpm_survey.respon.QuickAction;

import java.util.ArrayList;
import java.util.HashMap;


public class RespondenceActivity extends AppCompatActivity{
    HashMap<String, String> mapp;
    ArrayList<HashMap<String, String>> mylistt;
    ListView lis_page_3;
    String[] page_T;
    String[] page_Tombol1;
    String[] page_Tombol2;
    String[] page_Tombol3;
    SimpleAdapter Adapter;
    ImageButton gg;

    private Context mContext;


    //menu
    private static final int ID_LOCK   = 1;
    private static final int ID_UPLOAD = 2;
    private static final int ID_TRASH  = 3;
    QuickAction quickAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respondence);

        lis_page_3 = (ListView) findViewById(R.id.list_page_3);

        //Menu
        ActionItem lockItem 	= new ActionItem(ID_LOCK, "Lock", getResources().getDrawable(R.drawable.menu_info));
        ActionItem uploadItem   = new ActionItem(ID_UPLOAD, "Upload", getResources().getDrawable(R.drawable.menu_ok));
        ActionItem trashItem    = new ActionItem(ID_TRASH, "Trash", getResources().getDrawable(R.drawable.menu_eraser));

        quickAction = new QuickAction(this, QuickAction.VERTICAL);

        quickAction.addActionItem(lockItem);
        quickAction.addActionItem(uploadItem);
        quickAction.addActionItem(trashItem);

        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);

                //here we can filter which action item was clicked with pos or actionId parameter
                if (actionId == ID_LOCK) {
                    Toast.makeText(getApplicationContext(), "file is locked...!!!", Toast.LENGTH_SHORT).show();
                } else if (actionId == ID_UPLOAD) {
                    Toast.makeText(getApplicationContext(), "files uploaded...!!!", Toast.LENGTH_SHORT).show();
                } else if (actionId == ID_TRASH){
                    Toast.makeText(getApplicationContext(), "deleted files...!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });




        //ListView
        page_T = new String[]{"RESPONDEN 1", "RESPONDEN 2", "RESPONDEN 3"};
        page_Tombol1 = new String[]{Integer.toString(R.drawable.lock), Integer.toString(R.drawable.lock), Integer.toString(R.drawable.lock)};
        page_Tombol2 = new String[]{Integer.toString(R.drawable.upload), Integer.toString(R.drawable.upload), Integer.toString(R.drawable.upload)};
        page_Tombol3 = new String[]{Integer.toString(R.drawable.trash), Integer.toString(R.drawable.trash), Integer.toString(R.drawable.trash)};

        mylistt = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < page_T.length; i++) {
            mapp = new HashMap<String, String>();
            mapp.put("pageee", page_T[i]);
            //mapp.put("tombol1", page_Tombol1[i]);
            //mapp.put("tombol2", page_Tombol2[i]);
            //mapp.put("tombol3", page_Tombol3[i]);

            mylistt.add(mapp);

        }

        Adapter = new SimpleAdapter(this, mylistt, R.layout.list_frag_ketiga,
                new String[]{"pageee"}, new int[]{R.id.txt_text});
        lis_page_3.setAdapter(Adapter);
        lis_page_3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent aa=new Intent(RespondenceActivity.this, SurveyActivity.class);
                startActivity(aa);
            }
        });

    }

    /* public void eventUpload(final View v){
       v.post(new Runnable() {
                @Override
                public void run() {
                    showPopupMenu(v);
                }
        });

    }


    private void showPopupMenu(View view) {

        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

        popup.show();
    }*/



    public void eventUpload(View v){
        quickAction.show(v);
    }






}
