package com.sciencehighgames.electronicstructure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.app.Activity;

/**
 * Created by sarahhinsley on 13/04/2015.
 */
public class ElecStructsOnlyEasy extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.elecstructsonly_easy);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_easylevel1);
    }

    public void onChooseVeryEasyLevel1(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 11;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseALittleHarderLevel1(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 12;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }
}
