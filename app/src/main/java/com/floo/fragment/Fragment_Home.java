package com.floo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.floo.mpm_survey.MainActivity;
import com.floo.mpm_survey.R;
import com.floo.mpm_survey.RespondenceActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Home extends Fragment {
    HashMap<String, String>map;
    ArrayList<HashMap<String, String>> mylist;
    String[] atas, tengah, bawah;
    ListView isi;
    SimpleAdapter Adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");

        isi = (ListView) v.findViewById(R.id.list_isi);

        atas = new String[]{"SAMPLE SURVEY 1", "SAMPLE SURVEY 2", "SAMPLE SURVEY 3",
                "SAMPLE SURVEY 4", "SAMPLE SURVEY 5", "SAMPLE SURVEY 6",
                "SAMPLE SURVEY 7", "IT'S A VERY VERY LONG LONG SAMPLE NAME QUIZ 8",
                "SAMPLE SURVEY 9", "SAMPLE SURVEY 10", "SAMPLE SURVEY 11"};

        tengah = new String[]{"Total Question : 30", "Total Question : 30", "Total Question : 30",
                "Total Question : 30", "Total Question : 30", "Total Question : 30",
                "Total Question : 30", "Total Question : 30",
                "Total Question : 30", "Total Question : 30", "Total Question : 30"};

        bawah = new String[]{"22/7/2015 | by admin", "22/7/2015 | by admin", "22/7/2015 | by admin",
                "22/7/2015 | by admin", "22/7/2015 | by admin", "22/7/2015 | by admin",
                "22/7/2015 | by admin", "22/7/2015 | by admin",
                "22/7/2015 | by admin", "22/7/2015 | by admin", "22/7/2015 | by admin"};

        mylist = new ArrayList<HashMap<String, String>>();
        for (int i=0; i < atas.length; i++){
            map = new HashMap<String, String>();
            map.put("Atasss", atas[i]);
            map.put("Tengahhh", tengah[i]);
            map.put("Bawahhh", bawah[i]);

            mylist.add(map);
        }

        Adapter = new SimpleAdapter((MainActivity)getActivity(), mylist, R.layout.list_home,
                new String[]{"Atasss","Tengahhh","Bawahhh"}, new int[]{R.id.txt_atas, R.id.txt_tengah, R.id.txt_bawah});
        isi.setAdapter(Adapter);

        isi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent res=new Intent((MainActivity)getActivity(), RespondenceActivity.class);
                startActivity(res);
            }
        });


                return v;
    }
}
