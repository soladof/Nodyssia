package com.Spiros.Nodyssia.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.Spiros.Nodyssia.ChatList.ChatListAdapter;
import com.Spiros.Nodyssia.Matches.MatchesAdapter;
import com.Spiros.Nodyssia.Matches.MatchesObject;
import com.Spiros.Nodyssia.R;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends Fragment {

    private RecyclerView mMatch, mChat;
    private RecyclerView.Adapter mMatchesAdapter, mChatAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager, mChatLayoutManager;

    private String currentUserID;
    private View view;

    public MatchesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_matches, container, false);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUserMatchId();
        getChats();
        getNewMatches();

        return view;
    }

    private void getNewMatches(){
        mMatch = view.findViewById(R.id.match);
        mMatch.setNestedScrollingEnabled(false);
        mMatch.setHasFixedSize(false);
        mMatchesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mMatch.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());
        mMatch.setAdapter(mMatchesAdapter);
    }

    private void getChats(){
        mChat = view.findViewById(R.id.chat);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mChat.addItemDecoration(dividerItemDecoration);
        mChatAdapter = new ChatListAdapter(getDataSetChat(), getContext());
        mChat.setAdapter(mChatAdapter);
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        matchDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) {
        for(int i = 0; i < resultsMatches.size(); i++){
            if(resultsMatches.get(i).getUserId().equals(key))
                return;
        }

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String  userId = dataSnapshot.getKey(),
                            name = "",
                            profileImageUrl = "",
                            chatId = "";

                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    if(dataSnapshot.child("connections").child("matches").child(FirebaseAuth.getInstance().getUid()).child("ChatId").getValue()!=null)
                        chatId = dataSnapshot.child("connections").child("matches").child(FirebaseAuth.getInstance().getUid()).child("ChatId").getValue().toString();



                    for(int i = 0; i < resultsMatches.size(); i++){
                        if(resultsMatches.get(i).getUserId().equals(userId))
                            return;
                    }

                    MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, chatId, "");
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                    if(!chatId.equals(""))
                        FetchLastMessage(chatId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void FetchLastMessage(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(key);
        Query query = userDb.orderByKey().limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String chatId = dataSnapshot.getRef().getKey();
                    DataSnapshot messageNode = dataSnapshot.getChildren().iterator().next();
                    String messageId = messageNode.getKey();
                    String message = "";

                    if(dataSnapshot.child(messageId).child("text").getValue()!=null)
                        message = dataSnapshot.child(messageId).child("text").getValue().toString();

                    if(message.equals(""))
                        return;


                    for(int i = 0; i < resultsMatches.size(); i++){
                        if(resultsMatches.get(i).getChatId().equals(chatId)) {
                            resultsMatches.get(i).setLastMessage(message);
                            for(int j = 0; j < resultsChat.size(); j++){
                                if(resultsChat.get(j).getChatId().equals(chatId)){
                                    resultsMatches.get(i).setLastMessage(message);
                                    mChatAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                            resultsChat.add(resultsMatches.get(i));
                            mChatAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private ArrayList<MatchesObject> resultsChat = new ArrayList<>();
    private List<MatchesObject> getDataSetChat() {
        return resultsChat;
    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }

}