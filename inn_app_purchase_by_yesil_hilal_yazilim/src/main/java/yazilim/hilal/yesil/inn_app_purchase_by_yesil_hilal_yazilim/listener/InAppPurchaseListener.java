package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.List;

import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo.PurchaseStatus;

public interface InAppPurchaseListener {


    public  void returnAllProductsDetailsFromPlayStore(List<SkuDetails> listUserBoughtSku);

    public void isPruductBought(PurchaseStatus statusOfPurchase);


    public void listOfStatusProducts(List<PurchaseStatus> listOfBoughtProducts);
}
