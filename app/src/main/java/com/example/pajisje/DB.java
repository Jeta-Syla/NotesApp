package com.example.pajisje;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserData.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns for user data
    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Table name and columns for notes
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTE_ID = "note_id";
    public static final String COLUMN_NOTE_TITLE = "title";
    public static final String COLUMN_NOTE_CONTENT = "content";
    public static final String COLUMN_NOTE_USER_ID = "user_id";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table for storing user data
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUMN_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(CREATE_USER_TABLE);

        // Create table for storing notes
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                COLUMN_NOTE_CONTENT + " TEXT NOT NULL, " +
                COLUMN_NOTE_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_NOTE_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exists and create new ones
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // Method to insert a new user into the database
    public long addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt())); // Store hashed password

        // Inserting row
        return db.insert(TABLE_USER, null, values);
    }

    // Method to check if email exists (for login or forgot password)
    public boolean checkEmailExists(String email) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + " = ?";
            try (Cursor cursor = db.rawQuery(query, new String[]{email})) {
                return cursor.moveToFirst();
            }
        }
    }

    // Method to validate user login
    public boolean validateUser(String email, String password) {
        String hashedPassword = getPassword(email); // Get hashed password from DB

        if (hashedPassword != null) {
            // Check if the entered password matches the hashed password
            return BCrypt.checkpw(password, hashedPassword);
        }
        return false; // Email not found or password doesn't match
    }


    // Method to get password for a given email
    public String getPassword(String email) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + " = ?";
            try (Cursor cursor = db.rawQuery(query, new String[]{email})) {
                if (cursor != null && cursor.moveToFirst()) {
                    int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                    if (passwordColumnIndex >= 0) {
                        return cursor.getString(passwordColumnIndex);
                    } else {
                        // Log or handle error in case column is not found
                        return null; // Or throw an exception if required
                    }
                }
            }
        }
        return null;
    }


    // Add note method
    public long addNote(String title, String content, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, title);
        values.put(COLUMN_NOTE_CONTENT, content);
        values.put(COLUMN_NOTE_USER_ID, userId);

        return db.insert(TABLE_NOTES, null, values);
    }

    // Update note method
    public int updateNote(int noteId, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, title);
        values.put(COLUMN_NOTE_CONTENT, content);

        return db.update(TABLE_NOTES, values, COLUMN_NOTE_ID + "=?", new String[]{String.valueOf(noteId)});
    }

    // Method to get notes for a user
    public Cursor getNotesByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NOTES,
                new String[]{COLUMN_NOTE_TITLE}, // Include title here
                COLUMN_NOTE_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);
    }

    @SuppressLint("Range")
    public int getUserIdByEmail(String email) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + " = ?";
            try (Cursor cursor = db.rawQuery(query, new String[]{email})) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                }
            }
        }
        return -1;
    }

    public Cursor getNoteByTitle(String title, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_NOTES, // Table name
                null, // Select all columns
                COLUMN_NOTE_TITLE + " = ? AND " + COLUMN_NOTE_USER_ID + " = ?", // WHERE clause
                new String[]{title, String.valueOf(userId)}, // WHERE clause arguments
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );
    }

    // Delete note by title and user ID
    public int deleteNoteByTitle(String title, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                TABLE_NOTES, // Table name
                COLUMN_NOTE_TITLE + " = ? AND " + COLUMN_NOTE_USER_ID + " = ?", // WHERE clause
                new String[]{title, String.valueOf(userId)} // WHERE clause arguments
        );
    }
}
