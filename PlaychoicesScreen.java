package com.sciencehighgames.electronicstructure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sciencehighgames.electronicstructure.free.Constants;


/**
 * Created by sarahhinsley on 09/01/2015.
 */
public class PlaychoicesScreen extends ActionBarActivity {

    private AdView adView;
    private static final String AD_UNIT_ID = "ca-app-pub-4754286916525017/1614067886";
    private static final String DEVICE_ID = "e0182dbca392df78";

    public float startValue1, startValue2, startValue3, startValue4;
    public ConcirclesView shellsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.playchoices);
/*
        if (Constants.type == Constants.Type.FREE) {

            adView = (AdView) this.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(DEVICE_ID)
                    .build();
            adView.loadAd(adRequest);
        }
        */

        shellsView = (ConcirclesView) findViewById(R.id.shellsarea);
        startValue1 = getResources().getInteger(R.integer.start_value_lvl1);
        startValue2 = getResources().getInteger(R.integer.start_value_lvl2);
        startValue3 = getResources().getInteger(R.integer.start_value_lvl3);
        startValue4 = getResources().getInteger(R.integer.start_value_lvl4);


    }


    public void onChooseLevel1(View view) {

        Intent getStartValue = new Intent(this, Level1Choices.class);

        final int startValue = 1;
        //shellsView.getPlayChoice();

        getStartValue.putExtra("passStartValue", startValue);

        startActivity(getStartValue);
    }

    public void onChooseLevel2(View view) {

      Intent getStartValue2 = new Intent(this, Level2Choices.class);

        final int startValue = 2;
        //shellsView.setPlayChoice(startValue);

     getStartValue2.putExtra("passStartValue", startValue);

      startActivity(getStartValue2);


    }

    public void onChooseLevel3(View view) {

        Intent getStartValue3 = new Intent(this, Level3Choices.class);

        final int startValue = 3;

        getStartValue3.putExtra("passStartValue", startValue);

        startActivity(getStartValue3);
    }
}



