package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

 class YHYManager {

     protected static final String DATABASE_NAME = "BillingDB";
     protected static final String TABLE_PURCHASE_STATUS = "purchase_status";
     protected static List<String> listApplicationSKU  = new ArrayList<>();

     protected SharedPreferences.Editor sp;
     public static final String IS_FRESH_START = "IS_FRESH_START";

     protected Activity activity;

     public YHYManager() {

     }


     public YHYManager(Activity activity) {
         this.activity = activity;

     }

     protected   void showToastMessage(Context context, String text){

        if(ConnectToPlay.getInstance().shouldShowToast) {

            Toast toast = Toast.makeText(context,
                    text, Toast.LENGTH_LONG);

            toast.setGravity(Gravity.BOTTOM, 0, 0);


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                TextView v = toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
            }

            toast.show();
        }

    }


    protected   void restartApp(Context context){
        try {
            String packageName = context.getPackageName();
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            String className = launchIntent.getComponent().getClassName();

            Intent mStartActivity = null;

            mStartActivity = new Intent(context, Class.forName(className));

            int mPendingIntentId = 123456;

            PendingIntent mPendingIntent = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

                mPendingIntent = PendingIntent.getActivity( context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_MUTABLE);
            }
            else
            {
                mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId /* Request code */, mStartActivity,
                        PendingIntent.FLAG_ONE_SHOT);


            }

             //mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);



            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 6500, mPendingIntent);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 3500);

        } catch (ClassNotFoundException e) {
            showToastMessage(context,"Your Application's Main Activity is not found");

            e.printStackTrace();
        }
    }

     public void makeStringOnSP(String key, String value){

         sp.putString(key,value).commit();
     }

     public String getStringFromSP(String key, String defaultValue){
         return PreferenceManager.getDefaultSharedPreferences(activity).getString(key,defaultValue);
     }
}
