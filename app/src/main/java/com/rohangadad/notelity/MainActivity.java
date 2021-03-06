package com.rohangadad.notelity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
public class MainActivity extends ListActivity {
    private static final int EDITOR_ACTIVITY_REQUEST = 1001;
    private static final int MENU_DELETE_ID = 1002;
    private int currentNoteId;
    private NotesDataSource datasource;
    private static Button button1;
    private static Button button2;
    ArrayList<NoteItem> notesList;
    private static boolean titleFlag = false;
    private static boolean dateFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(getListView());
        button1 = (Button) findViewById(R.id.titleSort);
        button2 = (Button) findViewById(R.id.dateSort);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortTitleDisplay();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sortDateDisplay();
            }
        });
        datasource = new NotesDataSource(this);
        refreshDisplay();
    }
    public void sortTitleDisplay() {
        notesList = (ArrayList) datasource.findAll();
        Iterator<NoteItem> iterator1 = notesList.iterator();
        while (iterator1.hasNext()) {
            NoteItem noteItem = iterator1.next();
            if (noteItem.getText().equals("")) {
                datasource.remove(noteItem);
                refreshDisplay();
            }
        }
        ArrayList<NoteItem> noteItems = new ArrayList<>();
        Iterator<NoteItem> iterator2 = notesList.iterator();
        while (iterator2.hasNext()) {
            NoteItem item = iterator2.next();
            noteItems.add(item);
        }
        if(titleFlag == false) {
            titleFlag = true;
            Collections.sort(noteItems, new Comparator<NoteItem>() {
                @Override
                public int compare(NoteItem noteItem, NoteItem t1) {
                    return noteItem.getText().compareToIgnoreCase(t1.getText());
                }
            });
        } else {
            titleFlag = false;
            Collections.sort(noteItems, new Comparator<NoteItem>() {
                @Override
                public int compare(NoteItem noteItem, NoteItem t1) {
                    return t1.getText().compareToIgnoreCase(noteItem.getText());
                }
            });
        }
        ArrayAdapter<NoteItem> adapter = new MyAdapter(this, R.layout.list_item_layout, noteItems);
        setListAdapter(adapter);
    }
    public void sortDateDisplay() {
        notesList = (ArrayList) datasource.findAll();
        Iterator<NoteItem> iterator1 = notesList.iterator();
        while (iterator1.hasNext()) {
            NoteItem noteItem = iterator1.next();
            if (noteItem.getText().equals("")) {
                datasource.remove(noteItem);
                refreshDisplay();
            }
        }
        ArrayList<NoteItem> noteItems = new ArrayList<>();
        Iterator<NoteItem> iterator2 = notesList.iterator();
        while (iterator2.hasNext()) {
            NoteItem item = iterator2.next();
            noteItems.add(item);
        }
        if(dateFlag == false) {
            dateFlag = true;
            refreshDisplay();
        }
        else {
            dateFlag = false;
            Collections.reverse(noteItems);
        }
        ArrayAdapter<NoteItem> adapter = new MyAdapter(this, R.layout.list_item_layout, noteItems);
        setListAdapter(adapter);
    }
    public void refreshDisplay() {
        notesList = (ArrayList) datasource.findAll();
        Iterator<NoteItem> iterator1 = notesList.iterator();
        while (iterator1.hasNext()) {
            NoteItem noteItem = iterator1.next();
            if (noteItem.getText().equals("")) {
                datasource.remove(noteItem);
                refreshDisplay();
            }
        }
        ArrayList<NoteItem> noteItems = new ArrayList<>();
        Iterator<NoteItem> iterator2 = notesList.iterator();
        while (iterator2.hasNext()) {
            NoteItem item = iterator2.next();
            noteItems.add(item);
        }
        ArrayAdapter<NoteItem> adapter = new MyAdapter(this, R.layout.list_item_layout, noteItems);
        setListAdapter(adapter);
    }
    public void searchFunction(String query) {

        refreshDisplay();

        notesList = (ArrayList) datasource.findAll();
        for (int i=0; i<notesList.size(); i++) {
            NoteItem item = notesList.get(i);
            if (!item.getText().toLowerCase().contains(query.toLowerCase())) {
                notesList.remove(i);
            }
        }
        ArrayAdapter<NoteItem> adapter = new MyAdapter(MainActivity.this, R.layout.list_item_layout, notesList);
        setListAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String query) {
                // this is your adapter that will be filtered
                searchFunction(query);
                return true;
            }
            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                searchFunction(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            createNote();
        }
        return super.onOptionsItemSelected(item);
    }
    private void createNote() {
        NoteItem note = NoteItem.getNew();
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", note.getText());
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        NoteItem note = notesList.get(position);
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("key", note.getKey());
        intent.putExtra("text", note.getText());
        startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            NoteItem note = new NoteItem();
            note.setKey(data.getStringExtra("key"));
            note.setText(data.getStringExtra("text"));
            datasource.update(note);
            refreshDisplay();
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        currentNoteId = (int)info.id;
        menu.add(0, MENU_DELETE_ID, 0, "Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_DELETE_ID) {
            NoteItem note = notesList.get(currentNoteId);
            datasource.remove(note);
            refreshDisplay();
        }
        return super.onContextItemSelected(item);
    }
}