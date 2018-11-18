package de.domjos.schooltools.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import de.domjos.schooltools.R;


public class LearningCardQueryActivity extends AppCompatActivity {
    private BottomNavigationView navigation;

    private ListView lvLearningCardQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning_card_query_activity);
        this.initControls();
    }


    private void initControls() {
        this.navigation = this.findViewById(R.id.navigation);
        BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                }
                return false;
            }
        };
        this.navigation.setOnNavigationItemSelectedListener(listener);

        this.lvLearningCardQueries = this.findViewById(R.id.lvLearningCardQueries);
    }
}
