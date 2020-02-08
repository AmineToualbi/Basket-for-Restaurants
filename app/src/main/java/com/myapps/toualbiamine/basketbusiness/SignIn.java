package com.myapps.toualbiamine.basketbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapps.toualbiamine.basketbusiness.Common.Common;
import com.myapps.toualbiamine.basketbusiness.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    EditText restaurantNameInput;
    EditText passwordInput;

    Button signInBtn;
    CheckBox rememberMeCb;
    TextView forgotPassword;

    ProgressBar signInProgressBar;

    final String TAG = "SignInActivity";

    FirebaseDatabase database;
    DatabaseReference tableUser;

    String signInRestaurantName;
    String signInPassword;

    Dialog forgotPasswordPopup;
    Button okButton;
    ImageButton closePopupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        restaurantNameInput = (MaterialEditText) findViewById(R.id.restaurantSignIn);
        passwordInput = (MaterialEditText) findViewById(R.id.passwordSignIn);

        signInBtn = (Button) findViewById(R.id.signInBtn);
        rememberMeCb = (CheckBox) findViewById(R.id.rememberMeCb);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        signInProgressBar = (ProgressBar) findViewById(R.id.signInProgressBar);
        signInProgressBar.setVisibility(View.INVISIBLE);

        forgotPasswordPopup = new Dialog(this);

        //Initialize Firebase.
        database = FirebaseDatabase.getInstance();
        tableUser = database.getReference("Users");         //Get the table Users created in the db.

        //Initialize Paper => library to use save key-value pairs on phone storage = easier than SharedPreferences.
        Paper.init(this);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInProgressBar.setVisibility(View.VISIBLE);
                loginUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordPopup();
            }
        });
    }

    private void loginUser() {
        signInRestaurantName = restaurantNameInput.getText().toString();
        signInPassword = passwordInput.getText().toString();

        if(TextUtils.isEmpty(signInRestaurantName) || TextUtils.isEmpty(signInPassword)) {
            Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_SHORT).show();
            signInProgressBar.setVisibility(View.INVISIBLE);
        }
        else {
            checkUserInDB();
        }
    }

    private void checkUserInDB() {
        //Get the data by querying the database.
        tableUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Data inputted by the user => credentials.
                signInRestaurantName = restaurantNameInput.getText().toString();
                signInPassword = passwordInput.getText().toString();

                //Check if email inputted is .edu & exists in DB.
                if(!(signInRestaurantName.equals("")) && dataSnapshot.child(signInRestaurantName).exists()) {
                    //Get user information.
                    User user = dataSnapshot.child(signInRestaurantName).getValue(User.class);

                    if(user.getIsStaff() && user.getPassword().equals(signInPassword)) {

                        signInProgressBar.setVisibility(View.INVISIBLE);

                        if (rememberMeCb.isChecked()) {      //Save email & password to remember.
                            Paper.book().write(Common.USER_KEY, signInRestaurantName);
                            Paper.book().write(Common.PWD_KEY, signInPassword);
                            Paper.book().write(Common.NAME_KEY, user.getName());
                        }

                        Intent goToHome = new Intent(getApplicationContext(), Home.class);
                        Common.currentUser = user;
                        startActivity(goToHome);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Your email and/or password may be incorrect!", Toast.LENGTH_SHORT).show();
                        signInProgressBar.setVisibility(View.INVISIBLE);

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Your email and/or password may be incorrect!", Toast.LENGTH_SHORT).show();
                    signInProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Your email and/or password may be incorrect!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordPopup() {
        //Show the popup.
        forgotPasswordPopup.setContentView(R.layout.popup_forgot_password);
        forgotPasswordPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forgotPasswordPopup.show();

        okButton = (Button) forgotPasswordPopup.findViewById(R.id.okBtn);
        closePopupButton = (ImageButton) forgotPasswordPopup.findViewById(R.id.closePopup);

        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordPopup.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordPopup.dismiss();
            }
        });
    }

}
