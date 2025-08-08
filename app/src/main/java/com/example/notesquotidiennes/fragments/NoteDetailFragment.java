package com.example.notesquotidiennes.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.notesquotidiennes.R;
import com.example.notesquotidiennes.database.DatabaseHelper;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteDetailFragment extends Fragment {

    private TextView titleTextView, contentTextView, reminderTextView;
    private ImageView photoImageView;
    private DatabaseHelper dbHelper;
    private int noteId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        // Toolbar avec flèche de retour
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back); // Assure-toi d'avoir cette icône dans drawable
        toolbar.setTitle("Détail de la note");
        toolbar.setNavigationOnClickListener(v -> {
            // Retour à la liste des notes
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new NotesListFragment())
                    .commit();
        });

        titleTextView = view.findViewById(R.id.title);
        contentTextView = view.findViewById(R.id.content);
        reminderTextView = view.findViewById(R.id.reminder_detail);
        photoImageView = view.findViewById(R.id.photo_detail);
        Button editButton = view.findViewById(R.id.edit_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

        dbHelper = new DatabaseHelper(getActivity());

        if (getArguments() != null) {
            noteId = getArguments().getInt("note_id", 0);
        }

        String[] note = dbHelper.getNoteById(noteId);
        if (note != null) {
            titleTextView.setText(note[0]);
            contentTextView.setText(note[1]);
            String photoPath = note[2];
            String reminderDateStr = note[3];

            if (photoPath != null && !photoPath.isEmpty()) {
                photoImageView.setImageURI(Uri.fromFile(new File(photoPath)));
            } else {
                photoImageView.setImageResource(R.drawable.ic_photo_24);
            }

            if (reminderDateStr != null && !reminderDateStr.equals("0")) {
                long reminderDate = Long.parseLong(reminderDateStr);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                reminderTextView.setText("Rappel : " + sdf.format(new Date(reminderDate)));
            } else {
                reminderTextView.setText("Aucun rappel");
            }
        }

        editButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("note_id", noteId);
            NoteEditFragment editFragment = new NoteEditFragment();
            editFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        deleteButton.setOnClickListener(v -> {
            if (dbHelper.deleteNote(noteId)) {
                Toast.makeText(getActivity(), "Note supprimée", Toast.LENGTH_SHORT).show();
                // Retour à la liste des notes
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, new NotesListFragment())
                        .commit();
            } else {
                Toast.makeText(getActivity(), "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}