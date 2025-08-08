package com.example.notesquotidiennes.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notesquotidiennes.R;
import java.io.File;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    public interface OnNoteActionListener {
        void onEdit(int noteId);
        void onDelete(int noteId);
        void onView(int noteId);
    }

    private List<NoteItem> notes;
    private OnNoteActionListener listener;

    public NoteAdapter(List<NoteItem> notes, OnNoteActionListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteItem note = notes.get(position);
        holder.title.setText(note.title);

        // Affichage de la photo
        if (note.photoPath != null && !note.photoPath.isEmpty()) {
            holder.photo.setImageURI(Uri.fromFile(new File(note.photoPath)));
        } else {
            holder.photo.setImageResource(R.drawable.ic_photo_24);
        }

        holder.title.setOnClickListener(v -> listener.onView(note.id));
        holder.editIcon.setOnClickListener(v -> listener.onEdit(note.id));
        holder.deleteIcon.setOnClickListener(v -> listener.onDelete(note.id));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView title;
        ImageButton editIcon, deleteIcon;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.note_photo);
            title = itemView.findViewById(R.id.note_title);
            editIcon = itemView.findViewById(R.id.edit_icon);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
        }
    }

    public static class NoteItem {
        public int id;
        public String title;
        public String photoPath;

        public NoteItem(int id, String title, String photoPath) {
            this.id = id;
            this.title = title;
            this.photoPath = photoPath;
        }
    }
}