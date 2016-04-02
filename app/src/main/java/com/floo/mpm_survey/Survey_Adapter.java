package com.floo.mpm_survey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Floo on 2/3/2016.
 */
public class Survey_Adapter extends BaseAdapter {
    Context context;
    ArrayList<Survey> listData;

    public ArrayList<Survey> getListData() {
        return listData;
    }

    public Survey_Adapter(Context context, ArrayList<Survey> listData){
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    class ViewHolder {
        private TextView txtSurveyName,txtDivisi, txtUploadedInfo, txtLastModified, txtidParsing;
        private LinearLayout row;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_home1,null);
            viewHolder = new ViewHolder();
            viewHolder.txtidParsing = (TextView) view.findViewById(R.id.txt_IDPARSING);
            viewHolder.txtSurveyName = (TextView) view.findViewById(R.id.txt_surveyAtas);
            viewHolder.txtDivisi = (TextView) view.findViewById(R.id.txt_surverTengah);
            viewHolder.row = (LinearLayout)view.findViewById(R.id.surveyRow);
            viewHolder.txtUploadedInfo= (TextView) view.findViewById(R.id.uploadedInfo);
            viewHolder.txtLastModified = (TextView) view.findViewById(R.id.txt_surveyBawah);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        if(position%2==0)
            viewHolder.row.setBackgroundResource(R.color.row_color);
        Survey survey = listData.get(position);
        String Sid = survey.getSurvey_id();
        viewHolder.txtidParsing.setText(Sid);
        String Ssurveynama = survey.getSurvey_nama();
        viewHolder.txtSurveyName.setText(Ssurveynama);
        String Sdivisi = survey.getDivisi()+" | "+ survey.getJenis_respondence();
        viewHolder.txtDivisi.setText(Sdivisi);
        //String Sjenisresponce = survey.getJenis_respondence();
        //viewHolder.txtJenisRespon.setText(Sjenisresponce);
        String Slastmodified = survey.getLast_modified();
        viewHolder.txtLastModified.setText(Slastmodified);

        String uploadedInfo = "Total Upload: "+survey.getUploadedCount()+" / "+survey.getTotalRespondenCount();
        viewHolder.txtUploadedInfo.setText(uploadedInfo);
        return view;
    }
}
