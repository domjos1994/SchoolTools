package de.domjos.schooltools.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.LearningCardQueryFragmentAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;
import de.domjos.schooltools.fragment.LearningCardFragment;
import de.domjos.schooltools.helper.Helper;

public class LearningCardOverviewActivity extends FragmentActivity {
    private ViewPager viewPager;
    private LearningCardQueryFragmentAdapter fragmentAdapter;
    private Button cmdLearningCardQueryStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.learning_card_overview_activity);
        this.initControls();

        this.cmdLearningCardQueryStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cmdLearningCardQueryStart.getText().equals(getString(R.string.learningCard_query))) {
                    final Dialog dialog = new Dialog(LearningCardOverviewActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.learning_card_dialog);
                    dialog.setCancelable(true);


                    final Spinner spLearningCardQuery = dialog.findViewById(R.id.spLearningCardQueries);
                    final ArrayAdapter<LearningCardQuery> learningCardQueries = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, MainActivity.globals.getSqLite().getLearningCardQueries(""));
                    spLearningCardQuery.setAdapter(learningCardQueries);
                    learningCardQueries.notifyDataSetChanged();

                    final Button btnStartSop = dialog.findViewById(R.id.cmdStart);
                    btnStartSop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cmdLearningCardQueryStart.setText(getString(R.string.learningCard_query_end));
                            fragmentAdapter.setQuery(learningCardQueries.getItem(spLearningCardQuery.getSelectedItemPosition()));
                            viewPager.setAdapter(fragmentAdapter);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    cmdLearningCardQueryStart.setText(getString(R.string.learningCard_query));
                    fragmentAdapter.setQuery(null);
                    viewPager.setAdapter(fragmentAdapter);
                }
            }
        });
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
                        startActivityForResult(new Intent(getApplicationContext(), LearningCardQueryActivity.class), 99);
                        break;
                }
                return false;
            }
        };

        BottomNavigationView navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(listener);

        this.cmdLearningCardQueryStart = this.findViewById(R.id.cmdLearningCardQueryStart);

        this.fragmentAdapter = new LearningCardQueryFragmentAdapter(this.getSupportFragmentManager(), this.getApplicationContext(), null);
        this.viewPager = this.findViewById(R.id.pager);
        this.viewPager.setAdapter(this.fragmentAdapter);
    }
}
