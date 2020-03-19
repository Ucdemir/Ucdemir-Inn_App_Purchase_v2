package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo;

import com.android.billingclient.api.Purchase;

public class PurchaseStatus {

    private String skuName;
    private boolean isBought;

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }
}
