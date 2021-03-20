package com.example.bloody_diary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String FIRST = "first";
    private final String PREVIOUS = "previous";
    private final String NEXT = "next";
    private final String LAST = "last";
    private final String CURRENT = "current";
    private final String UP = "↑";
    private final String DOWN = "↓";
    private TextView tvDataLabel;
    private TextView tvBreakfastBefore;
    private TextView tvBreakfastAfter ;
    private TextView tvLunchBefore    ;
    private TextView tvLunchAfter     ;
    private TextView tvDinnerBefore   ;
    private TextView tvDinnerAfter    ;
    private TextView tvBreakfastPreviousBefore;
    private TextView tvBreakfastPreviousAfter;
    private TextView tvLunchPreviousBefore;
    private TextView tvLunchPreviousAfter;
    private TextView tvDinnerPreviousBefore;
    private TextView tvDinnerPreviousAfter;
    private TextView tvAnalysisAverageDay;
    private TextView tvAnalysisAverageMonth;
    private TextView tvAnalysisAverageWeek;
    private TextView tvAnalysisGHDay;
    private TextView tvAnalysisGHWeek;
    private TextView tvAnalysisGHMonth;
    private TextView tvDeltaDay;
    private TextView tvDeltaMonth;
    private TextView tvDeltaWeek;
    private DataBaseHelper mDBHelper;
    private Intent intent;
    public int diaryId, diaryCount, diaryDate;
    private float valueGH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("myLogs", "======== MainActivity: Starting =========");
        //Define text views
        tvDataLabel               = (TextView) findViewById(R.id.tvDataLabel);
        tvBreakfastBefore         = (TextView) findViewById(R.id.tvBreakfastBefore);
        tvBreakfastAfter          = (TextView) findViewById(R.id.tvBreakfastAfter );
        tvLunchBefore             = (TextView) findViewById(R.id.tvLunchBefore    );
        tvLunchAfter              = (TextView) findViewById(R.id.tvLunchAfter     );
        tvDinnerBefore            = (TextView) findViewById(R.id.tvDinnerBefore   );
        tvDinnerAfter             = (TextView) findViewById(R.id.tvDinnerAfter    );
        tvBreakfastPreviousBefore = (TextView) findViewById(R.id.tvBreakfastPreviousBefore);
        tvBreakfastPreviousAfter  = (TextView) findViewById(R.id.tvBreakfastPreviousAfter);
        tvLunchPreviousBefore     = (TextView) findViewById(R.id.tvLunchPreviousBefore );
        tvLunchPreviousAfter      = (TextView) findViewById(R.id.tvLunchPreviousAfter  );
        tvDinnerPreviousBefore    = (TextView) findViewById(R.id.tvDinnerPreviousBefore);
        tvDinnerPreviousAfter     = (TextView) findViewById(R.id.tvDinnerPreviousAfter );
        tvAnalysisAverageDay      = (TextView) findViewById(R.id.tvAnalysisAverageDay  );
        tvAnalysisAverageMonth    = (TextView) findViewById(R.id.tvAnalysisAverageMonth);
        tvAnalysisAverageWeek     = (TextView) findViewById(R.id.tvAnalysisAverageWeek );
        tvAnalysisGHDay     = (TextView) findViewById(R.id.tvAnalysisGHDay);
        tvAnalysisGHWeek     = (TextView) findViewById(R.id.tvAnalysisGHWeek);
        tvAnalysisGHMonth     = (TextView) findViewById(R.id.tvAnalysisGHMonth);
        tvDeltaDay                = (TextView) findViewById(R.id.tvDeltaDay  );
        tvDeltaMonth              = (TextView) findViewById(R.id.tvDeltaMonth);
        tvDeltaWeek               = (TextView) findViewById(R.id.tvDeltaWeek );
        TextView tvToday = (TextView) findViewById(R.id.tvToday);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdf.format(Calendar.getInstance().getTime());
        tvToday.setText(date);
        //Define buttons
        ImageButton ibtnNew = (ImageButton) findViewById(R.id.ibtnNew);
        ImageButton ibtnEdit = (ImageButton) findViewById(R.id.ibtnEdit);
        ImageButton ibtnDelete = (ImageButton) findViewById(R.id.ibtnDelete);
        ImageButton ibtnList = (ImageButton) findViewById(R.id.ibtnList);

        ImageButton ibtnFirst = (ImageButton) findViewById(R.id.ibtnFirst);
        ImageButton ibtnPrevious = (ImageButton) findViewById(R.id.ibtnPrevious);
        ImageButton ibtnNext = (ImageButton) findViewById(R.id.ibtnNext);
        ImageButton ibtnLast = (ImageButton) findViewById(R.id.ibtnLast);

        ibtnNew.setOnClickListener(this);
        ibtnEdit.setOnClickListener(this);
        ibtnDelete.setOnClickListener(this);
        ibtnList.setOnClickListener(this);
        ibtnFirst.setOnClickListener(this);
        ibtnPrevious.setOnClickListener(this);
        ibtnNext.setOnClickListener(this);
        ibtnLast.setOnClickListener(this);

        //Check if database exist
        mDBHelper = new DataBaseHelper(getApplicationContext());
        mDBHelper.checkDBFile();
        valueGH = mDBHelper.getLastGH();

        diaryCount = mDBHelper.getDiaryCount();
        diaryId = 0;
        //Invoke diary data to present
        invokeDiaries(LAST);
        Log.d("myLogs", "======== MainActivity: Started =========");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        valueGH = mDBHelper.getLastGH();
        if (!mDBHelper.isDiaryExist(diaryDate)) {
            invokeDiaries(LAST);
        } else {
            invokeDiaries(CURRENT);
        }
        Log.d("myLogs", "MainActivity: Restarted");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.itemNew:
                this.actionNew();
                break;
            case R.id.itemEdit:
                this.actionEdit();
                break;
            case R.id.itemDelete:
                this.actionDelete();
                break;
            case R.id.itemList:
                this.actionList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setDataView(Diary cDiary, Diary pDiary) {
        Float delta, average;
        tvDataLabel              .setText("Measuring data for "
                + Diary.convertDateExcelToString(cDiary.getMdate()));
        // Setting info for current date
        tvBreakfastBefore        .setText(cDiary.getBbft().toString() +
                setUpOrDown(cDiary.getBbft(), pDiary.getBbft()));
        tvBreakfastAfter         .setText(cDiary.getAbft().toString() +
                setUpOrDown(cDiary.getAbft(), pDiary.getAbft()));
        tvLunchBefore            .setText(cDiary.getBlun().toString() +
                setUpOrDown(cDiary.getBlun(), pDiary.getBlun()));
        tvLunchAfter             .setText(cDiary.getAlun().toString() +
                setUpOrDown(cDiary.getAlun(), pDiary.getAlun()));
        tvDinnerBefore           .setText(cDiary.getBdin().toString() +
                setUpOrDown(cDiary.getBdin(), pDiary.getBdin()));
        tvDinnerAfter            .setText(cDiary.getAdin().toString() +
                setUpOrDown(cDiary.getAdin(), pDiary.getAdin()));
        // Setting info for previous date
        tvBreakfastPreviousBefore.setText("Previous " + pDiary.getBbft().toString());
        tvBreakfastPreviousAfter .setText("Previous " + pDiary.getAbft().toString());
        tvLunchPreviousBefore    .setText("Previous " + pDiary.getBlun().toString());
        tvLunchPreviousAfter     .setText("Previous " + pDiary.getAlun().toString());
        tvDinnerPreviousBefore   .setText("Previous " + pDiary.getBdin().toString());
        tvDinnerPreviousAfter    .setText("Previous " + pDiary.getAdin().toString());
        // Setting info for per Day analysis data
        average = cDiary.getAverage();
        delta = average - valueGH;
        tvAnalysisAverageDay     .setText(String.format("%.2f", average));
        tvAnalysisGHDay          .setText(String.format("%.2f", valueGH));
        tvDeltaDay               .setText(String.format("%.2f", delta));
        // Setting info for per Week analysis data
        average = mDBHelper.getDiaryAverage(cDiary.getMdate() - 7, cDiary.getMdate());
        delta = average - valueGH;
        tvAnalysisAverageWeek    .setText(String.format("%.2f", average));
        tvAnalysisGHWeek         .setText(String.format("%.2f", valueGH));
        tvDeltaWeek              .setText(String.format("%.2f", delta));
        // Setting info for per Month analysis data
        average = mDBHelper.getDiaryAverage(cDiary.getMdate() - 30, cDiary.getMdate());
        delta = average - valueGH;
        tvAnalysisAverageMonth   .setText(String.format("%.2f", average));
        tvAnalysisGHMonth        .setText(String.format("%.2f", valueGH));
        tvDeltaMonth             .setText(String.format("%.2f", delta));
        Log.d("myLogs", "MainActivity: All data displayed");
    }

    private String setUpOrDown(float curGlucose, float prevGlucose){
        String trend = "";
        if (!((curGlucose == 0) || (prevGlucose == 0))) {
        if (curGlucose > prevGlucose) {
            trend = " " + UP;
        } else if (curGlucose < prevGlucose) {
            trend = " " + DOWN;
        }};
        return trend;
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnNew:
                this.actionNew();
                break;
            case R.id.ibtnEdit:
                this.actionEdit();
                break;
            case R.id.ibtnDelete:
                this.actionDelete();
                break;
            case R.id.ibtnList:
                this.actionList();
               break;
            case R.id.ibtnFirst:
                invokeDiaries(FIRST);
                //Toast.makeText(this, "Jump to the first record", Toast.LENGTH_SHORT).show();
                Log.d("myLogs", "MainActivity: btnFirst pressed | _id = "
                        + diaryId + " | Date = " + diaryDate);
                break;
            case R.id.ibtnPrevious:
                invokeDiaries(PREVIOUS);
                //Toast.makeText(this, "Jump to the previous record", Toast.LENGTH_SHORT).show();
                Log.d("myLogs", "MainActivity: btnPrevious pressed | _id = "
                        + diaryId + " | Date = " + diaryDate);
                break;
            case R.id.ibtnNext:
                invokeDiaries(NEXT);
                //Toast.makeText(this, "Jump to the next record", Toast.LENGTH_SHORT).show();
                Log.d("myLogs", "MainActivity: btnNext pressed | _id = "
                        + diaryId + " | Date = " + diaryDate);
                break;
            case R.id.ibtnLast:
                invokeDiaries(LAST);
                //Toast.makeText(this, "Jump to the last record", Toast.LENGTH_SHORT).show();
                Log.d("myLogs", "MainActivity: btnLast pressed | _id = "
                        + diaryId + " | Date = " + diaryDate);
                break;
       }
    }

    private void actionNew() {
        //Toast.makeText(this, "MainActivity: Adding new record", Toast.LENGTH_SHORT).show();
        Log.d("myLogs", "MainActivity: btnNew pressed");
        Intent intentNew = new Intent(getApplicationContext(), AddOrEditActivity.class);
        intentNew.putExtra("Action", "New");
        startActivity(intentNew);
    }

    private void actionEdit() {
        //Toast.makeText(this, "MainActivity: Editing the record", Toast.LENGTH_SHORT).show();
        Log.d("myLogs", "MainActivity: btnEdit pressed");
        Intent intentEdit = new Intent(getApplicationContext(), AddOrEditActivity.class);
        intentEdit.putExtra("Action", "Edit");
        intentEdit.putExtra("Date", diaryDate);
        startActivity(intentEdit);
    }

    private void actionDelete() {
        FragmentManager manager = getSupportFragmentManager();
        DiaryDialog mainDialog = new DiaryDialog();
        mainDialog.setArguments("ActivityMain", diaryDate);
        mainDialog.show(manager, "mainDialog");
        //Toast.makeText(this, "MainActivity: Deleting the record", Toast.LENGTH_SHORT).show();
        Log.d("myLogs", "MainActivity: btnDelete pressed");
   }

    private void actionList() {
        intent = new Intent(this, ListDiaryActivity.class);
        startActivity(intent);
        Log.d("myLogs", "MainActivity: btnList pressed");
        Log.d("myLogs", "MainActivity: Start of activity ListDiaryActivity");
    }

    private void invokeDiaries(String diaryType) {
        Diary curDiary = null;
        Diary prevDiary = null;
        switch (diaryType) {
            case FIRST:
                curDiary = mDBHelper.getFirstDiary();
                break;
            case PREVIOUS:
                if (mDBHelper.isFirstDiary(diaryDate)) {
                    curDiary = mDBHelper.getDiaryByDate(diaryDate);
                } else {
                    curDiary = mDBHelper.getPreviousDiary(diaryDate);
                }
                break;
            case NEXT:
                if (mDBHelper.isLastDiary(diaryDate)) {
                    curDiary = mDBHelper.getDiaryByDate(diaryDate);
                } else {
                    curDiary = mDBHelper.getNextDiary(diaryDate);
                }
                break;
            case LAST:
                curDiary = mDBHelper.getLastDiary();
                break;
            case CURRENT:
                curDiary = mDBHelper.getDiaryByDate(diaryDate);
                break;
        }
        assert curDiary != null;
        diaryId = curDiary.get_id();
        diaryDate = curDiary.getMdate();
        prevDiary = mDBHelper.getPreviousDiary(diaryDate);
        setDataView(curDiary, prevDiary);
    }

    public void okClicked() {
        int curDiaryDateAfterDelete = 0;
        if (mDBHelper.isLastDiary(diaryDate)) {
            curDiaryDateAfterDelete = mDBHelper.getPreviousDiary(diaryDate).getMdate();
        } else {
            curDiaryDateAfterDelete = mDBHelper.getNextDiary(diaryDate).getMdate();
        }
        if (mDBHelper.deleteDiaryByDate(diaryDate)) {
            diaryDate = curDiaryDateAfterDelete;
            Diary curDiary = mDBHelper.getDiaryByDate(diaryDate);
            Diary prevDiary = mDBHelper.getPreviousDiary(diaryDate);
            setDataView(curDiary, prevDiary);
        } else {
            Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelClicked() {
    }
}