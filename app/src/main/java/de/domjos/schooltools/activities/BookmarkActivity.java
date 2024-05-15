/*
 * Copyright (C) 2017-2022  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.activities;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltoolslib.model.Bookmark;
import de.domjos.schooltoolslib.model.Subject;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.schooltools.helper.ApiHelper;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.IntentHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.tokenizer.CommaTokenizer;

public final class BookmarkActivity extends AbstractActivity {
    private BottomNavigationView navigation;
    private Spinner spBookmarksFilterSubject, spBookmarksSubject;
    private ArrayAdapter<Subject> bookmarksFilterSubjectAdapter, bookmarksSubjectAdapter;
    private SearchView searchView;

    private WebView wvPreview;
    private SwipeRefreshDeleteList lvBookmarks;

    private EditText txtBookmarkTitle, txtBookmarkThemes, txtBookmarkDescription, txtBookmarkLink;
    private MultiAutoCompleteTextView txtBookmarkTags;
    private CheckBox chkBookmarkImport;
    private ArrayAdapter<String> tagAdapter;
    private ImageButton cmdBookmarkLink, cmdBookmarkFile;
    private Bookmark currentBookmark = new Bookmark();
    private Validator validator;
    private long subjectID = 0;

    public BookmarkActivity() {
        super(R.layout.bookmark_activity);
    }

    @Override
    protected void initActions() {
        this.changeControls(false, true);
        this.getItemFromOutSide();
        Helper.setBackgroundToActivity(this);

        this.cmdBookmarkLink.setOnClickListener(v -> {
            Helper.closeSoftKeyboard(BookmarkActivity.this);
            wvPreview.loadUrl(txtBookmarkLink.getText().toString());
        });

        this.cmdBookmarkLink.setOnLongClickListener(v -> {
            openIntent(currentBookmark, BookmarkActivity.this);
            return false;
        });

        this.cmdBookmarkFile.setOnClickListener(v -> {
            File defaultDir = getApplicationContext().getFilesDir();
            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.root = new File(Objects.requireNonNull(new File(ApiHelper.findExistingFolder(BookmarkActivity.this)).getParent()));
            properties.error_dir = defaultDir;
            properties.offset = defaultDir;
            properties.extensions = new String[]{"pdf", "doc", "docx", "ppt", "pptx", "odp", "odt", "xls", "xlsx", "ods", "png", "jpg"};
            properties.selection_type = DialogConfigs.FILE_SELECT;
            FilePickerDialog dialog = new FilePickerDialog(BookmarkActivity.this, properties);
            dialog.setTitle(getString(R.string.bookmark_import));
            dialog.show();

            dialog.setDialogSelectionListener(files -> {
                if(files!=null) {
                    if(files.length==1) {
                        txtBookmarkLink.setText(files[0]);
                    }
                }
            });
        });

        txtBookmarkLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().endsWith("pdf")
                    || s.toString().endsWith("doc") || s.toString().endsWith("docx") || s.toString().endsWith("ods")
                    || s.toString().endsWith("ppt") || s.toString().endsWith("pptx") || s.toString().endsWith("odp")
                    || s.toString().endsWith("xls") || s.toString().endsWith("xlsx") || s.toString().endsWith("odt")
                    || s.toString().endsWith("jpg") || s.toString().endsWith("png")) {
                    chkBookmarkImport.setVisibility(View.VISIBLE);
                } else {
                    chkBookmarkImport.setVisibility(View.GONE);
                }
            }
        });

        this.wvPreview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                currentBookmark.setPreview(capturePreview());
            }
        });

        this.lvBookmarks.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener)  listObject -> {
            currentBookmark = (Bookmark) listObject;
            setFieldsFromObject();
        });

        this.lvBookmarks.setOnDeleteListener(listObject -> {
            if(currentBookmark.getId()!=0) {
                MainActivity.globals.getSqLite().deleteEntry("bookmarks", currentBookmark);
            }
            changeControls(false, true);
            reloadBookmarks("");
        });

        this.spBookmarksFilterSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = bookmarksFilterSubjectAdapter.getItem(position);
                if(subject!=null) {
                    subjectID = subject.getId();
                    reloadBookmarks(searchView.getQuery().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                reloadBookmarks(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        this.searchView.setOnCloseListener(() -> {
            reloadBookmarks("");
            return false;
        });
    }

    public static void openIntent(Bookmark bookmark, Activity activity) {
        String s = bookmark.getLink();
        if(s.endsWith("pdf") || s.endsWith("doc") || s.endsWith("docx") || s.endsWith("ods")
            || s.endsWith("ppt") || s.endsWith("pptx") || s.endsWith("odp") || s.endsWith("xls")
            || s.endsWith("xlsx") || s.endsWith("odt") || s.endsWith("jpg") || s.endsWith("png")) {

            File file = new File(s);

            boolean fileExists = file.exists();
            if(!file.exists())  {
                if(bookmark.getData()!=null) {
                    try {
                        if(Helper.checkPermissions(Helper.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            if(file.createNewFile()) {
                                FileOutputStream outputStream = new FileOutputStream(file);
                                outputStream.write(bookmark.getData());
                                outputStream.close();
                                fileExists = true;
                            }
                        }
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity);
                    }
                }
            }

            if(fileExists) {
                try {
                    IntentHelper.openFileViaIntent(file, activity);
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity);
                }
            }

        } else {
            try {
                IntentHelper.openWebBrowser(activity, s);
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity);
            }
        }
    }

    @Override
    protected void initControls() {
        // init Toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        // init home as up
        if(this.getSupportActionBar()!=null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // init other controls
        List<Subject> subjects = MainActivity.globals.getSqLite().getSubjects("");
        subjects.add(0, new Subject());
        int spinner_item = R.layout.spinner_item;

        this.searchView = this.findViewById(R.id.cmdSearch);

        this.txtBookmarkTags = this.findViewById(R.id.txtBookMarkTags);
        this.tagAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1);
        this.txtBookmarkTags.setAdapter(this.tagAdapter);
        this.txtBookmarkTags.setTokenizer(new CommaTokenizer());
        this.tagAdapter.notifyDataSetChanged();

        this.lvBookmarks = this.findViewById(R.id.lvBookmarks);
        this.lvBookmarks.setContextMenu(R.menu.ctx_bookmark);
        this.reloadBookmarks("");

        this.wvPreview = this.findViewById(R.id.wvPreview);
        this.cmdBookmarkLink = this.findViewById(R.id.cmdBookMarkLink);
        this.cmdBookmarkFile = this.findViewById(R.id.cmdBookMarkFile);

        this.spBookmarksFilterSubject = this.findViewById(R.id.spBookmarksFilterSubject);
        this.bookmarksFilterSubjectAdapter = new ArrayAdapter<>(this.getApplicationContext(), spinner_item, subjects);
        this.spBookmarksFilterSubject.setAdapter(this.bookmarksFilterSubjectAdapter);
        this.bookmarksFilterSubjectAdapter.notifyDataSetChanged();

        this.spBookmarksSubject = this.findViewById(R.id.spBookmarksSubject);
        this.bookmarksSubjectAdapter = new ArrayAdapter<>(this.getApplicationContext(), spinner_item, subjects);
        this.spBookmarksSubject.setAdapter(this.bookmarksSubjectAdapter);
        this.bookmarksSubjectAdapter.notifyDataSetChanged();

        this.txtBookmarkTitle = this.findViewById(R.id.txtBookMarkTitle);
        this.txtBookmarkThemes = this.findViewById(R.id.txtBookMarkThemes);
        this.txtBookmarkDescription = this.findViewById(R.id.txtBookMarkDescription);
        this.txtBookmarkLink = this.findViewById(R.id.txtBookMarkLink);
        this.chkBookmarkImport = this.findViewById(R.id.chkBookMarkImport);
        this.chkBookmarkImport.setVisibility(View.GONE);

        // init navigation
        this.navigation = this.findViewById(R.id.navigation);
        this.navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navTimeTableSubAdd:
                    currentBookmark = new Bookmark();
                    changeControls(true, true);
                    return true;
                case R.id.navTimeTableSubEdit:
                    changeControls(true, false);
                    return true;
                case R.id.navTimeTableSubDelete:
                    if(currentBookmark.getId()!=0) {
                        MainActivity.globals.getSqLite().deleteEntry("bookmarks", currentBookmark);
                    }
                    changeControls(false, true);
                    reloadBookmarks("");
                    return true;
                case R.id.navTimeTableSubCancel:
                    changeControls(false, true);
                    return true;
                case R.id.navTimeTableSubSave:
                    if(validator.getState()) {
                        try {
                            currentBookmark.setTitle(txtBookmarkTitle.getText().toString());
                            currentBookmark.setTags(txtBookmarkTags.getText().toString());
                            currentBookmark.setThemes(txtBookmarkThemes.getText().toString());
                            currentBookmark.setDescription(txtBookmarkDescription.getText().toString());
                            currentBookmark.setLink(txtBookmarkLink.getText().toString());
                            if(spBookmarksSubject.getSelectedItem()!=null) {
                                Subject subject = bookmarksSubjectAdapter.getItem(spBookmarksSubject.getSelectedItemPosition());
                                currentBookmark.setSubject(subject);
                            }
                            if(chkBookmarkImport.isChecked()) {
                                File importFile = new File(txtBookmarkLink.getText().toString());
                                FileInputStream fileInputStream = new FileInputStream(importFile);
                                byte[] fileContent = new byte[(int) importFile.length()];
                                fileInputStream.close();
                                currentBookmark.setData(fileContent);
                            }
                            MainActivity.globals.getSqLite().insertOrUpdateBookmark(currentBookmark);
                            changeControls(false, true);
                            reloadBookmarks("");
                            currentBookmark = new Bookmark();
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, BookmarkActivity.this);
                        }
                    }
                    return true;
            }
            return false;
        });
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(BookmarkActivity.this, R.mipmap.ic_launcher_round);
        this.validator.addEmptyValidator(this.txtBookmarkTitle);
        this.validator.addEmptyValidator(this.txtBookmarkLink);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menBookmarkOpen) {
            BaseDescriptionObject baseDescriptionObject = this.lvBookmarks.getAdapter().getObject();
            if(baseDescriptionObject!=null) {
                if(baseDescriptionObject instanceof Bookmark) {
                    openIntent((Bookmark) baseDescriptionObject, BookmarkActivity.this);
                }
            }

        }

        return super.onContextItemSelected(item);
    }

    private void changeControls(boolean editMode, boolean reset) {
        this.searchView.setEnabled(!editMode);

        this.spBookmarksFilterSubject.setEnabled(!editMode);
        if(editMode) {
            this.lvBookmarks.setVisibility(View.GONE);
            this.wvPreview.setVisibility(View.VISIBLE);
        } else {
            this.lvBookmarks.setVisibility(View.VISIBLE);
            this.wvPreview.setVisibility(View.GONE);
        }

        this.txtBookmarkTitle.setEnabled(editMode);
        this.txtBookmarkTags.setEnabled(editMode);
        this.txtBookmarkThemes.setEnabled(editMode);
        this.txtBookmarkDescription.setEnabled(editMode);
        this.txtBookmarkLink.setEnabled(editMode);
        this.spBookmarksSubject.setEnabled(editMode);
        this.cmdBookmarkLink.setEnabled(editMode);
        this.cmdBookmarkFile.setEnabled(editMode);
        this.chkBookmarkImport.setEnabled(editMode);

        this.navigation.getMenu().getItem(0).setVisible(!editMode);
        this.navigation.getMenu().getItem(1).setVisible(!editMode);
        this.navigation.getMenu().getItem(2).setVisible(!editMode);
        this.navigation.getMenu().getItem(3).setVisible(editMode);
        this.navigation.getMenu().getItem(4).setVisible(editMode);

        if(reset) {
            this.txtBookmarkTitle.setText("");
            this.txtBookmarkTags.setText("");
            this.txtBookmarkThemes.setText("");
            this.txtBookmarkDescription.setText("");
            this.txtBookmarkLink.setText("");
            this.spBookmarksSubject.setSelection(-1);
            this.chkBookmarkImport.setChecked(false);
        }
    }

    private void reloadBookmarks(String search) {
        List<String> tags = new LinkedList<>();
        this.lvBookmarks.getAdapter().clear();
        for(Bookmark bookmark : MainActivity.globals.getSqLite().getBookmarks("")) {
            getTagsFromBookmark(bookmark, tags);

            if(!search.trim().isEmpty()) {
                if(!bookmark.getTags().trim().toLowerCase().contains(search.toLowerCase())) {
                    if(!bookmark.getThemes().toLowerCase().trim().contains(search.toLowerCase())) {
                        if(!bookmark.getDescription().toLowerCase().trim().contains(search.toLowerCase())) {
                            if(!bookmark.getTitle().toLowerCase().trim().contains(search.toLowerCase())) {
                                continue;
                            }
                        }
                    }
                }
            }

            if(this.subjectID!=0) {
                if(bookmark.getSubject()!=null) {
                    if(bookmark.getSubject().getId()!=this.subjectID) {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            this.lvBookmarks.getAdapter().add(bookmark);
        }

        this.tagAdapter.clear();
        for(String tag : tags) {
            this.tagAdapter.add(tag);
        }
    }

    private void getTagsFromBookmark(Bookmark bookmark, List<String> tags) {
        if(bookmark.getTags().trim().contains(";")) {
            for(String tag : bookmark.getTags().trim().split(";")) {
                if(!tags.contains(tag.trim())) {
                    tags.add(tag.trim());
                }
            }
        } else if(bookmark.getTags().trim().contains(",")) {
            for(String tag : bookmark.getTags().trim().split(",")) {
                if(!tags.contains(tag.trim())) {
                    tags.add(tag.trim());
                }
            }
        } else {
            if(!tags.contains(bookmark.getTags().trim())) {
                tags.add(bookmark.getTags().trim());
            }
        }
    }

    private byte[] capturePreview() {
        try {
            Bitmap bm = Bitmap.createBitmap(this.wvPreview.getMeasuredWidth(), this.wvPreview.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            Paint paint = new Paint();
            int iHeight = bm.getHeight();
            canvas.drawBitmap(bm, 0, iHeight, paint);
            this.wvPreview.draw(canvas);

            ByteArrayOutputStream fOut = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 50, fOut);
            fOut.flush();
            fOut.close();
            bm.recycle();
            return fOut.toByteArray();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, BookmarkActivity.this);
        }
        return null;
    }

    private void getItemFromOutSide() {
        if(this.getIntent()!=null) {
            int id = this.getIntent().getIntExtra("id", 0);

            if(id != 0) {
                List<Bookmark> bookmarks = MainActivity.globals.getSqLite().getBookmarks("ID=" + id);
                if(bookmarks!=null) {
                    if(!bookmarks.isEmpty()) {
                        this.currentBookmark = bookmarks.get(0);
                        this.changeControls(true, false);
                        this.setFieldsFromObject();
                    }
                }
            }
        }
    }

    private void setFieldsFromObject() {
        if(currentBookmark!=null) {
            txtBookmarkTitle.setText(currentBookmark.getTitle());
            txtBookmarkTags.setText(currentBookmark.getTags());
            txtBookmarkThemes.setText(currentBookmark.getThemes());
            txtBookmarkDescription.setText(currentBookmark.getDescription());
            txtBookmarkLink.setText(currentBookmark.getLink());
            chkBookmarkImport.setChecked(currentBookmark.getData()!=null);
            if(currentBookmark.getSubject()!=null) {
                spBookmarksSubject.setSelection(bookmarksSubjectAdapter.getPosition(currentBookmark.getSubject()));
            }
        }
    }
}
