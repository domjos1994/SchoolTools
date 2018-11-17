package de.domjos.schooltools.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import de.domjos.schooltools.R;

public class LearningCardOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.learning_card_overview_activity);
        this.initControls();


    }


    private void initControls() {
        BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navLearningCardTeacher:
                        startActivity(new Intent(getApplicationContext(), TimeTableTeacherActivity.class));
                        break;
                    case R.id.navLearningCardLesson:
                        Intent intent = new Intent(getApplicationContext(), TimeTableSubjectActivity.class);
                        intent.putExtra("parent", R.layout.learning_card_overview_activity);
                        startActivity(intent);
                        break;
                    case R.id.navLearningCardGroups:
                        startActivityForResult(new Intent(getApplicationContext(), LearningCardGroupActivity.class), 99);
                        break;
                    case R.id.navLearningCardQueries:

                        break;
                }
                return false;
            }
        };

        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(listener);
    }
}
