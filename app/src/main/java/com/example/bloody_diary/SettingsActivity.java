package com.example.bloody_diary;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener  {

    private TextView tvDateGH;
    private EditText etGH;
    private int dialogType;
    private DataBaseHelper mDBHelper;
    private DiaryHelper mDHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d("myLogs", "======== SettingsActivity: Starting =========");

        Button btnClearDB = (Button) findViewById(R.id.btnClearDB);
        Button btnLoadDB  = (Button) findViewById(R.id.btnLoadDB);
        Button btnSendDB  = (Button) findViewById(R.id.btnSendDB);
        Button btnSetGH   = (Button) findViewById(R.id.btnSetGH);
        Button btnAbout   = (Button) findViewById(R.id.btnAbout);
        Button btnDonate  = (Button) findViewById(R.id.btnDonate);
        ImageButton ibtnDateGH = (ImageButton) findViewById(R.id.ibtnnDateGH);

        btnClearDB.setOnClickListener(this);
        btnLoadDB .setOnClickListener(this);
        btnSendDB .setOnClickListener(this);
        btnSetGH  .setOnClickListener(this);
        btnAbout  .setOnClickListener(this);
        btnDonate .setOnClickListener(this);
        ibtnDateGH.setOnClickListener(this);

        etGH = (EditText) findViewById(R.id.etGH);

        tvDateGH = (TextView) findViewById(R.id.tvDateGH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdf.format(Calendar.getInstance().getTime());
        tvDateGH.setText(date);

        mDBHelper = new DataBaseHelper(this);
        mDHelper = new DiaryHelper(this);

        Log.d("myLogs", "SettingsActivity: Started");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        FragmentManager manager = getSupportFragmentManager();
        DiaryDialog mainDialog = new DiaryDialog();
        switch (v.getId()) {
            case R.id.btnAbout:
                Log.d("myLogs", "SettingsActivity: Calling AboutDialog");
                dialogType = 2;
                mainDialog.setArguments("SettingsActivity", dialogType);
                mainDialog.show(manager, "mainDialog");
                break;
            case R.id.btnClearDB:
                Log.d("myLogs", "SettingsActivity: Calling AlertDialog");
                dialogType = 1;
                mainDialog.setArguments("SettingsActivity", dialogType);
                mainDialog.show(manager, "mainDialog");
                break;
            case R.id.btnLoadDB:
                Log.d("myLogs", "SettingsActivity: Calling AlertDialog");
                Toast.makeText(getApplicationContext(), "Sorry! Under construction",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.btnSendDB:
                Log.d("myLogs", "SettingsActivity: Calling SenderActivity");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mDBHelper.exportDiaryToCSV(this);
                }
                break;
            case R.id.btnSetGH:
                Log.d("myLogs", "SettingsActivity: Calling SettingDialog");
                mDHelper.checkValue(etGH);
                mDBHelper.deleteGHByDate(Diary.convertStringToDateExcel(tvDateGH.getText().toString()));
                mDBHelper.addGH(Diary.convertStringToDateExcel(tvDateGH.getText().toString()),
                        1, (float) Float.parseFloat(etGH.getText().toString()));
                Toast.makeText(getApplicationContext(), "Value of Glycated hemoglobin set",
                        Toast.LENGTH_LONG).show();
              break;
            case R.id.btnDonate:
                Log.d("myLogs", "SettingsActivity: Calling DonateActivity");
                Toast.makeText(getApplicationContext(), "Sorry! Under construction",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.ibtnnDateGH:
                Log.d("myLogs", "SettingsActivity: Calling DonateActivity");
                mDHelper.datePicker(this, tvDateGH);
                break;
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_settings_activity, menu);
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

    public void okClicked() {
        if (dialogType == 1) {
           mDBHelper.clearDatabase();
        }
    }

    public void cancelClicked() {
    }


}
