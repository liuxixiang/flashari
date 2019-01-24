package com.lxh.processmodule.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.widget.ImageView;

import java.util.Calendar;

public class FlashAirFileInfoBean implements Parcelable {

    public static final int ATTR_MASK_ARCHIVE = 0x00000020;
    public static final int ATTR_MASK_DIRECTORY = 0x00000010;
    public static final int ATTR_MASK_VOLUME = 0x00000008;
    public static final int ATTR_MASK_SYSTEM_FILE = 0x00000004;
    public static final int ATTR_MASK_HIDDEN_FILE = 0x00000002;
    public static final int ATTR_MASK_READ_ONLY = 0x00000001;

    private String dir;
    private String fileName;
    private String size;
    private int attribute;
    private String thumbnailUrl;
    private int year;
    private int month;
    private int day;
    private int hourOfDay;
    private int minute;
    private int second;
    private String date;

    public FlashAirFileInfoBean(String info, String dir) {
        int start;
        int end;

        start = info.lastIndexOf(",");
        int time = Integer.parseInt(info.substring(start + 1).trim());

        end = start;
        start = info.lastIndexOf(",", end - 1);
        int date = Integer.parseInt(info.substring(start + 1, end).trim());

        end = start;
        start = info.lastIndexOf(",", end - 1);
        this.attribute = Integer.parseInt(info.substring(start + 1, end).trim());

        end = start;
        start = info.lastIndexOf(",", end - 1);
        this.size = info.substring(start + 1, end);

        end = start;
        int dirLength = dir.length();
        if (dir.equals("/")) {
            dirLength = 0;
        }
        start = info.indexOf(",", dirLength);
        this.fileName = info.substring(start + 1, end);

        this.dir = dir;

        this.year = ((date >> 9) & 0x0000007f) + 1980;
        this.month = (date >> 5) & 0x0000000f - 1;
        this.day = (date) & 0x0000001f;

        this.hourOfDay = (time >> 11) & 0x0000001f;
        this.minute = (time >> 5) & 0x0000003f;
        this.second = ((time) & 0x0000001f) * 2;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hourOfDay, minute, second);
        this.date = DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar) + "";

    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public boolean isDirectory() {
        return (attribute & ATTR_MASK_DIRECTORY) > 0;
    }

    @Override
    public String toString() {
        return "DIR=" + dir + " FILENAME=" + fileName + " SIZE=" + size
                + " ATTRIBUTE=" + attribute + " DATE="
                + date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dir);
        dest.writeString(this.fileName);
        dest.writeString(this.size);
        dest.writeInt(this.attribute);
        dest.writeString(this.thumbnailUrl);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.hourOfDay);
        dest.writeInt(this.minute);
        dest.writeInt(this.second);
        dest.writeString(this.date);
    }

    protected FlashAirFileInfoBean(Parcel in) {
        this.dir = in.readString();
        this.fileName = in.readString();
        this.size = in.readString();
        this.attribute = in.readInt();
        this.thumbnailUrl = in.readString();
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.hourOfDay = in.readInt();
        this.minute = in.readInt();
        this.second = in.readInt();
        this.date = in.readString();
    }

    public static final Creator<FlashAirFileInfoBean> CREATOR = new Creator<FlashAirFileInfoBean>() {
        @Override
        public FlashAirFileInfoBean createFromParcel(Parcel source) {
            return new FlashAirFileInfoBean(source);
        }

        @Override
        public FlashAirFileInfoBean[] newArray(int size) {
            return new FlashAirFileInfoBean[size];
        }
    };
}
