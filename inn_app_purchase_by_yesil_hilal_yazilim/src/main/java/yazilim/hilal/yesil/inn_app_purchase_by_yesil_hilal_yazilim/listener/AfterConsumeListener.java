package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener;

import com.android.billingclient.api.BillingResult;

public interface AfterConsumeListener {

    public void afterConsume(BillingResult billingResult,String s);
}
