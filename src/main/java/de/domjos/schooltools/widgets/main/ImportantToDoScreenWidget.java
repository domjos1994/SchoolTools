package de.domjos.schooltools.widgets.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.adapter.ToDoAdapter;
import de.domjos.schooltools.core.model.todo.ToDo;
import de.domjos.schooltools.helper.Helper;

public final class ImportantToDoScreenWidget extends ScreenWidget {
    private ToDoAdapter toDoAdapter;

    public ImportantToDoScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        ListView lvImportantToDos = view.findViewById(R.id.lvImportantToDos);
        this.toDoAdapter = new ToDoAdapter(super.activity, R.layout.todo_item, new ArrayList<ToDo>());
        lvImportantToDos.setAdapter(this.toDoAdapter);
        this.toDoAdapter.notifyDataSetChanged();

        lvImportantToDos.setOnTouchListener(Helper.addOnTouchListenerForScrolling());
    }

    public void addToDos() {
        this.toDoAdapter.clear();
        if(super.view.getVisibility()==View.VISIBLE) {
            List<ToDo> toDos = MainActivity.globals.getSqLite().getToDos("");
            Map<ToDo, Integer> todoMap = new HashMap<>();
            for(ToDo toDo : toDos) {
                todoMap.put(toDo, toDo.getImportance());
            }

            Object[] a = todoMap.entrySet().toArray();
            if(a!=null) {
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        if(o2 instanceof Map.Entry && o1 instanceof Map.Entry) {
                            Map.Entry entry1 = (Map.Entry) o1;
                            Map.Entry entry2 = (Map.Entry) o2;

                            if(entry1.getValue() instanceof Integer && entry2.getValue() instanceof Integer) {
                                return ((Integer) entry1.getValue()).compareTo(((Integer) entry2.getValue()));
                            }
                        }
                        return -1;
                    }
                });
                toDos.clear();
                for(Object obj : a) {
                    if(obj instanceof Map.Entry) {
                        Map.Entry entry = (Map.Entry) obj;
                        if(entry.getKey() instanceof ToDo) {
                            this.toDoAdapter.add((ToDo) entry.getKey());
                        }
                    }

                    if(this.toDoAdapter.getCount() % 5 == 0) {
                        break;
                    }
                }
                if(this.toDoAdapter.isEmpty()) {
                    ToDo toDo = new ToDo();
                    toDo.setTitle(super.activity.getString(R.string.main_noEntry));
                    this.toDoAdapter.add(toDo);
                }
            }
        }
    }
}
