package yazilim.hilal.yesil.inn_app_purchase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yazilim.hilal.yesil.inn_app_purchase.databinding.ActivityMainBinding;
import yazilim.hilal.yesil.inn_app_purchase.databinding.ActivityProBinding;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.InAppPurchaseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.SuccessfullyPurchasedListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.BillingDB;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.ConnectToPlay;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.EntityPurchaseStatus;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo.PurchaseStatus;

public class ProFragment extends AppCompatActivity {


    public ActivityProBinding binding;
    //private HashMap<String,SkuDetails> hashMapSkuDetails = new HashMap<>();
    private HashMap<String, ProductDetails> hashMapSkuDetails = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding =  DataBindingUtil.setContentView(this, R.layout.activity_pro);

        ConnectToPlay.getInstance().initForActivity(this)./*showHud("Loading").*/startToWork(ConnectToPlay.CallType.GetPriceProducts)
                .setInAppPurchaseListener(new InAppPurchaseListener() {
                    @Override
                    public void returnAllProductsDetailsFromPlayStore(HashMap<String,ProductDetails> hashMapSkuDetails) {
                        ProFragment.this.hashMapSkuDetails = hashMapSkuDetails;
                        setPrice();


                        //ConnectToPlay.getInstance().hideHud();
                    }

                }).setSuccessfullyPurchasedListener(new SuccessfullyPurchasedListener() {
            @Override
            public void successfullyPurchased(String sku) {


            }
        });

        binding.btnOfBor.setOnClickListener(v->{

            ConnectToPlay.getInstance().startBuyOut(this,hashMapSkuDetails.get("bor"));

        });

        binding.btnOfGas.setOnClickListener(v->{
            ConnectToPlay.getInstance().startBuyOut(this,hashMapSkuDetails.get("gas"));
        });

        binding.btnOfNoads.setOnClickListener(v->{
            ConnectToPlay.getInstance().startBuyOut(this,hashMapSkuDetails.get("noads"));
        });

        binding.btnOfPro.setOnClickListener(v->{
            ConnectToPlay.getInstance().startBuyOut(this,hashMapSkuDetails.get("pro"));
        });


        binding.btnOfSun.setOnClickListener(v->{
            ConnectToPlay.getInstance().startBuyOut(this, hashMapSkuDetails.get("sun"));
        });



        binding.btnViewLibrary.setOnClickListener(v->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Ucdemir/Ucdemir-Inn_App_Purchase_v2"));
            startActivity(browserIntent);
        });


    }


    @Override
    protected void onStart() {
        super.onStart();




    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    private void setPrice(){


        for (Map.Entry<String, ProductDetails> e  : hashMapSkuDetails.entrySet()){

            Log.d("YHY", "price : " + e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice());

            switch (e.getKey()){

                case "bor":

                    //binding.btnOfBor.setText(binding.btnOfBor.getText()+" ("+ e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice()+")");

                    //String aa = "ajaja" +  e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice();

                    binding.btnOfBor.setText( e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice());
                    break;
                case "gas":

                    binding.btnOfGas.setText( e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice());

                    //binding.btnOfGas.setText(binding.btnOfGas.getText()+" ("+  e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice()+")");

                    break;

                case "noads":
                    binding.btnOfNoads.setText( e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice());

                    //binding.btnOfNoads.setText(binding.btnOfNoads.getText()+" ("+  e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice()+")");
                    break;

                case "pro":
                    binding.btnOfPro.setText( e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice());

                    //binding.btnOfPro.setText(binding.btnOfPro.getText()+" ("+  e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice()+")");
                    break;

                case "sun":
                    binding.btnOfSun.setText( e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice());

                    //binding.btnOfSun.setText(binding.btnOfSun.getText()+" ("+  e.getValue().getOneTimePurchaseOfferDetails().getFormattedPrice()+")");
                    break;

            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectToPlay.getInstance().endConnection();
    }
}
