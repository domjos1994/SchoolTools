/*
 * Copyright (C) 2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Map;

import de.domjos.schooltools.R;

import de.domjos.schooltools.core.marklist.de.GermanListWithCrease;
import de.domjos.schooltools.core.model.marklist.MarkListInterface;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.Log4JHelper;
import de.domjos.schooltools.helper.custom.LabelledSeekBar;

public class MarkListExtendedActivity extends AppCompatActivity {
    private GraphView graphView;
    private LabelledSeekBar sbMaximumPoints, sbCustomMark, sbCustomPoints, sbBestMarkFirst, sbWorstMarkTo;
    private GermanListWithCrease germanLinearList;

    private TextView lblMarkListState;
    private ImageView ivMarkListState;
    private String detailedErrorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marklist_extended_activity);
        this.initControls();
        this.initDefaultValues();
        Helper.closeSoftKeyboard(MarkListExtendedActivity.this);

        this.sbMaximumPoints.setOnChangeListener(new Runnable() {
            @Override
            public void run() {
                sbCustomPoints.setMax(sbMaximumPoints.getCurrent());
                sbBestMarkFirst.setMax(sbMaximumPoints.getCurrent());
                sbWorstMarkTo.setMax(sbMaximumPoints.getCurrent());
                graphView.getViewport().setMaxX(sbMaximumPoints.getCurrent()+1);
                calculateMarkListAndDrawDiagram();
            }
        });

        this.sbCustomMark.setOnChangeListener(new Runnable() {
            @Override
            public void run() {
                calculateMarkListAndDrawDiagram();
            }
        });

        this.sbCustomPoints.setOnChangeListener(new Runnable() {
            @Override
            public void run() {
                calculateMarkListAndDrawDiagram();
            }
        });

        this.sbBestMarkFirst.setOnChangeListener(new Runnable() {
            @Override
            public void run() {
                calculateMarkListAndDrawDiagram();
            }
        });

        this.sbWorstMarkTo.setOnChangeListener(new Runnable() {
            @Override
            public void run() {
                calculateMarkListAndDrawDiagram();
            }
        });

        this.ivMarkListState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!detailedErrorMessage.equals("")) {
                    Helper.createToast(getApplicationContext(), detailedErrorMessage);
                }
            }
        });

        this.lblMarkListState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!detailedErrorMessage.equals("")) {
                    Helper.createToast(getApplicationContext(), detailedErrorMessage);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if(this.germanLinearList!=null) {
            intent.putExtra("maxPoints", this.germanLinearList.getMaxPoints());
            intent.putExtra("markMulti", this.germanLinearList.getMarkMultiplier());
            intent.putExtra("pointsMulti", this.germanLinearList.getPointsMultiplier());
            intent.putExtra("bestMarkAt", this.germanLinearList.getBestMarkAt());
            intent.putExtra("worstMarkTo", this.germanLinearList.getWorstMarkTo());
            intent.putExtra("customMark", this.germanLinearList.getCustomMark());
            intent.putExtra("customPoints", this.germanLinearList.getCustomPoints());
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
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
            Helper.printException(getApplicationContext(), ex);
        }
    }

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
        this.sbMaximumPoints.setMax(MainActivity.settings.getMarkListMax());
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

            this.germanLinearList = new GermanListWithCrease(getApplicationContext(), (int) this.sbMaximumPoints.getCurrent());
            this.germanLinearList.setMarkMultiplier(0.1);
            this.germanLinearList.setPointsMultiplier(1);
            this.germanLinearList.setCustomPoints(this.sbCustomPoints.getCurrent());
            this.germanLinearList.setCustomMark(this.sbCustomMark.getCurrent());
            this.germanLinearList.setWorstMarkTo(this.sbWorstMarkTo.getCurrent());
            this.germanLinearList.setBestMarkAt(this.sbBestMarkFirst.getCurrent());
            this.germanLinearList.setViewMode(MarkListInterface.ViewMode.lowestPointsFirst);

            Map<Double, Double> mp = germanLinearList.calculate();
            String format = "%s: %s: %s %s\n%s: %s %s\n%s: %s %s\nP(%s %s, %s %s)";
            String settings =
                String.format(
                    format,
                    this.getString(R.string.main_menu_settings),
                    this.getString(R.string.marklist_maxPoints),
                    germanLinearList.getMaxPoints(),
                    this.getString(R.string.marklist_points),
                    this.getString(R.string.marklist_bestMarkAt),
                    germanLinearList.getBestMarkAt(),
                    this.getString(R.string.marklist_points),
                    this.getString(R.string.marklist_worstMarkTo),
                    germanLinearList.getWorstMarkTo(),
                    this.getString(R.string.marklist_points),
                    germanLinearList.getCustomPoints(),
                    this.getString(R.string.marklist_points),
                    this.getString(R.string.marklist_mark),
                    germanLinearList.getCustomMark());
            DataPoint[] dpArray = new DataPoint[mp.size()];
            int i = 0;
            for(Map.Entry<Double, Double> entry : mp.entrySet()) {
                DataPoint point = null;

                if (entry.getKey() >= germanLinearList.getBestMarkAt() && entry.getKey() <= germanLinearList.getWorstMarkTo()) {
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

                if (point == null) {
                    dpArray[i] = new DataPoint(entry.getKey(), entry.getValue());
                } else {
                    dpArray[i] = point;
                }
                i++;
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dpArray);
            series.setColor(Color.WHITE);
            series.setDrawAsPath(false);
            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Helper.createToast(getApplicationContext(), getString(R.string.marklist_points) + ": " + dataPoint.getX() + "\n" + getString(R.string.marklist_mark) + ": " + dataPoint.getY());
                }
            });

            this.graphView.getSeries().clear();
            this.graphView.addSeries(series);
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
            Log4JHelper.getLogger(MarkListActivity.class.getName()).error(ex);
        } else {
            this.ivMarkListState.setVisibility(View.GONE);
            this.lblMarkListState.setText(this.getString(R.string.marklist_state_ok));
            this.detailedErrorMessage = content;
        }
    }
}
