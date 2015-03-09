package it.neokree.materialtabtest;

/**
 * Created by efanovev on 15.02.2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.Calendar;

public class DB {
    private static final String DB_NAME = "cur_db3";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "MainData";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TOOL = "tool";
    public static final String COLUMN_TOOLFROM = "toolfrom";
    public static final String COLUMN_TOOLTO = "toolto";
    public static final String COLUMN_SIGNAL = "signal";
    public static final String COLUMN_TP = "tp";
    public static final String COLUMN_PROB = "prob";
    public static final String COLUMN_RATE = "rate";

    private static final String DB_CREATE_TABLE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATE + " integer, " +
                    COLUMN_TOOL + " text, " +
                    COLUMN_TOOLFROM + " text, " +
                    COLUMN_TOOLTO + " text, " +
                    COLUMN_SIGNAL + " text, " +
                    COLUMN_TP + " integer, " +
                    COLUMN_PROB + " real, " +
                    COLUMN_RATE + " real " +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private Cursor tmpCursor;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(Long date, String tool, int tp, double prob, String signal, double rate) {

        Long id = getItemId(date, tool, tp, prob);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TOOL, tool);
        cv.put(COLUMN_TOOLFROM, TextUtils.substring(tool, 0, 3));
        cv.put(COLUMN_TOOLTO, TextUtils.substring(tool, 3, 6));
        cv.put(COLUMN_SIGNAL, signal);
        cv.put(COLUMN_TP, tp);
        cv.put(COLUMN_PROB, prob);
        cv.put(COLUMN_RATE, rate);

        if (id == 0)
            mDB.insert(DB_TABLE, null, cv);
        else
            mDB.update(DB_TABLE, cv, COLUMN_ID + "=" + id.toString(), null);
    }

    public Long getItemId(Long date, String tool, int tp, double prob ){

        String sqlQuery = "select " +
                DB_TABLE+"."+ COLUMN_ID + " " +
                "from " + DB_TABLE + " AS " + DB_TABLE + " " +
                "where " + COLUMN_DATE + " = ?" + " " +
                "and " + COLUMN_TOOL + " = ?" + " " +
                "and " + COLUMN_TP + " = ?" + " " +
                "and " + COLUMN_PROB + " = ?";

        tmpCursor = mDB.rawQuery(sqlQuery, new String[]{date.toString(), tool, Integer.toString(tp), Double.toString(prob)});
        Long result = 0L;
        if (tmpCursor.moveToFirst())
            result = tmpCursor.getLong(tmpCursor.getColumnIndex(COLUMN_ID));
        tmpCursor.close();
        return result;

    }


    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {

            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TABLE);
            String tool = "USDRUB";
            Calendar cal = Calendar.getInstance();

            ContentValues cv = new ContentValues();

            cv.put(COLUMN_DATE, cal.getTimeInMillis()/1000);
            cv.put(COLUMN_TOOL, tool);
            cv.put(COLUMN_TOOLFROM, TextUtils.substring(tool, 0, 3));
            cv.put(COLUMN_TOOLTO, TextUtils.substring(tool, 3, 6));
            cv.put(COLUMN_SIGNAL, "up");
            cv.put(COLUMN_TP, 50);
            cv.put(COLUMN_PROB, 60);
            cv.put(COLUMN_RATE, 62.6254);

            db.insert(DB_TABLE, null, cv);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        }
    }
}
