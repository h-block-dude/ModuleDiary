package com.example.hamzah.modulediary.MyStory;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;

/**
 * contains list of stories
 */
public class MyStoryFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView lvStories;
    private ArrayList<StoryBean> storyArray;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String uID;
    @BindView(R.id.rlBottomSheetStory) View mLayoutBottomSheet;
    @BindView(R.id.tvTagsBottomSheetStory) TextView tvTags;
    @BindView(R.id.tvStoryBottomSheetDate) TextView tvDate;
    @BindView(R.id.tvCommentBottomSheetStory) TextView tvComment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private StoriesAdapter adapter;
    @BindView(R.id.tvStoryNoStory) TextView tvNoStory;

    public MyStoryFragment() {
        // Required empty public constructor
    }

    public static MyStoryFragment newInstance(String param1, String param2) {
        MyStoryFragment fragment = new MyStoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();

        View rootView = inflater.inflate(R.layout.fragment_my_story, container, false);
        ButterKnife.bind(this, rootView);
        lvStories = (ListView)rootView.findViewById(R.id.lvStories);


        mBottomSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setUpList();

        lvStories.setOnItemClickListener(this);

        return rootView;
    }

    private void setUpList() {
        mDatabase.child(USERS)
                .child(uID)
                .child("story")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() != 0) {
                            lvStories.setVisibility(View.VISIBLE);
                            tvNoStory.setVisibility(View.GONE);
                            storyArray = new ArrayList<>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                StoryBean story = postSnapshot.getValue(StoryBean.class);
//                            mood.setId(postSnapshot.getKey());
                                storyArray.add(story);
                            }
                            adapter = new StoriesAdapter(getActivity(), storyArray);
                            lvStories.setAdapter(adapter);
                        } else{
                            lvStories.setVisibility(View.GONE);
                            tvNoStory.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //handles a list item click and expands bottomsheet accordingly
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        StoryBean ListStory = (StoryBean) adapterView.getItemAtPosition(i);
        String sDate = ListStory.getDate();
        Query findDate = mDatabase
                .child(USERS)
                .child(uID)
                .child("story")
                .orderByKey().equalTo(sDate);

        findDate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        StoryBean story = postSnapshot.getValue(StoryBean.class);
                        setTags(story);
                        tvDate.setText(story.getDate());
                        if (story.getComment().equalsIgnoreCase("")){
                            tvComment.setText("No comment");
                        } else{
                            tvComment.setText(story .getComment());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setTags(StoryBean story) {
        String tags = "";
        if (!story.getTags().get(0).equalsIgnoreCase("none")) {
            for (int i = 0; i < story.getTags().size(); i++ ) {
                if(i != story.getTags().size() -1) {
                    tags = tags + story.getTags().get(i) + ", ";
                } else {
                    tags = tags + story.getTags().get(i);
                }
            }
            tvTags.setText(tags);
        } else {
            tvTags.setText("No tags selected");
        }
    }
}
