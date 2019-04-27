package de.domjos.schooltools.services;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;

public class ImportToDictionaryTask extends AsyncTask<Void, Void, Boolean> {
    private Timer timer;
    private int max = 0, current = 0;
    private static final int NOTIFY_ID = 999;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private final Activity activity;
    private String path;

    public ImportToDictionaryTask(Activity activity, String path) {
        this.builder = new NotificationCompat.Builder(activity, MainActivity.CHANNEL_ID);
        this.manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        this.builder.setSmallIcon(R.mipmap.ic_launcher);
        this.builder.build();
        this.activity = activity;
        this.path = path;
        this.timer = new Timer();
    }

    @Override
    protected void onPreExecute() {
        this.updateProgress(0, 0, false);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if(this.path.toLowerCase().trim().endsWith(".zip")) {
                String dir = this.activity.getFilesDir() + "/dict/";
                File directory = new File(dir);
                if(directory.exists()) {
                    this.deleteDirectory(directory);
                }
                if(directory.mkdirs()) {
                    this.unzipFile(this.path, dir);
                }
                String txtFile = MainActivity.globals.getUserSettings().getPathToDictionary();
                if(!txtFile.equals("")) {
                    this.readFileToDictionary(txtFile);
                }
                if(directory.exists()) {
                    this.deleteDirectory(directory);
                }
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        this.updateProgress(100, 100, true);
        manager.cancel(ImportToDictionaryTask.NOTIFY_ID);
    }


    private void unzipFile(String path, String dir) throws Exception {
        changeMessage(R.string.settings_school_learningCard_dictionary_path_message_unzipFile);
        byte[] buffer = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(path)));
        ZipEntry ze;
        int count;

        while ((ze = zipInputStream.getNextEntry()) != null) {
            String filename = ze.getName();
            if (ze.isDirectory()) {
                File fmd = new File(dir + filename);
                if(fmd.mkdirs()) {
                    continue;
                }
            }

            FileOutputStream fout = new FileOutputStream(dir + filename);

            while ((count = zipInputStream.read(buffer)) != -1) {
                fout.write(buffer, 0, count);
            }

            fout.close();
            zipInputStream.closeEntry();
        }
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        changeMessage(R.string.settings_school_learningCard_dictionary_path_message_deleteDir);
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if(!deleteDirectory(file)) {
                    return false;
                }
            }
        }
        return directoryToBeDeleted.delete();
    }

    private void readFileToDictionary(String path) throws Exception {
        this.max = this.getMaxNumberOfRows(path);
        FileInputStream fileInputStream = new FileInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line = bufferedReader.readLine();

        // read language
        String motherLang = "", foreignLang = "";
        if(line.startsWith("# ")) {
            String langPart = line.substring(1, 8);
            String[] lang = langPart.trim().split("-");
            motherLang = lang[0].trim();
            foreignLang = lang[1].trim();
        }

        this.current = 1;
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateProgress(max, current, true);
                changeMessage(String.format(activity.getString(R.string.settings_school_learningCard_dictionary_path_message_readLine), current, max));
            }
        }, 0, 10000);
        MainActivity.globals.getSqLite().deleteEntry("dict", "motherLanguage='" + motherLang + "' AND foreignLanguage='" + foreignLang + "'");
        if(!motherLang.equals("") && !foreignLang.equals("")) {
            while (line != null) {
                if(!line.startsWith("# ") && !line.trim().isEmpty()) {
                    String[] dictLine = line.split("\t");
                    String motherItem = dictLine[0].trim();
                    String foreignItem = dictLine[1].trim();
                    MainActivity.globals.getSqLite().insertToDictionary(motherLang, motherItem, foreignLang, foreignItem);
                }
                line = bufferedReader.readLine();
                this.current++;
            }
        }
        this.timer.cancel();
    }

    private int getMaxNumberOfRows(String path) throws Exception {
        FileInputStream stream = new FileInputStream(path);
        byte[] buffer = new byte[8192];
        int count = 0;
        int n;
        while ((n = stream.read(buffer)) > 0) {
            for (int i = 0; i < n; i++) {
                if (buffer[i] == '\n') count++;
            }
        }
        stream.close();
        return count;
    }

    private void changeMessage(final String message) {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.setContentText(message);
                manager.notify(ImportToDictionaryTask.NOTIFY_ID, builder.build());
            }
        });
    }

    private void changeMessage(final int id) {
        this.changeMessage(this.activity.getString(id));
    }

    private void updateProgress(final int max, final int current, final boolean fixedDuration) {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.setProgress(max, current, !fixedDuration);
                manager.notify(ImportToDictionaryTask.NOTIFY_ID, builder.build());
            }
        });
    }
}
