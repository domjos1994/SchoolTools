package de.domjos.schooltools.dbcontroller.repository.general;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import de.domjos.schooltools.dbcontroller.model.general.Subject;
import de.domjos.schooltools.dbcontroller.model.general.TeacherSubjects;

@Dao
public interface SubjectDao {

    @Query("SELECT * FROM subjects")
    List<Subject> getSubjects();

    @Query("SELECT * FROM subjects where id=:id")
    Subject getSubject(int id);

    @Query("SELECT * FROM subjects where title=:title")
    Subject getSubjectByTitle(String title);

    @Transaction
    @Query("SELECT * FROM subjects where teacherID=:id")
    List<TeacherSubjects> getSubjectsByTeacherID(int id);

    @Query("SELECT count(id) FROM subjects")
    long countSubjects();

    @Insert
    void insertAll(Subject... subjects);

    @Update
    void updateAll(Subject... subjects);

    @Delete
    void deleteAll(Subject... subjects);
}
