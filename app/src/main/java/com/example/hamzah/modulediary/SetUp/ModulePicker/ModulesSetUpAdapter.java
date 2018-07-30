package com.example.hamzah.modulediary.SetUp.ModulePicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.MODULES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;

/**
 * handles list of views, including checkbox
 * also adding and removing from the database
 */

public class ModulesSetUpAdapter extends ArrayAdapter<ModulesSetUpBean>  {

    private ArrayList<ModulesSetUpBean> dataSet;
    Context mContext;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    private static class ViewHolder {
        TextView tvModuleName;
        TextView tvModuleCode;
        TextView tvCredits;
        CheckBox cbModules;
    }

    //interface for credits callback listener
    public interface OnListItemSelectedListener {
        public void onModuleSelected(int credits);
    }

    public ModulesSetUpAdapter(@NonNull Context context, ArrayList<ModulesSetUpBean> data) {
        super(context, R.layout.modules_set_up_row_item, data);
        this.dataSet = data;
        this.mContext = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ModulesSetUpBean modulesSetUpBean = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.modules_set_up_row_item, parent, false);

            viewHolder.tvModuleName = (TextView) convertView.findViewById(R.id.tvModuleSetUpTitle);
            viewHolder.tvModuleCode = (TextView) convertView.findViewById(R.id.tvModuleSetUpCode);
            viewHolder.tvCredits = (TextView) convertView.findViewById(R.id.tvModuleSetUpCredits);
            viewHolder.cbModules = (CheckBox) convertView.findViewById(R.id.cbModulesSetUp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set text for the fields
        viewHolder.tvModuleCode.setText(modulesSetUpBean.getId());
        viewHolder.tvModuleName.setText(modulesSetUpBean.getTitle());
        viewHolder.tvCredits.setText("Credits: " + modulesSetUpBean.getCredits());

        //checkbox, adds and removes modules from database accordingly
        viewHolder.cbModules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                modulesSetUpBean.setChecked(cb.isChecked());

                //update the credits total field
                int credits = 0;
                if(mContext instanceof OnListItemSelectedListener) {
                    if(modulesSetUpBean.isChecked()){
                        credits = modulesSetUpBean.getCredits();
                        addModule(modulesSetUpBean);
                    } else {
                        credits = 0 - modulesSetUpBean.getCredits();
                        removeModule(modulesSetUpBean);
                    }
                    ((OnListItemSelectedListener)mContext).onModuleSelected(credits);
                }
            }
        });

        return convertView;
    }

    //  -- Helper functions --

    private void removeModule(ModulesSetUpBean module) {
        String uID = firebaseAuth.getCurrentUser().getUid();

        mDatabase.child(USERS)
                .child(uID)
                .child(MODULES)
                .child("Y" + Long.toString(module.getYear()))
                .child(module.getSemester())
                .child(module.getId()).setValue(null);
    }

    private void addModule(ModulesSetUpBean module) {
        String uID = firebaseAuth.getCurrentUser().getUid();

        mDatabase.child(USERS)
                .child(uID)
                .child(MODULES)
                .child("Y" + Long.toString(module.getYear()))
                .child(module.getSemester())
                .child(module.getId()).setValue(module);
    }
}
