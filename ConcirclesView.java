package com.sciencehighgames.electronicstructure;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.os.Parcelable;
import android.media.MediaPlayer;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * Created by sarahhinsley on 12/01/2015.
 */
public class ConcirclesView extends View {


    Bitmap[] electronArray = new Bitmap[20];
   // Bitmap[] shells = new Bitmap[1];
    double[] elecposX_array = new double[20];
    double[] elecposY_array = new double[20];
    static Bitmap[] atomionLabel = new Bitmap[36];

    double Zoriginaldistfromcentre;

    double[] ZvalueElectronPosition = new double[20];
    public static int[] NumberElectronsInEachShell = new int[4];
    static int atomicnumber = 0;
    MediaPlayer clicksound = MediaPlayer.create(this.getContext(), R.raw.click);
    public boolean changeelement = false;
    boolean undo = false;
    boolean orientationchanged = false;

    boolean isElectronTooClose;
    double x, y;
    static int touchcanvasnumber = 0;

    double[] radius = new double[4];

    //upper and lower limits of shell distance from centre
    double[][] shellDistanceFromCentre = new double[4][2];

     double[] radiusSquared = new double[4];

    int parentWidth;
    int parentHeight;
    int oldParentHeight;
    int oldParentWidth;


    public ConcirclesView(Context shellsScreen, AttributeSet attrs) {
        super(shellsScreen, attrs);

        //shells[0] = BitmapFactory.decodeResource(getResources(), R.drawable.concircles);

        //put 20 cyan balls into an array - these represent electrons
        for (int i = 0; i < 20; i++) {
            electronArray[i] = BitmapFactory.decodeResource(getResources(), R.drawable.cyanballnocanvas25px);
            elecposX_array[i] = 0.0;
            elecposY_array[i] = 0.0;
        }
    //assign bitmap images to a particular slot in the array atomionLabel
        assignAtomIonLabel();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //here I am finding out the width of the custom view if it's landscape, or the height if it's portrait.
        //I want both of these to stay as they have been set in the xml file
        //I then need the height and width to be the same, so the shells are circles rather than ovals.
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            parentHeight = parentWidth;
            this.setMeasuredDimension(parentWidth, parentHeight);
        } else {
            parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            parentWidth = parentHeight;
            this.setMeasuredDimension(parentWidth, parentHeight);
        }
        //ON SOME DEVICES, THE OUTER SHELL IS GOING OFF THE SCREEN AT THE EDGES.  IT WOULD BE NICE TO PUT SOME CODE HERE
        // TO CHECK WHETHER THE CUSTOM VIEW IS BIGGER THAN THE SCREEN SIZE AND IF IT IS, MAKE THE CUSTOM VIEW SMALLER.
        //I CAN'T FIND OUT HOW TO GET THE WIDTH/HEIGHT OF THE PHGONE SCREEN THOUGH.  TRIED LOTS OF THINGS, NONE WORK.
    }
//size of shells may change when screen is re-orientated, so this re-calculates where the electrons should go
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (orientationchanged) {
            parentWidth = MeasureSpec.getSize(w);
            parentHeight = MeasureSpec.getSize(h);
            double customViewSizeChange = (double) parentHeight / (double) oldParentHeight;
            for (int i = 0; i < 20; i++) {
                elecposX_array[i] = customViewSizeChange * elecposX_array[i];
                elecposY_array[i] = customViewSizeChange * elecposY_array[i];
                ZvalueElectronPosition[i] = customViewSizeChange * ZvalueElectronPosition[i];
                orientationchanged = false;
                oldParentHeight = 0;
                oldParentWidth = 0;

            }
        }
    }


    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Style.STROKE);
        circlePaint.setStrokeWidth((float) 5.0);

        radius[0] = (float) (0.15 * canvas.getWidth());
        radius[1] = (float) (0.26 * canvas.getWidth());
        radius[2] = (float) (0.37 * canvas.getWidth());
        radius[3] = (float) (0.48 * canvas.getWidth());

        for (int i =0; i<4; i++) {
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, (float) radius[i], circlePaint);
        }
        double percentOfRadius0 = 0.3 * radius[0];
        for (int i = 0; i<4; i++) {
                shellDistanceFromCentre[i][0] = radius[i] - percentOfRadius0;
                shellDistanceFromCentre[i][1] = radius[i] + percentOfRadius0;
            //the radius is the z of pythagorus theorm, zsquared = xsquared + ysquared, so we need to square the
            //radius for some calculations later on
            radiusSquared[i] = radius[i] * radius[i];
            }


        if (touchcanvasnumber == 20) {

            setToast("No more electrons available");
            canvas.drawBitmap(atomionLabel[atomicnumber], (canvas.getWidth() / 2 - (atomionLabel[atomicnumber].getWidth() / 2)),
                    (canvas.getHeight() / 2 - (atomionLabel[atomicnumber].getHeight() / 2)), null);

            drawOtherElectrons(canvas);
        } else {

            if (changeelement == true) {
                canvas.drawBitmap(atomionLabel[atomicnumber], (canvas.getWidth() / 2 - (atomionLabel[atomicnumber].getWidth() / 2)),
                        (canvas.getHeight() / 2 - (atomionLabel[atomicnumber].getHeight() / 2)), null);
                changeelement = false;
                if (undo == true) {
                    drawOtherElectrons(canvas);
                    undo = false;

                }
            } else {
                setElectronsInEachShellToZero();
                canvas.drawBitmap(atomionLabel[atomicnumber], (canvas.getWidth() / 2 - (atomionLabel[atomicnumber].getWidth() / 2)),
                        (canvas.getHeight() / 2 - (atomionLabel[atomicnumber].getHeight() / 2)), null);
                //redraw electrons already there
                drawOtherElectrons(canvas);
//work out the distance of the electron from the centre, this is used to work out which shell it's been put in
                electronDistFromCentre(canvas);
                //check which shell the electron has been placed nearest to.  This method also then "snaps" the electron
                //to the nearest shell line, making the look of it neater
                //the electron is then made visible
                nearestToWhichShell(canvas);
                //go through all the electrons placed so far and count how many are in each shell.
                //This information will then be used to check the users answer against where the electrons SHOULD
                //have been placed
                countElectronsinEachShell();
            }
        }

    }
    //call method which calculates the distance of the point touched from the centre of the
    //concentric circles
    public double electronDistFromCentre(Canvas canvas) {
        double tempx = x - canvas.getWidth() / 2;
        double tempy = y - canvas.getHeight() / 2;
        return Zoriginaldistfromcentre = Math.sqrt((tempx * tempx) + (tempy * tempy));
    }

    //find out which shell it's nearest to, and reset position of electron to nearest shell
    public void nearestToWhichShell(Canvas canvas) {

        if (Zoriginaldistfromcentre > shellDistanceFromCentre[0][0] && Zoriginaldistfromcentre < shellDistanceFromCentre[0][1]) {
            //call method to reset electron to position so it snaps to the shell1
            resetElectronPosition(radiusSquared[0], canvas);
        } else if (Zoriginaldistfromcentre > shellDistanceFromCentre[1][0] && Zoriginaldistfromcentre < shellDistanceFromCentre[1][1]) {
            resetElectronPosition(radiusSquared[1], canvas);
        } else if (Zoriginaldistfromcentre > shellDistanceFromCentre[2][0] && Zoriginaldistfromcentre < shellDistanceFromCentre[2][1]) {
            resetElectronPosition(radiusSquared[2], canvas);
        } else if (Zoriginaldistfromcentre > shellDistanceFromCentre[3][0] && Zoriginaldistfromcentre < shellDistanceFromCentre[3][1]) {
            resetElectronPosition(radiusSquared[3], canvas);
        } else {
            //where a method is called which makes a toast appear and tell the user to touch on the white line of a shell
            setToast("Touch on the white line of a shell");
        }
    }

    private void resetElectronPosition(double shellZsquared, Canvas canvas) {
        //this method will also check whether a user has tried to place one electron on top of another.
        //It can  cause a bug if they do this, because they may think they've put less electrons in than they really have.

        isElectronTooClose = false;
        //check if the electron has been placed in an area between 10 O'clock to 2 O'Clock on the canvas,
        //or between 4 O'clock and 8 O'clock

        if (x >= 236 && x <= 444) {
            //if yes then reset the y value
            double temp = Math.abs(shellZsquared - ((x - canvas.getWidth() / 2) * (x - canvas.getWidth() / 2)));
            //I've put the following if statement in because at one point I was trying to sqrt a negative when the user
            //pressed in certain parts of the screen.  However, being careful to set the upper and lower values of x for
            //the above if statement, and making the previous line an absolute value, seems to have sorted the
            //problem out, but I've left the if statement below in just in case.
            if (temp < 0) {
                setToast("An electron has not been placed, try again");
            } else {
                if (y < canvas.getHeight() / 2) {
                    y = Math.abs(Math.sqrt(temp) - canvas.getWidth() / 2);
                } else {
                    y = Math.sqrt(temp) + canvas.getWidth() / 2;
                }
            }
            y = y - electronArray[0].getWidth() / 2;
            x = x - electronArray[0].getWidth() / 2;

//call method to check whether the user is trying to put an electron too close to another one already there
            electronTooClose(x, y);
            //now that the x and y values have been reset so the electrons snaps to the nearest shell line,
            // make the electron visible


            if (!isElectronTooClose) {


                makeElectronVisible(canvas, shellZsquared);

            }

        } else {
            //if no then reset the x value
            double temp2 = Math.abs(shellZsquared - ((y - canvas.getHeight() / 2) * (y - canvas.getHeight() / 2)));
            //see above for why this if statement is here
            if (temp2 < 0) {
                setToast("An electron has not been placed, try again");
            } else {
                if (x < canvas.getWidth() / 2) {

                    x = Math.abs(Math.sqrt(temp2) -
                            canvas.getHeight() / 2);
                } else {
                x = Math.sqrt(temp2) +
                        canvas.getHeight() / 2;
            }
        }

            y = y - electronArray[0].getWidth() / 2;
            x = x - electronArray[0].getWidth() / 2;

            //check whether or not the new electron has been placed too close to an old electron
            electronTooClose(x, y);
            //make electron visible, BUT ONLY IF ELECTRONS TRYING TO BE ADDED IS NOT TOO CLOSE - SO NEEDS TO BE
            //IN AN IF STATEMENT
            if (!isElectronTooClose) {

                makeElectronVisible(canvas, shellZsquared);
            }
        }
    }
//method to make an electron appear in the shell nearest to where the user touched the screen
    private double[] makeElectronVisible(Canvas canvas, double Zsquared) {

        elecposX_array[touchcanvasnumber] = x;
        elecposY_array[touchcanvasnumber] = y;
        ZvalueElectronPosition[touchcanvasnumber] = Math.sqrt(Zsquared);
        canvas.drawBitmap(electronArray[touchcanvasnumber], (float) x,
                (float) y, null);
        ++touchcanvasnumber;
        clicksound.start();
      //  ZvalueElectronPosition[touchcanvasnumber] = Math.round(ZvalueElectronPosition[touchcanvasnumber]);
        return ZvalueElectronPosition;
    }
//the array that contains the count of number of electrons in each shell is set to zero here
    public void setElectronsInEachShellToZero() {
        for (int i = 0; i < 4; i++) {
            NumberElectronsInEachShell[i] = 0;
        }
    }
//method which counts the number of electrons in each shell.
    //This information is used later, in the onCheck method in the ShellsScreen class to check whether the user
    //has placed electrons in the right place for that particular atom/ion.
    public int[] countElectronsinEachShell() {


        for (int i = 0; i < ZvalueElectronPosition.length; i++) {
            if (Math.round(ZvalueElectronPosition[i]) == Math.round(radius[0])) {
                NumberElectronsInEachShell[0]++;
            } else if (Math.round(ZvalueElectronPosition[i]) == Math.round(radius[1])) {
                NumberElectronsInEachShell[1]++;
            } else if (Math.round(ZvalueElectronPosition[i]) == Math.round(radius[2])) {
                NumberElectronsInEachShell[2]++;
            } else if (Math.round(ZvalueElectronPosition[i]) == Math.round(radius[3])) {
                NumberElectronsInEachShell[3]++;
            }
        }
        return NumberElectronsInEachShell;
    }
//returns number of electrons in each shell, for use in the ShellsScreen class, where the answer is checked
    //in the onCheck method
    public int[] getNumberElectronsInEachShell() {
        return NumberElectronsInEachShell;
    }
//returns the value of the z value of the electron position.
    //"Z value" is the z from pythagoras: z^2 = x^2 + y^2.  This Z value is the position of the electron
    //from the centre, so can be checked against the Z value of each shell, to find out which shell
    // an electron has been placed in.
    public void resetElectronsToZero() {
        for (int i=0; i<20; i++) {
            ZvalueElectronPosition[i] = 0;
            elecposX_array[i] = 0;
            elecposY_array[i] = 0;
        }

    }
    public double[] getZvalueElectronPosition() {
        return ZvalueElectronPosition;
    }

    //when a user presses the "undo" button, the most recent electron added will be removed.
    //Not only the electron has to be removed, but the number of electrons in whichever shell it was in
    //also has to be reduced by one, which is what the if...else if... statements are doing.
    //A user can press undo any number of times until all the electrons are removed - electrons will
    //be removed in order of "last in first out".
    public void undo() {
        undo = true;
        changeelement = true;
        if (touchcanvasnumber == 0) {
            setToast("There are no electrons left to remove");
            resetElectronsToZero();

            for (int i=0; i<20; i++) {
                ZvalueElectronPosition[i] = 0;
                elecposX_array[i] = 0;
                elecposY_array[i] = 0;
            }
        } else {

            if (Math.round(ZvalueElectronPosition[touchcanvasnumber - 1]) == Math.round(radius[0])) {

                NumberElectronsInEachShell[0]--;
            } else if (Math.round(ZvalueElectronPosition[touchcanvasnumber - 1]) == Math.round(radius[1])) {
                NumberElectronsInEachShell[1]--;
            } else if (Math.round(ZvalueElectronPosition[touchcanvasnumber - 1]) == Math.round(radius[2])) {
                NumberElectronsInEachShell[2]--;
            } else if (Math.round(ZvalueElectronPosition[touchcanvasnumber - 1]) == Math.round(radius[3])) {
                NumberElectronsInEachShell[3]--;
            }

            ZvalueElectronPosition[touchcanvasnumber - 1] = 0;
            elecposX_array[touchcanvasnumber - 1] = 0;
            elecposY_array[touchcanvasnumber - 1] = 0;
            getNumberElectronsInEachShell();
            touchcanvasnumber--;
            //the canvas is redrawn, with one less electron, once the electron is removed
            invalidate();
        }
    }

    private void drawOtherElectrons(Canvas canvas) {
        for (int i = 1; i < 21; i++) {
            if (touchcanvasnumber == i) {
                for (int j = 0; j < i; j++) {
                    canvas.drawBitmap(electronArray[j], (float) elecposX_array[j],
                            (float) elecposY_array[j], null);
                }
            }
        }
    }
    //checks whether user is trying to place an electron too close to one already there.
    private void electronTooClose(double xpos, double ypos) {
        for (int i = 0; i < elecposY_array.length; i++) {
            if (ypos > (elecposY_array[i] - 20) && ypos < (elecposY_array[i] + 20) && xpos > (elecposX_array[i] - 20) && (xpos < elecposX_array[i] + 20)) {
                isElectronTooClose = true;
                setToast("Attempt to place an electron too close to another one.  Try again");
            }
        }
    }
//Sets various toasts to pop-up when the user attempts to do something they can't do:
    //1.  Pressing next when already at Ca (no more elements).
    //2.  Pressing previous when already at H.
    //3.  Trying to add more than 20 elements.
    //4.  Trying to remove electrons when there are no more electrons left to remove.
    //5.  User tries to put an electron on top of another one (may cause a bug, because user may think they've
    //added one less electron than they actually have).
    public void setToast(String toastComment) {
    Toast toast_touchLine = Toast.makeText(getContext(), toastComment, Toast.LENGTH_SHORT);
    LinearLayout toastLayout = (LinearLayout) toast_touchLine.getView();
    TextView toastTV_touchLine = (TextView) toastLayout.getChildAt(0);
    toastTV_touchLine.setGravity(Gravity.CENTER);
    toastTV_touchLine.setTextSize(30);
    toast_touchLine.show();
}
    public void assignAtomIonLabel() {
        atomionLabel[1] = BitmapFactory.decodeResource(getResources(), R.drawable.hydrogen);
        atomionLabel[2] = BitmapFactory.decodeResource(getResources(), R.drawable.helium);
        atomionLabel[3] = BitmapFactory.decodeResource(getResources(), R.drawable.lithium);
        atomionLabel[4] = BitmapFactory.decodeResource(getResources(), R.drawable.beryllium);
        atomionLabel[5] = BitmapFactory.decodeResource(getResources(), R.drawable.boron);
        atomionLabel[6] = BitmapFactory.decodeResource(getResources(), R.drawable.carbon);
        atomionLabel[7] = BitmapFactory.decodeResource(getResources(), R.drawable.nitrogen);
        atomionLabel[8] = BitmapFactory.decodeResource(getResources(), R.drawable.oxygen);
        atomionLabel[9] = BitmapFactory.decodeResource(getResources(), R.drawable.fluorine);
        atomionLabel[10] = BitmapFactory.decodeResource(getResources(), R.drawable.neon);
        atomionLabel[11] = BitmapFactory.decodeResource(getResources(), R.drawable.sodium);
        atomionLabel[12] = BitmapFactory.decodeResource(getResources(), R.drawable.magnesium);
        atomionLabel[13] = BitmapFactory.decodeResource(getResources(), R.drawable.aluminium);
        atomionLabel[14] = BitmapFactory.decodeResource(getResources(), R.drawable.silicon);
        atomionLabel[15] = BitmapFactory.decodeResource(getResources(), R.drawable.phosphorus);
        atomionLabel[16] = BitmapFactory.decodeResource(getResources(), R.drawable.sulphur);
        atomionLabel[17] = BitmapFactory.decodeResource(getResources(), R.drawable.chlorine);
        atomionLabel[18] = BitmapFactory.decodeResource(getResources(), R.drawable.argon);
        atomionLabel[19] = BitmapFactory.decodeResource(getResources(), R.drawable.potassium);
        atomionLabel[20] = BitmapFactory.decodeResource(getResources(), R.drawable.calcium);
        atomionLabel[21] = BitmapFactory.decodeResource(getResources(), R.drawable.hydrogen_pos);
        atomionLabel[22] = BitmapFactory.decodeResource(getResources(),R.drawable.hydrogen_anion);
        atomionLabel[23] = BitmapFactory.decodeResource(getResources(), R.drawable.lithium_cation);
        atomionLabel[24] = BitmapFactory.decodeResource(getResources(), R.drawable.beryllium_cation);
        atomionLabel[25] = BitmapFactory.decodeResource(getResources(), R.drawable.nitrogen_anion);
        atomionLabel[26] = BitmapFactory.decodeResource(getResources(), R.drawable.oxygen_anion);
        atomionLabel[27] = BitmapFactory.decodeResource(getResources(), R.drawable.fluorine_anion);
        atomionLabel[28] = BitmapFactory.decodeResource(getResources(), R.drawable.sodium_cation);
        atomionLabel[29] = BitmapFactory.decodeResource(getResources(), R.drawable.magnesium_cation);
        atomionLabel[30] = BitmapFactory.decodeResource(getResources(), R.drawable.aluminium_cation);
        atomionLabel[31] = BitmapFactory.decodeResource(getResources(), R.drawable.phosphorus_anion);
        atomionLabel[32] = BitmapFactory.decodeResource(getResources(), R.drawable.sulphur_anion);
        atomionLabel[33] = BitmapFactory.decodeResource(getResources(), R.drawable.chlorine_anion);
        atomionLabel[34] = BitmapFactory.decodeResource(getResources(), R.drawable.potassium_cation);
        atomionLabel[35] = BitmapFactory.decodeResource(getResources(), R.drawable.calcium_cation);
    }
    public Bitmap getAtomionLabel(int elementIonNumber) {

            return atomionLabel[elementIonNumber];

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("touchcanvasnumber", this.touchcanvasnumber);
        bundle.putDoubleArray("eposX", this.elecposX_array);
        bundle.putDoubleArray("eposY", this.elecposY_array);
        bundle.putIntArray("NumEsInEachShell", this.NumberElectronsInEachShell);
        bundle.putInt("atomicnumber", this.atomicnumber);
        bundle.putDoubleArray("Zvalue", this.ZvalueElectronPosition);
        bundle.putInt("oldParentHeight",this.parentHeight);
        bundle.putInt("oldParentWidth", this.parentWidth);
      //  bundle.putDoubleArray("radius", this.radius);
        // ... save everything
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            touchcanvasnumber = bundle.getInt("touchcanvasnumber");
            elecposX_array = bundle.getDoubleArray("eposX");
            elecposY_array = bundle.getDoubleArray("eposY");
            atomicnumber = bundle.getInt("atomicnumber");
            NumberElectronsInEachShell = bundle.getIntArray("NumEsInEachShell");
            ZvalueElectronPosition = bundle.getDoubleArray("Zvalue");
            oldParentHeight = bundle.getInt("oldParentHeight");
            oldParentWidth = bundle.getInt("oldParentWidth");
           // radius = bundle.getDoubleArray("radius");
            // ... load everything
            state = bundle.getParcelable("instanceState");
        }
        invalidate();
        super.onRestoreInstanceState(state);
      changeelement = true;
        undo = true;
        orientationchanged = true;
    }
}

