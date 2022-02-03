/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.MarkListAdapter;
import de.domjos.schooltoolslib.exceptions.MarkListException;
import de.domjos.schooltoolslib.marklist.de.GermanExponentialList;
import de.domjos.schooltoolslib.marklist.de.GermanIHKList;
import de.domjos.schooltoolslib.marklist.de.GermanLinearList;
import de.domjos.schooltoolslib.marklist.de.GermanListWithCrease;
import de.domjos.schooltoolslib.model.marklist.MarkList;
import de.domjos.schooltoolslib.model.marklist.MarkListInterface;
import de.domjos.schooltoolslib.model.marklist.MarkListWithMarkMode;
import de.domjos.schooltools.helper.Helper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.schooltools.settings.MarkListSettings;

/**
 * Activity For the MarkList-Screen
 * @author Dominic Joas
 * @version 1.0
 */
public final class MarkListActivity extends AbstractActivity {
    // controls of layout
    private TableLayout grdMarkListSimple, grdMarkListExpert, grdMarkListWithCrease;

    private ScrollView grdControls;

    private ListView lvMarkList;
    private Spinner spMarkListType;
    private TextView lblMarkListState;
    private String detailedErrorMessage = "";

    private ImageView ivMarkListState;

    private EditText txtMarkListMaxPoints;
    private SearchView cmdSearch;

    private RadioButton rbMarkListSimpleQuarterMarks, rbMarkListSimpleTenthMarks;
    private CheckBox chkMarkListSimpleHalfPoints, chkMarkListSimpleDictatMode;

    private Spinner spMarkListExpertView, spMarkListExpertMarkMode;

    private EditText txtMarkListWithCreaseCustomPoints, txtMarkListWithCreaseCustomMark;
    private EditText txtMarkListWithCreaseBestMarkAt, txtMarkListWithCreaseWorstMarkTo;

    private FloatingActionButton cmdMarkListOpenSettings;
    private MarkListAdapter markListAdapter;

    private Validator baseValidator, withCreaseValidator;

    private float scale;

    private MarkListSettings curSettings;

    public MarkListActivity() {
        super(R.layout.marklist_activity);
    }

    @Override
    protected void initActions() {
        Helper.setBackgroundToActivity(this);
        this.setValues("");
        this.openMarkList(this.getIntent().getIntExtra("id", 0));
        Helper.closeSoftKeyboard(MarkListActivity.this);

        this.lvMarkList.setOnItemClickListener((parent, view, position, id) -> {
            Map.Entry<Double, Double> entry = markListAdapter.getItem(position);

            if(entry!=null) {
                String text;
                if(curSettings.getViewMode()==0 || curSettings.getViewMode()==1) {
                    int percentage = (int) (entry.getValue() / (curSettings.getMaxPoints() / 100.0f));
                    if(curSettings.isDictatMode()) {
                        text = String.format("%s: %s%n%s: %s (%s%%)", getString(R.string.marklist_mark), entry.getKey(), getString(R.string.marklist_failures), entry.getValue(), percentage);
                    } else {
                        text = String.format("%s: %s%n%s: %s (%s%%)", getString(R.string.marklist_mark), entry.getKey(), getString(R.string.marklist_points), entry.getValue(), percentage);
                    }
                } else {
                    int percentage = (int) (entry.getKey() / (curSettings.getMaxPoints() / 100.0f));
                    if(curSettings.isDictatMode()) {
                        text = String.format("%s: %s%n%s: %s (%s%%)", getString(R.string.marklist_mark), entry.getValue(), getString(R.string.marklist_failures), entry.getKey(), percentage);
                    } else {
                        text = String.format("%s: %s%n%s: %s (%s%%)", getString(R.string.marklist_mark), entry.getValue(), getString(R.string.marklist_points), entry.getKey(), percentage);
                    }
                }

                MessageHelper.printMessage(text, R.mipmap.ic_launcher_round, MarkListActivity.this);
            }
        });

        this.cmdMarkListOpenSettings.setOnClickListener(v -> {
            int list = spMarkListType.getSelectedItemPosition();

            if(grdMarkListSimple.getVisibility()==View.GONE) {
                grdMarkListSimple.setVisibility(View.VISIBLE);
                grdControls.getLayoutParams().height = (int) (150 * scale + 0.5f);
            } else {
                grdMarkListSimple.setVisibility(View.GONE);
                grdControls.getLayoutParams().height = Toolbar.LayoutParams.WRAP_CONTENT;
            }

            if(list==1 || list==2) {
                grdMarkListWithCrease.setVisibility(grdMarkListSimple.getVisibility());
                txtMarkListWithCreaseCustomMark.setEnabled(list!=2);
                txtMarkListWithCreaseCustomPoints.setEnabled(list!=2);
                if(list==2) {
                    txtMarkListWithCreaseCustomMark.setText("");
                    txtMarkListWithCreaseCustomPoints.setText("");
                } else {
                    //txtMarkListWithCreaseCustomMark.setText(String.valueOf(3.5));
                    if(!txtMarkListMaxPoints.getText().toString().isEmpty()) {
                        txtMarkListWithCreaseCustomPoints.setText(String.valueOf(Integer.parseInt(txtMarkListMaxPoints.getText().toString())/2));
                    } else {
                        txtMarkListWithCreaseCustomPoints.setText(String.valueOf(10.0));
                    }
                }
            }
            if(MainActivity.globals.getUserSettings().isExpertMode()) {
                grdMarkListExpert.setVisibility(grdMarkListSimple.getVisibility());
            }
        });

        this.cmdSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                findItem(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        this.cmdSearch.setOnCloseListener(() -> {
            markListAdapter.findItem(-1);
            calculateMarkList();
            return false;
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) { calculateMarkList();}
        };

        this.txtMarkListMaxPoints.addTextChangedListener(textWatcher);

        this.rbMarkListSimpleTenthMarks.setOnCheckedChangeListener((buttonView, isChecked) -> calculateMarkList());

        this.rbMarkListSimpleQuarterMarks.setOnCheckedChangeListener((buttonView, isChecked) -> calculateMarkList());

        this.chkMarkListSimpleHalfPoints.setOnCheckedChangeListener((buttonView, isChecked) -> calculateMarkList());

        this.chkMarkListSimpleDictatMode.setOnCheckedChangeListener((buttonView, isChecked) -> calculateMarkList());

        this.spMarkListType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(grdMarkListSimple.getVisibility()==View.VISIBLE) {
                    if(grdMarkListWithCrease.getVisibility()==View.GONE) {
                        grdMarkListWithCrease.setVisibility(View.VISIBLE);
                    } else {
                        grdMarkListWithCrease.setVisibility(View.GONE);
                    }
                }

                if(position==1 || position==2) {
                    grdMarkListWithCrease.setVisibility(grdMarkListSimple.getVisibility());
                    txtMarkListWithCreaseCustomMark.setEnabled(position!=2);
                    txtMarkListWithCreaseCustomPoints.setEnabled(position!=2);
                    if(position==2) {
                        txtMarkListWithCreaseCustomMark.setText("");
                        txtMarkListWithCreaseCustomPoints.setText("");
                    } else {
                        txtMarkListWithCreaseCustomMark.setText(String.valueOf(3.5));
                        if(!txtMarkListMaxPoints.getText().toString().isEmpty()) {
                            txtMarkListWithCreaseCustomPoints.setText(String.valueOf(Integer.parseInt(txtMarkListMaxPoints.getText().toString())/2));
                        } else {
                            txtMarkListWithCreaseCustomPoints.setText(String.valueOf(10.0));
                        }
                    }
                }

                calculateMarkList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spMarkListExpertMarkMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateMarkList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spMarkListExpertView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateMarkList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.txtMarkListWithCreaseCustomMark.addTextChangedListener(textWatcher);
        this.txtMarkListWithCreaseCustomPoints.addTextChangedListener(textWatcher);
        this.txtMarkListWithCreaseBestMarkAt.addTextChangedListener(textWatcher);
        this.txtMarkListWithCreaseWorstMarkTo.addTextChangedListener(textWatcher);

        this.lblMarkListState.setOnClickListener(view -> {
            if(!detailedErrorMessage.equals("")) {
                MessageHelper.printMessage(detailedErrorMessage, R.mipmap.ic_launcher_round, MarkListActivity.this);
            }
        });

        this.ivMarkListState.setOnClickListener(view -> {
            if(!detailedErrorMessage.equals("")) {
                MessageHelper.printMessage(detailedErrorMessage, R.mipmap.ic_launcher_round, MarkListActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 99) {
                this.spMarkListType.setSelection(data.getIntExtra("type", 1));
                this.txtMarkListMaxPoints.setText(String.valueOf(data.getIntExtra("maxPoints", 20)));
                this.txtMarkListWithCreaseBestMarkAt.setText(String.valueOf(data.getDoubleExtra("bestMarkAt", 20.0)));
                this.txtMarkListWithCreaseWorstMarkTo.setText(String.valueOf(data.getDoubleExtra("worstMarkTo", 0.0)));
                this.txtMarkListWithCreaseCustomMark.setText(String.valueOf(data.getDoubleExtra("customMark", 3.5)));
                this.txtMarkListWithCreaseCustomPoints.setText(String.valueOf(data.getDoubleExtra("customPoints", 10.0)));
                this.calculateMarkList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menMarkListExtended:
                try {
                    Intent extIntent = new Intent(this.getApplicationContext(), MarkListExtendedActivity.class);
                    extIntent.putExtra("maxPoints", Integer.parseInt(this.txtMarkListMaxPoints.getText().toString()));
                    extIntent.putExtra("type", this.spMarkListType.getSelectedItemPosition());
                    if(this.txtMarkListWithCreaseBestMarkAt.getText()!=null) {
                        extIntent.putExtra("bestMarkAt", Double.parseDouble(this.txtMarkListWithCreaseBestMarkAt.getText().toString()));
                    }
                    if(this.txtMarkListWithCreaseWorstMarkTo.getText()!=null) {
                        extIntent.putExtra("worstMarkTo", Double.parseDouble(this.txtMarkListWithCreaseWorstMarkTo.getText().toString()));
                    }
                    if(this.txtMarkListWithCreaseCustomMark.getText()!=null) {
                        if(!this.txtMarkListWithCreaseCustomMark.getText().toString().isEmpty()) {
                            extIntent.putExtra("customMark", Double.parseDouble(this.txtMarkListWithCreaseCustomMark.getText().toString()));
                        }
                    }
                    if(this.txtMarkListWithCreaseCustomPoints.getText()!=null) {
                        if(!this.txtMarkListWithCreaseCustomPoints.getText().toString().isEmpty()) {
                            extIntent.putExtra("customPoints", Double.parseDouble(this.txtMarkListWithCreaseCustomPoints.getText().toString()));
                        }
                    }
                    if(this.spMarkListType.getSelectedItem()!=null) {
                        extIntent.putExtra("markMode", this.spMarkListType.getSelectedItemPosition());
                    }
                    startActivityForResult(extIntent, 99);
                } catch (IllegalStateException ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListActivity.this);
                }
                break;
            case R.id.menMarkListSaveAsDefault:
                this.curSettings = this.getValues("");
                MainActivity.globals.getSqLite().insertOrUpdateMarkList("", this.curSettings);
                MessageHelper.printMessage(this.getString(R.string.message_marklist_settings_saved), R.mipmap.ic_launcher_round, MarkListActivity.this);
                break;
            case R.id.menMarkListResetToDefault:
                this.setValues("");
                break;
            case R.id.menMarkListSaveMarkList:
                try {
                    if(baseValidator.getState()) {
                        if(spMarkListType.getSelectedItemPosition()==1) {
                            if(!withCreaseValidator.getState()) {
                                return false;
                            }
                        }
                        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MarkListActivity.this, R.style.MyDialogStyle));
                        builder.setTitle(this.getString(R.string.marklist_settings_save_title));
                        final EditText input = new EditText(MarkListActivity.this);
                        input.setHint(this.getString(R.string.sys_title));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        builder.setView(input);
                        builder.setPositiveButton(this.getString(R.string.sys_save), (dialog, which) -> {
                            if(input.getText()!=null) {
                                if(!input.getText().toString().isEmpty()) {
                                    curSettings = getValues(input.getText().toString());
                                    MainActivity.globals.getSqLite().insertOrUpdateMarkList(input.getText().toString(), curSettings);
                                    MessageHelper.printMessage(getString(R.string.message_marklist_settings_saved), R.mipmap.ic_launcher_round, MarkListActivity.this);
                                }
                            }
                        });
                        builder.show();
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListActivity.this);
                }
                break;
            case R.id.menMarkListOpenMarkList:
                try {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MarkListActivity.this, R.style.MyDialogStyle));
                    builder.setTitle(this.getString(R.string.marklist_settings_open_title));
                    final Spinner spinner = new Spinner(this.getApplicationContext());
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this.getApplicationContext(), R.layout.spinner_item, new ArrayList<>());
                    spinner.setAdapter(spinnerAdapter);
                    spinnerAdapter.notifyDataSetChanged();
                    for(String list : MainActivity.globals.getSqLite().listMarkLists()) {
                        spinnerAdapter.add(list);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    spinner.setLayoutParams(lp);
                    builder.setView(spinner);
                    builder.setPositiveButton(this.getString(R.string.sys_open), (dialog, which) -> setValues(spinner.getSelectedItem().toString()));
                    builder.setNegativeButton(this.getString(R.string.sys_delete), (dialog, which) -> MainActivity.globals.getSqLite().deleteEntry("markLists", "title='" + spinner.getSelectedItem().toString() + "'"));
                    builder.show();
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListActivity.this);
                }
                break;
            case R.id.menMainHelp:
                super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_marklist"));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initControls() {
        // init Toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        // init home as up
        if(this.getSupportActionBar()!=null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // init scale-factor
        this.scale = getApplicationContext().getResources().getDisplayMetrics().density;
        this.cmdMarkListOpenSettings = this.findViewById(R.id.cmdMarkListOpenSearch);

        // init layouts
        this.grdControls = this.findViewById(R.id.grdControls);
        this.grdMarkListSimple = this.findViewById(R.id.grdMarkListSimple);
        this.grdMarkListExpert = this.findViewById(R.id.grdMarkListExpert);
        this.grdMarkListWithCrease = this.findViewById(R.id.grdMarkListWithCrease);

        // init menu_markList-type
        this.spMarkListType = this.findViewById(R.id.spMarkListType);

        // init error label
        this.lblMarkListState = this.findViewById(R.id.lblMarkListState);
        this.ivMarkListState = this.findViewById(R.id.ivMarkListState);

        // init mark-list
        this.lvMarkList = this.findViewById(R.id.lvMarkList);
        this.markListAdapter = new MarkListAdapter(MarkListActivity.this, R.layout.marklist_item, new ArrayList<>());
        this.lvMarkList.setAdapter(this.markListAdapter);
        this.markListAdapter.notifyDataSetChanged();

        // init maxPoints
        this.txtMarkListMaxPoints = this.findViewById(R.id.txtMarkListMaxPoints);
        this.cmdSearch = this.findViewById(R.id.cmdSearch);

        // init simple
        this.rbMarkListSimpleQuarterMarks = this.findViewById(R.id.rbMarkListSimpleQuarterMarks);
        this.rbMarkListSimpleTenthMarks = this.findViewById(R.id.rbMarkListSimpleTenthMarks);
        this.chkMarkListSimpleHalfPoints = this.findViewById(R.id.chkMarkListSimpleHalfPoints);
        this.chkMarkListSimpleDictatMode = this.findViewById(R.id.chkMarkListSimpleDictatMode);

        // init expert
        this.spMarkListExpertView = this.findViewById(R.id.spMarkListExpertView);
        this.spMarkListExpertMarkMode = this.findViewById(R.id.spMarkListExpertMarkMode);

        // init with crease
        this.txtMarkListWithCreaseCustomMark = this.findViewById(R.id.txtMarkListWithCreaseCustomMark);
        this.txtMarkListWithCreaseCustomPoints = this.findViewById(R.id.txtMarkListWithCreaseCustomPoints);
        this.txtMarkListWithCreaseBestMarkAt = this.findViewById(R.id.txtMarkListWithCreaseBestMarkAt);
        this.txtMarkListWithCreaseWorstMarkTo = this.findViewById(R.id.txtMarkListWithCreaseWorstMarkTo);
    }

    @Override
    protected void initValidator() {
        this.baseValidator = new Validator(MarkListActivity.this, R.mipmap.ic_launcher_round);
        this.baseValidator.addIntegerValidator(this.txtMarkListMaxPoints);

        this.withCreaseValidator = new Validator(MarkListActivity.this, R.mipmap.ic_launcher_round);
        this.withCreaseValidator.addDoubleValidator(this.txtMarkListWithCreaseCustomMark);
        this.withCreaseValidator.addDoubleValidator(this.txtMarkListWithCreaseCustomPoints);
        this.withCreaseValidator.addDoubleValidator(this.txtMarkListWithCreaseBestMarkAt);
        this.withCreaseValidator.addDoubleValidator(this.txtMarkListWithCreaseWorstMarkTo);
    }

    private void setValues(String title) {
        this.curSettings = MainActivity.globals.getSqLite().getMarkList(title);
        this.spMarkListType.setSelection(this.curSettings.getType());
        this.txtMarkListMaxPoints.setText(String.valueOf(this.curSettings.getMaxPoints()));
        this.rbMarkListSimpleTenthMarks.setChecked(this.curSettings.isTenthMarks());
        this.chkMarkListSimpleHalfPoints.setChecked(this.curSettings.isHalfPoints());
        this.chkMarkListSimpleDictatMode.setChecked(this.curSettings.isDictatMode());
        this.spMarkListExpertView.setSelection(this.curSettings.getViewMode());
        this.spMarkListExpertMarkMode.setSelection(this.curSettings.getMarMode());
        this.txtMarkListWithCreaseCustomMark.setText(String.valueOf(this.curSettings.getCustomMark()));
        this.txtMarkListWithCreaseCustomPoints.setText(String.valueOf(this.curSettings.getCustomPoints()));
        this.txtMarkListWithCreaseBestMarkAt.setText(String.valueOf(this.curSettings.getBestMarkAt()));
        this.txtMarkListWithCreaseWorstMarkTo.setText(String.valueOf(this.curSettings.getWorstMarkTo()));
    }

    private MarkListSettings getValues(String title) {
        MarkListSettings settings = new MarkListSettings(title);
        settings.setMaxPoints(Integer.parseInt(this.txtMarkListMaxPoints.getText().toString()));
        settings.setType(this.spMarkListType.getSelectedItemPosition());
        settings.setTenthMarks(this.rbMarkListSimpleTenthMarks.isChecked());
        settings.setHalfPoints(this.chkMarkListSimpleHalfPoints.isChecked());
        settings.setDictatMode(this.chkMarkListSimpleDictatMode.isChecked());
        settings.setViewMode(this.spMarkListExpertView.getSelectedItemPosition());
        settings.setMarMode(this.spMarkListExpertMarkMode.getSelectedItemPosition());
        settings.setCustomMark(Double.parseDouble(this.txtMarkListWithCreaseCustomMark.getText().toString()));
        settings.setCustomPoints(Double.parseDouble(this.txtMarkListWithCreaseCustomPoints.getText().toString()));
        settings.setBestMarkAt(Double.parseDouble(this.txtMarkListWithCreaseBestMarkAt.getText().toString()));
        settings.setWorstMarkTo(Double.parseDouble(this.txtMarkListWithCreaseWorstMarkTo.getText().toString()));
        return settings;
    }

    private void calculateMarkList() {
        try {
            if(this.baseValidator.getState()) {
                MarkList markList;
                switch (this.spMarkListType.getSelectedItemPosition()) {
                    case 0:
                        markList = new GermanLinearList(this.getApplicationContext(), 20);
                        break;
                    case 1:
                        if(!this.withCreaseValidator.getState()) {
                            this.markListAdapter.clear();
                            return;
                        }
                        markList = new GermanListWithCrease(this.getApplicationContext(), 20);
                        break;
                    case 2:
                        if(!this.withCreaseValidator.getState()) {
                            this.markListAdapter.clear();
                            return;
                        }
                        markList = new GermanExponentialList(this.getApplicationContext(), 20);
                        break;
                    default:
                        markList = new GermanIHKList(this.getApplicationContext(), 20);
                }

                if(!this.txtMarkListMaxPoints.getText().toString().isEmpty()) {
                    markList.setMaxPoints(Integer.parseInt(this.txtMarkListMaxPoints.getText().toString()));
                }
                markList.setViewMode(MarkListInterface.ViewMode.highestPointsFirst);
                markList.setDictatMode(this.chkMarkListSimpleDictatMode.isChecked());
                if(this.chkMarkListSimpleHalfPoints.isChecked()) {
                    markList.setPointsMultiplier(0.5);
                } else {
                    markList.setPointsMultiplier(1.0);
                }
                if(this.rbMarkListSimpleQuarterMarks.isChecked()) {
                    markList.setMarkMultiplier(0.25);
                } else {
                    markList.setMarkMultiplier(0.1);
                }

                if(MainActivity.globals.getUserSettings().isExpertMode()) {
                    switch (this.spMarkListExpertView.getSelectedItemPosition()) {
                        case 0:
                            markList.setViewMode(MarkListInterface.ViewMode.bestMarkFirst);
                            break;
                        case 1:
                            markList.setViewMode(MarkListInterface.ViewMode.worstMarkFirst);
                            break;
                        case 2:
                            markList.setViewMode(MarkListInterface.ViewMode.highestPointsFirst);
                            break;
                        case 3:
                            markList.setViewMode(MarkListInterface.ViewMode.lowestPointsFirst);
                            break;
                        default:
                            throw new MarkListException(this.getApplicationContext(), R.string.message_marklist_not_implemented, markList);
                    }
                }

                switch (this.spMarkListType.getSelectedItemPosition()) {
                    case 0:
                        if(markList instanceof GermanLinearList) {
                            if(MainActivity.globals.getUserSettings().isExpertMode()) {
                                if(spMarkListExpertMarkMode.getSelectedItemPosition()==0) {
                                    ((GermanLinearList) markList).setMarkMode(MarkListWithMarkMode.MarkMode.normalMarks);
                                } else if(spMarkListExpertMarkMode.getSelectedItemPosition()==1) {
                                    ((GermanLinearList) markList).setMarkMode(MarkListWithMarkMode.MarkMode.pointMarks);
                                }
                            }
                        }
                        break;
                    case 1:
                        if(markList instanceof GermanListWithCrease) {
                            if(MainActivity.globals.getUserSettings().isExpertMode()) {
                                if(spMarkListExpertMarkMode.getSelectedItemPosition()==0) {
                                    ((GermanListWithCrease) markList).setMarkMode(MarkListWithMarkMode.MarkMode.normalMarks);
                                } else if(spMarkListExpertMarkMode.getSelectedItemPosition()==1) {
                                    ((GermanListWithCrease) markList).setMarkMode(MarkListWithMarkMode.MarkMode.pointMarks);
                                }
                            }

                            String customMark = this.txtMarkListWithCreaseCustomMark.getText().toString();
                            if(!customMark.equals("")) {
                                ((GermanListWithCrease) markList).setCustomMark(Double.parseDouble(customMark));
                            }

                            String customPoints = this.txtMarkListWithCreaseCustomPoints.getText().toString();
                            if(!customPoints.equals("")) {
                                ((GermanListWithCrease) markList).setCustomPoints(Double.parseDouble(customPoints));
                            }

                            String bestMarkAt = this.txtMarkListWithCreaseBestMarkAt.getText().toString();
                            if(!bestMarkAt.equals("")) {
                                ((GermanListWithCrease) markList).setBestMarkAt(Double.parseDouble(bestMarkAt));
                            }

                            String worstMarkTo = this.txtMarkListWithCreaseWorstMarkTo.getText().toString();
                            if(!worstMarkTo.equals("")) {
                                ((GermanListWithCrease) markList).setWorstMarkTo(Double.parseDouble(worstMarkTo));
                            }
                        }
                        break;
                    case 2:
                        if(markList instanceof GermanExponentialList) {
                            if(MainActivity.globals.getUserSettings().isExpertMode()) {
                                if(spMarkListExpertMarkMode.getSelectedItemPosition()==0) {
                                    ((GermanExponentialList) markList).setMarkMode(MarkListWithMarkMode.MarkMode.normalMarks);
                                } else if(spMarkListExpertMarkMode.getSelectedItemPosition()==1) {
                                    ((GermanExponentialList) markList).setMarkMode(MarkListWithMarkMode.MarkMode.pointMarks);
                                }
                            }

                            String customMark = this.txtMarkListWithCreaseCustomMark.getText().toString();
                            if(!customMark.equals("")) {
                                ((GermanExponentialList) markList).setCustomMark(Double.parseDouble(customMark));
                            }

                            String customPoints = this.txtMarkListWithCreaseCustomPoints.getText().toString();
                            if(!customPoints.equals("")) {
                                ((GermanExponentialList) markList).setCustomPoints(Double.parseDouble(customPoints));
                            }

                            String bestMarkAt = this.txtMarkListWithCreaseBestMarkAt.getText().toString();
                            if(!bestMarkAt.equals("")) {
                                ((GermanExponentialList) markList).setBestMarkAt(Double.parseDouble(bestMarkAt));
                            }

                            String worstMarkTo = this.txtMarkListWithCreaseWorstMarkTo.getText().toString();
                            if(!worstMarkTo.equals("")) {
                                ((GermanExponentialList) markList).setWorstMarkTo(Double.parseDouble(worstMarkTo));
                            }
                        }
                        break;
                    default:
                        if(markList instanceof GermanIHKList) {
                            markList.calculate();
                        }
                        break;
                }

                this.markListAdapter.setDictatMode(markList.isDictatMode());
                this.markListAdapter.setViewMode(markList.getViewMode());


                this.markListAdapter.clear();
                for(Map.Entry<Double, Double> entry : markList.calculate().entrySet()) {
                    this.markListAdapter.add(entry);
                }
                this.updateState(null);
            } else {
                this.markListAdapter.clear();
            }
        } catch (MarkListException ex) {
            this.updateState(ex);
        }
    }

    private void updateState(Exception ex) {
        if(ex!=null) {
            this.ivMarkListState.setVisibility(View.VISIBLE);
            this.lblMarkListState.setText(this.getString(R.string.marklist_state_error));
            this.detailedErrorMessage = ex.getMessage();
            //MessageHelper.printException(ex.getMessage(), R.mipmap.ic_launcher_round, MarkListActivity.this);
        } else {
            this.ivMarkListState.setVisibility(View.GONE);
            this.lblMarkListState.setText(this.getString(R.string.marklist_state_ok));
            this.detailedErrorMessage = "";
        }
    }

    private void openMarkList(int id) {
        List<String> titles = MainActivity.globals.getSqLite().listMarkLists("id=" + id);
        if(titles!=null) {
            if(!titles.isEmpty()) {
                this.setValues(titles.get(0));
            }
        }
    }

    private void findItem(String text) {
        if(text!=null) {
            if(!text.trim().isEmpty()) {
                text = text.trim().toLowerCase();
                boolean isPoints = false;
                double number = 0.0;
                try {
                    number = Double.parseDouble(text);
                    isPoints = true;
                } catch (Exception ex) {
                    try {
                        if(text.endsWith("pkt") || text.endsWith("punkte") || text.endsWith("p") || text.endsWith("pt") || text.endsWith("f") || text.endsWith("fehler")) {
                            isPoints = true;
                            number = this.findValue(number, text, "pkt");
                            number = this.findValue(number, text, "punkte");
                            number = this.findValue(number, text, "p");
                            number = this.findValue(number, text, "pt");
                            number = this.findValue(number, text, "f");
                            number = this.findValue(number, text, "fehler");
                        } else if(text.endsWith("n") || text.endsWith("note")) {
                            number = this.findValue(number, text, "n");
                            number = this.findValue(number, text, "note");
                        }

                        if(number!=0.0) {
                            MarkListInterface.ViewMode viewMode = markListAdapter.getViewMode();
                            int position = 0;
                            boolean found = false;
                            switch (viewMode) {
                                case bestMarkFirst:
                                case worstMarkFirst:
                                    if(isPoints) {
                                        for(;position<=markListAdapter.getCount()-1; position++) {
                                            Map.Entry<Double, Double> entry = markListAdapter.getItem(position);
                                            if(entry!=null) {
                                                if (entry.getValue() == number) {
                                                    found = true;
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        for(;position<=markListAdapter.getCount()-1; position++) {
                                            Map.Entry<Double, Double> entry = markListAdapter.getItem(position);
                                            if(entry!=null) {
                                                if (entry.getKey() == number) {
                                                    found = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case lowestPointsFirst:
                                case highestPointsFirst:
                                    if(isPoints) {
                                        for(;position<=markListAdapter.getCount()-1; position++) {
                                            Map.Entry<Double, Double> entry = markListAdapter.getItem(position);
                                            if(entry!=null) {
                                                if (entry.getKey() == number) {
                                                    found = true;
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        for(;position<=markListAdapter.getCount()-1; position++) {
                                            Map.Entry<Double, Double> entry = markListAdapter.getItem(position);
                                            if(entry!=null) {
                                                if (entry.getValue() == number) {
                                                    found = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    break;
                            }

                            if(found) {
                                this.markListAdapter.findItem(position);
                                this.calculateMarkList();

                                this.lvMarkList.smoothScrollToPosition(0);
                                this.lvMarkList.setSelection(position);
                                this.lvMarkList.smoothScrollToPositionFromTop(position, 0, 1000);
                            }
                        }
                    } catch(Exception e) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListActivity.this);
                    }
                }
            }
        }
    }

    private double findValue(double value, String text, String part) {
        if(text.endsWith(part)) {
            return Double.parseDouble(text.replace(part, "").trim());
        } else {
            return value;
        }
    }
}
