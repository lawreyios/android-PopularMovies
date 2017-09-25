package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Lawrey on 25/9/17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +

                        MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " STRING NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_POSTER + " STRING NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_RATINGS + " REAL NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_RELEASE + " STRING NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_SYNOPSIS + " STRING NOT NULL, " +
                        MoviesContract.MoviesEntry.COLUMN_TITLE + " STRING NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
