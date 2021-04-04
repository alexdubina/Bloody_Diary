package com.example.bloody_diary;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaryDialog extends AppCompatDialogFragment {
    private String activityName;
    private int diaryDate;
    private final String ACTIVITY_MAIN = "ActivityMain";
    private final String LIST_DIARY_ACTIVITY = "ListDiaryActivity";
    private final String SETTINGS_ACTIVITY = "SettingsActivity";
    private String title;
    private String message;
    private String button1String;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("myLogs", "DiaryDialog: Beginning");
        if (activityName.equals(SETTINGS_ACTIVITY)) {
            if (diaryDate == 1) {
                title = "Clearing of Data!";
                message = "Are you sure?";
                button1String = "Clear";
            } else if (diaryDate == 2) {
                title = "About!";
                message = (String) getText(R.string.about_story);
                button1String = "OK";
            } else if (diaryDate == 3) {
                title = "Your attention!";
                message = (String) getText(R.string.currency_story) + "1.00 USD = 28.00 UAH";
                button1String = "OK";
            }
        } else {
            title = "Deleting of the record!";
            message = "Data for " + Diary.convertDateExcelToString(diaryDate) + " will be deleted?";
            button1String = "Delete";
        }
        String button2String = "Cancel";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);  // заголовок
        if (activityName.equals(SETTINGS_ACTIVITY)) {
            if (diaryDate == 1) {builder.setIcon(R.drawable.swiper);}
        } else {
            builder.setIcon(R.drawable.recirclebin);
        }
        builder.setMessage(message); // сообщение
        Log.d("myLogs", "DiaryDialog: activityName = " + activityName);

        builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (activityName) {
                    case ACTIVITY_MAIN:
                        ((MainActivity) getActivity()).okClicked();
                        Toast.makeText(getActivity(), "Record was deleted", Toast.LENGTH_LONG).show();
                        break;
                    case LIST_DIARY_ACTIVITY:
                        ((ListDiaryActivity) getActivity()).okClicked();
                        Toast.makeText(getActivity(), "Record was deleted", Toast.LENGTH_LONG).show();
                        break;
                    case SETTINGS_ACTIVITY:
                        ((SettingsActivity) getActivity()).okClicked();
                        break;
                }
            }
        });
        if (!((diaryDate == 2) | (diaryDate == 3))) {
            builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    switch (activityName) {
                        case ACTIVITY_MAIN:
                            ((MainActivity) getActivity()).cancelClicked();
                            Toast.makeText(getActivity(), "Action canceled", Toast.LENGTH_LONG).show();
                            break;
                        case LIST_DIARY_ACTIVITY:
                            ((ListDiaryActivity) getActivity()).cancelClicked();
                            Toast.makeText(getActivity(), "Action canceled", Toast.LENGTH_LONG).show();
                            break;
                        case SETTINGS_ACTIVITY:
                            ((SettingsActivity) getActivity()).cancelClicked();
                            Toast.makeText(getActivity(), "Action canceled", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        }
        builder.setCancelable(true);

        return builder.create();
    }

    public void setArguments(String dialogTag, int dialogDiaryDate) {
        activityName = dialogTag;
        diaryDate = dialogDiaryDate;
    }
}
