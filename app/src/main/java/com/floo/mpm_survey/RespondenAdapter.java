package com.floo.mpm_survey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Floo on 2/3/2016.
 */
public class RespondenAdapter extends BaseAdapter {

    Context context;

    public ArrayList<Responden> getListData() {
        return listData;
    }


    private ArrayList<Responden> listData;

    public RespondenAdapter(Context context, ArrayList<Responden> listData){
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
        private TextView idParsingResponden, nameResponden, statusParsingResponden;
        private ImageView lockIcon,finalIcon;
        private RelativeLayout row;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_frag_ketiga,null);
            viewHolder = new ViewHolder();
            viewHolder.idParsingResponden= (TextView) view.findViewById(R.id.txt_idParsingResponden);
            viewHolder.nameResponden = (TextView) view.findViewById(R.id.txt_text);
            //viewHolder.statusParsingResponden = (TextView) view.findViewById(R.id.txt_statusParsingResponden);
            viewHolder.row = (RelativeLayout)view.findViewById(R.id.rowWrapper);
            viewHolder.lockIcon = (ImageView) view.findViewById(R.id.imv_lock);
            viewHolder.finalIcon = (ImageView) view.findViewById(R.id.imv_final);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        if(position%2==0)
            viewHolder.row.setBackgroundResource(R.color.row_color);
        Responden responden = listData.get(position);
        String id_Respondence = responden.getID_RESPONDENCE();
        viewHolder.idParsingResponden.setText(id_Respondence);
        String nama_Respondence = responden.getNAMA_RESPONDENCE();
        viewHolder.nameResponden.setText(nama_Respondence);
        boolean isLocked = responden.isLOCKED();
        boolean isFinal = responden.isFINAL();
        //viewHolder.statusParsingResponden.setText(status_respondence);
        if(!isLocked) {
            viewHolder.lockIcon.setVisibility(View.GONE);
        } else {
            viewHolder.lockIcon.setVisibility(View.VISIBLE);
        }
        if(!isFinal) {
            viewHolder.finalIcon.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.finalIcon.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
