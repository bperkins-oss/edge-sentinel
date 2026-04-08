package com.bp22intel.edgesentinel.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bp22intel.edgesentinel.data.local.entity.KnownTowerEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class KnownTowerDao_Impl implements KnownTowerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<KnownTowerEntity> __insertionAdapterOfKnownTowerEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTowersByCountry;

  public KnownTowerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfKnownTowerEntity = new EntityInsertionAdapter<KnownTowerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `known_towers` (`id`,`mcc`,`mnc`,`lac`,`cid`,`latitude`,`longitude`,`range`,`radio`,`source`,`updated`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final KnownTowerEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMcc());
        statement.bindLong(3, entity.getMnc());
        statement.bindLong(4, entity.getLac());
        statement.bindLong(5, entity.getCid());
        statement.bindDouble(6, entity.getLatitude());
        statement.bindDouble(7, entity.getLongitude());
        statement.bindLong(8, entity.getRange());
        statement.bindString(9, entity.getRadio());
        statement.bindString(10, entity.getSource());
        statement.bindLong(11, entity.getUpdated());
      }
    };
    this.__preparedStmtOfDeleteTowersByCountry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM known_towers WHERE mcc = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTowers(final List<KnownTowerEntity> towers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfKnownTowerEntity.insert(towers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTowersByCountry(final int mcc, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTowersByCountry.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, mcc);
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
          __preparedStmtOfDeleteTowersByCountry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object findTower(final int mcc, final int mnc, final int lac, final int cid,
      final Continuation<? super KnownTowerEntity> $completion) {
    final String _sql = "SELECT * FROM known_towers WHERE mcc = ? AND mnc = ? AND lac = ? AND cid = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, mcc);
    _argIndex = 2;
    _statement.bindLong(_argIndex, mnc);
    _argIndex = 3;
    _statement.bindLong(_argIndex, lac);
    _argIndex = 4;
    _statement.bindLong(_argIndex, cid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<KnownTowerEntity>() {
      @Override
      @Nullable
      public KnownTowerEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfCid = CursorUtil.getColumnIndexOrThrow(_cursor, "cid");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRange = CursorUtil.getColumnIndexOrThrow(_cursor, "range");
          final int _cursorIndexOfRadio = CursorUtil.getColumnIndexOrThrow(_cursor, "radio");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "updated");
          final KnownTowerEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpMcc;
            _tmpMcc = _cursor.getInt(_cursorIndexOfMcc);
            final int _tmpMnc;
            _tmpMnc = _cursor.getInt(_cursorIndexOfMnc);
            final int _tmpLac;
            _tmpLac = _cursor.getInt(_cursorIndexOfLac);
            final int _tmpCid;
            _tmpCid = _cursor.getInt(_cursorIndexOfCid);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpRange;
            _tmpRange = _cursor.getInt(_cursorIndexOfRange);
            final String _tmpRadio;
            _tmpRadio = _cursor.getString(_cursorIndexOfRadio);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final long _tmpUpdated;
            _tmpUpdated = _cursor.getLong(_cursorIndexOfUpdated);
            _result = new KnownTowerEntity(_tmpId,_tmpMcc,_tmpMnc,_tmpLac,_tmpCid,_tmpLatitude,_tmpLongitude,_tmpRange,_tmpRadio,_tmpSource,_tmpUpdated);
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
  public Flow<KnownTowerEntity> observeTower(final int mcc, final int mnc, final int lac,
      final int cid) {
    final String _sql = "SELECT * FROM known_towers WHERE mcc = ? AND mnc = ? AND lac = ? AND cid = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, mcc);
    _argIndex = 2;
    _statement.bindLong(_argIndex, mnc);
    _argIndex = 3;
    _statement.bindLong(_argIndex, lac);
    _argIndex = 4;
    _statement.bindLong(_argIndex, cid);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"known_towers"}, new Callable<KnownTowerEntity>() {
      @Override
      @Nullable
      public KnownTowerEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfCid = CursorUtil.getColumnIndexOrThrow(_cursor, "cid");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRange = CursorUtil.getColumnIndexOrThrow(_cursor, "range");
          final int _cursorIndexOfRadio = CursorUtil.getColumnIndexOrThrow(_cursor, "radio");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "updated");
          final KnownTowerEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpMcc;
            _tmpMcc = _cursor.getInt(_cursorIndexOfMcc);
            final int _tmpMnc;
            _tmpMnc = _cursor.getInt(_cursorIndexOfMnc);
            final int _tmpLac;
            _tmpLac = _cursor.getInt(_cursorIndexOfLac);
            final int _tmpCid;
            _tmpCid = _cursor.getInt(_cursorIndexOfCid);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpRange;
            _tmpRange = _cursor.getInt(_cursorIndexOfRange);
            final String _tmpRadio;
            _tmpRadio = _cursor.getString(_cursorIndexOfRadio);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final long _tmpUpdated;
            _tmpUpdated = _cursor.getLong(_cursorIndexOfUpdated);
            _result = new KnownTowerEntity(_tmpId,_tmpMcc,_tmpMnc,_tmpLac,_tmpCid,_tmpLatitude,_tmpLongitude,_tmpRange,_tmpRadio,_tmpSource,_tmpUpdated);
          } else {
            _result = null;
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
  public Object getTowerCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM known_towers";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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

  @Override
  public Object getTowerCountByCountry(final int mcc,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM known_towers WHERE mcc = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, mcc);
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

  @Override
  public Object getInstalledCountries(final Continuation<? super List<Integer>> $completion) {
    final String _sql = "SELECT DISTINCT mcc FROM known_towers ORDER BY mcc";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Integer>>() {
      @Override
      @NonNull
      public List<Integer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Integer> _result = new ArrayList<Integer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Integer _item;
            _item = _cursor.getInt(0);
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

  @Override
  public Object findTowersInArea(final double minLat, final double maxLat, final double minLng,
      final double maxLng, final Continuation<? super List<KnownTowerEntity>> $completion) {
    final String _sql = "SELECT * FROM known_towers WHERE latitude BETWEEN ? AND ? AND longitude BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindDouble(_argIndex, minLat);
    _argIndex = 2;
    _statement.bindDouble(_argIndex, maxLat);
    _argIndex = 3;
    _statement.bindDouble(_argIndex, minLng);
    _argIndex = 4;
    _statement.bindDouble(_argIndex, maxLng);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<KnownTowerEntity>>() {
      @Override
      @NonNull
      public List<KnownTowerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMcc = CursorUtil.getColumnIndexOrThrow(_cursor, "mcc");
          final int _cursorIndexOfMnc = CursorUtil.getColumnIndexOrThrow(_cursor, "mnc");
          final int _cursorIndexOfLac = CursorUtil.getColumnIndexOrThrow(_cursor, "lac");
          final int _cursorIndexOfCid = CursorUtil.getColumnIndexOrThrow(_cursor, "cid");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRange = CursorUtil.getColumnIndexOrThrow(_cursor, "range");
          final int _cursorIndexOfRadio = CursorUtil.getColumnIndexOrThrow(_cursor, "radio");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "updated");
          final List<KnownTowerEntity> _result = new ArrayList<KnownTowerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final KnownTowerEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpMcc;
            _tmpMcc = _cursor.getInt(_cursorIndexOfMcc);
            final int _tmpMnc;
            _tmpMnc = _cursor.getInt(_cursorIndexOfMnc);
            final int _tmpLac;
            _tmpLac = _cursor.getInt(_cursorIndexOfLac);
            final int _tmpCid;
            _tmpCid = _cursor.getInt(_cursorIndexOfCid);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final int _tmpRange;
            _tmpRange = _cursor.getInt(_cursorIndexOfRange);
            final String _tmpRadio;
            _tmpRadio = _cursor.getString(_cursorIndexOfRadio);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final long _tmpUpdated;
            _tmpUpdated = _cursor.getLong(_cursorIndexOfUpdated);
            _item = new KnownTowerEntity(_tmpId,_tmpMcc,_tmpMnc,_tmpLac,_tmpCid,_tmpLatitude,_tmpLongitude,_tmpRange,_tmpRadio,_tmpSource,_tmpUpdated);
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
