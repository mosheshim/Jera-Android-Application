package mosh.com.jera_v1.database

import android.content.Context
import androidx.room.*
import mosh.com.jera_v1.dao.JeraDAO
import mosh.com.jera_v1.models.CartItem

const val DB_NAME = "JeraDB"

@Database(entities = [CartItem::class], version =1)
abstract class JeraDataBase: RoomDatabase() {
    companion object{
        fun create(context: Context):JeraDataBase{
            return Room.databaseBuilder(
                context,
                JeraDataBase::class.java,
                DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    abstract fun JeraDao():JeraDAO
}