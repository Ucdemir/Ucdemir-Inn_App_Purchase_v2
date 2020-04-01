# Android Google Play In APP Billing Library v:2.1.0 or newer
**Yeşil Hilal Yazilim : As your service, Develop Future**

[![](https://jitpack.io/v/Ucdemir/Ucdemir-Inn_App_Purchase_v2.svg)](https://jitpack.io/#Ucdemir/Ucdemir-Inn_App_Purchase_v2)



You can test this library, with real device:
[Browse Example on Google Play](https://play.google.com/store/apps/details?id=yazilim.hilal.yesil.inn_app_purchase)

[This Library designed as Google guide](https://developer.android.com/google/play/billing/billing_library_overview)



**Note: Google use cache functions.. That result some updates to be updated lately... If you want immediate update. Clear Google Play Application data from device settings**



* Library is supported for "**INAPP**"
* Subscription  will be supported later!
* Library use Roomdb for your products, You don’t need implementation to check status of your products
* Library use Shared dependency. Your app will be less sized
and no multidex needed
* Library checks your products status on every time app starts. You can get status(bought or not)!
* Every product bought by client need to be " Acknowledged" in **SUCCES State**. Library is making this for you! 
* Library support (immediate buy, response late purchase succus, response late purchase reject, user canceled purchase)
* You can check example app!

 **Note: If user come situation with "**response late, purchase success**", User need to use apps between three days for
Acknowledged. Otherwise item will be refunded! This is Google rule!**

**You can't use example project with emulator since it does not have play store**

## Implementation

don't forget to add permission to manifest

```android
<uses-permission android:name="com.android.vending.BILLING"/>
```

In Project module add this:


```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
```
 
Again project module add this dependencies

 Note: You can have newer version of this libraries,
 
 Moreover, You must have billing, room and kprogresshud dependency!

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
  implementation 'com.github.Ucdemir:Ucdemir-Inn_App_Purchase_v2:0.0.5.1'
  sharedGroup dependencies
```





## How to use Library ?

        
### MainActivity:

Create String Array List:

 ```java
    public static List<String> listOfApplicationSKU;
```
 

***Add your products names (SKU)***


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


If you need listener for after consumed product, you can set "afterConsume" listener, otherwise dont use it

If you need acknowledge listener, use onAcknowledgePurchaseResponse listener. Otherwise dont use it.

***You have to use "setProductStatusGotListener" listener.***

Because your application have to check products every time app start.

This listener, have to call on Your application's **MainActivity** 

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

"setProductStatusGotListener" called, after queryPurchases()  is executed..

Our Library use RoomDB for you for your product.... If you want to check product status 

you can use this method:

   ```java
ConnectToPlay.getInstance().whatIsProductStatus(sku);
  ```
  
sku is your product name which is added in MainActivity in to Arraylist.

You can check every product with 

   ```java
ConnectToPlay.getInstance().whatIsProductStatus(sku);
  ```

setProductStatusGotListener returns Hasmaps 

***HashMap<String, Purchase> hashMapPurchaseDetails***

**Why listener return this?**

Answer: If you want to consume product use this:

   ```java
Purchase p = hashMapPurchaseDetails.get(sku);
ConnectToPlay.getInstance().consumeProduct(p.getPurchaseToken(),p.getDeveloperPayload());
  ```
  
Hasmap's first parameter name of product(sku)

second parameter is Purchase Class off Google in app billing class
        
        
        
### In your App billing Actvity/Fragment use this:

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

You can remove hud, if you don’t want spinner.
**Be Attention, There is no billingSKUS() function on this**

listener, named 'returnAllProductsDetailsFromPlayStore' returns HashMap:

First parameter product name(sku)

Second is Google SkuDetails:

**You need SkuDetails for buy product!**

For example: If you have named "pro" product, You must call in button to start buying
  
  
  ```java
  ConnectToPlay.getInstance().startBuyOut(this, hashMapSkuDetails.get("pro"));
  ```
  
  
  For more details, view example!
