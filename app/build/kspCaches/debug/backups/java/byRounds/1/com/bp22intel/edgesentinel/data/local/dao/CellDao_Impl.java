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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bp22intel.edgesentinel.data.local.entity.CellTowerEntity;
import java.lang.Class;
import java.lang.Double;
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
public final class CellDao_Impl implements CellDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CellTowerEntity> __insertionAdapterOfCellTowerEntity;

  private final EntityDeletionOrUpdateAdapter<CellTowerEntity> __updateAdapterOfCellTowerEntity;

  public CellDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCellTowerEntity = new EntityInsertionAdapter<CellTowerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cells` (`id`,`cid`,`lac_tac`,`mcc`,`mnc`,`signal_strength`,`network_type`,`latitude`,`longitude`,`first_seen`,`last_seen`,`times_seen`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CellTowerEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCid());
        statement.bindLong(3, entity.getLacTac());
        statement.bindLong(4, entity.getMcc());
        statement.bindLong(5, entity.getMnc());
        statement.bindLong(6, entity.getSignalStrength());
        statement.bindString(7, entity.getNetworkType());
        if (entity.getLatitude() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getLongitude());
        }
        statement.bindLong(10, entity.getFirstSeen());
        statement.bindLong(11, entity.getLastSeen());
        statement.bindLong(12, entity.getTimesSeen());
      }
    };
    this.__updateAdapterOfCellTowerEntity = new EntityDeletionOrUpdateAdapter<CellTowerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cells` SET `id` = ?,`cid` = ?,`lac_tac` = ?,`mcc` = ?,`mnc` = ?,`signal_strength` = ?,`network_type` = ?,`latitude` = ?,`longitude` = ?,`first_seen` = ?,`last_seen` = ?,`times_seen` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CellTowerEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCid());
        statement.bindLong(3, entity.getLacTac());
        statement.bindLong(4, entity.getMcc());
        statement.bindLong(5, entity.getMnc());
        statement.bindLong(6, entity.getSignalStrength());
        statement.bindString(7, entity.getNetworkType());
        if (entity.getLatitude() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getLongitude());
        }
        statement.bindLong(10, entity.getFirstSeen());
        statement.bindLong(11, entity.getLastSeen());
        statement.bindLong(12, entity.getTimesSeen());
        statement.bindLong(13, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final CellTowerEntity cell, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCellTowerEntity.insertAndReturnId(cell);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final CellTowerEntity cell, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCellTowerEntity.handle(cell);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CellTowerEntity>> getAll() {
    final String _sql = "SELECT * FROM cells ORDER BY last_seen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cells"}, new Callable<List<CellTowerEntity>>() {
      @Override
      @NonNull
      public List<CellTowerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCid = CursorUtil.getColumnIndexOrThrow(_cursor, "cid");
          final int _cursorIndexOfLacTac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac_tac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signal_strength");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "network_type");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfTimesSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "times_seen");
          final List<CellTowerEntity> _result = new ArrayList<CellTowerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpCid;
            _tmpCid = _cursor.getInt(_cursorIndexOfCid);
            final int _tmpLacTac;
            _tmpLacTac = _cursor.getInt(_cursorIndexOfLacTac);
            final int _tmpMcc;
            _tmpMcc = _cursor.getInt(_cursorIndexOfMcc);
            final int _tmpMnc;
            _tmpMnc = _cursor.getInt(_cursorIndexOfMnc);
            final int _tmpSignalStrength;
            _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            final String _tmpNetworkType;
            _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final int _tmpTimesSeen;
            _tmpTimesSeen = _cursor.getInt(_cursorIndexOfTimesSeen);
            _item = new CellTowerEntity(_tmpId,_tmpCid,_tmpLacTac,_tmpMcc,_tmpMnc,_tmpSignalStrength,_tmpNetworkType,_tmpLatitude,_tmpLongitude,_tmpFirstSeen,_tmpLastSeen,_tmpTimesSeen);
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
  public Object getByCid(final int cid, final Continuation<? super CellTowerEntity> $completion) {
    final String _sql = "SELECT * FROM cells WHERE cid = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CellTowerEntity>() {
      @Override
      @Nullable
      public CellTowerEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCid = CursorUtil.getColumnIndexOrThrow(_cursor, "cid");
          final int _cursorIndexOfLacTac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac_tac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signal_strength");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "network_type");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfTimesSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "times_seen");
          final CellTowerEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpCid;
            _tmpCid = _cursor.getInt(_cursorIndexOfCid);
            final int _tmpLacTac;
            _tmpLacTac = _cursor.getInt(_cursorIndexOfLacTac);
            final int _tmpMcc;
            _tmpMcc = _cursor.getInt(_cursorIndexOfMcc);
            final int _tmpMnc;
            _tmpMnc = _cursor.getInt(_cursorIndexOfMnc);
            final int _tmpSignalStrength;
            _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            final String _tmpNetworkType;
            _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final int _tmpTimesSeen;
            _tmpTimesSeen = _cursor.getInt(_cursorIndexOfTimesSeen);
            _result = new CellTowerEntity(_tmpId,_tmpCid,_tmpLacTac,_tmpMcc,_tmpMnc,_tmpSignalStrength,_tmpNetworkType,_tmpLatitude,_tmpLongitude,_tmpFirstSeen,_tmpLastSeen,_tmpTimesSeen);
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
  public Object getByLacTac(final int lacTac,
      final Continuation<? super List<CellTowerEntity>> $completion) {
    final String _sql = "SELECT * FROM cells WHERE lac_tac = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, lacTac);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CellTowerEntity>>() {
      @Override
      @NonNull
      public List<CellTowerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCid = CursorUtil.getColumnIndexOrThrow(_cursor, "cid");
          final int _cursorIndexOfLacTac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac_tac");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfSignalStrength = CursorUtil.getColumnIndexOrThrow(_cursor, "signal_strength");
          final int _cursorIndexOfNetworkType = CursorUtil.getColumnIndexOrThrow(_cursor, "network_type");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "first_seen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "last_seen");
          final int _cursorIndexOfTimesSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "times_seen");
          final List<CellTowerEntity> _result = new ArrayList<CellTowerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CellTowerEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpCid;
            _tmpCid = _cursor.getInt(_cursorIndexOfCid);
            final int _tmpLacTac;
            _tmpLacTac = _cursor.getInt(_cursorIndexOfLacTac);
            final int _tmpMcc;
            _tmpMcc = _cursor.getInt(_cursorIndexOfMcc);
            final int _tmpMnc;
            _tmpMnc = _cursor.getInt(_cursorIndexOfMnc);
            final int _tmpSignalStrength;
            _tmpSignalStrength = _cursor.getInt(_cursorIndexOfSignalStrength);
            final String _tmpNetworkType;
            _tmpNetworkType = _cursor.getString(_cursorIndexOfNetworkType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final int _tmpTimesSeen;
            _tmpTimesSeen = _cursor.getInt(_cursorIndexOfTimesSeen);
            _item = new CellTowerEntity(_tmpId,_tmpCid,_tmpLacTac,_tmpMcc,_tmpMnc,_tmpSignalStrength,_tmpNetworkType,_tmpLatitude,_tmpLongitude,_tmpFirstSeen,_tmpLastSeen,_tmpTimesSeen);
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
