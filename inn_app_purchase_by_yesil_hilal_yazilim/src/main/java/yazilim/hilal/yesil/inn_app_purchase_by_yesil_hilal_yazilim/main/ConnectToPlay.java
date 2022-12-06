package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;


import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.common.collect.ImmutableList;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.R;

import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterAcknowledgePurchaseResponseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.AfterConsumeListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.InAppPurchaseListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.ProductStatusGotListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.listener.SuccessfullyPurchasedListener;
import yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.pojo.PurchaseStatus;



public class ConnectToPlay  extends YHYManager{


    private   BillingClient mBillingClient;
    private static  ConnectToPlay instance;

    private boolean isFirstOpen = false;

    //Below one old one
    //private HashMap<String,SkuDetails> hashMapSkuDetails = new HashMap<>();
    private HashMap<String,ProductDetails> hashMapProductDetails = new HashMap<>();
    private List<ProductDetails> productDetailsList = new ArrayList<>();


    private HashMap<String,Purchase> hashMapPurchaseDetails = new HashMap<>();


    private KProgressHUD hud;



    private InAppPurchaseListener mInAppPurchaseListener;//it get price
    private ProductStatusGotListener mProductStatusGotListener;// it get product status
    private SuccessfullyPurchasedListener mSuccessfullyPurchasedListener;

    private AfterConsumeListener mAfterConsumeListener;
    private AfterAcknowledgePurchaseResponseListener mAfterAcknowledgePurchaseResponseListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    private boolean shouldRestartApp = true;
    protected boolean shouldShowToast = true;
    private boolean shouldFirstProductsReturnTrue = false;

    private String purchaseToken = "";

    public ConnectToPlay() {
        super();
    }

    public ConnectToPlay(Activity activity) {
        super(activity);
    }



    public enum CallType{
        GetPriceProducts,
        CheckProductStatus,

    }

    public  ConnectToPlay showHud(String msg){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f)
                .setAnimationSpeed(2);
         if(msg== null) {
             hud.setLabel("");
         }else{
             hud.setLabel(msg);
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
        sp = PreferenceManager.getDefaultSharedPreferences(activity).edit();

        return instance;
    }

    public  ConnectToPlay billingSKUS(List<String> listApplicationSKU){
        YHYManager.listApplicationSKU = listApplicationSKU;
        DaoPurchaseStatus dao = BillingDB.getDatabase(activity).purchaseStatusDAO();

        isFirstOpen = false;

        String isFreshStart = getStringFromSP(IS_FRESH_START,null);


        for(String sku : listApplicationSKU){
           //int count = dao.getCountOfSKU(sku);
           //if(count == 0){

            if(isFreshStart == null){
                makeStringOnSP(IS_FRESH_START,"NO");
               isFirstOpen = true;
               EntityPurchaseStatus entity = new EntityPurchaseStatus();
               entity.setProductName(sku);
               entity.setBought(true);
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



                                        if(purchaseToken.equals("")){

                                            if(list != null) {
                                                Purchase p = list.get(0);
                                                instance.purchaseStatus(billingResult, list);
                                                purchaseToken = p.getPurchaseToken();
                                            }
                                        }else{
                                            purchaseToken = "";
                                        }




                                        //mBillingClient.endConnection();
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
                            getStatusOfProducts();
                            break;
                    }

                }

                @Override
                public void onBillingServiceDisconnected() {



                    //This area has been commented for
                    //illegal state exception to see it casued by this
                    /*try {

                        startToWork(type);
                    }catch (IllegalStateException e){


                    }*/


                }
            });

             acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                @Override
                public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                    ConnectToPlay.super.showToastMessage(activity,activity.getString(R.string.purchase_acknowledged));


                    if(mAfterAcknowledgePurchaseResponseListener != null){
                        mAfterAcknowledgePurchaseResponseListener.onAcknowledgePurchaseResponse(billingResult);
                    }


                    /*if(shouldRestartApp) {
                        ConnectToPlay.super.restartApp(activity);
                    }*/
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


    public  void  startBuyOut(Activity activity,ProductDetails productDetails){



        //below old code for buyout
        /*if(mBillingClient != null) {
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
        }*/
        //above old code for buyout

        if(mBillingClient != null) {

            ImmutableList productDetailsParamsList =
                    ImmutableList.of(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                    .setProductDetails(productDetails)
                                    // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                    // for a list of offers that are available to the user
                                    //.setOfferToken(selectedOfferToken)
                                    .build()
                    );

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();


            BillingResult billingResult = mBillingClient.launchBillingFlow(activity, billingFlowParams);


        }

    }

    private void getPriceOfAllProduct(){


        //Below old code that get price
        /*SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
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
                });*/
        //Above old code that get price


        if(mBillingClient != null){

            QueryProductDetailsParams queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                            .setProductList(
                                    setProductListForGetDetails())
                            .build();

            mBillingClient.queryProductDetailsAsync(
                    queryProductDetailsParams,
                    new ProductDetailsResponseListener() {
                        public void onProductDetailsResponse(BillingResult billingResult,
                                                             List<ProductDetails> productDetailsList) {


                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {

                                ConnectToPlay.this.productDetailsList = productDetailsList;


                                mInAppPurchaseListener.
                                        returnAllProductsDetailsFromPlayStore(initHashMapProductDetails(productDetailsList));

                                /*for(ProductDetails productDetails : productDetailsList){
                                    productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();


                                }*/


                            }




                        }
                    }
            );

        }

    }







    //Making Acknowledged
    private void checkIsAcknowledged(Purchase purchase){

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                //.setDeveloperPayload(purchase.getDeveloperPayload())
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

            String sku = boughtPurchase.getProducts().get(0);

                if (sku.equals(appSkuName)) {



                    BillingDB.getDatabase(activity).purchaseStatusDAO().updatePurchaseStatus(true
                            ,sku);



                    super.showToastMessage(activity, activity.getString(R.string.succuss_buy));



                    if(shouldRestartApp) {
                        ConnectToPlay.super.restartApp(activity);
                    }


                    if (!boughtPurchase.isAcknowledged()) {
                        checkIsAcknowledged(boughtPurchase);
                    }
                    if(mSuccessfullyPurchasedListener != null){
                        mSuccessfullyPurchasedListener.successfullyPurchased(sku);
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
    private void  getStatusOfProducts(){

        //Below old codes
        /* List<PurchaseStatus> listOfBoughtProducts = new ArrayList<>();
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

                //Below isFirstOpen requred for fresh start..
                //it false so, product will get correct ones...
                isFirstOpen = false;



                if (mProductStatusGotListener != null) {
                    mProductStatusGotListener.onProductStatusGot(list);
                }
            }
        }else{
            notConnectedToGooglePlay();
        }*/

        //------ Above old Codes


        if(mBillingClient != null) {
            mBillingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                    new PurchasesResponseListener() {
                        public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> listOfAllProducts) {


                            if(mBillingClient != null) {

                                if(listOfAllProducts != null) {

                                    a:for (Purchase item : listOfAllProducts) {
                                        for (String appSku : listApplicationSKU) {
                                            String productId = item.getProducts().get(0);


                                            if (productId.equals(appSku)) {

                                                if (item.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                                    BillingDB.getDatabase(activity).purchaseStatusDAO().updatePurchaseStatus(true, productId);
                                                    checkIsAcknowledged(item);
                                                }
                                            }
                                        }
                                    }

                                    //Below code just convert List<Purchase>  to HashMap<String,Purchase>
                                    HashMap<String, Purchase> list = initHashMapPurchaseDetails(listOfAllProducts);
                                    updateOwnedProductsOnDB(list);

                                    //Below isFirstOpen requred for fresh start..
                                    //it false so, product will get correct ones...
                                    //isFirstOpen = false;



                                    if (mProductStatusGotListener != null) {
                                        mProductStatusGotListener.onProductStatusGot(list);
                                    }
                                }
                            }else{
                                notConnectedToGooglePlay();
                            }


                        }
                    }
            );
        }



        //Above code gets product data such as product id & name & description
    }

    public void consumeProduct(String purchaseToken, String payload){
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        //.setDeveloperPayload(payload)
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
                String sku = purchase.getProducts().get(0);
                if(sku.equals(productName)){
                    hashMapPurchaseDetails.put(productName,purchase);
                }
            }
        }

        return hashMapPurchaseDetails;
    }


    //Below one old one
    /*@Deprecated
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
    }*/
    //Above one old one


    private HashMap<String,ProductDetails> initHashMapProductDetails(List<ProductDetails> listOfProductDetails){
        hashMapProductDetails = new HashMap<>();


        for(ProductDetails productDetails : listOfProductDetails){

            for(String productName : listApplicationSKU){
                if(productDetails.getProductId().equals(productName)){
                    hashMapProductDetails.put(productName,productDetails);
                }

            }

        }

        return hashMapProductDetails;
    }


    public ConnectToPlay setInAppPurchaseListener(InAppPurchaseListener listener){

        mInAppPurchaseListener = listener;

        return this;
    }

    public ConnectToPlay setProductStatusGotListener(ProductStatusGotListener listener){

        mProductStatusGotListener = listener;

        return this;
    }


    public ConnectToPlay setSuccessfullyPurchasedListener(SuccessfullyPurchasedListener listener){

        mSuccessfullyPurchasedListener = listener;


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


        if(isFirstOpen){
            if(shouldFirstProductsReturnTrue) {
                return true;

            }else{
              return BillingDB.getDatabase(activity).purchaseStatusDAO().isProductBought(skuName);
            }
        }
       else {

            return BillingDB.getDatabase(activity).purchaseStatusDAO().isProductBought(skuName);
        }
    }



    public  String statusOfProduct(String sku){

        if(whatIsProductStatus(sku)){
            return "Product Bought";
        }else{
            return "Is not bought";
        }
    }


    public static void printProductStatus(String wannaAddPrefix){

        for (String sku :YHYManager.listApplicationSKU ){

            if(wannaAddPrefix == null) {

                Log.d("status", getInstance().statusOfProduct(sku));


            }else{

                Log.d("status", wannaAddPrefix+": "+getInstance().statusOfProduct(sku));

            }
        }

    }



    public void endConnection(){
        if(mBillingClient != null){
            mBillingClient.endConnection();
        }
    }
    public ConnectToPlay shouldShowToast(boolean shouldShowToast){
        this.shouldShowToast = shouldShowToast;
        return  instance;
    }
    public ConnectToPlay shouldRestartApp(boolean shouldRestartApp){
        this.shouldRestartApp = shouldRestartApp;
        return  instance;
    }
    public ConnectToPlay shouldFirstProductsReturnTrue(boolean shouldFirstProductsReturnTrue){
        this.shouldFirstProductsReturnTrue = shouldFirstProductsReturnTrue;
        return  instance;
    }


    private ArrayList<QueryProductDetailsParams.Product>  setProductListForGetDetails(){
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        for(String productId : YHYManager.listApplicationSKU ){
            productList.add(  QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build());
        }

        return productList;
    }

}
