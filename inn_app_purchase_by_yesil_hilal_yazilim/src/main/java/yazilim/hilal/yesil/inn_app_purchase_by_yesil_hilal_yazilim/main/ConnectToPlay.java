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
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.R;

import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterAcknowledgePurchaseResponseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterConsumeListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.InAppPurchaseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.ProductStatusGotListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo.PurchaseStatus;



public class ConnectToPlay  extends YHYManager{


    private   BillingClient mBillingClient;
    private static  ConnectToPlay instance;


    private boolean isProductStatusGot = false;
    private  List<Purchase> listUserBoughtPurchase = new ArrayList<>();

    private HashMap<String,SkuDetails> hashMapSkuDetails = new HashMap<>();
    private HashMap<String,Purchase> hashMapPurchaseDetails = new HashMap<>();

    private KProgressHUD hud;

    private Activity activity;

    private InAppPurchaseListener mInAppPurchaseListener;
    private ProductStatusGotListener mProductStatusGotListener;
    private AfterConsumeListener mAfterConsumeListener;
    private AfterAcknowledgePurchaseResponseListener mAfterAcknowledgePurchaseResponseListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;



    public enum CallType{
        GetPriceProducts,
        CheckProductStatus,

    }
    private ConnectToPlay(){

    }

    public  ConnectToPlay showHud(String msg){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f)
                .setAnimationSpeed(2);
         if(msg!= null) {
             hud.setLabel("");
         }
         hud.show();

        return instance;
    }

    public void hideHud(){
        if(hud != null){
            hud.dismiss();
        }

    }

    public static ConnectToPlay getInstance(){
        if (instance == null){
            instance = new ConnectToPlay();

        }

        return instance;
    }

    public  ConnectToPlay initForActivity(Activity a){
        this.activity = a;

        return instance;
    }

    public  ConnectToPlay billingSKUS(List<String> listApplicationSKU){
        YHYManager.listApplicationSKU = listApplicationSKU;
        DaoPurchaseStatus dao = BillingDB.getDatabase(activity).purchaseStatusDAO();

        for(String sku : listApplicationSKU){
           int count = dao.getCountOfSKU(sku);
           if(count == 0){
               EntityPurchaseStatus entity = new EntityPurchaseStatus();
               entity.setProductName(sku);
               entity.setBought(false);
               dao.insert(entity);
           }
        }


        return instance;

    }


    public  ConnectToPlay startToWork(final CallType type){



            mBillingClient = BillingClient.newBuilder(activity).
                    enablePendingPurchases().
                    setListener(new PurchasesUpdatedListener() {
                                    @Override
                                    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {

                                       instance.purchaseStatus( billingResult,  list);
                                    }
                                }

                    ).build();

            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {



                    switch (type){
                        case GetPriceProducts:
                            getPriceOfAllProduct();
                            break;

                        case CheckProductStatus:
                            getCachedQueryList();
                            break;
                    }

                }

                @Override
                public void onBillingServiceDisconnected() {
                        startToWork(type);

                }
            });

             acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                @Override
                public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                    ConnectToPlay.super.showToastMessage(activity,activity.getString(R.string.purchase_acknowledged));
                    if(mAfterAcknowledgePurchaseResponseListener != null){
                        mAfterAcknowledgePurchaseResponseListener.onAcknowledgePurchaseResponse(billingResult);
                    }


                    ConnectToPlay.super.restartApp(activity);
                }
            };


        return  instance;
    }




    //If client buy any item or have bought before or canceled during payout this method listener start
    private void purchaseStatus(BillingResult billingResult, List<Purchase> purchases){
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {

                handlePurchase(purchase,billingResult);


            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

            super.showToastMessage(activity, activity.getString(R.string.user_canceled_purchase));


        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

            super.showToastMessage(activity, activity.getString(R.string.already_purchased));


        }

        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR) {

            super.showToastMessage(activity, activity.getString(R.string.error_occurred));


        }

        else {

        }
    }


    public  void  startBuyOut(Activity activity,SkuDetails skuDetails){

        if(mBillingClient != null) {
            BillingResult responseCode = mBillingClient.isFeatureSupported(BillingClient.FeatureType.IN_APP_ITEMS_ON_VR);
            if (responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                mBillingClient.launchBillingFlow(activity, flowParams).getResponseCode();
            } else {
                super.showToastMessage(activity, activity.getString(R.string.in_app_purchase_is_not_suppored));

            }

        }else{
            notConnectedToGooglePlay();
        }
    }

    private void getPriceOfAllProduct(){



        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(listApplicationSKU).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> listOfSkuProductDetails) {

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && listOfSkuProductDetails != null) {


                            mInAppPurchaseListener.
                                    returnAllProductsDetailsFromPlayStore(initHashMapSkuDetails(listOfSkuProductDetails));
                        }

                    }
                });
    }







    //Making Acknowledged
    private void checkIsAcknowledged(Purchase purchase){

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .setDeveloperPayload(purchase.getDeveloperPayload())
                                .build();
                if (mBillingClient != null) {

                    mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                } else {
                    notConnectedToGooglePlay();
                }
            }

        }else if(purchase.getPurchaseState() == Purchase.PurchaseState.PENDING){
            int k = 1;
        }else{

            int k = 1;
        }
    }

    public void handlePurchase(Purchase boughtPurchase, BillingResult  resultCode) {


        if(boughtPurchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

             a: for(String  appSkuName : listApplicationSKU ) {

                if (boughtPurchase.getSku().equals(appSkuName)) {



                    BillingDB.getDatabase(activity).purchaseStatusDAO().updatePurchaseStatus(true
                            ,boughtPurchase.getSku());

                    super.showToastMessage(activity, activity.getString(R.string.succuss_buy));


                    if (!boughtPurchase.isAcknowledged()) {
                        checkIsAcknowledged(boughtPurchase);
                    }
                    break a;
                }

            }

        }

        else if (boughtPurchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {


            super.showToastMessage(activity,activity.getString(R.string.purchase_is_on_pending));
        }


    }


    //It return all non-consumed  tried to buy items which in PurchaseState.PURCHASED and PurchaseState.Pending
    //This method used in cached, for real time use queryPurchaseHistoryAsync()
    //As Cached function, some items that rejected by bank it take time to update purchasesResult.getPurchasesList();
    //After bank reject purchase, Item is not got by purchasesResult.getPurchasesList(); if you want immediate update,
    //Delete Play Store data from device settings
    private void  getCachedQueryList(){

        isProductStatusGot = false;
        List<PurchaseStatus> listOfBoughtProducts = new ArrayList<>();

        if(mBillingClient != null) {

            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
            List<Purchase> listOfAllProducts = purchasesResult.getPurchasesList();

            if(listOfAllProducts != null) {

                a:for (Purchase item : listOfAllProducts) {
                    for (String appSku : listApplicationSKU) {

                        if (item.getSku().equals(appSku)) {

                            if (item.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                BillingDB.getDatabase(activity).purchaseStatusDAO().updatePurchaseStatus(true, item.getSku());
                                checkIsAcknowledged(item);
                            }
                        }
                    }
                }

                //Below code just convert List<Purchase>  to HashMap<String,Purchase>
                HashMap<String, Purchase> list = initHashMapPurchaseDetails(listOfAllProducts);
                updateOwnedProductsOnDB(list);

                isProductStatusGot = true;
                if (mProductStatusGotListener != null) {
                    mProductStatusGotListener.onProductStatusGot(list);
                }
            }
        }else{
            notConnectedToGooglePlay();
        }
    }

    public void consumeProduct(String purchaseToken, String payload){
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .setDeveloperPayload(payload)
                        .build();

        if (mBillingClient != null) {

            mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String s) {


                    if(mAfterConsumeListener != null){
                        mAfterConsumeListener.afterConsume(billingResult,s);
                    }
                }
            });
        }else{
            notConnectedToGooglePlay();
        }
    }

    private void notConnectedToGooglePlay(){
        super.showToastMessage(activity,activity.getString(R.string.not_connected_to_google));

    }

    private void updateOwnedProductsOnDB(HashMap<String,Purchase>  boughtLlist ){

        for(String appSku : super.listApplicationSKU){
            Purchase item = boughtLlist.get(appSku);

            if(item == null){
                BillingDB.getDatabase(activity).purchaseStatusDAO().updatePurchaseStatus(false,appSku);
            }

        }




    }


    private HashMap<String,Purchase> initHashMapPurchaseDetails(List<Purchase> listOfSkuProductDetails){
        hashMapPurchaseDetails = new HashMap<>();
        for(Purchase purchase : listOfSkuProductDetails){
            for(String productName : listApplicationSKU){
                if(purchase.getSku().equals(productName)){
                    hashMapPurchaseDetails.put(productName,purchase);
                }
            }
        }

        return hashMapPurchaseDetails;
    }

    private HashMap<String,SkuDetails> initHashMapSkuDetails(List<SkuDetails> listOfSkuProductDetails){
        hashMapSkuDetails = new HashMap<>();


        for(SkuDetails skuDetails : listOfSkuProductDetails){

            for(String productName : listApplicationSKU){
                if(skuDetails.getSku().equals(productName)){
                    hashMapSkuDetails.put(productName,skuDetails);
                }

            }

        }

        return hashMapSkuDetails;
    }


    public ConnectToPlay setInAppPurchaseListener(InAppPurchaseListener listener){

        mInAppPurchaseListener = listener;

        return this;
    }

    public ConnectToPlay setProductStatusGotListener(ProductStatusGotListener listener){

        mProductStatusGotListener = listener;

        return this;
    }

    public ConnectToPlay setAfterConsumeListener(AfterConsumeListener listener){

        mAfterConsumeListener = listener;
        return this;
    }

    public ConnectToPlay setAfterAcknowledgePurchaseResponseListener(AfterAcknowledgePurchaseResponseListener listener){

        this.mAfterAcknowledgePurchaseResponseListener = listener;

        return this;
    }

    public boolean whatIsProductStatus(String skuName){


        if(isProductStatusGot) {
            return BillingDB.getDatabase(activity).purchaseStatusDAO().isProductBought(skuName);
        }else{

            super.showToastMessage(activity,"You called this function before initialization Of GooglePlay, Use" +
                    "setProductStatusGotListener method. If you dont, this method always return false");

            return false;

        }
    }



}
