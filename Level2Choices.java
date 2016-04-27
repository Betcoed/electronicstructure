package com.sciencehighgames.electronicstructure;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Created by sarahhinsley on 13/03/2015.
 */
public class Level2Choices extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.level2choices);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_level2);
    }

    public void onChooseEasyLevel2(View view) {
        Intent getStartValue = new Intent(this, PENScreen.class);

        final int startValue = 21;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseMediumLevel2(View view) {
        Intent getStartValue = new Intent(this, PENScreen.class);

        final int startValue = 22;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseHardLevel2(View view) {
        Intent getStartValue = new Intent(this, PENScreen.class);

        final int startValue = 23;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseChallengeLevel2(View view) {
        Intent getStartValue = new Intent(this, PENScreen.class);

        final int startValue = 24;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }
}


