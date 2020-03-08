package com.luckyboy.libnetwork.cache;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.luckyboy.libcommon.global.AppGlobals;


@Database(entities = {Cache.class}, version = 1)
@TypeConverters({DataConvert.class})
public abstract class CacheDatabase extends RoomDatabase {

    private static final CacheDatabase database;

    private static final Migration sMigration = new Migration(1, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table teacher rename to student");
            database.execSQL("alter table teacher add column teacher_age INTEGER NOT NULL default 0");
        }
    };

    static {
        // 创建一个内存数据库
        // 但是这种数据库的数据只存在于内存中 也就是进程被杀之后 数据随之丢失
        // Room.inMemoryDatabaseBuilder()
        database = Room.databaseBuilder(AppGlobals.getInstance(), CacheDatabase.class, "ppd_cache.db")
                .allowMainThreadQueries()
                //设置查询的线程池
                //.openHelperFactory()
                //.setQueryExecutor()
                //数据库创建和打开后的回调
                //.addCallback()
                //设置数据库的日志模式
                //.setJournalMode()
                //数据库升级异常后根据指定版本回退
                .fallbackToDestructiveMigrationFrom()
                //.addMigrations(CacheDatabase.sMigration)
                .build();
    }

    // 编译后自动帮我们生成getCache的方法实现
    public abstract CacheDao getCacheDao();

    public static CacheDatabase getCacheDb(){
        return database;
    }

}
