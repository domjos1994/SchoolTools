package de.domjos.schooltools.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.LearningCardGroupAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;

import java.util.ArrayList;

public class LearningCardGroupActivity extends AppCompatActivity {
    private FloatingActionButton cmdLearningCardGroupAdd;
    private ListView lvLearnCardGroups;
    private LearningCardGroupAdapter learningCardGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning_card_group_activity);
        this.initControls();
    }

    private void initControls() {
        this.cmdLearningCardGroupAdd = this.findViewById(R.id.cmdLearningCardGroupAdd);

        this.learningCardGroupAdapter = new LearningCardGroupAdapter(this.getApplicationContext(), R.layout.learning_card_group_item, new ArrayList<LearningCardGroup>());
        this.lvLearnCardGroups = this.findViewById(R.id.lvLearningCardGroups);
        this.lvLearnCardGroups.setAdapter(this.learningCardGroupAdapter);
        this.learningCardGroupAdapter.notifyDataSetChanged();
    }
}
