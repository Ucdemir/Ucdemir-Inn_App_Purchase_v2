package yazilim.hilal.yesil.inn_app_purchase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.android.billingclient.api.SkuDetails;

import java.util.List;

import yazilim.hilal.yesil.inn_app_purchase.databinding.ActivityMainBinding;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.InAppPurchaseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.BillingDB;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.ConnectToPlay;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main.EntityPurchaseStatus;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo.PurchaseStatus;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    private List<SkuDetails> listOfAllAppSKU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


         binding =  DataBindingUtil.setContentView(this, R.layout.activity_main);

        ConnectToPlay.startToWork(ConnectToPlay.CallType.GetPriceProducts)
                .setmInAppPurchaseListener(new InAppPurchaseListener() {
                    @Override
                    public void returnAllProductsDetailsFromPlayStore(List<SkuDetails> listUserBoughtSku) {
                        listOfAllAppSKU = listUserBoughtSku;
                        setPrice();
                    }

                    @Override
                    public void isPruductBought(PurchaseStatus statusOfPurchase) {

                        for (String sku : App.listOfApplicationSKU) {

                            if(sku.equals(statusOfPurchase.getSkuName())){

                                EntityPurchaseStatus status = new EntityPurchaseStatus();
                                status.setProductName(sku);

                                if (statusOfPurchase.isBought()) {

                                    status.setBought(true);
                                    BillingDB.getDatabase(MainActivity.this).purchaseStatusDAO().updatePurchaseStatus(status);

                                } else {
                                    BillingDB.getDatabase(MainActivity.this).purchaseStatusDAO().updatePurchaseStatus(status);
                                    status.setBought(false);

                                }

                            }

                        }

                    }

                    @Override
                    public void listOfStatusProducts(List<PurchaseStatus> listOfBoughtProducts) {

                    }
                });


        binding.btnOfBor.setOnClickListener(v->{



        });

        binding.btnOfGas.setOnClickListener(v->{

        });

        binding.btnOfNoads.setOnClickListener(v->{

        });

        binding.btnOfPro.setOnClickListener(v->{

        });


        binding.btnOfSun.setOnClickListener(v->{

        });
    }


    private void setPrice(){


        for (SkuDetails sku : listOfAllAppSKU){

            switch (sku.getSku()){

                case "bor":

                    binding.btnOfBor.setText(binding.btnOfBor.getText()+" ("+ sku.getPrice()+")");
                    break;
                case "gas":

                    binding.btnOfGas.setText(binding.btnOfGas.getText()+" ("+ sku.getPrice()+")");
                    break;

                case "noads":

                    binding.btnOfNoads.setText(binding.btnOfNoads.getText()+" ("+ sku.getPrice()+")");
                    break;

                case "pro":

                    binding.btnOfPro.setText(binding.btnOfPro.getText()+" ("+ sku.getPrice()+")");
                    break;

                case "sun":

                    binding.btnOfSun.setText(binding.btnOfSun.getText()+" ("+ sku.getPrice()+")");
                    break;

            }

        }

    }
}
