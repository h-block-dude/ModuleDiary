package com.example.hamzah.modulediary.ModulesList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hamzah.modulediary.R;
import com.example.hamzah.modulediary.SetUp.ModulePicker.ModulesSetUpBean;

import java.util.ArrayList;

/**
 * handles the view for each list item in modules
 */

public class ModulesListAdapter extends ArrayAdapter<ModulesSetUpBean> {


    Context mContext;

    private static class ViewHolder {
        TextView tvModuleName;
        TextView tvModuleCode;
        TextView tvCredits;
    }

    public ModulesListAdapter(@NonNull Context context, ArrayList<ModulesSetUpBean> data) {
        super(context, R.layout.modules_list_row_item, data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ModulesSetUpBean modulesSetUpBean = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.modules_list_row_item, parent, false);

            viewHolder.tvModuleName = (TextView) convertView.findViewById(R.id.tvModulesListTitle);
            viewHolder.tvModuleCode = (TextView) convertView.findViewById(R.id.tvModulesListCode);
            viewHolder.tvCredits = (TextView) convertView.findViewById(R.id.tvModulesListCredits);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set text for the fields
        viewHolder.tvModuleCode.setText(modulesSetUpBean.getId());
        viewHolder.tvModuleName.setText(modulesSetUpBean.getTitle());
        viewHolder.tvCredits.setText("Credits: " + modulesSetUpBean.getCredits());

        return convertView;
    }
}
