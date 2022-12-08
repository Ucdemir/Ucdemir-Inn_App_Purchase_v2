# Android Google Play In APP Billing Library v:3.0.3 
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

 **Note-2: At fresh start, you can set "shouldFirstProductsReturnTrue" to true in order to  return products states  -> true for better user experience
 
  **Note-3: add this below commands inside application tag  in manifest
  
  ```java
        android:fullBackupContent="false"
        android:allowBackup="false"
        tools:replace="android:allowBackup"
```

# Read Carefully : 
-Every time the application is opened, all products are checked and the correct results are shown.

## Implementation
add permission to manifest

```android
<uses-permission android:name="com.android.vending.BILLING"/>
```

Add this In Project module  :


```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
```
 
Add this dependencies also inside project module

Moreover, You must have billing, room and kprogresshud dependency!

```java
ext.sharedGroup = {dependencyHandler->
   delegate = dependencyHandler
    implementation 'com.android.billingclient:billing:5.1.0'
    implementation "androidx.room:room-runtime:2.4.3"
    annotationProcessor 'androidx.room:room-compiler:2.4.3'
    implementation 'com.kaopiz:kprogresshud:1.2.0'
    implementation group: 'com.google.guava', name: 'guava', version: '11.0.2'
}
```

***Why should you add those in project module?***

Answer: Those  dependencies can be use on every module  if you add  on project module... So less size, No multidex!

  
  
 **Add this two lines in main module :**
 ```java
  implementation 'com.github.Ucdemir:Ucdemir-Inn_App_Purchase_v2:0.0.6.3'
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


