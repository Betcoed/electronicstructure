package com.sciencehighgames.electronicstructure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuInflater;

/**
 * Created by sarahhinsley on 22/04/2015.
 */
public class PENTutorial1_Protons extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pen_tutorial1_protons);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pen_tutorial1_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_arrow) {
            Intent getPENTutorial2 = new Intent(PENTutorial1_Protons.this, PENTutorial2_electrons.class);
            startActivity(getPENTutorial2);
        }

        return super.onOptionsItemSelected(item);
    }

}
