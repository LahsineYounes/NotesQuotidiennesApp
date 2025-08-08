package com.example.notesquotidiennes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.notesquotidiennes.fragments.NotesListFragment;
import com.example.notesquotidiennes.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Affiche Notes par défaut
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new NotesListFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            if (item.getItemId() == R.id.nav_notes) {
                selectedFragment = new NotesListFragment();
            } else {
                selectedFragment = new ProfileFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, selectedFragment)
                    .commit();
            return true;
        });
    }
}