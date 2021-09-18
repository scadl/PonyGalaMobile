package net.scadsdnd.ponygala;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class caheDB extends SQLiteOpenHelper {

    public final static String TAB = "images";
    public final String[] COLS = {"ind", "full", "title", "author"};

    public caheDB(Context context) {
        super(context, "ag_cache.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TAB + " ("+COLS[0]+" INTEGER, " + COLS[1] + " TEXT, " + COLS[2] + " TEXT, " + COLS[3] + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB + ";");
        onCreate(sqLiteDatabase);
    }
}
