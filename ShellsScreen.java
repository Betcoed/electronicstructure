package com.sciencehighgames.electronicstructure;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.sciencehighgames.electronicstructure.util.IabHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sarahhinsley on 09/01/2015.
 */
public class ShellsScreen extends Activity {

    IInAppBillingService mService;
    int purchaseState = 3;
    IabHelper iabHelper;

    public int startValue;
    boolean correct = false;
    private ConcirclesView concirclesView;

    //for in-app billing, the user will be locked out once they have had maxTurns goes of the app
    // the following integer and variable are used to track the number of goes the user has had.
    public static final int maxTurns = 6;
    int turnNumber = 0;

    //declare sound variables
    MediaPlayer negativesound, correctsound;
    int previousPressedInPENScreen = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shells_screenlayout);

        //next 3 lines are for sorting out in-app billing - will check later whether user has purchased product or not
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        negativesound = MediaPlayer.create(this, R.raw.negativebeep);
        correctsound = MediaPlayer.create(this, R.raw.correctsound);

        //the following line has to come AFTER setContentView, or calling invalidate from this Activity
        //won't work
        concirclesView = (ConcirclesView) findViewById(R.id.shellsarea);

        Intent getStartValue = getIntent();

        startValue = getStartValue.getIntExtra("passStartValue", 0);
        concirclesView.atomicnumber = getStartValue.getIntExtra("passAtomicNumber", 0);


        Intent passPENScreenTrue = getIntent();
        previousPressedInPENScreen = passPENScreenTrue.getIntExtra("fromPENScreen", 0);
        turnNumber = passPENScreenTrue.getIntExtra("passTurnNumber", 0);

        if (startValue == 12 || startValue == 14 || startValue == 15 ||
                startValue == 32 || startValue == 34 || startValue == 35) {
            View previousButton = findViewById(R.id.previousButton);
            previousButton.setVisibility(View.GONE);
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

        }
            if (startValue < 30) {
                concirclesView.atomicnumber = 0;
            }
            concirclesView.touchcanvasnumber = 0;
            setAtomicnumber(startValue);

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("atomicnumber", concirclesView.atomicnumber);
        outState.putInt("startValue", startValue);
        outState.putInt("touchcanvasnumber", concirclesView.touchcanvasnumber);
        outState.putIntArray("numberElecsInEachShell", concirclesView.NumberElectronsInEachShell);
        outState.putBoolean("correct", correct);
    }

    public void setAtomicnumber(int playchoice) {
        if (playchoice == 11 || playchoice == 31) {
            concirclesView.atomicnumber++;
            //'previousPressedInPENScreen' required because otherwise ShellsScreen will treat it as a 'next'.
            //When user presses previous in PEN Screen, 'previousPressedInPENScreen' changes to a 1, hence allowing
            //choices here
        } else if (playchoice == 13 || (playchoice ==33 && previousPressedInPENScreen == 0)) {
            //method to call to choose which element/ion to bring up next.  I think the atomionLabel array will
            //have to be ordered as atomthenion, or maybe not?  Maybe I will have to go through each element/ion
            //individually and specify which element/ion to bring up next, to amke it go in order.  There are
            //quite a few that will just be atomicnumber++, then just do other separately
            getNextAtomThenIon();
        }
            else if (playchoice == 33 && previousPressedInPENScreen == 1) {
                getPreviousAtomthenIon();
            //reset previousPressedInPENScreen to zero, because this has now been used for the purpose intended i.e.
            //to tell ShellsScreen that the user clicked the previous button in the PENScreen.
            previousPressedInPENScreen = 0;
        } else if (playchoice == 12 || playchoice == 32){
            concirclesView.atomicnumber = 1 + (int) (Math.random() * ((20 - 1) + 1));
        } else if (playchoice ==14 || playchoice == 34) {
            concirclesView.atomicnumber = 21 + (int) (Math.random() * ((35 - 21) + 1));
        } else if (playchoice == 15 || playchoice == 16 || playchoice == 35 || playchoice == 36) {
            concirclesView.atomicnumber = 1 + (int) (Math.random() * ((35 - 1) + 1));
        }
    }

    public void onClickCheck(View view) {
//call check method from ConcirclesView class
//set number of electrons in each shell to zero
        correct = false;

        concirclesView.getZvalueElectronPosition();

        //check which shell each electron is in, by going through ZvalueElectronPosition, and counting number
        //of electrons in each shell.  Pu the count into the array NumberElectronsInEachShell.

        //concirclesView.countElectronsinEachShell();
        concirclesView.getNumberElectronsInEachShell();
        if (concirclesView.atomicnumber < 21) {
            //call method to check electron positions against the element shown in the centre
            checkAtoms();
        } else {
            //or call method to check electron positions against ion shown in the centre
            checkIons();
        }
    }

    private void checkAtoms() {
        //first check that the length of array Zvalueelectronposition is equal to the atomic number, if not, it's incorrect
        if (concirclesView.touchcanvasnumber == concirclesView.atomicnumber) {
            //open another if statement to start checking each separate element
            // first check that they've put electrons in shell 1, if not, the answer must be wrong (for atoms)

            if (concirclesView.NumberElectronsInEachShell[0] == 0) {

                final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                textViewToChange.setText("Electrons required in shell 1");
                setIncorrect();
                //check that there are 2 electrons in shell 1 (apart from hydrogen)
            } else if (concirclesView.NumberElectronsInEachShell[0] == 1 && concirclesView.atomicnumber != 1) {

                final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                textViewToChange.setText(
                        "2 electrons required in shell 1");
                setIncorrect();
                //check whether there are any electrons in shell 4
            } else if (concirclesView.NumberElectronsInEachShell[3] == 0) {
                //check whether there are any electrons in shell 3
                if (concirclesView.NumberElectronsInEachShell[2] == 0) {
                    //check whether there are any electrons in shell 2
                    if (concirclesView.NumberElectronsInEachShell[1] == 0) {
                        //if there are no electrons ins shell 2, then call the checkanswer method, with
                        //parameters to check whether the element is hydrogen or helium, and to check whether
                        //it's correct
                        level1and2CheckAnswer(1, 2, 0, 0);
                    } else if (concirclesView.NumberElectronsInEachShell[0] == 2) {
                        level1and2CheckAnswer(3, 10, 1, 2);
                    } else {

                        final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                        textViewToChange.setText(
                                "2 electrons required in shell 1");
                        setIncorrect();
                    }
                } else if (concirclesView.NumberElectronsInEachShell[0] == 1) {

                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText(
                            "2 electrons required in shell 1");
                    setIncorrect();
                } else if (concirclesView.NumberElectronsInEachShell[1] == 8) {
                    level1and2CheckAnswer(11, 18, 2, 10);
                } else {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText(
                            "Wrong number of electrons in shell 2");
                    setIncorrect();
                }
                //check that there are 8 electrons in shells 2 and 3
            } else if ((concirclesView.NumberElectronsInEachShell[2] == 8) && (concirclesView.NumberElectronsInEachShell[1] == 8)) {
                //check there are 2 electrons in shell 1
                if (concirclesView.NumberElectronsInEachShell[0] == 2) {
                    //if there are 2 electrons in shell 1 and 8 electrons in shells 2 and 3, then check whether it is
                    //calcium or potassium, and whether it is correct.
                    level1and2CheckAnswer(19, 20, 3, 18);
                } else {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText("2 electrons required in shell 1");
                    setIncorrect();
                }
                //if not 8 electrons in shells 2 and 3, then call incorrect (by this stage the element should be
                //either potassium or calcium
            } else {
                final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                textViewToChange.setText("Incorrect number of electrons in shells 2 or 3");
                setIncorrect();
            }

        } else {
            final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
            textViewToChange.setText("Wrong number of electrons");
            setIncorrect();
        }
    }

    //method check the answer for an ion
    //for ions the "atomicnumber" here is not their real atomicnumber, I have just numbered the ions from 21 -35
    //with H+ being 21 and Ca2+ being 35.  it is convenient to just extend the "atomicnumber" variable here.
    private void checkIons() {
        if (concirclesView.NumberElectronsInEachShell[3] != 0) {
            final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
            textViewToChange.setText("This ion has no electrons in shell 4");
            setIncorrect();
            //if condition is referring to H+
        } else if (concirclesView.atomicnumber == 21) {
            //if there has been no touches onto the canvas (i.e. no electrons added)
            if (concirclesView.touchcanvasnumber == 0) {
                setCorrect();
            } else {
                final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                textViewToChange.setText("This ion has no electrons");
                setIncorrect();
            }
            //if condition is referring to all ions except H+
        } else if (concirclesView.atomicnumber >= 22 && concirclesView.atomicnumber <= 35) {
            if (concirclesView.NumberElectronsInEachShell[0] != 2) {
                final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                textViewToChange.setText("This ion requires 2 electrons in shell 1");
                setIncorrect();
                //the ions H-, Li+ and Be2+ (all have just 2 electrons in shell 1)
            } else if (concirclesView.atomicnumber >= 22 && concirclesView.atomicnumber <= 24) {
                if (concirclesView.touchcanvasnumber != 2) {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText("This ion has only 2 electrons");
                    setIncorrect();
                } else if (concirclesView.NumberElectronsInEachShell[1] != 0 && concirclesView.NumberElectronsInEachShell[2] != 0) {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText("This ion has no electrons in shells 2 or 3");
                    setIncorrect();
                } else {
                    setCorrect();
                }
            } else if (concirclesView.atomicnumber >= 25 && concirclesView.atomicnumber <= 30) {
                if (concirclesView.touchcanvasnumber != 10) {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText("This ion has 10 electrons");
                    setIncorrect();
                } else if (concirclesView.NumberElectronsInEachShell[2] != 0) {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText("This ion has no electrons in shell 3");
                    setIncorrect();
                } else {
                    setCorrect();
                }
            } else if (concirclesView.atomicnumber >= 31 && concirclesView.atomicnumber <= 35) {
                if (concirclesView.touchcanvasnumber != 18) {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText("This ion has 18 electrons");
                    setIncorrect();
                } else if (concirclesView.NumberElectronsInEachShell[2] != 8) {
                    final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                    textViewToChange.setText("This ion has 8 electrons in shell 3");
                    setIncorrect();
                } else {
                    setCorrect();
                }
            }
        }
    }

    //make the buttons disappear and the incorrect message appear
    private void setIncorrect() {

        findViewById(R.id.buttons).setVisibility(View.GONE);
        findViewById(R.id.incorrect).setVisibility(View.VISIBLE);
        negativesound.start();
        new CountDownTimer(3000, 50) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                findViewById(R.id.incorrect).setVisibility(View.GONE);
                findViewById(R.id.buttons).setVisibility(View.VISIBLE);

            }
        }.start();
    }

    //"correct" is displayed, where buttons normally are, for 3 seconds, then reverts back to buttons.
    //unless the atom was Ca (level 1a) or Ca2+ (level 1b), in which case "you have completed this level shows up
    //and then it goes back to the choices screen
    private void setCorrect() {

        findViewById(R.id.buttons).setVisibility(View.GONE);
        findViewById(R.id.correct).setVisibility(View.VISIBLE);

        correct = true;
        //this variable tracks how many goes the user has had
        turnNumber++;
        correctsound.start();

        new CountDownTimer(3000, 50) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                findViewById(R.id.correct).setVisibility(View.GONE);
                findViewById(R.id.buttons).setVisibility(View.VISIBLE);

                if (purchaseState != 0 && turnNumber >= maxTurns) {

                    Intent getInAppBillingScreen = new Intent(ShellsScreen.this, InAppBillingScreen.class);
                    startActivity(getInAppBillingScreen);

                } else
                {

                if (startValue > 30) {
                    //  finish();
                    Intent getStartValue = new Intent(ShellsScreen.this, PENScreen.class);

                    getStartValue.putExtra("passStartValue", startValue);
                    getStartValue.putExtra("passAtomicNumber", concirclesView.atomicnumber);
                    getStartValue.putExtra("passTurnNumber", turnNumber);

                    startActivity(getStartValue);
                } else if ((concirclesView.atomicnumber == 20 && startValue == 11) || (concirclesView.atomicnumber == 35 && startValue == 13)) {
                    finish();
                    Intent getCompletedLevel = new Intent(ShellsScreen.this, LevelCompleted.class);
                    getCompletedLevel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getCompletedLevel.putExtra("passStartValue", startValue);
                    startActivity(getCompletedLevel);
                    reset();
                } else {
                    setNextElement();
                }
            }
       }
        }.start();
    }

    //check that there are the correct number of electrons in the outer shell
    private void level1and2CheckAnswer(int lowerElimit, int upperElimit, int shell, int EinPreviousShells) {
        if ((concirclesView.atomicnumber >= lowerElimit) && (concirclesView.atomicnumber <= upperElimit) && (!correct)) {

            if (concirclesView.NumberElectronsInEachShell[shell] == (concirclesView.atomicnumber - EinPreviousShells)) {
                setCorrect();
            } else {
                final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
                textViewToChange.setText("Too many electrons in a shell");
                setIncorrect();
            }
        } else {
            final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrong);
            textViewToChange.setText("Maximum of 2 electrons in shell 1, 8 electrons in shells 2 and 3");
            setIncorrect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        concirclesView.touchcanvasnumber = 0;
    }

    //Takes user to the element with one atomic number higher, or if on random setting, takes the user to a new
//random element
    public void onClickNext(View view) {

        if (purchaseState != 0 && turnNumber >= maxTurns) {

            Intent getInAppBillingScreen = new Intent(ShellsScreen.this, InAppBillingScreen.class);
            startActivity(getInAppBillingScreen);

        } else
            turnNumber++;
        setNextElement();
    }

    //Takes user to the element with one atomic number higher
    private void setNextElement() {
        if ((concirclesView.atomicnumber == 20 && (startValue == 11 || startValue == 31)) ||
                (concirclesView.atomicnumber == 35 && (startValue ==13 || startValue == 33))) {
            concirclesView.setToast("No larger elements available");
        } else {
            //setNextElement_random(startValue);
            setAtomicnumber(startValue);
            reset();
        }
    }
    /*public void setNextElement_random(int levelValue) {
        setAtomicnumber(levelValue);
        reset();
    }*/

    //clears the canvas
    public void onClickClear(View view) {
        reset();
    }

    private void reset() {
        concirclesView.touchcanvasnumber = 0;
        concirclesView.changeelement = true;
        concirclesView.setElectronsInEachShellToZero();
        concirclesView.resetElectronsToZero();
        concirclesView.invalidate();
    }
    //removes that last electron that was added, if the user keeps pressing undo, then it keeps removing electrons, most recent ones
    //added are removed first
    public void onClickUndo(View view) {
        //call method from concirclesView which removes an electron, and resets touchcanvasnumber etc. to the correct value
        concirclesView.undo();
    }

    //Takes user to the element with an atomic number one less than they are currently on.  If current
    //element is hydrogen, and they press previous, a toast comes up saying "no smaller elements available"
    public void onClickPrevious(View view) {

        turnNumber--;
        if (concirclesView.atomicnumber < 2 && (startValue == 11 || startValue == 13 || startValue == 31 || startValue == 33)) {
            String noSmallerElements = "No smaller elements available";
            Toast toast = Toast.makeText(this, noSmallerElements, Toast.LENGTH_SHORT);
            LinearLayout toastLayout = (LinearLayout) toast.getView();

            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setGravity(Gravity.CENTER);
            toastTV.setTextSize(30);
            toast.show();
        } else {
            switch (startValue) {
                case 11: case 31:
                    concirclesView.atomicnumber--;
                    break;
                case 13: case 33:
                    getPreviousAtomthenIon();
            }

            concirclesView.touchcanvasnumber = 0;
            concirclesView.changeelement = true;
            concirclesView.invalidate();
        }
    }

    private void getNextAtomThenIon() {
        if (concirclesView.atomicnumber == 0 || concirclesView.atomicnumber == 21 ||
                concirclesView.atomicnumber == 2 || concirclesView.atomicnumber == 5 ||
                concirclesView.atomicnumber == 6 || concirclesView.atomicnumber == 10 ||
                concirclesView.atomicnumber == 14 || concirclesView.atomicnumber == 18) {
            concirclesView.atomicnumber++;
        } else if (concirclesView.atomicnumber == 1 || concirclesView.atomicnumber == 3 || concirclesView.atomicnumber == 4) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 20;
        } else if (concirclesView.atomicnumber == 22) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 20;
        } else if (concirclesView.atomicnumber == 23 || concirclesView.atomicnumber == 24) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 19;
        } else if (concirclesView.atomicnumber == 7 || concirclesView.atomicnumber == 8 || concirclesView.atomicnumber == 9) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 18;
        } else if (concirclesView.atomicnumber == 25 || concirclesView.atomicnumber == 26 || concirclesView.atomicnumber == 27) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 17;
        } else if (concirclesView.atomicnumber == 11 || concirclesView.atomicnumber == 12 || concirclesView.atomicnumber == 13) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 17;
        } else if (concirclesView.atomicnumber == 28 || concirclesView.atomicnumber == 29 || concirclesView.atomicnumber == 30) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 16;
        } else if (concirclesView.atomicnumber == 15 || concirclesView.atomicnumber == 16 || concirclesView.atomicnumber == 17) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 16;
        } else if (concirclesView.atomicnumber == 31 || concirclesView.atomicnumber == 32 || concirclesView.atomicnumber == 33) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 15;
        } else if (concirclesView.atomicnumber == 19 || concirclesView.atomicnumber == 20) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 15;
        } else {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 14;
        }
    }

    private void getPreviousAtomthenIon() {
        if (concirclesView.atomicnumber == 19 || concirclesView.atomicnumber == 15 ||
                concirclesView.atomicnumber == 11 || concirclesView.atomicnumber == 7 ||
                concirclesView.atomicnumber == 6 || concirclesView.atomicnumber == 3 ||
                concirclesView.atomicnumber == 22 ) {
            concirclesView.atomicnumber--;
        } else if (concirclesView.atomicnumber == 2 ) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 20;
        } else if (concirclesView.atomicnumber == 21 || concirclesView.atomicnumber == 23 || concirclesView.atomicnumber == 24) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 20;
        } else if (concirclesView.atomicnumber == 4 || concirclesView.atomicnumber == 5) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 19;
        } else if (concirclesView.atomicnumber == 25 || concirclesView.atomicnumber == 26 || concirclesView.atomicnumber == 27) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 18;
        } else if (concirclesView.atomicnumber == 8 || concirclesView.atomicnumber == 9 || concirclesView.atomicnumber == 10) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 17;
        } else if (concirclesView.atomicnumber == 29 || concirclesView.atomicnumber == 30 || concirclesView.atomicnumber == 28) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 17;
        } else if (concirclesView.atomicnumber == 12 || concirclesView.atomicnumber == 13 || concirclesView.atomicnumber == 14) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 16;
        } else if (concirclesView.atomicnumber == 31 || concirclesView.atomicnumber == 32 || concirclesView.atomicnumber == 33) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 16;
        } else if (concirclesView.atomicnumber == 16 || concirclesView.atomicnumber == 17 || concirclesView.atomicnumber == 18) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 15;
        } else if (concirclesView.atomicnumber == 34 || concirclesView.atomicnumber == 35) {
            concirclesView.atomicnumber = concirclesView.atomicnumber - 15;
        } else if (concirclesView.atomicnumber == 20) {
            concirclesView.atomicnumber = concirclesView.atomicnumber + 14;
        }
    }

}



