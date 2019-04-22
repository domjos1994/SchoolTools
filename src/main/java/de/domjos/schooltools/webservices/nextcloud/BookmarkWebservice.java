/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.webservices.nextcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltools.core.model.Bookmark;

public class BookmarkWebservice extends AsyncTask<URL, Void, String> {
    private HttpURLConnection httpURLConnection;
    private ProgressDialog dialog;

    public BookmarkWebservice(Context context) {
        this.dialog = new ProgressDialog(context);
    }

    public List<Bookmark> getData() throws Exception {
        List<Bookmark> bookmarks = new LinkedList<>();
        StringBuilder result = new StringBuilder();
        if (this.httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            InputStream in = new BufferedInputStream(this.httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JSONObject obj = new JSONObject(result.toString());
            String state = obj.getString("status");
            if(state.trim().toLowerCase().equals("success")) {
                JSONArray data = obj.getJSONArray("data");
                for(int i = 0; i<=data.length()-1;i++) {
                    JSONObject currentBookmark = data.getJSONObject(i);
                    Bookmark bookmark = new Bookmark();
                    bookmark.setTitle(currentBookmark.getString("title"));
                    bookmark.setLink(currentBookmark.getString("url"));
                    bookmark.setDescription(currentBookmark.getString("description"));
                    JSONArray tags = currentBookmark.getJSONArray("tags");
                    StringBuilder tagsBuilder = new StringBuilder();
                    for(int j = 0; j<=tags.length()-1; j++) {
                        tagsBuilder.append(tags.getString(j));
                        tagsBuilder.append("; ");
                    }
                    bookmark.setTags(tagsBuilder.toString());
                    bookmarks.add(bookmark);
                }
            }
        }
        return bookmarks;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setTitle(R.string.app_name);
        this.dialog.setMessage("Retrieving data...");
        this.dialog.show();
    }

    @Override
    protected String doInBackground(URL... urls){
        String url = MainActivity.globals.getUserSettings().getNextCloudHost();
        String user = MainActivity.globals.getUserSettings().getNextCloudUser();
        String pwd = MainActivity.globals.getUserSettings().getNextCloudPwd();

        try {
            this.connect(url, user, pwd, true);
            List<Bookmark> bookmarks = this.getData();
            List<Bookmark> savedBookmarks = MainActivity.globals.getSqLite().getBookmarks("");


            for(Bookmark bookmark : bookmarks) {
                boolean alreadyAvailable = false;
                for(Bookmark savedBookmark : savedBookmarks) {
                    if(bookmark.getTitle().trim().toLowerCase().equals(savedBookmark.getTitle().trim().toLowerCase())) {
                        alreadyAvailable = true;
                        break;
                    }
                }

                if(!alreadyAvailable) {
                    MainActivity.globals.getSqLite().insertOrUpdateBookmark(bookmark);
                }
            }

            for(Bookmark savedBookmark : savedBookmarks) {
                boolean alreadyAvailable = false;
                for(Bookmark bookmark : bookmarks) {
                    if(bookmark.getTitle().trim().toLowerCase().equals(savedBookmark.getTitle().trim().toLowerCase())) {
                        alreadyAvailable = true;
                        break;
                    }
                }

                if(!alreadyAvailable) {
                    String relativeUrl = "/index.php/apps/bookmarks/public/rest/v2/bookmark" + createBookmarkParameter(savedBookmark);
                    this.send(url, user, pwd, relativeUrl);
                }
            }
        } catch (Exception ex) {
            return "";
        }

        return null;
    }

    private String connect(String host, final String user, final String pwd, boolean connect) throws Exception {
        URL url = new URL (host + "/index.php/apps/bookmarks/public/rest/v2/bookmark");
        this.httpURLConnection = (HttpURLConnection) url.openConnection();
        Authenticator.setDefault(new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user,pwd.toCharArray());
            }
        });

        this.httpURLConnection.setUseCaches(false);
        this.httpURLConnection.setRequestMethod("GET");
        if(connect) {
            this.httpURLConnection.connect();
            return this.httpURLConnection.getResponseMessage();
        } else {
            return "";
        }
    }

    private String send(String rootUrl, String user, String pwd, String relativeUrl) {
        BufferedReader in = null;
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection;
        try {
            URL url = new URL(rootUrl + relativeUrl);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Authorization", "Basic " + Base64.encodeToString((user + ":" + pwd).getBytes(), Base64.DEFAULT));
        } catch (Exception e) {
            return e.getMessage();
        }
        try {
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String inputLine;
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                connection.disconnect();
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        return "";
    }

    private String createBookmarkParameter(Bookmark bookmark) {
        if(!bookmark.getTitle().isEmpty() && !bookmark.getLink().startsWith("http")) {
            //tittle can only be set if the sheme is given
            //this is a bug we need to fix
            bookmark.setLink("http://" + bookmark.getLink());
        }

        String url = "?url=" + URLEncoder.encode(bookmark.getLink());

        if(!bookmark.getTitle().isEmpty()) {
            url += "&title=" + URLEncoder.encode(bookmark.getTitle());
        }
        if(!bookmark.getDescription().isEmpty()) {
            url += "&description=" + URLEncoder.encode(bookmark.getDescription());
        }

        for(String tag : bookmark.getTags().split(";")) {
            url += "&" + URLEncoder.encode("item[tags][]") + "=" + URLEncoder.encode(tag);
        }

        return url;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.dialog.dismiss();
    }
}
