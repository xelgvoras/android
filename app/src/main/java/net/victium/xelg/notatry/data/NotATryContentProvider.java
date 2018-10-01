package net.victium.xelg.notatry.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public class NotATryContentProvider extends ContentProvider {

    public static final int CHARACTER_STATUS = 100;
    public static final int CHARACTER_STATUS_WITH_ID = 101;
    public static final int DUSK_LAYERS = 200;
    public static final int DUSK_LAYERS_WITH_ID = 201;
    public static final int ACTIVE_SHIELDS = 300;
    public static final int ACTIVE_SHIELDS_WITH_ID = 301;
    public static final int ACTIVE_SHIELDS_WITH_NAME = 302;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(NotATryContract.CONTENT_AUTHORITY,
                NotATryContract.CHARACTER_STATUS_PATH,
                CHARACTER_STATUS);
        uriMatcher.addURI(NotATryContract.CONTENT_AUTHORITY,
                NotATryContract.CHARACTER_STATUS_PATH + "/#",
                CHARACTER_STATUS_WITH_ID);
        uriMatcher.addURI(NotATryContract.CONTENT_AUTHORITY,
                NotATryContract.DUSK_LAYERS_PATH,
                DUSK_LAYERS);
        uriMatcher.addURI(NotATryContract.CONTENT_AUTHORITY,
                NotATryContract.DUSK_LAYERS_PATH + "/#",
                DUSK_LAYERS_WITH_ID);
        uriMatcher.addURI(NotATryContract.CONTENT_AUTHORITY,
                NotATryContract.ACTIVE_SHIELDS_PATH,
                ACTIVE_SHIELDS);
        uriMatcher.addURI(NotATryContract.CONTENT_AUTHORITY,
                NotATryContract.ACTIVE_SHIELDS_PATH + "/#",
                ACTIVE_SHIELDS_WITH_ID);
        uriMatcher.addURI(NotATryContract.CONTENT_AUTHORITY,
                NotATryContract.ACTIVE_SHIELDS_PATH + "/*",
                ACTIVE_SHIELDS_WITH_NAME);

        return uriMatcher;
    }

    private NotATryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new NotATryDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        String mSelection;
        String[] mSelectionArgs;

        switch (match) {
            case CHARACTER_STATUS:
                retCursor = db.query(NotATryContract.CharacterStatusEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case DUSK_LAYERS:
                retCursor = db.query(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ACTIVE_SHIELDS:
                retCursor = db.query(NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ACTIVE_SHIELDS_WITH_ID:
                String shieldId = uri.getPathSegments().get(1);
                mSelection = NotATryContract.ActiveShieldsEntry._ID + "=?";
                mSelectionArgs = new String[]{shieldId};

                retCursor = db.query(NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        null,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null);
                break;
            case ACTIVE_SHIELDS_WITH_NAME:
                String shieldName = uri.getPathSegments().get(1);
                mSelection = NotATryContract.ActiveShieldsEntry.COLUMN_NAME + "=?";
                mSelectionArgs = new String[]{shieldName};

                retCursor = db.query(NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        null,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null);
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int math = sUriMatcher.match(uri);
        Uri returnUri;
        long id;

        switch (math) {
            case CHARACTER_STATUS:
                id = db.insert(NotATryContract.CharacterStatusEntry.TABLE_NAME,
                        null,
                        values);
                returnUri = NotATryContract.CharacterStatusEntry.CONTENT_URI;
                break;
            case DUSK_LAYERS:
                id = db.insert(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                        null,
                        values);
                returnUri = NotATryContract.DuskLayersSummaryEntry.CONTENT_URI;
                break;
            case ACTIVE_SHIELDS:
                id = db.insert(NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        null,
                        values);
                returnUri = NotATryContract.ActiveShieldsEntry.CONTENT_URI;
                break;
                default:
                    throw new UnsupportedOperationException("Unkonown uri: " + uri);
        }

        if (id > 0) {
            returnUri = ContentUris.withAppendedId(returnUri, id);
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int deletedRows;

        switch (match) {
            case DUSK_LAYERS:
                deletedRows = db.delete(NotATryContract.DuskLayersSummaryEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ACTIVE_SHIELDS:
                deletedRows = db.delete(NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ACTIVE_SHIELDS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                deletedRows = db.delete(NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int updatedRows;

        switch (match) {
            case CHARACTER_STATUS:
                updatedRows = db.update(NotATryContract.CharacterStatusEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ACTIVE_SHIELDS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                updatedRows = db.update(NotATryContract.ActiveShieldsEntry.TABLE_NAME,
                        values,
                        mSelection,
                        mSelectionArgs);
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }
}
