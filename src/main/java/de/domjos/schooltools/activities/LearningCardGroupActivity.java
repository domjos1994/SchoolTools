/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.learningCard.LearningCardGroup;
import de.domjos.schooltools.core.model.objects.BaseDescriptionObject;
import de.domjos.schooltools.custom.AbstractActivity;
import de.domjos.schooltools.custom.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.Helper;

public final class LearningCardGroupActivity extends AbstractActivity {
    private FloatingActionButton cmdLearningCardGroupAdd;
    private SwipeRefreshDeleteList lvLearnCardGroups;

    public LearningCardGroupActivity() {
        super(R.layout.learning_card_group_activity);
    }

    @Override
    protected void initActions() {
        this.cmdLearningCardGroupAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LearningCardGroupEntryActivity.class);
            intent.putExtra("ID", 0);
            startActivityForResult(intent, 99);
        });

        this.lvLearnCardGroups.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                Intent intent = new Intent(getApplicationContext(), LearningCardGroupEntryActivity.class);
                LearningCardGroup group = (LearningCardGroup) listObject;
                if(group!=null) {
                    intent.putExtra("ID", group.getID());
                }
                startActivityForResult(intent, 99);
            }
        });

        this.lvLearnCardGroups.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                LearningCardGroup group = (LearningCardGroup) listObject;

                if(group!=null) {
                    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                MainActivity.globals.getSqLite().deleteEntry("learningCardGroups", "ID=" + group.getID());
                                reloadGroups();
                                dialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(LearningCardGroupActivity.this, R.style.AlertDialogTheme);
                    builder.setMessage(R.string.learningCard_group_delete_dialog);
                    builder.setPositiveButton(R.string.learningCard_group_delete_dialog_yes, dialogClickListener);
                    builder.setNegativeButton(R.string.learningCard_group_delete_dialog_no, dialogClickListener);
                    builder.show();
                }
            }
        });

        this.lvLearnCardGroups.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reloadGroups();
            }
        });
    }

    @Override
    protected void initControls() {
        this.cmdLearningCardGroupAdd = this.findViewById(R.id.cmdLearningCardGroupAdd);

        this.lvLearnCardGroups = this.findViewById(R.id.lvLearningCardGroups);

        this.reloadGroups();
        Helper.setBackgroundToActivity(this);
    }

    private void reloadGroups() {
        this.lvLearnCardGroups.getAdapter().clear();
        for(LearningCardGroup group : MainActivity.globals.getSqLite().getLearningCardGroups("", false)) {
            this.lvLearnCardGroups.getAdapter().add(group);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_only, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(Helper.showHelpMenu(item, this.getApplicationContext(), "help_learning_cards"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if(requestCode==99) {
                this.reloadGroups();
            }
        }
    }
}
