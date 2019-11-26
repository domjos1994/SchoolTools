/*
 * MamaPlanner
 * Copyright (C) 2019 Domjos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.customwidgets.widgets.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.domjos.customwidgets.R;
import de.domjos.customwidgets.utils.Helper;
import de.domjos.customwidgets.utils.WidgetUtils;

public class WidgetCalendar extends LinearLayout {
    private Context context;
    private ImageButton cmdCalSkipNext, cmdCalNext, cmdCalPrevious, cmdCalSkipPrevious;
    private TextView lblCalDate;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateFormatWithDay;
    private TableLayout tableLayout;
    private LinearLayout llDays;
    private List<Map.Entry<String, Event>> events;
    private Map<String, Integer> groups;
    private ClickListener clickListener;
    private ClickListener hourHeaderClickListener;
    private ClickListener longClickListener;
    private ClickListener hourGroupListener;
    private Event currentEvent;
    private HorizontalScrollView horizontalScrollView;
    private Drawable toolBarBackground;
    private Drawable monthViewBackground;
    private Drawable dayViewBackground;
    private Drawable focusBackground;
    private float hourLabelWidth;

    public WidgetCalendar(Context context) {
        super(context);

        this.toolBarBackground = null;
        this.monthViewBackground = null;
        this.dayViewBackground = null;
        this.focusBackground = null;
        this.hourLabelWidth = 200f;

        this.initDefaults(context);
        this.initControls();
        this.initActions();
    }

    public WidgetCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WidgetCalendar, 0, 0);
        this.toolBarBackground = a.getDrawable(R.styleable.WidgetCalendar_toolBarBackground);
        this.monthViewBackground = a.getDrawable(R.styleable.WidgetCalendar_monthViewBackground);
        this.dayViewBackground = a.getDrawable(R.styleable.WidgetCalendar_dayViewBackground);
        this.focusBackground = a.getDrawable(R.styleable.WidgetCalendar_focusBackground);
        this.hourLabelWidth = a.getDimension(R.styleable.WidgetCalendar_groupLabelWidth, 200f);

        this.initDefaults(context);
        this.initControls();
        this.initActions();
    }

    public void setCurrentDate(Date dt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        this.lblCalDate.setText(this.dateFormat.format(dt));
        this.reload();
    }

    public String getCurrentMonth() {
        return this.lblCalDate.getText().toString();
    }

    public void addEvent(Event event) {
        if(event.getCalendar() != null) {
            this.events.add(new AbstractMap.SimpleEntry<>(this.dateFormatWithDay.format(event.getCalendar().getTime()), event));
        }
    }

    public void addGroup(String name, int color) {
        this.groups.put(name, color);
    }

    public Map<String, Integer> getGroups() {
        return this.groups;
    }

    public Event getCurrentEvent() {
        return this.currentEvent;
    }

    @SuppressWarnings("unused")
    public List<Map.Entry<String, Event>> getEvents() {
        return this.events;
    }

    public void setOnClick(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnHourHeaderClick(ClickListener clickListener) {
        this.hourHeaderClickListener = clickListener;
    }

    public void setOnHourGroupClick(ClickListener clickListener) {
        this.hourGroupListener = clickListener;
    }

    public void setOnLongClick(ClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void showMonth(boolean month) {
        this.tableLayout.setVisibility(month ? VISIBLE : GONE);
    }

    public void showDay(boolean day) {
        if(day) {
            this.tableLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.llDays.setVisibility(VISIBLE);
        } else {
            this.tableLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            this.llDays.setVisibility(GONE);
        }
    }

    public void reload() {
        this.reloadCalendar();
    }

    private void initActions() {

        this.cmdCalNext.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                Helper.printException(this.context, ex);
            }
        });

        this.cmdCalSkipNext.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                Helper.printException(this.context, ex);
            }
        });

        this.cmdCalPrevious.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                Helper.printException(this.context, ex);
            }
        });

        this.cmdCalSkipPrevious.setOnClickListener(view -> {
            try {
                Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
                if(date!=null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                    this.reloadCalendar();
                }
            } catch (Exception ex) {
                Helper.printException(this.context, ex);
            }
        });

        this.lblCalDate.setOnLongClickListener(view -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                this.lblCalDate.setText(this.dateFormat.format(calendar.getTime()));
                this.reloadCalendar();
            } catch (Exception ex) {
                Helper.printException(this.context, ex);
            }
            return false;
        });
    }

    private void initDefaults(Context context) {
        this.context = context;
        this.setOrientation(VERTICAL);

        this.events = new LinkedList<>();
        this.groups = new LinkedHashMap<>();
    }

    private void initControls() {
        this.dateFormat = new SimpleDateFormat("MM.yyyy", Helper.getLocale());
        this.dateFormatWithDay = new SimpleDateFormat("dd.MM.yyyy", Helper.getLocale());

        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setBackground(this.toolBarBackground);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setWeightSum(8);
        this.addView(linearLayout);

        this.cmdCalSkipPrevious = new ImageButton(this.context);
        this.cmdCalSkipPrevious.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_skip_previous));
        this.cmdCalSkipPrevious.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        this.cmdCalSkipPrevious.setBackground(null);
        linearLayout.addView(this.cmdCalSkipPrevious);

        this.cmdCalPrevious = new ImageButton(this.context);
        this.cmdCalPrevious.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_previous));
        this.cmdCalPrevious.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        this.cmdCalPrevious.setBackground(null);
        linearLayout.addView(this.cmdCalPrevious);

        this.lblCalDate = new TextView(this.context);
        this.lblCalDate.setText(this.dateFormat.format(new Date()));
        LayoutParams layoutParams = this.getLayoutParamsByWeight(4, linearLayout);
        layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
        this.lblCalDate.setLayoutParams(layoutParams);
        this.lblCalDate.setGravity(Gravity.CENTER);
        this.lblCalDate.setTextSize(24);
        this.lblCalDate.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(this.lblCalDate);

        this.cmdCalNext = new ImageButton(this.context);
        this.cmdCalNext.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_next));
        this.cmdCalNext.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        this.cmdCalNext.setBackground(null);
        linearLayout.addView(this.cmdCalNext);

        this.cmdCalSkipNext = new ImageButton(this.context);
        this.cmdCalSkipNext.setImageDrawable(WidgetUtils.getDrawable(this.context, R.drawable.ic_skip_next));
        this.cmdCalSkipNext.setLayoutParams(this.getLayoutParamsByWeight(1, linearLayout));
        this.cmdCalSkipNext.setBackground(null);
        linearLayout.addView(this.cmdCalSkipNext);

        TextView textView = new TextView(this.context);
        LinearLayout.LayoutParams tmp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        textView.setLayoutParams(tmp);
        textView.setBackgroundColor(WidgetUtils.getColor(this.context, android.R.color.background_dark));
        this.addView(textView);

        this.tableLayout = new TableLayout(this.context);
        this.tableLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.tableLayout.setWeightSum(7);
        this.tableLayout.setBackground(this.monthViewBackground);
        this.addView(this.tableLayout);

        textView = new TextView(this.context);
        textView.setLayoutParams(tmp);
        textView.setBackgroundColor(WidgetUtils.getColor(this.context, android.R.color.background_dark));
        this.addView(textView);

        ScrollView scrollView = new ScrollView(this.context);
        scrollView.setBackground(this.dayViewBackground);
        scrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.setHorizontalScrollBarEnabled(true);
        scrollView.setVerticalScrollBarEnabled(true);
        scrollView.setScrollBarStyle(SCROLLBARS_OUTSIDE_INSET);
        scrollView.setFillViewport(true);
        this.addView(scrollView);

        this.horizontalScrollView = new HorizontalScrollView(this.context);
        this.horizontalScrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.horizontalScrollView.setHorizontalScrollBarEnabled(true);
        this.horizontalScrollView.setVerticalScrollBarEnabled(true);
        scrollView.addView(this.horizontalScrollView);

        this.llDays = new LinearLayout(this.context);
        this.llDays.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.llDays.setOrientation(VERTICAL);
        this.horizontalScrollView.addView(this.llDays);

        this.reloadCalendar();
    }

    private void addDaysOfWeek() {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Helper.getLocale());
        for(String dayOfWeek : dateFormatSymbols.getWeekdays()) {
            if(dayOfWeek.length()>=3) {
                ((TableRow) this.tableLayout.getChildAt(0)).addView(this.addTextView(dayOfWeek.substring(0, 3)));
            }
        }
    }

    private void addRows() {
        this.tableLayout.removeAllViews();
        Calendar calendar = this.getDefaultCalendar();
        int max = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
        if(Helper.getLocale().getLanguage().equals(Locale.GERMAN.getLanguage())) {
            max++;
        }
        for(int i = 0; i <= max; i++) {
            TableRow tblRow = new TableRow(this.context);
            tblRow.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            this.tableLayout.addView(tblRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void addHours() {
        this.llDays.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(this.addTextViewWithWidth("h", this.hourLabelWidth));
        for(int i = 1; i<=24; i++) {
            final int hour = i;
            TextView txt = this.addTextViewWithWidth(String.valueOf(i), 100);
            txt.setOnClickListener(view -> {
                for(int j = 0; j<=linearLayout.getChildCount() - 1; j++) {
                    linearLayout.getChildAt(j).setBackgroundColor(WidgetUtils.getColor(this.context, android.R.color.transparent));
                }
                txt.setBackground(this.focusBackground);
            });
            txt.setOnLongClickListener(view -> {
                for(int j = 0; j<=linearLayout.getChildCount() - 1; j++) {
                    linearLayout.getChildAt(j).setBackgroundColor(WidgetUtils.getColor(this.context, android.R.color.transparent));
                }
                txt.setBackground(this.focusBackground);
                if(this.hourHeaderClickListener!=null) {
                    this.currentEvent.getCalendar().set(Calendar.HOUR_OF_DAY, hour);
                    this.hourHeaderClickListener.onClick(this.currentEvent);
                }
                return false;
            });
            linearLayout.addView(txt);
        }
        this.llDays.addView(linearLayout);

        for(Map.Entry<String, Integer> group : this.groups.entrySet()) {
            List<Event> dayEvents = new LinkedList<>();
            if(this.currentEvent!=null) {
                Calendar calendar = this.currentEvent.getCalendar();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                for(Map.Entry<String, Event> entry : this.events) {
                    if(entry.getValue().getColor()==group.getValue()) {
                        int year2 = entry.getValue().getCalendar().get(Calendar.YEAR);
                        int month2 = entry.getValue().getCalendar().get(Calendar.MONTH);
                        int day2 = entry.getValue().getCalendar().get(Calendar.DAY_OF_MONTH);

                        if(year==year2 && month==month2 && day==day2) {
                            dayEvents.add(entry.getValue());
                        }
                    }
                }
            }

            List<Map.Entry<String, Integer>> entries = new LinkedList<>();
            for(int i = 1; i<=24; i++) {
                entries.add(new AbstractMap.SimpleEntry<>("", WidgetUtils.getColor(this.context, android.R.color.transparent)));
            }
            for(Event current : dayEvents) {
                if(current.getEnd()==null) {
                    entries.set(0, new AbstractMap.SimpleEntry<>(current.getName(), current.getColor()));
                    for(int i = 1; i<=23; i++) {
                        entries.get(i).setValue(current.getColor());
                    }
                } else {
                    int startHour = current.getCalendar().get(Calendar.HOUR_OF_DAY);
                    int endHour = current.getEnd().get(Calendar.HOUR_OF_DAY);

                    entries.set(startHour - 1, new AbstractMap.SimpleEntry<>(current.getName(), current.getColor()));

                    for(int i = startHour; i<=endHour - 1; i++) {
                        entries.get(i).setValue(current.getColor());
                    }
                }
            }

            LinearLayout groupLayout = new LinearLayout(this.context);
            groupLayout.setOrientation(HORIZONTAL);
            groupLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            groupLayout.addView(this.addTextViewWithWidth(group.getKey(), this.hourLabelWidth, group.getValue()));

            String key = "";
            int color = -1;
            for(int i = 1; i<=24; i++) {
                final int hour = i;
                if(key.equals("") || key.equals(entries.get(i - 1).getKey())) {
                    key = entries.get(i - 1).getKey();
                    color = entries.get(i - 1).getValue();
                    if(key.equals("")) {
                        TextView textView = this.addTextViewWithWidth(entries.get(i - 1).getKey(), 100, entries.get(i - 1).getValue());
                        textView.setOnClickListener(view -> {
                            for(int j = 0; j<=groupLayout.getChildCount()-1; j++) {
                                TextView txt = (TextView) groupLayout.getChildAt(j);
                                if(txt.getBackground()!=null) {
                                    if (((ColorDrawable) txt.getBackground()).getColor() != group.getValue()) {
                                        txt.setBackground(null);
                                    }
                                }
                            }
                            textView.setBackground(this.focusBackground);
                        });
                        textView.setOnLongClickListener(view -> {
                            if(this.hourGroupListener!=null) {
                                this.currentEvent.getCalendar().set(Calendar.HOUR_OF_DAY, hour);
                                this.currentEvent.setColor(group.getValue());
                                this.hourGroupListener.onClick(this.currentEvent);
                            }
                            return false;
                        });
                        groupLayout.addView(textView);
                    }
                } else {
                    int length = 1;
                    for(int j = i; j<=24; j++) {
                        if(color == entries.get(j - 1).getValue()) {
                            length++;
                        } else {
                            break;
                        }
                    }
                    groupLayout.addView(this.addTextViewWithWidth(key, length * 100, entries.get(i - 1).getValue()));
                    i += (length - 2);
                    key = entries.get(i - 1).getKey();
                }
            }
            this.llDays.addView(groupLayout);
        }
    }

    private void reloadCalendar() {
        try {
            this.addRows();
            this.addDaysOfWeek();
            this.addHours();
            this.scrollToCurrentTime();

            Calendar calendar = this.getDefaultCalendar();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for(int i = 1; i<=max; i++) {
                calendar.set(Calendar.DAY_OF_MONTH, i);

                int row = calendar.get(Calendar.WEEK_OF_MONTH);
                if(Helper.getLocale().getLanguage().equals(Locale.GERMAN.getLanguage())) {
                    row++;
                }
                ((TableRow) this.tableLayout.getChildAt(row)).addView(this.addDay(i));
            }

            int weeks = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
            if(Helper.getLocale().getLanguage().equals(Locale.GERMAN.getLanguage())) {
                weeks++;
            }

            int children = ((TableRow) this.tableLayout.getChildAt(weeks)).getChildCount();
            for(int i = children; i<7; i++) {
                ((TableRow) this.tableLayout.getChildAt(weeks)).addView(this.addTextView(""));
            }

            if(((TableRow) this.tableLayout.getChildAt(1)).getChildCount() == 0) {
                this.tableLayout.removeView(this.tableLayout.getChildAt(1));
            }

            int position = 0;
            for(int i = day-1; i>0; i--) {
                ((TableRow) this.tableLayout.getChildAt(1)).addView(this.addTextView(""), position);
            }

            this.tableLayout.invalidate();
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
    }


    private TextView addTextViewWithWidth(String text, float width, int color) {
        TextView lbl = new TextView(this.context);
        lbl.setText(text);
        lbl.setGravity(Gravity.CENTER);
        try {
            lbl.setBackgroundColor(WidgetUtils.getColor(this.context, color));
        } catch (Exception ex) {
            lbl.setBackgroundColor(color);
        }
        lbl.setTextSize(16);
        lbl.setTypeface(null, Typeface.BOLD);
        lbl.setLayoutParams(new TableRow.LayoutParams((int) width, TableRow.LayoutParams.WRAP_CONTENT));
        return lbl;
    }

    private TextView addTextViewWithWidth(String text, float width) {
        return this.addTextViewWithWidth(text, width, android.R.color.transparent);
    }

    private TextView addTextView(String text) {
        TextView lbl = new TextView(this.context);
        lbl.setText(text);
        lbl.setGravity(Gravity.CENTER);
        lbl.setTextSize(16);
        lbl.setTypeface(null, Typeface.BOLD);
        lbl.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        return lbl;
    }

    private LinearLayout addDay(int currentDayOfMonth) {
        Calendar calendar = this.getDefaultCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
        String strDate = this.dateFormatWithDay.format(calendar.getTime());
        List<Event> eventsOnDay = new LinkedList<>();
        for(Map.Entry<String, Event> entry : this.events) {
            if(entry.getKey().equals(strDate)) {
                eventsOnDay.add(entry.getValue());
            }
        }

        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
        linearLayout.setPadding(5, 5, 5, 5);
        linearLayout.addView(this.addTextViewToDay(String.valueOf(currentDayOfMonth), android.R.color.transparent, -1));
        Calendar today = Calendar.getInstance();
        if(
            calendar.get(Calendar.YEAR)==today.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH)==today.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH)==currentDayOfMonth) {

            linearLayout.setBackground(this.focusBackground);
            Event event = new Event() {
                @Override
                public int getIcon() {
                    return -1;
                }
            };
            event.setCalendar(calendar.getTime());
            this.currentEvent = event;
        }

        linearLayout.setOnClickListener(view -> {
            for(int i = 0; i<=this.tableLayout.getChildCount() - 1; i++) {
                for(int j = 0; j<=((TableRow) this.tableLayout.getChildAt(i)).getChildCount() - 1; j++) {
                    View v = ((TableRow) this.tableLayout.getChildAt(i)).getChildAt(j);
                    if(v instanceof LinearLayout) {
                        v.setBackground(null);
                    }
                }
            }

            linearLayout.setBackground(this.focusBackground);

            Event event = new Event() {
                @Override
                public int getIcon() {
                    return -1;
                }
            };
            event.setCalendar(calendar.getTime());
            this.currentEvent = event;
            this.addHours();
            this.scrollToCurrentTime();
        });

        for(Event event : eventsOnDay) {
            LinearLayout layout = this.addTextViewToDay(event.getName(), event.getColor(), event.getIcon());
            ((LinearLayout.LayoutParams) layout.getLayoutParams()).setMargins(0, 0, 0, 2);
            layout.setOnClickListener(v -> {
                this.currentEvent = event;
                if(this.clickListener != null) {
                    this.clickListener.onClick(event);
                }
            });
            layout.setOnLongClickListener(v -> {
                this.currentEvent = event;
                if(this.longClickListener != null) {
                    this.longClickListener.onClick(this.currentEvent);
                }
                return false;
            });
            linearLayout.addView(layout);
        }
        return linearLayout;
    }

    private LinearLayout addTextViewToDay(String content, int color, int drawable) {
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(color != -1) {
            try {
                linearLayout.setBackgroundColor(WidgetUtils.getColor(this.context, color));
            } catch (Exception ex) {
                try {
                    linearLayout.setBackgroundColor(color);
                } catch (Exception ignored) {}
            }
        }
        linearLayout.setPadding(-3, -3, -3, -3);
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setWeightSum(10);

        if(drawable!=-1) {
            ImageView imageView = new ImageView(this.context);
            imageView.setImageDrawable(WidgetUtils.getDrawable(this.context, drawable));
            imageView.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
            ((LayoutParams) imageView.getLayoutParams()).weight = 3;
            linearLayout.addView(imageView);
        }

        TextView textView = new TextView(this.context);
        if(color==android.R.color.transparent || color == -1) {
            textView.setGravity(Gravity.CENTER);
        } else {
            textView.setBackground(WidgetUtils.getDrawable(this.context, R.drawable.textview_rounded));
        }
        textView.setText(content);
        textView.setPadding(7, 7, 7, 7);
        textView.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        ((LayoutParams) textView.getLayoutParams()).weight = drawable == -1 ? 10 : 7;
        linearLayout.addView(textView);
        return linearLayout;
    }

    private LayoutParams getLayoutParamsByWeight(float weight, LinearLayout linearLayout) {
        if(linearLayout.getOrientation()==HORIZONTAL) {
            return new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, weight);
        } else {
            return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0, weight);
        }
    }

    private Calendar getDefaultCalendar() {
        Calendar calendar = Calendar.getInstance(Helper.getLocale());
        try {
            Date date = this.dateFormat.parse(this.lblCalDate.getText().toString());
            if(date != null) {
                calendar.setTime(date);
            }
        } catch (Exception ex) {
            Helper.printException(this.context, ex);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        return calendar;
    }

    private void scrollToCurrentTime() {
        if(this.currentEvent!=null) {
            Date dt = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);
            int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH);
            int currentYear = this.currentEvent.getCalendar().get(Calendar.YEAR), currentMonth = this.currentEvent.getCalendar().get(Calendar.MONTH);
            int currentDay = this.currentEvent.getCalendar().get(Calendar.DAY_OF_MONTH);

            if(year==currentYear && month==currentMonth && day==currentDay) {
                int x = 150 + (100 * calendar.get(Calendar.HOUR_OF_DAY));

                this.horizontalScrollView.scrollTo(x, 0);
            }
        } else {
            Date dt = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);

            int x = 150 + (100 * calendar.get(Calendar.HOUR_OF_DAY));

            this.horizontalScrollView.scrollTo(x, 0);
        }
    }

    @FunctionalInterface
    public interface ClickListener {
        void onClick(Event event);
    }
}
