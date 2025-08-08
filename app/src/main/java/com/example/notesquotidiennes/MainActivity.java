package com.example.notesquotidiennes;

import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.widget.Button;

import com.example.notesquotidiennes.database.DatabaseHelper;
import com.example.notesquotidiennes.fragments.LoginFragment;
import com.example.notesquotidiennes.fragments.SignupFragment;

public class MainActivity extends AppCompatActivity {

    private Button loginToggle, signupToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Création de la base de données (test)
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        loginToggle = findViewById(R.id.login_toggle);
        signupToggle = findViewById(R.id.signup_toggle);

        // Par défaut, afficher LoginFragment
        if (savedInstanceState == null) {
            showLogin();
        }

        loginToggle.setOnClickListener(v -> {
            if (!loginToggle.isSelected()) {
                showLogin();
            }
        });

        signupToggle.setOnClickListener(v -> {
            if (!signupToggle.isSelected()) {
                showSignup();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void showLogin() {
        loginToggle.setSelected(true);
        signupToggle.setSelected(false);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private void showSignup() {
        loginToggle.setSelected(false);
        signupToggle.setSelected(true);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, new SignupFragment())
                .commit();
    }
}

