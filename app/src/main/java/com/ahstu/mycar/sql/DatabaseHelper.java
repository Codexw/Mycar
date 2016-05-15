package com.ahstu.mycar.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xuning on 2016/5/6.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table carinfo(car_number text primary key,car_brand text,car_sign text" +
                ",car_model text,car_enginerno text,car_level text,car_mile integer,car_gas integer,car_enginerstate text,car_shiftstate text,car_light text" +
                ",car_start boolean,car_lock boolean,car_door boolean,car_air boolean)";

        db.execSQL(sql);
    }


}
