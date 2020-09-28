/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.content.Context;
import android.os.Environment;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Paragraph;

import java.io.File;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltoolslib.marklist.de.GermanLinearList;
import de.domjos.schooltoolslib.marklist.de.GermanListWithCrease;
import de.domjos.schooltoolslib.model.Note;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.schooltoolslib.model.TimerEvent;
import de.domjos.schooltoolslib.model.learningCard.LearningCard;
import de.domjos.schooltoolslib.model.learningCard.LearningCardGroup;
import de.domjos.schooltoolslib.model.mark.SchoolYear;
import de.domjos.schooltoolslib.model.mark.Test;
import de.domjos.schooltoolslib.model.mark.Year;
import de.domjos.schooltoolslib.model.marklist.MarkListInterface;
import de.domjos.schooltoolslib.model.marklist.MarkListWithMarkMode;
import de.domjos.schooltoolslib.model.timetable.Day;
import de.domjos.schooltoolslib.model.timetable.Hour;
import de.domjos.schooltoolslib.model.timetable.PupilHour;
import de.domjos.schooltoolslib.model.timetable.SchoolClass;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.schooltoolslib.model.timetable.TeacherHour;
import de.domjos.schooltoolslib.model.timetable.TimeTable;
import de.domjos.schooltoolslib.model.todo.ToDo;
import de.domjos.schooltoolslib.model.todo.ToDoList;
import de.domjos.schooltoolslib.utils.fileUtils.*;
import de.domjos.schooltoolslib.utils.xFileUtils.ObjectXML;
import de.domjos.schooltools.settings.MarkListSettings;

public class ApiHelper {
    private Context context;
    private SQLite sqLite;
    private final boolean overrideEntry, cancelExport;

    public ApiHelper(Context context) {
        this.context = context;
        this.sqLite = MainActivity.globals.getSqLite();
        this.overrideEntry = MainActivity.globals.getUserSettings().isApiOverrideEntries();
        this.cancelExport = MainActivity.globals.getUserSettings().isApiCancelExport();
    }

    public PDFBuilder exportMarkListToPDF(PDFBuilder pdfBuilder, MarkListSettings settings) throws Exception {
        String pointsOrErrors;
        if(settings.isDictatMode()) {
            pointsOrErrors = this.context.getString(R.string.marklist_failures);
        } else {
            pointsOrErrors = this.context.getString(R.string.marklist_points);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(this.context.getString(R.string.marklist_type)).append(": ").append(this.context.getResources().getStringArray(R.array.marklist_type)[settings.getType()]).append(",\n");
        builder.append(this.context.getString(R.string.marklist_view)).append(": ").append(this.context.getResources().getStringArray(R.array.marklist_view)[settings.getViewMode()]).append(",\n");
        builder.append(this.context.getString(R.string.marklist_markMode)).append(": ").append(this.context.getResources().getStringArray(R.array.marklist_markMode)[settings.getMarMode()]).append(",\n");
        builder.append(this.context.getString(R.string.marklist_maxPoints)).append(": ").append(settings.getMaxPoints()).append(" ").append(pointsOrErrors).append(",\n");
        if(settings.isDictatMode()) {
            builder.append(this.context.getString(R.string.marklist_dictatMode)).append(",\n");
        }
        if(settings.getType()!=0) {
            builder.append(this.context.getString(R.string.marklist_bestMarkAt)).append(": ").append(settings.getBestMarkAt()).append(" ").append(pointsOrErrors).append(", ");
            builder.append(this.context.getString(R.string.marklist_worstMarkTo)).append(": ").append(settings.getWorstMarkTo()).append(" ").append(pointsOrErrors).append(",\n");
            builder.append(pointsOrErrors).append(": ").append(settings.getCustomPoints()).append(" ").append(pointsOrErrors).append(", ");
            builder.append(this.context.getString(R.string.marklist_mark)).append(": ").append(settings.getCustomMark());
        }

        String content = builder.toString();
        if(content.endsWith(",\n")) {
            content = content.substring(0, content.length()-2);
        }

        pdfBuilder.addTitle(settings.getTitle(), "subHeader", Paragraph.ALIGN_CENTER);
        pdfBuilder.addEmptyLine(3);
        pdfBuilder.addParagraph(this.context.getString(R.string.main_menu_settings), content, "subHeader", "CONTENT_PARAM");
        List<List<Map.Entry<String, BaseColor>>> lsBuilders = new LinkedList<>();
        Map<Double, Double> mp = null;
        switch (settings.getType()) {
            case 0:
                GermanLinearList markListLinear = new GermanLinearList(this.context, settings.getMaxPoints());
                markListLinear.setDictatMode(settings.isDictatMode());
                markListLinear.setMarkMode(MarkListWithMarkMode.MarkMode.values()[settings.getMarMode()]);
                markListLinear.setViewMode(MarkListInterface.ViewMode.values()[settings.getViewMode()]);
                if(settings.getTenthMarks()==1) {
                    markListLinear.setMarkMultiplier(0.1);
                } else {
                    markListLinear.setMarkMultiplier(1);
                }
                if(settings.getHalfPoints()==1) {
                    markListLinear.setPointsMultiplier(0.5);
                } else {
                    markListLinear.setPointsMultiplier(1);
                }
                mp = markListLinear.calculate();
                break;
            case 1:
                GermanListWithCrease markListCrease = new GermanListWithCrease(this.context, settings.getMaxPoints());
                markListCrease.setDictatMode(settings.isDictatMode());
                markListCrease.setMarkMode(MarkListWithMarkMode.MarkMode.values()[settings.getMarMode()]);
                markListCrease.setViewMode(MarkListInterface.ViewMode.values()[settings.getViewMode()]);
                if(settings.getTenthMarks()==1) {
                    markListCrease.setMarkMultiplier(0.1);
                } else {
                    markListCrease.setMarkMultiplier(1);
                }
                if(settings.getHalfPoints()==1) {
                    markListCrease.setPointsMultiplier(0.5);
                } else {
                    markListCrease.setPointsMultiplier(1);
                }
                markListCrease.setCustomMark(settings.getCustomMark());
                markListCrease.setCustomPoints(settings.getCustomPoints());
                markListCrease.setBestMarkAt(settings.getBestMarkAt());
                markListCrease.setWorstMarkTo(settings.getWorstMarkTo());
                mp = markListCrease.calculate();
                break;
            default:
        }

        if(mp!=null) {
            for(Map.Entry<Double, Double> entry : mp.entrySet()) {
                List<Map.Entry<String, BaseColor>> ls = new LinkedList<>();
                ls.add(new AbstractMap.SimpleEntry<>(String.valueOf(entry.getKey()), BaseColor.GRAY));
                ls.add(new AbstractMap.SimpleEntry<>(String.valueOf(entry.getValue()), BaseColor.GRAY));
                lsBuilders.add(ls);
            }
            pdfBuilder.addEmptyLine(2);
            pdfBuilder.addTable(Arrays.asList(this.context.getString(R.string.marklist_mark), this.context.getString(R.string.marklist_points)), null, lsBuilders);
        }
        pdfBuilder.newPage();
        return pdfBuilder;
    }

    public String exportMarkListToTEXT(List<MarkListSettings> settingList) {
        CSVBridge writer = new CSVBridge(";", "id;title;max_points;best_mark_at;worst_mark_to;custom_mark;custom_points;half_points;tenth_marks;dictat_mode;type;mark_mode;view_mode", MainActivity.globals.getUserSettings().getDateFormat());
        for(int i = 1; i<= settingList.size(); i++) {
            try {
                MarkListSettings settings = settingList.get(i-1);
                writer.writeValue(i, "id", settings.getId());
                writer.writeValue(i, "title", settings.getTitle());
                writer.writeValue(i, "max_points", settings.getMaxPoints());
                writer.writeValue(i, "best_mark_at", settings.getBestMarkAt());
                writer.writeValue(i, "worst_mark_to", settings.getWorstMarkTo());
                writer.writeValue(i, "custom_mark", settings.getCustomMark());
                writer.writeValue(i, "custom_points", settings.getCustomPoints());
                writer.writeValue(i, "half_points", settings.getHalfPoints());
                writer.writeValue(i, "tenth_marks", settings.getTenthMarks());
                writer.writeValue(i, "dictat_mode", settings.getDictatMode());
                writer.writeValue(i, "type", settings.getType());
                writer.writeValue(i, "mark_mode", settings.getMarMode());
                writer.writeValue(i, "view_mode", settings.getViewMode());
                if(settingList.size()!=i) {
                    writer.addNewLine();
                }
            } catch (Exception ex) {
                if(this.cancelExport) {
                    return ex.toString();
                } else {
                    writer.replaceWithNewLine();
                }
            }
        }
        return writer.toString();
    }

    public boolean exportMarkListToXML(String where, String path) throws Exception {
        List<MarkListSettings> markListSettings = this.sqLite.getMarkListSearch(where);
        if(!markListSettings.isEmpty()) {
            ObjectXML.saveObjectListToXML("MarkListSettings", markListSettings, path, MainActivity.globals.getUserSettings().getDateFormat());
            return true;
        } else {
            return false;
        }
    }

    public boolean importMarkListFromTEXT(String content) {
        if(!content.trim().contains(";")) {
            return false;
        } else {
            CSVBridge reader = new CSVBridge(";", "", content.trim(), true, MainActivity.globals.getUserSettings().getDateFormat());
            for(int i = 1; i<=reader.size(); i++) {
                MarkListSettings settings = new MarkListSettings(reader.readStringValue(i, "title"));
                settings.setId(reader.readIntegerValue(i, "id"));
                settings.setMaxPoints(reader.readIntegerValue(i, "max_points"));
                settings.setBestMarkAt(reader.readDoubleValue(i, "best_mark_at"));
                settings.setWorstMarkTo(reader.readDoubleValue(i, "worst_mark_to"));
                settings.setCustomMark(reader.readDoubleValue(i, "custom_mark"));
                settings.setCustomPoints(reader.readDoubleValue(i, "custom_points"));
                settings.setHalfPoints(reader.readIntegerValue(i, "half_points"));
                settings.setTenthMarks(reader.readIntegerValue(i, "tenth_marks"));
                settings.setDictatMode(reader.readIntegerValue(i, "dictat_mode"));
                settings.setType(reader.readIntegerValue(i, "type"));
                settings.setMarMode(reader.readIntegerValue(i, "mark_mode"));
                settings.setViewMode(reader.readIntegerValue(i, "view_mode"));
                this.saveMarkListSettings(settings);
            }
        }
        return true;
    }

    public boolean importMarkListFromXML(String path) throws Exception {
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            XMLBuilder builder = new XMLBuilder(file);
            List<XMLElement> elements = builder.getElements("MarkListSettings");
            for(XMLElement xmlElement : elements) {
                MarkListSettings markListSettings = new MarkListSettings(this.unescapeText(xmlElement.getAttributes().get("title")));
                markListSettings.setType(this.getIntegerFromMap(xmlElement.getAttributes(), "type"));
                if(xmlElement.getSubElements().size()!=2) {
                    if(MainActivity.globals.getUserSettings().isApiCancelExport()) {
                        return false;
                    } else {
                        continue;
                    }
                } else {
                    Map<String, String> map1 = xmlElement.getSubElements().get(0).getAttributes();
                    Map<String, String> map2 = xmlElement.getSubElements().get(1).getAttributes();
                    markListSettings.setMaxPoints(this.getIntegerFromMap(map1, "maxPoints"));
                    markListSettings.setDictatMode(this.getBooleanFromMap(map1, "dictatMode"));
                    markListSettings.setHalfPoints(this.getBooleanFromMap(map1, "halfPoints"));
                    markListSettings.setTenthMarks(this.getBooleanFromMap(map1, "tenthMarks"));
                    markListSettings.setViewMode(this.getIntegerFromMap(map1, "viewMode"));
                    markListSettings.setMarMode(this.getIntegerFromMap(map1, "markMode"));
                    markListSettings.setBestMarkAt(this.getDoubleFromMap(map2, "bestMarkAt"));
                    markListSettings.setWorstMarkTo(this.getDoubleFromMap(map2, "worstMarkTo"));
                    markListSettings.setCustomPoints(this.getDoubleFromMap(map2, "customPoints"));
                    markListSettings.setCustomMark(this.getDoubleFromMap(map2, "customMark"));
                }

                this.saveMarkListSettings(markListSettings);
            }
            return true;
        }
        return false;
    }

    private void saveMarkListSettings(MarkListSettings markListSettings) {
        if(markListSettings!=null) {
            if(this.overrideEntry) {
                if(!this.sqLite.entryExists("markListSettings", markListSettings.getId())) {
                    markListSettings.setId(0);
                }
            } else {
                markListSettings.setId(0);
            }
            this.sqLite.insertOrUpdateMarkList(markListSettings.getTitle(), markListSettings);
        }
    }

    public PDFBuilder exportMarkToPDF(PDFBuilder pdfBuilder, SchoolYear schoolYear) throws Exception {
        pdfBuilder.addTitle(schoolYear.getSubject().getTitle(), "header", Paragraph.ALIGN_CENTER);
        pdfBuilder.addEmptyLine(2);
        List<Test> tests = schoolYear.getTests();
        for(Test test : tests) {
            pdfBuilder.addTitle(test.getTitle(), "subHeader", Paragraph.ALIGN_CENTER);
            pdfBuilder.addEmptyLine(3);
            List<List<Map.Entry<String, BaseColor>>> lsBuilders = new LinkedList<>();
            List<Map.Entry<String, BaseColor>> ls = new LinkedList<>();
            ls.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.mark_date), BaseColor.GRAY));
            ls.add(new AbstractMap.SimpleEntry<>(ConvertHelper.convertDateToString(test.getTestDate(), this.context), BaseColor.GRAY));
            lsBuilders.add(ls);
            ls = new LinkedList<>();
            ls.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.sys_memory), BaseColor.GRAY));
            ls.add(new AbstractMap.SimpleEntry<>(ConvertHelper.convertDateToString(test.getMemoryDate(), this.context), BaseColor.GRAY));
            lsBuilders.add(ls);
            ls = new LinkedList<>();
            ls.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.mark_weight), BaseColor.GRAY));
            ls.add(new AbstractMap.SimpleEntry<>(String.valueOf(test.getWeight()), BaseColor.GRAY));
            lsBuilders.add(ls);
            ls = new LinkedList<>();
            ls.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.mark_average), BaseColor.GRAY));
            ls.add(new AbstractMap.SimpleEntry<>(String.valueOf(test.getAverage()), BaseColor.GRAY));
            lsBuilders.add(ls);
            ls = new LinkedList<>();
            ls.add(new AbstractMap.SimpleEntry<>(this.context.getString(R.string.mark_mark), BaseColor.GRAY));
            ls.add(new AbstractMap.SimpleEntry<>(String.valueOf(test.getMark()), BaseColor.GRAY));
            lsBuilders.add(ls);

            pdfBuilder.addTable(Arrays.asList(this.context.getString(R.string.sys_description), ""), null, lsBuilders);
            pdfBuilder.addEmptyLine(2);
            pdfBuilder.addParagraph(this.context.getString(R.string.sys_description), test.getDescription(), "subHeader", "CONTENT_PARAM");
            pdfBuilder.addEmptyLine(2);
            pdfBuilder.addParagraph(this.context.getString(R.string.mark_themes), test.getThemes(), "subHeader", "CONTENT_PARAM");
            pdfBuilder.newPage();
        }
        return pdfBuilder;
    }

    public String exportMarkToTEXT(List<SchoolYear> schoolYears) {
        CSVBridge writer = new CSVBridge(";", "id;subject;year;tests", MainActivity.globals.getUserSettings().getDateFormat());
        for(int i = 1; i<=schoolYears.size(); i++) {
            try {
                SchoolYear schoolYear = schoolYears.get(i-1);
                writer.writeValue(i, "id", schoolYear.getID());
                writer.writeValue(i, "subject", this.getCSVObjectFromSubject(schoolYear.getSubject(), "|", "[", "]"), "(", ")");
                writer.writeValue(i, "year", this.getCSVObjectFromYear(schoolYear.getYear()), "(", ")");
                List<CSVObject> testObjects = new LinkedList<>();
                for(Test test : schoolYear.getTests()) {
                    if(test!=null) {
                        CSVObject csvObject = new CSVObject("|", 9);
                        csvObject.writeValue("1", test.getId());
                        csvObject.writeValue("2", test.getTitle());
                        csvObject.writeValue("3", test.getMark());
                        csvObject.writeValue("4", test.getAverage());
                        csvObject.writeValue("5", test.getWeight());
                        csvObject.writeValue("6", test.getThemes());
                        csvObject.writeValue("7", test.getDescription());
                        csvObject.writeValue("8", test.getTestDate(), MainActivity.globals.getUserSettings().getDateFormat());
                        csvObject.writeValue("9", test.getMemoryDate(), MainActivity.globals.getUserSettings().getDateFormat());
                        testObjects.add(csvObject);
                    }
                }
                writer.writeValue(i, "tests", testObjects, "(", ")");
                if(schoolYears.size()!=i) {
                    writer.addNewLine();
                }
            } catch (Exception ex) {
                if(this.cancelExport) {
                    return ex.toString();
                } else {
                    writer.replaceWithNewLine();
                }
            }
        }
        return writer.toString();
    }

    public boolean exportMarkToXML(String where, String path) throws Exception {
        XMLBuilder builder = new XMLBuilder("Marks", new File(path));
        List<SchoolYear> schoolYears = this.sqLite.getSchoolYears(where);
        if(!schoolYears.isEmpty()) {
            for(SchoolYear schoolYear : schoolYears) {
                XMLElement element = new XMLElement("SchoolYear");
                element.addAttribute("id", String.valueOf(schoolYear.getID()));

                XMLElement yearElement = this.getXMLElementFromYear(schoolYear.getYear());
                if(yearElement!=null) {
                    element.addSubElement(yearElement);
                }

                XMLElement xmlElement = this.getXMLElementFromSubject(schoolYear.getSubject());
                if(xmlElement!=null) {
                    element.addSubElement(xmlElement);
                }

                if(schoolYear.getTests()!=null) {
                    XMLElement tests = new XMLElement("Tests");
                    for(Test test : schoolYear.getTests()) {
                        XMLElement testElement = new XMLElement("Test");
                        testElement.addAttribute("id", String.valueOf(test.getId()));
                        testElement.addAttribute("title", this.escapeText(test.getTitle()));
                        testElement.addAttribute("description", this.escapeText(test.getDescription()));
                        testElement.addAttribute("themes", this.escapeText(test.getThemes()));
                        testElement.addAttribute("mark", String.valueOf(test.getMark()));
                        testElement.addAttribute("average", String.valueOf(test.getAverage()));
                        testElement.addAttribute("weight", String.valueOf(test.getWeight()));
                        testElement.addAttribute("memoryDate", ConvertHelper.convertDateToString(test.getMemoryDate(), this.context));
                        testElement.addAttribute("testDate", ConvertHelper.convertDateToString(test.getTestDate(), this.context));
                        tests.addSubElement(testElement);
                    }
                    element.addSubElement(tests);
                }
                builder.addElement(element);
            }
            builder.save();
            return true;
        } else {
            return false;
        }
    }

    public boolean importMarkFromTEXT(String content) {
        if(!content.trim().contains(";")) {
            return false;
        } else {
            CSVBridge reader = new CSVBridge(";", "", content.trim(), true, MainActivity.globals.getUserSettings().getDateFormat());
            for(int i = 1; i<=reader.size(); i++) {
                SchoolYear schoolYear = new SchoolYear();
                schoolYear.setID(reader.readIntegerValue(i, "id"));
                schoolYear.setSubject(this.getSubjectFromBridge(reader, i));
                List<CSVObject> csvObjects = reader.readObjectsValue(i, "year", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        schoolYear.setYear(this.getYearFromCSVObject(csvObjects.get(0)));
                    }
                }
                csvObjects = reader.readObjectsValue(i, "tests", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        for(CSVObject csvObject : csvObjects) {
                            if(csvObject!=null) {
                                Test test = new Test();
                                test.setId(csvObject.readIntegerValue("1"));
                                test.setTitle(csvObject.readStringValue("2"));
                                test.setMark(csvObject.readDoubleValue("3"));
                                test.setAverage(csvObject.readDoubleValue("4"));
                                test.setWeight(csvObject.readDoubleValue("5"));
                                test.setThemes(csvObject.readStringValue("6"));
                                test.setDescription(csvObject.readStringValue("7"));
                                test.setTestDate(csvObject.readDateValue("8", MainActivity.globals.getUserSettings().getDateFormat()));
                                test.setMemoryDate(csvObject.readDateValue("9", MainActivity.globals.getUserSettings().getDateFormat()));
                                schoolYear.addTest(test);
                            }
                        }
                    }
                }

                this.saveSchoolYear(schoolYear);
            }
        }
        return true;
    }

    public boolean importMarkFromXML(String path) throws Exception {
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            XMLBuilder builder = new XMLBuilder(file);
            List<XMLElement> elements = builder.getElements("Marks");
            for(XMLElement xmlElement : elements.get(0).getSubElements()) {
                SchoolYear schoolYear = new SchoolYear();
                schoolYear.setID(this.getIntegerFromMap(xmlElement.getAttributes(), "id"));
                List<XMLElement> subElements = xmlElement.getSubElements();
                if(!subElements.isEmpty()) {
                    if(subElements.size()>=3) {
                        schoolYear.setYear(this.getYearFromXMLElement(subElements.get(0)));
                        schoolYear.setSubject(this.getSubjectFromXMLElement(subElements.get(1)));
                        XMLElement testElement = subElements.get(2);
                        if(testElement.getSubElements()!=null) {
                            if(!testElement.getSubElements().isEmpty()) {
                                for(XMLElement element : testElement.getSubElements()) {
                                    Test test = (Test) this.getBaseDescriptionObject(element);
                                    test.setThemes(this.unescapeText(element.getAttributes().get("themes")));
                                    test.setMark(this.getDoubleFromMap(element.getAttributes(), "mark"));
                                    test.setAverage(this.getDoubleFromMap(element.getAttributes(), "average"));
                                    test.setWeight(this.getDoubleFromMap(element.getAttributes(), "weight"));
                                    String memoryDate = element.getAttributes().get("memoryDate");
                                    if(memoryDate!=null) {
                                        test.setMemoryDate(ConvertHelper.convertStringToDate(memoryDate, this.context));
                                    }
                                    String testDate = element.getAttributes().get("testDate");
                                    if(testDate!=null) {
                                        test.setTestDate(ConvertHelper.convertStringToDate(testDate, this.context));
                                    }
                                    schoolYear.addTest(test);
                                }
                            }
                        }
                    }
                }

                this.saveSchoolYear(schoolYear);
            }
        }
        return true;
    }

    private void saveSchoolYear(SchoolYear schoolYear) {
        if(schoolYear!=null) {
            schoolYear.setYear(this.saveYear(schoolYear.getYear()));
            schoolYear.setSubject(this.saveSubject(schoolYear.getSubject()));
            for(int i = 0; i<=schoolYear.getTests().size()-1; i++) {
                schoolYear.getTests().get(i).setId(0);
                schoolYear.getTests().get(i).setId(this.sqLite.insertOrUpdateTest(schoolYear.getTests().get(i)));
            }

            if(this.overrideEntry) {
                if(!this.sqLite.entryExists("schoolYears", schoolYear.getID())) {
                    schoolYear.setID(0);
                }
            } else {
                schoolYear.setID(0);
            }

            for(int i = 0; i<=schoolYear.getTests().size()-1; i++) {
                this.sqLite.insertOrUpdateSchoolYear(schoolYear.getSubject().getTitle(), schoolYear.getYear().getTitle(), schoolYear.getTests().get(i));
            }
        }
    }

    public PDFBuilder exportTimeTableToPDF(PDFBuilder pdfBuilder, TimeTable timeTable, List<String> headers, SQLite sqLite) throws Exception {
        pdfBuilder.addTitle(timeTable.getTitle(), "header", Paragraph.ALIGN_CENTER);
        pdfBuilder.addEmptyLine(3);
        String content = "%s%n%s%n%s";
        String classTitle = "";
        if(timeTable.getSchoolClass()!=null) {
            classTitle = this.context.getString(R.string.timetable_class) + ": " + timeTable.getSchoolClass().getTitle();
        }
        String yearTitle = "";
        if(timeTable.getYear()!=null) {
            yearTitle = this.context.getString(R.string.mark_year) + ": " + timeTable.getYear().getTitle();
        }
        String description = "";
        if(!timeTable.getDescription().isEmpty()) {
            description = this.context.getString(R.string.sys_description) + "\n" + timeTable.getDescription();
        }
        content = String.format(content, yearTitle, classTitle, description);
        pdfBuilder.addParagraph("", content, "header", "CONTENT_PARAM");
        pdfBuilder.addEmptyLine(2);

        List<List<Map.Entry<String, BaseColor>>> tblCells = new LinkedList<>();
        List<Hour> hours = sqLite.getHours("");
        for(Hour hour : hours) {
            if(hour.isBreak()) {
                List<Map.Entry<String, BaseColor>> tblRows = new LinkedList<>();
                tblRows.add(new AbstractMap.SimpleEntry<>(hour.getStart() + " - " + hour.getEnd(), BaseColor.GRAY));
                for(int i = 0; i<=6; i++) {
                    tblRows.add(new AbstractMap.SimpleEntry<>("", BaseColor.GRAY));
                }
                tblCells.add(tblRows);
            } else {
                List<Map.Entry<String, BaseColor>> tblRows = new LinkedList<>();
                tblRows.add(new AbstractMap.SimpleEntry<>(hour.getStart() + " - " + hour.getEnd(), BaseColor.WHITE));
                for(int i = 0; i<=6; i++) {
                    tblRows.add(new AbstractMap.SimpleEntry<>("", BaseColor.WHITE));
                }
                tblCells.add(tblRows);
            }
        }

        for(int column = 0; column<=timeTable.getDays().length-1; column++) {
            Day day = timeTable.getDays()[column];
            int tmp = 0;
            if(day!=null) {
                if(MainActivity.globals.getUserSettings().isTimeTableMode()) {
                    Object[] objArray = day.getTeacherHour().values().toArray();
                    int row = 0;
                    for(Hour hour : hours) {
                        if(objArray.length<tmp+1) {
                            break;
                        }
                        if(objArray[tmp] instanceof TeacherHour) {
                            for(Map.Entry<Hour, TeacherHour> entry : day.getTeacherHour().entrySet()) {
                                if(entry.getKey().getId()==hour.getId()) {
                                    Subject subject = entry.getValue().getSubject();
                                    String roomNumber = entry.getValue().getRoomNumber();
                                    if (tblCells.get(row).get(0).getValue() != BaseColor.GRAY) {
                                        tblCells.get(row).set(column + 1, new AbstractMap.SimpleEntry<>(subject.getAlias() + "\n" + roomNumber, new BaseColor(Integer.parseInt(subject.getBackgroundColor()))));
                                        tmp++;
                                    }
                                    break;
                                }
                            }
                        }
                        row++;
                    }
                } else {
                    Object[] objArray = day.getPupilHour().values().toArray();
                    int row = 0;
                    for(Hour hour : hours) {
                        if(objArray.length<tmp+1) {
                            break;
                        }
                        if(objArray[tmp] instanceof PupilHour) {
                            for(Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                                if(entry.getKey().getId()==hour.getId()) {
                                    Subject subject = entry.getValue().getSubject();
                                    String roomNumber = entry.getValue().getRoomNumber();
                                    if (tblCells.get(row).get(0).getValue() != BaseColor.GRAY) {
                                        tblCells.get(row).set(column + 1, new AbstractMap.SimpleEntry<>(subject.getAlias() + "\n" + roomNumber, new BaseColor(Integer.parseInt(subject.getBackgroundColor()))));
                                        tmp++;
                                    }
                                    break;
                                }
                            }
                        }
                        row++;
                    }
                }
            }
        }

        pdfBuilder.addTable(headers, new float[]{20.0f, 15.0f, 15.0f, 15.0f, 15.0f, 15.0f, 15.0f, 15.0f}, tblCells);
        pdfBuilder.newPage();
        return pdfBuilder;
    }

    public String exportTimeTableToTEXT(List<TimeTable> timeTables) {
        CSVBridge writer = new CSVBridge(";", "id;title;description;year;school_class;days", MainActivity.globals.getUserSettings().getDateFormat());
        for(int i = 1; i<=timeTables.size(); i++) {
            try {
                TimeTable timeTable = timeTables.get(i-1);
                writer.writeValue(i, "id", timeTable.getId());
                writer.writeValue(i, "title", timeTable.getTitle());
                writer.writeValue(i, "description", timeTable.getDescription());
                writer.writeValue(i, "year", this.getCSVObjectFromYear(timeTable.getYear()), "(", ")");
                writer.writeValue(i, "school_class", this.getCSVObjectFromSchoolClass(timeTable.getSchoolClass(), "|"), "(", ")");

                if(timeTable.getDays()!=null) {
                    List<CSVObject> dayObjects = new LinkedList<>();
                    for(Day day : timeTable.getDays()) {
                        if(day!=null) {
                            CSVObject csvObject = new CSVObject("|", 3);
                            if(day.getPupilHour()!=null) {
                                csvObject.writeValue("1", "p");
                            } else if(day.getTeacherHour()!=null) {
                                csvObject.writeValue("1", "t");
                            } else {
                                throw new Exception();
                            }
                            csvObject.writeValue("2", day.getPositionInWeek());

                            List<CSVObject> hourObjects = new LinkedList<>();
                            if(day.getPupilHour()!=null) {
                                List<Hour> hours = this.sqLite.getHours("");
                                for(Hour hour : hours) {
                                    boolean hourIsInMap = false;
                                    for(Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                                        if(entry!=null) {
                                            CSVObject hourObject = new CSVObject("###", 4);
                                            if(hour.getId()==entry.getKey().getId()) {
                                                CSVObject timeObject = this.getCSVObjectFromHour(entry.getKey());

                                                if(entry.getValue()!=null) {
                                                    CSVObject subjectObject = this.getCSVObjectFromSubject(entry.getValue().getSubject(), "##", "{", "}");
                                                    CSVObject teacherObject = this.getCSVObjectFromTeacher(entry.getValue().getTeacher(), "##");
                                                    hourObject.writeValue("1", timeObject, "{{", "}}");
                                                    hourObject.writeValue("2", subjectObject, "{{", "}}");
                                                    hourObject.writeValue("3", teacherObject, "{{", "}}");
                                                    hourObject.writeValue("4", entry.getValue().getRoomNumber());
                                                    hourObjects.add(hourObject);
                                                    hourIsInMap = true;
                                                }
                                            }
                                        }
                                    }
                                    if(!hourIsInMap) {
                                        CSVObject hourObject = new CSVObject("###", 3);
                                        CSVObject timeObject = this.getCSVObjectFromHour(hour);
                                        hourObject.writeValue("1", timeObject, "{{", "}}");
                                        hourObject.writeValue("2", new LinkedList<>(), "{{", "}}");
                                        hourObject.writeValue("3", new LinkedList<>(), "{{", "}}");
                                        hourObjects.add(hourObject);
                                    }
                                }
                            }
                            if(day.getTeacherHour()!=null) {
                                List<Hour> hours = this.sqLite.getHours("");
                                for(Hour hour : hours) {
                                    boolean hourIsInMap = false;
                                    for (Map.Entry<Hour, TeacherHour> entry : day.getTeacherHour().entrySet()) {
                                        if (entry != null) {
                                            CSVObject hourObject = new CSVObject("###", 4);
                                            if(hour.getId()==entry.getKey().getId()) {
                                                CSVObject timeObject = this.getCSVObjectFromHour(entry.getKey());

                                                if (entry.getValue() != null) {
                                                    CSVObject subjectObject = this.getCSVObjectFromSubject(entry.getValue().getSubject(), "##", "{", "}");
                                                    CSVObject schoolClassObject = this.getCSVObjectFromSchoolClass(entry.getValue().getSchoolClass(), "##");
                                                    hourObject.writeValue("1", timeObject, "{{", "}}");
                                                    hourObject.writeValue("2", subjectObject, "{{", "}}");
                                                    hourObject.writeValue("3", schoolClassObject, "{{", "}}");
                                                    hourObject.writeValue("4", entry.getValue().getRoomNumber());
                                                    hourObjects.add(hourObject);
                                                    hourIsInMap = true;
                                                }
                                            }
                                        }
                                    }

                                    if(!hourIsInMap) {
                                        CSVObject hourObject = new CSVObject("###", 3);
                                        CSVObject timeObject = this.getCSVObjectFromHour(hour);
                                        hourObject.writeValue("1", timeObject, "{{", "}}");
                                        hourObject.writeValue("2", new LinkedList<>(), "{{", "}}");
                                        hourObject.writeValue("3", new LinkedList<>(), "{{", "}}");
                                        hourObjects.add(hourObject);
                                    }
                                }
                            }

                            csvObject.writeValue("3", hourObjects, "[", "]");
                            dayObjects.add(csvObject);
                        }
                    }
                    writer.writeValue(i, "days", dayObjects, "(", ")");
                    if(timeTables.size()!=i) {
                        writer.addNewLine();
                    }
                }
            } catch (Exception ex) {
                if(this.cancelExport) {
                    return ex.toString();
                } else {
                    writer.replaceWithNewLine();
                }
            }
        }
        return writer.toString();
    }

    public boolean exportTimeTableToXML(String where, String path) throws Exception {
        XMLBuilder builder = new XMLBuilder("TimeTables", new File(path));
        List<TimeTable> timeTables = this.sqLite.getTimeTables(where);
        if(timeTables != null) {
            if(!timeTables.isEmpty()) {
                for(TimeTable timeTable : timeTables) {
                    XMLElement xmlElement = new XMLElement("TimeTable");
                    xmlElement.addAttribute("id", String.valueOf(timeTable.getId()));
                    xmlElement.addAttribute("title", this.escapeText(timeTable.getTitle()));
                    xmlElement.addAttribute("description", String.valueOf(timeTable.getDescription()));

                    XMLElement yearElement = this.getXMLElementFromYear(timeTable.getYear());
                    if(yearElement!=null) {
                        xmlElement.addSubElement(yearElement);
                    }

                    XMLElement classElement = this.getXMLElementFromSchoolClass(timeTable.getSchoolClass());
                    if(classElement!=null) {
                        xmlElement.addSubElement(classElement);
                    }

                    if(timeTable.getDays()!=null) {
                        XMLElement daysElement = new XMLElement("Days");
                        for(Day day : timeTable.getDays()) {
                            if(day!=null) {
                                XMLElement dayElement = new XMLElement("Day");
                                dayElement.addAttribute("positionInWeek", String.valueOf(day.getPositionInWeek()));
                                if(day.getPupilHour()!=null) {
                                    XMLElement pupilElement = new XMLElement("Hours");
                                    pupilElement.addAttribute("mode", "pupil");
                                    List<Hour> hours = this.sqLite.getHours("");
                                    for(Hour hour : hours) {
                                        boolean hourIsInMap = false;
                                        for (Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                                            if (entry.getKey() != null) {
                                                if(hour.getId()==entry.getKey().getId()) {
                                                    XMLElement hourElement = this.getXMLElementFromHour(entry.getKey());
                                                    if (hourElement != null) {
                                                        pupilElement.addSubElement(hourElement);

                                                        if (entry.getValue() != null) {
                                                            if (entry.getValue().getSubject() != null) {
                                                                XMLElement subject = this.getXMLElementFromSubject(entry.getValue().getSubject());
                                                                if (subject != null) {
                                                                    hourElement.addSubElement(subject);
                                                                }
                                                            }

                                                            if (entry.getValue().getTeacher() != null) {
                                                                XMLElement teacher = this.getXMLElementFromTeacher(entry.getValue().getTeacher());
                                                                if (teacher != null) {
                                                                    hourElement.addSubElement(teacher);
                                                                }
                                                            }
                                                            if(entry.getValue().getRoomNumber() != null) {
                                                                XMLElement element = new XMLElement("RoomNumber");
                                                                element.setContent(entry.getValue().getRoomNumber());
                                                                hourElement.addSubElement(element);
                                                            }
                                                        }
                                                        hourIsInMap = true;
                                                    }

                                                }
                                            }
                                        }
                                        if(!hourIsInMap) {
                                            XMLElement hourElement = this.getXMLElementFromHour(hour);
                                            if(hourElement!=null) {
                                                pupilElement.addSubElement(hourElement);
                                            }
                                        }
                                    }
                                    dayElement.addSubElement(pupilElement);
                                } else if(day.getTeacherHour()!=null) {
                                    XMLElement teacherElement = new XMLElement("Hours");
                                    teacherElement.addAttribute("mode", "teacher");
                                    List<Hour> hours = this.sqLite.getHours("");
                                    for(Hour hour : hours) {
                                        boolean hourIsInMap = false;
                                        for (Map.Entry<Hour, PupilHour> entry : day.getPupilHour().entrySet()) {
                                            if (entry.getKey() != null) {
                                                if(hour.getId()==entry.getKey().getId()) {
                                                    XMLElement hourElement = this.getXMLElementFromHour(entry.getKey());
                                                    if (hourElement != null) {
                                                        teacherElement.addSubElement(hourElement);

                                                        if (entry.getValue() != null) {
                                                            if (entry.getValue().getSubject() != null) {
                                                                XMLElement subject = this.getXMLElementFromSubject(entry.getValue().getSubject());
                                                                if (subject != null) {
                                                                    hourElement.addSubElement(subject);
                                                                }
                                                            }

                                                            if (entry.getValue().getTeacher() != null) {
                                                                XMLElement teacher = this.getXMLElementFromTeacher(entry.getValue().getTeacher());
                                                                if (teacher != null) {
                                                                    hourElement.addSubElement(teacher);
                                                                }
                                                            }

                                                            if(entry.getValue().getRoomNumber() != null) {
                                                                XMLElement element = new XMLElement("RoomNumber");
                                                                element.setContent(entry.getValue().getRoomNumber());
                                                                hourElement.addSubElement(element);
                                                            }
                                                        }
                                                        hourIsInMap = true;
                                                    }
                                                }
                                            }
                                        }
                                        if(!hourIsInMap) {
                                            XMLElement hourElement = this.getXMLElementFromHour(hour);
                                            if(hourElement!=null) {
                                                teacherElement.addSubElement(hourElement);
                                            }
                                        }
                                    }
                                    dayElement.addSubElement(teacherElement);
                                }
                                daysElement.addSubElement(dayElement);
                            }
                        }
                        xmlElement.addSubElement(daysElement);
                    }
                    builder.addElement(xmlElement);
                }
            }
        }
        builder.save();
        return true;
    }

    public boolean importTimeTableFromTEXT(String content) {
        if(!content.trim().contains(";")) {
            return false;
        } else {
            CSVBridge reader = new CSVBridge(";", "", content.trim(), true, MainActivity.globals.getUserSettings().getDateFormat());
            for(int i = 1; i<=reader.size(); i++) {
                TimeTable timeTable = new TimeTable();
                timeTable.setId(reader.readIntegerValue(i, "id"));
                timeTable.setTitle(reader.readStringValue(i, "title"));
                timeTable.setDescription(reader.readStringValue(i, "description"));
                timeTable.setYear(this.getYearFromCSVObject(reader.readObjectValue(i, "year", "|", "(", ")")));
                timeTable.setSchoolClass(this.getSchoolClassFromCSVObject(reader.readObjectValue(i, "school_class", "|", "(", ")")));

                List<CSVObject> csvObjects = reader.readObjectsValue(i, "days", "|", "(", ")");
                if(csvObjects!=null) {
                    for(CSVObject csvObject : csvObjects) {
                        Day day = new Day();
                        day.setPositionInWeek(csvObject.readIntegerValue("2"));
                        String type = csvObject.readStringValue("1");
                        if(type.equals("p")) {
                            List<CSVObject> hourObjects = csvObject.readObjectsValue("3", "###", "[", "]");
                            for(CSVObject hourObject : hourObjects) {
                                Hour hour = null;
                                Subject subject = null;
                                Teacher teacher = null;
                                List<CSVObject> subObjects = hourObject.readObjectsValue("1", "##", "{{", "}}");
                                if(subObjects!=null) {
                                    if(!subObjects.isEmpty()) {
                                        hour = this.getHourFromCSVObject(subObjects.get(0));
                                        if(hour!=null) {
                                            hour = this.saveHour(hour);
                                        }
                                    }
                                }

                                subObjects = hourObject.readObjectsValue("2", "##", "{{", "}}");
                                if(subObjects!=null) {
                                    if(!subObjects.isEmpty()) {
                                        subject = this.getSubjectFromCSVObject(subObjects.get(0), "{", "}");
                                        if(subject!=null) {
                                            subject = this.saveSubject(subject);
                                        }
                                    }
                                }

                                subObjects = hourObject.readObjectsValue("3", "##", "{{", "}}");
                                if(subObjects!=null) {
                                    if(!subObjects.isEmpty()) {
                                        teacher = this.getTeacherFromCSVObject(subObjects.get(0));
                                        if(teacher!=null) {
                                            teacher = this.saveTeacher(teacher);
                                        }
                                    }
                                }

                                String roomNumber = hourObject.readStringValue("4");
                                day.addPupilHour(hour, subject, teacher, roomNumber);
                            }
                        } else if(type.equals("t")) {
                            List<CSVObject> hourObjects = csvObject.readObjectsValue("3", "###", "[", "]");
                            for(CSVObject hourObject : hourObjects) {
                                Hour hour = null;
                                Subject subject = null;
                                SchoolClass schoolClass = null;
                                List<CSVObject> subObjects = hourObject.readObjectsValue("1", "##", "{{", "}}");
                                if(subObjects!=null) {
                                    if(!subObjects.isEmpty()) {
                                        hour = this.getHourFromCSVObject(subObjects.get(0));
                                        if(hour!=null) {
                                            hour = this.saveHour(hour);
                                        }
                                    }
                                }

                                subObjects = hourObject.readObjectsValue("2", "##", "{{", "}}");
                                if(subObjects!=null) {
                                    if(!subObjects.isEmpty()) {
                                        subject = this.getSubjectFromCSVObject(subObjects.get(0), "{", "}");
                                        if(subject!=null) {
                                            subject = this.saveSubject(subject);
                                        }
                                    }
                                }

                                subObjects = hourObject.readObjectsValue("3", "##", "{{", "}}");
                                if(subObjects!=null) {
                                    if(!subObjects.isEmpty()) {
                                        schoolClass = this.getSchoolClassFromCSVObject(subObjects.get(0));
                                        if(schoolClass!=null) {
                                            schoolClass = this.saveSchoolClass(schoolClass);
                                        }
                                    }
                                }

                                String roomNumber = hourObject.readStringValue("4");
                                day.addTeacherHour(hour, subject, schoolClass, roomNumber);
                            }
                        }
                        timeTable.addDay(day);
                    }
                }

                this.saveTimeTable(timeTable);
            }
        }
        return true;
    }

    public boolean importTimeTableFromXML(String path) throws Exception {
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            XMLBuilder builder = new XMLBuilder(file);
            List<XMLElement> elements = builder.getElements("TimeTables");
            for(XMLElement xmlElement : elements.get(0).getSubElements()) {
                TimeTable timeTable = new TimeTable();
                timeTable.setId(this.getIntegerFromMap(xmlElement.getAttributes(), "id"));
                timeTable.setTitle(this.unescapeText(xmlElement.getAttributes().get("title")));
                timeTable.setDescription(this.unescapeText(xmlElement.getAttributes().get("description")));

                for(XMLElement subElement : xmlElement.getSubElements()) {
                    if(subElement.getElement().equals("Year")) {
                        timeTable.setYear(this.getYearFromXMLElement(subElement));
                    }
                    if(subElement.getElement().equals("SchoolClass")) {
                        timeTable.setSchoolClass(this.getSchoolClassFromXMLElement(subElement));
                    }
                    if(subElement.getElement().equals("Days")) {
                        if(subElement.getSubElements()!=null) {
                            for(XMLElement dayElement : subElement.getSubElements()) {
                                Day day = new Day();
                                day.setPositionInWeek(this.getIntegerFromMap(dayElement.getAttributes(), "positionInWeek"));
                                if(dayElement.getSubElements()!=null) {
                                    for(XMLElement hoursElement : dayElement.getSubElements()) {
                                        if(Objects.equals(hoursElement.getAttributes().get("mode"), "pupil")) {
                                            if(hoursElement.getSubElements()!=null) {
                                                for(XMLElement hourElement : hoursElement.getSubElements()) {
                                                    Hour hour = this.getHourFromXMLElement(hourElement);

                                                    Subject subject = null;
                                                    Teacher teacher = null;
                                                    String roomNumber = "";
                                                    if(hourElement.getSubElements()!=null) {
                                                        if(!hourElement.getSubElements().isEmpty()) {
                                                            subject = this.getSubjectFromXMLElement(hourElement.getSubElements().get(0));
                                                            if(hourElement.getSubElements().size()>=2) {
                                                                teacher = this.getTeacherFromXMLElement(hourElement.getSubElements().get(1));
                                                            }
                                                            if(hourElement.getSubElements().size()==3) {
                                                                roomNumber = hourElement.getSubElements().get(2).getContent();
                                                            }
                                                        }
                                                    }

                                                    day.addPupilHour(hour, subject, teacher, roomNumber);
                                                }
                                            }
                                        } else if(Objects.equals(hoursElement.getAttributes().get("mode"), "teacher")) {
                                            if(hoursElement.getSubElements()!=null) {
                                                for(XMLElement hourElement : hoursElement.getSubElements()) {
                                                    Hour hour = this.getHourFromXMLElement(hourElement);

                                                    Subject subject = null;
                                                    SchoolClass schoolClass = null;
                                                    String roomNumber = "";
                                                    if(hourElement.getSubElements()!=null) {
                                                        if(!hourElement.getSubElements().isEmpty()) {
                                                            subject = this.getSubjectFromXMLElement(hourElement.getSubElements().get(0));
                                                            if(hourElement.getSubElements().size()>=2) {
                                                                schoolClass = this.getSchoolClassFromXMLElement(hourElement.getSubElements().get(1));
                                                            }

                                                            if(hourElement.getSubElements().size()==3) {
                                                                roomNumber = hourElement.getSubElements().get(2).getContent();
                                                            }
                                                        }
                                                    }

                                                    day.addTeacherHour(hour, subject, schoolClass, roomNumber);
                                                }
                                            }
                                        }
                                    }
                                }
                                timeTable.addDay(day);
                            }
                        }
                    }
                }

                this.saveTimeTable(timeTable);
            }
        }
        return true;
    }

    private void saveTimeTable(TimeTable timeTable) {
        if(timeTable!=null) {
            timeTable.setYear(this.saveYear(timeTable.getYear()));
            timeTable.setSchoolClass(this.saveSchoolClass(timeTable.getSchoolClass()));

            if(this.overrideEntry) {
                if(!this.sqLite.entryExists("timeTables", timeTable.getId())) {
                    timeTable.setId(0);
                }
            } else {
                timeTable.setId(0);
            }
            this.sqLite.insertOrUpdateTimeTable(timeTable);
        }
    }

    public PDFBuilder exportNoteToPDF(PDFBuilder pdfBuilder, Note note) throws Exception {
        pdfBuilder.addTitle(note.getTitle(), "header", Paragraph.ALIGN_CENTER);
        pdfBuilder.addParagraph("", ConvertHelper.convertDateToString(note.getMemoryDate(), this.context), "header", "subHeader");
        pdfBuilder.addParagraph(this.context.getString(R.string.sys_description), note.getDescription(), "subHeader", "CONTENT_PARAM");
        pdfBuilder.newPage();
        return pdfBuilder;
    }

    public String exportNoteToTEXT(List<Note> notes) {
        CSVBridge writer = new CSVBridge(";", "id;title;memory_date;description", MainActivity.globals.getUserSettings().getDateFormat());
        for(int i = 1; i<=notes.size(); i++) {
            try {
                writer.writeValue(i, "id", notes.get(i-1).getId());
                writer.writeValue(i, "title", notes.get(i-1).getTitle());
                writer.writeValue(i, "memory_date", notes.get(i-1).getMemoryDate());
                writer.writeValue(i, "description", notes.get(i-1).getDescription());
                if(notes.size()!=i) {
                    writer.addNewLine();
                }
            } catch (Exception ex) {
                if(this.cancelExport) {
                    return ex.toString();
                } else {
                    writer.replaceWithNewLine();
                }
            }
        }
        return writer.toString();
    }

    public boolean exportNoteToXML(String where, String path) throws Exception {
        XMLBuilder builder = new XMLBuilder("Notes", new File(path));
        List<Note> notes = this.sqLite.getNotes(where);
        if(notes != null) {
            if(!notes.isEmpty()) {
                for(Note note : notes) {
                    XMLElement element = new XMLElement("Note");
                    element.addAttribute("id", String.valueOf(note.getId()));
                    element.addAttribute("memory_date", ConvertHelper.convertDateToString(note.getMemoryDate(), this.context));
                    element.addAttribute("title", this.escapeText(note.getTitle()));
                    element.setContent(this.escapeText(note.getDescription()));
                    builder.addElement(element);
                }
            }
        }
        builder.save();
        return true;
    }

    public boolean importNoteFromTEXT(String content) {
        if (!content.trim().contains(";")) {
            return false;
        } else {
            CSVBridge reader = new CSVBridge(";", "", content.trim(), true, MainActivity.globals.getUserSettings().getDateFormat());
            for(int i = 1; i<=reader.size(); i++) {
                Note note = new Note();
                note.setId(reader.readIntegerValue(i, "id"));
                note.setTitle(reader.readStringValue(i, "title"));
                note.setMemoryDate(reader.readDateValue(i, "memory_date"));
                note.setDescription(reader.readStringValue(i, "description"));

                this.saveNote(note);
            }
        }
        return true;
    }

    public boolean importNoteFromXML(String path) throws Exception {
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            XMLBuilder builder = new XMLBuilder(file);
            List<XMLElement> elements = builder.getElements("Notes");
            for(XMLElement xmlElement : elements.get(0).getSubElements()) {
                Note note = new Note();
                note.setId(this.getIntegerFromMap(xmlElement.getAttributes(), "id"));
                String memory = xmlElement.getAttributes().get("memory_date");
                if(memory!=null) {
                    if(!memory.isEmpty()) {
                        note.setMemoryDate(ConvertHelper.convertStringToDate(memory, this.context));
                    }
                }
                note.setTitle(this.unescapeText(xmlElement.getAttributes().get("title")));
                note.setDescription(this.unescapeText(xmlElement.getContent()));

                this.saveNote(note);
            }
        }
        return true;
    }

    private void saveNote(Note note) {
        if(note!=null) {
            if(this.overrideEntry) {
                if(this.sqLite.entryExists("notes", note.getId())) {
                    this.sqLite.insertOrUpdateNote(note);
                    return;
                } else {
                    note.setId(0);
                }
            } else {
                note.setId(0);
            }
            this.sqLite.insertOrUpdateNote(note);
        }
    }

    public PDFBuilder exportToDoListToPDF(PDFBuilder pdfBuilder, ToDoList toDoList) throws Exception {
        pdfBuilder.addTitle(toDoList.getTitle(), "header", Paragraph.ALIGN_CENTER);
        pdfBuilder.addTitle(ConvertHelper.convertDateToString(toDoList.getListDate(), this.context), "subHeader", Paragraph.ALIGN_CENTER);
        pdfBuilder.addParagraph(this.context.getString(R.string.sys_description), toDoList.getDescription(), "subHeader", "CONTENT_PARAM");
        pdfBuilder.addEmptyLine(3);

        List<String> headerList =
                Arrays.asList(
                        this.context.getString(R.string.sys_title),
                        this.context.getString(R.string.todo_category),
                        this.context.getString(R.string.sys_memory),
                        this.context.getString(R.string.sys_description),
                        "",
                        this.context.getString(R.string.todo_solved)
                );

        List<List<Map.Entry<String, BaseColor>>> ls = new LinkedList<>();
        for(ToDo toDo : toDoList.getToDos()) {
            List<Map.Entry<String, BaseColor>> toDoEntry = new LinkedList<>();
            toDoEntry.add(new AbstractMap.SimpleEntry<>(toDo.getTitle(), BaseColor.WHITE));
            toDoEntry.add(new AbstractMap.SimpleEntry<>(toDo.getCategory(), BaseColor.WHITE));
            toDoEntry.add(new AbstractMap.SimpleEntry<>(ConvertHelper.convertDateToString(toDo.getMemoryDate(), this.context), BaseColor.WHITE));
            toDoEntry.add(new AbstractMap.SimpleEntry<>(toDo.getDescription(), BaseColor.WHITE));
            toDoEntry.add(new AbstractMap.SimpleEntry<>(String.valueOf(toDo.getImportance()), BaseColor.WHITE));
            toDoEntry.add(new AbstractMap.SimpleEntry<>(String.valueOf(toDo.isSolved()), BaseColor.WHITE));
            ls.add(toDoEntry);
        }
        pdfBuilder.addTable(headerList, null, ls);
        pdfBuilder.newPage();
        return pdfBuilder;
    }

    public String exportToDoListToTEXT(List<ToDoList> toDoLists) {
        CSVBridge writer = new CSVBridge(";", "id;title;description;list_date;to_dos", MainActivity.globals.getUserSettings().getDateFormat());
        for(int i = 1; i<=toDoLists.size(); i++) {
            try {
                writer.writeValue(i, "id", toDoLists.get(i-1).getId());
                writer.writeValue(i, "title", toDoLists.get(i-1).getTitle());
                writer.writeValue(i, "description", toDoLists.get(i-1).getDescription());
                writer.writeValue(i, "list_date", toDoLists.get(i-1).getListDate());
                List<CSVObject> csvObjects = new LinkedList<>();
                for(ToDo toDo : toDoLists.get(i-1).getToDos()) {
                    if(toDo!=null) {
                        csvObjects.add(this.getCSVObjectFromToDo(toDo));
                    }
                }
                writer.writeValue(i, "to_dos", csvObjects, "(", ")");
                if(toDoLists.size()!=i) {
                    writer.addNewLine();
                }
            } catch (Exception ex) {
                if(this.cancelExport) {
                    return ex.toString();
                } else {
                    writer.replaceWithNewLine();
                }
            }
        }
        return writer.toString();
    }

    public boolean exportToDoListToXMLElement(String where, String path) throws Exception {
        XMLBuilder builder = new XMLBuilder("ToDoLists", new File(path));
        List<ToDoList> toDoLists = this.sqLite.getToDoLists(where);
        if(toDoLists != null) {
            if(!toDoLists.isEmpty()) {
                for(ToDoList toDoList : toDoLists) {
                    XMLElement element = new XMLElement("ToDoList");
                    element.addAttribute("id", String.valueOf(toDoList.getId()));
                    element.addAttribute("list_date", ConvertHelper.convertDateToString(toDoList.getListDate(), this.context));
                    element.addAttribute("title", this.escapeText(toDoList.getTitle()));
                    if(toDoList.getToDos()!=null) {
                        if(!toDoList.getToDos().isEmpty()) {
                            for(ToDo toDo : toDoList.getToDos()) {
                                element.addSubElement(this.getXMLElementFromToDo(toDo));
                            }
                        }
                    }
                    element.setContent(this.escapeText(toDoList.getDescription()));
                    builder.addElement(element);
                }
            }
        }
        builder.save();
        return true;
    }

    public boolean importToDoListFromText(String content) {
        if (!content.contains(";")) {
            return false;
        } else {
            CSVBridge reader = new CSVBridge(";", "", content.trim(), true, MainActivity.globals.getUserSettings().getDateFormat());
            for(int i = 1; i<=reader.size(); i++) {
                ToDoList toDoList = new ToDoList();
                toDoList.setId(reader.readIntegerValue(i, "id"));
                toDoList.setTitle(reader.readStringValue(i, "title"));
                toDoList.setDescription(reader.readStringValue(i, "description"));
                toDoList.setListDate(reader.readDateValue(i, "list_date"));
                this.saveToDoList(toDoList);

                List<CSVObject> csvObjects = reader.readObjectsValue(i, "to_dos", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        for(CSVObject csvObject : csvObjects) {
                            if(csvObject!=null) {
                                toDoList.addToDo(this.saveToDo(this.getToDoFromCSVObject(csvObject), toDoList.getTitle()));
                            }
                        }
                    }
                }


            }
        }
        return true;
    }

    public boolean importToDoListFromXML(String path) throws Exception {
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            XMLBuilder builder = new XMLBuilder(file);
            List<XMLElement> elements = builder.getElements("ToDoLists");
            for(XMLElement xmlElement : elements.get(0).getSubElements()) {
                ToDoList toDoList = new ToDoList();
                toDoList.setId(this.getIntegerFromMap(xmlElement.getAttributes(), "id"));
                toDoList.setListDate(ConvertHelper.convertStringToDate(xmlElement.getAttributes().get("list_date"), this.context));
                toDoList.setTitle(this.unescapeText(xmlElement.getAttributes().get("title")));
                toDoList.setDescription(this.unescapeText(xmlElement.getContent()));
                this.saveToDoList(toDoList);

                if(xmlElement.getSubElements()!=null) {
                    for(XMLElement element : xmlElement.getSubElements()) {
                        toDoList.addToDo(this.saveToDo(this.getToDoFromXMLElement(element), toDoList.getTitle()));
                    }
                }
            }
        }
        return true;
    }

    private void saveToDoList(ToDoList toDoList) {
        if(toDoList!=null) {
            for(int i = 0; i<=toDoList.getToDos().size()-1; i++) {
                toDoList.getToDos().set(i, this.saveToDo(toDoList.getToDos().get(i), toDoList.getTitle()));
            }

            if(this.overrideEntry) {
                if(!this.sqLite.entryExists("toDoLists", toDoList.getId())) {
                    toDoList.setId(0);
                }
            } else {
                toDoList.setId(0);
            }
            this.sqLite.insertOrUpdateToDoList(toDoList);
        }
    }

    public PDFBuilder exportTimerEventToPDF(PDFBuilder pdfBuilder, TimerEvent timerEvent) throws Exception {
        pdfBuilder.addTitle(timerEvent.getTitle(), "header", Paragraph.ALIGN_CENTER);
        pdfBuilder.addTitle(this.context.getString(R.string.todo_category) + ": " + timerEvent.getCategory(), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        pdfBuilder.addTitle(this.context.getString(R.string.mark_date) + ": " + ConvertHelper.convertDateToString(timerEvent.getEventDate(), this.context), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        pdfBuilder.addTitle(this.context.getString(R.string.sys_memory) + ": " + ConvertHelper.convertDateToString(timerEvent.getMemoryDate(), this.context), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        if(timerEvent.getSchoolClass()!=null) {
            pdfBuilder.addTitle(this.context.getString(R.string.timetable_class) + ": " + timerEvent.getSchoolClass().getTitle(), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        }
        if(timerEvent.getTeacher()!=null) {
            pdfBuilder.addTitle(this.context.getString(R.string.timetable_teacher) + ": " + timerEvent.getTeacher().getLastName(), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        }
        if(timerEvent.getSubject()!=null) {
            pdfBuilder.addTitle(this.context.getString(R.string.timetable_subject_alias) + ": " + timerEvent.getSubject().getAlias(), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        }
        pdfBuilder.addParagraph(this.context.getString(R.string.sys_description), timerEvent.getDescription(), "CONTENT_PARAM", "CONTENT_PARAM");
        pdfBuilder.newPage();
        return pdfBuilder;
    }

    public String exportTimerEventToTEXT(List<TimerEvent> timerEvents) {
        CSVBridge writer = new CSVBridge(";", "id;title;category;description;event_date;memory_date;school_class;subject;teacher", MainActivity.globals.getUserSettings().getDateFormat());
        for(int i = 1; i<=timerEvents.size(); i++) {
            try {
                writer.writeValue(i, "id", timerEvents.get(i-1).getId());
                writer.writeValue(i, "title", timerEvents.get(i-1).getTitle());
                writer.writeValue(i, "category", timerEvents.get(i-1).getCategory());
                writer.writeValue(i, "description", timerEvents.get(i-1).getDescription());
                writer.writeValue(i, "event_date", timerEvents.get(i-1).getEventDate());
                writer.writeValue(i, "memory_date", timerEvents.get(i-1).getMemoryDate());
                writer.writeValue(i, "school_class", this.getCSVObjectFromSchoolClass(timerEvents.get(i-1).getSchoolClass(), "|"), "(", ")");
                writer.writeValue(i, "subject", this.getCSVObjectFromSubject(timerEvents.get(i-1).getSubject(), "|", "[", "]"), "(", ")");
                writer.writeValue(i, "teacher", this.getCSVObjectFromTeacher(timerEvents.get(i-1).getTeacher(), "|"), "(", ")");
                if(timerEvents.size()!=i) {
                    writer.addNewLine();
                }
            } catch (Exception ex) {
                if(this.cancelExport) {
                    return ex.toString();
                } else {
                    writer.replaceWithNewLine();
                }
            }
        }
        return writer.toString();
    }

    public boolean exportTimerEventToXML(String where, String path) throws Exception {
        XMLBuilder builder = new XMLBuilder("TimerEvents", new File(path));
        List<TimerEvent> timerEvents = this.sqLite.getTimerEvents(where);
        if(timerEvents != null) {
            if(!timerEvents.isEmpty()) {
                for(TimerEvent timerEvent : timerEvents) {
                    XMLElement element = new XMLElement("TimerEvent");
                    element.addAttribute("id", String.valueOf(timerEvent.getId()));
                    element.addAttribute("event_date", ConvertHelper.convertDateToString(timerEvent.getEventDate(), this.context));
                    element.addAttribute("memory_date", ConvertHelper.convertDateToString(timerEvent.getMemoryDate(), this.context));
                    element.addAttribute("title", this.escapeText(timerEvent.getTitle()));
                    element.addAttribute("category", this.escapeText(timerEvent.getCategory()));
                    element.addSubElement(this.getXMLElementFromSubject(timerEvent.getSubject()));
                    element.addSubElement(this.getXMLElementFromSchoolClass(timerEvent.getSchoolClass()));
                    element.addSubElement(this.getXMLElementFromTeacher(timerEvent.getTeacher()));
                    element.setContent(this.escapeText(timerEvent.getDescription()));
                    builder.addElement(element);
                }
            }
        }
        builder.save();
        return true;
    }

    public boolean importTimerEventFromTEXT(String content) {
        if (!content.trim().contains(";")) {
            return false;
        } else {
            CSVBridge reader = new CSVBridge(";", "", content.trim(), true, MainActivity.globals.getUserSettings().getDateFormat());
            for(int i = 1; i<=reader.size(); i++) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setId(reader.readIntegerValue(i, "id"));
                timerEvent.setTitle(reader.readStringValue(i, "title"));
                timerEvent.setCategory(reader.readStringValue(i, "category"));
                timerEvent.setDescription(reader.readStringValue(i, "description"));
                timerEvent.setEventDate(reader.readDateValue(i, "event_date"));
                timerEvent.setMemoryDate(reader.readDateValue(i, "memory_date"));
                List<CSVObject> csvObjects = reader.readObjectsValue(i, "school_class", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        timerEvent.setSchoolClass(this.getSchoolClassFromCSVObject(csvObjects.get(0)));
                    }
                }
                csvObjects = reader.readObjectsValue(i, "subject", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        timerEvent.setSubject(this.getSubjectFromCSVObject(csvObjects.get(0), "[", "]"));
                    }
                }
                csvObjects = reader.readObjectsValue(i, "teacher", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        timerEvent.setTeacher(this.getTeacherFromCSVObject(csvObjects.get(0)));
                    }
                }

                this.saveTimerEvent(timerEvent);
            }
        }
        return true;
    }

    public boolean importTimerEventFromXML(String path) throws Exception {
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            XMLBuilder builder = new XMLBuilder(file);
            List<XMLElement> elements = builder.getElements("TimerEvents");
            for(XMLElement xmlElement : elements.get(0).getSubElements()) {
                TimerEvent timerEvent = new TimerEvent();
                timerEvent.setId(this.getIntegerFromMap(xmlElement.getAttributes(), "id"));
                String memory = xmlElement.getAttributes().get("memory_date");
                if(memory!=null) {
                    if(!memory.isEmpty()) {
                        timerEvent.setMemoryDate(ConvertHelper.convertStringToDate(memory, this.context));
                    }
                }
                String event = xmlElement.getAttributes().get("event_date");
                if(event!=null) {
                    if(!event.isEmpty()) {
                        timerEvent.setEventDate(ConvertHelper.convertStringToDate(event, this.context));
                    }
                }
                timerEvent.setTitle(this.unescapeText(xmlElement.getAttributes().get("title")));
                timerEvent.setCategory(this.unescapeText(xmlElement.getAttributes().get("category")));
                for(XMLElement element : xmlElement.getSubElements()) {
                    switch (element.getElement()) {
                        case "Teacher":
                            timerEvent.setTeacher(this.getTeacherFromXMLElement(element));
                            break;
                        case "Subject":
                            timerEvent.setSubject(this.getSubjectFromXMLElement(element));
                            break;
                        case "SchoolClass":
                            timerEvent.setSchoolClass(this.getSchoolClassFromXMLElement(element));
                            break;
                        default:
                    }
                }
                timerEvent.setDescription(this.unescapeText(xmlElement.getContent()));
                this.saveTimerEvent(timerEvent);
            }
        }
        return true;
    }

    public PDFBuilder exportLearningCardGroupToPDF(PDFBuilder pdfBuilder, LearningCardGroup learningCardGroup) throws Exception {
        pdfBuilder.addTitle(learningCardGroup.getTitle(), "header", Paragraph.ALIGN_CENTER);
        pdfBuilder.addTitle(this.context.getString(R.string.todo_category) + ": " + learningCardGroup.getCategory(), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        pdfBuilder.addTitle(this.context.getString(R.string.learningCard_group_deadline) + ": " + ConvertHelper.convertDateToString(learningCardGroup.getDeadLine(), this.context), "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        if(learningCardGroup.getTeacher()!=null) {
            String name = "";
            if(learningCardGroup.getTeacher().getFirstName()!=null) {
                if(!learningCardGroup.getTeacher().getFirstName().isEmpty()) {
                    name = learningCardGroup.getTeacher().getFirstName() + " ";
                }
            }
            name += learningCardGroup.getTeacher().getLastName();
            pdfBuilder.addTitle(this.context.getString(R.string.timetable_teacher) + ": " + name, "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        }
        if(learningCardGroup.getSubject()!=null) {
            String subject = learningCardGroup.getSubject().getAlias() + " " + learningCardGroup.getSubject().getTitle();
            pdfBuilder.addTitle(this.context.getString(R.string.timetable_subject_alias) + ": " + subject, "CONTENT_PARAM", Paragraph.ALIGN_LEFT);
        }
        pdfBuilder.addParagraph(this.context.getString(R.string.sys_description), learningCardGroup.getDescription(), "CONTENT_PARAM", "CONTENT_PARAM");
        List<List<Map.Entry<String, BaseColor>>> rows = new LinkedList<>();
        for(LearningCard learningCard : learningCardGroup.getLearningCards()) {
            List<Map.Entry<String, BaseColor>> columns = new LinkedList<>();
            columns.add(new AbstractMap.SimpleEntry<>(learningCard.getTitle(), BaseColor.WHITE));
            columns.add(new AbstractMap.SimpleEntry<>(learningCard.getQuestion(), BaseColor.WHITE));
            columns.add(new AbstractMap.SimpleEntry<>(learningCard.getAnswer(), BaseColor.WHITE));
            columns.add(new AbstractMap.SimpleEntry<>(learningCard.getCategory(), BaseColor.WHITE));
            columns.add(new AbstractMap.SimpleEntry<>(String.valueOf(learningCard.getPriority()), BaseColor.WHITE));
            columns.add(new AbstractMap.SimpleEntry<>(learningCard.getNote1(), BaseColor.WHITE));
            columns.add(new AbstractMap.SimpleEntry<>(learningCard.getNote2(), BaseColor.WHITE));
            rows.add(columns);
        }
        List<String> header = new LinkedList<>();
        header.add(this.context.getString(R.string.sys_title));
        header.add(this.context.getString(R.string.learningCard_question));
        header.add(this.context.getString(R.string.learningCard_answer));
        header.add(this.context.getString(R.string.todo_category));
        header.add(this.context.getString(R.string.learningCard_priority));
        header.add(this.context.getString(R.string.learningCard_note1));
        header.add(this.context.getString(R.string.learningCard_note2));
        float[] sizes = {1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f};
        pdfBuilder.addTable(header, sizes, rows);
        pdfBuilder.newPage();
        return pdfBuilder;
    }

    public String exportLearningCardGroupToTEXT(List<LearningCardGroup> learningCardGroups) {
        CSVBridge writer = new CSVBridge(";", "id;title;category;description;deadline;subject;teacher;learningCards", MainActivity.globals.getUserSettings().getDateFormat());
        for(int i = 1; i<=learningCardGroups.size(); i++) {
            try {
                writer.writeValue(i, "id", learningCardGroups.get(i-1).getId());
                writer.writeValue(i, "title", learningCardGroups.get(i-1).getTitle());
                writer.writeValue(i, "category", learningCardGroups.get(i-1).getCategory());
                writer.writeValue(i, "description", learningCardGroups.get(i-1).getDescription());
                writer.writeValue(i, "deadline", learningCardGroups.get(i-1).getDeadLine());
                writer.writeValue(i, "subject", this.getCSVObjectFromSubject(learningCardGroups.get(i-1).getSubject(), "|", "[", "]"), "(", ")");
                writer.writeValue(i, "teacher", this.getCSVObjectFromTeacher(learningCardGroups.get(i-1).getTeacher(), "|"), "(", ")");
                List<CSVObject> csvObjects = new LinkedList<>();
                for(LearningCard learningCard : learningCardGroups.get(i-1).getLearningCards()) {
                    CSVObject csvObject = new CSVObject("|", 8);
                    csvObject.writeValue("1", learningCard.getId());
                    csvObject.writeValue("2", learningCard.getTitle());
                    csvObject.writeValue("3", learningCard.getQuestion());
                    csvObject.writeValue("4", learningCard.getAnswer());
                    csvObject.writeValue("5", learningCard.getCategory());
                    csvObject.writeValue("6", learningCard.getPriority());
                    csvObject.writeValue("7", learningCard.getNote1());
                    csvObject.writeValue("8", learningCard.getNote2());
                    csvObjects.add(csvObject);
                }
                writer.writeValue(i, "learningCards", csvObjects, "(", ")");
                if(learningCardGroups.size()!=i) {
                    writer.addNewLine();
                }
            } catch (Exception ex) {
                if(this.cancelExport) {
                    return ex.toString();
                } else {
                    writer.replaceWithNewLine();
                }
            }
        }
        return writer.toString();
    }

    public boolean exportLearningCardGroupToXML(String where, String path) throws Exception {
        XMLBuilder builder = new XMLBuilder("LearningCardGroups", new File(path));
        List<LearningCardGroup> learningCardGroups = this.sqLite.getLearningCardGroups(where, true);
        if(learningCardGroups != null) {
            if(!learningCardGroups.isEmpty()) {
                for(LearningCardGroup learningCardGroup : learningCardGroups) {
                    XMLElement element = new XMLElement("LearningCardGroup");
                    element.addAttribute("id", String.valueOf(learningCardGroup.getId()));
                    element.addAttribute("deadline", ConvertHelper.convertDateToString(learningCardGroup.getDeadLine(), this.context));
                    element.addAttribute("title", this.escapeText(learningCardGroup.getTitle()));
                    element.addAttribute("category", this.escapeText(learningCardGroup.getCategory()));
                    element.addSubElement(this.getXMLElementFromSubject(learningCardGroup.getSubject()));
                    element.addSubElement(this.getXMLElementFromTeacher(learningCardGroup.getTeacher()));
                    element.setContent(this.escapeText(learningCardGroup.getDescription()));
                    for(LearningCard learningCard : learningCardGroup.getLearningCards()) {
                        XMLElement learningCardElement = new XMLElement("LearningCard");
                        learningCardElement.addAttribute("id", String.valueOf(learningCard.getId()));
                        learningCardElement.addAttribute("title", learningCard.getTitle());
                        learningCardElement.addAttribute("category", learningCard.getCategory());
                        learningCardElement.addAttribute("question", learningCard.getQuestion());
                        learningCardElement.addAttribute("note1", learningCard.getNote1());
                        learningCardElement.addAttribute("note2", learningCard.getNote2());
                        learningCardElement.addAttribute("priority", String.valueOf(learningCard.getPriority()));
                        learningCardElement.setContent(learningCard.getAnswer());
                        element.addSubElement(learningCardElement);
                    }
                    builder.addElement(element);
                }
            }
        }
        builder.save();
        return true;
    }

    public boolean importLearningCardGroupFromText(String content) {
        if (!content.trim().contains(";")) {
            return false;
        } else {
            CSVBridge reader = new CSVBridge(";", "", content.trim(), true, MainActivity.globals.getUserSettings().getDateFormat());
            for(int i = 1; i<=reader.size(); i++) {
                LearningCardGroup learningCardGroup = new LearningCardGroup();
                learningCardGroup.setId(reader.readIntegerValue(i, "id"));
                learningCardGroup.setTitle(reader.readStringValue(i, "title"));
                learningCardGroup.setCategory(reader.readStringValue(i, "category"));
                learningCardGroup.setDescription(reader.readStringValue(i, "description"));
                learningCardGroup.setDeadLine(reader.readDateValue(i, "deadline"));
                List<CSVObject> csvObjects = reader.readObjectsValue(i, "subject", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        learningCardGroup.setSubject(this.getSubjectFromCSVObject(csvObjects.get(0), "[", "]"));
                    }
                }
                csvObjects = reader.readObjectsValue(i, "teacher", "|", "(", ")");
                if(csvObjects!=null) {
                    if(!csvObjects.isEmpty()) {
                        learningCardGroup.setTeacher(this.getTeacherFromCSVObject(csvObjects.get(0)));
                    }
                }
                csvObjects = reader.readObjectsValue(i, "learningCards", "|", "(", ")");
                if(csvObjects!=null) {
                    if (!csvObjects.isEmpty()) {
                        for(CSVObject csvObject : csvObjects) {
                            LearningCard learningCard = new LearningCard();
                            learningCard.setId(csvObject.readIntegerValue("1"));
                            learningCard.setTitle(csvObject.readStringValue("2"));
                            learningCard.setQuestion(csvObject.readStringValue("3"));
                            learningCard.setAnswer(csvObject.readStringValue("4"));
                            learningCard.setCategory(csvObject.readStringValue("5"));
                            learningCard.setPriority(csvObject.readIntegerValue("6"));
                            learningCard.setNote1(csvObject.readStringValue("7"));
                            learningCard.setNote2(csvObject.readStringValue("8"));
                            learningCardGroup.getLearningCards().add(this.saveLearningCard(learningCard));
                        }
                    }
                }

                this.saveLearningCardGroup(learningCardGroup);
            }
        }
        return true;
    }

    public boolean importLearningCardGroupFromXML(String path) throws Exception {
        File file = new File(path);
        if(file.exists() && file.isFile()) {
            XMLBuilder builder = new XMLBuilder(file);
            List<XMLElement> elements = builder.getElements("LearningCardGroups");
            for(XMLElement xmlElement : elements.get(0).getSubElements()) {
                LearningCardGroup learningCardGroup = new LearningCardGroup();
                learningCardGroup.setId(this.getIntegerFromMap(xmlElement.getAttributes(), "id"));
                String deadline = xmlElement.getAttributes().get("deadline");
                if(deadline!=null) {
                    if(!deadline.isEmpty()) {
                        learningCardGroup.setDeadLine(ConvertHelper.convertStringToDate(deadline, this.context));
                    }
                }
                learningCardGroup.setTitle(this.unescapeText(xmlElement.getAttributes().get("title")));
                learningCardGroup.setCategory(this.unescapeText(xmlElement.getAttributes().get("category")));
                for(XMLElement element : xmlElement.getSubElements()) {
                    switch (element.getElement()) {
                        case "Teacher":
                            learningCardGroup.setTeacher(this.getTeacherFromXMLElement(element));
                            break;
                        case "Subject":
                            learningCardGroup.setSubject(this.getSubjectFromXMLElement(element));
                            break;
                        default:
                    }
                }
                learningCardGroup.setDescription(this.unescapeText(xmlElement.getContent()));
                for(XMLElement subElement : xmlElement.getSubElements()) {
                    LearningCard learningCard = new LearningCard();
                    Map<String, String> attributes = subElement.getAttributes();
                    if(attributes!=null) {
                        String id = attributes.get("id");
                        if(id!=null) {
                            learningCard.setId(Integer.parseInt(id));
                        }
                        learningCard.setTitle(attributes.get("title"));
                        learningCard.setCategory(attributes.get("category"));
                        learningCard.setQuestion(attributes.get("question"));
                        learningCard.setNote1(attributes.get("note1"));
                        learningCard.setNote2(attributes.get("note2"));
                        String priority = attributes.get("priority");
                        if(priority!=null) {
                            learningCard.setId(Integer.parseInt(priority));
                        }
                        learningCard.setAnswer(subElement.getContent());
                        learningCardGroup.getLearningCards().add(this.saveLearningCard(learningCard));
                    }
                }
                this.saveLearningCardGroup(learningCardGroup);
            }
        }
        return true;
    }

    private LearningCard saveLearningCard(LearningCard learningCard) {
        if(learningCard!=null) {
            if(this.overrideEntry) {
                if(this.sqLite.entryExists("learningCards", learningCard.getId())) {
                    return learningCard;
                } else {
                    learningCard.setId(0);
                }
            } else {
                learningCard.setId(0);
            }
        }
        return learningCard;
    }

    private void saveLearningCardGroup(LearningCardGroup learningCardGroup) {
        if(learningCardGroup!=null) {
            if(this.overrideEntry) {
                if(this.sqLite.entryExists("learningCardGroups", learningCardGroup.getId())) {
                    this.sqLite.insertOrUpdateLearningCardGroup(learningCardGroup);
                    return;
                } else {
                    learningCardGroup.setId(0);
                }
            } else {
                learningCardGroup.setId(0);
            }
            this.sqLite.insertOrUpdateLearningCardGroup(learningCardGroup);
        }
    }

    public static String findExistingFolder(Context context) {
        File documentsFolder, downloadsFolder;
        documentsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if(ApiHelper.isExternalStorageWritable()) {
            if(documentsFolder.exists() && documentsFolder.isDirectory()) {
                return documentsFolder.getAbsolutePath();
            } else if(downloadsFolder.exists() && downloadsFolder.isDirectory()) {
                return downloadsFolder.getAbsolutePath();
            } else {
                if(documentsFolder.mkdirs()) {
                    return documentsFolder.getAbsolutePath();
                } else if(downloadsFolder.mkdirs()) {
                    return downloadsFolder.getAbsolutePath();
                } else {
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                }
            }
        } else {
            return context.getFilesDir().getAbsolutePath();
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    private void saveTimerEvent(TimerEvent timerEvent) {
        if(timerEvent!=null) {
            timerEvent.setSchoolClass(this.saveSchoolClass(timerEvent.getSchoolClass()));
            timerEvent.setSubject(this.saveSubject(timerEvent.getSubject()));
            timerEvent.setTeacher(this.saveTeacher(timerEvent.getTeacher()));

            if(this.overrideEntry) {
                if(!this.sqLite.entryExists("timerEvents", timerEvent.getId())) {
                    timerEvent.setId(0);
                }
            } else {
                timerEvent.setId(0);
            }
            this.sqLite.insertOrUpdateTimerEvent(timerEvent);
        }
    }

    private boolean getBooleanFromMap(Map<String, String> map, String key) {
        String result = map.get(key);
        return result != null && (result.equals("1") || result.toLowerCase().equals("true"));
    }

    private int getIntegerFromMap(Map<String, String> map, String key) {
        String result = map.get(key);
        if(result==null) {
            return 0;
        } else {
            if(Helper.isInteger(result)) {
                return Integer.parseInt(result);
            } else {
                return 0;
            }
        }
    }

    private double getDoubleFromMap(Map<String, String> map, String key) {
        String result = map.get(key);
        if(result==null) {
            return 0.0;
        } else {
            if(Helper.isDouble(result)) {
                return Double.parseDouble(result);
            } else {
                return 0.0;
            }
        }
    }

    private Hour getHourFromCSVObject(CSVObject csvObject) {
        if(csvObject!=null) {
            Hour hour = new Hour();
            hour.setId(csvObject.readIntegerValue("1"));
            hour.setStart(csvObject.readStringValue("2"));
            hour.setEnd(csvObject.readStringValue("3"));
            hour.setBreak(csvObject.readBooleanValue("4"));
            return hour;
        } else {
            return null;
        }
    }

    private CSVObject getCSVObjectFromHour(Hour hour) {
        if(hour!=null) {
            CSVObject csvObject = new CSVObject("##", 4);
            csvObject.writeValue("1", hour.getId());
            csvObject.writeValue("2", hour.getStart());
            csvObject.writeValue("3", hour.getEnd());
            csvObject.writeValue("4", hour.isBreak());
            return csvObject;
        } else {
            return null;
        }
    }

    private Hour getHourFromXMLElement(XMLElement element) {
        Hour hour = new Hour();
        hour.setId(this.getIntegerFromMap(element.getAttributes(), "id"));
        hour.setStart(this.unescapeText(element.getAttributes().get("start")));
        hour.setEnd(this.unescapeText(element.getAttributes().get("end")));
        hour.setBreak(this.getBooleanFromMap(element.getAttributes(), "break"));

        if(!this.sqLite.entryExists("hours", hour.getId())) {
            hour.setId(0);
        }
        hour.setId(sqLite.insertOrUpdateHour(hour));
        return hour;
    }

    private XMLElement getXMLElementFromHour(Hour hour) {
        if(hour!=null) {
            XMLElement xmlElement = new XMLElement("Hour");
            xmlElement.addAttribute("id", String.valueOf(hour.getId()));
            xmlElement.addAttribute("start", hour.getStart());
            xmlElement.addAttribute("end", hour.getEnd());
            xmlElement.addAttribute("break", String.valueOf(hour.isBreak()));
            return xmlElement;
        } else {
            return null;
        }
    }

    private Hour saveHour(Hour hour) {
        if(hour!=null) {
            List<Hour> hours = this.sqLite.getHours("start_time='" + hour.getStart() + "'");
            if(hours.isEmpty()) {
                hour.setId(0);
            } else {
                hour.setId(hours.get(0).getId());
            }
            hour.setId(this.sqLite.insertOrUpdateHour(hour));
        }
        return hour;
    }

    private ToDo getToDoFromCSVObject(CSVObject csvObject) {
        if(csvObject!=null) {
            ToDo toDo = new ToDo();
            toDo.setId(csvObject.readIntegerValue("1"));
            toDo.setTitle(csvObject.readStringValue("2"));
            toDo.setCategory(csvObject.readStringValue("3"));
            toDo.setDescription(csvObject.readStringValue("4"));
            toDo.setMemoryDate(csvObject.readDateValue("5", MainActivity.globals.getUserSettings().getDateFormat()));
            toDo.setImportance(csvObject.readIntegerValue("6"));
            toDo.setSolved(csvObject.readBooleanValue("7"));
            return toDo;
        } else {
            return null;
        }
    }

    private CSVObject getCSVObjectFromToDo(ToDo toDo) {
        if(toDo!=null) {
            CSVObject csvObject = new CSVObject("|", 7);
            csvObject.writeValue("1", toDo.getId());
            csvObject.writeValue("2", toDo.getTitle());
            csvObject.writeValue("3", toDo.getCategory());
            csvObject.writeValue("4", toDo.getDescription());
            csvObject.writeValue("5", toDo.getMemoryDate(), MainActivity.globals.getUserSettings().getDateFormat());
            csvObject.writeValue("6", toDo.getImportance());
            csvObject.writeValue("7", toDo.isSolved());
            return csvObject;
        } else {
            return null;
        }
    }

    private ToDo getToDoFromXMLElement(XMLElement element) {
        if(element!=null) {
            ToDo toDo = new ToDo();
            toDo.setId(getIntegerFromMap(element.getAttributes(), "id"));
            toDo.setTitle(this.unescapeText(element.getAttributes().get("title")));
            toDo.setCategory(this.unescapeText(element.getAttributes().get("category")));
            toDo.setDescription(this.unescapeText(element.getContent()));
            try {
                toDo.setMemoryDate(ConvertHelper.convertStringToDate(element.getAttributes().get("memory_date"), this.context));
            } catch (Exception ex) {
                toDo.setMemoryDate(null);
            }
            toDo.setImportance(this.getIntegerFromMap(element.getAttributes(), "importance"));
            toDo.setSolved(this.getBooleanFromMap(element.getAttributes(), "solved"));
            return toDo;
        } else {
            return null;
        }
    }

    private XMLElement getXMLElementFromToDo(ToDo toDo) {
        if(toDo!=null) {
            XMLElement element = new XMLElement("ToDo");
            element.addAttribute("id", String.valueOf(toDo.getId()));
            element.addAttribute("title", this.escapeText(toDo.getTitle()));
            element.addAttribute("category", this.escapeText(toDo.getCategory()));
            element.setContent(this.escapeText(toDo.getDescription()));
            element.addAttribute("memory_date", ConvertHelper.convertDateToString(toDo.getMemoryDate(), this.context));
            element.addAttribute("importance", String.valueOf(toDo.getImportance()));
            element.addAttribute("solved", String.valueOf(toDo.isSolved()));
            return element;
        } else {
            return null;
        }
    }

    private ToDo saveToDo(ToDo toDo, String title) {
        if(toDo!=null) {
            toDo.setId(0);
            toDo.setId(this.sqLite.insertOrUpdateToDo(toDo, title));
        }
        return toDo;
    }

    private SchoolClass getSchoolClassFromCSVObject(CSVObject csvObject) {
        if(csvObject!=null) {
            SchoolClass schoolClass = new SchoolClass();
            schoolClass.setId(csvObject.readIntegerValue("1"));
            schoolClass.setTitle(csvObject.readStringValue("2"));
            schoolClass.setDescription(csvObject.readStringValue("3"));
            schoolClass.setNumberOfPupils(csvObject.readIntegerValue("4"));
            return schoolClass;
        } else {
            return null;
        }
    }

    private CSVObject getCSVObjectFromSchoolClass(SchoolClass schoolClass, String separator) {
        if(schoolClass!=null) {
            CSVObject csvObject = new CSVObject(separator, 4);
            csvObject.writeValue("1", schoolClass.getId());
            csvObject.writeValue("2", schoolClass.getTitle());
            csvObject.writeValue("3", schoolClass.getDescription());
            csvObject.writeValue("4", schoolClass.getNumberOfPupils());
            return csvObject;
        } else {
            return null;
        }
    }

    private SchoolClass getSchoolClassFromXMLElement(XMLElement element) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(this.getIntegerFromMap(element.getAttributes(), "id"));
        schoolClass.setTitle(this.unescapeText(element.getAttributes().get("title")));
        schoolClass.setDescription(this.unescapeText(element.getAttributes().get("description")));
        schoolClass.setNumberOfPupils(this.getIntegerFromMap(element.getAttributes(), "numberOfPupils"));

        if(!this.sqLite.entryExists("classes", schoolClass.getId())) {
            schoolClass.setId(0);
        }
        schoolClass.setId(sqLite.insertOrUpdateClass(schoolClass));
        return schoolClass;
    }

    private XMLElement getXMLElementFromSchoolClass(SchoolClass schoolClass) {
        if(schoolClass!=null) {
            XMLElement xmlElement = new XMLElement("SchoolClass");
            xmlElement.addAttribute("id", String.valueOf(schoolClass.getId()));
            xmlElement.addAttribute("title", this.escapeText(schoolClass.getTitle()));
            xmlElement.addAttribute("numberOfPupils", String.valueOf(schoolClass.getNumberOfPupils()));
            xmlElement.addAttribute("description", this.escapeText(schoolClass.getDescription()));
            return xmlElement;
        } else {
            return null;
        }
    }

    private SchoolClass saveSchoolClass(SchoolClass schoolClass) {
        if(schoolClass!=null) {
            List<SchoolClass> schoolClasses = this.sqLite.getClasses("title='" + schoolClass.getTitle() + "'");
            if(schoolClasses.isEmpty()) {
                schoolClass.setId(0);
            } else {
                schoolClass.setId(schoolClasses.get(0).getId());
            }
            schoolClass.setId(this.sqLite.insertOrUpdateClass(schoolClass));
        }
        return schoolClass;
    }

    private Year getYearFromCSVObject(CSVObject csvObject) {
        if(csvObject!=null) {
            Year year = new Year();
            year.setId(csvObject.readIntegerValue("1"));
            year.setTitle(csvObject.readStringValue("2"));
            year.setDescription(csvObject.readStringValue("3"));
            return year;
        } else {
            return null;
        }
    }

    private CSVObject getCSVObjectFromYear(Year year) {
        if(year!=null) {
            CSVObject csvObject = new CSVObject("|", 3);
            csvObject.writeValue("1", year.getId());
            csvObject.writeValue("2", year.getTitle());
            csvObject.writeValue("3", year.getDescription());
            return csvObject;
        } else {
            return null;
        }
    }

    private Year getYearFromXMLElement(XMLElement element) {
        Year year = new Year();
        year.setTitle(this.unescapeText(element.getAttributes().get("title")));
        year.setId(this.getIntegerFromMap(element.getAttributes(), "id"));
        year.setDescription(this.unescapeText(element.getAttributes().get("description")));

        if(!this.sqLite.entryExists("years", year.getId()))  {
            year.setId(0);
        }
        year.setId(sqLite.insertOrUpdateYear(year));
        return year;
    }

    private XMLElement getXMLElementFromYear(Year year) {
        if(year!=null) {
            return this.getDescriptionElement(year.getId(), year.getTitle(), year.getDescription());
        } else {
            return null;
        }
    }

    private XMLElement getDescriptionElement(long id, String title, String description) {
        XMLElement element = new XMLElement("Year");
        element.addAttribute("id", String.valueOf(id));
        element.addAttribute("title", this.escapeText(title));
        element.addAttribute("description", this.escapeText(description));
        return element;
    }

    private Year saveYear(Year year) {
        if(year!=null) {
            List<Year> years = this.sqLite.getYears("title='" + year.getTitle() + "'");
            if(years.isEmpty()) {
                year.setId(0);
            } else {
                year.setId(years.get(0).getId());
            }
            year.setId(this.sqLite.insertOrUpdateYear(year));
        }
        return year;
    }

    private Subject getSubjectFromCSVObject(CSVObject csvObject, String startTag, String endTag) {
        if(csvObject!=null) {
            Subject subject = new Subject();
            subject.setId(csvObject.readIntegerValue("1"));
            subject.setTitle(csvObject.readStringValue("2"));
            subject.setAlias(csvObject.readStringValue("3"));
            subject.setDescription(csvObject.readStringValue("4"));
            subject.setBackgroundColor(csvObject.readStringValue("5"));
            subject.setHoursInWeek(csvObject.readIntegerValue("6"));
            subject.setMainSubject(csvObject.readBooleanValue("7"));
            List<CSVObject> csvObjects = csvObject.readObjectsValue("8", "#", startTag, endTag);
            if(csvObjects!=null) {
                if(!csvObjects.isEmpty()) {
                    subject.setTeacher(this.getTeacherFromCSVObject(csvObjects.get(0)));
                }
            }
            return subject;
        } else {
            return null;
        }
    }

    private CSVObject getCSVObjectFromSubject(Subject subject, String separator, String startTag, String endTag) {
        if(subject!=null) {
            CSVObject csvObject = new CSVObject(separator, 7);
            csvObject.writeValue("1", subject.getId());
            csvObject.writeValue("2", subject.getTitle());
            csvObject.writeValue("3", subject.getAlias());
            csvObject.writeValue("4", subject.getDescription());
            csvObject.writeValue("5", subject.getBackgroundColor());
            csvObject.writeValue("6", subject.getHoursInWeek());
            csvObject.writeValue("7", subject.isMainSubject());
            csvObject.writeValue("8", this.getCSVObjectFromTeacher(subject.getTeacher(), "#"), startTag, endTag);
            return csvObject;
        } else {
            return null;
        }
    }

    private Subject getSubjectFromXMLElement(XMLElement element) {
        Subject subject = new Subject();
        subject.setId(this.getIntegerFromMap(element.getAttributes(), "id"));
        subject.setTitle(this.unescapeText(element.getAttributes().get("title")));
        subject.setAlias(this.unescapeText(element.getAttributes().get("alias")));
        subject.setDescription(this.unescapeText(element.getAttributes().get("description")));
        subject.setBackgroundColor(this.unescapeText(element.getAttributes().get("backgroundColor")));
        subject.setHoursInWeek(this.getIntegerFromMap(element.getAttributes(), "hoursInWeek"));
        subject.setMainSubject(this.getBooleanFromMap(element.getAttributes(), "mainSubject"));
        if(element.getSubElements()!=null) {
            if(!element.getSubElements().isEmpty()) {
                subject.setTeacher(this.getTeacherFromXMLElement(element.getSubElements().get(0)));
            }
        }

        if(!this.sqLite.entryExists("subjects", subject.getId())) {
            subject.setId(0);
        }
        subject.setId(this.sqLite.insertOrUpdateSubject(subject));
        return subject;
    }

    private XMLElement getXMLElementFromSubject(Subject subject) {
        if(subject!=null) {
            XMLElement xmlElement = new XMLElement("Subject");
            xmlElement.setParentElement("SchoolYear");
            xmlElement.addAttribute("id", String.valueOf(subject.getId()));
            xmlElement.addAttribute("title", this.escapeText(subject.getTitle()));
            xmlElement.addAttribute("alias", this.escapeText(subject.getAlias()));
            xmlElement.addAttribute("description", this.escapeText(subject.getDescription()));
            xmlElement.addAttribute("backgroundColor", this.escapeText(subject.getBackgroundColor()));
            xmlElement.addAttribute("hoursInWeek", String.valueOf(subject.getHoursInWeek()));
            xmlElement.addAttribute("mainSubject", String.valueOf(subject.isMainSubject()));
            XMLElement teacherElement = this.getXMLElementFromTeacher(subject.getTeacher());
            if(teacherElement!=null) {
                xmlElement.addSubElement(teacherElement);
            }
            return xmlElement;
        } else {
            return null;
        }
    }

    private Subject saveSubject(Subject subject) {
        if(subject!=null) {
            if(subject.getTeacher()!=null) {
                subject.setTeacher(this.saveTeacher(subject.getTeacher()));
            }
            List<Subject> subjects = this.sqLite.getSubjects("title='" + subject.getTitle() + "'");
            if(subjects.isEmpty()) {
                subject.setId(0);
            } else {
                subject.setId(subjects.get(0).getId());
            }
            subject.setId(this.sqLite.insertOrUpdateSubject(subject));
        }
        return subject;
    }

    private Teacher getTeacherFromCSVObject(CSVObject csvObject) {
        if(csvObject!=null) {
            Teacher teacher = new Teacher();
            teacher.setId(csvObject.readIntegerValue("1"));
            teacher.setFirstName(csvObject.readStringValue("2"));
            teacher.setLastName(csvObject.readStringValue("3"));
            teacher.setDescription(csvObject.readStringValue("4"));
            return teacher;
        } else {
            return null;
        }
    }

    private CSVObject getCSVObjectFromTeacher(Teacher teacher, String separator) {
        if(teacher!=null) {
            CSVObject csvObject = new CSVObject(separator, 4);
            csvObject.writeValue("1", teacher.getId());
            csvObject.writeValue("2", teacher.getFirstName());
            csvObject.writeValue("3", teacher.getLastName());
            csvObject.writeValue("4", teacher.getDescription());
            return csvObject;
        } else {
            return null;
        }
    }

    private Teacher getTeacherFromXMLElement(XMLElement element) {
        Teacher teacher = new Teacher();
        teacher.setId(this.getIntegerFromMap(element.getAttributes(), "id"));
        teacher.setFirstName(this.unescapeText(element.getAttributes().get("firstName")));
        teacher.setLastName(this.unescapeText(element.getAttributes().get("lastName")));
        teacher.setDescription(this.unescapeText(element.getAttributes().get("description")));

        if(!this.sqLite.entryExists("teachers", teacher.getId())) {
            teacher.setId(0);
        }
        teacher.setId(sqLite.insertOrUpdateTeacher(teacher));
        return teacher;
    }

    private XMLElement getXMLElementFromTeacher(Teacher teacher) {
        if(teacher!=null) {
            XMLElement xmlElement = new XMLElement("Teacher");
            xmlElement.addAttribute("id", String.valueOf(teacher.getId()));
            xmlElement.addAttribute("firstName", this.escapeText(teacher.getFirstName()));
            xmlElement.addAttribute("lastName", this.escapeText(teacher.getLastName()));
            xmlElement.addAttribute("description", this.escapeText(teacher.getDescription()));
            return xmlElement;
        } else {
            return null;
        }
    }

    private Subject getSubjectFromBridge(CSVBridge reader, int i) {
        List<CSVObject> csvObjects = reader.readObjectsValue(i, "subject", "|", "(", ")");
        if(csvObjects!=null) {
            if(!csvObjects.isEmpty()) {
                return this.getSubjectFromCSVObject(csvObjects.get(0), "[", "]");
            }
        }
        return null;
    }

    private BaseDescriptionObject getBaseDescriptionObject(XMLElement element) {
        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        baseDescriptionObject.setId(this.getIntegerFromMap(element.getAttributes(), "id"));
        baseDescriptionObject.setTitle(this.unescapeText(element.getAttributes().get("title")));
        baseDescriptionObject.setDescription(this.unescapeText(element.getAttributes().get("description")));
        return baseDescriptionObject;
    }

    private Teacher saveTeacher(Teacher teacher) {
        if(teacher!=null) {
            List<Teacher> teachers = this.sqLite.getTeachers("lastName='" + teacher.getLastName() + "'");
            if(teachers.isEmpty()) {
                teacher.setId(0);
            } else {
                teacher.setId(teachers.get(0).getId());
            }
            teacher.setId(this.sqLite.insertOrUpdateTeacher(teacher));
        }
        return teacher;
    }

    private String escapeText(String text) {
        if(text!=null) {
            return text.replace("\n", "[n]").replace(";", "[,]");
        } else {
            return "";
        }
    }

    private String unescapeText(String text) {
        if(text!=null) {
            return text.replace("[n]", "\n").replace("[,]", ";");
        } else {
            return "";
        }
    }
}
