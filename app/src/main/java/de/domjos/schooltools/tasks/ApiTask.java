package de.domjos.schooltools.tasks;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.widget.ProgressBar;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.tasks.ProgressBarTask;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.helper.ApiHelper;
import de.domjos.schooltools.helper.EventHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;
import de.domjos.schooltools.settings.MarkListSettings;
import de.domjos.schooltoolslib.model.Memory;
import de.domjos.schooltoolslib.model.Note;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.schooltoolslib.model.learningCard.LearningCardGroup;
import de.domjos.schooltoolslib.model.mark.SchoolYear;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.schooltoolslib.utils.fileUtils.PDFBuilder;

public class ApiTask extends ProgressBarTask<Void, Boolean> {
    private Type type;
    private Format format;
    private String path;
    private ApiHelper apiHelper;
    private SQLite sqLite;
    private String strType;
    private String where;
    private String entryType;
    private int id;

    public ApiTask(Activity activity, ProgressBar progressBar, Type type, Format format, String path, String strType, String where, String entryType, int id) {
        super(activity, R.string.api_action, R.string.api_action, MainActivity.globals.getUserSettings().isNotificationsShown(), R.drawable.ic_import_export_black_24dp, progressBar);

        this.type = type;
        this.format = format;
        this.path = path;
        this.apiHelper = new ApiHelper(super.getContext());
        this.sqLite = MainActivity.globals.getSqLite();
        this.strType = strType;
        this.where = where;
        this.entryType = entryType;
        this.id = 0;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        switch (this.type) {
            case Export:
                switch (this.format) {
                    case Pdf:
                        return this.exportPDF(this.path, this.strType, this.where);
                    case CSV:
                        return this.exportTextOrCsv(this.path, this.strType, "csv", this.where);
                    case TXT:
                        return this.exportTextOrCsv(this.path, this.strType, "txt", this.where);
                    case XML:
                        return this.exportXml(this.path, this.strType, this.where);
                    case Calendar:
                        return this.exportToCalendar(this.entryType, this.id);
                }
                break;
            case Import:
                switch (this.format) {
                    case CSV:
                    case TXT:
                        return this.importTextOrCsv(this.path, this.strType);
                    case XML:
                        return this.importXml(this.path, this.strType);
                }
                break;
        }
        return null;
    }

    private boolean importTextOrCsv(String path, String type) {
        String content = Helper.getStringFromFile(path, super.getContext());
        try {
            if(type.equals(super.getContext().getString(R.string.main_nav_mark_list))) return this.apiHelper.importMarkListFromTEXT(content);
            if(type.equals(super.getContext().getString(R.string.main_nav_calculateMark))) return this.apiHelper.importMarkFromTEXT(content);
            if(type.equals(super.getContext().getString(R.string.main_nav_timetable))) return this.apiHelper.importTimeTableFromTEXT(content);
            if(type.equals(super.getContext().getString(R.string.main_nav_notes))) return this.apiHelper.importNoteFromTEXT(content);
            if(type.equals(super.getContext().getString(R.string.main_nav_todo))) return this.apiHelper.importToDoListFromText(content);
            if(type.equals(super.getContext().getString(R.string.main_nav_learningCards))) return this.apiHelper.importLearningCardGroupFromText(content);
            if(type.equals(super.getContext().getString(R.string.main_nav_timer))) return this.apiHelper.importTimerEventFromTEXT(content);
        } catch (Exception ex) {
            super.printException(ex);
        }

        return false;
    }

    private boolean importXml(String path, String type) {
        try {
            if(type.equals(super.getContext().getString(R.string.main_nav_mark_list))) return this.apiHelper.importMarkListFromXML(path);
            if(type.equals(super.getContext().getString(R.string.main_nav_calculateMark))) return this.apiHelper.importMarkFromXML(path);
            if(type.equals(super.getContext().getString(R.string.main_nav_timetable))) return this.apiHelper.importTimeTableFromXML(path);
            if(type.equals(super.getContext().getString(R.string.main_nav_notes))) return this.apiHelper.importNoteFromXML(path);
            if(type.equals(super.getContext().getString(R.string.main_nav_todo))) return this.apiHelper.importToDoListFromXML(path);
            if(type.equals(super.getContext().getString(R.string.main_nav_learningCards))) return this.apiHelper.importLearningCardGroupFromXML(path);
            if(type.equals(super.getContext().getString(R.string.main_nav_timer))) return this.apiHelper.importTimerEventFromXML(path);
        } catch (Exception ex) {
            super.printException(ex);
        }
        return false;
    }

    private boolean exportTextOrCsv(String path, String type, String format, String where) {
        String extension = "";
        if(format.equals(super.getContext().getString(R.string.api_format_csv))) {
            extension = "csv";
        }
        String exportPath = String.format("%s/export_%s.%s", path, type, extension);

        StringBuilder content = new StringBuilder();
        if(type.equals(super.getContext().getString(R.string.main_nav_mark_list))) {
            List<MarkListSettings> settingsList = new LinkedList<>();
            for(String name : this.sqLite.listMarkLists(where)) {
                settingsList.add(this.sqLite.getMarkList(name));
            }
            content.append(this.apiHelper.exportMarkListToTEXT(settingsList));
        } else if(type.equals(super.getContext().getString(R.string.main_nav_calculateMark))) {
            content.append(this.apiHelper.exportMarkToTEXT(this.sqLite.getSchoolYears(where)));
        } else if(type.equals(super.getContext().getString(R.string.main_nav_timetable))) {
            content.append(this.apiHelper.exportTimeTableToTEXT(this.sqLite.getTimeTables(where)));
        } else if(type.equals(super.getContext().getString(R.string.main_nav_notes))) {
            content.append(this.apiHelper.exportNoteToTEXT(this.sqLite.getNotes(where)));
        } else if(type.equals(super.getContext().getString(R.string.main_nav_todo))) {
            content.append(this.apiHelper.exportToDoListToTEXT(this.sqLite.getToDoLists(where)));
        } else if(type.equals(super.getContext().getString(R.string.main_nav_learningCards))) {
            content.append(this.apiHelper.exportLearningCardGroupToTEXT(this.sqLite.getLearningCardGroups(where, true)));
        } else if(type.equals(super.getContext().getString(R.string.main_nav_timer))) {
            content.append(this.apiHelper.exportTimerEventToTEXT(this.sqLite.getTimerEvents(where)));
        }

        return Helper.writeStringToFile(content.toString(), exportPath, super.getContext());
    }

    private boolean exportXml(String path, String type, String where) {
        try {
            String exportPath = String.format("%s/export_%s.xml", path, type);

            if(type.equals(super.getContext().getString(R.string.main_nav_mark_list))) {
                return this.apiHelper.exportMarkListToXML(where, exportPath);
            } else if(type.equals(super.getContext().getString(R.string.main_nav_calculateMark))) {
                return this.apiHelper.exportMarkToXML(where, exportPath);
            } else if(type.equals(super.getContext().getString(R.string.main_nav_timetable))) {
                return this.apiHelper.exportTimeTableToXML(where, exportPath);
            } else if(type.equals(super.getContext().getString(R.string.main_nav_notes))) {
                return this.apiHelper.exportNoteToXML(where, exportPath);
            } else if(type.equals(super.getContext().getString(R.string.main_nav_todo))) {
                return this.apiHelper.exportToDoListToXMLElement(where, exportPath);
            } else if(type.equals(super.getContext().getString(R.string.main_nav_learningCards))) {
                return this.apiHelper.exportLearningCardGroupToXML(where, exportPath);
            } else if(type.equals(super.getContext().getString(R.string.main_nav_timer))) {
                return this.apiHelper.exportTimerEventToXML(where, exportPath);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, super.getContext());
        }
        return false;
    }

    private boolean exportPDF(String path, String type, String where) {
        try {
            File emptyPDF = new File(path);
            if(!emptyPDF.exists()) {
                if (!emptyPDF.createNewFile()) {
                    return false;
                }
            }

            PDFBuilder pdfBuilder = new PDFBuilder(emptyPDF.getAbsolutePath(), ConvertHelper.convertDrawableToByteArray(super.getContext(), R.drawable.icon), super.getContext());
            pdfBuilder.addFont("header", Font.FontFamily.HELVETICA, 32, true, true, BaseColor.BLACK);
            pdfBuilder.addFont("subHeader", Font.FontFamily.HELVETICA, 28, true, false, BaseColor.BLACK);
            pdfBuilder.addFont("CONTENT_PARAM", Font.FontFamily.HELVETICA, 16, false, false, BaseColor.BLACK);

            if(type.equals(super.getContext().getString(R.string.main_nav_mark_list))) {
                List<String> stringList = sqLite.listMarkLists(where);
                List<MarkListSettings> markListSettings = new LinkedList<>();
                for(String string : stringList) {
                    markListSettings.add(sqLite.getMarkList(string));
                }
                pdfBuilder.addTitle(super.getContext().getString(R.string.main_nav_mark_list), "header", Paragraph.ALIGN_CENTER);
                for(MarkListSettings settings : markListSettings) {
                    pdfBuilder = this.apiHelper.exportMarkListToPDF(pdfBuilder, settings);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(super.getContext().getString(R.string.main_nav_calculateMark))) {
                List<SchoolYear> schoolYears = this.sqLite.getSchoolYears(where);
                for(SchoolYear schoolYear : schoolYears) {
                    pdfBuilder = apiHelper.exportMarkToPDF(pdfBuilder, schoolYear);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(super.getContext().getString(R.string.main_nav_timetable))) {
                List<TimeTable> timeTables = this.sqLite.getTimeTables(where);
                List<String> headers =
                        Arrays.asList(
                                super.getContext().getString(R.string.timetable_times), super.getContext().getString(R.string.timetable_days_mon), super.getContext().getString(R.string.timetable_days_tue), super.getContext().getString(R.string.timetable_days_wed),
                                super.getContext().getString(R.string.timetable_days_thu), super.getContext().getString(R.string.timetable_days_fri), super.getContext().getString(R.string.timetable_days_sat), super.getContext().getString(R.string.timetable_days_sun)
                        );

                for(TimeTable timeTable : timeTables) {
                    pdfBuilder = this.apiHelper.exportTimeTableToPDF(pdfBuilder, timeTable, headers, this.sqLite);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(super.getContext().getString(R.string.main_nav_notes))) {
                for(Note note : this.sqLite.getNotes(where)) {
                    pdfBuilder = this.apiHelper.exportNoteToPDF(pdfBuilder, note);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(super.getContext().getString(R.string.main_nav_todo))) {
                for(ToDoList toDoList : this.sqLite.getToDoLists(where)) {
                    pdfBuilder = this.apiHelper.exportToDoListToPDF(pdfBuilder, toDoList);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(super.getContext().getString(R.string.main_nav_learningCards))) {
                for(LearningCardGroup learningCardGroup : this.sqLite.getLearningCardGroups(where, true)) {
                    pdfBuilder = this.apiHelper.exportLearningCardGroupToPDF(pdfBuilder, learningCardGroup);
                }
                pdfBuilder.close();
                return true;
            } else if(type.equals(super.getContext().getString(R.string.main_nav_timer))) {
                for(TimerEvent timerEvent : this.sqLite.getTimerEvents(where)) {
                    pdfBuilder = this.apiHelper.exportTimerEventToPDF(pdfBuilder, timerEvent);
                }
                pdfBuilder.close();
                return true;
            }
        } catch (Exception ex) {
            super.printException(ex);
        }
        return false;
    }

    private boolean exportToCalendar(String entryType, int id) {
        try {
            if(entryType.equals(super.getContext().getString(R.string.api_entry_all))) {
                if(Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_CALENDAR, (Activity) super.getContext(), Manifest.permission.WRITE_CALENDAR)) {
                    try {
                        EventHelper helper = new EventHelper(super.getContext());
                        helper.saveMemoriesToCalendar((Activity) super.getContext());
                    } catch (Exception ex) {
                        super.printException(ex);
                    }
                }
                return true;
            } else {
                for(Memory memory : MainActivity.globals.getSqLite().getCurrentMemories()) {
                    if(memory.getId()==id) {
                        EventHelper helper = new EventHelper(memory, super.getContext());
                        Intent intent = helper.openCalendar();
                        if(intent!=null) {
                            super.getContext().startActivity(intent);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            super.printException(ex);
        }
        return false;
    }

    public enum Type {
        Import,
        Export
    }

    public enum Format {
        CSV,
        TXT,
        XML,
        Pdf,
        Calendar
    }
}
