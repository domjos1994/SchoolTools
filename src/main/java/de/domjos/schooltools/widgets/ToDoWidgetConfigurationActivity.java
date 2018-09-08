package de.domjos.schooltools.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.LinkedList;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.todo.ToDoList;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;

/**
 * Configuration Screen for ToDoWidget
 * @see de.domjos.schooltools.widgets.ToDoWidget
 * @see de.domjos.schooltools.services.ToDoWidgetService
 * @see de.domjos.schooltools.factories.ToDoRemoteFactory
 * @author Dominic Joas
 * @version 0.1
 */
public class ToDoWidgetConfigurationActivity extends AppCompatActivity {
    private int appWidgetID;
    private Button cmdSave;
    private Spinner cmbToDoLists;
    private CheckBox chkToDoNotSolved;
    private ArrayAdapter<ToDoList> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_widget_configuration_activity);
        setResult(RESULT_CANCELED);
        this.initControls();

        this.cmdSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                ToDoList toDoList = adapter.getItem(cmbToDoLists.getSelectedItemPosition());

                if(toDoList!=null) {
                    getSettings(toDoList.getID(), chkToDoNotSolved.isChecked());
                }
            }
        });
    }

    private void initControls() {
        SQLite sqLite = new SQLite(this.getApplicationContext());
        Bundle extras = this.getIntent().getExtras();
        if(extras!=null) {
            this.appWidgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        }

        this.cmdSave = this.findViewById(R.id.cmdSave);
        this.chkToDoNotSolved = this.findViewById(R.id.chkToDoNotSolved);

        this.cmbToDoLists = this.findViewById(R.id.cmbToDoList);
        this.adapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item, new LinkedList<ToDoList>());
        this.cmbToDoLists.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();

        for(ToDoList toDoList : sqLite.getToDoLists("")) {
            this.adapter.add(toDoList);
        }
    }

    private void getSettings(int id, boolean solved) {
        try {
            this.saveSettings(id, solved);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
            ToDoWidget.updateAppWidget(this.getApplicationContext(), appWidgetManager, this.appWidgetID);

            Intent resultValue = new Intent(getApplicationContext(), ToDoWidget.class);
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.appWidgetID);
            resultValue.putExtra("id", id);
            resultValue.putExtra("solved", solved);
            setResult(RESULT_OK, resultValue);
            finish();
        } catch (Exception ex) {
            Helper.printException(this.getApplicationContext(), ex);
        }
    }

    private void saveSettings(int id, boolean solved) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
        editor.putInt("todo_list_id_" + this.appWidgetID, id);
        editor.putBoolean("todo_list_solved_" + this.appWidgetID, solved);
        editor.apply();
    }
}
