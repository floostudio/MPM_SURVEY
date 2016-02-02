package com.floo.mpm_survey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.Toast;

import com.floo.database.SQLite;


public class LoginActivity extends ActionBarActivity {
    EditText user, pwd;
    Button login;
    ImageView info1, info2;
    AlertDialog pesan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);



        Typeface font=Typeface.createFromAsset(getAssets(), "fonts/SFCompactDisplay-Regular.ttf");
        Typeface f=Typeface.createFromAsset(getAssets(), "fonts/SFCompactDisplay-Bold.ttf");

        //location
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS Disabled, Buka Setting dan Aktifkan GPS?")
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
        else
        {
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_LONG).show();
        }



        user = (EditText) findViewById(R.id.edtUser);
        user.setTypeface(font);

        pwd = (EditText) findViewById(R.id.edtPwd);
        pwd.setTypeface(font);
        login = (Button) findViewById(R.id.btn_Login);
        login.setTypeface(f);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String us = user.getText().toString();
                final String pw = pwd.getText().toString();
                try {
                    if (user.getText().toString().trim().isEmpty()) {
                        user.setError("Please enter valid username");
                    } else if (pwd.getText().toString().trim().isEmpty()) {
                        pwd.setError("Please enter valid password");
                    } else if (us.length() > 0 && pw.length() > 0) {
                        SQLite sql = new SQLite(LoginActivity.this);
                        sql.open();
                        if (sql.Login(us, pw)) {
                            AlertDialog.Builder buil = new AlertDialog.Builder(LoginActivity.this);
                            buil.setMessage("Successfully Logged In").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent menu = new Intent(LoginActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(menu);
                                }
                            });
                            pesan = buil.create();
                            pesan.show();
                        } else {
                            AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this);
                            build.setMessage("Sorry your Failed Login").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    user.setText("");
                                    pwd.setText("");
                                }
                            });
                            pesan = build.create();
                            pesan.show();
                        }
                        sql.close();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });

        info1 = (ImageView) findViewById(R.id.info1);
        info1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setError("USERNAME");
                user.requestFocus();
            }
        });

        info2 = (ImageView) findViewById(R.id.info2);
        info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd.setError("PASSWORD");
                pwd.requestFocus();
            }
        });


    }



}


