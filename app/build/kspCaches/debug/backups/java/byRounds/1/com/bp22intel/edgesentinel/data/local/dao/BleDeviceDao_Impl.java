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
import com.bp22intel.edgesentinel.data.local.entity.BleDeviceEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class BleDeviceDao_Impl implements BleDeviceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BleDeviceEntity> __insertionAdapterOfBleDeviceEntity;

  private final EntityDeletionOrUpdateAdapter<BleDeviceEntity> __updateAdapterOfBleDeviceEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBefore;

  public BleDeviceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBleDeviceEntity = new EntityInsertionAdapter<BleDeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ble_devices` (`id`,`mac_address`,`advertising_data_hash`,`manufacturer_id`,`device_name`,`first_seen`,`last_seen`,`location_clusters`,`seen_count`,`is_tracker_type`,`tracker_protocol`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BleDeviceEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMacAddress());
        statement.bindString(3, entity.getAdvertisingDataHash());
        if (entity.getManufacturerId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getManufacturerId());
        }
        if (entity.getDeviceName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDeviceName());
        }
        statement.bindLong(6, entity.getFirstSeen());
        statement.bindLong(7, entity.getLastSeen());
        statement.bindString(8, entity.getLocationClusters());
        statement.bindLong(9, entity.getSeenCount());
        final int _tmp = entity.isTrackerType() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getTrackerProtocol() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getTrackerProtocol());
        }
      }
    };
    this.__updateAdapterOfBleDeviceEntity = new EntityDeletionOrUpdateAdapter<BleDeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `ble_devices` SET `id` = ?,`mac_address` = ?,`advertising_data_hash` = ?,`manufacturer_id` = ?,`device_name` = ?,`first_seen` = ?,`last_seen` = ?,`location_clusters` = ?,`seen_count` = ?,`is_tracker_type` = ?,`tracker_protocol` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BleDeviceEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getMacAddress());
        statement.bindString(3, entity.getAdvertisingDataHash());
        if (entity.getManufacturerId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getManufacturerId());
        }
        if (entity.getDeviceName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDeviceName());
        }
        statement.bindLong(6, entity.getFirstSeen());
        statement.bindLong(7, entity.getLastSeen());
        statement.bindString(8, entity.getLocationClusters());
        statement.bindLong(9, entity.getSeenCount());
        final int _tmp = entity.isTrackerType() ? 1 : 0;
        statement.bindLong(10, _tmp);
        if (entity.getTrackerProtocol() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getTrackerProtocol());
        }
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteBefore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ble_devices WHERE last_seen < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BleDeviceEntity device, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBleDeviceEntity.insertAndReturnId(device);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BleDeviceEntity device, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBleDeviceEntity.handle(device);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBefore(final long before, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBefore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
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
          __preparedStmtOfDeleteBefore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BleDeviceEntity>> getAll() {
    final String _sql = "SELECT * FROM ble_devices ORDER BY last_seen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ble_devices"}, new Callable<List<BleDeviceEntity>>() {
      @Override
      @NonNull
      public List<BleDeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "mac_address");
          final int _cursorIndexOfAdvertisingDataHash = CursorUtil.getColumnIndexOrThrow(_cursor, "advertising_data_hash");
          final int _cursorIndexOfManufacturerId = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer_id");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "device_name");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfLocationClusters = CursorUtil.getColumnIndexOrThrow(_cursor, "location_clusters");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seen_count");
          final int _cursorIndexOfIsTrackerType = CursorUtil.getColumnIndexOrThrow(_cursor, "is_tracker_type");
          final int _cursorIndexOfTrackerProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "tracker_protocol");
          final List<BleDeviceEntity> _result = new ArrayList<BleDeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BleDeviceEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMacAddress;
            _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            final String _tmpAdvertisingDataHash;
            _tmpAdvertisingDataHash = _cursor.getString(_cursorIndexOfAdvertisingDataHash);
            final Integer _tmpManufacturerId;
            if (_cursor.isNull(_cursorIndexOfManufacturerId)) {
              _tmpManufacturerId = null;
            } else {
              _tmpManufacturerId = _cursor.getInt(_cursorIndexOfManufacturerId);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final String _tmpLocationClusters;
            _tmpLocationClusters = _cursor.getString(_cursorIndexOfLocationClusters);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            final boolean _tmpIsTrackerType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackerType);
            _tmpIsTrackerType = _tmp != 0;
            final String _tmpTrackerProtocol;
            if (_cursor.isNull(_cursorIndexOfTrackerProtocol)) {
              _tmpTrackerProtocol = null;
            } else {
              _tmpTrackerProtocol = _cursor.getString(_cursorIndexOfTrackerProtocol);
            }
            _item = new BleDeviceEntity(_tmpId,_tmpMacAddress,_tmpAdvertisingDataHash,_tmpManufacturerId,_tmpDeviceName,_tmpFirstSeen,_tmpLastSeen,_tmpLocationClusters,_tmpSeenCount,_tmpIsTrackerType,_tmpTrackerProtocol);
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
  public Object getByMacAddress(final String macAddress,
      final Continuation<? super BleDeviceEntity> $completion) {
    final String _sql = "SELECT * FROM ble_devices WHERE mac_address = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, macAddress);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BleDeviceEntity>() {
      @Override
      @Nullable
      public BleDeviceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "mac_address");
          final int _cursorIndexOfAdvertisingDataHash = CursorUtil.getColumnIndexOrThrow(_cursor, "advertising_data_hash");
          final int _cursorIndexOfManufacturerId = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer_id");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "device_name");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfLocationClusters = CursorUtil.getColumnIndexOrThrow(_cursor, "location_clusters");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seen_count");
          final int _cursorIndexOfIsTrackerType = CursorUtil.getColumnIndexOrThrow(_cursor, "is_tracker_type");
          final int _cursorIndexOfTrackerProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "tracker_protocol");
          final BleDeviceEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMacAddress;
            _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            final String _tmpAdvertisingDataHash;
            _tmpAdvertisingDataHash = _cursor.getString(_cursorIndexOfAdvertisingDataHash);
            final Integer _tmpManufacturerId;
            if (_cursor.isNull(_cursorIndexOfManufacturerId)) {
              _tmpManufacturerId = null;
            } else {
              _tmpManufacturerId = _cursor.getInt(_cursorIndexOfManufacturerId);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final String _tmpLocationClusters;
            _tmpLocationClusters = _cursor.getString(_cursorIndexOfLocationClusters);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            final boolean _tmpIsTrackerType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackerType);
            _tmpIsTrackerType = _tmp != 0;
            final String _tmpTrackerProtocol;
            if (_cursor.isNull(_cursorIndexOfTrackerProtocol)) {
              _tmpTrackerProtocol = null;
            } else {
              _tmpTrackerProtocol = _cursor.getString(_cursorIndexOfTrackerProtocol);
            }
            _result = new BleDeviceEntity(_tmpId,_tmpMacAddress,_tmpAdvertisingDataHash,_tmpManufacturerId,_tmpDeviceName,_tmpFirstSeen,_tmpLastSeen,_tmpLocationClusters,_tmpSeenCount,_tmpIsTrackerType,_tmpTrackerProtocol);
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
  public Object getByAdvertisingHash(final String hash,
      final Continuation<? super BleDeviceEntity> $completion) {
    final String _sql = "SELECT * FROM ble_devices WHERE advertising_data_hash = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, hash);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BleDeviceEntity>() {
      @Override
      @Nullable
      public BleDeviceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "mac_address");
          final int _cursorIndexOfAdvertisingDataHash = CursorUtil.getColumnIndexOrThrow(_cursor, "advertising_data_hash");
          final int _cursorIndexOfManufacturerId = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer_id");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "device_name");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfLocationClusters = CursorUtil.getColumnIndexOrThrow(_cursor, "location_clusters");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seen_count");
          final int _cursorIndexOfIsTrackerType = CursorUtil.getColumnIndexOrThrow(_cursor, "is_tracker_type");
          final int _cursorIndexOfTrackerProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "tracker_protocol");
          final BleDeviceEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMacAddress;
            _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            final String _tmpAdvertisingDataHash;
            _tmpAdvertisingDataHash = _cursor.getString(_cursorIndexOfAdvertisingDataHash);
            final Integer _tmpManufacturerId;
            if (_cursor.isNull(_cursorIndexOfManufacturerId)) {
              _tmpManufacturerId = null;
            } else {
              _tmpManufacturerId = _cursor.getInt(_cursorIndexOfManufacturerId);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final String _tmpLocationClusters;
            _tmpLocationClusters = _cursor.getString(_cursorIndexOfLocationClusters);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            final boolean _tmpIsTrackerType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackerType);
            _tmpIsTrackerType = _tmp != 0;
            final String _tmpTrackerProtocol;
            if (_cursor.isNull(_cursorIndexOfTrackerProtocol)) {
              _tmpTrackerProtocol = null;
            } else {
              _tmpTrackerProtocol = _cursor.getString(_cursorIndexOfTrackerProtocol);
            }
            _result = new BleDeviceEntity(_tmpId,_tmpMacAddress,_tmpAdvertisingDataHash,_tmpManufacturerId,_tmpDeviceName,_tmpFirstSeen,_tmpLastSeen,_tmpLocationClusters,_tmpSeenCount,_tmpIsTrackerType,_tmpTrackerProtocol);
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
  public Flow<List<BleDeviceEntity>> getTrackers() {
    final String _sql = "SELECT * FROM ble_devices WHERE is_tracker_type = 1 ORDER BY last_seen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ble_devices"}, new Callable<List<BleDeviceEntity>>() {
      @Override
      @NonNull
      public List<BleDeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "mac_address");
          final int _cursorIndexOfAdvertisingDataHash = CursorUtil.getColumnIndexOrThrow(_cursor, "advertising_data_hash");
          final int _cursorIndexOfManufacturerId = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer_id");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "device_name");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfLocationClusters = CursorUtil.getColumnIndexOrThrow(_cursor, "location_clusters");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seen_count");
          final int _cursorIndexOfIsTrackerType = CursorUtil.getColumnIndexOrThrow(_cursor, "is_tracker_type");
          final int _cursorIndexOfTrackerProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "tracker_protocol");
          final List<BleDeviceEntity> _result = new ArrayList<BleDeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BleDeviceEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMacAddress;
            _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            final String _tmpAdvertisingDataHash;
            _tmpAdvertisingDataHash = _cursor.getString(_cursorIndexOfAdvertisingDataHash);
            final Integer _tmpManufacturerId;
            if (_cursor.isNull(_cursorIndexOfManufacturerId)) {
              _tmpManufacturerId = null;
            } else {
              _tmpManufacturerId = _cursor.getInt(_cursorIndexOfManufacturerId);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final String _tmpLocationClusters;
            _tmpLocationClusters = _cursor.getString(_cursorIndexOfLocationClusters);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            final boolean _tmpIsTrackerType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackerType);
            _tmpIsTrackerType = _tmp != 0;
            final String _tmpTrackerProtocol;
            if (_cursor.isNull(_cursorIndexOfTrackerProtocol)) {
              _tmpTrackerProtocol = null;
            } else {
              _tmpTrackerProtocol = _cursor.getString(_cursorIndexOfTrackerProtocol);
            }
            _item = new BleDeviceEntity(_tmpId,_tmpMacAddress,_tmpAdvertisingDataHash,_tmpManufacturerId,_tmpDeviceName,_tmpFirstSeen,_tmpLastSeen,_tmpLocationClusters,_tmpSeenCount,_tmpIsTrackerType,_tmpTrackerProtocol);
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
  public Flow<List<BleDeviceEntity>> getRecentDevices(final long since) {
    final String _sql = "SELECT * FROM ble_devices WHERE last_seen > ? ORDER BY last_seen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ble_devices"}, new Callable<List<BleDeviceEntity>>() {
      @Override
      @NonNull
      public List<BleDeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "mac_address");
          final int _cursorIndexOfAdvertisingDataHash = CursorUtil.getColumnIndexOrThrow(_cursor, "advertising_data_hash");
          final int _cursorIndexOfManufacturerId = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer_id");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "device_name");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfLocationClusters = CursorUtil.getColumnIndexOrThrow(_cursor, "location_clusters");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seen_count");
          final int _cursorIndexOfIsTrackerType = CursorUtil.getColumnIndexOrThrow(_cursor, "is_tracker_type");
          final int _cursorIndexOfTrackerProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "tracker_protocol");
          final List<BleDeviceEntity> _result = new ArrayList<BleDeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BleDeviceEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMacAddress;
            _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            final String _tmpAdvertisingDataHash;
            _tmpAdvertisingDataHash = _cursor.getString(_cursorIndexOfAdvertisingDataHash);
            final Integer _tmpManufacturerId;
            if (_cursor.isNull(_cursorIndexOfManufacturerId)) {
              _tmpManufacturerId = null;
            } else {
              _tmpManufacturerId = _cursor.getInt(_cursorIndexOfManufacturerId);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final String _tmpLocationClusters;
            _tmpLocationClusters = _cursor.getString(_cursorIndexOfLocationClusters);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            final boolean _tmpIsTrackerType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackerType);
            _tmpIsTrackerType = _tmp != 0;
            final String _tmpTrackerProtocol;
            if (_cursor.isNull(_cursorIndexOfTrackerProtocol)) {
              _tmpTrackerProtocol = null;
            } else {
              _tmpTrackerProtocol = _cursor.getString(_cursorIndexOfTrackerProtocol);
            }
            _item = new BleDeviceEntity(_tmpId,_tmpMacAddress,_tmpAdvertisingDataHash,_tmpManufacturerId,_tmpDeviceName,_tmpFirstSeen,_tmpLastSeen,_tmpLocationClusters,_tmpSeenCount,_tmpIsTrackerType,_tmpTrackerProtocol);
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
  public Flow<List<BleDeviceEntity>> getFrequentDevices(final int minCount) {
    final String _sql = "SELECT * FROM ble_devices WHERE seen_count >= ? ORDER BY seen_count DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, minCount);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ble_devices"}, new Callable<List<BleDeviceEntity>>() {
      @Override
      @NonNull
      public List<BleDeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "mac_address");
          final int _cursorIndexOfAdvertisingDataHash = CursorUtil.getColumnIndexOrThrow(_cursor, "advertising_data_hash");
          final int _cursorIndexOfManufacturerId = CursorUtil.getColumnIndexOrThrow(_cursor, "manufacturer_id");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "device_name");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfLocationClusters = CursorUtil.getColumnIndexOrThrow(_cursor, "location_clusters");
          final int _cursorIndexOfSeenCount = CursorUtil.getColumnIndexOrThrow(_cursor, "seen_count");
          final int _cursorIndexOfIsTrackerType = CursorUtil.getColumnIndexOrThrow(_cursor, "is_tracker_type");
          final int _cursorIndexOfTrackerProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "tracker_protocol");
          final List<BleDeviceEntity> _result = new ArrayList<BleDeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BleDeviceEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpMacAddress;
            _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            final String _tmpAdvertisingDataHash;
            _tmpAdvertisingDataHash = _cursor.getString(_cursorIndexOfAdvertisingDataHash);
            final Integer _tmpManufacturerId;
            if (_cursor.isNull(_cursorIndexOfManufacturerId)) {
              _tmpManufacturerId = null;
            } else {
              _tmpManufacturerId = _cursor.getInt(_cursorIndexOfManufacturerId);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final String _tmpLocationClusters;
            _tmpLocationClusters = _cursor.getString(_cursorIndexOfLocationClusters);
            final int _tmpSeenCount;
            _tmpSeenCount = _cursor.getInt(_cursorIndexOfSeenCount);
            final boolean _tmpIsTrackerType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackerType);
            _tmpIsTrackerType = _tmp != 0;
            final String _tmpTrackerProtocol;
            if (_cursor.isNull(_cursorIndexOfTrackerProtocol)) {
              _tmpTrackerProtocol = null;
            } else {
              _tmpTrackerProtocol = _cursor.getString(_cursorIndexOfTrackerProtocol);
            }
            _item = new BleDeviceEntity(_tmpId,_tmpMacAddress,_tmpAdvertisingDataHash,_tmpManufacturerId,_tmpDeviceName,_tmpFirstSeen,_tmpLastSeen,_tmpLocationClusters,_tmpSeenCount,_tmpIsTrackerType,_tmpTrackerProtocol);
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
  public Object countNewDevicesSince(final long since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM ble_devices WHERE first_seen > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
