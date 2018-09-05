package net.victium.xelg.notatry.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NotATryContentProvider extends ContentProvider {
    public static final int CHARACTER_STATUS = 100;
    public static final int CHARACTER_STATUS_WITH_ID = 101;
    public static final int DUSK_SUMMARY = 200;
    public static final int DUSK_SUMMARY_WITH_ID = 201;
    public static final int ACTIVE_SHIELDS = 300;
    public static final int ACTIVE_SHIELDS_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(
                NotATryContract.AUTHORITY,
                NotATryContract.PATH_CHARACTER_STATUS,
                CHARACTER_STATUS
        );
        uriMatcher.addURI(
                NotATryContract.AUTHORITY,
                NotATryContract.PATH_CHARACTER_STATUS + "/#",
                CHARACTER_STATUS_WITH_ID
        );
        uriMatcher.addURI(
                NotATryContract.AUTHORITY,
                NotATryContract.PATH_DUSK_SUMMARY,
                DUSK_SUMMARY
        );
        uriMatcher.addURI(
                NotATryContract.AUTHORITY,
                NotATryContract.PATH_DUSK_SUMMARY + "/#",
                DUSK_SUMMARY_WITH_ID
        );
        uriMatcher.addURI(
                NotATryContract.AUTHORITY,
                NotATryContract.PATH_ACTIVE_SHIELDS,
                ACTIVE_SHIELDS
        );
        uriMatcher.addURI(
                NotATryContract.AUTHORITY,
                NotATryContract.PATH_ACTIVE_SHIELDS + "/#",
                ACTIVE_SHIELDS_WITH_ID
        );

        return uriMatcher;
    }

    private NotATryDbHelper mNotATryDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mNotATryDbHelper = new NotATryDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = mNotATryDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case CHARACTER_STATUS:
                returnCursor = sqLiteDatabase.query(
                        NotATryContract.CharacterStatusEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case DUSK_SUMMARY:
                returnCursor = sqLiteDatabase.query(
                        NotATryContract.DuskLayersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ACTIVE_SHIELDS:
                returnCursor = sqLiteDatabase.query(
                        NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase sqLiteDatabase = mNotATryDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        long id;
        SQLException  sqlException = new SQLException("Failed to insert into " + uri);

        switch (match) {
            case CHARACTER_STATUS:
                id = sqLiteDatabase.insert(
                        NotATryContract.CharacterStatusEntry.TABLE_NAME,
                        null,
                        values
                );

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(NotATryContract.CharacterStatusEntry.CONTENT_URI, id);
                } else {
                    throw sqlException;
                }
                break;

            case DUSK_SUMMARY:
                id = sqLiteDatabase.insert(
                        NotATryContract.DuskLayersEntry.TABLE_NAME,
                        null,
                        values
                );

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(NotATryContract.DuskLayersEntry.CONTENT_URI, id);
                } else {
                    throw sqlException;
                }
                break;

            case ACTIVE_SHIELDS:
                id = sqLiteDatabase.insert(
                        NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        null,
                        values
                );

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(NotATryContract.ActiveShieldsEntry.CONTENT_URI, id);
                } else {
                    throw sqlException;
                }
                break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mNotATryDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        String id;

        switch (match) {
            case CHARACTER_STATUS_WITH_ID:
                id = uri.getPathSegments().get(1);
                rowsDeleted = sqLiteDatabase.delete(
                        NotATryContract.CharacterStatusEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{id}
                );
                break;

            case DUSK_SUMMARY_WITH_ID:
                id = uri.getPathSegments().get(1);
                rowsDeleted = sqLiteDatabase.delete(
                        NotATryContract.DuskLayersEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{id}
                );
                break;

            case ACTIVE_SHIELDS_WITH_ID:
                id = uri.getPathSegments().get(1);
                rowsDeleted = sqLiteDatabase.delete(
                        NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{id}
                );
                break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (0 != rowsDeleted) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
