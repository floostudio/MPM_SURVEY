package com.floo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.floo.mpm_survey.Option;
import com.floo.mpm_survey.Question;
import com.floo.mpm_survey.Responden;
import com.floo.mpm_survey.PublicListener;
import com.floo.mpm_survey.RespondenceActivity;
import com.floo.mpm_survey.Survey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Floo on 2/3/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "SurveyDatabase.db";

    //TABLE RESPONDENCE
    private static final String TABLE_RESPONDENCE = "table_respondence";
    private static final String KEY_RESPONDENCE_ID = "id_respondence";
    private static final String KEY_RESPONDENCE_NAME = "nama_respondence";
    private static final String KEY_RESPONDENCE_IS_LOCK = "is_lock";
    private static final String KEY_CREATE_AT_RESPONDENCE = "created_at";
    private static final String KEY_LASTMODIFIED_RESPONDENCE = "last_modified";
    private static final String KEY_LATITUDE_RESPONDENCE = "latitude";
    private static final String KEY_LONGITUDE_RESPONDENCE = "longitude";
    private static final String KEY_RESPONDENCE_IS_FINAL = "is_final";
    private static final String KEY_RESPONDENCE_IS_UPLOADED = "is_uploaded";
    private static final String KEY_RESPONDENCE_IDSURVEY = "id_survey";


    String SYNTAX_RESPONDENCE = "CREATE TABLE "+TABLE_RESPONDENCE+ "("+ KEY_RESPONDENCE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_RESPONDENCE_NAME + " TEXT,"+ KEY_RESPONDENCE_IS_LOCK + " BOOLEAN,"+KEY_CREATE_AT_RESPONDENCE+ " DATETIME,"
            +KEY_LASTMODIFIED_RESPONDENCE+" DATETME NULL,"+KEY_LATITUDE_RESPONDENCE+" TEXT NULL,"+KEY_LONGITUDE_RESPONDENCE+" TEXT NULL, "
            +KEY_RESPONDENCE_IS_FINAL+" BOOLEAN, "+KEY_RESPONDENCE_IS_UPLOADED+" BOOLEAN, "+ KEY_RESPONDENCE_IDSURVEY +" TEXT)";
    String DROP_RESPONDENCE = "DROP TABLE IF EXISTS "+TABLE_RESPONDENCE;

    //TABLE SURVEY
    private static final String TABLE_SURVEY = "survey_table";
    private static final String KEY_SURVEYID = "_survey_id";
    private static final String KEY_SURVEYNAMA = "_survey_nama";
    private static final String KEY_TANGGALAKTIF = "_tanggal_aktif";
    private static final String KEY_DIVISI = "_divisi";
    private static final String KEY_ISAKTIF = "_is_aktif";
    private static final String KEY_JENISRESPONDENCE = "_jenis_respondence";
    private static final String KEY_LASTMODIFIED = "_last_modified";

    String CREATE_TABLE = "CREATE TABLE " + TABLE_SURVEY + " (" + KEY_SURVEYID + " TEXT PRIMARY KEY," + KEY_SURVEYNAMA + " TEXT," +
            "" + KEY_TANGGALAKTIF + " TEXT," + KEY_DIVISI + " TEXT," + KEY_ISAKTIF + " TEXT," + KEY_JENISRESPONDENCE + " TEXT," + KEY_LASTMODIFIED + " TEXT)";
    String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_SURVEY;

    //TABLE PERTANYAAN
    private static final String TABLE_PERTANYAAN = "pertanyaan_table";
    private static final String KEY_IDPERTANYAAN = "pertanyaan_id";
    private static final String KEY_PERTANYAAN = "pertanyaan";
    private static final String KEY_JENISJAWABAN = "jenis_jawaban";
    private static final String KEY_URUTAN = "urutan";
    private static final String KEY_PERTANYAAN_IDSURVEY = "survey_id";


    String SYNTAX_PERTANYAAN = "CREATE TABLE " + TABLE_PERTANYAAN + " (" + KEY_IDPERTANYAAN + " TEXT PRIMARY KEY," + KEY_PERTANYAAN +
            " TEXT," + KEY_JENISJAWABAN + " TEXT," + KEY_URUTAN + " TEXT," +KEY_PERTANYAAN_IDSURVEY+" TEXT)";
    String DROP_PERTANYAAN = "DROP TABLE IF EXISTS "+TABLE_PERTANYAAN;

    //TABLE OPTION
    private static final String TABLE_OPTION = "option_table";
    private static final String KEY_OPTION_ID = "optionID";
    private static final String KEY_OPTION_TEXT = "optionText";
    private static final String KEY_OPTION_BOBOT = "optionBobot";
    private static final String KEY_OPTION_URUTAN = "optionUrutan";
    //Add FK pertanyaan ID in option
    private static final String KEY_OPTION_PERTANYAAN_ID = "optionPertanyaanID";


    String SYTAX_OPTION = "CREATE TABLE " +TABLE_OPTION+ " (" + KEY_OPTION_ID + " TEXT PRIMARY KEY,"+ KEY_OPTION_TEXT +
            " TEXT,"+ KEY_OPTION_BOBOT + " TEXT,"+ KEY_OPTION_URUTAN + " TEXT, "+KEY_OPTION_PERTANYAAN_ID+" TEXT)";
    String DROP_OPTION = "DROP TABLE IF EXISTS "+TABLE_OPTION;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(SYNTAX_PERTANYAAN);
        db.execSQL(SYNTAX_RESPONDENCE);
        db.execSQL(SYTAX_OPTION);
        /*
        db.execSQL("INSERT INTO table_respondence (id_respondence, nama_respondence, status_respondence, created_at, " +
                "last_modified, latitude, longitude, id_survey) VALUES (1, 'floostudio', 'unlock', '2016-02-02 16:29:57', '2016-02-02 16:29:57', 123, 456, '1');");
                */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(DROP_PERTANYAAN);
        db.execSQL(DROP_RESPONDENCE);
        db.execSQL(DROP_OPTION);
        onCreate(db);
    }

    /*----------T A B L E  S U R V E Y----------*/

    public void addSurvey(Survey survey) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_SURVEYID, survey.getSurvey_id());
            values.put(KEY_SURVEYNAMA, survey.getSurvey_nama());
            values.put(KEY_TANGGALAKTIF, survey.getTanggal_aktif());
            values.put(KEY_DIVISI, survey.getDivisi());
            values.put(KEY_ISAKTIF, survey.getIs_aktif());
            values.put(KEY_JENISRESPONDENCE, survey.getJenis_respondence());
            values.put(KEY_LASTMODIFIED, survey.getLast_modified());
            db.insert(TABLE_SURVEY, null, values);
            db.close();
        } catch (Exception e) {
            Log.e("problem", e + "");
        }
    }


    public ArrayList<Survey> getAllSurvey() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Survey> surveyList = null;
        try {
            surveyList = new ArrayList<Survey>();
            String QUERY = "SELECT * FROM " + TABLE_SURVEY;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    Survey survey = new Survey();
                    survey.setSurvey_id(cursor.getString(0));
                    survey.setSurvey_nama(cursor.getString(1));
                    survey.setTanggal_aktif(cursor.getString(2));
                    survey.setDivisi(cursor.getString(3));
                    survey.setIs_aktif(cursor.getString(4));
                    survey.setJenis_respondence(cursor.getString(5));
                    survey.setLast_modified(cursor.getString(6));
                    surveyList.add(survey);
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return surveyList;
    }


    public int getSurveyCount() {
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String QUERY = "SELECT * FROM " + TABLE_SURVEY;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return 0;
    }

    /*----------T A B L E  P E R T A N Y A A N----------*/
    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IDPERTANYAAN, question.getPertanyaan_id());
            values.put(KEY_PERTANYAAN, question.getPertanyaan());
            values.put(KEY_JENISJAWABAN, question.getJenis_jawaban());
            values.put(KEY_URUTAN, question.getUrutan());
            values.put(KEY_PERTANYAAN_IDSURVEY, question.getSurvey_id());
            db.insert(TABLE_PERTANYAAN, null, values);
            db.close();
        } catch (Exception e) {
            Log.e("problem", e + "");
        }
    }


    public ArrayList<Question> getAllQuestion(String id_survey) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Question> questionList = null;
        try {
            questionList = new ArrayList<Question>();
            String QUERY = "SELECT * FROM " + TABLE_PERTANYAAN + " WHERE "+KEY_PERTANYAAN_IDSURVEY+" = "+"'"+id_survey+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    Question question = new Question();
                    question.setPertanyaan_id(cursor.getString(0));
                    question.setPertanyaan(cursor.getString(1));
                    question.setJenis_jawaban(cursor.getString(2));
                    question.setUrutan(cursor.getString(3));
                    question.setSurvey_id(cursor.getString(4));
                    questionList.add(question);
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return questionList;
    }


    public int getQuestionCount(String id_survey) {
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String QUERY = "SELECT * FROM " + TABLE_PERTANYAAN + " WHERE "+KEY_PERTANYAAN_IDSURVEY+" = "+"'"+id_survey+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return 0;
    }

    /*----------T A B L E  R E S P O N D E N----------*/
    public ArrayList<Responden> getAllResponden(String id_survey){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Responden> answerList = null;
        try {
            answerList = new ArrayList<>();
            //String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+ " WHERE "+ KEY_RESPONDENCE_IDSURVEY +" = "+"'"+id_survey+"'";
            String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+
                    " WHERE "+ KEY_RESPONDENCE_IDSURVEY +" = "+"'"+id_survey+"' AND "+KEY_RESPONDENCE_IS_UPLOADED+" = 0";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    Responden responden = new Responden();
                    responden.setID_RESPONDENCE(cursor.getString(0));
                    responden.setNAMA_RESPONDENCE(cursor.getString(1));
                    responden.setLOCKED(cursor.getInt(2) > 0);
                    responden.setCRETAED_AT(cursor.getString(3));
                    responden.setLAST_MODIFIED(cursor.getString(4));
                    responden.setLATITUDE(cursor.getString(5));
                    responden.setLONGITUDE(cursor.getString(6));
                    responden.setFINAL(cursor.getInt(7) > 0);
                    responden.setUPLOADED(cursor.getInt(8)>0);
                    answerList.add(responden);
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return answerList;
    }
    public Responden getRespondenByID(String respondenID){
        SQLiteDatabase db = this.getReadableDatabase();
        Responden result = null;
        try {
            //answerList = new ArrayList<>();
            //String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+ " WHERE "+ KEY_RESPONDENCE_IDSURVEY +" = "+"'"+id_survey+"'";
            String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+
                    " WHERE "+ KEY_RESPONDENCE_ID+" = "+"'"+respondenID+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    Responden responden = new Responden();
                    responden.setID_RESPONDENCE(cursor.getString(0));
                    responden.setNAMA_RESPONDENCE(cursor.getString(1));
                    responden.setLOCKED(cursor.getInt(2) > 0);
                    responden.setCRETAED_AT(cursor.getString(3));
                    responden.setLAST_MODIFIED(cursor.getString(4));
                    responden.setLATITUDE(cursor.getString(5));
                    responden.setLONGITUDE(cursor.getString(6));
                    responden.setFINAL(cursor.getInt(7) > 0);
                    responden.setUPLOADED(cursor.getInt(8)>0);
                    result = responden;
                    //answerList.add(responden);
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return result;
    }
    public ArrayList<Responden> getAllLockedResponden(String id_survey){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Responden> answerList = null;
        try {
            answerList = new ArrayList<>();
            //String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+ " WHERE "+ KEY_RESPONDENCE_IDSURVEY +" = "+"'"+id_survey+"'";
            String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+
                    " WHERE "+ KEY_RESPONDENCE_IDSURVEY +" = "+"'"+id_survey+"' AND "+KEY_RESPONDENCE_IS_UPLOADED+" = 0 AND "+
                    KEY_RESPONDENCE_IS_LOCK+" = 1";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    Responden responden = new Responden();
                    responden.setID_RESPONDENCE(cursor.getString(0));
                    responden.setNAMA_RESPONDENCE(cursor.getString(1));
                    responden.setLOCKED(cursor.getInt(2) > 0);
                    responden.setCRETAED_AT(cursor.getString(3));
                    responden.setLAST_MODIFIED(cursor.getString(4));
                    responden.setLATITUDE(cursor.getString(5));
                    responden.setLONGITUDE(cursor.getString(6));
                    responden.setFINAL(cursor.getInt(7) > 0);
                    responden.setUPLOADED(cursor.getInt(8)>0);
                    answerList.add(responden);
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("error", e + "");
        }
        return answerList;
    }
    public long addResponden(Responden responden,String surveyID){
        SQLiteDatabase db = this.getWritableDatabase();
        long repondenceID = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_RESPONDENCE_IDSURVEY, surveyID);
            values.put(KEY_RESPONDENCE_NAME, responden.getNAMA_RESPONDENCE());
            values.put(KEY_RESPONDENCE_IS_LOCK, responden.isLOCKED());
            values.put(KEY_LASTMODIFIED_RESPONDENCE, responden.getLAST_MODIFIED());
            values.put(KEY_CREATE_AT_RESPONDENCE, responden.getCRETAED_AT());
            values.put(KEY_LATITUDE_RESPONDENCE, responden.getLATITUDE());
            values.put(KEY_LONGITUDE_RESPONDENCE, responden.getLONGITUDE());
            values.put(KEY_RESPONDENCE_IS_FINAL, responden.isFINAL());
            values.put(KEY_RESPONDENCE_IS_UPLOADED, responden.isUPLOADED());

            repondenceID = db.insert(TABLE_RESPONDENCE, null, values);
            db.close();
        } catch (Exception e) {
            Log.e("problem", e + "");
        }
        return  repondenceID;
    }
    public void updateRespondenceLastModified(String id_respondence, String lastModified, String latitude, String longitude){
        SQLiteDatabase db = this.getWritableDatabase();

        String strFilter = KEY_RESPONDENCE_ID +"=" + id_respondence;
        ContentValues args = new ContentValues();
        args.put(KEY_LASTMODIFIED_RESPONDENCE, lastModified);
        args.put(KEY_LATITUDE_RESPONDENCE, latitude);
        args.put(KEY_LONGITUDE_RESPONDENCE, longitude);
        db.update(TABLE_RESPONDENCE,args,strFilter,null);
    }


    public void getDeleteResponden(String id_respondence) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_RESPONDENCE + " where id_respondence='" + id_respondence + "'");
            //RespondenceActivity.ra.RefreshList();
    }

    public void setLockResponden(String id_respondence,boolean isLock){
        SQLiteDatabase db=this.getWritableDatabase();
        //db.execSQL("update "+TABLE_RESPONDENCE+" set status_respondence='lock' where id_respondence='"+id_respondence+"'");
        //RespondenceActivity.ra.RefreshList();
        String strFilter = KEY_RESPONDENCE_ID +"=" + id_respondence;
        ContentValues args = new ContentValues();
        args.put(KEY_RESPONDENCE_IS_LOCK, isLock);
        db.update(TABLE_RESPONDENCE, args, strFilter, null);
        RespondenceActivity.ra.RefreshList();
    }
    public void setFinalResponden(String id_respondence,boolean isFinal){
        SQLiteDatabase db=this.getWritableDatabase();
        //db.execSQL("update "+TABLE_RESPONDENCE+" set status_respondence='lock' where id_respondence='"+id_respondence+"'");
        //RespondenceActivity.ra.RefreshList();
        String strFilter = KEY_RESPONDENCE_ID +"=" + id_respondence;
        ContentValues args = new ContentValues();
        args.put(KEY_RESPONDENCE_IS_FINAL, isFinal);
        db.update(TABLE_RESPONDENCE, args, strFilter, null);
        RespondenceActivity.ra.RefreshList();
    }
    public void setUploadedResponden(String id_respondence){
        SQLiteDatabase db=this.getWritableDatabase();
        //db.execSQL("update "+TABLE_RESPONDENCE+" set status_respondence='lock' where id_respondence='"+id_respondence+"'");
        //RespondenceActivity.ra.RefreshList();
        String strFilter = KEY_RESPONDENCE_ID +"=" + id_respondence;
        ContentValues args = new ContentValues();
        args.put(KEY_RESPONDENCE_IS_UPLOADED, true);
        db.update(TABLE_RESPONDENCE,args,strFilter,null);
        RespondenceActivity.ra.RefreshList();
    }

    public int getUploadedRespondenCount(String idSurvey){
        int count =0;
        String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+
                " WHERE "+ KEY_RESPONDENCE_IDSURVEY +" = "+"'"+idSurvey +"' AND "+KEY_RESPONDENCE_IS_UPLOADED+" = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        count = cursor.getCount();
        cursor.close();

        return count;
    }
    public int getRespondenCount(String idSurvey){
        int count =0;
        String QUERY = "SELECT * FROM " + TABLE_RESPONDENCE+
                " WHERE "+ KEY_RESPONDENCE_IDSURVEY +" = "+"'"+idSurvey +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        count = cursor.getCount();
        cursor.close();

        return count;
    }
    /*
    public void unlockResponden(String id_respondence){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update "+TABLE_RESPONDENCE+" set status_respondence='unlock' where id_respondence='"+id_respondence+"'");
        RespondenceActivity.ra.RefreshList();
    }*/
    /*TABLE OPTION*/
    public void addOption(Option option) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_OPTION_ID, option.getOPTION_ID());
            values.put(KEY_OPTION_BOBOT, option.getBOBOT());
            values.put(KEY_OPTION_TEXT, option.getTEXT());
            values.put(KEY_OPTION_URUTAN, option.getURUTAN());
            values.put(KEY_OPTION_PERTANYAAN_ID, option.getPERTANYAAN_ID());
            db.insert(TABLE_OPTION, null, values);
            db.close();
        } catch (Exception e) {
            Log.e("problem", e + "");
        }
    }
    public List<Option> getOptionsByPertanyaanID(String pertanyaanID){
        List<Option>options = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY = "SELECT * FROM " + TABLE_OPTION+ " WHERE "+KEY_OPTION_PERTANYAAN_ID+" = "+"'"+pertanyaanID+"'";
        Cursor cursor = db.rawQuery(QUERY, null);
        if (!cursor.isLast()) {
            while (cursor.moveToNext()) {
                Option row = new Option();
                row.setOPTION_ID(cursor.getString(0));
                row.setTEXT(cursor.getString(1));
                row.setBOBOT(cursor.getString(2));
                row.setURUTAN(cursor.getString(3));
                row.setPERTANYAAN_ID(cursor.getString(4));
                options.add(row);
            }
        }
        db.close();
        return options;
    }
}
