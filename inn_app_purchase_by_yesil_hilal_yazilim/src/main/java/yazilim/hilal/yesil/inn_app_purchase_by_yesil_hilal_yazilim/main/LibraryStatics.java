package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class LibraryStatics {


    public static void showToastMessage(Context context, String text){

        Toast toast = Toast.makeText(context,
                text, Toast.LENGTH_LONG);

        toast.setGravity(Gravity.BOTTOM, 0, 0);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);

        toast.show();

    }


    public static void restartApp(Context context){
        try {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();

        Intent mStartActivity = null;

            mStartActivity = new Intent(context, Class.forName(packageName+"."+className));

        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3500, mPendingIntent);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 2000);

        } catch (ClassNotFoundException e) {
            showToastMessage(context,"Your Application's Main Activity is not found");

            e.printStackTrace();
        }
    }
}
