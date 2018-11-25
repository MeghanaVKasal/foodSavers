package com.frankegan.foodsavers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login_Activity extends AppCompatActivity {

    Button loginButton;
    EditText usernameText;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        loginButton = (Button)findViewById(R.id.button);
        usernameText   = (EditText)findViewById(R.id.usernameText);
        password = (EditText)findViewById(R.id.passwordText);

        loginButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Log.v("EditText", usernameText.getText().toString());
                        Log.v("EditText", password.getText().toString());
                    }
                });
    }
}
