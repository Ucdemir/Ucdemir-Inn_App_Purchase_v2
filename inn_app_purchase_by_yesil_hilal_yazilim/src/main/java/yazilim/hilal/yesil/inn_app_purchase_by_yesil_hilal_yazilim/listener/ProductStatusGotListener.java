package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener;

import com.android.billingclient.api.Purchase;

import java.util.HashMap;

public interface ProductStatusGotListener {

    void  onProductStatusGot(HashMap<String, Purchase> hashMapPurchaseDetails);
}
