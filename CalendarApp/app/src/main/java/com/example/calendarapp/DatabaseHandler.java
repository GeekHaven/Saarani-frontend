package com.example.calendarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "eventStorage";
    private static final String TABLE_EVENTS = "events";
    private static final String KEY_ID = "id";
    private static final String KEY_EVENT_ID="event_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESC = "description";
    private static final String KEY_BY= "by_name";
    private static final String KEY_DATE="date";
    private static final String KEY_TIME="time";
    private static final String KEY_VENUE="venue";
    private static final String KEY_MARKER= "marker";
    private static final String KEY_PHOTO_URL="photo_url";
    private static final String KEY_ATTACHMENT_LIST="attachment_list";
    private static final String KEY_ATTACHMENT_NAME_LIST="attachment_name_list";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("+KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT," + KEY_DESC + " TEXT,"
                + KEY_BY + " TEXT," + KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_VENUE + " TEXT," + KEY_MARKER +" TEXT," + KEY_EVENT_ID+" TEXT," + KEY_PHOTO_URL+" TEXT," + KEY_ATTACHMENT_LIST +" TEXT," + KEY_ATTACHMENT_NAME_LIST+" TEXT"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    void addEvent(ListItems listItems) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,listItems.getName());
        values.put(KEY_DESC,listItems.getDesc());
        values.put(KEY_BY,listItems.getByName());
        values.put(KEY_DATE,listItems.getDate());
        values.put(KEY_TIME,listItems.getTime());
        values.put(KEY_VENUE,listItems.getVenue());
        values.put(KEY_MARKER,listItems.getMarker());
        values.put(KEY_EVENT_ID,listItems.getEventId());
        values.put(KEY_PHOTO_URL,listItems.getPhotoUrl());
        JSONObject json = new JSONObject();
        json.put("attachmentList", new JSONArray(listItems.getArrayList()));
        String arrayList = json.toString();
        values.put(KEY_ATTACHMENT_LIST,arrayList);
        JSONObject json1 = new JSONObject();
        json1.put("attachmentNameList", new JSONArray(listItems.getNameList()));
        String nameList = json1.toString();
        values.put(KEY_ATTACHMENT_NAME_LIST,nameList);

        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }
    ListItems getEvent(String id) throws JSONException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[] { KEY_ID,KEY_NAME,
                        KEY_DESC, KEY_BY,KEY_DATE, KEY_TIME,KEY_VENUE,KEY_MARKER,KEY_EVENT_ID,KEY_PHOTO_URL,KEY_ATTACHMENT_LIST,KEY_ATTACHMENT_NAME_LIST }, KEY_EVENT_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        JSONObject json_attach_list = null;
        if (cursor != null) {
            json_attach_list = new JSONObject(cursor.getString(10));
        }
        JSONArray attach_list = null;
        if (json_attach_list != null) {
            attach_list = json_attach_list.optJSONArray("attachmentList");
        }
        ArrayList<String> attachment_list=new ArrayList<>();
        if (attach_list != null) {
            for (int i = 0; i < attach_list.length(); i++) {
                String str_value=attach_list.optString(i);
                attachment_list.add(str_value);
            }
        }

        JSONObject json_attach_name_list = null;
        if (cursor != null) {
            json_attach_name_list = new JSONObject(cursor.getString(11));
        }
        JSONArray attach_name_list = null;
        if (json_attach_name_list != null) {
            attach_name_list = json_attach_name_list.optJSONArray("attachmentNameList");
        }
        ArrayList<String> attachment_name_list =new ArrayList<>();
        if (attach_name_list != null) {
            for (int i = 0; i < attach_name_list.length(); i++) {
                String str_value=attach_name_list.optString(i);
                attachment_name_list.add(str_value);
            }
        }

        ListItems item = new ListItems(cursor.getString(1),
                cursor.getString(2), cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),attachment_list);
        item.setId(Integer.parseInt(cursor.getString(0)));
        item.setPhotoUrl(cursor.getString(9));
        item.setNameList(attachment_name_list);
        return item;
    }

    public List<ListItems> getAllEvents() throws JSONException {
        List<ListItems> listItems =new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject json_attach_list = null;
                json_attach_list = new JSONObject(cursor.getString(10));
                JSONArray attach_list = null;
                attach_list = json_attach_list.optJSONArray("attachmentList");
                ArrayList<String> attachment_list=new ArrayList<>();
                if (attach_list != null) {
                    for (int i = 0; i < attach_list.length(); i++) {
                        String str_value=attach_list.optString(i);
                        attachment_list.add(str_value);
                    }
                }

                JSONObject json_attach_name_list = null;
                json_attach_name_list = new JSONObject(cursor.getString(11));
                JSONArray attach_name_list = null;
                attach_name_list = json_attach_name_list.optJSONArray("attachmentNameList");
                ArrayList<String> attachment_name_list =new ArrayList<>();
                if (attach_name_list != null) {
                    for (int i = 0; i < attach_name_list.length(); i++) {
                        String str_value=attach_name_list.optString(i);
                        attachment_name_list.add(str_value);
                    }
                }

                ListItems item = new ListItems(cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),attachment_list);
                item.setId(Integer.valueOf(cursor.getString(0)));
                item.setNameList(attachment_name_list);
                item.setPhotoUrl(cursor.getString(9));
                listItems.add(item);
            } while (cursor.moveToNext());
        }

        return listItems;
    }

    public int updateMarker(ListItems listItems,String marker) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,listItems.getName());
        values.put(KEY_DESC,listItems.getDesc());
        values.put(KEY_BY,listItems.getByName());
        values.put(KEY_DATE,listItems.getDate());
        values.put(KEY_TIME,listItems.getTime());
        values.put(KEY_VENUE,listItems.getVenue());
        values.put(KEY_MARKER,marker);
        values.put(KEY_EVENT_ID,listItems.getEventId());
        values.put(KEY_PHOTO_URL,listItems.getPhotoUrl());
        JSONObject json = new JSONObject();
        json.put("attachmentList", new JSONArray(listItems.getArrayList()));
        String arrayList = json.toString();
        values.put(KEY_ATTACHMENT_LIST,arrayList);
        JSONObject json1 = new JSONObject();
        json1.put("attachmentNameList", new JSONArray(listItems.getNameList()));
        String nameList = json1.toString();
        values.put(KEY_ATTACHMENT_NAME_LIST,nameList);

        return db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
                new String[] {String.valueOf(listItems.getId())});
    }

    public int updateSet(ListItems listItems,String id) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,listItems.getName());
        values.put(KEY_DESC,listItems.getDesc());
        values.put(KEY_BY,listItems.getByName());
        values.put(KEY_DATE,listItems.getDate());
        values.put(KEY_TIME,listItems.getTime());
        values.put(KEY_VENUE,listItems.getVenue());
        values.put(KEY_MARKER,listItems.getMarker());
        values.put(KEY_EVENT_ID,listItems.getEventId());
        values.put(KEY_PHOTO_URL,listItems.getPhotoUrl());
        JSONObject json = new JSONObject();
        json.put("attachmentList", new JSONArray(listItems.getArrayList()));
        String arrayList = json.toString();
        values.put(KEY_ATTACHMENT_LIST,arrayList);
        JSONObject json1 = new JSONObject();
        json1.put("attachmentNameList", new JSONArray(listItems.getNameList()));
        String nameList = json1.toString();
        values.put(KEY_ATTACHMENT_NAME_LIST,nameList);

        return db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
                new String[] {String.valueOf(listItems.getId())});
    }

    public void deleteEvent(ListItems listItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(listItem.getId()) });
        db.close();
    }
    public int getEventCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    public void deleteDatabase() throws JSONException {
        List<ListItems> allEvents =getAllEvents();
        for(int i=0;i<allEvents.size();i++){
            deleteEvent(allEvents.get(i));
        }
    }
}
