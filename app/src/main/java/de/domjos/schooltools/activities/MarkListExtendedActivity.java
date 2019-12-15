/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;

import de.domjos.schooltoolslib.marklist.de.GermanExponentialList;
import de.domjos.schooltoolslib.marklist.de.GermanIHKList;
import de.domjos.schooltoolslib.marklist.de.GermanLinearList;
import de.domjos.schooltoolslib.marklist.de.GermanListWithCrease;
import de.domjos.schooltoolslib.model.marklist.ExtendedMarkList;
import de.domjos.schooltoolslib.model.marklist.MarkList;
import de.domjos.schooltoolslib.model.marklist.MarkListInterface;
import de.domjos.schooltools.helper.ApiHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.customwidgets.widgets.LabelledSeekBar;

public final class MarkListExtendedActivity extends AbstractActivity {
    private int type = 1;
    private GraphView graphView;
    private LabelledSeekBar sbMaximumPoints, sbCustomMark, sbCustomPoints, sbBestMarkFirst, sbWorstMarkTo;
    private MarkList markList;

    private TextView lblMarkListState;
    private ImageView ivMarkListState;
    private String detailedErrorMessage = "";

    public MarkListExtendedActivity() {
        super(R.layout.marklist_extended_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {
        this.initDefaultValues();
        Helper.closeSoftKeyboard(MarkListExtendedActivity.this);

        this.sbMaximumPoints.setOnChangeListener(() -> {
            sbCustomPoints.setMax(sbMaximumPoints.getCurrent());
            sbBestMarkFirst.setMax(sbMaximumPoints.getCurrent());
            sbWorstMarkTo.setMax(sbMaximumPoints.getCurrent());
            graphView.getViewport().setMaxX(sbMaximumPoints.getCurrent()+1);
            calculateMarkListAndDrawDiagram();
        });

        this.sbCustomMark.setOnChangeListener(this::calculateMarkListAndDrawDiagram);
        this.sbCustomPoints.setOnChangeListener(this::calculateMarkListAndDrawDiagram);
        this.sbBestMarkFirst.setOnChangeListener(this::calculateMarkListAndDrawDiagram);
        this.sbWorstMarkTo.setOnChangeListener(this::calculateMarkListAndDrawDiagram);

        this.ivMarkListState.setOnClickListener(view -> {
            if(!detailedErrorMessage.equals("")) {
                MessageHelper.printMessage(detailedErrorMessage, R.mipmap.ic_launcher_round, MarkListExtendedActivity.this);
            }
        });

        this.lblMarkListState.setOnClickListener(view -> {
            if(!detailedErrorMessage.equals("")) {
                MessageHelper.printMessage(detailedErrorMessage, R.mipmap.ic_launcher_round, MarkListExtendedActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if(this.markList !=null) {
            intent.putExtra("type", this.type);
            intent.putExtra("maxPoints", this.markList.getMaxPoints());
            intent.putExtra("markMulti", this.markList.getMarkMultiplier());
            intent.putExtra("pointsMulti", this.markList.getPointsMultiplier());
            if(this.markList instanceof ExtendedMarkList) {
                intent.putExtra("bestMarkAt", ((ExtendedMarkList)this.markList).getBestMarkAt());
                intent.putExtra("worstMarkTo", ((ExtendedMarkList)this.markList).getWorstMarkTo());
                intent.putExtra("customMark", ((ExtendedMarkList)this.markList).getCustomMark());
                intent.putExtra("customPoints", ((ExtendedMarkList)this.markList).getCustomPoints());
            } else {
                intent.putExtra("bestMarkAt", this.markList.getMaxPoints());
                intent.putExtra("worstMarkTo", 0.0);
                intent.putExtra("customMark", 3.5);
                intent.putExtra("customPoints", this.markList.getMaxPoints()/2);
            }
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marklist_extended, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            case R.id.menHelp:
                startActivity(new Intent(this.getApplicationContext(), HelpActivity.class));
                break;
            case R.id.menShare:
                if(Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, MarkListExtendedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    this.graphView.takeSnapshotAndShare(MarkListExtendedActivity.this, "marklist_graph", "Marklist-Graph");
                }
                break;
            case R.id.menTakeSnapshot:
                if(Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, MarkListExtendedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    File defaultDir = getApplicationContext().getFilesDir();
                    DialogProperties properties = new DialogProperties();
                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                    properties.selection_type = DialogConfigs.DIR_SELECT;
                    properties.root = new File(Objects.requireNonNull(new File(ApiHelper.findExistingFolder(MarkListExtendedActivity.this)).getParent()));
                    properties.extensions = null;
                    properties.error_dir = defaultDir;
                    properties.offset = defaultDir;

                    FilePickerDialog dialog = new FilePickerDialog(MarkListExtendedActivity.this, properties);
                    dialog.setTitle(getString(R.string.api_path_choose_dir));

                    final Bitmap bitmap = this.graphView.takeSnapshot();

                    dialog.setDialogSelectionListener(files -> {
                        FileOutputStream outputStream = null;
                        try {
                            if(files!=null) {
                                outputStream = new FileOutputStream(files[0] + File.separatorChar + "graphView.png");
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                outputStream.flush();

                            }
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListExtendedActivity.this);
                        } finally {
                            if(outputStream!=null) {
                                try {
                                    outputStream.close();
                                } catch (Exception ex) {
                                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListExtendedActivity.this);
                                }
                            }
                        }
                    });
                    dialog.show();
                }
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initControls() {
        try {

            if(this.getSupportActionBar()!=null) {
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            this.lblMarkListState = this.findViewById(R.id.lblMarkListState);
            this.ivMarkListState = this.findViewById(R.id.ivMarkListState);

            this.graphView = this.findViewById(R.id.graph);
            this.sbMaximumPoints = this.findViewById(R.id.sbMaximumPoints);
            this.sbCustomMark = this.findViewById(R.id.sbCustomMark);
            this.sbCustomPoints = this.findViewById(R.id.sbCustomPoints);
            this.sbWorstMarkTo = this.findViewById(R.id.sbWorstMarkTo);
            this.sbBestMarkFirst = this.findViewById(R.id.sbBestMarkFirst);
            this.configureGraphView();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListExtendedActivity.this);
        }
    }

    @SuppressWarnings("deprecation")
    private void configureGraphView() {
        int color = Color.WHITE;

        this.graphView.setPadding(1, 1, 1, 1);
        this.graphView.getViewport().setYAxisBoundsManual(true);
        this.graphView.getViewport().setMinY(6);
        this.graphView.getViewport().setMaxY(1);

        this.graphView.getViewport().setXAxisBoundsManual(true);
        this.graphView.getViewport().setMinX(0);
        this.graphView.getViewport().setMaxX(21);

        this.graphView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.graphView.setDrawingCacheBackgroundColor(color);

        // format graphView
        GridLabelRenderer gridLabel = this.graphView.getGridLabelRenderer();
        gridLabel.setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        gridLabel.setHorizontalAxisTitle(this.getString(R.string.marklist_points));
        gridLabel.setHorizontalAxisTitleColor(color);
        gridLabel.setHorizontalLabelsColor(color);
        gridLabel.setVerticalAxisTitle(this.getString(R.string.marklist_mark));
        gridLabel.setVerticalAxisTitleColor(color);
        gridLabel.setVerticalLabelsColor(color);
        gridLabel.setGridColor(color);
    }

    private void initDefaultValues() {
        this.type = this.getIntent().getIntExtra("type", 1);
        this.sbMaximumPoints.setMax(MainActivity.globals.getUserSettings().getMarkListMax());
        this.sbMaximumPoints.setCurrent(this.getIntent().getIntExtra("maxPoints", 20));
        this.sbCustomMark.setMax(6);
        this.sbCustomMark.setCurrent(this.getIntent().getDoubleExtra("customMark", 3.5));
        this.sbCustomPoints.setMax(this.sbMaximumPoints.getCurrent());
        this.sbCustomPoints.setCurrent(this.getIntent().getDoubleExtra("customPoints", this.sbMaximumPoints.getCurrent()/2));
        this.sbBestMarkFirst.setMax(this.sbMaximumPoints.getCurrent());
        this.sbBestMarkFirst.setCurrent(this.getIntent().getDoubleExtra("bestMarkAt", this.sbMaximumPoints.getCurrent()));
        this.sbWorstMarkTo.setMax(this.sbMaximumPoints.getCurrent());
        this.sbWorstMarkTo.setCurrent(this.getIntent().getDoubleExtra("worstMarkTo", 0.0));
        this.graphView.getViewport().setMaxX(sbMaximumPoints.getCurrent()+1);
        int markMode = this.getIntent().getIntExtra("markMode", 0);
        if(markMode==0) {
            this.sbBestMarkFirst.setCurrent(this.sbMaximumPoints.getCurrent());
            this.sbWorstMarkTo.setCurrent(0.0f);
            this.sbCustomPoints.setCurrent(this.sbMaximumPoints.getCurrent()/2);
            this.sbCustomMark.setCurrent(3.5f);
        }
        this.calculateMarkListAndDrawDiagram();
    }

    private void calculateMarkListAndDrawDiagram() {
        try {
            /*if(this.sbMaximumPoints.getCurrent()<=this.sbBestMarkFirst.getCurrent()) {
                this.sbBestMarkFirst.setCurrent(this.sbMaximumPoints.getCurrent());
            }*/

            switch (this.type) {
                case 0:
                    this.markList = new GermanLinearList(getApplicationContext(), (int) this.sbMaximumPoints.getCurrent());
                    break;
                case 1:
                    this.markList = new GermanListWithCrease(getApplicationContext(), (int) this.sbMaximumPoints.getCurrent());
                    ((GermanListWithCrease) this.markList).setCustomPoints(this.sbCustomPoints.getCurrent());
                    ((GermanListWithCrease) this.markList).setCustomMark(this.sbCustomMark.getCurrent());
                    ((GermanListWithCrease) this.markList).setWorstMarkTo(this.sbWorstMarkTo.getCurrent());
                    ((GermanListWithCrease) this.markList).setBestMarkAt(this.sbBestMarkFirst.getCurrent());
                    break;
                case 2:
                    this.markList = new GermanExponentialList(getApplicationContext(), (int) this.sbMaximumPoints.getCurrent());
                    ((GermanExponentialList) this.markList).setCustomPoints(this.sbCustomPoints.getCurrent());
                    ((GermanExponentialList) this.markList).setCustomMark(this.sbCustomMark.getCurrent());
                    ((GermanExponentialList) this.markList).setWorstMarkTo(this.sbWorstMarkTo.getCurrent());
                    ((GermanExponentialList) this.markList).setBestMarkAt(this.sbBestMarkFirst.getCurrent());
                    break;
                case 3:
                    this.markList = new GermanIHKList(getApplicationContext(), (int) this.sbMaximumPoints.getCurrent());
                    break;
            }
            this.markList.setMarkMultiplier(0.1);
            this.markList.setPointsMultiplier(1);
            this.markList.setViewMode(MarkListInterface.ViewMode.lowestPointsFirst);

            Map<Double, Double> mp = markList.calculate();

            String settings;
            if(markList instanceof ExtendedMarkList) {
                String format = "%s: %s: %s %s%n%s: %s %s%n%s: %s %s%nP(%s %s, %s %s)";
                settings =
                    String.format(
                        format,
                        this.getString(R.string.main_menu_settings),
                        this.getString(R.string.marklist_maxPoints),
                        markList.getMaxPoints(),
                        this.getString(R.string.marklist_points),
                        this.getString(R.string.marklist_bestMarkAt),
                        ((ExtendedMarkList)markList).getBestMarkAt(),
                        this.getString(R.string.marklist_points),
                        this.getString(R.string.marklist_worstMarkTo),
                        ((ExtendedMarkList)markList).getWorstMarkTo(),
                        this.getString(R.string.marklist_points),
                        ((ExtendedMarkList)markList).getCustomPoints(),
                        this.getString(R.string.marklist_points),
                        this.getString(R.string.marklist_mark),
                        ((ExtendedMarkList)markList).getCustomMark());
            } else {
                String format = "%s: %s: %s %s";
                settings =
                    String.format(
                        format,
                        this.getString(R.string.main_menu_settings),
                        this.getString(R.string.marklist_maxPoints),
                        markList.getMaxPoints(),
                        this.getString(R.string.marklist_points));
            }
            DataPoint[] dpArray = new DataPoint[mp.size()];
            int i = 0;

            Map<Double, Double> tmp = new TreeMap<>(mp);
            for(Map.Entry<Double, Double> entry : tmp.entrySet()) {
                DataPoint point = null;

                if(this.markList instanceof ExtendedMarkList) {
                    if (entry.getKey() >= ((ExtendedMarkList)markList).getBestMarkAt() && entry.getKey() <= ((ExtendedMarkList)markList).getWorstMarkTo()) {
                        for (int j = 0; j <= dpArray.length - 1; j++) {
                            if (dpArray[j] != null) {
                                if (dpArray[j].getY() == entry.getValue()) {
                                    point = dpArray[j];
                                    break;
                                }
                                if (dpArray[j].getX() == entry.getKey()) {
                                    point = dpArray[j];
                                    break;
                                }
                            }
                        }
                    }
                }

                if (point == null) {
                    dpArray[i] = new DataPoint(entry.getKey(), entry.getValue());
                } else {
                    dpArray[i] = point;
                }
                i++;
            }

            this.graphView.getSeries().clear();
            Set<String> stringSet = MainActivity.globals.getUserSettings().getDiagramView();
            String[] values = this.getResources().getStringArray(R.array.settings_school_extended_diagram_view_entries);
            if(stringSet.contains(values[0])) {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dpArray);
                int extendedColor = MainActivity.globals.getUserSettings().getExtendedColor();
                if(extendedColor!=0) {
                    series.setColor(extendedColor);
                } else {
                    series.setColor(Color.WHITE);
                }
                series.setDrawAsPath(true);
                series.setOnDataPointTapListener((series1, dataPoint) -> MessageHelper.printMessage(getString(R.string.marklist_points) + ": " + dataPoint.getX() + "\n" + getString(R.string.marklist_mark) + ": " + dataPoint.getY(), R.mipmap.ic_launcher_round, MarkListExtendedActivity.this));
                this.graphView.addSeries(series);
            }
            if(stringSet.contains(values[1])) {
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dpArray);
                int extendedColor = MainActivity.globals.getUserSettings().getExtendedColor();
                if(extendedColor!=0) {
                    series.setColor(extendedColor);
                } else {
                    series.setColor(Color.WHITE);
                }
                series.setOnDataPointTapListener((series12, dataPoint) -> MessageHelper.printMessage(getString(R.string.marklist_points) + ": " + dataPoint.getX() + "\n" + getString(R.string.marklist_mark) + ": " + dataPoint.getY(), R.mipmap.ic_launcher_round, MarkListExtendedActivity.this));
                this.graphView.addSeries(series);
            }

            this.updateState(null, settings);
        } catch (Exception ex) {
            this.updateState(ex, null);
        }
    }

    private void updateState(Exception ex, String content) {
        if(ex!=null) {
            this.ivMarkListState.setVisibility(View.VISIBLE);
            this.lblMarkListState.setText(this.getString(R.string.marklist_state_error));
            this.detailedErrorMessage = ex.getMessage();
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MarkListExtendedActivity.this);
        } else {
            this.ivMarkListState.setVisibility(View.GONE);
            this.lblMarkListState.setText(this.getString(R.string.marklist_state_ok));
            this.detailedErrorMessage = content;
        }
    }
}
