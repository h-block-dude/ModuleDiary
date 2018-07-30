package com.example.hamzah.modulediary.MyStory;

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
 * sets up the list of stories using individual row handling
 */
public class StoriesAdapter extends ArrayAdapter<StoryBean> {

    private static class ViewHolder {
        TextView tvDay;
        TextView tvTags;
        TextView tvComment;
        TextView tvDate;
    }



    public StoriesAdapter(@NonNull Context context, ArrayList<StoryBean> data) {
        super(context, R.layout.stories_list_row_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final StoryBean storyBean = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.stories_list_row_item, parent, false);

            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tvStoryDate);
            viewHolder.tvDay = (TextView)convertView.findViewById(R.id.tvStoryDay);
            viewHolder.tvTags = (TextView)convertView.findViewById(R.id.tvStoryTags);
            viewHolder.tvComment = (TextView)convertView.findViewById(R.id.tvStoryComment);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        String tags = "";
        if (!storyBean.getTags().get(0).equalsIgnoreCase("none")) {
            for (int i = 0; i < storyBean.getTags().size(); i++ ) {
                if(i != storyBean.getTags().size() -1) {
                    tags = tags + storyBean.getTags().get(i) + ", ";
                } else {
                    tags = tags + storyBean.getTags().get(i);
                }
            }
            viewHolder.tvTags.setText("Tags: " + tags);
        } else {
            viewHolder.tvTags.setText("No tags selected");
        }


        viewHolder.tvDay.setText(storyBean.getDay());
        viewHolder.tvDate.setText(storyBean.getDate());
        if (storyBean.getComment().equalsIgnoreCase("")){
            viewHolder.tvComment.setText("No comment");
        } else {
            viewHolder.tvComment.setText("Comment: " + storyBean.getComment());
        }

        return convertView;
    }
}
