package com.tae.james.tableexample1.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamessmith on 15/09/2016.
 */
public class Database extends SQLiteOpenHelper {

    private static final String database = "myDatabase";
    private static final int version = 1;

    private static final String table = "myTable";
    private static final String id = "id";
    private static final String forename = "forename";
    private static final String surname = "surname";
    private static final String image = "image";
    private static final String role = "role";
    private static final String dob = "dob";
    private static final String TAG = Database.class.getName();
    private SQLiteDatabase db;

    public Database(Context context) {
        super(context, database, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(TAG, "onCreate has been invoked");
        String db = "CREATE TABLE " + table + "(" + id + " integer primary key autoincrement, "
                + forename + " TEXT NOT NULL, " + surname + " TEXT NOT NULL, " + role + " TEXT NOT NULL, "
                + dob + " TEXT NOT NULL, " + image + " BLOB DEFAULT NULL)";
        sqLiteDatabase.execSQL(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " +table);
        onCreate(sqLiteDatabase);
    }

    private boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
                new String[] {"table", tableName});

        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public List<DatabaseModel> fetchData() {
        DatabaseModel databaseModel = new DatabaseModel();
        List<DatabaseModel> databases = new ArrayList<>();
        db = this.getReadableDatabase();
        String query = "SELECT * FROM " + table;
        Cursor cursor;;

        if (db.isOpen()) {

            if (!isTableExists(db, table)) {
                onCreate(db);
            }
                cursor = db.rawQuery(query, null);

                if (cursor != null) {
                    Log.v(TAG, "cursor is not null");
                    Bitmap bitmap;

                    if (cursor.isLast()) {
                        cursor.moveToFirst();
                    }

                    int i = 0;

                    while (cursor.moveToNext()) {
                        databaseModel.setForename(cursor.getString(1));
                        databaseModel.setSurname(cursor.getString(2));
                        databaseModel.setRole(cursor.getString(3));
                        databaseModel.setDob(cursor.getString(4));

                        if(cursor.getBlob(5) != null) {
                            bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(5), 0, cursor.getBlob(5).length);
                            databaseModel.setImage(bitmap);
                        }

                        databases.add(databaseModel);
                        Log.v(TAG, "got forename: " + databases.get(i).getForename());
                        i++;
                    }
                    cursor.close();
                    db.close();
                }
            }

        return databases;
    }

    public void newEntry(DatabaseModel databaseModel) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.v(TAG, "new forename: " + databaseModel.getForename());
        String query = "SELECT * FROM " + table + " WHERE " + forename + "='" + databaseModel.getForename() + "'";
        boolean exists = false;
        Cursor cursor;

        if (db.isOpen()) {

            Log.v(TAG, "new Entry");
            cursor = db.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.isLast()) {
                    cursor.moveToFirst();
                }
                if (cursor.getCount() >= 1) {
                    Log.v(TAG, "exists " + forename);
                    exists = true;
                } else {
                    exists = false;
                }
            }

            if (!exists) {
                Log.v(TAG, "adding new account with name of:  " + databaseModel.getForename());
                values.put(forename, databaseModel.getForename());
                values.put(surname, databaseModel.getSurname());
                values.put(role, databaseModel.getRole());
                values.put(dob, databaseModel.getDob());
                int bytes = databaseModel.getImage().getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                databaseModel.getImage().copyPixelsToBuffer(buffer);
                byte[] imageData = buffer.array();
                values.put(image, imageData);
                db.insert(table, null, values);
            }
            db.close(); // Close database connection.
            cursor.close();//Close cursor.
        }
    }
}
