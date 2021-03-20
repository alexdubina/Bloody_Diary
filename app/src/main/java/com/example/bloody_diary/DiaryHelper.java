package com.example.bloody_diary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DiaryHelper {
    private final Context mContext;

    public DiaryHelper(Context mContext) {
        this.mContext = mContext;
    }

    public Float checkValue(EditText et) {
        float result;
        String string = et.getText().toString();
        if (string.isEmpty()) {
            result = (float) -1;
            et.setText("0.0");
            et.requestFocus();
            Toast.makeText(mContext.getApplicationContext(),"The value must not be empty",
                    Toast.LENGTH_SHORT).show();
        } else try {
            result = Float.parseFloat(string);
            if (result < 0) {
                result = (float) -1;
                et.setText("0.0");
                et.requestFocus();
                Toast.makeText(mContext.getApplicationContext(),"The value must not be negative",
                        Toast.LENGTH_SHORT).show();
            } else if (result > 30) {
                result = (float) -1;
                et.requestFocus();
                Toast.makeText(mContext.getApplicationContext(),"The value must be less than 30",
                        Toast.LENGTH_SHORT).show();
            }} catch (Exception e) {
            result = (float) -1;
            et.setText("0.0");
            et.requestFocus();
            Toast.makeText(mContext.getApplicationContext(),"Wrong input",
                    Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public void datePicker(Context mContext, TextView et) {
        int year = 0;
        int month = 0;
        int day = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            final Calendar myCalendar = Calendar.getInstance();
            year = myCalendar.get(Calendar.YEAR);
            month = myCalendar.get(Calendar.MONTH);
            day = myCalendar.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog picker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                et.setText(String.format("%02d", dayOfMonth) + "." + String.format("%02d", (monthOfYear + 1)) + "." + year);
            }
        }, year, month, day);
        picker.getDatePicker();
        picker.show();
    }
}
