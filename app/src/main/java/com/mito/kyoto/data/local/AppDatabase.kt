package com.mito.kyoto.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.mito.kyoto.data.local.dao.UserDao
import com.mito.kyoto.data.local.entities.*

@Database(
    entities = [UserEntity::class, FriendshipEntity::class, FriendRequestEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mito_kyoto.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}