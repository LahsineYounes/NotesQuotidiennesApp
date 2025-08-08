package com.example.notesquotidiennes.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.notesquotidiennes.R;
import com.example.notesquotidiennes.database.DatabaseHelper;
import com.example.notesquotidiennes.receivers.ReminderReceiver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoteEditFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText titleEditText, contentEditText;
    private ImageView photoPreview;
    private TextView reminderText;

    private String photoPath = null;
    private long reminderDate = 0;
    private int noteId = 0; // 0 = nouvelle note, sinon édition

    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_edit, container, false);

        // Toolbar avec flèche de retour
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back); // Assure-toi d'avoir cette icône dans drawable
        toolbar.setNavigationOnClickListener(v -> {
            // Retour à la liste des notes
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, new NotesListFragment())
                    .commit();
        });

        titleEditText = view.findViewById(R.id.title);
        contentEditText = view.findViewById(R.id.content);
        photoPreview = view.findViewById(R.id.photo_preview);
        Button photoButton = view.findViewById(R.id.photo_button);
        Button reminderButton = view.findViewById(R.id.reminder_button);
        reminderText = view.findViewById(R.id.reminder_text);
        Button saveButton = view.findViewById(R.id.save_button);

        dbHelper = new DatabaseHelper(getActivity());

        // Mode édition
        if (getArguments() != null) {
            noteId = getArguments().getInt("note_id", 0);
            if (noteId != 0) {
                String[] note = dbHelper.getNoteById(noteId);
                if (note != null) {
                    titleEditText.setText(note[0]);
                    contentEditText.setText(note[1]);
                    photoPath = note[2];
                    reminderDate = note[3] != null ? Long.parseLong(note[3]) : 0;
                    if (photoPath != null && !photoPath.isEmpty()) {
                        photoPreview.setImageURI(Uri.fromFile(new File(photoPath)));
                    }
                    if (reminderDate > 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        reminderText.setText("Rappel : " + sdf.format(new Date(reminderDate)));
                    }
                }
                saveButton.setText("Modifier");
            }
        }

        photoButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Ajouter une photo");
            builder.setItems(new String[]{"Prendre une photo", "Choisir dans la galerie"}, (dialog, which) -> {
                if (which == 0) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
                }
            });
            builder.show();
        });

        reminderButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), (view1, year, month, dayOfMonth) -> {
                TimePickerDialog timePicker = new TimePickerDialog(getActivity(), (view2, hourOfDay, minute) -> {
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                    reminderDate = calendar.getTimeInMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    reminderText.setText("Rappel : " + sdf.format(new Date(reminderDate)));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePicker.show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                boolean success;
                if (noteId == 0) {
                    // Ajout d'une nouvelle note (remplace 1 par l'ID utilisateur réel)
                    success = dbHelper.addNote(1, title, content, photoPath, reminderDate);
                } else {
                    // Modification d'une note existante
                    success = dbHelper.updateNote(noteId, title, content, photoPath, reminderDate);
                }
                if (success) {
                    if (reminderDate > 0) {
                        scheduleReminder(title, reminderDate, noteId == 0 ? dbHelper.getLastNoteId() : noteId);
                    }
                    Toast.makeText(getActivity(), noteId == 0 ? "Note enregistrée" : "Note modifiée", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                photoPreview.setImageBitmap(imageBitmap);
                photoPath = saveImageToInternalStorage(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                photoPreview.setImageURI(selectedImage);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    photoPath = saveImageToInternalStorage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        Context context = getActivity();
        File directory = context.getDir("images", Context.MODE_PRIVATE);
        String fileName = "note_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void scheduleReminder(String title, long reminderTime, int noteId) {
        Context context = getActivity();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Vérification de la permission pour Android 12+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(context, "Veuillez autoriser l'application à programmer des rappels précis.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, noteId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
    }
}