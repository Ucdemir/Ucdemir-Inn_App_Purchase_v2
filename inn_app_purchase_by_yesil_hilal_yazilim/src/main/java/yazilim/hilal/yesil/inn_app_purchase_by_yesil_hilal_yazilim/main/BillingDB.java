package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;

import android.content.Context;
import android.database.Cursor;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;




@Database(entities = {EntityPurchaseStatus.class},version = 1, exportSchema = false)
public abstract class BillingDB extends RoomDatabase {

    private static Context ctx;

    public abstract DaoPurchaseStatus purchaseStatusDAO();

    private static volatile BillingDB INSTANCE;

    public static BillingDB getDatabase(final Context context) {
        ctx = context;
        if (INSTANCE == null) {
            synchronized (BillingDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BillingDB.class, YHYManager.DATABASE_NAME)/*addCallback(dbCallback)*/
                            .allowMainThreadQueries().build();



                }
            }
        }
        return INSTANCE;
    }


    //This Method In App first Time start init db
   /* private static RoomDatabase.Callback dbCallback = new RoomDatabase.Callback() {
        public void onCreate(SupportSQLiteDatabase db) {

            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                @Override
                public void run() {


                    for(String skuName : ConnectToPlay.listApplicationSKU) {
                        
                        Cursor cursor = BillingDB.getDatabase(ctx).purchaseStatusDAO().isProductInsertedToDB(skuName);
                        int k = cursor.getCount();

                        if (cursor !=null && k< 1) {
                            EntityPurchaseStatus item = new EntityPurchaseStatus();
                            item.setProductName(skuName);
                            item.setBought(false);

                            BillingDB.getDatabase(ctx).purchaseStatusDAO().insert(item);

                        }
                    }

                }
            });
        }
    };*/
}
