package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.R;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.InAppPurchaseListener;

//a

public class ConnectToPlay {


    private  static BillingClient mBillingClient;
    private static SkuDetails skuDetails;
    private static  ConnectToPlay instance;
    private Context context;
    private InAppPurchaseListener mInAppPurchaseListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private String productSKU = "pro";



    public enum CallType{
        GetPriceProducts,
        CheckProductStatus,

    }
    private ConnectToPlay(){

    }
    public static ConnectToPlay getInstance(){
        if (instance == null){
            instance = new ConnectToPlay();
        }

        return instance;
    }

    public  void initBilling(Application app){
        this.context = app;
    }


    public static ConnectToPlay startToWork(final CallType type){



        if(mBillingClient == null) {

            mBillingClient = BillingClient.newBuilder(getInstance().context).
                    enablePendingPurchases().
                    setListener(new PurchasesUpdatedListener() {
                                    @Override
                                    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {

                                       getInstance().purchaseStatus( billingResult,  list);
                                    }
                                }

                    ).build();
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {



                    switch (type){
                        case GetPriceProducts:
                            getInstance().getPriceOfProduct();
                            break;

                        case CheckProductStatus:
                            getInstance().getCachedQueryList();
                            break;
                    }

                }

                @Override
                public void onBillingServiceDisconnected() {
                    if(mBillingClient != null) {
                        mBillingClient.startConnection(this);

                    }else{
                        startToWork(type);
                    }
                }
            });

             getInstance().acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                @Override
                public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                }
            };

        }else{
            startToWork(type);
        }

        return getInstance();
    }




    //If client buy any item or have bought before or canceled during payout this method listener start
    private void purchaseStatus(BillingResult billingResult, List<Purchase> purchases){
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {

                handlePurchase(purchase,billingResult);


            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {



        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

            LibraryStatics.showToastMessage(context, context.getString(R.string.pro_succesfully_bought));


            if(mInAppPurchaseListener != null) {
                mInAppPurchaseListener.isPruductOwned(true);
            }

            LibraryStatics.restartApp(context);

        }

        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR) {

            LibraryStatics.showToastMessage(context, context.getString(R.string.error_occurred));


        }

        else {

        }
    }


    public static void  startBuyOut(Context context){

        if(mBillingClient != null) {
            BillingResult responseCode = mBillingClient.isFeatureSupported(BillingClient.FeatureType.IN_APP_ITEMS_ON_VR);
            if (responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                mBillingClient.launchBillingFlow((Activity) context, flowParams).getResponseCode();
            } else {
                LibraryStatics.showToastMessage(context, context.getString(R.string.in_app_purchase_is_not_suppored));

            }

        }
    }

    private void getPriceOfProduct(){
        List<String> skuList = new ArrayList<>();
        skuList.add(productSKU);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                            for (SkuDetails skuProduct : skuDetailsList) {

                                //product detail
                                skuDetails = skuProduct;

                                String sku = skuProduct.getSku();
                                String price = skuProduct.getPrice();


                                if (productSKU.equals(sku)) {



                                    if(mInAppPurchaseListener != null) {
                                        mInAppPurchaseListener.returnPrice(price);
                                    }


                                }


                            }
                        }

                    }
                });
    }


    public ConnectToPlay setmInAppPurchaseListener(InAppPurchaseListener listener){

         mInAppPurchaseListener = listener;

         return this;
    }


    //Making Acknowledged
    private void checkIsAcknowledged(Purchase purchase){
        if (!purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .setDeveloperPayload(purchase.getDeveloperPayload())
                            .build();
            if(mBillingClient != null) {

                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    public void handlePurchase(Purchase purchase, BillingResult  resultCode) {

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            if (purchase.getSku().equals(productSKU)) {
                if(mInAppPurchaseListener != null) {
                    mInAppPurchaseListener.isPruductOwned(true);
                }
                LibraryStatics.showToastMessage(context, context.getString(R.string.succuss_buy));
                LibraryStatics.restartApp(context);

                if (!purchase.isAcknowledged()) {
                    //If its not Acknowledged() do it
                   checkIsAcknowledged(purchase);

                }

            }

        }

        else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {

        }

    }


    //It return all non-consumed items
    //This method used in cached, for real time use queryPurchaseHistoryAsync()
    private void  getCachedQueryList(){

        if(mBillingClient != null) {



            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
            List<Purchase> result = purchasesResult.getPurchasesList();

            for (Purchase row : result) {

                //commentle
                //consumeProductForTestPurpose(row.getPurchaseToken(),row.getDeveloperPayload());
                if (row.getSku().equals(productSKU)) {

                    LibraryStatics.showToastMessage(context, context.getString(R.string.pro_succesfully_bought));

                    if(mInAppPurchaseListener!= null) {
                        mInAppPurchaseListener.isPruductOwned(true);
                    }
                    LibraryStatics.restartApp(context);

                }
            }

            if (result.size() == 0) {
                if(mInAppPurchaseListener!= null) {
                    mInAppPurchaseListener.isPruductOwned(false);
                }
                LibraryStatics.showToastMessage(context, context.getString(R.string.no_purchase));

            }
        }
    }

    private void consumeProductForTestPurpose(String purchaseToken, String payload){
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .setDeveloperPayload(payload)
                        .build();

        if (mBillingClient != null) {

            mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String s) {


                }
            });
        }
    }



    //It return all bought items consumed and nonconsumed not best practice
    /*private void getLatestPurchaseList(){

        if (billingClient != null){

            billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
                @Override
                public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> purchasesList) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                        for (PurchaseHistoryRecord purchase : purchasesList) {

                            //commentden cikarilcak
                            if(purchase.getSku().equals(skuPro)){


                                DataManager.showToastMessage(getMainActivity(), getString(R.string.pro_succesfully_bought));
                                dbSqlite.updateStatus();
                                getMainActivity().restartApp();
                            }


                        }

                        if(purchasesList.size() == 0){
                            DataManager.showToastMessage(getMainActivity(), getString(R.string.no_purchase));

                        }
                    }
                }
            });


        }
    }*/
}
