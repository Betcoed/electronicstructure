package com.sciencehighgames.electronicstructure;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Created by sarahhinsley on 16/04/2015.
 */
public class Level3MediumChoices extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.level3mediumchoices);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_mediumlevel3);
    }

    public void onChooseEasierMedium(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 33;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseHarderMedium(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 34;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }
}
