package com.example.bloody_diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import static com.example.bloody_diary.DataBaseHelper.DBNAME;

public class ListDiaryActivity extends AppCompatActivity {
    private int diaryIdBeforeDeleting;
    private int listPosition;
    private ListView lvDiary;
    private ListDiaryAdapter adapter;
    private List<Diary> myDiaryList;
    private DataBaseHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_diary);
        Log.d("myLogs", "======== ListDiaryActivity: Starting =========");

        lvDiary = (ListView) findViewById(R.id.listview_diary);
        mDBHelper = new DataBaseHelper(this);

        //Get product list in db when db exists
        myDiaryList = mDBHelper.getListDiary();
        Log.d("myLogs", "ListDiaryActivity: DiaryList obtained");
        //Init adapter
        adapter = new ListDiaryAdapter(this, myDiaryList);
        //Set adapter for listview
        lvDiary.setAdapter(adapter);
        lvDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idDiary = (int) view.getTag();
                Toast.makeText(getApplicationContext(),
                        "Record ID: " + idDiary + " | Date: "
                                + ((TextView) view.findViewById(R.id.tvDate)).getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        registerForContextMenu(lvDiary);
        Log.d("myLogs", "======== ListDiaryActivity: Started =========");
    }

     @Override
     protected void onRestart() {
         super.onRestart();
         myDiaryList = mDBHelper.getListDiary();
         adapter.updateList(myDiaryList);
         Log.d("myLogs", "======== ListDiaryActivity: Restarted =========");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_diary_activity, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

  @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        //Get id of item clicked
        // Retrieve the item that was clicked on
        View v = (View) lvDiary.getAdapter().getView(
                info.position, null, null);
        listPosition = info.position;
        switch (item.getItemId()) {
            case R.id.menu_item_edit:
                Intent i = new Intent(getApplicationContext(), AddOrEditActivity.class);
                i.putExtra("Action", "Edit");
                int diaryDate = mDBHelper.getDiaryById((int) v.getTag()).getMdate();
                i.putExtra("Date", diaryDate); //Id product saved to tag
                startActivity(i);
                break;
            case R.id.menu_item_del:
                diaryIdBeforeDeleting = (int) v.getTag();
                Diary deleteDiary = mDBHelper.getDiaryById(diaryIdBeforeDeleting);
                int diaryDateBeforeDeleting = deleteDiary.getMdate();
                Log.d("myLogs", "ListDiaryActivity: btnDelete pressed");
                FragmentManager manager = getSupportFragmentManager();
                DiaryDialog mainDialog = new DiaryDialog();
                mainDialog.setArguments("ListDiaryActivity", diaryDateBeforeDeleting);
                mainDialog.show(manager, "mainDialog");
                break;
        }
        return true;
    }

    public void okClicked() {
        //Toast.makeText(this, "MainActivity: Deleting the record", Toast.LENGTH_SHORT).show();
        if(mDBHelper.deleteDiaryById(diaryIdBeforeDeleting)){
            //Toast.makeText(getApplicationContext(),"Deleted", Toast.LENGTH_SHORT).show();
            myDiaryList.remove(listPosition);
            myDiaryList = mDBHelper.getListDiary();
            adapter.updateList(myDiaryList);
        } else {
            //Toast.makeText(getApplicationContext(),"Delete failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelClicked() {
    }
}