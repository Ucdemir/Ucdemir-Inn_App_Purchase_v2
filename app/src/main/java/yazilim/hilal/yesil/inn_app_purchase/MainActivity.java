package yazilim.hilal.yesil.inn_app_purchase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yazilim.hilal.yesil.inn_app_purchase.databinding.ActivityMainBinding;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterAcknowledgePurchaseResponseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterConsumeListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.ProductStatusGotListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.ConnectToPlay;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo.PurchaseStatus;

public class MainActivity extends AppCompatActivity {

    public static List<String> listOfApplicationSKU;

    private ActivityMainBinding binding;

    private HashMap<String, Purchase> hashMapPurchaseDetails = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        listOfApplicationSKU = new ArrayList<>();
        listOfApplicationSKU.add("bor");
        listOfApplicationSKU.add("gas");
        listOfApplicationSKU.add("noads");
        listOfApplicationSKU.add("pro");
        listOfApplicationSKU.add("sun");



        binding =  DataBindingUtil.setContentView(this, R.layout.activity_main);


        /*ConnectToPlay.getInstance().initForActivity(this).billingSKUS(listOfApplicationSKU).startToWork(ConnectToPlay.CallType.CheckProductStatus).
                setProductStatusGotListener(new ProductStatusGotListener() {
                    @Override
                    public void onProductStatusGot(HashMap<String, Purchase> hashMapPurchaseDetails) {
                    MainActivity.this.hashMapPurchaseDetails = hashMapPurchaseDetails;


                    ConnectToPlay.printProductStatus("onListener");


                      Purchase p = hashMapPurchaseDetails.get("bor");
                        ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());

                         p = hashMapPurchaseDetails.get("gas");
                        ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());


                         p = hashMapPurchaseDetails.get("noads");
                        ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());

                        p = hashMapPurchaseDetails.get("pro");
                        ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());

                        p = hashMapPurchaseDetails.get("sun");
                        ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());


                        for (String sku : listOfApplicationSKU){



                            Purchase p = hashMapPurchaseDetails.get(sku);
                            ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());


                            //String status  = ConnectToPlay.getInstance().statusOfProduct(sku).toString();
                            switch (sku){



                                case  "bor":
                                    binding.statusOfBor.setText(ConnectToPlay.getInstance().statusOfProduct(sku));
                                    break;
                                case  "gas":
                                    binding.statusOfGas.setText(ConnectToPlay.getInstance().statusOfProduct(sku));
                                    break;
                                case  "noads":
                                    binding.statusOfNoads.setText(ConnectToPlay.getInstance().statusOfProduct(sku));
                                    break;
                                case  "pro":
                                    binding.statusOfPro.setText(ConnectToPlay.getInstance().statusOfProduct(sku));
                                    break;
                                case  "sun":
                                    binding.statusOfSun.setText(ConnectToPlay.getInstance().statusOfProduct(sku));
                                    break;
                            }
                        }




                    }
                }).setAfterConsumeListener(new AfterConsumeListener() {
            @Override
            public void afterConsume(BillingResult billingResult, String s) {



            }
        }).setAfterAcknowledgePurchaseResponseListener(new AfterAcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

            }
        });//.shouldFirstProductsReturnTrue(true);*/




        checkIsThereUpdate();




        binding.btnPro.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this,ProFragment.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectToPlay.getInstance().endConnection();
    }

    private void checkIsThereUpdate(){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {

            Toast.makeText(MainActivity.this,"AAA",Toast.LENGTH_LONG).show();
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                /*&& appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)*/) {
                // Request the update.

                Toast.makeText(MainActivity.this,"BBB",Toast.LENGTH_LONG).show();

                String k = "";
            }
        });
    }
}
