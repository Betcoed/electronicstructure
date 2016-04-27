package com.sciencehighgames.electronicstructure;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Created by sarahhinsley on 07/04/2015.
 */
public class Level3Choices extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.level3choices);
       // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title_level3);
    }


    public void onChooseEasyLevel3(View view) {
        Intent getStartValue3 = new Intent(this, Level3EasyChoices.class);

        final int startValue = 31;

        getStartValue3.putExtra("passStartValue", startValue);

        startActivity(getStartValue3);
    }


    public void onChooseMediumLevel3(View view) {

        Intent getStartValue3 = new Intent(this, Level3MediumChoices.class);

        final int startValue = 33;

        getStartValue3.putExtra("passStartValue", startValue);

        startActivity(getStartValue3);
    }

    public void onChooseHardLevel3(View view) {
        Intent getStartValue = new Intent(this, ShellsScreen.class);

        final int startValue = 35;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseChallengeLevel3(View view) {
        Intent getStartValue = new Intent(this, NumberForm.class);

        final int startValue = 36;

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }
}
