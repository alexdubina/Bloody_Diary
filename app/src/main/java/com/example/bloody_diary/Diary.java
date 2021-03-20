package com.example.bloody_diary;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Diary {
    private static long dateStart = 25569;
    private static long dateMultiplier = 86400000;
    private MaterialDatePicker materialDatePicker;
    private int _id;
    private int mdate;
    private int userid;
    private Float bbft;
    private Float abft;
    private Float blun;
    private Float alun;
    private Float bdin;
    private Float adin;
    private Float average;

    public Diary(int _id, int mdate, int userid, Float bbft, Float abft, Float blun,
                 Float alun, Float bdin, Float adin, Float average) {
        this._id     = _id;
        this.mdate   = mdate;
        this.userid  = userid;
        this.bbft    = bbft;
        this.abft    = abft;
        this.blun    = blun;
        this.alun    = alun;
        this.bdin    = bdin;
        this.adin    = adin;
        this.average = average;
    }

        // ==== Getters and Setters ====
    // ---- Field _ID of Table diary ----
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    // ---- Field mdate of Table diary ----
    public int getMdate() {
        return mdate;
    }

    // ---- Field mdate of Table diary ----
    public void setMdate(int mdate) {
        this.mdate = mdate;
    }

    // ---- Field userid of Table diary ----
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    // ---- Field bbft (before breakfast) of Table diary ----
    public Float getBbft() {
        return bbft;
    }

    public void setBbft(Float bbft) {
        this.bbft = bbft;
    }

    // ---- Field abft (after breakfast) of Table diary ----
    public Float getAbft() {
        return abft;
    }

    public void setAbft(Float abft) {
        this.abft = abft;
    }

    // ---- Field blun (before lunch) of Table diary ----
    public Float getBlun() {
        return blun;
    }

    public void setBlun(Float blun) {
        this.blun = blun;
    }

    // ---- Field alun (after lunch) of Table diary ----
    public Float getAlun() {
        return alun;
    }

    public void setAlun(Float alun) {
        this.alun = alun;
    }

    // ---- Field bdin (before dinner) of Table diary ----
    public Float getBdin() {
        return bdin;
    }

    public void setBdin(Float bdin) {
        this.bdin = bdin;
    }

    // ---- Field abft (after dinner) of Table diary ----
    public Float getAdin() {
        return adin;
    }

    public void setAdin(Float adin) {
        this.adin = adin;
    }

    // ---- Field average (average measurement) of Table diary ----
    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }

    // ---- Date converters ----
    public static String convertDateExcelToString(int dateExcel) {
        String dateString = "";
        Date dateJava = new Date();
        dateJava.setTime((dateExcel - dateStart)*dateMultiplier);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        dateString = sdf.format(dateJava);
        return dateString;
    }

    public static int convertStringToDateExcel(String dateString) {
        int dateExcel = 0;
        Date dateJava = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            dateJava = sdf.parse(dateString);
        } catch (ParseException e) {

        }
        dateExcel = (int) (dateJava.getTime()/dateMultiplier + dateStart);
        return dateExcel;
    }

    public static Float getAverage(List<Float> listAvg) {
        Float result = (float) 0;
        Float cur;
        int listSize = listAvg.size();
        for (int i = 0; i  < listSize; i++) {
            cur = listAvg.get(i);
            result += cur;
        }
        result = result/listSize;
        return result;
    }

}
