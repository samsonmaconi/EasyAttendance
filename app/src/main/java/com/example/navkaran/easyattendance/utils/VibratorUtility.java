package com.example.navkaran.easyattendance.utils;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

/**
 * This is a Singleton Utility Class for
 * initiating device vibrations. It is instantiated
 * at the first call of it's static vibrate method.
 */
public class VibratorUtility {
    private static String TAG = "VibratorUtility";
    private static Vibrator vibrator;
    private static VibratorUtility vibratorUtility;


    private VibratorUtility(Context context){
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
    }

    public static void vibrate(final Context context, final int duration, final int count) {
        if(vibratorUtility==null){
            vibratorUtility = new VibratorUtility(context);
        }

        if (vibratorUtility.vibrator.hasVibrator()) {
            Log.d(TAG, "Has Vibrator");
            Log.d(TAG, "Count: " + count);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int c = count;
                    while (c-- > 0) {
                        exec(duration);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }).start();

        } else {
            Log.d(TAG, "No Vibrator");
        }
    }

    //overloaded method to send boolean vibration feedback
    public static void vibrate(final Context context, boolean positive) {
        if(!positive){
            vibrate(context, 75, 2);
        }else{
            vibrate(context, 300, 1);
        }
    }

    private static void exec(int duration) {
        vibratorUtility.vibrator.vibrate(duration);
        Log.d(TAG, "Vibrated for " + duration + " milliseconds");
    }

}
