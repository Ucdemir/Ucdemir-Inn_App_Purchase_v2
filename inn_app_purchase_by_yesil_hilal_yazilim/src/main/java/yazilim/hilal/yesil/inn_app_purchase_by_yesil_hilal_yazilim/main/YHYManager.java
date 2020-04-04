package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

 class YHYManager {

     protected static final String DATABASE_NAME = "BillingDB";
     protected static final String TABLE_PURCHASE_STATUS = "purchase_status";
     protected static List<String> listApplicationSKU  = new ArrayList<>();


    protected   void showToastMessage(Context context, String text){

        Toast toast = Toast.makeText(context,
                text, Toast.LENGTH_LONG);

        toast.setGravity(Gravity.BOTTOM, 0, 0);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);

        toast.show();

    }


    protected   void restartApp(Context context){
        try {
            String packageName = context.getPackageName();
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            String className = launchIntent.getComponent().getClassName();

            Intent mStartActivity = null;

            mStartActivity = new Intent(context, Class.forName(className));

            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
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
}
