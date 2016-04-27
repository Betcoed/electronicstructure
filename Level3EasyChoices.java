package com.sciencehighgames.electronicstructure;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Created by sarahhinsley on 16/04/2015.
 */
public class Level3EasyChoices extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.level3easychoices);
      //  getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_easylevel3);


    }

    public void onChooseVeryEasyLevel1(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 31;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseALittleHarderLevel1(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 32;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }
}
