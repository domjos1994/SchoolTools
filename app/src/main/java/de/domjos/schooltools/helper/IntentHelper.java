package de.domjos.schooltools.helper;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import android.webkit.MimeTypeMap;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;

import java.io.File;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.ApiActivity;

public class IntentHelper {

    public static void openWebBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
        intent.putExtra(SearchManager.QUERY, "Karlos");
        context.startActivity(intent);
    }

    public static void openFileViaIntent(File file, Activity activity) {
        try {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            String content = IntentHelper.fileExt(file.getAbsolutePath());
            String mimeType = myMime.getMimeTypeFromExtension(content);

            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".helper.SchoolToolsFileProvider", file);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(file.getAbsolutePath()), mimeType);
                intent = Intent.createChooser(intent, "Open File");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            activity.startActivity(intent);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity);
        }
    }

    public static Intent openFileViaIntent(File file, Context context) {
        try {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            String content = IntentHelper.fileExt(file.getAbsolutePath());
            String mimeType = myMime.getMimeTypeFromExtension(content);

            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".helper.SchoolToolsFileProvider", file);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(file.getAbsolutePath()), mimeType);
                intent = Intent.createChooser(intent, "Open File");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            return intent;
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, context);
        }
        return null;
    }

    private static String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
}
