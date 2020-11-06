package live.kiptechie.architecture.example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import live.kiptechie.architecture.example.models.Note;
import live.kiptechie.architecture.example.utils.Constants;

public class AddEditNoteActivity extends AppCompatActivity {

    TextInputEditText editTextTitle, editTextDescription;
    NumberPicker numberPickerPriority;
    boolean isEdit = false;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        initializeFields();
    }

    private void initializeFields() {
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        isEdit = getIntent().getBooleanExtra(Constants.IS_EDIT, false);
       if (isEdit) {
           setTitle("Edit Note");
           note = getIntent().getParcelableExtra(Constants.EXTRA_EDIT_NOTE_PARCELABLE);
          if (note != null) {
              editTextTitle.setText(note.getTitle());
              editTextDescription.setText(note.getDescription());
              numberPickerPriority.setValue(note.getPriority());
          }
       } else {
           setTitle("Create Note");
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_note) {
            saveNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        String title = Objects.requireNonNull(editTextTitle.getText()).toString();
        String description = Objects.requireNonNull(editTextDescription.getText()).toString();
        int priority = numberPickerPriority.getValue();
        if (title.trim().isEmpty()) {
            Toast.makeText(this, "Title is empty.", Toast.LENGTH_SHORT).show();
        } else if (description.trim().isEmpty()) {
            Toast.makeText(this, "Description is empty.", Toast.LENGTH_SHORT).show();
        } else {
            Note newNote = new Note(priority, title, description);
            Intent data = new Intent();
            if (isEdit && note.getId() != -1) {
                data.putExtra(Constants.EXTRA_EDIT_NOTE_PARCELABLE, newNote);
                data.putExtra(Constants.EXTRA_ID, note.getId());
            } else {
                data.putExtra(Constants.EXTRA_ADD_NOTE_PARCELABLE, newNote);
            }
            setResult(RESULT_OK, data);
            finish();
        }
    }
}