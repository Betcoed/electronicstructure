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
public class ElecStructsOnlyMedium extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.elecstructsonly_medium);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_mediumlevel1);
    }

    public void onChooseEasierMedium(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 13;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseHarderMedium(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 14;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }
}
