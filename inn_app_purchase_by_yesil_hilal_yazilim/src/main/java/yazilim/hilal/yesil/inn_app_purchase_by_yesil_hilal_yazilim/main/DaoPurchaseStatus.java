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

    @Update
    public void updatePurchaseStatus(EntityPurchaseStatus entity);


    @Query("Select COUNT(*) from "+ YHYManager.TABLE_PURCHASE_STATUS + " where productName =:ProductName")
     Cursor isProductInsertedToDB(String ProductName);
}
