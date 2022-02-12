package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;


import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DaoPurchaseStatus {
    @Insert
    public long insert(EntityPurchaseStatus data);

    @Query("Select * from "+ YHYManager.TABLE_PURCHASE_STATUS + " where productName =:productName")
    int getUID(String productName);

    @Query("UPDATE "+ YHYManager.TABLE_PURCHASE_STATUS + " SET isBought=:isBought where productName =:productName")
    public void  updatePurchaseStatus(boolean isBought, String productName);


    @Query("Select isBought from "+ YHYManager.TABLE_PURCHASE_STATUS + " where productName =:productName")
    boolean isProductBought(String productName);


    @Query("Select COUNT(*) from "+ YHYManager.TABLE_PURCHASE_STATUS + " where productName =:ProductName")
     int getCountOfSKU(String ProductName);


}
