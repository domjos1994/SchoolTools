package de.domjos.schooltools.widgets.main;

import android.app.Activity;
import android.view.View;

public abstract class ScreenWidget {
    protected View view;
    protected Activity activity;

    ScreenWidget(View view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    public void setVisibility(boolean visibility) {
        if(visibility) {
            this.view.setVisibility(View.VISIBLE);
        } else {
            this.view.setVisibility(View.GONE);
        }
    }

    public abstract void init();
}
