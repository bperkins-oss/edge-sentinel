package com.bp22intel.edgesentinel.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bp22intel.edgesentinel.data.local.entity.BaselineEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BaselineDao_Impl implements BaselineDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BaselineEntity> __insertionAdapterOfBaselineEntity;

  private final EntityDeletionOrUpdateAdapter<BaselineEntity> __updateAdapterOfBaselineEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public BaselineDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBaselineEntity = new EntityInsertionAdapter<BaselineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `baselines` (`id`,`latitude`,`longitude`,`radius`,`label`,`cell_towers_json`,`wifi_aps_json`,`ble_count_min`,`ble_count_max`,`network_type_dist_json`,`observation_count`,`confidence`,`created_at`,`updated_at`,`day_profile_json`,`night_profile_json`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BaselineEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getLatitude());
        statement.bindDouble(3, entity.getLongitude());
        statement.bindDouble(4, entity.getRadius());
        if (entity.getLabel() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLabel());
        }
        statement.bindString(6, entity.getCellTowersJson());
        statement.bindString(7, entity.getWifiApsJson());
        statement.bindLong(8, entity.getBleCountMin());
        statement.bindLong(9, entity.getBleCountMax());
        statement.bindString(10, entity.getNetworkTypeDistJson());
        statement.bindLong(11, entity.getObservationCount());
        statement.bindString(12, entity.getConfidence());
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindLong(14, entity.getUpdatedAt());
        if (entity.getDayProfileJson() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getDayProfileJson());
        }
        if (entity.getNightProfileJson() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getNightProfileJson());
        }
      }
    };
    this.__updateAdapterOfBaselineEntity = new EntityDeletionOrUpdateAdapter<BaselineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `baselines` SET `id` = ?,`latitude` = ?,`longitude` = ?,`radius` = ?,`label` = ?,`cell_towers_json` = ?,`wifi_aps_json` = ?,`ble_count_min` = ?,`ble_count_max` = ?,`network_type_dist_json` = ?,`observation_count` = ?,`confidence` = ?,`created_at` = ?,`updated_at` = ?,`day_profile_json` = ?,`night_profile_json` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BaselineEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getLatitude());
        statement.bindDouble(3, entity.getLongitude());
        statement.bindDouble(4, entity.getRadius());
        if (entity.getLabel() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getLabel());
        }
        statement.bindString(6, entity.getCellTowersJson());
        statement.bindString(7, entity.getWifiApsJson());
        statement.bindLong(8, entity.getBleCountMin());
        statement.bindLong(9, entity.getBleCountMax());
        statement.bindString(10, entity.getNetworkTypeDistJson());
        statement.bindLong(11, entity.getObservationCount());
        statement.bindString(12, entity.getConfidence());
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindLong(14, entity.getUpdatedAt());
        if (entity.getDayProfileJson() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getDayProfileJson());
        }
        if (entity.getNightProfileJson() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getNightProfileJson());
        }
        statement.bindLong(17, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM baselines WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM baselines";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BaselineEntity baseline,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBaselineEntity.insertAndReturnId(baseline);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BaselineEntity baseline,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBaselineEntity.handle(baseline);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BaselineEntity>> getAll() {
    final String _sql = "SELECT * FROM baselines ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"baselines"}, new Callable<List<BaselineEntity>>() {
      @Override
      @NonNull
      public List<BaselineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCellTowersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "cell_towers_json");
          final int _cursorIndexOfWifiApsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "wifi_aps_json");
          final int _cursorIndexOfBleCountMin = CursorUtil.getColumnIndexOrThrow(_cursor, "ble_count_min");
          final int _cursorIndexOfBleCountMax = CursorUtil.getColumnIndexOrThrow(_cursor, "ble_count_max");
          final int _cursorIndexOfNetworkTypeDistJson = CursorUtil.getColumnIndexOrThrow(_cursor, "network_type_dist_json");
          final int _cursorIndexOfObservationCount = CursorUtil.getColumnIndexOrThrow(_cursor, "observation_count");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfDayProfileJson = CursorUtil.getColumnIndexOrThrow(_cursor, "day_profile_json");
          final int _cursorIndexOfNightProfileJson = CursorUtil.getColumnIndexOrThrow(_cursor, "night_profile_json");
          final List<BaselineEntity> _result = new ArrayList<BaselineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BaselineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpRadius;
            _tmpRadius = _cursor.getDouble(_cursorIndexOfRadius);
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpCellTowersJson;
            _tmpCellTowersJson = _cursor.getString(_cursorIndexOfCellTowersJson);
            final String _tmpWifiApsJson;
            _tmpWifiApsJson = _cursor.getString(_cursorIndexOfWifiApsJson);
            final int _tmpBleCountMin;
            _tmpBleCountMin = _cursor.getInt(_cursorIndexOfBleCountMin);
            final int _tmpBleCountMax;
            _tmpBleCountMax = _cursor.getInt(_cursorIndexOfBleCountMax);
            final String _tmpNetworkTypeDistJson;
            _tmpNetworkTypeDistJson = _cursor.getString(_cursorIndexOfNetworkTypeDistJson);
            final int _tmpObservationCount;
            _tmpObservationCount = _cursor.getInt(_cursorIndexOfObservationCount);
            final String _tmpConfidence;
            _tmpConfidence = _cursor.getString(_cursorIndexOfConfidence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpDayProfileJson;
            if (_cursor.isNull(_cursorIndexOfDayProfileJson)) {
              _tmpDayProfileJson = null;
            } else {
              _tmpDayProfileJson = _cursor.getString(_cursorIndexOfDayProfileJson);
            }
            final String _tmpNightProfileJson;
            if (_cursor.isNull(_cursorIndexOfNightProfileJson)) {
              _tmpNightProfileJson = null;
            } else {
              _tmpNightProfileJson = _cursor.getString(_cursorIndexOfNightProfileJson);
            }
            _item = new BaselineEntity(_tmpId,_tmpLatitude,_tmpLongitude,_tmpRadius,_tmpLabel,_tmpCellTowersJson,_tmpWifiApsJson,_tmpBleCountMin,_tmpBleCountMax,_tmpNetworkTypeDistJson,_tmpObservationCount,_tmpConfidence,_tmpCreatedAt,_tmpUpdatedAt,_tmpDayProfileJson,_tmpNightProfileJson);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super BaselineEntity> $completion) {
    final String _sql = "SELECT * FROM baselines WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BaselineEntity>() {
      @Override
      @Nullable
      public BaselineEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCellTowersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "cell_towers_json");
          final int _cursorIndexOfWifiApsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "wifi_aps_json");
          final int _cursorIndexOfBleCountMin = CursorUtil.getColumnIndexOrThrow(_cursor, "ble_count_min");
          final int _cursorIndexOfBleCountMax = CursorUtil.getColumnIndexOrThrow(_cursor, "ble_count_max");
          final int _cursorIndexOfNetworkTypeDistJson = CursorUtil.getColumnIndexOrThrow(_cursor, "network_type_dist_json");
          final int _cursorIndexOfObservationCount = CursorUtil.getColumnIndexOrThrow(_cursor, "observation_count");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfDayProfileJson = CursorUtil.getColumnIndexOrThrow(_cursor, "day_profile_json");
          final int _cursorIndexOfNightProfileJson = CursorUtil.getColumnIndexOrThrow(_cursor, "night_profile_json");
          final BaselineEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpRadius;
            _tmpRadius = _cursor.getDouble(_cursorIndexOfRadius);
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpCellTowersJson;
            _tmpCellTowersJson = _cursor.getString(_cursorIndexOfCellTowersJson);
            final String _tmpWifiApsJson;
            _tmpWifiApsJson = _cursor.getString(_cursorIndexOfWifiApsJson);
            final int _tmpBleCountMin;
            _tmpBleCountMin = _cursor.getInt(_cursorIndexOfBleCountMin);
            final int _tmpBleCountMax;
            _tmpBleCountMax = _cursor.getInt(_cursorIndexOfBleCountMax);
            final String _tmpNetworkTypeDistJson;
            _tmpNetworkTypeDistJson = _cursor.getString(_cursorIndexOfNetworkTypeDistJson);
            final int _tmpObservationCount;
            _tmpObservationCount = _cursor.getInt(_cursorIndexOfObservationCount);
            final String _tmpConfidence;
            _tmpConfidence = _cursor.getString(_cursorIndexOfConfidence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpDayProfileJson;
            if (_cursor.isNull(_cursorIndexOfDayProfileJson)) {
              _tmpDayProfileJson = null;
            } else {
              _tmpDayProfileJson = _cursor.getString(_cursorIndexOfDayProfileJson);
            }
            final String _tmpNightProfileJson;
            if (_cursor.isNull(_cursorIndexOfNightProfileJson)) {
              _tmpNightProfileJson = null;
            } else {
              _tmpNightProfileJson = _cursor.getString(_cursorIndexOfNightProfileJson);
            }
            _result = new BaselineEntity(_tmpId,_tmpLatitude,_tmpLongitude,_tmpRadius,_tmpLabel,_tmpCellTowersJson,_tmpWifiApsJson,_tmpBleCountMin,_tmpBleCountMax,_tmpNetworkTypeDistJson,_tmpObservationCount,_tmpConfidence,_tmpCreatedAt,_tmpUpdatedAt,_tmpDayProfileJson,_tmpNightProfileJson);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllSync(final Continuation<? super List<BaselineEntity>> $completion) {
    final String _sql = "SELECT * FROM baselines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BaselineEntity>>() {
      @Override
      @NonNull
      public List<BaselineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfCellTowersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "cell_towers_json");
          final int _cursorIndexOfWifiApsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "wifi_aps_json");
          final int _cursorIndexOfBleCountMin = CursorUtil.getColumnIndexOrThrow(_cursor, "ble_count_min");
          final int _cursorIndexOfBleCountMax = CursorUtil.getColumnIndexOrThrow(_cursor, "ble_count_max");
          final int _cursorIndexOfNetworkTypeDistJson = CursorUtil.getColumnIndexOrThrow(_cursor, "network_type_dist_json");
          final int _cursorIndexOfObservationCount = CursorUtil.getColumnIndexOrThrow(_cursor, "observation_count");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfDayProfileJson = CursorUtil.getColumnIndexOrThrow(_cursor, "day_profile_json");
          final int _cursorIndexOfNightProfileJson = CursorUtil.getColumnIndexOrThrow(_cursor, "night_profile_json");
          final List<BaselineEntity> _result = new ArrayList<BaselineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BaselineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpRadius;
            _tmpRadius = _cursor.getDouble(_cursorIndexOfRadius);
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpCellTowersJson;
            _tmpCellTowersJson = _cursor.getString(_cursorIndexOfCellTowersJson);
            final String _tmpWifiApsJson;
            _tmpWifiApsJson = _cursor.getString(_cursorIndexOfWifiApsJson);
            final int _tmpBleCountMin;
            _tmpBleCountMin = _cursor.getInt(_cursorIndexOfBleCountMin);
            final int _tmpBleCountMax;
            _tmpBleCountMax = _cursor.getInt(_cursorIndexOfBleCountMax);
            final String _tmpNetworkTypeDistJson;
            _tmpNetworkTypeDistJson = _cursor.getString(_cursorIndexOfNetworkTypeDistJson);
            final int _tmpObservationCount;
            _tmpObservationCount = _cursor.getInt(_cursorIndexOfObservationCount);
            final String _tmpConfidence;
            _tmpConfidence = _cursor.getString(_cursorIndexOfConfidence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpDayProfileJson;
            if (_cursor.isNull(_cursorIndexOfDayProfileJson)) {
              _tmpDayProfileJson = null;
            } else {
              _tmpDayProfileJson = _cursor.getString(_cursorIndexOfDayProfileJson);
            }
            final String _tmpNightProfileJson;
            if (_cursor.isNull(_cursorIndexOfNightProfileJson)) {
              _tmpNightProfileJson = null;
            } else {
              _tmpNightProfileJson = _cursor.getString(_cursorIndexOfNightProfileJson);
            }
            _item = new BaselineEntity(_tmpId,_tmpLatitude,_tmpLongitude,_tmpRadius,_tmpLabel,_tmpCellTowersJson,_tmpWifiApsJson,_tmpBleCountMin,_tmpBleCountMax,_tmpNetworkTypeDistJson,_tmpObservationCount,_tmpConfidence,_tmpCreatedAt,_tmpUpdatedAt,_tmpDayProfileJson,_tmpNightProfileJson);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
