package com.sciencehighgames.electronicstructure;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.content.res.Configuration;

import com.android.vending.billing.IInAppBillingService;
import com.sciencehighgames.electronicstructure.util.IabHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by sarahhinsley on 23/03/2015.
 */
public class NumberForm extends Activity {

    int startValue;
    ImageView imageView_Symbol;
    ShellsScreen shellsScreen = new ShellsScreen();
    private ConcirclesView concirclesView;
    //ConcirclesView concirclesView = new ConcirclesView(this, null);
    Bitmap bMap;
    EditText shellEditText[] = new EditText[4];
    MediaPlayer negativeSound, correctSound;
    int[] electronsInShells = new int[4];

    private static final String KEY_TEXT_VALUE0 = "textValue0";
    private static final String KEY_TEXT_VALUE1 = "textValue1";
    private static final String KEY_TEXT_VALUE2 = "textValue2";
    private static final String KEY_TEXT_VALUE3 = "textValue3";
    String savedNumbers[] = new String[4];

    //stuff for in-app billing
    IInAppBillingService mService;
    int purchaseState = 3;
    IabHelper iabHelper;
    public static final int maxTurns = 6;
    int turnNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        negativeSound = MediaPlayer.create(this, R.raw.negativebeep);
        correctSound = MediaPlayer.create(this, R.raw.correctsound);

       setContentView(R.layout.numberform);

        Intent getStartValue = getIntent();
        ConcirclesView concirclesView = new ConcirclesView(this, null);
        startValue = getStartValue.getIntExtra("passStartValue", 0);
        turnNumber = getStartValue.getIntExtra("passTurnNumber", 0);

        shellsScreen.setAtomicnumber(startValue);

        shellEditText[0] = (EditText)findViewById(R.id.editText_shell1);
        shellEditText[1] = (EditText)findViewById(R.id.editText_shell2);
        shellEditText[2] = (EditText)findViewById(R.id.editText_shell3);
        shellEditText[3] = (EditText)findViewById(R.id.editText_shell4);

        if (savedInstanceState != null) {
            savedNumbers[0] = savedInstanceState.getString(KEY_TEXT_VALUE0);
            savedNumbers[1] = savedInstanceState.getString(KEY_TEXT_VALUE1);
            savedNumbers[2] = savedInstanceState.getString(KEY_TEXT_VALUE2);
            savedNumbers[3] = savedInstanceState.getString(KEY_TEXT_VALUE3);
            for (int i=0; i<4; i++) {
                shellEditText[i].setText(savedNumbers[i]);
            }
            concirclesView.atomicnumber = savedInstanceState.getInt("atomicnumber");
        }

        concirclesView.assignAtomIonLabel();

        bMap = concirclesView.getAtomionLabel(concirclesView.atomicnumber);
        imageView_Symbol = (ImageView) findViewById(R.id.imageView_symbol);
        imageView_Symbol.setImageBitmap(bMap);

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
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TEXT_VALUE0, String.valueOf(shellEditText[0].getText()));
        outState.putString(KEY_TEXT_VALUE1, String.valueOf(shellEditText[1].getText()));
        outState.putString(KEY_TEXT_VALUE2, String.valueOf(shellEditText[2].getText()));
        outState.putString(KEY_TEXT_VALUE3, String.valueOf(shellEditText[3].getText()));
        outState.putInt("atomicnumber", concirclesView.atomicnumber);
    }

    public void onChooseNext(View view) {

        if (purchaseState != 0 && turnNumber >= maxTurns) {

            Intent getInAppBillingScreen = new Intent(NumberForm.this, InAppBillingScreen.class);
            startActivity(getInAppBillingScreen);

        } else
        turnNumber++;
            resetForNewElement();

    }

    //a new atomic number is generated randomly, and its symbol is shown, then all edit text boxes are emptied,
    private void resetForNewElement() {
        //atomic number is reset first, then Bitmap image can be reset based on atomic number
        ConcirclesView concirclesView = new ConcirclesView(this, null);
        //generate different atomicnumber
        shellsScreen.setAtomicnumber(startValue);
        //change symbol
        bMap = concirclesView.getAtomionLabel(concirclesView.atomicnumber);
        imageView_Symbol.setImageBitmap(bMap);
        //make edit text boxes empty, and with black text
        clearNumberForm();
    }

    public void onChooseCheck(View view) {
        //set TextViews so that if a user has put nothing in a box, then this is translated as zero
        setEmptyTextViewsToZero();
        changeToNumbers();
        //which method is called here depends on whether the image shown is an atom or an ion
        if (concirclesView.atomicnumber <= 20) {
            checkAtomsInNumberForm();
        } else {
            checkIonsInNumberForm();
        }
    }

    public void onChooseClear(View view) {
        //reset the textviews to empty
        clearNumberForm();
    }
    private void clearNumberForm() {

            for (int i = 0; i < 4; i++) {
                shellEditText[i].getText().clear();
                shellEditText[i].setTextColor(Color.BLACK);
            }
    }

    private void checkIonsInNumberForm() {
        if (electronsInShells[3] != 0) {
            setIncorrect("This ion has no electrons in shell 4", 3);
        } else if (electronsInShells[0] == 1){
            setIncorrect("Wrong number of electrons in shell 1", 0);
        } else if (electronsInShells[2] != 0) {
            if (concirclesView.atomicnumber >= 21 && concirclesView.atomicnumber <=30) {
                setIncorrect("There are no electrons in shell 3", 2);
            } else if (concirclesView.atomicnumber >= 30 && concirclesView.atomicnumber <=35) {
                if (electronsInShells[2] != 8) {
                    setIncorrect("There are 8 electrons in shell 3", 2);
                } else if (electronsInShells[3] == 0 && electronsInShells[1] == 8 && electronsInShells[0] == 2) {
                        level3correct();
                } else {
                    setIncorrect("Wrong number of electrons in shell 1 or 2", 0);
                    shellEditText[1].setTextColor(Color.RED);
                }
            }
        } else if (electronsInShells[1] != 0) {
            if (concirclesView.atomicnumber >= 21 && concirclesView.atomicnumber <= 24) {
                setIncorrect("There are no electrons in shell 2", 1);
            } else if (concirclesView.atomicnumber >= 25 && concirclesView.atomicnumber <= 30) {
                if (electronsInShells[1] != 8) {
                    setIncorrect("There are 8 electrons in shell 2", 1);
                } else if (electronsInShells[3] == 0 && electronsInShells[0] == 2 && electronsInShells[2] == 0) {
                    level3correct();
                } else {
                    setIncorrect("Wrong number of electrons in shell 1", 0);
                }
            } else if (concirclesView.atomicnumber >= 31 && concirclesView.atomicnumber <= 35) {
                setIncorrect("there are 8 electrons in shell 3", 2);
            }
        } else if (electronsInShells[0] != 0) {
            if (concirclesView.atomicnumber >= 25 && concirclesView.atomicnumber <=35) {
                setIncorrect("There are 8 electrons in shell 2", 1);
            } else if (concirclesView.atomicnumber == 21) {
                setIncorrect("There are no electrons in shell 1", 0);
            } else if (concirclesView.atomicnumber >= 22 && concirclesView.atomicnumber <= 24) {
                if (electronsInShells[0] == 2) {
                    level3correct();
                } else {
                    setIncorrect("There are 2 electrons in shell 1", 0);
                }
            }
        } else if (electronsInShells[0] == 0) {
            if (concirclesView.atomicnumber == 21) {
                level3correct();
            } else {
                setIncorrect("Type the number of electrons that go in each shell", 0);
            }
        }

    }

    //this method checks if the user has typed in the correct numbers, if the symbol shown is an atom.
    private void checkAtomsInNumberForm() {

        //change text added to numbers

       //first check something other than zero has been typed int the first shell box
        if (electronsInShells[0] == 0 ) {
           // final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
           // textViewToChange.setText("This element requires electrons in shell 1");
            //shellEditText[0].setTextColor(Color.RED);
            setIncorrect("This element requires electrons in shell 1", 0);
            //now check H and He
        } else if (concirclesView.atomicnumber == 1 || concirclesView.atomicnumber == 2) {
            //H and He shouldn't have any electrons in shells 2, 3 or 4
            if (electronsInShells[1] == 0 && electronsInShells[2] == 0 &&
                    electronsInShells[3] == 0) {
                // the '1' below refers to the real shell number, not the number in an array.
                    mayBeCorrect(1);
            } else {
              //  final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
              //  textViewToChange.setText("This element has no electrons in shells 2, 3 or 4");
                setIncorrect("This element has no electrons in shells 2, 3 or 4", 1);
                for (int i=0; i<4; i++) {
                    if (i!=0) {
                        shellEditText[i].setTextColor(Color.RED);
                    }
                }
            }
        } else
        //checking period 2 elements
        if (concirclesView.atomicnumber >= 3 && concirclesView.atomicnumber <= 10) {
            //period 2 elements shouldn't have any electrons in shells 3 and 4
            if (electronsInShells[2] == 0 &&
                    electronsInShells[3] == 0) {
                //period 2 elements should have 2 electrons in shell 1
                if (electronsInShells[0] == 2) {
                    // the '2' below refers to the real shell number, not the number in an array.
                    mayBeCorrect(2);
                } else {
                  //  final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
                  //  textViewToChange.setText("This element has 2 electrons in shell 1");
                    setIncorrect("This element has 2 electrons in shell 1", 0);
                   // shellEditText[0].setTextColor(Color.RED);
                }
            } else {
               // final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
               // textViewToChange.setText("This element has no electrons in shells 3 or 4");
                setIncorrect("This element has no electrons in shells 3 or 4", 2);
               // shellEditText[2].setTextColor(Color.RED);
                shellEditText[3].setTextColor(Color.RED);
            }
        } else
        //checking period 3 elements
        if (concirclesView.atomicnumber >= 11 && concirclesView.atomicnumber <= 18) {
            //period 3 elements shouldn't have any electrons in shell 4

            if (electronsInShells[3] == 0) {

//period 3 elements should have 8 electrons in shell 2
                if (electronsInShells[1] == 8) {

//period 3 elements should have 2 elecrons in shell 1
                    if (electronsInShells[0] == 2) {

                        // the '3' below refers to the real shell number, not a number in an array.
                            mayBeCorrect(3);
                    } else {
                        //this just brings up the incorrect sad face with "2 electrons in shell 1 are required
                       // twoElecsInShell1();
                        setIncorrect("This element has 2 electrons in shell 1", 0);
                    }
                } else {
                   // final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
                  //  textViewToChange.setText("This element has 8 electrons in shell 2");
                    setIncorrect("This element has 8 electrons in shell 2", 1);
                   // shellEditText[1].setTextColor(Color.RED);
                }

            } else {

               // final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
               // textViewToChange.setText("This element has no electrons in shells 3 or 4");
                setIncorrect("This element has no electrons in shells 3 or 4", 3);
               // shellEditText[3].setTextColor(Color.RED);
            }
        } else
        //checking K and Ca
        if (concirclesView.atomicnumber == 19 || concirclesView.atomicnumber == 20) {
            //K and Ca should have 2 electrons in shell 1, and 8 electrons in shells 2 and 3
            if (electronsInShells[1] == 8 && electronsInShells[2] == 8) {
                if (electronsInShells[0] == 2) {
                    // the '4' below refers to the real shell number, not a number in an array.
                    mayBeCorrect(4);
                } else {
                   // twoElecsInShell1();
                    setIncorrect("This element has 2 electrons in shell 1", 0);
                }
            } else {

              //  final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
              //  textViewToChange.setText("This element has 8 electrons in shells 2 and 3");
                setIncorrect("This element has 8 electrons in shells 2 and 3", 1);
                //set text colour to red
               // shellEditText[1].setTextColor(Color.RED);
                shellEditText[2].setTextColor(Color.RED);
            }
        }
    }

    private void changeToNumbers() {
        String retrieveTexts[] = new String[4];

        for (int i = 0; i < 4; i++) {
            retrieveTexts[i] = shellEditText[i].getText().toString();
            electronsInShells[i] = Integer.parseInt(retrieveTexts[i]);
        }
    }

    //make the buttons disappear and the incorrect message appear
    private void setIncorrect(String hint, int energylevel) {

        final TextView textViewToChange = (TextView) findViewById(R.id.HintForWrongNumForm);
        textViewToChange.setText(hint);
        shellEditText[energylevel].setTextColor(Color.RED);
        findViewById(R.id.textView_instr1).setVisibility(View.GONE);
        findViewById(R.id.textView_instr2).setVisibility(View.GONE);
        findViewById(R.id.incorrectForNumForm).setVisibility(View.VISIBLE);
        negativeSound.start();
        new CountDownTimer(3000, 50) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                findViewById(R.id.incorrectForNumForm).setVisibility(View.GONE);
                findViewById(R.id.textView_instr1).setVisibility(View.VISIBLE);
                findViewById(R.id.textView_instr2).setVisibility(View.VISIBLE);
            }
        }.start();
    }
//this is called to check the outer shell text box, to see if the user has put the right number in that
    //textbox for that particular atom.
    private void mayBeCorrect(int shellnumber) {
        //first take whatever the user has entered and turn it into a set of integers

        int sum = 0;

        for (int i : electronsInShells) {
            sum += i;
        }
        if (concirclesView.atomicnumber == sum) {
            level3correct();
        } else {
            String stringHint = "Wrong number of electrons in shell " + shellnumber;
            setIncorrect(stringHint, shellnumber-1);

        }
    }

    private void level3correct() {

        findViewById(R.id.textView_instr1).setVisibility(View.GONE);
        findViewById(R.id.textView_instr2).setVisibility(View.GONE);
        findViewById(R.id.correctForNumForm).setVisibility(View.VISIBLE);
        turnNumber++;
        correctSound.start();
        new CountDownTimer(3000, 50) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                findViewById(R.id.correctForNumForm).setVisibility(View.GONE);
                findViewById(R.id.textView_instr1).setVisibility(View.VISIBLE);
                findViewById(R.id.textView_instr2).setVisibility(View.VISIBLE);
                if (purchaseState != 0 && turnNumber >= maxTurns) {

                    Intent getInAppBillingScreen = new Intent(NumberForm.this, InAppBillingScreen.class);
                    startActivity(getInAppBillingScreen);

                } else {

                    if (startValue > 30) {
                        //finish();
                        Intent getStartValue = new Intent(NumberForm.this, PENScreen.class);

                        getStartValue.putExtra("passStartValue", startValue);
                        getStartValue.putExtra("passAtomicNumber", concirclesView.atomicnumber);
                        getStartValue.putExtra("passTurnNumber",turnNumber);
                        startActivity(getStartValue);
                    } else {
                        resetForNewElement();
                    }
                }
            }
        }.start();
    }

    private void setEmptyTextViewsToZero() {

        String ed_text[] = new String[4];

        for (int i=0; i<4; i++) {
            ed_text[i] = shellEditText[i].getText().toString().trim();

            if (ed_text[i].isEmpty() || ed_text[i].length() == 0 || ed_text[i].equals("") || ed_text[i] == null) {
                shellEditText[i].setText("0");
            }
        }

    }
}
