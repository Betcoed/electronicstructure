package com.sciencehighgames.electronicstructure;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import java.util.HashMap;
import java.util.ArrayList;
import android.content.pm.PackageInfo;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by sarahhinsley on 07/04/2015.
 */
public class PENScreen extends Activity {


    public static final String CALCULATOR_PACKAGE ="com.android.calculator2";
    public static final String CALCULATOR_CLASS ="com.android.calculator2.Calculator";

    private static final String KEY_TEXT_VALUE0 = "textValue0";
    private static final String KEY_TEXT_VALUE1 = "textValue1";
    private static final String KEY_TEXT_VALUE2 = "textValue2";
    private static final String KEY_COLOR_VALUE0 = "colorValue0";
    private static final String KEY_COLOR_VALUE1 = "colorValue1";
    private static final String KEY_COLOR_VALUE2 = "colorValue2";
    String savedNumbers[] = new String[3];
    int[] savedColors = new int[3];

    ImageView elementSymbol_ImageView;

    EditText PENEditText[] = new EditText[3];

    int[] PENnumbers = new int[3];
    int elementSymbolNumber;
    MediaPlayer negativeSound, correctSound;
    int startValue;
    int previousPressedInPENScreen = 0;

    //for in-app billing, the user will be locked out once they have had maxTurns goes of the app
    // the following integer and variable are used to track the number of goes the user has had.
    public static final int maxTurns = 6;
    int turnNumber;
    IInAppBillingService mService;
    int fullVersionIsOwned;
    int purchaseState = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        negativeSound = MediaPlayer.create(this, R.raw.negativebeep);
        correctSound = MediaPlayer.create(this, R.raw.correctsound);

        setContentView(R.layout.pen_screen);
        Intent getStartValue = getIntent();
        startValue = getStartValue.getIntExtra("passStartValue", 0);
        elementSymbolNumber = getStartValue.getIntExtra("passAtomicNumber",0);
        turnNumber = getStartValue.getIntExtra("passTurnNumber",0);
        elementSymbol_ImageView = (ImageView)findViewById(R.id.elementSymbolImageView);
        PENEditText[0] = (EditText)findViewById(R.id.protonsEditText);
        PENEditText[1] = (EditText)findViewById(R.id.electronsEditText);
        PENEditText[2] = (EditText)findViewById(R.id.neutronsEditText);

        switch ( startValue ) {
            case 21:
                elementSymbolNumber = 0;
                break;
            case 22:
                elementSymbolNumber = 61;
                break;
            case 23:
                elementSymbolNumber = 132;
                break;
            case 24:
                elementSymbolNumber = 1 + (int) (Math.random() * ((167 - 1) + 1));
                View previousButton = findViewById(R.id.previousButton);
                previousButton.setVisibility(View.GONE);
                break;
            case 32:case 34:case 35:case 36:
                View previousButton2 = findViewById(R.id.previousButton);
                previousButton2.setVisibility(View.GONE);
                Button nextButton = (Button) findViewById(R.id.nextButton);
                DisplayMetrics dm = nextButton.getResources().getDisplayMetrics();
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) nextButton.getLayoutParams();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    lp.setMargins(convertDpToPx(40, dm), convertDpToPx(5, dm), convertDpToPx(40, dm), convertDpToPx(5, dm));
                    nextButton.setLayoutParams(lp);
                } else {
                    lp.setMargins(convertDpToPx(5, dm), convertDpToPx(40, dm), convertDpToPx(5, dm), convertDpToPx(40, dm));
                    nextButton.setLayoutParams(lp);
                }
                break;
        }



        if (savedInstanceState != null) {
            savedNumbers[0] = savedInstanceState.getString(KEY_TEXT_VALUE0);
            savedNumbers[1] = savedInstanceState.getString(KEY_TEXT_VALUE1);
            savedNumbers[2] = savedInstanceState.getString(KEY_TEXT_VALUE2);
            savedColors[0] = savedInstanceState.getInt(KEY_COLOR_VALUE0);
            savedColors[1] = savedInstanceState.getInt(KEY_COLOR_VALUE1);
            savedColors[2] = savedInstanceState.getInt(KEY_COLOR_VALUE2);

            for (int i=0; i<3; i++) {
                PENEditText[i].setText(savedNumbers[i]);
                PENEditText[i].setTextColor(savedColors[i]);

            }
            elementSymbolNumber = savedInstanceState.getInt("elementSymbolNumber");
        }

        if (startValue > 30) {
            elementSymbol_ImageView.setImageResource(elementSymbolsForLevel3[elementSymbolNumber]);
        } else {
            elementSymbol_ImageView.setImageResource(elementSymbols[elementSymbolNumber]);
        }
    }

    private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
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

    private int checkOwnedItems() {


        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");

            if (response == 0) {

                ArrayList<String> purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                System.out.println("purchaseDataList.size = " + purchaseDataList.size());
                for (int i = 0; i < purchaseDataList.size(); ++i) {

                    String purchaseData = purchaseDataList.get(i);

                    // do something with this purchase information
                    // e.g. display the updated list of products owned by user
                    JSONObject jo = new JSONObject(purchaseData);
                    purchaseState = jo.getInt("purchaseState");

                }

                // if continuationToken != null, call getPurchases again
                // and pass in the token to retrieve more items
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return purchaseState;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TEXT_VALUE0, String.valueOf(PENEditText[0].getText()));
        outState.putString(KEY_TEXT_VALUE1, String.valueOf(PENEditText[1].getText()));
        outState.putString(KEY_TEXT_VALUE2, String.valueOf(PENEditText[2].getText()));
        outState.putInt(KEY_COLOR_VALUE0, Integer.valueOf(PENEditText[0].getCurrentTextColor()));
        outState.putInt(KEY_COLOR_VALUE1, Integer.valueOf(PENEditText[1].getCurrentTextColor()));
        outState.putInt(KEY_COLOR_VALUE2, Integer.valueOf(PENEditText[2].getCurrentTextColor()));
        outState.putInt("elementSymbolNumber", elementSymbolNumber);
    }

    public void onClickNext_PEN(View view) {
        if (purchaseState != 0 && turnNumber >= maxTurns) {

            Intent getInAppBillingScreen = new Intent(PENScreen.this, InAppBillingScreen.class);
            startActivity(getInAppBillingScreen);

        } else {
            turnNumber++;

            if (startValue > 30) {
                if ((elementSymbolNumber == 20 && startValue == 31) || (elementSymbolNumber == 35 && startValue == 33)) {
                    setToast("No more elements available for this level");
                } else if (startValue == 36) {
                    finish();
                    Intent getNumberForm = new Intent(PENScreen.this, NumberForm.class);
                    getNumberForm.putExtra("passStartValue", startValue);
                    getNumberForm.putExtra("passTurnNumber",turnNumber);
                    getNumberForm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(getNumberForm);
                } else {
                    finish();
                    Intent getShellsScreen = new Intent(PENScreen.this, ShellsScreen.class);
                    getShellsScreen.putExtra("passStartValue", startValue);
                    getShellsScreen.putExtra("passAtomicNumber", elementSymbolNumber);
                    getShellsScreen.putExtra("passTurnNumber", turnNumber);
                    System.out.println("turnNumber in PEN = " + turnNumber);
                    getShellsScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(getShellsScreen);
                }
            } else if ((elementSymbolNumber == 60 && startValue == 21) || (elementSymbolNumber == 131 && startValue == 22)
                    || (elementSymbolNumber == 166 && startValue == 23)) {
                setToast("No more elements available for this level");
            } else {
                nextElementIonIsotope();
            }
        }
    }

    private void setToast(String toastComment) {
        Toast toast_touchLine = Toast.makeText(this, toastComment, Toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast_touchLine.getView();
        TextView toastTV_touchLine = (TextView) toastLayout.getChildAt(0);
        toastTV_touchLine.setGravity(Gravity.CENTER);
        toastTV_touchLine.setTextSize(30);
        toast_touchLine.show();
    }

    private void nextElementIonIsotope() {
        switch (startValue) {
            case 21: case 22: case 23:
                elementSymbolNumber++;
                break;
            case 24:
                elementSymbolNumber = 1 + (int) (Math.random() * ((167 - 1) + 1));
                break;
        }

        elementSymbol_ImageView.setImageResource(elementSymbols[elementSymbolNumber]);
        clearEditTexts();

    }
    private void clearEditTexts(){
        for (int i=0; i<3; i++) {
            PENEditText[i].getText().clear();
            PENEditText[i].setTextColor(Color.BLACK);
        }
    }


    public void onClickCheckPEN(View view) {
        //set empty text boxes to zero, and change anything written in the textboxes to numbers, to avoid confusion with
        //how the user has entered numbers
        setEmptyTextViewsToZero();
        changeToNumbers();
        //if level 3 (so user is completing elec structures AND PEN, then use different checking method,
        //since answers are held in different arrays

        if (startValue < 30) {
            checkForLevel2();
        } else {
            checkForLevel3();
        }
    }

    private void checkForLevel2() {
        if (PENnumbers[0] == numberOfProtons[elementSymbolNumber]) {
            //if protons correct, then check electrons
            if (PENnumbers[1] == numberOfElectrons[elementSymbolNumber]) {

                //if electrons and protons correct, then check neutrons
                if (PENnumbers[2] == numberOfNeutrons[elementSymbolNumber]) {

                    setCorrect();
                } else {
                    //if neutrons wrong, then show incorrect message
                    setIncorrect("Wrong number of neutrons", 2);
                }
            } else {
                //if number of electrons wrong then show incorrect message
                setIncorrect("Wrong number of electrons", 1);
            }
        } else {
            //if number of protons wrong then show incorrect message
            setIncorrect("Wrong number of protons", 0);
        }
    }

    private void checkForLevel3() {
        if (PENnumbers[0] == numberOfProtonsLevel3[elementSymbolNumber]) {
            //if protons correct, then check electrons
            if (PENnumbers[1] == numberOfElectronsLevel3[elementSymbolNumber]) {

                //if electrons and protons correct, then check neutrons
                if (PENnumbers[2] == numberOfNeutronsLevel3[elementSymbolNumber]) {

                    setCorrect();
                } else {
                    //if neutrons wrong, then show incorrect message
                    setIncorrect("Wrong number of neutrons", 2);
                }
            } else {
                //if number of electrons wrong then show incorrect message
                setIncorrect("Wrong number of electrons", 1);
            }
        } else {
            //if number of protons wrong then show incorrect message
            setIncorrect("Wrong number of protons", 0);
        }
    }

    //clears the text fields
    public void onClickClearPEN(View view) {
        clearEditTexts();
    }

    //tells the user they are correct, then moves on to the next element/ion
    private void setCorrect() {
        findViewById(R.id.PENButtons).setVisibility(View.GONE);
        findViewById(R.id.correctForPEN).setVisibility(View.VISIBLE);
        turnNumber++;
        correctSound.start();
        new CountDownTimer(3000, 50) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                findViewById(R.id.correctForPEN).setVisibility(View.GONE);
                findViewById(R.id.PENButtons).setVisibility(View.VISIBLE);
                if (purchaseState != 0 && turnNumber >= maxTurns) {

                    Intent getInAppBillingScreen = new Intent(PENScreen.this, InAppBillingScreen.class);
                    startActivity(getInAppBillingScreen);

                } else {

                    if (startValue > 30) {
                        if ((elementSymbolNumber == 20 && startValue == 31) || (elementSymbolNumber == 35 && startValue == 33)) {
                            finish();
                            Intent getCompletedLevel = new Intent(PENScreen.this, LevelCompleted.class);
                            getCompletedLevel.putExtra("passStartValue", startValue);
                            getCompletedLevel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(getCompletedLevel);
                        } else if (startValue == 36) {
                            finish();
                            Intent getNumberForm = new Intent(PENScreen.this, NumberForm.class);
                            getNumberForm.putExtra("passStartValue", startValue);
                            getNumberForm.putExtra("passTurnNumber", turnNumber);
                            // getNumberForm.putExtra("passAtomicNumber", elementSymbolNumber);
                            getNumberForm.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(getNumberForm);
                        } else {
                            finish();
                            Intent getShellsScreen = new Intent(PENScreen.this, ShellsScreen.class);
                            getShellsScreen.putExtra("passStartValue", startValue);
                            getShellsScreen.putExtra("passAtomicNumber", elementSymbolNumber);
                            getShellsScreen.putExtra("passTurnNumber", turnNumber);
                            getShellsScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(getShellsScreen);
                        }
                    } else if ((elementSymbolNumber == 60 && startValue == 21) || (elementSymbolNumber == 131 && startValue == 22)
                            || (elementSymbolNumber == 166 && startValue == 23)) {
                        finish();
                        Intent getCompletedLevel = new Intent(PENScreen.this, LevelCompleted.class);
                        getCompletedLevel.putExtra("passStartValue", startValue);
                        getCompletedLevel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(getCompletedLevel);
                    } else {
                        nextElementIonIsotope();
                    }
                }
            }
        }.start();
    }

    private void setIncorrect(String hint, int PEN) {
        final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongPEN);
        textViewToChange.setText(hint);
        PENEditText[PEN].setTextColor(Color.RED);

        findViewById(R.id.PENButtons).setVisibility(View.GONE);
        findViewById(R.id.incorrectForPEN).setVisibility(View.VISIBLE);
        negativeSound.start();
        new CountDownTimer(3000, 50) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                findViewById(R.id.incorrectForPEN).setVisibility(View.GONE);
                findViewById(R.id.PENButtons).setVisibility(View.VISIBLE);

            }
        }.start();
    }
    private void setEmptyTextViewsToZero() {

        String ed_text[] = new String[3];

        for (int i=0; i<3; i++) {
            ed_text[i] = PENEditText[i].getText().toString().trim();
            if (ed_text[i].isEmpty() || ed_text[i].length() == 0 || ed_text[i].equals("") || ed_text[i] == null) {
                PENEditText[i].setText("0");
            }
        }
    }

    private void changeToNumbers() {
        String retrieveTexts[] = new String[3];

        for (int i = 0; i < 3; i++) {
            retrieveTexts[i] = PENEditText[i].getText().toString();
            PENnumbers[i] = Integer.parseInt(retrieveTexts[i]);
        }
    }

    public void onClickPreviousPEN(View view) {

        previousPressedInPENScreen = 1;
        if (elementSymbolNumber == 0 || ((startValue == 31 || startValue == 33) && elementSymbolNumber == 1)) {
            setToast("No smaller elements available");
        } else if (startValue > 30) {
            if (startValue == 31) {
                elementSymbolNumber = elementSymbolNumber - 2;
            }

            Intent getShellsScreen = new Intent(PENScreen.this, ShellsScreen.class);
            getShellsScreen.putExtra("passStartValue", startValue);
            getShellsScreen.putExtra("passAtomicNumber", elementSymbolNumber);
            getShellsScreen.putExtra("fromPENScreen", previousPressedInPENScreen);
            getShellsScreen.putExtra("passTurnNumber", turnNumber);
            startActivity(getShellsScreen);

        } else if (startValue == 22 && elementSymbolNumber == 61 || (startValue == 23 && elementSymbolNumber == 132)) {
                setToast("No smaller elements available for this level");
            } else {
                switch (startValue) {
                    case 21:
                    case 22:
                    case 23:
                        elementSymbolNumber--;
                        break;
                    case 31:

                }

                elementSymbol_ImageView.setImageResource(elementSymbols[elementSymbolNumber]);
                clearEditTexts();
            }
        }

    int[] elementSymbols = {
            R.drawable.hydrogen,
            R.drawable.hydrogen_pos,
            R.drawable.hydrogen_anion,
            R.drawable.deuterium,
            R.drawable.tritium,
            R.drawable.helium,
            R.drawable.lithium,
            R.drawable.lithium_cation,
            R.drawable.lithium_6_isotope,
            R.drawable.beryllium,
            R.drawable.beryllium_cation,
            R.drawable.boron_10_isotope,
            R.drawable.boron,
            R.drawable.carbon,
            R.drawable.carbon_13_isotope,
            R.drawable.carbon_14_isotope,
            R.drawable.nitrogen,
            R.drawable.nitrogen_anion,
            R.drawable.nitrogen_15_isotope,
            R.drawable.oxygen,
            R.drawable.oxygen_anion,
            R.drawable.oxygen_17_isotope,
            R.drawable.oxygen_18_isotope,
            R.drawable.fluorine,
            R.drawable.fluorine_anion,
            R.drawable.neon,
            R.drawable.neon_21_isotope,
            R.drawable.neon_22_isotope,
            R.drawable.sodium,
            R.drawable.sodium_cation,
            R.drawable.magnesium,
            R.drawable.magnesium_cation,
            R.drawable.magnesium_25_isotope,
            R.drawable.magnesium_26_isotope,
            R.drawable.aluminium,
            R.drawable.aluminium_cation,
            R.drawable.silicon,
            R.drawable.silicon_29_isotope,
            R.drawable.silicon_30_isotope,
            R.drawable.phosphorus,
            R.drawable.phosphorus_anion,
            R.drawable.sulphur,
            R.drawable.sulphur_anion,
            R.drawable.sulphur_33_isotope,
            R.drawable.sulphur_34_isotope,
            R.drawable.sulphur_36_isotope,
            R.drawable.chlorine35,
            R.drawable.chlorine35anion,
            R.drawable.chlorine37isotope,
            R.drawable.chlorine37anion_isotope,
            R.drawable.argon,
            R.drawable.argon36isotope,
            R.drawable.argon38isotope,
            R.drawable.potassium,
            R.drawable.potassium_cation,
            R.drawable.potassium40isotope,
            R.drawable.potassium41isotope,
            R.drawable.calcium,
            R.drawable.calcium_cation,
            R.drawable.calcium42isotope,
            R.drawable.calcium44isotope,
            R.drawable.scandium,
            R.drawable.scandium_cation,
            R.drawable.titanium46isotope,
            R.drawable.titanium47isotope,
            R.drawable.titanium48isotope,
            R.drawable.titanium48cation,
            R.drawable.vanadium50isotope,
            R.drawable.vanadium51isotope,
            R.drawable.vanadium_5plus,
            R.drawable.vanadium_4plus,
            R.drawable.vanadium_3plus,
            R.drawable.vanadium_2plus,
            R.drawable.chromium52isotope,
            R.drawable.chromium50isotope,
            R.drawable.chromium53isotope,
            R.drawable.chromium_3plus,
            R.drawable.chromium_6plus,
            R.drawable.manganese,
            R.drawable.manganese_2plus,
            R.drawable.manganese_3plus,
            R.drawable.manganese_4plus,
            R.drawable.iron54isotope,
            R.drawable.iron56isotope,
            R.drawable.iron57isotope,
            R.drawable.iron_2plus,
            R.drawable.iron_3plus,
            R.drawable.cobalt,
            R.drawable.cobalt_2plus,
            R.drawable.cobalt_3plus,
            R.drawable.nickel58isotope,
            R.drawable.nickel60isotope,
            R.drawable.nickel62isotope,
            R.drawable.nickel_2plus,
            R.drawable.copper63isotope,
            R.drawable.copper65isotope,
            R.drawable.copper_2plus,
            R.drawable.copper_cation,
            R.drawable.zinc64isotope,
            R.drawable.zinc66isotope,
            R.drawable.zinc68isotope,
            R.drawable.zinc_2plus,
            R.drawable.rhodium,
            R.drawable.rhodium_3plus,
            R.drawable.palladium105isotope,
            R.drawable.palladium106isotope,
            R.drawable.palladium108isotope,
            R.drawable.palladium_1plus,
            R.drawable.palladium_2plus,
            R.drawable.palladium_4plus,
            R.drawable.silver107isotope,
            R.drawable.silver107_1plus,
            R.drawable.silver109isotope,
            R.drawable.silver109_2plus,
            R.drawable.cadmium_111isotope,
            R.drawable.cadmium112isotope,
            R.drawable.cadmium114isotope,
            R.drawable.cadmium_2plus,
            R.drawable.platinum194isotope,
            R.drawable.platinum195isotope,
            R.drawable.platinum196isotope,
            R.drawable.platinum_2plus,
            R.drawable.platinum_4plus,
            R.drawable.gold,
            R.drawable.mercury199isotope,
            R.drawable.mercury201isotope,
            R.drawable.mercury202isotope,
            R.drawable.mercury_1plus,
            R.drawable.mercury_2plus,
            R.drawable.tungsten184isotope,
            R.drawable.tungsten186isotope,
            R.drawable.tungsten182isotope,
            R.drawable.arsenic,
            R.drawable.bromine79isotope,
            R.drawable.bromine81isotope,
            R.drawable.bromine79_1minus,
            R.drawable.bromine81_1minus,
            R.drawable.indium113isotope,
            R.drawable.indium115isotope,
            R.drawable.tin120isotope,
            R.drawable.tin118isotope,
            R.drawable.tin116isotope,
            R.drawable.iodine,
            R.drawable.iodine_minus,
            R.drawable.lead208isotope,
            R.drawable.lead206isotope,
            R.drawable.lead207isotope,
            R.drawable.lead_2plus,
            R.drawable.lead_4plus,
            R.drawable.krypton84isotope,
            R.drawable.krypton86isotope,
            R.drawable.krypton83isotope,
            R.drawable.xenon129isotope,
            R.drawable.xenon132isotope,
            R.drawable.xenon131isotope,
            R.drawable.caesium,
            R.drawable.caesium_cation,
            R.drawable.rubidium85isotope,
            R.drawable.rubidium87isotope,
            R.drawable.rubidium_cation,
            R.drawable.barium138isotope,
            R.drawable.barium136isotope,
            R.drawable.barium137isotope,
            R.drawable.barium_cation,
            R.drawable.uranium238isotope,
            R.drawable.uranium235isotope,
            R.drawable.plutonium
    };

    static final int[] numberOfProtons = {
            1,1,1,1,1,2,3,3,3,4,4,5,5,6,6,6,7,7,7,8,8,8,8,9,9,10,10,10,11,11,12,12,12,12,13,13,
            14,14,14,15,15,16,16,16,16,16,17,17,17,17,18,18,18,19,19,19,19,20,20,20,20,21,21,
            22,22,22,22,23,23,23,23,23,23,24,24,24,24,24,25,25,25,25,26,26,26,26,26,27,27,27,
            28,28,28,28,29,29,29,29,30,30,30,30,45,45,46,46,46,46,46,46,47,47,47,47,48,48,48,48,
            78,78,78,78,78,79,80,80,80,80,80,74,74,74,33,35,35,35,35,49,49,50,50,50,
            53,53,82,82,82,82,82,36,36,36,54,54,54,55,55,37,37,37,56,56,56,56,92,92,94
    };
    static final int[] numberOfElectrons = {
            1,0,2,1,1,2,3,2,3,4,2,5,5,6,6,6,7,10,7,8,10,8,8,9,10,10,10,10,11,10,12,10,12,12,13,10,
            14,14,14,15,18,16,18,16,16,16,17,18,17,18,18,18,18,19,18,19,19,20,18,20,20,21,18,
            22,22,22,18,23,23,18,19,20,21,24,24,24,21,18,25,23,22,21,26,26,26,24,23,27,25,24,
            28,28,28,26,29,29,27,28,30,30,30,28,45,42,46,46,46,45,44,42,47,46,47,45,48,48,48,46,
            78,78,78,76,74,79,80,80,80,79,78,74,74,74,33,35,35,36,36,49,49,50,50,50,
            53,54,82,82,82,80,78,36,36,36,54,54,54,55,54,37,37,36,56,56,56,54,92,92,94
    };
    static final int[] numberOfNeutrons = {
            0,0,0,1,2,2,4,4,3,5,5,5,6,6,7,8,7,7,8,8,8,9,10,10,10,10,11,12,12,12,12,12,13,14,14,14,
            14,15,16,16,16,16,16,17,18,20,18,18,20,20,22,18,20,20,20,21,22,20,20,22,24,24,24,
            24,25,26,26,27,28,28,28,28,28,28,26,29,28,28,30,30,30,30,28,30,31,30,30,32,32,32,
            30,32,34,30,34,36,34,34,34,36,38,34,58,58,59,60,62,60,60,60,60,60,62,62,63,64,66,66,
            116,117,118,117,117,118,119,121,122,122,122,110,112,108,42,44,46,44,46,64,66,70,68,66,
            74,74,126,124,125,126,126,48,50,47,75,78,77,78,78,48,50,50,82,80,81,81,146,143,145

    };

//an extra hydrogen has been put in at position '0', because in ShellsScreen, the numbering starts at 1, not 0
    int[] elementSymbolsForLevel3 = {
            R.drawable.hydrogen,
           R.drawable.hydrogen,
    R.drawable.helium,
    R.drawable.lithium,
    R.drawable.beryllium,
    R.drawable.boron,
    R.drawable.carbon,
    R.drawable.nitrogen,
    R.drawable.oxygen,
    R.drawable.fluorine,
    R.drawable.neon,
    R.drawable.sodium,
    R.drawable.magnesium,
    R.drawable.aluminium,
    R.drawable.silicon,
    R.drawable.phosphorus,
    R.drawable.sulphur,
    R.drawable.chlorine35,
    R.drawable.argon,
    R.drawable.potassium,
    R.drawable.calcium,
    R.drawable.hydrogen_pos,
    R.drawable.hydrogen_anion,
    R.drawable.lithium_cation,
    R.drawable.beryllium_cation,
    R.drawable.nitrogen_anion,
    R.drawable.oxygen_anion,
    R.drawable.fluorine_anion,
    R.drawable.sodium_cation,
    R.drawable.magnesium_cation,
    R.drawable.aluminium_cation,
    R.drawable.phosphorus_anion,
    R.drawable.sulphur_anion,
    R.drawable.chlorine35anion,
    R.drawable.potassium_cation,
    R.drawable.calcium_cation,
    };

    //an extra 0 has been put in at position '0', because in ShellsScreen, the numbering starts at 1, not 0
    static final int[] numberOfProtonsLevel3 = {
            0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,1,1,3,4,7,8,9,11,12,13,15,16,17,19,20
    };

    //an extra 0 has been put in at position '0', because in ShellsScreen, the numbering starts at 1, not 0
    static final int[] numberOfElectronsLevel3 = {
            0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,0,2,2,2,10,10,10,10,10,10,18,18,18,18,18
    };

    //an extra 0 has been put in at position '0', because in ShellsScreen, the numbering starts at 1, not 0
    static final int[] numberOfNeutronsLevel3 = {
            0,0,2,4,5,6,6,7,8,10,10,12,12,14,14,16,16,18,22,20,20,0,0,4,5,7,8,10,12,12,14,16,16,18,20,20
    };

    public void onClickCalculator(View view)  {
        ArrayList<HashMap<String,Object>> items =new ArrayList<HashMap<String,Object>>();
        final PackageManager pm = getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        for (PackageInfo pi : packs) {
            if( pi.packageName.toString().toLowerCase().contains("calcul")){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("appName", pi.applicationInfo.loadLabel(pm));
                map.put("packageName", pi.packageName);
                items.add(map);
            }
        }

        if(items.size()>=1){
            String packageName = (String) items.get(0).get("packageName");
            Intent i = pm.getLaunchIntentForPackage(packageName);
            if (i != null)
                startActivity(i);
        }
        else{
            setToast("Calculator not available");
        }
    }
}
