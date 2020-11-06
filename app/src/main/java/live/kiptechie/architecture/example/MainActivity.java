package live.kiptechie.architecture.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import live.kiptechie.architecture.example.adapters.NoteRecyclerAdapter;
import live.kiptechie.architecture.example.models.Note;
import live.kiptechie.architecture.example.utils.Constants;
import live.kiptechie.architecture.example.view_models.NoteViewModel;

public class MainActivity extends AppCompatActivity {

    NoteViewModel noteViewModel;
    final NoteRecyclerAdapter adapter = new NoteRecyclerAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("My Notes");
        FloatingActionButton buttonAddNote = findViewById(R.id.add_note_button);
        buttonAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(Constants.IS_EDIT, false);
            startActivityForResult(intent, Constants.ADD_NOTE_REQ_CODE);
        });
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, notes -> {
            // update our recycler view.
            adapter.submitList(notes);
            recyclerView.setAdapter(adapter);
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String title = adapter.getNoteAt(viewHolder.getAdapterPosition()).getTitle();
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, title + " deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
        adapter.setOnNoteClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(Constants.EXTRA_EDIT_NOTE_PARCELABLE, note);
            intent.putExtra(Constants.IS_EDIT, true);
            startActivityForResult(intent, Constants.EDIT_NOTE_REQ_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Note myNote;
        if (data != null) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Constants.ADD_NOTE_REQ_CODE:
                        myNote = data.getParcelableExtra(Constants.EXTRA_ADD_NOTE_PARCELABLE);
                        if (myNote != null) {
                            Note note = new Note(myNote.getPriority(), myNote.getTitle(), myNote.getDescription());
                            noteViewModel.insert(note);
                            Toast.makeText(this, myNote.getTitle() + " saved!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Constants.EDIT_NOTE_REQ_CODE:
                        myNote = data.getParcelableExtra(Constants.EXTRA_EDIT_NOTE_PARCELABLE);
                        int id = data.getIntExtra(Constants.EXTRA_ID, -1);
                        if (myNote != null) {
                            if (id == -1) {
                                Toast.makeText(this, "Note cannot be updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                Note note = new Note(myNote.getPriority(), myNote.getTitle(), myNote.getDescription());
                                note.setId(id);
                                noteViewModel.update(note);
                                Toast.makeText(this, myNote.getTitle() + " updated!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            int count = adapter.getItemCount();
            noteViewModel.deleteAllNotes();
            Toast.makeText(this, "All " + count + " notes deleted!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}