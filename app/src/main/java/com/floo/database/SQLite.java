package com.floo.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLite {
    //mendeklarasikan NAMA_DB DAN TABLE DAN DATABASE VERSION
    private static final String NAMA_DB ="INIDB";

    private static final int DB_VERSION = 2;
    //mendeklarasikan ROW Tabel User
    private static final String NAMA_TABEL="users";
    private static final String ROW_ID = "_id";
    private static final String ROW_USERNAME = "username";
    private static final String ROW_PASSWORD = "password";
    //private static final String TAG = "DBAdapter";
    //mendeklarasikan Tabel respondence
    private static final String TABEL_RESPONDENCE = "respondence";
    private static final String RESPONDENCE_ID = "_id";
    private static final String RESPONDENCE_NAMA = "nama";

    //mendeklarasikan CREATE_TABLE = MEMBUAT TABLE"
    private static final String CREATE_TABLE =
            "create table "+NAMA_TABEL+" ("+ROW_ID+" integer PRIMARY KEY autoincrement, "+ROW_USERNAME+" text,"+ROW_PASSWORD+" text)";

    //mendeklarasikan CREATE_TABLE = MEMBUAT TABLE RESPONDENCE
    private static final String CREATE_TABLE_RESPON =
            "create table "+TABEL_RESPONDENCE+" ("+RESPONDENCE_ID+" integer PRIMARY KEY autoincrement, "+RESPONDENCE_NAMA+" text)";

    //membuat mendeklarasikan itu adalah context
    private final Context context;
    //membuat mendeklarasikan DatabaseOpenHelper itu adalah dbhelper
    private DatabaseOpenHelper dbhelper;
    //membuat mendeklarasikan SQLiteDatabase itu adalah db
    private SQLiteDatabase db;

    //mengambil context untuk mengakses system di android
    public SQLite(Context ctx) {
        //mendeklarasikan ctx adalah context ( context context di ganti ctx )
        this.context = ctx;
        // membuat DatabaseOpenHelper
        dbhelper = new DatabaseOpenHelper(context);
        //menuliskan DatabaseOpenHelper = SQLiteDatabase
        db = dbhelper.getWritableDatabase();
    }
    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
        //membuat database
        public DatabaseOpenHelper(Context context) {
            super(context, NAMA_DB, null, DB_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(CREATE_TABLE);
            db.execSQL("insert into "+NAMA_TABEL+" values (1,'123456','123456');");
            db.execSQL("insert into "+NAMA_TABEL+" values (2,'admin','admin');");

            //TABLE RESPONDENCE
            db.execSQL(CREATE_TABLE_RESPON);
            db.execSQL("insert into "+TABEL_RESPONDENCE+" values (1, 'RESPONDENCE 1');");
            db.execSQL("insert into "+TABEL_RESPONDENCE+" values (2, 'RESPONDENCE 2');");
            db.execSQL("insert into "+TABEL_RESPONDENCE+" values (3, 'RESPONDENCE 3');");
        }
        //memperbarui database bila sudah ada
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            // TODO Auto-generated method stub
            /*Log.w(TAG, "Upgrading database from version " + oldVer
                    + " to "
                    + newVer + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);*/

            db.execSQL("DROP TABLE IF EXISTS "+NAMA_DB);
            onCreate(db);



        }
    }

    public void open() throws SQLException
    {
        db = dbhelper.getWritableDatabase();
    }


    //menutup DatabaseOpenHelper
    public void close() {
        dbhelper.close();
    }
    //menambahkan pada row
    public void addRow(String nama, String sekolah) {

        ContentValues values = new ContentValues();
        values.put(ROW_USERNAME, nama);
        values.put(ROW_PASSWORD, sekolah);

        try {
            //menambahkan nama tabel bila tidak akan error
            // db.delete(NAMA_TABEL, null, null);
            db.insert(NAMA_TABEL, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }
    //membuat array pada table layout
    public ArrayList<ArrayList<Object>> ambilSemuaBaris() {
        ArrayList<ArrayList<Object>> dataArray = new ArrayList<ArrayList<Object>>();
        Cursor cur;
        try {
            cur = db.query(NAMA_TABEL,
                    new String[] { ROW_ID, ROW_USERNAME, ROW_PASSWORD }, null, null,
                    null, null, null);
            cur.moveToFirst();
            if (!cur.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cur.getLong(0));
                    dataList.add(cur.getString(1));
                    dataList.add(cur.getString(2));

                    dataArray.add(dataList);

                } while (cur.moveToNext());

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("DEBE ERROR", e.toString());
        }
        return dataArray;

    }

    public boolean Login(String username, String password) throws SQLException
    {
        Cursor mcursor=db.rawQuery("SELECT * FROM " + NAMA_TABEL + " WHERE "+ROW_USERNAME+"=? AND "+ROW_PASSWORD+"=?", new String[]{username,password});
        if (mcursor != null) {
            if(mcursor.getCount() > 0)
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ArrayList<Object>> tabelRespondence() {
        ArrayList<ArrayList<Object>> dataArray = new ArrayList<ArrayList<Object>>();
        Cursor cur;
        try {
            cur = db.query(TABEL_RESPONDENCE,
                    new String[] { RESPONDENCE_ID, RESPONDENCE_NAMA }, null, null,
                    null, null, null);
            cur.moveToFirst();
            if (!cur.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cur.getLong(0));
                    dataList.add(cur.getString(1));

                    dataArray.add(dataList);

                } while (cur.moveToNext());

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("DEBE ERROR", e.toString());
        }
        return dataArray;

    }




}