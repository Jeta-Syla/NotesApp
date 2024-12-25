package com.example.pajisje;

import android.app.AlertDialog;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;


public class NoteActivity extends AppCompatActivity {

    private ListView notesListView;
    private List<String> notesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notesListView = findViewById(R.id.notesListView);

        // Sample data
        notesList.add("Note 1: \nShopping List");
        notesList.add("Note 2: \nWork Tasks");

        // Set custom adapter
        notesListView.setAdapter(new CustomAdapter());

        Button saveButton = findViewById(R.id.saveButton);
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText contentEditText = findViewById(R.id.contentEditText);
        Button cancelButton = findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(v -> {
            // Navigate to MainActivity
            Intent intent = new Intent(NoteActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: Close SignUpActivity if you don't want it in the back stack
        });

        saveButton.setOnClickListener(v -> {
            String newNote = titleEditText.getText().toString().trim() + ": \n" + contentEditText.getText().toString().trim();
            if (!newNote.isEmpty()) {
                notesList.add(newNote); // Add note to the list
                titleEditText.setText(""); // Clear input field
                contentEditText.setText("");
                ((BaseAdapter) notesListView.getAdapter()).notifyDataSetChanged(); // Notify adapter
                Toast.makeText(NoteActivity.this, "Note added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NoteActivity.this, "Enter a valid note", Toast.LENGTH_SHORT).show();
            }
        }); 
    }

    private class CustomAdapter extends BaseAdapter {

        private class ViewHolder {
            TextView noteTextView;
            Button viewContentButton;
            Button editButton;
            Button deleteButton;
            
        }

        @Override
        public int getCount() {
            return notesList.size();
        }

        @Override
        public Object getItem(int position) {
            return notesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                // Inflate custom row layout
                convertView = LayoutInflater.from(NoteActivity.this)
                        .inflate(R.layout.note_item, parent, false);
                holder = new ViewHolder();
                holder.noteTextView = convertView.findViewById(R.id.noteTextView);
                holder.editButton = convertView.findViewById(R.id.editButton);
                holder.deleteButton = convertView.findViewById(R.id.deleteButton);
                holder.viewContentButton = convertView.findViewById(R.id.viewContentButton); // Initialize View Content button
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Set note text
            String noteText = notesList.get(position);
            holder.noteTextView.setText(noteText);

            // Handle Edit button
            holder.editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setTitle("Edit Note");

                final EditText input = new EditText(NoteActivity.this);
                input.setText(noteText);
                input.setSelection(input.getText().length());
                builder.setView(input);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String updatedNote = input.getText().toString().trim();
                    if (!updatedNote.isEmpty()) {
                        notesList.set(position, updatedNote);
                        notifyDataSetChanged();
                        Toast.makeText(NoteActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NoteActivity.this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                builder.show();
            });

            // View Button
            holder.viewContentButton.setOnClickListener(v -> {
                // Krijo një Intent për të hapur NoteDetailActivity
                Intent intent = new Intent(NoteActivity.this, NoteDetailActivity.class);

                // Reuse the existing noteText variable to extract title and content
                String noteTitle = noteText.split(":")[0]; // Pjesa e para e shënimit është titulli
                String noteContent = noteText.split(":")[1].trim(); // Pjesa e dyte është përmbajtja

                // Dërgo titullin dhe përmbajtjen në NoteDetailActivity
                intent.putExtra("noteTitle", noteTitle); // Dërgo titullin
                intent.putExtra("noteContent", noteContent); // Dërgo përmbajtjen
                startActivity(intent); // Hapni NoteDetailActivity
            });
            // Butoni per te fshire Notes
            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(NoteActivity.this)
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            notesList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(NoteActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            return convertView;
        }

    }
}
