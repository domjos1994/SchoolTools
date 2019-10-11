/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.Intent;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import de.domjos.schooltools.R;
import de.domjos.schooltools.adapter.LearningCardGroupAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.helper.Helper;

import java.util.ArrayList;

public final class LearningCardGroupActivity extends AbstractActivity {
    private FloatingActionButton cmdLearningCardGroupAdd;
    private ListView lvLearnCardGroups;
    private LearningCardGroupAdapter learningCardGroupAdapter;

    public LearningCardGroupActivity() {
        super(R.layout.learning_card_group_activity);
    }

    @Override
    protected void initActions() {
        this.cmdLearningCardGroupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LearningCardGroupEntryActivity.class);
                intent.putExtra("ID", 0);
                startActivityForResult(intent, 99);
            }
        });

        this.lvLearnCardGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), LearningCardGroupEntryActivity.class);
                LearningCardGroup group = learningCardGroupAdapter.getItem(position);
                if(group!=null) {
                    intent.putExtra("ID", group.getID());
                }
                startActivityForResult(intent, 99);
            }
        });
    }

    @Override
    protected void initControls() {
        this.cmdLearningCardGroupAdd = this.findViewById(R.id.cmdLearningCardGroupAdd);

        this.learningCardGroupAdapter = new LearningCardGroupAdapter(this.getApplicationContext(), R.layout.learning_card_group_item, new ArrayList<LearningCardGroup>());
        this.lvLearnCardGroups = this.findViewById(R.id.lvLearningCardGroups);
        this.lvLearnCardGroups.setAdapter(this.learningCardGroupAdapter);
        this.learningCardGroupAdapter.notifyDataSetChanged();

        this.reloadGroups();
        Helper.setBackgroundToActivity(this);
    }

    private void reloadGroups() {
        this.learningCardGroupAdapter.clear();
        for(LearningCardGroup group : MainActivity.globals.getSqLite().getLearningCardGroups("", false)) {
            this.learningCardGroupAdapter.add(group);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_learning_cards"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK) {
            if(requestCode==99) {
                this.reloadGroups();
            }
        }
    }
}
