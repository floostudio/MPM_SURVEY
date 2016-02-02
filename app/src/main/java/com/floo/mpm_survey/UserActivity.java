package com.floo.mpm_survey;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.floo.database.SQLite;

public class UserActivity extends AppCompatActivity {
    //mendeklarasikan
    SQLite dm;
    EditText inama, isekolah;
    Button btambah;
    TableLayout tabel4data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //mengkaitkan TempatDatabase ke MainActivity
        dm = new SQLite(this);
        tabel4data = (TableLayout) findViewById (R.id.tabel_data);
        inama = (EditText) findViewById(R.id.inama);
        isekolah = (EditText) findViewById(R.id.isekolah);
        btambah = (Button) findViewById(R.id.btambah);
        //membuat onclick pada tombol button btambah
        btambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //menjalankan simpandata(); yang nanti akan di buat
                simpanData();
            }
        });
        //mengupdate table bila data sudah di simpan
        updateTable();

    }

    private void updateTable() {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        while (tabel4data.getChildCount() > 1) {
            tabel4data.removeViewAt(1);
        }
        double aa = tabel4data.getChildCount();
        String a = String.valueOf(aa);
        //Toast.makeText(getBaseContext(), "tabel data child : " + a,
          //      Toast.LENGTH_SHORT).show();

        ArrayList<ArrayList<Object>> data = dm.ambilSemuaBaris();//

        for (int posisi = 0; posisi < data.size(); posisi++) {
            TableRow tabelBaris = new TableRow(this);
            ArrayList<Object> baris = data.get(posisi);

            TextView idTxt = new TextView(this);
            idTxt.setText(baris.get(0).toString());
            tabelBaris.addView(idTxt);

            TextView namaTxt = new TextView(this);
            namaTxt.setText(baris.get(1).toString());
            tabelBaris.addView(namaTxt);

            TextView hobiTxt = new TextView(this);
            hobiTxt.setText(baris.get(2).toString());
            tabelBaris.addView(hobiTxt);

            tabel4data.addView(tabelBaris);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //membuat simpan data bila tombol buton di jalankan
    private void simpanData() {
        // TODO Auto-generated method stub
        try {
            dm.addRow(inama.getText().toString(), isekolah.getText().toString());
            Toast.makeText(getBaseContext(),
                    inama.getText().toString() + ", berhasil disimpan",
                    Toast.LENGTH_SHORT).show();
            updateTable();
            //mengkosongkan field yang akan di buat di bawah
            kosongkanField();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "gagal simpan, " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
    //membuat kosong pada text bila sudah di jalankan penyimpanan
    private void kosongkanField() {
        // TODO Auto-generated method stub
        inama.setText("");
        isekolah.setText("");
    }

}