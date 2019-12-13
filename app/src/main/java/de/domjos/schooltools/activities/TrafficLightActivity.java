package de.domjos.schooltools.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Timer;
import java.util.TimerTask;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Helper;

public final class TrafficLightActivity extends AbstractActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean permissionToRecordAccepted = false;

    private int currentState = 0;
    private boolean startRecording = false;

    private AppCompatImageView ivTrafficLights;
    private BottomNavigationView navigation;
    private GraphView graphView;
    private EditText txtTrafficLightsOrange, txtTrafficLightsRed, txtTrafficlightsMaximum;

    private MediaRecorder mediaRecorder;
    private Timer timer;

    private int currentPoint = 0;
    private LineGraphSeries<DataPoint> redLine = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> orangeLine = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> valueLine = new LineGraphSeries<>();

    private static int ORANGE_BORDER = 45;
    private static int RED_BORDER = 60;
    private static int MAXIMUM = 100;

    private int notification_id = -1;

    public TrafficLightActivity() {
        super(R.layout.traffic_light_activity, MainActivity.globals.getSqLite().getSetting("background"), R.drawable.bg_water);
    }

    @Override
    protected void initActions() {

        this.ivTrafficLights.setOnClickListener(view -> {
            if(!this.startRecording) {
                if(this.currentState==2) {
                    this.currentState = 0;
                } else {
                    this.currentState++;
                }
                this.updateTrafficLight();
            }
        });
    }

    @Override
    protected void initControls() {
        this.navigation = findViewById(R.id.navigation);
        BottomNavigationView.OnNavigationItemSelectedListener listener = menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navTrafficLightsStart:
                    this.startRecording = true;
                    this.navigation.getMenu().findItem(R.id.navTrafficLightsStart).setVisible(false);
                    this.navigation.getMenu().findItem(R.id.navTrafficLightsStop).setVisible(true);
                    this.startRecording();
                    break;
                case R.id.navTrafficLightsStop:
                    this.startRecording = false;
                    this.navigation.getMenu().findItem(R.id.navTrafficLightsStart).setVisible(true);
                    this.navigation.getMenu().findItem(R.id.navTrafficLightsStop).setVisible(false);
                    this.stopRecording();
                    break;
                case R.id.navTrafficLightsExpand:
                    if(this.graphView.getVisibility()==View.GONE) {
                        this.graphView.setVisibility(View.VISIBLE);
                    } else {
                        this.graphView.setVisibility(View.GONE);
                    }
                    break;
            }
            return true;
        };
        this.navigation.setOnNavigationItemSelectedListener(listener);
        this.navigation.getMenu().findItem(R.id.navTrafficLightsStop).setVisible(false);

        this.ivTrafficLights = this.findViewById(R.id.ivTrafficLights);
        this.mediaRecorder = new MediaRecorder();

        this.graphView = this.findViewById(R.id.graph);
        this.graphView.setVisibility(View.GONE);
        this.txtTrafficLightsOrange = this.findViewById(R.id.txtTrafficLightsOrange);
        this.txtTrafficLightsRed = this.findViewById(R.id.txtTrafficLightsRed);
        this.txtTrafficlightsMaximum = this.findViewById(R.id.txtTrafficLightsMaximum);

        this.iniDefault();

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    protected void onDestroy() {
        this.removeNotification();
        this.stopRecording();
        super.onDestroy();
    }



    private void stopRecording() {
        if(this.timer!=null) {
            this.timer.cancel();
            this.timer = null;
            this.currentPoint = 0;
            this.startRecording = false;
            this.mediaRecorder.stop();

            this.txtTrafficLightsRed.setEnabled(true);
            this.txtTrafficLightsOrange.setEnabled(true);
            this.txtTrafficlightsMaximum.setEnabled(true);
            this.iniDefault();
        }
    }

    private void startRecording() {
        try {
            if(!this.txtTrafficLightsOrange.getText().toString().trim().isEmpty()) {
                TrafficLightActivity.ORANGE_BORDER = Integer.parseInt(this.txtTrafficLightsOrange.getText().toString().trim());
            }
            if(!this.txtTrafficLightsRed.getText().toString().trim().isEmpty()) {
                TrafficLightActivity.RED_BORDER = Integer.parseInt(this.txtTrafficLightsRed.getText().toString().trim());
            }
            if(!this.txtTrafficlightsMaximum.getText().toString().trim().isEmpty()) {
                TrafficLightActivity.MAXIMUM = Integer.parseInt(this.txtTrafficlightsMaximum.getText().toString().trim());
            }
            this.iniDefault();

            this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            this.mediaRecorder.setOutputFile("/dev/null");
            this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            this.mediaRecorder.prepare();
            this.mediaRecorder.start();

            this.txtTrafficLightsRed.setEnabled(false);
            this.txtTrafficLightsOrange.setEnabled(false);
            this.txtTrafficlightsMaximum.setEnabled(false);

            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    double amp = getAmplitude();
                    double db = 60 + (20 * Math.log10(amp));

                    if(db < TrafficLightActivity.ORANGE_BORDER) {
                        currentState = 2;
                    } else if(db < TrafficLightActivity.RED_BORDER) {
                        currentState = 1;
                    } else {
                        currentState = 0;
                    }

                    TrafficLightActivity.this.runOnUiThread(()->{
                        updateTrafficLight();
                        updateGraphView(db);
                    });
                }
            }, 0, 1000);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, TrafficLightActivity.this);
        }
    }

    private double getAmplitude() {
        try {
            return this.mediaRecorder.getMaxAmplitude() / 2700.0;
        } catch (Exception ignored) {}
        return 0.0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted ) {
            this.navigation.setVisibility(View.GONE);
        }
    }


    private void updateTrafficLight() {
        int resId;
        switch (this.currentState) {
            case 0:
                resId = R.drawable.ic_traffic_black_red;
                this.showNotification();
                break;
            case 1:
                resId = R.drawable.ic_traffic_black_orange;
                this.removeNotification();
                break;
            default:
                resId = R.drawable.ic_traffic_black_green;
                this.removeNotification();
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.ivTrafficLights.setImageDrawable(this.getDrawable(resId));
        } else {
            this.ivTrafficLights.setImageDrawable(this.getResources().getDrawable(resId));
        }
    }

    private void iniDefault() {
        int color = Color.WHITE;

        this.graphView.setPadding(1, 1, 1, 1);
        this.graphView.getViewport().setYAxisBoundsManual(true);
        this.graphView.getViewport().setMinY(0);
        this.graphView.getViewport().setMaxY(TrafficLightActivity.MAXIMUM);

        this.graphView.getViewport().setXAxisBoundsManual(true);
        this.graphView.getViewport().setMinX(0);
        this.graphView.getViewport().setMaxX(this.currentPoint);

        this.graphView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.graphView.setDrawingCacheBackgroundColor(color);

        // format graphView
        GridLabelRenderer gridLabel = this.graphView.getGridLabelRenderer();
        gridLabel.setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        gridLabel.setHorizontalAxisTitle(this.getString(R.string.traffic_light_label_x));
        gridLabel.setHorizontalAxisTitleColor(color);
        gridLabel.setHorizontalLabelsColor(color);
        gridLabel.setVerticalAxisTitle(this.getString(R.string.traffic_light_label_y));
        gridLabel.setVerticalAxisTitleColor(color);
        gridLabel.setVerticalLabelsColor(color);
        gridLabel.setGridColor(color);

        this.graphView.removeAllSeries();
        this.redLine = new LineGraphSeries<>();
        this.orangeLine = new LineGraphSeries<>();
        this.valueLine = new LineGraphSeries<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.redLine.setColor(this.getColor(R.color.Red));
            this.orangeLine.setColor(this.getColor(R.color.Orange));
        } else {
            this.redLine.setColor(this.getResources().getColor(R.color.Red));
            this.orangeLine.setColor(this.getResources().getColor(R.color.Orange));
        }

        this.graphView.addSeries(this.redLine);
        this.graphView.addSeries(this.orangeLine);
        this.graphView.addSeries(this.valueLine);
    }

    private void updateGraphView(double data) {
        if(data==Double.POSITIVE_INFINITY || data == Double.NEGATIVE_INFINITY) {
            data = 0;
        }
        this.currentPoint++;
        this.graphView.getViewport().setMaxX(this.currentPoint);

        this.redLine.appendData(new DataPoint(this.currentPoint, TrafficLightActivity.RED_BORDER), true, this.currentPoint);
        this.orangeLine.appendData(new DataPoint(this.currentPoint, TrafficLightActivity.ORANGE_BORDER), true, this.currentPoint);
        this.valueLine.appendData(new DataPoint(this.currentPoint, data), true, this.currentPoint);
    }

    private void showNotification() {
        if(MainActivity.globals.getUserSettings().isNotificationsShown()) {
            if(this.notification_id==-1) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(TrafficLightActivity.this.getApplicationContext(), MainActivity.CHANNEL_ID);
                builder.setContentTitle(this.getString(R.string.traffic_light_notification_title));
                builder.setContentText(this.getString(R.string.traffic_light_notification_content));
                builder.setSmallIcon(R.mipmap.ic_launcher_round);

                NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
                if(notificationManager!=null) {
                    this.notification_id = 99;
                    notificationManager.notify(this.notification_id, builder.build());
                }
            }
        }
    }

    private void removeNotification() {
        if(this.notification_id!=-1) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            if(notificationManager!=null) {
                notificationManager.cancel(this.notification_id);
                this.notification_id = -1;
            }
        }
    }
}
