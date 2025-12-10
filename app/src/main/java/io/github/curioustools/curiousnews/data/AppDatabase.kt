package io.github.curioustools.curiousnews.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.curioustools.curiousnews.data.dao.NewsDao
import io.github.curioustools.curiousnews.data.entitiy.NewsEntity
import io.github.curioustools.curiousnews.commons.isDebugApp
import io.github.curioustools.curiousnews.commons.log
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Database(entities = [NewsEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    companion object {
        const val DB_NAME = "app_database"
        const val TABLE_NEWS = "news"

        fun allMigrations() = arrayOf(Migration1To2())

        fun instance(context: Context): AppDatabase{
            val dbBuilder = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            dbBuilder.addMigrations(*allMigrations())
            dbBuilder.also {dbBuilder ->
                if(isDebugApp()){
                    val executor: Executor = Executors.newSingleThreadExecutor()
                    val queryCallback = QueryCallback { sqlQuery, bindArgs ->
                        val str = "Room DB: executed query: `$sqlQuery` with args: `$bindArgs`"
                        log(str)
                    }
                    dbBuilder.setQueryCallback(queryCallback, executor)
                }
            }
            return dbBuilder.build()
        }
    }

}
class Migration1To2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        if ( db.isColumnExists(AppDatabase.TABLE_NEWS, "some_table_column").not() ) {
            db.execSQL("ALTER TABLE  ${AppDatabase.TABLE_NEWS} ADD COLUMN some_table_column TEXT NOT NULL DEFAULT ''")
        }
    }
}

fun SupportSQLiteDatabase.isColumnExists(tableName: String, columnName: String): Boolean {
    val cursor = this.query("PRAGMA table_info($tableName)")
    cursor.use {
        while (it.moveToNext()) {
            val nameIndex = it.getColumnIndex("name")
            if (nameIndex != -1) {
                val existingColumnName = it.getString(nameIndex)
                if (existingColumnName == columnName) {
                    return true
                }
            }
        }
    }
    return false
}