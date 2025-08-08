package com.example.notesquotidiennes.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notesquotidiennes.R;
import com.example.notesquotidiennes.adapters.NoteAdapter;
import com.example.notesquotidiennes.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class NotesListFragment extends Fragment {

    private RecyclerView notesRecycler;
    private Button addNoteButton;
    private DatabaseHelper dbHelper;
    private NoteAdapter adapter;
    private List<NoteAdapter.NoteItem> notesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        notesRecycler = view.findViewById(R.id.notes_recycler);
        addNoteButton = view.findViewById(R.id.add_note_button);
        dbHelper = new DatabaseHelper(getActivity());

        notesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadNotes();

        addNoteButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new NoteEditFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadNotes() {
        notesList.clear();
        // Remplace 1 par l'ID utilisateur réel
        List<NoteAdapter.NoteItem> notesFromDb = dbHelper.getAllNoteItems(1);
        notesList.addAll(notesFromDb);
        adapter = new NoteAdapter(notesList, new NoteAdapter.OnNoteActionListener() {
            @Override
            public void onEdit(int noteId) {
                Bundle bundle = new Bundle();
                bundle.putInt("note_id", noteId);
                NoteEditFragment editFragment = new NoteEditFragment();
                editFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onDelete(int noteId) {
                if (dbHelper.deleteNote(noteId)) {
                    Toast.makeText(getActivity(), "Note supprimée", Toast.LENGTH_SHORT).show();
                    loadNotes();
                } else {
                    Toast.makeText(getActivity(), "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onView(int noteId) {
                Bundle bundle = new Bundle();
                bundle.putInt("note_id", noteId);
                NoteDetailFragment detailFragment = new NoteDetailFragment();
                detailFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        notesRecycler.setAdapter(adapter);
    }
}