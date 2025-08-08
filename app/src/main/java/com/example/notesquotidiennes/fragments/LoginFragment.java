package com.example.notesquotidiennes.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.notesquotidiennes.HomeActivity;
import com.example.notesquotidiennes.R;
import com.example.notesquotidiennes.database.DatabaseHelper;

public class LoginFragment extends Fragment {

    private EditText usernameEditText, passwordEditText;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.login_button);

        dbHelper = new DatabaseHelper(getActivity());

        // Pré-remplir les champs si on a reçu des arguments
        Bundle args = getArguments();
        if (args != null) {
            String prefillUsername = args.getString("username", "");
            String prefillPassword = args.getString("password", "");
            usernameEditText.setText(prefillUsername);
            passwordEditText.setText(prefillPassword);
            loginButton.requestFocus();
        }

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkUser(username, password)) {
                Toast.makeText(getActivity(), "Connexion réussie", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Identifiants incorrects", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}