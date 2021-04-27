package com.Spiros.Nodyssia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.Spiros.Nodyssia.Cards.cardObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ZoomCardActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button btnSubmit;
    private TextView    mName,mJob,mAbout,ratingAv;
    private ImageView mImage,backBtn;
    private cardObject mCardObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_card);


        Intent i = getIntent();
        mCardObject = (cardObject)i.getSerializableExtra("cardObject");

        mName = findViewById(R.id.name);
        mJob = findViewById(R.id.job);
        mAbout = findViewById(R.id.about);
        mImage = findViewById(R.id.image);
        ratingAv = findViewById(R.id.ratingAverage);
        backBtn = findViewById(R.id.bck);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.rateSubmit);

        ratingAv.setText(mCardObject.getProfileRating()+"/5.0");
        ratingBar.setRating(Float.valueOf(mCardObject.getProfileRating()));
        mName.setText(mCardObject.getName() + ", " + mCardObject.getAge());
        mJob.setText(mCardObject.getJob());
        mAbout.setText(mCardObject.getAbout());

        if(!mCardObject.getProfileImageUrl().equals("default"))
            Glide.with(getApplicationContext()).load(mCardObject.getProfileImageUrl()).into(mImage);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRating(String.valueOf(ratingBar.getRating()));
            }
        });
    }

    private void submitRating(final String rating) {
        final DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(mCardObject.getUserId());
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String raterID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (dataSnapshot.exists()){
                    if(!dataSnapshot.child("ratings").child(raterID).exists()) {
                        userDb.child("ratings").child(raterID).setValue(rating);
                        Toast.makeText(ZoomCardActivity.this,
                                "You submitted "+ rating +" rating for" + mCardObject.getName(),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ZoomCardActivity.this,
                                "You already voted for this member",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
