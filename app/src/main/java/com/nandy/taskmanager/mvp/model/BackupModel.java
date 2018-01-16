package com.nandy.taskmanager.mvp.model;

import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Created by yana on 16.01.18.
 */

abstract class BackupModel {

    protected final  static String BACKUP_FILE_NAME = "tasks.db";
    protected final static String BACKUP_MIME_TYPE = "application/x-sqlite3";

    protected Query buildBackupFilesQuery() {
        return new Query.Builder()
                .addFilter(
                        Filters.and(
                                Filters.eq(SearchableField.TITLE, BACKUP_FILE_NAME),
                                Filters.eq(SearchableField.MIME_TYPE, BACKUP_MIME_TYPE)))
                .build();
    }
}
