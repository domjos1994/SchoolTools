package de.domjos.schooltools.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import de.domjos.schooltools.R;

public class DescriptionActivity extends AppCompatActivity {
    private TextView lblHeader, lblContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description_activity);
        this.initControls();

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            if(bundle.containsKey("header") && bundle.containsKey("content")) {
                this.setTitle(bundle.getString("header"));
                this.lblHeader.setText(bundle.getString("header"));
                this.lblContent.setText(bundle.getString("content"));
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void initControls() {
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(this.getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.lblHeader = this.findViewById(R.id.lblHeader);
        this.lblContent = this.findViewById(R.id.lblContent);
    }
}
