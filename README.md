# Android Google Play Billing Library v:2.1.0 or newer

You can test this library, with real device:
[Browse on Google Play](https://play.google.com/store/apps/details?id=yazilim.hilal.yesil.inn_app_purchase)

**Note: Google use cache functions.. That result some updates to be updated lately... If you want immediate update. Clear Google Play data from settings**



* Library is supported for "**INAPP**"
* Subscription  will be supported later!
* Library use Roomdb for your products, You dont need implementation to check status of your products
* Library use Shared dependency. Your app will be less sized
and no multidex needed
* Library checks your products status on everytime app starts. You can get status(bought or not)!
* Every product bought by client need to be " Acknowledged" in **SUCCES State**. Library is making this for you! 
* Library support (immediate buy, response late purchase succus, response late purchase reject, user canceled purchase)
* You can check example app!

 **Note: If user come situation with "**response late purchase succus**", User need to use apps between three days for
Acknowledged. Otherwise item wil be refunded! This is Google rule!**

**You can't use example project with emulator since it does not have play store**

## Implementation

don't forget to add permission to manifest

```android
<uses-permission android:name="com.android.vending.BILLING"/>
```

In Project mobule add this:


```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
```
 
 and again project module  add 

```java
ext.sharedGroup = {dependencyHandler->
    delegate = dependencyHandler
    implementation 'com.android.billingclient:billing:2.1.0'
    implementation "androidx.room:room-runtime:2.2.5"
    annotationProcessor 'androidx.room:room-compiler:2.2.5'
    implementation 'com.kaopiz:kprogresshud:1.1.0'
}
```



***Why you adding this?***

Answer: This dependencies can be use on every module your app have.. So less size, No multidex!

  
  
 **in main module add this two lines:**
 ```java
  implementation 'com.github.Ucdemir:Ucdemir-Inn_App_Purchase_v2:0.0.4.5'
  sharedGroup dependencies
```





## How to use Library ?

        
### MainActivity:

Create String ArrayList:

 ```java
    public static List<String> listOfApplicationSKU;
```
 

***Add your products names(SKU)***


 ```java
     listOfApplicationSKU = new ArrayList<>();
        listOfApplicationSKU.add("bor");
        listOfApplicationSKU.add("gas");
        listOfApplicationSKU.add("noads");
        listOfApplicationSKU.add("pro");
        listOfApplicationSKU.add("sun");

  ``` 
Call StartToWork with Enum parameter CallType.CheckProductStatus. 
This method checks of all your products status. User bought or rejected. 
call billingSKUS after initActivity with passing all of your skus 


 ```java
ConnectToPlay.getInstance().initForActivity(this).billingSKUS(listOfApplicationSKU).startToWork(ConnectToPlay.CallType.CheckProductStatus).
                setProductStatusGotListener(new ProductStatusGotListener() {
                    @Override
                    public void onProductStatusGot(HashMap<String, Purchase> hashMapPurchaseDetails) {
                    

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
```


If you need  listener for after consumed product, you can set "afterConsume" listener, otherwise dont use it

If you need Acknowledge listener, use onAcknowledgePurchaseResponse listener. Otherwise dont use it.

***You have to use "setProductStatusGotListener" listener.***

Because Your application have to check products every time app start.

this listener, have to call on Your application's **MainActivity** 

**Note: "startToWork" function have enum parameter***


   ```java
    public enum CallType{
        GetPriceProducts,
        CheckProductStatus,
    }
  ```
**Use  CheckProductStatus in your MainActivity**

**Use GetPriceProducts in your billing Actvity/fragment**

Google guide says this:

**Call queryPurchases() every time your app launches so that you can restore any purchases that a user has made since the app last stopped. Call queryPurchases() in your onResume() method, because a user can make a purchase when your app is in the background (for example, redeeming a promo code in the Google Play Store app).**

You can call this listener for onResume if you want!

"setProductStatusGotListener" calls, after queryPurchases()  is executed..

Our Library use RoomDB for you for your product.... If you want to check product status 

you can use this method:

   ```java
ConnectToPlay.getInstance().whatIsProductStatus(sku);
  ```
  
sku is your product name which is added in application.

You can check every product with 

   ```java
ConnectToPlay.getInstance().whatIsProductStatus(sku);
  ```

setProductStatusGotListener returns Hasmaps 

***HashMap<String, Purchase> hashMapPurchaseDetails***

**Why library return this?**

Answer: If you want to consume product use this:

   ```java
Purchase p = hashMapPurchaseDetails.get(sku);
ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());
  ```
  
Hasmap's first parameter name of product(sku)

second paramter is Purchase Class off Google in app biling class
        
        
        
### In your App biling Actvity/Fragment use this:

   ```java
   ConnectToPlay.getInstance().initForActivity(this).showHud("Loading").startToWork(ConnectToPlay.CallType.GetPriceProducts)
                .setInAppPurchaseListener(new InAppPurchaseListener() {
                    @Override
                    public void returnAllProductsDetailsFromPlayStore(HashMap<String,SkuDetails> hashMapSkuDetails) {
                        ProFragment.this.hashMapSkuDetails = hashMapSkuDetails;
                        setPrice();

                        ConnectToPlay.getInstance().hideHud();
                    }


                });

  ```

You can remove hud, if you dont want spinner.
**Be Attention, There is no billingSKUS() function on this**

listener, named 'returnAllProductsDetailsFromPlayStore' returns HashMap:

First parameter product name(sku)

Second is Google SkuDetails:

**You need SkuDetails for buy product!**

For example: If you have named "pro" product, You can call
  
  
  ```java
  ConnectToPlay.getInstance().startBuyOut(this, hashMapSkuDetails.get("pro"));
  ```
    
  this method should used, in when user click to buy product
