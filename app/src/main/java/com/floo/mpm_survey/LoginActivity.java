package com.floo.mpm_survey;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements AsyncResponse{
    EditText EdtUser, EdtPassword;
    Button BtnLogin;
    ImageView InfoUser, InfoPassword;
    AlertDialog message;
    ProgressDialog progressDialog;
    LocalLoginData localLoginData;
    Login loginTask;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Typeface font=Typeface.createFromAsset(getAssets(), "fonts/SFCompactDisplay-Regular.ttf");
        //Typeface f=Typeface.createFromAsset(getAssets(), "fonts/SFCompactDisplay-Bold.ttf");
        localLoginData = new LocalLoginData(this);


        //location

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Aplikasi ini membutuhkan akses lokasi, Buka Setting dan Aktifkan Layanan Lokasi?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        EdtUser = (EditText) findViewById(R.id.edtUser);
        //EdtUser.setTypeface(font);

        EdtPassword = (EditText) findViewById(R.id.edtPwd);
        //EdtPassword.setTypeface(font);
        BtnLogin = (Button) findViewById(R.id.btn_Login);
        //BtnLogin.setTypeface(f);
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for debug only

                //EdtUser.setText("KRISHNA", TextView.BufferType.EDITABLE);
                //EdtPassword.setText("KRISHNA01", TextView.BufferType.EDITABLE);

                final String us = EdtUser.getText().toString();
                final String pw = EdtPassword.getText().toString();


                try {
                    if (EdtUser.getText().toString().trim().isEmpty()) {
                        EdtUser.setError("Username harus diisi");
                    } else if (EdtPassword.getText().toString().trim().isEmpty()) {
                        EdtPassword.setError("Password harus diisi");
                    } else if (us.length() > 0 && pw.length() > 0) {
                        progressDialog = ProgressDialog.show(LoginActivity.this,"","Loading..",true,false);
                        //SQLite sql = new SQLite(LoginActivity.this);
                        //sql.open();
                        NetworkUtils utils = new NetworkUtils(LoginActivity.this);
                        if(utils.isConnectingToInternet())
                        {
                            loginTask = new Login();
                            loginTask.delegate = LoginActivity.this;
                            loginTask.execute(Data.loginUrl + us + "/" + pw);
                        }
                        else{
                            if (localLoginData.doLocalLogin(us,pw)) {//succes
                                progressDialog.dismiss();
                                Intent menu = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(menu);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                loginFailedMessage();
                            }
                        }

                        //sql.close();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });

        InfoUser = (ImageView) findViewById(R.id.info1);
        InfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdtUser.setError("USERNAME");
                EdtUser.requestFocus();
            }
        });

        InfoPassword = (ImageView) findViewById(R.id.info2);
        InfoPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdtPassword.setError("PASSWORD");
                EdtPassword.requestFocus();
            }
        });
    }
    void loginFailedMessage(){
        AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this);
        build.setTitle("Login Gagal");
        build.setMessage("Periksa kembali username dan password Anda")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EdtUser.setText("");
                        EdtPassword.setText("");
                    }
                });
        message = build.create();
        message.show();
    }

    @Override
    public void processFinish(String output) {
        Log.e("loginResponse",output);
        try {
            JSONObject object = new JSONObject(output);
            if(object.getString("Status").equals("BERHASIL")){
                localLoginData.saveLoginData(object.getJSONArray("Data").getJSONObject(0));
                Intent menu = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(menu);
                finish();
            }
            else{
                loginFailedMessage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            progressDialog.dismiss();
        }

    }
}


