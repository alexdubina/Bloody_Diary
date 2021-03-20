package com.example.bloody_diary;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.*;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DBNAME      = "mydb.sqlite3";
    public static final String DBLOCATION  = "/data/data/com.example.bloody_diary/databases/";
    public static final String TBL_DIARY   = "diary";
    public static final String TBL_GH      = "gh";

    public static final String KEY_ID      = "_id";
    public static final String KEY_MDATE   = "mdate";
    public static final String KEY_USERID  = "userid";
    public static final String KEY_BBFT    = "bbft";
    public static final String KEY_ABFT    = "abft";
    public static final String KEY_BLUN    = "blun";
    public static final String KEY_ALUN    = "alun";
    public static final String KEY_BDIN    = "bdin";
    public static final String KEY_ADIN    = "adin";
    public static final String KEY_AVERAGE = "average";

    public static final String KEY_ADATE   = "adate";
    public static final String KEY_GH      = "gh";


    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public DataBaseHelper(Context context) {
        super(context, DBNAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Database mydb.sqlite3 treatment
    public void createDBStructure() {
        Log.d("myLogs", "DataBaseHelper: Creating of database " + DBNAME);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdf.format(Calendar.getInstance().getTime());
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        // Creating of table DIARY
        mDatabase.execSQL("CREATE TABLE " + TBL_DIARY + "(" +
                KEY_ID      + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                KEY_MDATE   + " INTEGER NOT NULL," +
                KEY_USERID  + " INTEGER NOT NULL," +
                KEY_BBFT    + " INTEGER NOT NULL," +
                KEY_ABFT    + " INTEGER NOT NULL," +
                KEY_BLUN    + " INTEGER NOT NULL," +
                KEY_ALUN    + " INTEGER NOT NULL," +
                KEY_BDIN    + " INTEGER NOT NULL," +
                KEY_ADIN    + " INTEGER NOT NULL," +
                KEY_AVERAGE + " INTEGER NOT NULL" + ")");

        mDatabase.execSQL("CREATE TABLE " + TBL_GH + "(" +
                KEY_ID      + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                KEY_ADATE   + " INTEGER NOT NULL," +
                KEY_USERID  + " INTEGER NOT NULL," +
                KEY_GH      + " INTEGER NOT NULL" + ")");
        this.closeDatabase();
        this.insertBlankDiaryRow(date);
        this.insertBlankGhRow(date);
        Log.d("myLogs", "DataBaseHelper: Database " + DBNAME + " created ");
    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void dropDatabase() {
        this.closeDatabase();
        mContext.deleteDatabase(DBNAME);
        Log.d("myLogs", "DataBaseHelper: Database dropped");
    }

    public void clearDatabase() {
        this.dropDatabase();
        this.createDBStructure();
        Log.d("myLogs", "DataBaseHelper: Database cleared");
        Toast.makeText(mContext.getApplicationContext(), "Database cleared", Toast.LENGTH_LONG).show();
    }

    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public void checkDBFile() {
        //Check exists database
        File database = mContext.getDatabasePath(DBNAME);
        Log.d("myLogs", "DataBaseHelper: Check exists database " + DBNAME);
        if(!database.exists()) {
            Log.d("myLogs", "DataBaseHelper: Database do not exist");
            this.getReadableDatabase();
            //Copy db
            if(copyDatabase(mContext)) {
                Log.d("myLogs", "DataBaseHelper: Copied Database is ready to use");
            } else {
                Log.d("myLogs", "DataBaseHelper: Creating default database " + DBNAME);
                this.createDBStructure();
            }
        } else {
            Log.d("myLogs", "DataBaseHelper: Database "+ DBNAME +" is exist");
        }
    }

    private boolean copyDatabase(Context context) {
        try {
            Log.d("myLogs", "DataBaseHelper: Start copying of database");
            InputStream inputStream = context.getAssets().open(DBNAME);
            String outFileName = DataBaseHelper.DBLOCATION + DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[]buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("myLogs", "DataBaseHelper: DB file copied");
            return true;
        }catch (Exception e) {
            Log.d("myLogs", "DataBaseHelper: Fail in copying of database");
            e.printStackTrace();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void exportDiaryToCSV(Activity activity) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, TBL_DIARY + ".csv");
        int permissionStatus = ContextCompat.checkSelfPermission(mContext, permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus == PERMISSION_GRANTED) {
            Log.w("myLogs", "DataBaseHelper: Permission to write file granted");
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {permission.WRITE_EXTERNAL_STORAGE}, 1);                    ;
            Toast.makeText(mContext.getApplicationContext(), "Permission was not granted. " +
                    "Try once more", Toast.LENGTH_LONG).show();
            Log.w("myLogs", "DataBaseHelper: Ask for permission to write file granted");
        }
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            openDatabase();
            Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY,null);
            csvWrite.writeNext(cursor.getColumnNames());
            while(cursor.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7),
                        cursor.getString(8), cursor.getString(9)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            cursor.close();
            closeDatabase();
            Toast.makeText(mContext.getApplicationContext(), "File " +TBL_DIARY +
                    ".csv was created successfully", Toast.LENGTH_LONG).show();
            Log.w("myLogs", "DataBaseHelper: DB file exported");
        }
        catch(Exception sqlEx)
        {
            Log.e("DataBaseHelper: ", sqlEx.getMessage(), sqlEx);
        }
    }

    // Table GH treatment
    private void insertBlankGhRow(String date) {
        this.openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ADATE, Diary.convertStringToDateExcel(date));
        contentValues.put(KEY_USERID, 1);
        contentValues.put(KEY_GH, 65);
        mDatabase.insert(DataBaseHelper.TBL_GH, null, contentValues);
        this.closeDatabase();
        Log.d("myLogs", "DataBaseHelper: Blank row is inserted to table: " + TBL_GH);
    }

    public long addGH(int date, int userid, float valueGH) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ADATE,  date);
        contentValues.put(KEY_USERID, userid);
        contentValues.put(KEY_GH,     (Math.round(valueGH*10)));
        this.openDatabase();
        long returnValue = mDatabase.insert(TBL_GH, null, contentValues);
        this.closeDatabase();
        return returnValue;
    }

    public boolean deleteGHByDate(int GHDate) {
        this.openDatabase();
        int result = mDatabase.delete(TBL_GH,  KEY_ADATE + " =?",
                new String[]{String.valueOf(GHDate)});
        this.closeDatabase();
        return result !=0;
    }

    public float getLastGH() {
        float valueGH;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_GH +" ORDER BY " + KEY_ADATE,
                null);
        cursor.moveToLast();
        valueGH = cursor.getInt(3);
        //Only 1 result
        cursor.close();
        this.closeDatabase();
        Log.d("myLogs", "DataBaseHelper: getLastGH: " + valueGH);

        return valueGH/10;
    }

    // Table DIARY treatment
    private void insertBlankDiaryRow(String date) {
        this.openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MDATE,   Diary.convertStringToDateExcel(date));
        contentValues.put(KEY_USERID,  1);
        contentValues.put(KEY_BBFT,    72);
        contentValues.put(KEY_ABFT,    0);
        contentValues.put(KEY_BLUN,    0);
        contentValues.put(KEY_ALUN,    0);
        contentValues.put(KEY_BDIN,    0);
        contentValues.put(KEY_ADIN,    0);
        contentValues.put(KEY_AVERAGE, 72);
        mDatabase.insert(DataBaseHelper.TBL_DIARY, null, contentValues);
        this.closeDatabase();
        Log.d("myLogs", "DataBaseHelper: Blank row is inserted to table: " + TBL_DIARY);
    }

    public List<Diary> getListDiary() {
        Diary diary = null;
        List<Diary> diaryList = new ArrayList<>();
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" ORDER BY " + KEY_MDATE,
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            diary = new Diary(cursor.getInt(0), cursor.getInt(1),
                    cursor.getInt(2), cursor.getFloat(3)/10,
                    cursor.getFloat(4)/10, cursor.getFloat(5)/10,
                    cursor.getFloat(6)/10, cursor.getFloat(7)/10,
                    cursor.getFloat(8)/10, cursor.getFloat(9)/10);
            diaryList.add(diary);
            cursor.moveToNext();
        }
        cursor.close();
        this.closeDatabase();
        Log.d("myLogs", "DataBaseHelper: Return of diaryList");
        return diaryList;
    }

    public int getDiaryCount() {
        this.openDatabase();
        long counter = DatabaseUtils.queryNumEntries(mDatabase, TBL_DIARY);
        this.closeDatabase();
        return (int) counter;
    }

    public Diary getFirstDiary() {
        Diary diary = null;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" ORDER BY " + KEY_MDATE,
                null);
        cursor.moveToFirst();
        diary = new Diary(cursor.getInt(0), cursor.getInt(1),
                cursor.getInt(2), cursor.getFloat(3)/10,
                cursor.getFloat(4)/10, cursor.getFloat(5)/10,
                cursor.getFloat(6)/10, cursor.getFloat(7)/10,
                cursor.getFloat(8)/10, cursor.getFloat(9)/10);
        //Only 1 result
        cursor.close();
        this.closeDatabase();
        return diary;
    }

    public Diary getLastDiary() {
        Diary diary = null;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" ORDER BY " + KEY_MDATE,
                null);
        cursor.moveToLast();
        diary = new Diary(cursor.getInt(0), cursor.getInt(1),
                cursor.getInt(2), cursor.getFloat(3)/10,
                cursor.getFloat(4)/10, cursor.getFloat(5)/10,
                cursor.getFloat(6)/10, cursor.getFloat(7)/10,
                cursor.getFloat(8)/10, cursor.getFloat(9)/10);
        //Only 1 result
        cursor.close();
        this.closeDatabase();
        return diary;
    }

    public Diary getPreviousDiary(int diaryDate) {
        Diary diary = null;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" ORDER BY " + KEY_MDATE,
                null);
        cursor.moveToFirst();
        int curDate = cursor.getInt(1);
        while (curDate != diaryDate) {
            cursor.moveToNext();
            curDate = cursor.getInt(1);
        }
        cursor.moveToPrevious();
          if (cursor.isBeforeFirst()) {
              Float reg = Float.valueOf(0);
              diary = new Diary( 0, 0,0, reg, reg, reg, reg, reg, reg, reg);
        } else {
              diary = new Diary(cursor.getInt(0), cursor.getInt(1),
                      cursor.getInt(2), cursor.getFloat(3)/10,
                      cursor.getFloat(4)/10, cursor.getFloat(5)/10,
                      cursor.getFloat(6)/10, cursor.getFloat(7)/10,
                      cursor.getFloat(8)/10, cursor.getFloat(9)/10);
        }
        //Only 1 result
        cursor.close();
        this.closeDatabase();
        return diary;
    }

    public Diary getNextDiary(int diaryDate) {
        Diary diary = null;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" ORDER BY " + KEY_MDATE,
                null);
        cursor.moveToFirst();
        while (cursor.getInt(1) != diaryDate) {
            cursor.moveToNext();
        }
        cursor.moveToNext();
        if (cursor.isAfterLast()) {
            cursor.moveToLast();
        }
        diary = new Diary(cursor.getInt(0), cursor.getInt(1),
                cursor.getInt(2), cursor.getFloat(3)/10,
                cursor.getFloat(4)/10, cursor.getFloat(5)/10,
                cursor.getFloat(6)/10, cursor.getFloat(7)/10,
                cursor.getFloat(8)/10, cursor.getFloat(9)/10);
        //Only 1 result
        cursor.close();
        this.closeDatabase();
        return diary;
    }

    public Diary getDiaryById(int id) {
        Diary diary = null;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM DIARY WHERE _ID = ?",
                new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        diary = new Diary(cursor.getInt(0), cursor.getInt(1),
                cursor.getInt(2), cursor.getFloat(3)/10,
                cursor.getFloat(4)/10, cursor.getFloat(5)/10,
                cursor.getFloat(6)/10, cursor.getFloat(7)/10,
                cursor.getFloat(8)/10, cursor.getFloat(9)/10);
        //Only 1 resul
        cursor.close();
        this.closeDatabase();
        return diary;
    }

    public Diary getDiaryByDate(int date) {
        Diary diary = null;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM DIARY WHERE " + KEY_MDATE + " = ?",
                new String[]{String.valueOf(date)});
        cursor.moveToFirst();
        diary = new Diary(cursor.getInt(0), cursor.getInt(1),
                cursor.getInt(2), cursor.getFloat(3)/10,
                cursor.getFloat(4)/10, cursor.getFloat(5)/10,
                cursor.getFloat(6)/10, cursor.getFloat(7)/10,
                cursor.getFloat(8)/10, cursor.getFloat(9)/10);
        //Only 1 resul
        cursor.close();
        this.closeDatabase();
        return diary;
    }

    public float getDiaryAverage(int dateBegin, int dateEnd) {
        float result;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT AVG(" + KEY_AVERAGE +") FROM "+ TBL_DIARY +
                        " WHERE " + KEY_MDATE + " BETWEEN ? AND ?",
                new String[]{String.valueOf(dateBegin), String.valueOf(dateEnd)});
        cursor.moveToFirst();
        result = (cursor.getFloat(0)/10);
        //Only 1 resul
        cursor.close();
        this.closeDatabase();
        return result;
    }

    public long updateDiary(Diary diary) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MDATE,   diary.getMdate());
        contentValues.put(KEY_USERID,  diary.getUserid());
        contentValues.put(KEY_BBFT,    (Math.round(diary.getBbft()*10)));
        contentValues.put(KEY_ABFT,    (Math.round(diary.getAbft()*10)));
        contentValues.put(KEY_BLUN,    (Math.round(diary.getBlun()*10)));
        contentValues.put(KEY_ALUN,    (Math.round(diary.getAlun()*10)));
        contentValues.put(KEY_BDIN,    (Math.round(diary.getBdin()*10)));
        contentValues.put(KEY_ADIN,    (Math.round(diary.getAdin()*10)));
        contentValues.put(KEY_AVERAGE, (Math.round(diary.getAverage()*10)));
        String[] whereArgs = {Integer.toString(diary.getMdate())};
        this.openDatabase();
        long returnValue = mDatabase.update(TBL_DIARY, contentValues, KEY_MDATE + "=?", whereArgs);
        this.closeDatabase();
        return returnValue;
    }

    public long addDiary(Diary diary) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MDATE,   diary.getMdate());
        contentValues.put(KEY_USERID,  diary.getUserid());
        contentValues.put(KEY_BBFT,    (Math.round(diary.getBbft()*10)));
        contentValues.put(KEY_ABFT,    (Math.round(diary.getAbft()*10)));
        contentValues.put(KEY_BLUN,    (Math.round(diary.getBlun()*10)));
        contentValues.put(KEY_ALUN,    (Math.round(diary.getAlun()*10)));
        contentValues.put(KEY_BDIN,    (Math.round(diary.getBdin()*10)));
        contentValues.put(KEY_ADIN,    (Math.round(diary.getAdin()*10)));
        contentValues.put(KEY_AVERAGE, (Math.round(diary.getAverage()*10)));
        this.openDatabase();
        long returnValue = mDatabase.insert(TBL_DIARY, null, contentValues);
        this.closeDatabase();
        return returnValue;
    }

    public boolean deleteDiaryById(int id) {
        this.openDatabase();
        int result = mDatabase.delete(TBL_DIARY,  "_ID =?",
                new String[]{String.valueOf(id)});
        this.closeDatabase();
        return result !=0;
    }

    public boolean deleteDiaryByDate(int diaryDate) {
        this.openDatabase();
        int result = mDatabase.delete(TBL_DIARY,  KEY_MDATE + " =?",
                new String[]{String.valueOf(diaryDate)});
        this.closeDatabase();
        return result !=0;
    }

    public boolean isFirstDiary(int diaryDate) {
        Diary diary = null;
        boolean isFirst = false;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" ORDER BY " + KEY_MDATE,
                null);
        cursor.moveToFirst();
        if (cursor.getInt(1) == diaryDate) {
            isFirst = true;
        }
        cursor.close();
        this.closeDatabase();
        return isFirst;
    }

    public boolean isLastDiary(int diaryDate) {
        Diary diary = null;
        boolean isLast = false;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" ORDER BY " + KEY_MDATE,
                null);
        cursor.moveToLast();
        if (cursor.getInt(1) == diaryDate) {
            isLast = true;
        }
        cursor.close();
        this.closeDatabase();
        return isLast;
    }

    public boolean isDiaryExist(int date) {
        Diary diary = null;
        boolean isExist = true;
        this.openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TBL_DIARY +" WHERE " + KEY_MDATE + " = ?",
                new String[]{String.valueOf(date)});
        cursor.moveToFirst();
        if (cursor.isBeforeFirst()) {
            isExist = false;
        }
        cursor.close();
        this.closeDatabase();
        return isExist;
    }
}
