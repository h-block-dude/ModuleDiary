package com.example.hamzah.modulediary.Deadlines;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hamzah.modulediary.R;

import java.util.ArrayList;

/**
 * sets the views for the list of deadlines
 */

public class DeadlinesAdapter extends ArrayAdapter<DeadlinesBean> {

    private static class ViewHolder {
        TextView tvType;
        TextView tvDate;
        TextView tvTitle;
        TextView tvModuleName;
        TextView tvModuleCode;
    }

    public DeadlinesAdapter(@NonNull Context context, ArrayList<DeadlinesBean> data) {
        super(context, R.layout.deadlines_list_row_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        DeadlinesBean deadlines = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.deadlines_list_row_item, parent, false);

            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tvDeadlineDate);
            viewHolder.tvType = (TextView)convertView.findViewById(R.id.tvDeadlineType);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvDeadlineTitle);
            viewHolder.tvModuleCode = (TextView)convertView.findViewById(R.id.tvDeadlineModuleCode);
            viewHolder.tvModuleName = (TextView)convertView.findViewById(R.id.tvDeadlineModuleName);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvDate.setText("Due date: " + deadlines.getDate());
        viewHolder.tvModuleCode.setText(deadlines.getModuleId());
        viewHolder.tvModuleName.setText(deadlines.getModuleTitle());
        if (deadlines.getType().equalsIgnoreCase("Coursework")){
            viewHolder.tvType.setText("CW");
        } else {
            viewHolder.tvType.setText(deadlines.getType());
        }
        viewHolder.tvTitle.setText(deadlines.getTitle());

        return convertView;
    }
}
