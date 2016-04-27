package com.sciencehighgames.electronicstructure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by sarahhinsley on 22/04/2015.
 */
public class tutorialChoices extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tutorialchoices);
    }

    public void onChooseAtoms(View view) {
        finish();
        Intent atomsTutorial = new Intent(this, Tutorial_atoms1.class);

        startActivity(atomsTutorial);
    }

    public void onChooseIons(View view) {
        finish();
        Intent ionsTutorial = new Intent(this, Tutorial_Ions.class);

        startActivity(ionsTutorial);
    }

    public void onChoosePEN(View view) {
        finish();
        Intent PENTutorial = new Intent(this, PENTutorial1_Protons.class);

        startActivity(PENTutorial);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent getMainScreen = new Intent(tutorialChoices.this, MainActivity.class);
        getMainScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(getMainScreen);
    }
}
