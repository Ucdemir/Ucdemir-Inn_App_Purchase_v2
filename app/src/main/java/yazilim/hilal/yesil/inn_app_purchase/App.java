package yazilim.hilal.yesil.inn_app_purchase;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;


import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.*;
public class App  extends Application {

    public static List<String> listOfApplicationSKU;
    @Override
    public void onCreate() {
        super.onCreate();



        listOfApplicationSKU = new ArrayList<>();
        listOfApplicationSKU.add("bor");
        listOfApplicationSKU.add("gas");
        listOfApplicationSKU.add("noads");
        listOfApplicationSKU.add("pro");
        listOfApplicationSKU.add("sun");


        ConnectToPlay.initBillingForApp(this,listOfApplicationSKU);

    }
}
