package com.myapps.toualbiamine.basketbusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button signInBtn;
    TextView appSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInBtn = (Button) findViewById(R.id.btnSignIn);
        appSlogan = (TextView) findViewById(R.id.slogan);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/restaurant_font.otf");
        appSlogan.setTypeface(font);
        signInBtn.setTypeface(font);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignIn = new Intent(getApplicationContext(), SignIn.class);
                startActivity(goToSignIn);
            }
        });
    }

}
