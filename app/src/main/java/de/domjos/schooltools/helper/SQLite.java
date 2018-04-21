/*
 * Copyright (C) 2017  Dominic Joas
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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.Memory;
import de.domjos.schooltools.core.model.Note;
import de.domjos.schooltools.core.model.TimerEvent;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.core.model.todo.ToDoList;
import de.domjos.schooltools.core.model.mark.SchoolYear;
import de.domjos.schooltools.core.model.mark.Test;
import de.domjos.schooltools.core.model.mark.Year;
import de.domjos.schooltools.core.model.timetable.Day;
import de.domjos.schooltools.core.model.timetable.Hour;
import de.domjos.schooltools.core.model.timetable.SchoolClass;
import de.domjos.schooltools.core.model.Subject;
import de.domjos.schooltools.core.model.timetable.Teacher;
import de.domjos.schooltools.core.model.timetable.TimeTable;
import de.domjos.schooltools.settings.MarkListSettings;

/**
 * Database-Bridge to get Data from database to model
 * @see de.domjos.schooltools.core.model
 * @see android.database.sqlite.SQLiteOpenHelper
 * @author Dominic Joas
 * @version 1.0
 */
public class SQLite extends SQLiteOpenHelper {
    private Context context;

    public SQLite(Context context, String name, int version) {
        super(context, name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String initContent = Helper.readFileFromRaw(this.context, R.raw.init);
            String tables[] = initContent.split(";");
            for(String query : tables) {
                if(!query.trim().equals("")) {
                    db.execSQL(query);
                }
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.setVersion(newVersion);
            String initContent = Helper.readFileFromRaw(this.context, R.raw.update);
            String tables[] = initContent.split(";");
            for(String query : tables) {
                if(!query.trim().equals("")) {
                    db.execSQL(query);
                }
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
    }

    public void deleteEntry(String table, String column, int id, String where) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " AND " + where;
            }

            db.execSQL("DELETE FROM " + table + " WHERE " + column + "=" + id + where + ";");
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
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
            Helper.printException(this.context, ex);
        }
    }

    public List<Integer> getIDsFromTable(String table, String where) {
        List<Integer> ids = new LinkedList<>();
        String unformatted = "SELECT ID FROM %s%s;";
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if(!where.isEmpty()) {
                where = " WHERE " + where;
            }

            Cursor cursor = db.rawQuery(String.format(unformatted, table, where), null);
            while (cursor.moveToNext()) {
                ids.add(cursor.getInt(0));
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }

        return ids;
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
            Helper.printException(this.context, ex);
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
            Helper.printException(this.context, ex);
        }
        return markLists;
    }

    public MarkListSettings getMarkList(String title) {
        MarkListSettings settings = new MarkListSettings(title);
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM markLists WHERE title=?;", new String[]{title});
            while (cursor.moveToNext())  {
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
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return settings;
    }

    public List<MarkListSettings> getMarkListSearch(String where) {
        List<MarkListSettings> settingLists = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM markLists WHERE title like '%" + where + "%';", new String[]{});
            while (cursor.moveToNext())  {
                MarkListSettings settings = new MarkListSettings(cursor.getString(1));
                settings.setId(cursor.getInt(0));
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
                settingLists.add(settings);
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return settingLists;
    }

    public int insertOrUpdateTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(teacher.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE teachers SET lastName=?, firstName=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, teacher.getID());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO teachers(lastName, firstName, description) VALUES(?,?,?);");
            }
            sqLiteStatement.bindString(1, teacher.getLastName());
            sqLiteStatement.bindString(2, teacher.getFirstName());
            sqLiteStatement.bindString(3, teacher.getDescription());

            if(teacher.getID()==0) {
                teacher.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return teacher.getID();
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
                teacher.setID(cursor.getInt(0));
                teacher.setLastName(cursor.getString(1));
                teacher.setFirstName(cursor.getString(2));
                teacher.setDescription(cursor.getString(3));
                teachers.add(teacher);
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }

        return teachers;
    }

    public int insertOrUpdateClass(SchoolClass schoolClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(schoolClass.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE classes SET title=?, numberOfPupils=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, schoolClass.getID());
            } else {
                int id = 0;
                Cursor cursor = db.rawQuery("SELECT ID FROM classes WHERE title=?;", new String[]{schoolClass.getTitle().trim()});
                while (cursor.moveToNext()) {
                    id = cursor.getInt(0);
                }
                cursor.close();
                schoolClass.setID(id);
            }
            if(schoolClass.getID()==0) {
                sqLiteStatement = db.compileStatement("INSERT INTO classes(title, numberOfPupils, description) VALUES(?,?,?);");
            } else {
                sqLiteStatement = db.compileStatement("UPDATE classes SET title=?, numberOfPupils=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, schoolClass.getID());
            }
            sqLiteStatement.bindString(1, schoolClass.getTitle());
            sqLiteStatement.bindLong(2, schoolClass.getNumberOfPupils());
            sqLiteStatement.bindString(3, schoolClass.getDescription());

            if(schoolClass.getID()==0) {
                schoolClass.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return schoolClass.getID();
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
                schoolClass.setID(cursor.getInt(0));
                schoolClass.setTitle(cursor.getString(1));
                schoolClass.setNumberOfPupils(cursor.getInt(2));
                schoolClass.setDescription(cursor.getString(3));
                schoolClasses.add(schoolClass);
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }

        return schoolClasses;
    }

    public int insertOrUpdateSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(subject.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE subjects SET  title=?, alias=?, description=?, hoursInWeek=?, isMainSubject=?, backgroundColor=?, teacher=? WHERE ID=?;");
                sqLiteStatement.bindLong(8, subject.getID());
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

            if(subject.getID()==0) {
                subject.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return subject.getID();
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
                subject.setID(cursor.getInt(0));
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
            Helper.printException(this.context, ex);
        }

        return subjects;
    }

    public int insertOrUpdateHour(Hour hour) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(hour.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE hours SET start_time=?, end_time=?, isBreak=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, hour.getID());
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

            if(hour.getID()==0) {
                hour.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return hour.getID();
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
                hour.setID(cursor.getInt(0));
                hour.setStart(cursor.getString(1));
                hour.setEnd(cursor.getString(2));
                hour.setBreak(cursor.getInt(3)==1);
                hours.add(hour);
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }

        return hours;
    }

    public int insertOrUpdateTimeTable(TimeTable timeTable) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(timeTable.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE plans SET title=?, class=?, plan_year=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(5, timeTable.getID());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO plans(title, class, plan_year, description) VALUES(?,?,?,?);");
            }
            sqLiteStatement.bindString(1, timeTable.getTitle());
            if(timeTable.getSchoolClass()!=null) {
                sqLiteStatement.bindLong(2, this.insertOrUpdateClass(timeTable.getSchoolClass()));
            }
            if(timeTable.getYear()!=null) {
                sqLiteStatement.bindLong(3, timeTable.getYear().getID());
            }
            sqLiteStatement.bindString(4, timeTable.getDescription());

            if(timeTable.getID()==0) {
                timeTable.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.execSQL("DELETE FROM timeTable WHERE plan=" + timeTable.getID() + ";");
            for(Day day : timeTable.getDays()) {
                if(day!=null) {
                    for(Map.Entry<Hour, Map.Entry<Subject, Teacher>> entry : day.getPupilHour().entrySet()) {
                        if(entry.getValue()!=null) {
                            if(entry.getValue().getKey()!=null) {
                                sqLiteStatement = db.compileStatement("INSERT INTO timeTable(plan, day, hour, subject, teacher) VALUES(?, ?, ?, ?, ?);");
                                sqLiteStatement.bindLong(1, timeTable.getID());
                                sqLiteStatement.bindLong(2, day.getPositionInWeek());
                                sqLiteStatement.bindLong(3, entry.getKey().getID());
                                sqLiteStatement.bindLong(4, entry.getValue().getKey().getID());
                                if(entry.getValue().getValue()!=null) {
                                    sqLiteStatement.bindLong(5, entry.getValue().getValue().getID());
                                }
                                sqLiteStatement.execute();
                                sqLiteStatement.close();
                            }
                        }
                    }

                    for(Map.Entry<Hour, Map.Entry<Subject, SchoolClass>> entry : day.getTeacherHour().entrySet()) {
                        if(entry.getValue()!=null) {
                            if(entry.getValue().getKey()!=null) {
                                sqLiteStatement = db.compileStatement("INSERT INTO timeTable(plan, day, hour, subject, class) VALUES(?, ?, ?, ?, ?);");
                                sqLiteStatement.bindLong(1, timeTable.getID());
                                sqLiteStatement.bindLong(2, day.getPositionInWeek());
                                sqLiteStatement.bindLong(3, entry.getKey().getID());
                                sqLiteStatement.bindLong(4, entry.getValue().getKey().getID());
                                if(entry.getValue().getValue()!=null) {
                                    sqLiteStatement.bindLong(5, entry.getValue().getValue().getID());
                                }
                                sqLiteStatement.execute();
                                sqLiteStatement.close();
                            }
                        }
                    }
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return timeTable.getID();
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
                timeTable.setID(cursor.getInt(0));
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
                int id = timeTables.get(i).getID();

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

                    if(teacher!=null) {
                        day.addPupilHour(hours.get(0), subjects.get(0), teacher);
                    } else if(schoolClass!=null) {
                        day.addTeacherHour(hours.get(0), subjects.get(0), schoolClass);
                    } else {
                        day.addPupilHour(hours.get(0), subjects.get(0), null);
                        day.addTeacherHour(hours.get(0), subjects.get(0), null);
                    }
                }
                timeTables.get(i).addDay(day);
                cursor.close();
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }

        return timeTables;
    }

    public int insertOrUpdateTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(test.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE tests SET title=?, description=?, themes=?, weight=?, mark=?, average=?, testDate=? WHERE ID=?;");
                sqLiteStatement.bindLong(8, test.getID());
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
                sqLiteStatement.bindString(7, Converter.convertDateToString(test.getTestDate()));
            } else {
                sqLiteStatement.bindNull(7);
            }

            if(test.getID()==0) {
                test.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            if(test.getMemoryDate()!=null) {
                this.addMemory("tests", test.getID(), test.getMemoryDate(), db);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return test.getID();
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
                test.setID(cursor.getInt(0));
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
                        test.setTestDate(Converter.convertStringToDate(dt));
                    }
                } else {
                    test.setTestDate(null);
                }
                tests.add(test);
            }
            cursor.close();

            for(int i = 0; i<=tests.size()-1; i++) {
                tests.get(i).setMemoryDate(this.getMemoryDate("tests", tests.get(i).getID(), db));
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return tests;
    }

    public int insertOrUpdateYear(Year year) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(year.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE years SET title=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(3, year.getID());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO years(title,description) VALUES(?,?);");
            }
            sqLiteStatement.bindString(1, year.getTitle());
            sqLiteStatement.bindString(2, year.getDescription());

            if(year.getID()==0) {
                year.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return year.getID();
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
                year.setID(cursor.getInt(0));
                year.setTitle(cursor.getString(1));
                year.setDescription(cursor.getString(2));
                years.add(year);
            }
            cursor.close();

        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return years;
    }

    public int insertOrUpdateSchoolYear(String subject, String year, Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = 0;
        try {
            db.beginTransaction();
            int testID = this.insertOrUpdateTest(test);
            db.execSQL("DELETE FROM schoolYears WHERE test=" + testID + ";");

            int subjectID = 0, yearID = 0;
            List<Year> years = this.getYears("title='" + year + "'");
            if(year!=null) {
                if(!years.isEmpty()) {
                    yearID = years.get(0).getID();
                }
            }
            List<Subject> subjects = this.getSubjects("title='" + subject + "'");
            if(subjects!=null) {
                if(!subjects.isEmpty()) {
                    subjectID = subjects.get(0).getID();
                }
            }

            if(subjectID==0 || yearID == 0) {
                return 0;
            }

            SQLiteStatement sqLiteStatement = db.compileStatement("INSERT INTO schoolYears(year, subject, test) VALUES(?, ?, ?);");
            sqLiteStatement.bindLong(1, yearID);
            sqLiteStatement.bindLong(2, subjectID);
            sqLiteStatement.bindLong(3, testID);
            id = (int)sqLiteStatement.executeInsert();
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return id;
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

            for(Subject tmpSubject : subjects) {
                for(Year tmpYear : years) {
                    SchoolYear schoolYear = new SchoolYear();
                    schoolYear.setSubject(tmpSubject);
                    schoolYear.setYear(tmpYear);
                    Cursor cursor = db.rawQuery("SELECT test FROM schoolYears WHERE subject=? and [year]=?;", new String[]{String.valueOf(tmpSubject.getID()), String.valueOf(tmpYear.getID())});
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
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return schoolYears;
    }

    public List<SchoolYear> getSchoolYears(String where) {
        List<SchoolYear> schoolYears = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            List<Subject> subjects = this.getSubjects(where);
            List<Year> years = this.getYears("");

            for(Subject tmpSubject : subjects) {
                for(Year tmpYear : years) {
                    SchoolYear schoolYear = new SchoolYear();
                    schoolYear.setSubject(tmpSubject);
                    schoolYear.setYear(tmpYear);
                    Cursor cursor = db.rawQuery("SELECT test FROM schoolYears WHERE subject=? and [year]=?;", new String[]{String.valueOf(tmpSubject.getID()), String.valueOf(tmpYear.getID())});
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
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return schoolYears;
    }

    public int insertOrUpdateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(note.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE notes SET title=?, description=? WHERE ID=?;");
                sqLiteStatement.bindLong(3, note.getID());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO notes(title,description) VALUES(?,?);");
            }
            sqLiteStatement.bindString(1, note.getTitle());
            sqLiteStatement.bindString(2, note.getDescription());

            if(note.getID()==0) {
                note.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            if(note.getMemoryDate()!=null) {
                this.addMemory("notes", note.getID(), note.getMemoryDate(), db);
            } else {
                this.deleteEntry("memories", "itemID", note.getID(), "[table]='notes'");
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return note.getID();
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
                note.setID(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setDescription(cursor.getString(2));
                notes.add(note);
            }
            cursor.close();

            for(int i = 0; i<=notes.size()-1; i++) {
                notes.get(i).setMemoryDate(this.getMemoryDate("notes", notes.get(i).getID(), db));
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return notes;
    }

    public int insertOrUpdateToDoList(ToDoList toDoList) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(toDoList.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE toDoLists SET title=?, description=?, listDate=? WHERE ID=?;");
                sqLiteStatement.bindLong(4, toDoList.getID());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO toDoLists(title,description,listDate) VALUES(?,?,?);");
            }
            sqLiteStatement.bindString(1, toDoList.getTitle());
            sqLiteStatement.bindString(2, toDoList.getDescription());
            if(toDoList.getListDate()!=null) {
                sqLiteStatement.bindString(3, Converter.convertDateToString(toDoList.getListDate()));
            } else {
                sqLiteStatement.bindNull(3);
            }

            if(toDoList.getID()==0) {
                toDoList.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return toDoList.getID();
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
                toDoList.setID(cursor.getInt(0));
                toDoList.setTitle(cursor.getString(1));
                toDoList.setDescription(cursor.getString(2));
                String item = cursor.getString(3);
                if(item!=null) {
                    if(!item.equals("")) {
                        toDoList.setListDate(Converter.convertStringToDate(cursor.getString(3)));
                    }
                }
                toDoList.setToDos(this.getToDos("toDoList=" + toDoList.getID()));
                toDoLists.add(toDoList);
            }
            cursor.close();

        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return toDoLists;
    }

    public int insertOrUpdateToDo(ToDo toDo, String title) {
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
                if(toDo.getID()!=0) {
                    sqLiteStatement = db.compileStatement("UPDATE toDos SET title=?, description=?, importance=?, solved=?, category=?, toDoList=? WHERE ID=?;");
                    sqLiteStatement.bindLong(7, toDo.getID());
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
                sqLiteStatement.bindLong(6, toDoList.getID());

                if(toDo.getID()==0) {
                    toDo.setID((int) sqLiteStatement.executeInsert());
                } else {
                    sqLiteStatement.execute();
                }
                sqLiteStatement.close();

                if(toDo.getMemoryDate()!=null) {
                    this.addMemory("toDos", toDo.getID(), toDo.getMemoryDate(), db);
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception ex) {
                Helper.printException(this.context, ex);
            }
        }
        return toDo.getID();
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
                toDo.setID(cursor.getInt(0));
                toDo.setTitle(cursor.getString(1));
                toDo.setDescription(cursor.getString(2));
                toDo.setImportance(cursor.getInt(3));
                toDo.setSolved(cursor.getInt(4)==1);
                toDo.setCategory(cursor.getString(5));
                toDos.add(toDo);
            }
            cursor.close();

            for(int i = 0; i<=toDos.size()-1; i++) {
                toDos.get(i).setMemoryDate(this.getMemoryDate("toDos", toDos.get(i).getID(), db));
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return toDos;
    }

    public int insertOrUpdateTimerEvent(TimerEvent timerEvent) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            SQLiteStatement sqLiteStatement;
            if(timerEvent.getID()!=0) {
                sqLiteStatement = db.compileStatement("UPDATE timerEvents SET title=?, description=?, category=?, subject=?, teacher=?, schoolClass=?, eventDate=? WHERE ID=?;");
                sqLiteStatement.bindLong(8, timerEvent.getID());
            } else {
                sqLiteStatement = db.compileStatement("INSERT INTO timerEvents(title,description,category,subject,teacher,schoolClass,eventDate) VALUES(?,?,?,?,?,?,?);");
            }
            sqLiteStatement.bindString(1, timerEvent.getTitle());
            sqLiteStatement.bindString(2, timerEvent.getDescription());
            sqLiteStatement.bindString(3, timerEvent.getCategory());
            if(timerEvent.getSubject()!=null) {
                sqLiteStatement.bindLong(4, timerEvent.getSubject().getID());
            } else {
                sqLiteStatement.bindNull(4);
            }
            if(timerEvent.getTeacher()!=null) {
                sqLiteStatement.bindLong(5, timerEvent.getTeacher().getID());
            } else {
                sqLiteStatement.bindNull(5);
            }
            if(timerEvent.getSchoolClass()!=null) {
                sqLiteStatement.bindLong(6, timerEvent.getSchoolClass().getID());
            } else {
                sqLiteStatement.bindNull(6);
            }
            sqLiteStatement.bindString(7, Converter.convertDateToString(timerEvent.getEventDate()));

            if(timerEvent.getID()==0) {
                timerEvent.setID((int) sqLiteStatement.executeInsert());
            } else {
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();

            if(timerEvent.getMemoryDate()!=null) {
                this.addMemory("timerEvents", timerEvent.getID(), timerEvent.getMemoryDate(), db);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return timerEvent.getID();
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
                timerEvent.setID(cursor.getInt(0));
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

                timerEvent.setEventDate(Converter.convertStringToDate(cursor.getString(7)));
                timerEvents.add(timerEvent);
            }
            cursor.close();

            for(int i = 0; i<=timerEvents.size()-1; i++) {
                timerEvents.get(i).setMemoryDate(this.getMemoryDate("timerEvents", timerEvents.get(i).getID(), db));
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return timerEvents;
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
                        }
                        memories.add(memory);
                    }
                    itemCursor.close();
                }
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return memories;
    }

    public boolean itemExists(String table, int id) {
        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE id=?", new String[]{String.valueOf(id)});
            while (cursor.moveToNext()) {
                exists = true;
            }
            cursor.close();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        return exists;
    }

    private boolean showNotification(String date) throws Exception {
        Calendar calendar =  Converter.convertStringDateToCalendar(date);
        if(calendar!=null) {
            Calendar calendarCur = Converter.convertStringDateToCalendar(Converter.convertDateToString(new Date()));
            if(calendarCur!=null) {
                if(calendar.get(Calendar.YEAR)==calendarCur.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && calendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                    return true;
                } else {
                    calendar.add(Calendar.DATE, MainActivity.settings.getTimerNotificationDistance());
                    return calendarCur.after(calendar);
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void addMemory(String table, int id, Date date, SQLiteDatabase db) {
        db.execSQL("DELETE FROM memories WHERE itemID=" + id + " AND [table]='" + table + "';");
        db.execSQL(String.format("INSERT INTO memories(memoryDate, itemID, [table]) VALUES('%s', %s, '%s');", Converter.convertDateToString(date), id, table));
    }

    private Date getMemoryDate(String table, int id, SQLiteDatabase db) throws Exception {
        Cursor cursor = db.rawQuery("SELECT memoryDate FROM memories WHERE itemID=" + id + " AND [table]='" + table + "';", null);
        while (cursor.moveToNext()) {
            String item = cursor.getString(0);
            if(item!=null) {
                if(!item.equals("")) {
                    return Converter.convertStringToDate(item);
                }
            }
        }
        cursor.close();
        return null;
    }
}
