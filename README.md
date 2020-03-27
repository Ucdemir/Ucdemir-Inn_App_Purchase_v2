# Android Google Play Billing Library

* Library is supported for "INAPP"
* Subscription  will be supported later!
* Library use Roomdb for your products,You dont need implementation for this
* Library use Shared dependency. In your app and library use same dependency, Your app will be less sized
and No multidex needed


In Project add:this


```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
```
 
 and again project add 

```java
ext.sharedGroup = {dependencyHandler->
    delegate = dependencyHandler
    implementation 'com.android.billingclient:billing:2.1.0'
    implementation "androidx.room:room-runtime:2.2.5"
    annotationProcessor 'androidx.room:room-compiler:2.2.5'
    implementation 'com.kaopiz:kprogresshud:1.1.0'
}
```

### Why you adding this? :

Answer: this dependency can be use both app and library modules (less space)

  
  
  in main module add this:
 ```java
  implementation 'com.github.Ucdemir:Ucdemir-Inn_App_Purchase_v2:0.0.4.5'
```

  below implemention add this as exactly typed below
  
   ```java
     sharedGroup dependencies
```



## How to use Library ?

AS you know every android application have one Application class... If you dont have Applcation class lets create


In your project of Application class add list of your product skus:

   ```java
  public static List<String> listOfApplicationSKU;
	
```

   ```java
        listOfApplicationSKU = new ArrayList<>();
        listOfApplicationSKU.add("bor");
        listOfApplicationSKU.add("gas");
        listOfApplicationSKU.add("noads");
        listOfApplicationSKU.add("pro");
        listOfApplicationSKU.add("sun");
	
```
        
  and init  to library:
  
  
        ConnectToPlay.initBillingForApp(this,listOfApplicationSKU);
        
        
In your MainActivity of your applcation:

use this methods:

   ```java
   ConnectToPlay.getInstance().initForActivity(this).startToWork(ConnectToPlay.CallType.CheckProductStatus).
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


If you need  listener for after consumed product,

you can set "afterConsume" listener, otherwise dont use it


If you need Acknowledge listener use onAcknowledgePurchaseResponse listener otherwise dont use it.

### You have to use "setProductStatusGotListener" listener.
Because Your application have to check products every time app start.

this listener have to call on Your application's MainActivity... 

Google guide says this:

**Call queryPurchases() every time your app launches so that you can restore any purchases that a user has made since the app last stopped. Call queryPurchases() in your onResume() method, because a user can make a purchase when your app is in the background (for example, redeeming a promo code in the Google Play Store app).**

You can call this listener for onResume if you want!

"setProductStatusGotListener" calls, after queryPurchases()  is executed..

Our Library use RoomDB for you for your product.... If you want to check product status 

you can use this method:

   ```java
ConnectToPlay.getInstance().whatIsProductStatus(sku);
  ```
  
sku parameter is your product name which added in application.

You can check every product with 

   ```java
ConnectToPlay.getInstance().whatIsProductStatus(sku);
  ```

setProductStatusGotListener returns hasmaps 

HashMap<String, Purchase> hashMapPurchaseDetails

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

listener, named 'returnAllProductsDetailsFromPlayStore' returns HashMap:

First parameter product name(sku)

Second is Google SkuDetails:

**You need SkuDetails for buy product!**

For example If you have named "pro" product, You can call
  
  ```java
  ConnectToPlay.getInstance().startBuyOut(this, hashMapSkuDetails.get("pro"));
    ```
    
  this method should used, in when user click to buy product
