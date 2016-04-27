package com.sciencehighgames.electronicstructure;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.app.Activity;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;

import com.sciencehighgames.electronicstructure.free.Constants;
import com.sciencehighgames.electronicstructure.util.IabHelper;
import com.sciencehighgames.electronicstructure.util.IabResult;
import com.sciencehighgames.electronicstructure.util.Inventory;
import com.sciencehighgames.electronicstructure.util.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    IInAppBillingService mService;
    int fullVersionIsOwned;
    IabHelper iabHelper;

    private AdView adView;
    private static final String AD_UNIT_ID = "ca-app-pub-4754286916525017/4707135088";
    private static final String DEVICE_ID = "e0182dbca392df78";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        /*
        if (Constants.type == Constants.Type.FREE) {
            Log.i("TAG", "FREE VERSION");
            adView = (AdView) this.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(DEVICE_ID)
                    .build();
            adView.loadAd(adRequest);
        } else {
            Log.i("TAG", "PAID VERSION");
        }
        */
        // DEBUG XXX
// We consume the item straight away so we can test multiple purchases

    //    int response = mService.consumePurchase(3, getPackageName(), token);
// END DEBUG


        }

    ServiceConnection mServiceConn =new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            checkOwnedItems();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void checkOwnedItems() {


        try
        {
        Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
        int response = ownedItems.getInt("RESPONSE_CODE");

            if (response == 0) {
                ArrayList<String> ownedSkus =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String>  purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String>  signatureList =
                        ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken =
                        ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);

                    // do something with this purchase information
                    // e.g. display the updated list of products owned by user
                    JSONObject jo = new JSONObject(purchaseData);
                    int purchaseState = jo.getInt("purchaseState");
                    System.out.println("purchaseState = " + purchaseState);
                }

                // if continuationToken != null, call getPurchases again
                // and pass in the token to retrieve more items
            }

    }
        catch(RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void onWindowFocusChanged (boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        int numberOfImageViews = 6;

        //final ImageView[] w = new ImageView[numberOfImageViews];
        final Button[] triAtom = new Button[2];
/*
        waters[0] = (ImageView) findViewById(R.id.water1);
        waters[1] = (ImageView) findViewById(R.id.water2);
        waters[2] = (ImageView) findViewById(R.id.water3);
        waters[3] = (ImageView) findViewById(R.id.water4);
        waters[4] = (ImageView) findViewById(R.id.water5);

        //animate images.
        //First set up an AnimationSet to put all the animations into
        AnimationSet move_waters = new AnimationSet(true);
        //set up the rotations for each image
        RotateAnimation rotate_w[] = new RotateAnimation[5];
        for (int i=0; i<5; i++) {
            rotate_w[i] = new RotateAnimation(0f, 300f, waters[i].getWidth()/2, waters[i].getHeight()/2);
            rotate_w[i].setStartOffset(50);
            rotate_w[i].setRepeatCount(Animation.INFINITE);
            rotate_w[i].setDuration(9500);
            move_waters.addAnimation(rotate_w[i]);
        }

        //set up the translations (across the screen movements)
        TranslateAnimation trans_waters[] =  new TranslateAnimation[5];
        trans_waters[0] =  new TranslateAnimation(Animation.ABSOLUTE, -50, Animation.ABSOLUTE, -10, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_PARENT, (float) 0.8);
        trans_waters[1] =  new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        trans_waters[2] =  new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_PARENT, -0.3f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        trans_waters[3] =  new TranslateAnimation(Animation.ABSOLUTE, 0.1f, Animation.ABSOLUTE, 0.3f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -0.9f);
        trans_waters[4] =  new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -0.9f);

        for (int i=0; i<5; i++) {

            trans_waters[i].setRepeatCount(Animation.INFINITE);
            trans_waters[i].setDuration(12000);
            move_waters.addAnimation(trans_waters[i]);

        }
            move_waters.setRepeatMode(Animation.INFINITE);

        if(hasFocus) {

            for (int i=0; i<5; i++) {
                waters[i].startAnimation(move_waters);
            }
        } else {

            for (int i=0; i<5; i++) {
               waters[i].setAnimation(null);
            }
        } */
        triAtom[0] = (Button) findViewById(R.id.playButton);
        triAtom[1] = (Button) findViewById(R.id.tutorialButton);
       // w[2] = (ImageView) findViewById(R.id.water3);
        //w[3] = (ImageView) findViewById(R.id.water4);
       // w[4] = (ImageView) findViewById(R.id.water5);
       // w[5] = (ImageView) findViewById(R.id.water6);

        //animate images.
        //First set up an AnimationSet to put all the animations into
        AnimationSet[] move_triAtoms = new AnimationSet[2];
        //set up the rotations for each image
        RotateAnimation rotate_triAtoms[] = new RotateAnimation[2];

        for (int i=0; i<2; i++) {
            rotate_triAtoms[i] = new RotateAnimation(0f, 360f, triAtom[i].getWidth()/2, triAtom[i].getHeight()/2);

            rotate_triAtoms[i].setRepeatCount(Animation.INFINITE);

            rotate_triAtoms[i].setDuration(5400);
            move_triAtoms[i] = new AnimationSet(true);
            move_triAtoms[i].addAnimation(rotate_triAtoms[i]);
        }

        //set up the translations (across the screen movements)
       // TranslateAnimation trans_w[] =  new TranslateAnimation[numberOfImageViews];
       // trans_w[0] =  new TranslateAnimation(Animation.ABSOLUTE, -10, Animation.ABSOLUTE, -10, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_PARENT, (float) 0.8);
        //trans_w[1] =  new TranslateAnimation(Animation.ABSOLUTE, 0.1f, Animation.ABSOLUTE, 0.3f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.9f);
        //trans_w[2] =  new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_PARENT, -0.3f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        //trans_w[3] =  new TranslateAnimation(Animation.ABSOLUTE, 0.1f, Animation.ABSOLUTE, 0.3f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -0.9f);
        //trans_w[4] =  new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -0.9f);

       // for (int i=0; i<numberOfImageViews; i++) {
           // trans_w[i].setRepeatCount(Animation.INFINITE);
           // trans_w[i].setDuration(2000);
           // move_w[i].addAnimation(trans_w[i]);
         //   move_triAtoms[i].setRepeatMode(Animation.INFINITE);
       // }

        if(hasFocus) {
            for (int i=0; i<2; i++) {
                triAtom[i].startAnimation(move_triAtoms[i]);
            }
        } else {
            for (int i=0; i<2; i++) {
                triAtom[i].setAnimation(null);
            }
        }

    }
   public void onPlayButtonClick(View view) {

        Intent getNameScreenIntent = new Intent(this,PlaychoicesScreen.class);
       getNameScreenIntent.putExtra("purchased", fullVersionIsOwned);

        startActivity(getNameScreenIntent);
    }

    public void onTutorialButtonClick(View view) {

        Intent getTutorialChoices = new Intent(this, tutorialChoices.class);

        startActivity(getTutorialChoices);
    }
}
