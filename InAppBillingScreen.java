package com.sciencehighgames.electronicstructure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sciencehighgames.electronicstructure.util.IabHelper;
import com.sciencehighgames.electronicstructure.util.IabResult;
import com.sciencehighgames.electronicstructure.util.Inventory;
import com.sciencehighgames.electronicstructure.util.Purchase;

/**
 * Created by sarahhinsley on 14/05/2015.
 */
public class InAppBillingScreen extends Activity {

    private static final String TAG = "com.sciencehighgames.electronicstructure";
    IabHelper mHelper;
    static final String ITEM_SKU = "com.sciencehighgames.electronicstructure.premium";
    boolean itemBought;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.in_app_billing_screen);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjbmqh+gKROnS3HeY4FnVOojvYSoZ5V9ZUlGKx8IPKnx2/PFcGFs02bqURTDTze2Gh08l4SznttmJzM7uGlIwf68vW8q0A48eHPMYygmErvxav1PuOSNYy5cSaKCW4D73yaisMaIpCiT61ZIrKbSH3/OHobnQqpUTTEKvf5ioF7p5a8iEhbIRUuTNuly2g6MlSBbywmoCkiN74WWWB7Jx0ZAqmODbBJDEUyQnPDNXoMBqPYRd8gCS9wj/ubsBxTYJDyuOArLeArSphM6loodR2Fe+VdrOIfki0mBIcAO+aPkS0qRgsnoUYtkwclVaUSVvB2yzwW0TyninsFtKZsMaxwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "not OK" + result);

                } else {
                    Log.d(TAG, "OK");
                }
            }
        });

    }


    public void buyClick(View view) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener =
            new IabHelper.OnIabPurchaseFinishedListener() {

                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

                    if (result.isFailure()) {
                        if (result.getResponse() == 7) {
                            String purchaseUnsuccessful = "Purchase unsuccessful, item already owned";
                            Toast toast = Toast.makeText(InAppBillingScreen.this, purchaseUnsuccessful, Toast.LENGTH_SHORT);
                            LinearLayout toastLayout = (LinearLayout) toast.getView();

                            TextView toastTV = (TextView) toastLayout.getChildAt(0);
                            toastTV.setGravity(Gravity.CENTER);
                            toastTV.setTextSize(30);
                            toast.show();
                            Intent ReturntoScreen = new Intent(InAppBillingScreen.this, MainActivity.class);
                            ReturntoScreen.putExtra("passItemBought", itemBought);
                            startActivity(ReturntoScreen);
                            return;
                        }
                    } else if (purchase.getSku().equals(ITEM_SKU)) {
                        finish();

                        itemBought = true;
                        Intent ReturntoScreen = new Intent(InAppBillingScreen.this, MainActivity.class);
                        ReturntoScreen.putExtra("passItemBought", itemBought);
                      //  int response = mService.consumePurchase(3, getPackageName(), token);
                      //  mHelper.consumeAsync(purchase, null);
                        startActivity(ReturntoScreen);
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent ReturntoScreen = new Intent(InAppBillingScreen.this, MainActivity.class);
        startActivity(ReturntoScreen);
    }
}
