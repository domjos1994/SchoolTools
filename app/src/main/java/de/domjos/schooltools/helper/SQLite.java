/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.*;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.ConvertHelper;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltoolslib.model.Bookmark;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.schooltoolslib.model.Note;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.schooltoolslib.model.learningCard.*;
import de.domjos.schooltoolslib.model.timetable.PupilHour;
import de.domjos.schooltoolslib.model.timetable.TeacherHour;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.schooltoolslib.model.mark.SchoolYear;
import de.domjos.schooltoolslib.model.mark.Test;
import de.domjos.schooltoolslib.model.mark.Year;
import de.domjos.schooltoolslib.model.timetable.Day;
import de.domjos.schooltoolslib.model.timetable.Hour;
import de.domjos.schooltoolslib.model.timetable.SchoolClass;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltools.settings.GeneralSettings;
import de.domjos.schooltools.settings.MarkListSettings;

/**
 * Database-Bridge to get Data from database to model
 * @see de.domjos.schooltoolslib.model
 * @see android.database.sqlite.SQLiteOpenHelper
 * @author Dominic Joas
 * @version 1.0
 */
public class SQLite extends SQLiteOpenHelper {
    private Context context;

    /**
     * Constructor with the name and version
     * @param context current context of app
     * @param name name of database
     * @param version version of database
     */
    public SQLite(Context context, String name, int version) {
        super(context, name, null, version);
        new GeneralSettings(context).setDatabaseVersion(version);
        this.context = context;
    }

    /**
     * Constructor only with context
     * @see #SQLite(Context, String, int)
     * @param context current context of app
     */
    public SQLite(Context context) {
        super(context, "schoolTools.db", null, new GeneralSettings(context).getDatabaseVersion());
    }

    /**
     * Functions to initialize database at first start
     *  - create database tables
     *  - insert default subjects
     * @see #createDatabase(int, SQLiteDatabase)
     * @see #insertSubject(String, int, int, boolean, SQLiteDatabase)
     * @param db the SQLite-Database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            this.createDatabase(R.raw.init, db);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.insertSubject("D", R.string.timetable_subject_d_name, this.context.getColor(R.color.Red), true, db);
                this.insertSubject("M", R.string.timetable_subject_m_name, this.context.getColor(R.color.Blue), true, db);
                this.insertSubject("Eng", R.string.timetable_subject_eng_name, this.context.getColor(R.color.Yellow), true, db);
                this.insertSubject("Bio", R.string.timetable_subject_bio_name, this.context.getColor(R.color.Green), false, db);
                this.insertSubject("Ph", R.string.timetable_subject_ph_name, this.context.getColor(R.color.Gray), false, db);
                this.insertSubject("Ch", R.string.timetable_subject_ch_name, this.context.getColor(R.color.Orange), false, db);
                this.insertSubject("Sp", R.string.timetable_subject_sp_name, this.context.getColor(R.color.Black), false, db);
                this.insertSubject("Rel", R.string.timetable_subject_rel_name, this.context.getColor(R.color.White), false, db);
            } else {
                this.insertSubject("D", R.string.timetable_subject_d_name, this.context.getResources().getColor(R.color.Red), true, db);
                this.insertSubject("M", R.string.timetable_subject_m_name, this.context.getResources().getColor(R.color.Blue), true, db);
                this.insertSubject("Eng", R.string.timetable_subject_eng_name, this.context.getResources().getColor(R.color.Yellow), true, db);
                this.insertSubject("Bio", R.string.timetable_subject_bio_name, this.context.getResources().getColor(R.color.Green), false, db);
                this.insertSubject("Ph", R.string.timetable_subject_ph_name, this.context.getResources().getColor(R.color.Gray), false, db);
                this.insertSubject("Ch", R.string.timetable_subject_ch_name, this.context.getResources().getColor(R.color.Orange), false, db);
                this.insertSubject("Sp", R.string.timetable_subject_sp_name, this.context.getResources().getColor(R.color.Black), false, db);
                this.insertSubject("Rel", R.string.timetable_subject_rel_name, this.context.getResources().getColor(R.color.White), false, db);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    /**
     * Function called on update of database
     *  - create missing database tables
     *  - execute table updates
     * @see #onCreate(SQLiteDatabase)
     * @param db the SQLite-Database
     * @param oldVersion old version of database
     * @param newVersion new version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            this.onCreate(db);
            db.setVersion(newVersion);

            // version 0.2
            this.addColumnIfNotExists(db, "timeTable", "roomNumber", Types.VARCHAR, 255, "''");

            // version 0.4
            this.addColumnIfNotExists(db, "learningCardQueries", "randomVocab", Types.INTEGER, 1, "0");
            this.addColumnIfNotExists(db, "learningCardQueries", "randomVocabNumber", Types.INTEGER, 255, "0");

            this.createDatabase(R.raw.update, db);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    /**
     * Creates the database by executing queries from Raw-Resource
     * @see de.domjos.schooltools.helper.Helper#readFileFromRaw(Context, int)
     * @param resourceID the Resource-ID
     * @param db the SQLite-Database
     */
    private void createDatabase(int resourceID, SQLiteDatabase db) {
        String initContent = Helper.readFileFromRaw(this.context, resourceID);
        String[] tables = initContent.split(";");
        for(String query : tables) {
            if(!query.trim().isEmpty()) {
                db.execSQL(query);
            }
        }
    }

    /**
     * Creates the default subject
     * @param alias alias of subject
     * @param title title-string-resource of subject
     * @param color color of subject
     * @param mainSubject is main-subject
     * @param db the SQLite-Database
     */
    private void insertSubject(String alias, int title, int color, boolean mainSubject, SQLiteDatabase db) {
        Subject subject = new Subject();
        subject.setAlias(alias);
        subject.setTitle(this.context.getString(title));
        if(mainSubject) {
            subject.setHoursInWeek(4);
        } else {
            subject.setHoursInWeek(2);
        }
        subject.setMainSubject(mainSubject);
        subject.setBackgroundColor(String.valueOf(color));

        if(this.getColumns("subjects", "title", " WHERE title='" + this.context.getString(title) + "' and alias='" + alias + "'", db).isEmpty()) {
            this.insertOrUpdateSubject(subject, db);
        }
    }

    public void deleteEntry(String table, String column, long id, String where) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " AND " + where;
            }

            db.execSQL("DELETE FROM " + table + " WHERE " + column + "=" + id + where + ";");
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public void deleteEntry(String table, String where) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            db.execSQL("DELETE FROM " + table + where + ";");
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public void deleteEntry(String table, Object object) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if(object instanceof BaseDescriptionObject) {
                db.execSQL("DELETE FROM " + table + " WHERE ID=" + ((BaseDescriptionObject)object).getId() + ";");
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public boolean entryExists(@NonNull String table, String where) {
        SQLiteDatabase db = this.getReadableDatabase();

        if(!where.isEmpty()) {
            where = " WHERE " + where;
        }
        String query = String.format("SELECT ID FROM %s%s;", table, where);

        try {
            Cursor cursor = db.rawQuery(query, null);
            boolean state = cursor.moveToNext();
            cursor.close();
            return state;
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return false;
    }

    boolean entryExists(@NonNull String table, long id) {
        return this.entryExists(table, "ID=" + id);
    }

    public List<String> getColumns(String table, String column, String where) {
        return this.getColumns(table, column, where, null);
    }

    private List<String> getColumns(String table, String column, String where, SQLiteDatabase db) {
        List<String> results = new LinkedList<>();
        if(db==null) {
            db = this.getReadableDatabase();
        }
        try {
            Cursor cursor = db.rawQuery("SELECT " + column + " FROM " + table + " " + where, null);
            while (cursor.moveToNext()) {
                results.add(cursor.getString(0));
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return results;
    }

    List<LearningCard> getLearningCards(LearningCardQuery query) {
        List<LearningCard> learningCards = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(query.getWrongCardsOfQuery()==null)  {
                List<String> ls = new LinkedList<>();
                if (query.getLearningCardGroup() != null) {
                    ls.add("cardGroup=" + query.getLearningCardGroup().getId());
                }
                if (!query.getCategory().isEmpty()) {
                    ls.add("category='" + query.getCategory() + "'");
                }
                ls.add("priority>=" + query.getPriority());

                StringBuilder where = new StringBuilder(" WHERE ");
                for (String item : ls) {
                    if(!item.trim().isEmpty()) {
                        if(!where.toString().equals(" WHERE ")) {
                            where.append(" AND ");
                        }
                        where.append(item);
                    }
                }
                learningCards = this.getLearningCards(where.toString().replace(" WHERE ", ""), db);

                if(query.isRandomVocab()) {
                    List<LearningCard> randomVocab = new LinkedList<>();
                    for(int i = 0; i<=query.getRandomVocabNumber()-1; i++) {
                        Random generator = new Random();
                        int position = generator.nextInt(learningCards.size());
                        randomVocab.add(learningCards.get(position));
                    }
                    return randomVocab;
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return learningCards;
    }

    public void insertOrUpdateMarkList(String title, MarkListSettings settings) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            int id = 0;
            Cursor cursor = db.rawQuery("SELECT ID FROM markLists WHERE title=?;", new String[]{title});
            while (cursor.moveToNext()) {
                id = cursor.getInt(0);
            }
            cursor.close();

            SQLiteStatement sqLiteStatement;
            if(id==0) {
                sqLiteStatement = db.compileStatement(
                    "INSERT INTO markLists(title, type, maxPoints, tenthMarks, halfPoints," +
                    " dictatMode, viewMode, markMode, customMark, customPoints, bestMarkAt, worstMarkTo)" +
                    " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                );
            } else {
                sqLiteStatement = db.compileStatement(
                    "UPDATE markLists SET title=?, type=?, maxPoints=?, tenthMarks=?, halfPoints=?," +
                    " dictatMode=?, viewMode=?, markMode=?, customMark=?, customPoints=?, bestMarkAt=?, worstMarkTo=?" +
                    " WHERE ID=?;"
                );
                sqLiteStatement.bindLong(13, id);
            }
            sqLiteStatement.bindString(1, title);
            sqLiteStatement.bindLong(2, settings.getType());
            sqLiteStatement.bindLong(3, settings.getMaxPoints());
            sqLiteStatement.bindLong(4, settings.getTenthMarks());
            sqLiteStatement.bindLong(5, settings.getHalfPoints());
            sqLiteStatement.bindLong(6, settings.getDictatMode());
            sqLiteStatement.bindLong(7, settings.getViewMode());
            sqLiteStatement.bindLong(8, settings.getMarMode());
            sqLiteStatement.bindDouble(9, settings.getCustomMark());
            sqLiteStatement.bindDouble(10, settings.getCustomPoints());
            sqLiteStatement.bindDouble(11, settings.getBestMarkAt());
            sqLiteStatement.bindDouble(12, settings.getWorstMarkTo());
            sqLiteStatement.execute();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<String> listMarkLists() {
        return listMarkLists("");
    }

    public List<String> listMarkLists(String where) {
        List<String> markLists = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT title FROM markLists" + where + ";", null);
            while (cursor.moveToNext()) {
                String item = cursor.getString(0);
                if(!item.equals("")) {
                    markLists.add(item);
                }
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return markLists;
    }

    public MarkListSettings getMarkList(String title) {
        MarkListSettings settings = new MarkListSettings("");
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM markLists WHERE title=?;", new String[]{title});
            while (cursor.moveToNext())  {
                settings = this.getSettingsByCursor(cursor);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return settings;
    }

    public List<MarkListSettings> getMarkListSearch(String where) {
        List<MarkListSettings> settingLists = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM markLists WHERE title like '%" + where + "%';", new String[]{});
            while (cursor.moveToNext())  {
                settingLists.add(this.getSettingsByCursor(cursor));
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return settingLists;
    }

    private MarkListSettings getSettingsByCursor(Cursor cursor) {
        MarkListSettings settings = new MarkListSettings(cursor.getString(1));
        settings.setId(cursor.getInt(0));
        settings.setTitle(cursor.getString(1));
        settings.setType(cursor.getInt(2));
        settings.setMaxPoints(cursor.getInt(3));
        settings.setTenthMarks(cursor.getInt(4));
        settings.setHalfPoints(cursor.getInt(5));
        settings.setDictatMode(cursor.getInt(6));
        settings.setViewMode(cursor.getInt(7));
        settings.setMarMode(cursor.getInt(8));
        settings.setCustomMark(cursor.getDouble(9));
        settings.setCustomPoints(cursor.getDouble(10));
        settings.setBestMarkAt(cursor.getDouble(11));
        settings.setWorstMarkTo(cursor.getDouble(12));
        return settings;
    }

    public long insertOrUpdateTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(teacher.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE teachers SET lastName=?, firstName=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, teacher.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO teachers(lastName, firstName, description) VALUES(?,?,?);");
            }
            sqLiteStatement.bindString(1, teacher.getLastName());
            sqLiteStatement.bindString(2, teacher.getFirstName());

            if(teacher.getDescription()!=null) {
                sqLiteStatement.bindString(3, teacher.getDescription());
            } else {
                sqLiteStatement.bindNull(3);
            }

            teacher.setId(this.saveAndClose(teacher.getId(), db, sqLiteStatement));
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return teacher.getId();
    }

    public List<Teacher> getTeachers(String where) {
        List<Teacher> teachers = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM teachers" + where + ";", null);
            while (cursor.moveToNext()) {
                Teacher teacher = new Teacher();
                teacher.setId(cursor.getInt(0));
                teacher.setLastName(cursor.getString(1));
                teacher.setFirstName(cursor.getString(2));
                teacher.setDescription(cursor.getString(3));
                teachers.add(teacher);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }

        return teachers;
    }

    public long insertOrUpdateClass(SchoolClass schoolClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(schoolClass.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE classes SET title=?, numberOfPupils=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, schoolClass.getId());
            } else {
                int id = 0;
                Cursor cursor = db.rawQuery("SELECT ID FROM classes WHERE title=?;", new String[]{schoolClass.getTitle().trim()});
                while (cursor.moveToNext()) {
                    id = cursor.getInt(0);
                }
                cursor.close();
                schoolClass.setId(id);
            }
            if(schoolClass.getId()==0) {
                sqLiteStatement = db.compileStatement("INSERT INTO classes(title, numberOfPupils, description) VALUES(?,?,?);");
            } else {
                sqLiteStatement = db.compileStatement("UPDATE classes SET title=?, numberOfPupils=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, schoolClass.getId());
            }
            sqLiteStatement.bindString(1, schoolClass.getTitle());
            sqLiteStatement.bindLong(2, schoolClass.getNumberOfPupils());
            sqLiteStatement.bindString(3, schoolClass.getDescription());

            schoolClass.setId(this.saveAndClose(schoolClass.getId(), db, sqLiteStatement));
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return schoolClass.getId();
    }

    public List<SchoolClass> getClasses(String where) {
        List<SchoolClass> schoolClasses = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM classes" + where + ";", null);
            while (cursor.moveToNext()) {
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId(cursor.getInt(0));
                schoolClass.setTitle(cursor.getString(1));
                schoolClass.setNumberOfPupils(cursor.getInt(2));
                schoolClass.setDescription(cursor.getString(3));
                schoolClasses.add(schoolClass);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }

        return schoolClasses;
    }

    public long insertOrUpdateSubject(Subject subject) {
        return this.insertOrUpdateSubject(subject, null);
    }

    private long insertOrUpdateSubject(Subject subject, SQLiteDatabase db) {
        if(db==null) {
            db = this.getWritableDatabase();
        }
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(subject.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE subjects SET  title=?, alias=?, description=?, hoursInWeek=?, isMainSubject=?, backgroundColor=?, teacher=? WHERE ID=?;");
                sqLiteStatement.bindLong(8, subject.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO subjects(title, alias, description, hoursInWeek, isMainSubject, backgroundColor, teacher) VALUES(?,?,?,?,?,?,?);");
            }
            sqLiteStatement.bindString(1, subject.getTitle());
            sqLiteStatement.bindString(2, subject.getAlias());
            sqLiteStatement.bindString(3, subject.getDescription());
            sqLiteStatement.bindLong(4, subject.getHoursInWeek());
            if(subject.isMainSubject()) {
                sqLiteStatement.bindLong(5, 1);
            } else {
                sqLiteStatement.bindLong(5, 0);
            }
            sqLiteStatement.bindString(6, subject.getBackgroundColor());
            if(subject.getTeacher()!=null) {
                sqLiteStatement.bindLong(7, this.insertOrUpdateTeacher(subject.getTeacher()));
            } else {
                sqLiteStatement.bindLong(7, 0);
            }

            subject.setId(this.saveAndClose(subject.getId(), db, sqLiteStatement));
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return subject.getId();
    }

    public List<Subject> getSubjects(String where) {
        List<Subject> subjects = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM subjects" + where + ";", null);
            while (cursor.moveToNext()) {
                Subject subject = new Subject();
                subject.setId(cursor.getInt(0));
                subject.setTitle(cursor.getString(1));
                subject.setAlias(cursor.getString(2));
                subject.setDescription(cursor.getString(3));
                subject.setHoursInWeek(cursor.getInt(4));
                subject.setMainSubject(cursor.getInt(5)==1);
                subject.setBackgroundColor(cursor.getString(6));

                if(cursor.getInt(7)!=0) {
                    List<Teacher> teachers = this.getTeachers("ID=" + cursor.getInt(7));
                    if(teachers!=null) {
                        if(!teachers.isEmpty()) {
                            subject.setTeacher(teachers.get(0));
                        }
                    }
                }
                subjects.add(subject);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }

        return subjects;
    }

    public long insertOrUpdateHour(Hour hour) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(hour.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE hours SET start_time=?, end_time=?, isBreak=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, hour.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO hours(start_time, end_time, isBreak) VALUES(?,?,?);");
            }
            sqLiteStatement.bindString(1, hour.getStart());
            sqLiteStatement.bindString(2, hour.getEnd());
            if(hour.isBreak()) {
                sqLiteStatement.bindLong(3, 1);
            } else {
                sqLiteStatement.bindLong(3, 0);
            }

            hour.setId(this.saveAndClose(hour.getId(), db, sqLiteStatement));
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return hour.getId();
    }

    public List<Hour> getHours(String where) {
        List<Hour> hours = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM hours" + where + ";", null);
            while (cursor.moveToNext()) {
                Hour hour = new Hour();
                hour.setId(cursor.getInt(0));
                hour.setStart(cursor.getString(1));
                hour.setEnd(cursor.getString(2));
                hour.setBreak(cursor.getInt(3)==1);
                hours.add(hour);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }

        return hours;
    }

    public void insertOrUpdateTimeTable(TimeTable timeTable) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(timeTable.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE plans SET title=?, class=?, plan_year=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(5, timeTable.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO plans(title, class, plan_year, description) VALUES(?,?,?,?);");
            }
            sqLiteStatement.bindString(1, timeTable.getTitle());
            if(timeTable.getSchoolClass()!=null) {
                sqLiteStatement.bindLong(2, this.insertOrUpdateClass(timeTable.getSchoolClass()));
            }
            if(timeTable.getYear()!=null) {
                sqLiteStatement.bindLong(3, timeTable.getYear().getId());
            }
            sqLiteStatement.bindString(4, timeTable.getDescription());

            if(timeTable.getId()==0) {
                timeTable.setId((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.execSQL("DELETE FROM timeTable WHERE plan=" + timeTable.getId() + ";");
            for(Day day : timeTable.getDays()) {
                if(day!=null) {
                    for(Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                        if(entry.getValue()!=null) {
                            if(entry.getValue().getSubject()!=null) {
                                sqLiteStatement = db.compileStatement("INSERT INTO timeTable('plan', day, hour, subject, teacher, roomNumber, current_timetable) VALUES(?, ?, ?, ?, ?, ?, ?);");
                                if(entry.getValue().getTeacher()!=null) {
                                    sqLiteStatement.bindLong(5, entry.getValue().getTeacher().getId());
                                }
                                this.saveTimeTable(sqLiteStatement, timeTable, day, entry.getKey().getId(), entry.getValue().getSubject().getId(), entry.getValue().getRoomNumber());
                            }
                        }
                    }

                    for(Map.Entry<Hour, TeacherHour> entry : day.getTeacherHour().entrySet()) {
                        if(entry.getValue()!=null) {
                            if(entry.getValue().getSubject()!=null) {
                                sqLiteStatement = db.compileStatement("INSERT INTO timeTable('plan', day, hour, subject, class, roomNumber, current_timetable) VALUES(?, ?, ?, ?, ?, ?, ?);");
                                if(entry.getValue().getSchoolClass()!=null) {
                                    sqLiteStatement.bindLong(5, entry.getValue().getSchoolClass().getId());
                                }
                                this.saveTimeTable(sqLiteStatement, timeTable, day, entry.getKey().getId(), entry.getValue().getSubject().getId(), entry.getValue().getRoomNumber());
                            }
                        }
                    }
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    private void saveTimeTable(SQLiteStatement sqLiteStatement, TimeTable timeTable, Day day, long hour, long subject, String roomNumber) {
        sqLiteStatement.bindLong(1, timeTable.getId());
        sqLiteStatement.bindLong(2, day.getPositionInWeek());
        sqLiteStatement.bindLong(3, hour);
        sqLiteStatement.bindLong(4, subject);
        if(roomNumber!=null) {
            sqLiteStatement.bindString(6, roomNumber);
        }
        if(timeTable.isCurrentTimeTable()) {
            sqLiteStatement.bindLong(7, 1);
        } else {
            sqLiteStatement.bindLong(7, 0);
        }
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }

    public List<TimeTable> getTimeTables(String where) {
        List<TimeTable> timeTables = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM plans" + where + ";", null);
            while (cursor.moveToNext()) {
                TimeTable timeTable = new TimeTable();
                timeTable.setId(cursor.getInt(0));
                timeTable.setTitle(cursor.getString(1));
                List<SchoolClass> classes = this.getClasses("ID=" + cursor.getInt(2));
                if(classes!=null) {
                    if(!classes.isEmpty()) {
                        timeTable.setSchoolClass(classes.get(0));
                    }
                }
                List<Year> years = this.getYears("ID=" + cursor.getInt(3));
                if(years!=null) {
                    if(!years.isEmpty()) {
                        timeTable.setYear(years.get(0));
                    }
                }
                timeTable.setDescription(cursor.getString(4));
                timeTables.add(timeTable);
            }
            cursor.close();

            for(int i = 0; i<=timeTables.size()-1; i++) {
                long id = timeTables.get(i).getId();

                int currentDay = Integer.MAX_VALUE;
                Day day = new Day();
                day.setPositionInWeek(-1);

                cursor = db.rawQuery("SELECT * FROM timeTable WHERE plan=" + id + " ORDER BY day;", null);
                while (cursor.moveToNext()) {
                    if(currentDay!=cursor.getInt(2)) {
                        if(day.getPositionInWeek()!=-1) {
                            timeTables.get(i).addDay(day);
                        }
                        day = new Day();
                        currentDay = cursor.getInt(2);
                    }
                    day.setPositionInWeek(cursor.getInt(2));
                    if(cursor.getInt(8)==0) {
                        timeTables.get(i).setCurrentTimeTable(false);
                    } else {
                        timeTables.get(i).setCurrentTimeTable(true);
                    }
                    List<Hour> hours = this.getHours("ID=" + cursor.getInt(3));
                    List<Subject> subjects = this.getSubjects("ID=" + cursor.getInt(4));

                    Teacher teacher = null;
                    if(cursor.getInt(5)!=0) {
                        List<Teacher> teachers = this.getTeachers("ID=" + cursor.getInt(5));
                        if(teachers!=null) {
                            if(!teachers.isEmpty()) {
                                teacher = teachers.get(0);
                            }
                        }
                    }

                    SchoolClass schoolClass = null;
                    if(cursor.getInt(6)!=0) {
                        List<SchoolClass> schoolClasses = this.getClasses("ID=" + cursor.getInt(6));
                        if(schoolClasses!=null) {
                            if(!schoolClasses.isEmpty()) {
                                schoolClass = schoolClasses.get(0);
                            }
                        }
                    }

                    String roomNumber = cursor.getString(7);

                    if(teacher!=null) {
                        if(!hours.isEmpty()) {
                            if(!subjects.isEmpty()) {
                                day.addPupilHour(hours.get(0), subjects.get(0), teacher, roomNumber);
                            }
                        }
                    } else if(schoolClass!=null) {
                        if(!hours.isEmpty()) {
                            if(!subjects.isEmpty()) {
                                day.addTeacherHour(hours.get(0), subjects.get(0), schoolClass, roomNumber);
                            }
                        }
                    } else {
                        if(!hours.isEmpty()) {
                            if (!subjects.isEmpty()) {
                                day.addPupilHour(hours.get(0), subjects.get(0), null, roomNumber);
                                day.addTeacherHour(hours.get(0), subjects.get(0), null, roomNumber);
                            }
                        }
                    }
                }
                timeTables.get(i).addDay(day);
                cursor.close();
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }

        return timeTables;
    }

    long insertOrUpdateTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(test.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE tests SET title=?, description=?, themes=?, weight=?, mark=?, average=?, testDate=? WHERE ID=?;");
                sqLiteStatement.bindLong(8, test.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO tests(title,description,themes,weight,mark,average,testDate) VALUES(?,?,?,?,?,?,?);");
            }
            sqLiteStatement.bindString(1, test.getTitle());
            sqLiteStatement.bindString(2, test.getDescription());
            sqLiteStatement.bindString(3, test.getThemes());
            sqLiteStatement.bindDouble(4, test.getWeight());
            sqLiteStatement.bindDouble(5, test.getMark());
            sqLiteStatement.bindDouble(6, test.getAverage());
            if(test.getTestDate()!=null) {
                sqLiteStatement.bindString(7, ConvertHelper.convertDateToString(test.getTestDate(), this.context));
            } else {
                sqLiteStatement.bindNull(7);
            }

            if(test.getId()==0) {
                test.setId((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            if(test.getMemoryDate()!=null) {
                this.addMemory("tests", test.getId(), test.getMemoryDate(), db);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return test.getId();
    }

    public List<Test> getTests(String where) {
        List<Test> tests = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM tests" + where + ";", null);
            while (cursor.moveToNext()) {
                Test test = new Test();
                test.setId(cursor.getInt(0));
                test.setTitle(cursor.getString(1));
                test.setDescription(cursor.getString(2));
                test.setThemes(cursor.getString(3));
                test.setWeight(cursor.getDouble(4));
                test.setMark(cursor.getDouble(5));
                test.setAverage(cursor.getDouble(6));
                String dt = cursor.getString(7);
                if(dt!=null) {
                    if(dt.equals("")) {
                        test.setTestDate(null);
                    } else {
                        test.setTestDate(ConvertHelper.convertStringToDate(dt, this.context));
                    }
                } else {
                    test.setTestDate(null);
                }
                tests.add(test);
            }
            cursor.close();

            for(int i = 0; i<=tests.size()-1; i++) {
                tests.get(i).setMemoryDate(this.getMemoryDate("tests", tests.get(i).getId(), db));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return tests;
    }

    public long insertOrUpdateYear(Year year) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(year.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE years SET title=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(3, year.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO years(title,description) VALUES(?,?);");
            }
            sqLiteStatement.bindString(1, year.getTitle());
            sqLiteStatement.bindString(2, year.getDescription());

            year.setId(this.saveAndClose(year.getId(), db, sqLiteStatement));
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return year.getId();
    }

    public List<Year> getYears(String where) {
        List<Year> years = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM years" + where + ";", null);
            while (cursor.moveToNext()) {
                Year year = new Year();
                year.setId(cursor.getInt(0));
                year.setTitle(cursor.getString(1));
                year.setDescription(cursor.getString(2));
                years.add(year);
            }
            cursor.close();

        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return years;
    }

    public void insertOrUpdateSchoolYear(String subject, String year, Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            long testID = this.insertOrUpdateTest(test);
            db.execSQL("DELETE FROM schoolYears WHERE test=" + testID + ";");

            long subjectID = 0, yearID = 0;
            List<Year> years = this.getYears("title='" + year + "'");
            if(year!=null) {
                if(!years.isEmpty()) {
                    yearID = years.get(0).getId();
                }
            }
            List<Subject> subjects = this.getSubjects("title='" + subject + "'");
            if(subjects!=null) {
                if(!subjects.isEmpty()) {
                    subjectID = subjects.get(0).getId();
                }
            }

            if(subjectID==0 || yearID == 0) {
                return;
            }

            SQLiteStatement sqLiteStatement = db.compileStatement("INSERT INTO schoolYears(year, subject, test) VALUES(?, ?, ?);");
            sqLiteStatement.bindLong(1, yearID);
            sqLiteStatement.bindLong(2, subjectID);
            sqLiteStatement.bindLong(3, testID);
            sqLiteStatement.executeInsert();
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<SchoolYear> getSchoolYears(String subject, String year) {
        List<SchoolYear> schoolYears = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            List<Subject> subjects;
            if(subject.isEmpty()) {
                subjects = this.getSubjects("");
            } else {
                subjects = this.getSubjects("title='" + subject + "'");
            }

            List<Year> years = this.getYears("title='" + year + "'");

            schoolYears = this.getSchoolYear(subjects, years, db);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return schoolYears;
    }

    public List<SchoolYear> getSchoolYears(String where) {
        List<SchoolYear> schoolYears = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            List<Subject> subjects = this.getSubjects(where);
            List<Year> years = this.getYears("");

            schoolYears = this.getSchoolYear(subjects, years, db);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return schoolYears;
    }

    private List<SchoolYear> getSchoolYear(List<Subject> subjects, List<Year> years, SQLiteDatabase db) {
        List<SchoolYear> schoolYears = new LinkedList<>();
        for(Subject tmpSubject : subjects) {
            for(Year tmpYear : years) {
                SchoolYear schoolYear = new SchoolYear();
                schoolYear.setSubject(tmpSubject);
                schoolYear.setYear(tmpYear);
                Cursor cursor = db.rawQuery("SELECT test FROM schoolYears WHERE subject=? and [year]=?;", new String[]{String.valueOf(tmpSubject.getId()), String.valueOf(tmpYear.getId())});
                while (cursor.moveToNext()) {
                    List<Test> tests = this.getTests("ID=" + cursor.getInt(0));
                    if(tests!=null) {
                        if(!tests.isEmpty()) {
                            schoolYear.addTest(tests.get(0));
                        }
                    }
                }
                cursor.close();
                schoolYears.add(schoolYear);
            }
        }

        return schoolYears;
    }

    public void insertOrUpdateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(note.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE notes SET title=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(3, note.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO notes(title,description) VALUES(?,?);");
            }
            sqLiteStatement.bindString(1, note.getTitle());
            sqLiteStatement.bindString(2, note.getDescription());

            if(note.getId()==0) {
                note.setId((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            if(note.getMemoryDate()!=null) {
                this.addMemory("notes", note.getId(), note.getMemoryDate(), db);
            } else {
                this.deleteEntry("memories", "itemID", note.getId(), "[table]='notes'");
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<Note> getNotes(String where) {
        List<Note> notes = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM notes" + where + ";", null);
            while (cursor.moveToNext()) {
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setDescription(cursor.getString(2));
                note.setTitle(cursor.getString(1));
                notes.add(note);
            }
            cursor.close();

            for(int i = 0; i<=notes.size()-1; i++) {
                notes.get(i).setMemoryDate(this.getMemoryDate("notes", notes.get(i).getId(), db));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return notes;
    }

    public void insertOrUpdateToDoList(ToDoList toDoList) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(toDoList.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE toDoLists SET title=?, description=?, listDate=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, toDoList.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO toDoLists(title,description,listDate) VALUES(?,?,?);");
            }
            sqLiteStatement.bindString(1, toDoList.getTitle());
            sqLiteStatement.bindString(2, toDoList.getDescription());
            if(toDoList.getListDate()!=null) {
                sqLiteStatement.bindString(3, ConvertHelper.convertDateToString(toDoList.getListDate(), this.context));
            } else {
                sqLiteStatement.bindNull(3);
            }

            if(toDoList.getId()==0) {
                toDoList.setId((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<ToDoList> getToDoLists(String where) {
        List<ToDoList> toDoLists = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM toDoLists" + where + ";", null);
            while (cursor.moveToNext()) {
                ToDoList toDoList = new ToDoList();
                toDoList.setId(cursor.getInt(0));
                toDoList.setTitle(cursor.getString(1));
                toDoList.setDescription(cursor.getString(2));
                String item = cursor.getString(3);
                if(item!=null) {
                    if(!item.equals("")) {
                        toDoList.setListDate(ConvertHelper.convertStringToDate(cursor.getString(3), this.context));
                    }
                }
                toDoList.setToDos(this.getToDos("toDoList=" + toDoList.getId()));
                toDoLists.add(toDoList);
            }
            cursor.close();

        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return toDoLists;
    }

    public long insertOrUpdateToDo(ToDo toDo, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<ToDoList> toDoLists = this.getToDoLists("title='" + title + "'");
        ToDoList toDoList = null;
        if(toDoLists!=null) {
            if(!toDoLists.isEmpty()) {
                toDoList = toDoLists.get(0);
            }
        }
        if(toDoList!=null) {
            try {
                db.beginTransaction();
                SQLiteStatement sqLiteStatement;
                if(toDo.getId()!=0) {
                    sqLiteStatement = db.compileStatement("UPDATE toDos SET title=?, description=?, importance=?, solved=?, category=?, toDoList=? WHERE ID=?;");
                    sqLiteStatement.bindLong(7, toDo.getId());
                } else {
                    sqLiteStatement = db.compileStatement("INSERT INTO toDos(title,description,importance,solved,category,toDoList) VALUES(?,?,?,?,?,?);");
                }
                sqLiteStatement.bindString(1, toDo.getTitle());
                sqLiteStatement.bindString(2, toDo.getDescription());
                sqLiteStatement.bindLong(3, toDo.getImportance());
                if(toDo.isSolved()) {
                    sqLiteStatement.bindLong(4, 1);
                } else {
                    sqLiteStatement.bindLong(4, 0);
                }
                sqLiteStatement.bindString(5, toDo.getCategory());
                sqLiteStatement.bindLong(6, toDoList.getId());

                if(toDo.getId()==0) {
                    toDo.setId((int) sqLiteStatement.executeInsert());
                } else {
                    sqLiteStatement.execute();
                }
                sqLiteStatement.close();

                if(toDo.getMemoryDate()!=null) {
                    this.addMemory("toDos", toDo.getId(), toDo.getMemoryDate(), db);
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
            }
        }
        return toDo.getId();
    }

    public List<ToDo> getToDos(String where) {
        List<ToDo> toDos = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM toDos" + where + ";", null);
            while (cursor.moveToNext()) {
                ToDo toDo = new ToDo();
                toDo.setId(cursor.getInt(0));
                toDo.setTitle(cursor.getString(1));
                toDo.setDescription(cursor.getString(2));
                toDo.setImportance(cursor.getInt(3));
                toDo.setSolved(cursor.getInt(4)==1);
                toDo.setCategory(cursor.getString(5));
                toDos.add(toDo);
            }
            cursor.close();

            for(int i = 0; i<=toDos.size()-1; i++) {
                toDos.get(i).setMemoryDate(this.getMemoryDate("toDos", toDos.get(i).getId(), db));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return toDos;
    }

    public void insertOrUpdateTimerEvent(TimerEvent timerEvent) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(timerEvent.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE timerEvents SET title=?, description=?, category=?, subject=?, teacher=?, schoolClass=?, eventDate=? WHERE ID=?;");
                sqLiteStatement.bindLong(8, timerEvent.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO timerEvents(title,description,category,subject,teacher,schoolClass,eventDate) VALUES(?,?,?,?,?,?,?);");
            }
            sqLiteStatement.bindString(1, timerEvent.getTitle());
            sqLiteStatement.bindString(2, timerEvent.getDescription());
            sqLiteStatement.bindString(3, timerEvent.getCategory());
            if(timerEvent.getSubject()!=null) {
                sqLiteStatement.bindLong(4, timerEvent.getSubject().getId());
            } else {
                sqLiteStatement.bindNull(4);
            }
            if(timerEvent.getTeacher()!=null) {
                sqLiteStatement.bindLong(5, timerEvent.getTeacher().getId());
            } else {
                sqLiteStatement.bindNull(5);
            }
            if(timerEvent.getSchoolClass()!=null) {
                sqLiteStatement.bindLong(6, timerEvent.getSchoolClass().getId());
            } else {
                sqLiteStatement.bindNull(6);
            }
            sqLiteStatement.bindString(7, ConvertHelper.convertDateToString(timerEvent.getEventDate(), this.context));

            if(timerEvent.getId()==0) {
                timerEvent.setId((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            if(timerEvent.getMemoryDate()!=null) {
                this.addMemory("timerEvents", timerEvent.getId(), timerEvent.getMemoryDate(), db);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<TimerEvent> getTimerEvents(String where) {
        List<TimerEvent> timerEvents = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM timerEvents" + where + ";", null);
            while (cursor.moveToNext()) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setId(cursor.getInt(0));
                timerEvent.setTitle(cursor.getString(1));
                timerEvent.setDescription(cursor.getString(2));
                timerEvent.setCategory(cursor.getString(3));
                List<Subject> subjects = this.getSubjects("ID=" + cursor.getInt(4));
                if(subjects!=null) {
                    if(!subjects.isEmpty()) {
                        timerEvent.setSubject(subjects.get(0));
                    }
                }

                List<SchoolClass> schoolClasses = this.getClasses("ID=" + cursor.getInt(5));
                if(schoolClasses!=null) {
                    if(!schoolClasses.isEmpty()) {
                        timerEvent.setSchoolClass(schoolClasses.get(0));
                    }
                }

                List<Teacher> teachers = this.getTeachers("ID=" + cursor.getInt(6));
                if(teachers!=null) {
                    if(!teachers.isEmpty()) {
                        timerEvent.setTeacher(teachers.get(0));
                    }
                }

                timerEvent.setEventDate(ConvertHelper.convertStringToDate(cursor.getString(7), this.context));
                timerEvents.add(timerEvent);
            }
            cursor.close();

            for(int i = 0; i<=timerEvents.size()-1; i++) {
                timerEvents.get(i).setMemoryDate(this.getMemoryDate("timerEvents", timerEvents.get(i).getId(), db));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return timerEvents;
    }

    public long insertOrUpdateLearningCardGroup(LearningCardGroup learningCardGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(learningCardGroup.getId()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE learningCardGroups SET title=?, description=?, category=?, deadline=?, subject=?, teacher=? WHERE ID=?;");
                sqLiteStatement.bindLong(7, learningCardGroup.getId());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO learningCardGroups(title,description,category, deadline,subject,teacher) VALUES(?,?,?,?,?,?);");
            }
            sqLiteStatement.bindString(1, learningCardGroup.getTitle());
            sqLiteStatement.bindString(2, learningCardGroup.getDescription());
            sqLiteStatement.bindString(3, learningCardGroup.getCategory());
            if(learningCardGroup.getDeadLine()!=null) {
                sqLiteStatement.bindString(4, ConvertHelper.convertDateToString(learningCardGroup.getDeadLine(), this.context));
            } else {
                sqLiteStatement.bindNull(4);
            }
            if(learningCardGroup.getSubject()!=null) {
                sqLiteStatement.bindLong(5, learningCardGroup.getSubject().getId());
            } else {
                sqLiteStatement.bindNull(5);
            }
            if(learningCardGroup.getTeacher()!=null) {
                sqLiteStatement.bindLong(6, learningCardGroup.getTeacher().getId());
            } else {
                sqLiteStatement.bindNull(6);
            }

            if(learningCardGroup.getId()==0) {
                learningCardGroup.setId((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }

            List<LearningCard> cards = new LinkedList<>();
            for(LearningCard learningCard : learningCardGroup.getLearningCards()) {
                if(learningCard.getId()!=0 || !learningCard.getTitle().trim().equals("") || !learningCard.getQuestion().trim().equals("")) {
                    cards.add(learningCard);
                }
            }
            learningCardGroup.setLearningCards(cards);

            StringBuilder list = new StringBuilder("(");
            for(LearningCard learningCard : learningCardGroup.getLearningCards()) {
                if(learningCard.getId()!=0) {
                    list.append(learningCard.getId());
                    list.append(",");
                }
            }
            list.append(")");

            db.execSQL("DELETE FROM learningCards WHERE cardGroup=" + learningCardGroup.getId() + " AND NOT ID IN " + list.toString().replace(",)", ")"));

            for(LearningCard learningCard : learningCardGroup.getLearningCards()) {
                this.insertOrUpdateLearningCard(learningCardGroup.getId(), learningCard, db);
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        } finally {
            db.endTransaction();
        }
        return learningCardGroup.getId();
    }

    public List<LearningCardGroup> getLearningCardGroups(String where, boolean listLearningCards) {
        List<LearningCardGroup> learningCardGroups = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM learningCardGroups" + where, null);
            while (cursor.moveToNext()) {
                LearningCardGroup learningCardGroup = new LearningCardGroup();
                learningCardGroup.setId(cursor.getInt(0));
                learningCardGroup.setDescription(cursor.getString(2));
                learningCardGroup.setTitle(cursor.getString(1));
                learningCardGroup.setCategory(cursor.getString(3));
                String deadLine = cursor.getString(4);
                if(deadLine!=null) {
                    if(!deadLine.equals("")) {
                        learningCardGroup.setDeadLine(ConvertHelper.convertStringToDate(deadLine, this.context));
                    }
                }
                int subjectID = cursor.getInt(5);
                if(subjectID!=0) {
                    List<Subject> subjects = this.getSubjects("ID=" + subjectID);
                    if(subjects!=null) {
                        if(!subjects.isEmpty()) {
                            learningCardGroup.setSubject(subjects.get(0));
                        }
                    }

                }
                int teacherID = cursor.getInt(6);
                if(teacherID!=0) {
                    List<Teacher> teachers = this.getTeachers("ID=" + subjectID);
                    if(teachers!=null) {
                        if(!teachers.isEmpty()) {
                            learningCardGroup.setTeacher(teachers.get(0));
                        }
                    }
                }
                learningCardGroups.add(learningCardGroup);
            }
            cursor.close();

            if(listLearningCards) {
                for(LearningCardGroup group : learningCardGroups) {
                    group.setLearningCards(this.getLearningCards("cardGroup=" + group.getId(), db));
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return learningCardGroups;
    }

    public void insertOrUpdateLearningCardQuery(LearningCardQuery learningCardQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            SQLiteStatement statement;
            if(learningCardQuery.getId()==0) {
                statement = db.compileStatement(
                    "INSERT INTO learningCardQueries(title,description,cardGroup,category,priority,wrongCardsOfQuery," +
                            "periodic,period,untilDeadLine,answerMustEqual,showNotes,tries,showNotesImmediately, randomVocab, randomVocabNumber) " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                );
            } else {
                statement = db.compileStatement(
                    "UPDATE learningCardQueries SET title=?,description=?,cardGroup=?,category=?,priority=?,wrongCardsOfQuery=?," +
                            "periodic=?,period=?,untilDeadLine=?,answerMustEqual=?,showNotes=?,tries=?,showNotesImmediately=?," +
                            "randomVocab=?,randomVocabNumber=? " +
                            "WHERE ID=?"
                );
                statement.bindLong(16, learningCardQuery.getId());
            }

            statement.bindString(1, learningCardQuery.getTitle());
            statement.bindString(2, learningCardQuery.getDescription());
            if(learningCardQuery.getLearningCardGroup()!=null) {
                statement.bindLong(3, learningCardQuery.getLearningCardGroup().getId());
            } else {
                statement.bindNull(3);
            }
            statement.bindString(4, learningCardQuery.getCategory());
            statement.bindLong(5, learningCardQuery.getPriority());
            if(learningCardQuery.getWrongCardsOfQuery()!=null) {
                statement.bindLong(6, learningCardQuery.getWrongCardsOfQuery().getId());
            } else {
                statement.bindNull(6);
            }
            statement.bindLong(7, learningCardQuery.isPeriodic() ? 1 : 0);
            statement.bindLong(8, learningCardQuery.getPeriod());
            statement.bindLong(9, learningCardQuery.isUntilDeadLine() ? 1 : 0);
            statement.bindLong(10, learningCardQuery.isAnswerMustEqual() ? 1 : 0);
            statement.bindLong(11, learningCardQuery.isShowNotes() ? 1 : 0);
            statement.bindLong(12, learningCardQuery.getTries());
            statement.bindLong(13, learningCardQuery.isShowNotesImmediately() ? 1 : 0);
            statement.bindLong(14, learningCardQuery.isRandomVocab() ? 1 : 0);
            statement.bindLong(15, learningCardQuery.getRandomVocabNumber());

            statement.execute();
            statement.close();
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<LearningCardQuery> getLearningCardQueries(String where) {
        List<LearningCardQuery> learningCardQueries = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM learningCardQueries" + where, null);
            while (cursor.moveToNext()) {
                LearningCardQuery learningCardQuery = new LearningCardQuery();
                learningCardQuery.setId(cursor.getInt(0));
                learningCardQuery.setTitle(cursor.getString(1));
                learningCardQuery.setDescription(cursor.getString(2));
                int cardGroup = cursor.getInt(3);
                if(cardGroup!=0) {
                    if(this.entryExists("learningCardGroups", cardGroup)) {
                        learningCardQuery.setLearningCardGroup(this.getLearningCardGroups("ID=" + cardGroup, false).get(0));
                    }
                }
                learningCardQuery.setCategory(cursor.getString(4));
                learningCardQuery.setPriority(cursor.getInt(5));
                int wrongCard = cursor.getInt(6);
                if(wrongCard!=0) {
                    learningCardQuery.setWrongCardsOfQuery(this.getLearningCardQueries("ID=" + wrongCard).get(0));
                }
                learningCardQuery.setPeriodic(cursor.getInt(7)==1);
                learningCardQuery.setPeriod(cursor.getInt(8));
                learningCardQuery.setUntilDeadLine(cursor.getInt(9)==1);
                learningCardQuery.setAnswerMustEqual(cursor.getInt(10)==1);
                learningCardQuery.setShowNotes(cursor.getInt(11)==1);
                learningCardQuery.setTries(cursor.getInt(12));
                learningCardQuery.setShowNotesImmediately(cursor.getInt(13)==1);
                learningCardQuery.setRandomVocab(cursor.getInt(14)==1);
                learningCardQuery.setRandomVocabNumber(cursor.getInt(15));

                learningCardQueries.add(learningCardQuery);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return learningCardQueries;
    }

    public long insertOrUpdateLearningCardResult(LearningCardQueryResult learningCardQueryResult) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement;
            if(learningCardQueryResult.getId()==0) {
                statement = db.compileStatement("INSERT INTO learningCardQueryResults(learningCardQueryTraining, learningCard, answerTry1, resultTry1, answerTry2, resultTry2, answerTry3, resultTry3, resultWhole) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            } else {
                statement = db.compileStatement("UPDATE learningCardQueryResults SET learningCardQueryTraining=?, learningCard=?, answerTry1=?, resultTry1=?, answerTry2=?, resultTry2=?, answerTry3=?, resultTry3=?, resultWhole=? WHERE ID=?");
                statement.bindLong(10, learningCardQueryResult.getId());
            }
            statement.bindLong(1, learningCardQueryResult.getTraining().getID());
            statement.bindLong(2,learningCardQueryResult.getLearningCard().getId());
            statement.bindString(3,learningCardQueryResult.getTry1());
            statement.bindLong(4, learningCardQueryResult.isResult1() ? 1 : 0);
            statement.bindString(5,learningCardQueryResult.getTry2());
            statement.bindLong(6, learningCardQueryResult.isResult2() ? 1 : 0);
            statement.bindString(7,learningCardQueryResult.getTry3());
            statement.bindLong(8, learningCardQueryResult.isResult3() ? 1 : 0);
            if(learningCardQueryResult.getId()==0) {
                learningCardQueryResult.setId((int) statement.executeInsert());
            } else {
                statement.execute();
            }
            statement.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return learningCardQueryResult.getId();
    }

    private List<LearningCardQueryResult> getLearningCardResults(String where) {
        List<LearningCardQueryResult> learningCardQueryResults = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM learningCardQueryResults" + where, null);
            while (cursor.moveToNext()) {
                LearningCardQueryResult learningCardQueryResult = new LearningCardQueryResult();
                learningCardQueryResult.setId(cursor.getLong(0));
                learningCardQueryResult.setLearningCard(this.getLearningCards("ID=" + cursor.getInt(2), db).get(0));
                learningCardQueryResult.setTry1(cursor.getString(3));
                learningCardQueryResult.setResult1(cursor.getInt(4)==1);
                learningCardQueryResult.setTry2(cursor.getString(5));
                learningCardQueryResult.setResult2(cursor.getInt(6)==1);
                learningCardQueryResult.setTry3(cursor.getString(7));
                learningCardQueryResult.setResult3(cursor.getInt(8)==1);
                learningCardQueryResult.setResultWhole(cursor.getDouble(9));
                learningCardQueryResults.add(learningCardQueryResult);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return learningCardQueryResults;
    }

    public int insertOrUpdateLearningCardQueryTraining(LearningCardQueryTraining learningCardQueryTraining) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement;
            if(learningCardQueryTraining.getID()==0) {
                statement = db.compileStatement("INSERT INTO learningCardQueryTrainings(learningCardQuery) VALUES(?)");
            } else {
                statement = db.compileStatement("UPDATE learningCardQueryTrainings SET learningCardQuery=? WHERE ID=?");
                statement.bindLong(2, learningCardQueryTraining.getID());
            }
            statement.bindLong(1, learningCardQueryTraining.getLearningCardQuery().getId());

            if(learningCardQueryTraining.getID()==0) {
                learningCardQueryTraining.setID((int) statement.executeInsert());
            } else {
                statement.execute();
            }
            statement.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return learningCardQueryTraining.getID();
    }

    public List<LearningCardQueryTraining> getLearningCardQueryTraining(String where) {
        List<LearningCardQueryTraining> learningCardQueryTrainings = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM learningCardQueryTrainings" + where, null);
            while (cursor.moveToNext()) {
                LearningCardQueryTraining learningCardQueryTraining = new LearningCardQueryTraining();
                learningCardQueryTraining.setID(cursor.getInt(0));
                learningCardQueryTraining.setLearningCardQuery(this.getLearningCardQueries("ID=" + cursor.getInt(1)).get(0));
                learningCardQueryTrainings.add(learningCardQueryTraining);
            }
            cursor.close();

            for(int i = 0; i<=learningCardQueryTrainings.size()-1; i++) {
                learningCardQueryTrainings.get(i).setResults(this.getLearningCardResults("learningCardQueryTraining=" + learningCardQueryTrainings.get(i).getID()));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return learningCardQueryTrainings;
    }

    private void insertOrUpdateLearningCard(long learningCardGroupId, LearningCard learningCard, SQLiteDatabase db) {
        SQLiteStatement statement;
        if(learningCard.getId()==0) {
            statement = db.compileStatement("INSERT INTO learningCards(title,category,question,answer,note1,note2,priority,cardGroup) VALUES(?,?,?,?,?,?,?,?);");
        } else {
            statement = db.compileStatement("UPDATE learningCards SET title=?,category=?,question=?,answer=?,note1=?,note2=?,priority=?,cardGroup=? WHERE ID=?");
            statement.bindLong(9, learningCard.getId());
        }
        statement.bindString(1, learningCard.getTitle());
        statement.bindString(2, learningCard.getCategory());
        statement.bindString(3, learningCard.getQuestion());
        statement.bindString(4, learningCard.getAnswer());
        statement.bindString(5, learningCard.getNote1());
        statement.bindString(6, learningCard.getNote2());
        statement.bindLong(7, learningCard.getPriority());
        statement.bindLong(8, learningCardGroupId);
        statement.execute();
        statement.close();
    }

    private List<LearningCard> getLearningCards(String where, SQLiteDatabase db) {
        List<LearningCard> learningCards = new LinkedList<>();

        if (!where.isEmpty()) {
            where = " WHERE " + where;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM learningCards" + where, null);
        while (cursor.moveToNext()) {
            LearningCard learningCard = new LearningCard();
            learningCard.setId(cursor.getInt(0));
            learningCard.setTitle(cursor.getString(1));
            learningCard.setCategory(cursor.getString(2));
            learningCard.setQuestion(cursor.getString(3));
            learningCard.setAnswer(cursor.getString(4));
            learningCard.setNote1(cursor.getString(5));
            learningCard.setNote2(cursor.getString(6));
            learningCard.setPriority(cursor.getInt(7));
            learningCards.add(learningCard);
        }
        cursor.close();

        return learningCards;
    }

    public void insertOrUpdateBookmark(Bookmark bookmark) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement;
            if(bookmark.getId()==0) {
                statement = db.compileStatement("INSERT INTO bookmarks(title,tags,link,themes,subject,description,preview,contentData) VALUES(?,?,?,?,?,?,?,?);");
            } else {
                statement = db.compileStatement("UPDATE bookmarks SET title=?,tags=?,link=?,themes=?,subject=?,description=?,preview=?,contentData=? WHERE ID=?");
                statement.bindLong(9, bookmark.getId());
            }
            statement.bindString(1, bookmark.getTitle());
            statement.bindString(2, bookmark.getTags());
            statement.bindString(3, bookmark.getLink());
            statement.bindString(4, bookmark.getThemes());
            if(bookmark.getSubject()!=null) {
                statement.bindLong(5, bookmark.getSubject().getId());
            } else {
                statement.bindNull(5);
            }
            statement.bindString(6, bookmark.getDescription());
            if(bookmark.getPreview()!=null) {
                statement.bindBlob(7, bookmark.getPreview());
            } else {
                statement.bindNull(7);
            }
            if(bookmark.getData()!=null) {
                statement.bindBlob(8, bookmark.getData());
            } else {
                statement.bindNull(8);
            }
            statement.execute();
            statement.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<Bookmark> getBookmarks(String where) {
        List<Bookmark> bookmarks = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery("SELECT * FROM bookmarks" + where, null);
            while (cursor.moveToNext()) {
                Bookmark bookmark = new Bookmark();
                bookmark.setId(cursor.getInt(0));
                bookmark.setTitle(cursor.getString(1));
                bookmark.setTags(cursor.getString(2));
                bookmark.setLink(cursor.getString(3));
                bookmark.setThemes(cursor.getString(4));
                int subject_id = cursor.getInt(5);
                if(subject_id!=0) {
                    bookmark.setSubject(this.getSubjects("ID=" + subject_id).get(0));
                }
                bookmark.setDescription(cursor.getString(6));
                bookmark.setPreview(cursor.getBlob(7));
                bookmark.setData(cursor.getBlob(8));
                bookmarks.add(bookmark);
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return bookmarks;
    }

    public List<Memory> getCurrentMemories() {
        List<Memory> memories = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM memories", null);
            while (cursor.moveToNext()) {
                String memDate = cursor.getString(1);

                if(showNotification(memDate)) {
                    int id = cursor.getInt(2);
                    String table = cursor.getString(3);
                    Cursor itemCursor = db.rawQuery(String.format("SELECT title, description FROM %s WHERE ID=%s;", table, id), null);
                    while (itemCursor.moveToNext()) {
                        Memory memory = new Memory();
                        memory.setId(id);
                        memory.setTitle(itemCursor.getString(0));
                        memory.setDescription(itemCursor.getString(1));
                        memory.setDate(memDate);
                        switch (table.toLowerCase()) {
                            case "notes":
                                memory.setType(Memory.Type.Note);
                                break;
                            case "tests":
                                memory.setType(Memory.Type.Test);
                                break;
                            case "todos":
                                memory.setType(Memory.Type.toDo);
                                break;
                            case "timerevents":
                                memory.setType(Memory.Type.timerEvent);
                                break;
                            default:
                        }
                        memories.add(memory);
                    }
                    itemCursor.close();
                }
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return memories;
    }

    public void addSetting(String key, String value, byte[] content) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int id = 0;
            Cursor cursor = db.rawQuery("SELECT ID FROM settings WHERE alias=?", new String[]{key});
            while (cursor.moveToNext()) {
                id = cursor.getInt(0);
            }
            cursor.close();

            SQLiteStatement sqLiteStatement;
            if(id==0) {
                sqLiteStatement = db.compileStatement("INSERT INTO settings(alias, byteContent, stringContent) VALUES(?,?,?)");
            } else {
                sqLiteStatement = db.compileStatement("UPDATE settings SET alias=?, byteContent=?, stringContent=? WHERE ID=?");
                sqLiteStatement.bindLong(4, id);
            }
            sqLiteStatement.bindString(1, key);
            if(content!=null) {
                sqLiteStatement.bindBlob(2, content);
            } else {
                sqLiteStatement.bindNull(2);
            }
            sqLiteStatement.bindString(3, value);
            sqLiteStatement.execute();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public Map.Entry<String, byte[]> getSetting(String key) {
        Map.Entry<String, byte[]> entry = new AbstractMap.SimpleEntry<>("", null);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT byteContent, stringContent FROM settings WHERE alias=?", new String[]{key});
            while (cursor.moveToNext()) {
                entry = new AbstractMap.SimpleEntry<>(cursor.getString(1), cursor.getBlob(0));
            }
            cursor.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return entry;
    }

    public void addSync(String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM synchronisation WHERE type=?", new Object[]{type});
            SQLiteStatement sqLiteStatement = db.compileStatement("INSERT INTO synchronisation(type) VALUES(?)");
            sqLiteStatement.bindString(1, type);
            sqLiteStatement.executeInsert();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public Date getLastSyncDate(String type) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT ts FROM synchronisation WHERE type=?", new String[]{type});
            if(cursor!=null) {
                while (cursor.moveToNext()) {
                    date = new Date(cursor.getLong(0));
                }
                cursor.close();
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return date;
    }

    public void insertToDictionary(String lang, String item, String foreignLang, String foreignItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement sqLiteStatement = db.compileStatement("INSERT INTO dict(motherLanguage, motherItem, foreignLanguage, foreignItem) VALUES(?, ?, ?, ?)");
            sqLiteStatement.bindString(1, lang);
            sqLiteStatement.bindString(2, item);
            sqLiteStatement.bindString(3, foreignLang);
            sqLiteStatement.bindString(4, foreignItem);
            sqLiteStatement.executeInsert();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    public List<String> findInDictionary(String item) {
        List<String> items = new LinkedList<>();
        if(MainActivity.globals.getUserSettings().isDictionary()) {
            SQLiteDatabase db = this.getReadableDatabase();
            try {
                Cursor cursor = db.rawQuery("SELECT foreignItem FROM dict WHERE motherItem like '%" + item + "%'", null);
                while (cursor.moveToNext()) {
                    items.add(cursor.getString(0));
                }
                cursor.close();
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
            }
        }
        return items;
    }

    private boolean showNotification(String date) throws Exception {
        Calendar calendar =  ConvertHelper.convertStringToCalendar(date, this.context);
        if(calendar!=null) {
            Calendar calendarCur = ConvertHelper.convertStringToCalendar(ConvertHelper.convertDateToString(new Date(), this.context), this.context);
            if(calendarCur!=null) {
                if(calendar.get(Calendar.YEAR)==calendarCur.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && calendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                    return true;
                } else {
                    calendar.add(Calendar.DATE, MainActivity.globals.getUserSettings().getTimerNotificationDistance());
                    return calendarCur.after(calendar);
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void addMemory(String table, long id, Date date, SQLiteDatabase db) {
        db.execSQL("DELETE FROM memories WHERE itemID=" + id + " AND [table]='" + table + "';");
        db.execSQL(String.format("INSERT INTO memories(memoryDate, itemID, [table]) VALUES('%s', %s, '%s');", ConvertHelper.convertDateToString(date, this.context), id, table));
    }

    private Date getMemoryDate(String table, long id, SQLiteDatabase db) throws Exception {
        Cursor cursor = db.rawQuery("SELECT memoryDate FROM memories WHERE itemID=" + id + " AND [table]='" + table + "';", null);
        while (cursor.moveToNext()) {
            String item = cursor.getString(0);
            if(item!=null) {
                if(!item.equals("")) {
                    return ConvertHelper.convertStringToDate(item, this.context);
                }
            }
        }
        cursor.close();
        return null;
    }

    private void addColumnIfNotExists(SQLiteDatabase db, String table, String column, int type, int length, String defaultValue) {
        try {
            if(this.columnNotExists(db, table, column)) {
                Map<Integer, String> types = this.getAllJdbcTypeNames();
                String typeString = types.get(type);
                if(typeString!=null) {
                    if(typeString.toLowerCase().equals("varchar")) {
                        typeString += "(" + length + ")";
                    }
                } else {
                    return;
                }
                if(!defaultValue.equals("")) {
                    typeString += " DEFAULT " + defaultValue;
                }

                db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s", table, column, typeString));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    private Map<Integer, String> getAllJdbcTypeNames() throws  Exception {

        Map<Integer, String> result = new LinkedHashMap<>();

        for (Field field : Types.class.getFields()) {
            result.put(field.getInt(null), field.getName());
        }

        return result;
    }

    private boolean columnNotExists(SQLiteDatabase db, String table, String column) {
        boolean exists = false;
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        while (cursor.moveToNext()) {
            if(cursor.getString(1).equals(column)) {
                exists = true;
                break;
            }
        }
        cursor.close();
        return !exists;
    }

    private long saveAndClose(long id, SQLiteDatabase db, SQLiteStatement statement) {
        if(id==0) {
            id = (int) statement.executeInsert();
        } else {
            statement.execute();
        }
        statement.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }
}
