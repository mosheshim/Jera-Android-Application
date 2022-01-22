package mosh.com.jera_v1.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.net.URL
import java.util.*

@Entity(tableName = "CartItems")
data class CartItem(
    @PrimaryKey
    @ColumnInfo(name = "cart_item_id")
    val id: String = "",

    @ColumnInfo(name = "product_id")
    val productId:String = "",

    @ColumnInfo(name = "image_url")
    val imageURL: String = "",

    @ColumnInfo(name = "product_name")
    val productName: String = "" ,

    val quantity:Int = 0,
    val price:Int = 0,
    val extra:Int? = null
)
