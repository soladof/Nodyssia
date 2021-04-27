package com.Spiros.Nodyssia.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.Spiros.Nodyssia.Cards.cardAdapter;
import com.Spiros.Nodyssia.Cards.cardObject;
import com.Spiros.Nodyssia.R;
import com.Spiros.Nodyssia.SendNotification;
import com.Spiros.Nodyssia.ZoomCardActivity;

import java.util.ArrayList;
import java.util.List;

public class CardFragment  extends Fragment {

    private cardAdapter cardAdapter;
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference usersDb;

    List<cardObject> rowItems;
    View view;

    public CardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_card, container, false);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
            return view;
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<>();

        cardAdapter = new cardAdapter(getContext(), R.layout.item_card, rowItems );

        final SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);

        flingContainer.setAdapter(cardAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                //rowItems.remove(0);
                rowItems.add(rowItems.remove(0));
                cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                cardObject obj = (cardObject) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cardObject obj = (cardObject) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                //isConnectionMatch(userId);
                makeConnectionMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                cardObject cardObject = (cardObject) dataObject;
                Intent i = new Intent(getContext(), ZoomCardActivity.class);
                i.putExtra("cardObject", cardObject);
                startActivity(i);
            }
        });

        FloatingActionButton fabLike = view.findViewById(R.id.fabLike);
        FloatingActionButton fabNope = view.findViewById(R.id.fabNope);
        fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(rowItems.size()!=0)
                    flingContainer.getTopCardListener().selectRight();
            }
        });
        fabNope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(rowItems.size()!=0)
                    flingContainer.getTopCardListener().selectLeft();
            }
        });

        return view;
    }
    private void makeConnectionMatch(String userId) {
        Toast.makeText(getContext(), "Matched! Contact your new match for more info.", Toast.LENGTH_LONG).show();
        String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

        usersDb.child(userId).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
        usersDb.child(currentUId).child("connections").child("matches").child(userId).child("ChatId").setValue(key);

        SendNotification sendNotification = new SendNotification();
        sendNotification.SendNotification("Someone is interested on your services! Check your inbox!", "Nodyssia alert!", userId);
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getContext(), "Someone is interested on your services!", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    SendNotification sendNotification = new SendNotification();
                    sendNotification.SendNotification("Someone is interested on your services!", "Nodyssia alert!", dataSnapshot.getKey());

                    Snackbar.make(view.findViewById(R.id.layout), "Someone is interested on your services!", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

   private String  userInterest;

    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("interest").getValue() != null)
                        userInterest = dataSnapshot.child("interest").getValue().toString();

                    rowItems.clear();
                    cardAdapter.notifyDataSetChanged();
                    getUsersInfo();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUsersInfo(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("sex").getValue() != null) {
                    if(dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid()))
                        return;

                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("matches").hasChild(currentUId)) {
                      //  if(dataSnapshot.child("sex").getValue().toString().equals(userInterest) || userInterest.equals("Both")){
                            String  name = "",
                                    age = "",
                                    job = "",
                                    about = "",
                                    userSex = "",
                                    profileImageUrl = "default";

                         double average=0.0,total=0.0,rating=0.0;
                         int count=0;


                        if(dataSnapshot.child("name").getValue()!=null)
                                name = dataSnapshot.child("name").getValue().toString();
                            if(dataSnapshot.child("sex").getValue()!=null)
                                userSex = dataSnapshot.child("sex").getValue().toString();
                            if(dataSnapshot.child("age").getValue()!=null)
                                age = dataSnapshot.child("age").getValue().toString();
                            if(dataSnapshot.child("job").getValue()!=null)
                                job = dataSnapshot.child("job").getValue().toString();
                            if(dataSnapshot.child("about").getValue()!=null)
                                about = dataSnapshot.child("about").getValue().toString();
                            if (dataSnapshot.child("profileImageUrl").getValue()!=null)
                                profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                            if(dataSnapshot.child("ratings").getValue()!=null)
                            {
                                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                    rating = Double.valueOf(ds.child("ratings").getValue().toString());
                                    total += rating;
                                    count ++;
                                }
                                average = total / count;
                            }

                            cardObject item = new cardObject(dataSnapshot.getKey(), name, age, about, job, profileImageUrl,String.valueOf(average));

                            for(int i = 0; i < rowItems.size();i++)
                                if(rowItems.get(i) == item)
                                    return;

                            rowItems.add(item);
                            cardAdapter.notifyDataSetChanged();
                      //  }
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}