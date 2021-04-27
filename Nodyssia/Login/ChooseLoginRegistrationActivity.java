package com.Spiros.Nodyssia.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.Spiros.Nodyssia.R;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        Button mSignIn = findViewById(R.id.signIn);
        Button mRegister = findViewById(R.id.register);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this, RegisterActivity.class);
                startActivity(intent);
                return;
            }
        });
    }
}
