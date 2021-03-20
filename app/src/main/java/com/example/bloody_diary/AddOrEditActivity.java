package com.example.bloody_diary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddOrEditActivity extends AppCompatActivity implements View.OnClickListener {
    private int   _idToDiary;
    private String action;
    private int editDate;
    private TextView tvDatePicker;
    private EditText etBeforeBreakfast;
    private EditText etAfterBreakfast;
    private EditText etBeforeLunch;
    private EditText etAfterLunch;
    private EditText etBeforeDinner;
    private EditText etAfterDinner;
    private DataBaseHelper mDBHelper;
    private DiaryHelper mDHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit);

        TextView tvAddOrEdit = (TextView) findViewById(R.id.tvAddOrEdit);
        tvDatePicker      = (TextView) findViewById(R.id.tvDatePicker);
        etBeforeBreakfast = (EditText) findViewById(R.id.etBeforeBreakfast );
        etAfterBreakfast  = (EditText) findViewById(R.id.etAfterBreakfast  );
        etBeforeLunch     = (EditText) findViewById(R.id.etBeforeLunch     );
        etAfterLunch      = (EditText) findViewById(R.id.etAfterLunch      );
        etBeforeDinner    = (EditText) findViewById(R.id.etBeforeDinner    );
        etAfterDinner     = (EditText) findViewById(R.id.etAfterDinner     );

        ImageButton btnDate = (ImageButton) findViewById(R.id.btnDate);

        mDBHelper = new DataBaseHelper(getApplicationContext());
        action = getIntent().getExtras().getString("Action");
        tvAddOrEdit.setText(action + " Record");

        Diary curDairy = null;
        if("Edit".equals(action)){
            editDate = getIntent().getExtras().getInt("Date");
            curDairy = mDBHelper.getDiaryByDate(editDate);
            _idToDiary = curDairy.get_id();
            tvDatePicker.setText(Diary.convertDateExcelToString(editDate));
            btnDate.setEnabled(false);

        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            tvDatePicker.setText(String.valueOf(sdf.format(Calendar.getInstance().getTime())));
            _idToDiary = 0;
            Float reg = (float) 0;
            curDairy = new Diary( 0, 0,0, reg, reg, reg, reg, reg, reg, reg);
        }
        etBeforeBreakfast.setText(String.valueOf(curDairy.getBbft()));
        etAfterBreakfast .setText(String.valueOf(curDairy.getAbft()));
        etBeforeLunch    .setText(String.valueOf(curDairy.getBlun()));
        etAfterLunch     .setText(String.valueOf(curDairy.getAlun()));
        etBeforeDinner   .setText(String.valueOf(curDairy.getBdin()));
        etAfterDinner    .setText(String.valueOf(curDairy.getAdin()));

        mDHelper = new DiaryHelper(this);

        btnDate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        mDHelper.datePicker(this, tvDatePicker);
    }
    private void saveAction() {
       List<Float> listAverage = new ArrayList<>();
        int mdateToDiary;
        if("Edit".equals(action)) {
            mdateToDiary = editDate;
        } else {
            mdateToDiary = Diary.convertStringToDateExcel(tvDatePicker.getText().toString());
            if (mDBHelper.isDiaryExist(mdateToDiary)) {action = "Edit";}
        }
        int useridToDiary = 1; //Must be changed in final version
        Float bbftToDiary = mDHelper.checkValue(etBeforeBreakfast);
        if (bbftToDiary < 0) {return;} else if (bbftToDiary > 0) {listAverage.add(bbftToDiary);}
        Float abftToDiary = mDHelper.checkValue(etAfterBreakfast);
        if (abftToDiary < 0) {return;} else if (abftToDiary > 0) {listAverage.add(abftToDiary);}
        Float blunToDiary = mDHelper.checkValue(etBeforeLunch);
        if (blunToDiary < 0) {return;} else if (blunToDiary > 0) {listAverage.add(blunToDiary);}
        Float alunToDiary = mDHelper.checkValue(etAfterLunch);
        if (alunToDiary < 0) {return;} else if (alunToDiary > 0) {listAverage.add(alunToDiary);}
        Float bdinToDiary = mDHelper.checkValue(etBeforeDinner);
        if (bdinToDiary < 0) {return;} else if (bdinToDiary > 0) {listAverage.add(bdinToDiary);}
        Float adinToDiary = mDHelper.checkValue(etAfterDinner);
        if (adinToDiary < 0) {return;} else if (adinToDiary > 0) {listAverage.add(adinToDiary);}
        Float averageToDiary = Diary.getAverage(listAverage);

        Diary diary = new Diary(
                _idToDiary, mdateToDiary, useridToDiary, bbftToDiary, abftToDiary,
                blunToDiary, alunToDiary, bdinToDiary, adinToDiary, averageToDiary);
        if("Edit".equals(action)) {
            long result = mDBHelper.updateDiary(diary);
            if(result>0){
                Toast.makeText(getApplicationContext(),"Record updated",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Record update failed",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = mDBHelper.addDiary(diary);
            if(result>0){
                Toast.makeText(getApplicationContext(),"Record added",
                        Toast.LENGTH_SHORT).show();
                //back to main activity
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Record add failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_or_edit, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemSave:
                saveAction();
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}