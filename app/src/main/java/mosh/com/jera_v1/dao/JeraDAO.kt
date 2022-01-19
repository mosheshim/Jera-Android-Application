package mosh.com.jera_v1.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import mosh.com.jera_v1.models.CartItem
//TODO make the delete functions not suspended
@Dao
interface JeraDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCartItem(cartItem: CartItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCartItems(cartItems: List<CartItem>)

    @Query("SELECT * FROM CartItems")
    suspend fun getCart():List<CartItem>

    @Query("SELECT * FROM CartItems")
    fun getLiveCart():LiveData<List<CartItem>>

    @Delete
    suspend fun deleteItem(cartItem: CartItem)

    @Query("DELETE FROM CartItems WHERE cart_item_id = :id")
    suspend fun deleteItem(id:String)

    @Query("DELETE FROM CartItems")
    suspend fun deleteCart()

}