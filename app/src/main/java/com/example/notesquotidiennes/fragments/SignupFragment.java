package com.example.notesquotidiennes.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.notesquotidiennes.R;
import com.example.notesquotidiennes.database.DatabaseHelper;

public class SignupFragment extends Fragment {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        Button signupButton = view.findViewById(R.id.signup_button);

        dbHelper = new DatabaseHelper(getActivity());

        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.addUser(username, password)) {
                Toast.makeText(getActivity(), "Inscription réussie", Toast.LENGTH_SHORT).show();
                // Aller directement à LoginFragment avec les champs pré-remplis
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);
                LoginFragment loginFragment = new LoginFragment();
                loginFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, loginFragment)
                        .commit();
            } else {
                Toast.makeText(getActivity(), "Nom d'utilisateur déjà utilisé", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}