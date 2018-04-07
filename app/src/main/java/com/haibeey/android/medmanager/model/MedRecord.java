package com.haibeey.android.medmanager.model;

/**
 * Created by haibeey on 3/28/2018.
 */

public final class MedRecord {
    //the id
    private int ID;
    //the start period
    private int START_YEAR;
    private int START_MONTH;
    private int START_DAY;
    private int START_HOUR;
    private int START_MINUTE;
    //the end period
    private int END_YEAR;
    private int END_MONTH;
    private int END_DAY;
    private int END_HOUR;
    private int END_MINUTES;

    private String NAME;
    private String DESCRIPTION;

    @Override
    public int hashCode() {
        return getID();
    }

    public String getStartDate(){
        return getSTART_DAY()+"/"
                +getSTART_MONTH()+"/"
                +getSTART_YEAR();
    }

    public int getEND_MINUTES() {
        return END_MINUTES;
    }

    public int getSTART_MINUTE() {
        return START_MINUTE;
    }

    public String getEndDate(){
        return getEND_DAY()+"/"
                +getEND_MONTH()+"/"
                +getEND_YEAR();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MedRecord){
            MedRecord objCast=(MedRecord)obj;
            return objCast.getNAME().equals(getNAME()) &&
                    objCast.getDESCRIPTION().equals(getDESCRIPTION());
        }
        return false;
    }


    public int getEND_MONTH() {
        return END_MONTH;
    }

    public int getEND_DAY() {
        return END_DAY;
    }

    public int getEND_HOUR() {
        return END_HOUR;
    }

    public int getEND_YEAR() {
        return END_YEAR;
    }

    public int getID() {
        return ID;
    }

    public int getSTART_DAY() {
        return START_DAY;
    }

    public int getSTART_HOUR() {
        return START_HOUR;
    }

    public int getSTART_MONTH() {
        return START_MONTH;
    }

    public int getSTART_YEAR() {
        return START_YEAR;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public String getNAME() {
        return NAME;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public void setEND_DAY(int END_DAY) {
        this.END_DAY = END_DAY;
    }

    public void setEND_HOUR(int END_HOUR) {
        this.END_HOUR = END_HOUR;
    }

    public void setEND_MONTH(int END_MONTH) {
        this.END_MONTH = END_MONTH;
    }

    public void setEND_YEAR(int END_YEAR) {
        this.END_YEAR = END_YEAR;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setSTART_DAY(int START_DAY) {
        this.START_DAY = START_DAY;
    }

    public void setSTART_HOUR(int START_HOUR) {
        this.START_HOUR = START_HOUR;
    }

    public void setSTART_MONTH(int START_MONTH) {
        this.START_MONTH = START_MONTH;
    }

    public void setSTART_YEAR(int START_YEAR) {
        this.START_YEAR = START_YEAR;
    }

    public void setEND_MINUTES(int END_MINUTES) {
        this.END_MINUTES = END_MINUTES;
    }

    public void setSTART_MINUTE(int START_MINUTE) {
        this.START_MINUTE = START_MINUTE;
    }
}
