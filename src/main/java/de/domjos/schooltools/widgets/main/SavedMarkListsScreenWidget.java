package de.domjos.schooltools.widgets.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.adapter.MarkListAdapter;
import de.domjos.schooltools.core.exceptions.MarkListException;
import de.domjos.schooltools.core.marklist.de.GermanListWithCrease;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.settings.MarkListSettings;

public final class SavedMarkListsScreenWidget extends ScreenWidget {
    private Spinner cmbSavedMarkList;
    private MarkListAdapter markListAdapter;
    private ArrayAdapter<String> savedMarkListAdapter;

    public SavedMarkListsScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        this.cmbSavedMarkList = super.view.findViewById(R.id.cmbSavedMarklist);
        this.savedMarkListAdapter = new ArrayAdapter<>(super.activity, android.R.layout.simple_spinner_item, new ArrayList<String>());
        this.cmbSavedMarkList.setAdapter(this.savedMarkListAdapter);
        this.savedMarkListAdapter.notifyDataSetChanged();

        ListView lvMarkList = super.view.findViewById(R.id.lvMarklist);
        this.markListAdapter = new MarkListAdapter(super.activity, R.layout.marklist_item, new ArrayList<Map.Entry<Double, Double>>());
        lvMarkList.setAdapter(this.markListAdapter);
        this.markListAdapter.notifyDataSetChanged();

        lvMarkList.setOnTouchListener(Helper.addOnTouchListenerForScrolling());

        this.cmbSavedMarkList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.globals.getGeneralSettings().setWidgetMarkListSpinner(savedMarkListAdapter.getItem(position));
                MarkListSettings settings = MainActivity.globals.getSqLite().getMarkList(savedMarkListAdapter.getItem(position));
                calculateSelectedMarkList(settings);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void calculateSelectedMarkList(MarkListSettings settings) {
        try {
            this.markListAdapter.clear();
            GermanListWithCrease germanListWithCrease = new GermanListWithCrease(super.activity.getApplicationContext(), settings.getMaxPoints());
            germanListWithCrease.setMarkMultiplier(0.1);
            germanListWithCrease.setPointsMultiplier(0.5);
            germanListWithCrease.setDictatMode(settings.isDictatMode());
            germanListWithCrease.setWorstMarkTo(settings.getWorstMarkTo());
            germanListWithCrease.setBestMarkAt(settings.getBestMarkAt());
            germanListWithCrease.setCustomMark(settings.getCustomMark());
            germanListWithCrease.setCustomPoints(settings.getCustomPoints());
            this.markListAdapter.setViewMode(germanListWithCrease.getViewMode());
            this.markListAdapter.setDictatMode(germanListWithCrease.isDictatMode());

            for(Map.Entry<Double, Double> entry : germanListWithCrease.calculate().entrySet()) {
                this.markListAdapter.add(entry);
            }
        } catch (MarkListException ex) {
            Helper.printException(super.activity, ex);
        }
    }

    public void addMarkLists() {
        this.savedMarkListAdapter.clear();
        if(super.view.getVisibility()==View.VISIBLE) {
            for(String item : MainActivity.globals.getSqLite().listMarkLists()) {
                this.savedMarkListAdapter.add(item);
            }
        }
    }

    public void getSavedValue(String markList) {
        if(markList!=null) {
            for(int i = 0; i<=savedMarkListAdapter.getCount()-1; i++) {
                if(savedMarkListAdapter.getItem(i)!=null) {
                    String currentMarkList = this.savedMarkListAdapter.getItem(i);
                    if(currentMarkList!=null) {
                        if(currentMarkList.equals(markList)) {
                            this.cmbSavedMarkList.setSelection(i);
                            break;
                        }
                    }
                }
            }
        }
    }
}
