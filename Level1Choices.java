package com.sciencehighgames.electronicstructure;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.support.v7.app.ActionBarActivity;
/**
 * Created by sarahhinsley on 17/03/2015.
 */
public class Level1Choices extends ActionBarActivity {

    int fullVersionIsOwned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.level1choices);
        Intent getPurchase = getIntent();

        fullVersionIsOwned = getPurchase.getIntExtra("passStartValue", 0);
    }

    public void onChooseEasyLevel1(View view) {
        Intent getStartValues = new Intent(this, ElecStructsOnlyEasy.class);

        final int startValue = 11;

        getStartValues.putExtra("passStartValue", startValue);
        getStartValues.putExtra("purchased", fullVersionIsOwned);

        startActivity(getStartValues);
    }

    public void onChooseMediumlevel1(View view) {
        Intent getStartValues = new Intent(this, ElecStructsOnlyMedium.class);

        final int startValue = 13;

        getStartValues.putExtra("passStartValue", startValue);
        getStartValues.putExtra("purchased", fullVersionIsOwned);

        startActivity(getStartValues);
    }

    public void onChooseHardlevel1(View view) {
        Intent getStartValues = new Intent(this, ShellsScreen.class);

        final int startValue = 15;

        getStartValues.putExtra("passStartValue", startValue);
        getStartValues.putExtra("purchased", fullVersionIsOwned);

        startActivity(getStartValues);
    }

    public void onChooseChallengelevel1(View view) {
        Intent getStartValues = new Intent(this, NumberForm.class);

        final int startValue = 16;

        getStartValues.putExtra("passStartValue", startValue);
        getStartValues.putExtra("purchased", fullVersionIsOwned);
        startActivity(getStartValues);
    }
}


