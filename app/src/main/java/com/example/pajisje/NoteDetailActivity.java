package com.example.pajisje;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        // Merrni të dhënat nga Intent
        String noteTitle = getIntent().getStringExtra("noteTitle");
        String noteContent = getIntent().getStringExtra("noteContent");

        // Gjeni EditText në layout
        EditText titleEditText = findViewById(R.id.noteTitleEditText);
        EditText contentEditText = findViewById(R.id.noteContentEditText);

        // Vendosni të dhënat në EditText
        titleEditText.setText(noteTitle);
        contentEditText.setText(noteContent);
    }
}
