package yazilim.hilal.yesil.inn_app_purchase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;

import java.util.HashMap;
import java.util.List;

import yazilim.hilal.yesil.inn_app_purchase.databinding.ActivityMainBinding;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterAcknowledgePurchaseResponseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterConsumeListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.ProductStatusGotListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.ConnectToPlay;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo.PurchaseStatus;

public class MainActivity extends AppCompatActivity {



    private ActivityMainBinding binding;

    private HashMap<String, Purchase> hashMapPurchaseDetails = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding =  DataBindingUtil.setContentView(this, R.layout.activity_main);




        ConnectToPlay.getInstance().initForActivity(this).startToWork(ConnectToPlay.CallType.CheckProductStatus).
                setProductStatusGotListener(new ProductStatusGotListener() {
                    @Override
                    public void onProductStatusGot(HashMap<String, Purchase> hashMapPurchaseDetails) {
                    MainActivity.this.hashMapPurchaseDetails = hashMapPurchaseDetails;


                     /*Purchase p = hashMapPurchaseDetails.get("bor");
                        ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());

                       /*  p = hashMapPurchaseDetails.get("gas");
                        ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());*/


                        for (String sku : App.listOfApplicationSKU){


                           boolean status = ConnectToPlay.getInstance().whatIsProductStatus(sku);
                            switch (sku){

                                case  "bor":
                                    binding.statusOfBor.setText(statusOfProduct(status));
                                    break;
                                case  "gas":
                                    binding.statusOfGas.setText(statusOfProduct(status));
                                    break;
                                case  "noads":
                                    binding.statusOfNoads.setText(statusOfProduct(status));
                                    break;
                                case  "pro":
                                    binding.statusOfPro.setText(statusOfProduct(status));
                                    break;
                                case  "sun":
                                    binding.statusOfSun.setText(statusOfProduct(status));
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
        });






        binding.btnPro.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this,ProFragment.class);
            startActivity(intent);
        });
    }




    private String statusOfProduct(boolean status){

        if(status){
            return "Product Bought";
        }else{
            return "Is not bought";
        }
    }

}
