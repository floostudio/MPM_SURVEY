package com.floo.bantuan;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.floo.database.SQLite;
import com.floo.mpm_survey.R;
import com.floo.mpm_survey.SurveyActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Floo on 1/27/2016.
 */
public class Bantu extends AppCompatActivity {
    SQLite dmm;
    EditText tmbh;
    Button Res_tambah;
    TableLayout tabeldata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wbantu);
        dmm = new SQLite(this);
        tabeldata = (TableLayout) findViewById (R.id.tabel_data_res);
        tmbh = (EditText) findViewById(R.id.nama_respon);

        Res_tambah = (Button) findViewById(R.id.Respo_tambah);
        //membuat onclick pada tombol button btambah
        /*Res_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //menjalankan simpandata(); yang nanti akan di buat
               // simpanData();
            }
        });*/
        //mengupdate table bila data sudah di simpan
        updateTable();


    }

    private void updateTable() {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        while (tabeldata.getChildCount() > 1) {
            tabeldata.removeViewAt(1);
        }
        double aa = tabeldata.getChildCount();
        String a = String.valueOf(aa);
        //Toast.makeText(getBaseContext(), "tabel data child : " + a,
        //      Toast.LENGTH_SHORT).show();

        ArrayList<ArrayList<Object>> data = dmm.tabelRespondence();//

        for (int posisi = 0; posisi < data.size(); posisi++) {
            TableRow tabelBaris = new TableRow(this);
            ArrayList<Object> baris = data.get(posisi);

            TextView idTxt = new TextView(this);
            idTxt.setText(baris.get(0).toString());
            tabelBaris.addView(idTxt);

            TextView namaTxt = new TextView(this);
            namaTxt.setText(baris.get(1).toString());
            tabelBaris.addView(namaTxt);



            tabeldata.addView(tabelBaris);

        }
    }

}
