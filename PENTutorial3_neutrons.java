package com.sciencehighgames.electronicstructure;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by sarahhinsley on 23/04/2015.
 */
public class PENTutorial3_neutrons extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pen_tutorial3_neutrons);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pen_tutorial2_actionbar, menu);
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
            finish();
            Intent getMainActivity = new Intent(PENTutorial3_neutrons.this, MainActivity.class);
            getMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(getMainActivity);
        }

        return super.onOptionsItemSelected(item);
    }
}
