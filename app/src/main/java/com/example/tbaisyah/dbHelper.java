package com.example.tbaisyah;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

public class dbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2; // Database Version
    private static final String DATABASE_NAME = "tb_aisyah.db"; // Database Name
    private static final String TABLE_USER = "user"; // User table name
    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";
    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    private static final String TABLE_MOVIE = "movie";
    private static final String COLUMN_MOVIE_ID = "id_movie";
    private static final String COLUMN_MOVIE_TITLE = "title_movie";
    private static final String COLUMN_MOVIE_YEAR = "year_movie";
    private static final String COLUMN_MOVIE_GENRE = "genre_movie";
    private static final String COLUMN_MOVIE_DESC = "desc_movie";
    private static final String COLUMN_MOVIE_IMG = "img_movie";
    private String CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MOVIE + "("
            + COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_MOVIE_TITLE + " TEXT,"
            + COLUMN_MOVIE_YEAR + " TEXT," + COLUMN_MOVIE_GENRE + " TEXT," + COLUMN_MOVIE_DESC + " TEXT,"
            + COLUMN_MOVIE_IMG + " BLOB" + ")";
    private String DROP_MOVIE_TABLE = "DROP TABLE IF EXISTS " + TABLE_MOVIE;

    private Context context;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_MOVIE_TABLE);
        // Create tables again
        onCreate(db);
    }

    // Metode ini untuk membuat catatan user
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Memasukkan baris
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // Metode ini untuk mengambil semua user dan mengembalikan daftar catatan user
    public List<User> getAllUser() {
        // Array kolom yang akan diambil
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD
        };

        // Menyortir nama user
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query tabel user
        // Di sini fungsi query digunakan untuk mengambil catatan dari tabel user, fungsi ini berfungsi seperti kita menggunakan query sql
        // Permintaan SQL setara dengan fungsi permintaan :
        // SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
        Cursor cursor = db.query(TABLE_USER, // Tabel untuk query
                columns,
                null,      // Kolom untuk klausa WHERE
                null,   // Nilai untuk klausa WHERE
                null,       // Mengelompokkan baris
                null,        // Filter berdasarkan grup baris
                sortOrder);         // Urutan sortir


        // Melintasi semua baris dan menambahkan ke list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                // Menambahkan data user ke list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    // Metode untuk menghapus data user
    public int delete(String user_name){
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = {user_name};
        // Hapus data user dengan nama user
        int count = db.delete(TABLE_USER, COLUMN_USER_NAME + " = ?",whereArgs);
        return count;
    }

    // Metode untuk update data user
    public int update(String id, String nama, String email, String password){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_name", nama);
        values.put("user_email", email);
        values.put("user_password", password);
        String[] whereArgs = {id};
        int count = db.update(TABLE_USER,values,"user_id = ?",whereArgs);
        return count;
    }

    // Metode ini untuk memeriksa ada tidaknya email user
    public boolean checkUser(String email) {
        // Array kolom yang akan diambil yaitu id user
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,null,null,null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    // Metode ini untuk memeriksa ada tidaknya email dan password user
    public boolean checkUser(String email, String password) {
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USER,columns,selection,selectionArgs,null,null,null);

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public void insertDataMovie(String title_movie,String year_movie,String genre_movie,String desc_movie,byte[] img_movie){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO movie VALUES (NULL,?,?,?,?,?)";

        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, title_movie);
        statement.bindString(2, year_movie);
        statement.bindString(3, genre_movie);
        statement.bindString(4, desc_movie);
        statement.bindBlob(5, img_movie);

        statement.executeInsert();
    }

    public void updateDataMovie(String title_movie,String year_movie,String genre_movie,String desc_movie,byte[] img_movie,int id_movie) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE movie SET title_movie = ?, year_movie = ?, genre_movie = ?," +
                "desc_movie = ?, img_movie = ? WHERE id_movie = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, title_movie);
        statement.bindString(2, year_movie);
        statement.bindString(3, genre_movie);
        statement.bindString(4, desc_movie);
        statement.bindBlob(5, img_movie);
        statement.bindDouble(6, (double)id_movie);

        statement.execute();
        database.close();
    }

    public  void deleteDataMovie(int id_movie) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM movie WHERE id_movie = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id_movie);

        statement.execute();
        database.close();
    }

    public Cursor getDataMovie(String sql){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, null);
    }
}
