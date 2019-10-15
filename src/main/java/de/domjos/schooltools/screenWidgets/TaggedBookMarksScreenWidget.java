/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.screenWidgets;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.BookmarkActivity;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.adapter.ScreenWidgetAdapter;
import de.domjos.schooltools.core.model.Bookmark;
import de.domjos.schooltools.custom.CommaTokenizer;
import de.domjos.schooltools.custom.ScreenWidget;

public final class TaggedBookMarksScreenWidget extends ScreenWidget {
    private ScreenWidgetAdapter screenWidgetAdapter;

    public TaggedBookMarksScreenWidget(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    public void init() {
        this.screenWidgetAdapter = new ScreenWidgetAdapter(super.activity.getApplicationContext(), R.drawable.ic_bookmark_black_24dp, new ArrayList<>());
        for(Bookmark bookmark : MainActivity.globals.getSqLite().getBookmarks("")) {
            this.screenWidgetAdapter.add(bookmark);
        }

        List<String> tags = new LinkedList<>();
        for(int i = 0; i<=this.screenWidgetAdapter.getCount()-1; i++) {
            Bookmark bookmark = (Bookmark) this.screenWidgetAdapter.getItem(i);
            if(bookmark!=null) {
                String tagString = bookmark.getTags();
                if(tagString!=null) {
                    if(tagString.contains(";")) {
                        for(String tagItem : tagString.split(";")) {
                            tags.add(tagItem.trim());
                        }
                    } else if(tagString.contains(",")) {
                        for(String tagItem : tagString.split(",")) {
                            tags.add(tagItem.trim());
                        }
                    } else {
                        tags.add(tagString.trim());
                    }
                }
            }
        }

        MultiAutoCompleteTextView txtTaggedBookMarksTags = view.findViewById(R.id.txtTaggedBookMarksTags);
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(super.activity.getApplicationContext(), android.R.layout.simple_list_item_1, tags);
        txtTaggedBookMarksTags.setTokenizer(new CommaTokenizer());
        txtTaggedBookMarksTags.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();

        ListView lvTaggedBookMarks = view.findViewById(R.id.lvTaggedBookMarks);
        lvTaggedBookMarks.setAdapter(this.screenWidgetAdapter);
        this.screenWidgetAdapter.notifyDataSetChanged();

        txtTaggedBookMarksTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().trim().equals("")) {
                    List<String> writtenTags = new LinkedList<>();
                    if(s.toString().contains(";")) {
                        for(String item : s.toString().split(";")) {
                            writtenTags.add(item.trim().toLowerCase());
                        }
                    } else if(s.toString().contains(",")) {
                        for(String item : s.toString().split(",")) {
                            writtenTags.add(item.trim().toLowerCase());
                        }
                    } else {
                        writtenTags.add(s.toString().trim().toLowerCase());
                    }

                    screenWidgetAdapter.clear();
                    for(Bookmark bookmark : MainActivity.globals.getSqLite().getBookmarks("")) {
                        String tags = bookmark.getTags().trim();
                        List<String> tagList = new LinkedList<>();
                        if(tags.contains(";")) {
                            for(String item : tags.split(";")) {
                                tagList.add(item.trim().toLowerCase());
                            }
                        } else if(tags.contains(",")) {
                            for(String item : tags.split(",")) {
                                tagList.add(item.trim().toLowerCase());
                            }
                        } else {
                            tagList.add(tags.trim().toLowerCase());
                        }

                        Map<String, Boolean> tagMap = new LinkedHashMap<>();
                        for(String writtenTag : writtenTags) {
                            tagMap.put(writtenTag, tagList.contains(writtenTag));
                        }

                        boolean dontAdd = false;
                        for(String writtenTag : writtenTags) {
                            Boolean tmpState = tagMap.get(writtenTag);
                            if(tmpState!=null) {
                                if(!tmpState) {
                                    dontAdd = true;
                                    break;
                                }
                            }
                        }

                        if(!dontAdd) {
                            screenWidgetAdapter.add(bookmark);
                        }
                    }
                } else {
                    screenWidgetAdapter.clear();
                    for(Bookmark bookmark : MainActivity.globals.getSqLite().getBookmarks("")) {
                        screenWidgetAdapter.add(bookmark);
                    }
                }
            }
        });

        lvTaggedBookMarks.setOnItemLongClickListener((parent, view, position, id) -> {
            Bookmark bookmark = (Bookmark) screenWidgetAdapter.getItem(position);
            if(bookmark!=null) {
                BookmarkActivity.openIntent(bookmark, TaggedBookMarksScreenWidget.super.activity);
            }
            return false;
        });

        lvTaggedBookMarks.setOnItemClickListener((parent, view, position, id) -> {
            Bookmark bookmark = (Bookmark) screenWidgetAdapter.getItem(position);
            if(bookmark!=null) {
                Intent intent = new Intent(activity.getApplicationContext(), BookmarkActivity.class);
                intent.putExtra("id", bookmark.getID());
                activity.startActivity(intent);
            }
        });
    }
}
