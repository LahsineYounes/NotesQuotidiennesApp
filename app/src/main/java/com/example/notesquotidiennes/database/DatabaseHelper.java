package com.example.notesquotidiennes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.notesquotidiennes.adapters.NoteAdapter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT, " +
                "birth TEXT, " +
                "city TEXT, " +
                "photo_path TEXT)");
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, content TEXT, reminder_date LONG, photo_path TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    // Vérifier si l'utilisateur existe
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Ajouter un utilisateur
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // utilisateur déjà existant
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    // Récupérer l'utilisateur par username
    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
    }

    // Mettre à jour le profil utilisateur
    public boolean updateUserProfile(int userId, String birth, String city, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("birth", birth);
        values.put("city", city);
        values.put("photo_path", photoPath);
        int result = db.update("users", values, "id=?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    // Ajout d'une note
    public boolean addNote(int userId, String title, String content, String photoPath, long reminderDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("content", content);
        values.put("photo_path", photoPath);
        values.put("reminder_date", reminderDate);
        long result = db.insert("notes", null, values);
        return result != -1;
    }

    // Modification d'une note
    public boolean updateNote(int noteId, String title, String content, String photoPath, long reminderDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("photo_path", photoPath);
        values.put("reminder_date", reminderDate);
        int result = db.update("notes", values, "id=?", new String[]{String.valueOf(noteId)});
        return result > 0;
    }

    // Récupérer une note par son id
    public String[] getNoteById(int noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title, content, photo_path, reminder_date FROM notes WHERE id=?", new String[]{String.valueOf(noteId)});
        if (cursor.moveToFirst()) {
            String[] note = {
                    cursor.getString(0), // title
                    cursor.getString(1), // content
                    cursor.getString(2), // photo_path
                    cursor.isNull(3) ? null : String.valueOf(cursor.getLong(3)) // reminder_date
            };
            cursor.close();
            return note;
        }
        cursor.close();
        return null;
    }

    // Récupérer l'id de la dernière note insérée
    public int getLastNoteId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM notes ORDER BY id DESC LIMIT 1", null);
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    public boolean deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("notes", "id=?", new String[]{String.valueOf(noteId)});
        return result > 0;
    }

    public List<NoteAdapter.NoteItem> getAllNoteItems(int userId) {
        List<NoteAdapter.NoteItem> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title, photo_path FROM notes WHERE user_id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                notes.add(new NoteAdapter.NoteItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

}
