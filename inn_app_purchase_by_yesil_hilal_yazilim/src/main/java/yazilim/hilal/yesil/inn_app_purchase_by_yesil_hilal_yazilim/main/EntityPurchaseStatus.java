package yazilim.hilal.yesil.inn_app_purchase_by_yesil_hilal_yazilim.main;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;




@Entity(tableName = YHYManager.TABLE_PURCHASE_STATUS)
public class EntityPurchaseStatus  {

    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "productName")
    private String productName;

    @ColumnInfo(name = "isBought")
    private boolean isBought;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }
}
