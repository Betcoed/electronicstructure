package com.sciencehighgames.electronicstructure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

/**
 * Created by sarahhinsley on 17/03/2015.
 */
public class LevelCompleted extends Activity{

    int startValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.level_completed_layout);

        Intent getStartValue = getIntent();

        startValue = getStartValue.getIntExtra("passStartValue", 0);

        new CountDownTimer(3000, 50) {

            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {

                finish();

                switch (startValue) {

                        case 11:
                            Intent intent11 = new Intent(getApplicationContext(), ElecStructsOnlyEasy.class);
                            intent11.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent11);
                            break;
                        case 13:
                            Intent intent13 = new Intent(getApplicationContext(), ElecStructsOnlyMedium.class);
                            intent13.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent13);
                            break;
                    case 21:
                        Intent intent21 = new Intent(getApplicationContext(), Level2Choices.class);
                        intent21.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent21);
                        break;
                    case 22:
                        Intent intent22 = new Intent(getApplicationContext(), Level2Choices.class);
                        intent22.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent22);
                        break;
                    case 23:
                        Intent intent23 = new Intent(getApplicationContext(), Level2Choices.class);
                        intent23.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent23);
                        break;
                    case 31:
                        Intent intent31 = new Intent(getApplicationContext(), Level3EasyChoices.class);
                        intent31.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent31);
                        break;
                    case 33:
                        Intent intent33 = new Intent(getApplicationContext(), Level3MediumChoices.class);
                        intent33.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent33);
                        break;
                    default:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }


            }
        }.start();
    }
}

