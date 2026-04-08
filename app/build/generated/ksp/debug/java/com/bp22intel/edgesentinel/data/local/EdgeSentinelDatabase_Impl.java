package com.bp22intel.edgesentinel.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.bp22intel.edgesentinel.data.local.dao.AlertDao;
import com.bp22intel.edgesentinel.data.local.dao.AlertDao_Impl;
import com.bp22intel.edgesentinel.data.local.dao.BaselineDao;
import com.bp22intel.edgesentinel.data.local.dao.BaselineDao_Impl;
import com.bp22intel.edgesentinel.data.local.dao.BleDeviceDao;
import com.bp22intel.edgesentinel.data.local.dao.BleDeviceDao_Impl;
import com.bp22intel.edgesentinel.data.local.dao.CellDao;
import com.bp22intel.edgesentinel.data.local.dao.CellDao_Impl;
import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao;
import com.bp22intel.edgesentinel.data.local.dao.KnownTowerDao_Impl;
import com.bp22intel.edgesentinel.data.local.dao.ScanDao;
import com.bp22intel.edgesentinel.data.local.dao.ScanDao_Impl;
import com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao;
import com.bp22intel.edgesentinel.data.local.dao.TrustedNetworkDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EdgeSentinelDatabase_Impl extends EdgeSentinelDatabase {
  private volatile CellDao _cellDao;

  private volatile AlertDao _alertDao;

  private volatile ScanDao _scanDao;

  private volatile BleDeviceDao _bleDeviceDao;

  private volatile BaselineDao _baselineDao;

  private volatile KnownTowerDao _knownTowerDao;

  private volatile TrustedNetworkDao _trustedNetworkDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cells` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cid` INTEGER NOT NULL, `lac_tac` INTEGER NOT NULL, `mcc` INTEGER NOT NULL, `mnc` INTEGER NOT NULL, `signal_strength` INTEGER NOT NULL, `network_type` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, `first_seen` INTEGER NOT NULL, `last_seen` INTEGER NOT NULL, `times_seen` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `alerts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `threat_type` TEXT NOT NULL, `severity` TEXT NOT NULL, `confidence` TEXT NOT NULL, `summary` TEXT NOT NULL, `details_json` TEXT NOT NULL, `cell_id` INTEGER, `acknowledged` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `cell_count` INTEGER NOT NULL, `threat_level` TEXT NOT NULL, `duration_ms` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ble_devices` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `mac_address` TEXT NOT NULL, `advertising_data_hash` TEXT NOT NULL, `manufacturer_id` INTEGER, `device_name` TEXT, `first_seen` INTEGER NOT NULL, `last_seen` INTEGER NOT NULL, `location_clusters` TEXT NOT NULL, `seen_count` INTEGER NOT NULL, `is_tracker_type` INTEGER NOT NULL, `tracker_protocol` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `baselines` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `radius` REAL NOT NULL, `label` TEXT, `cell_towers_json` TEXT NOT NULL, `wifi_aps_json` TEXT NOT NULL, `ble_count_min` INTEGER NOT NULL, `ble_count_max` INTEGER NOT NULL, `network_type_dist_json` TEXT NOT NULL, `observation_count` INTEGER NOT NULL, `confidence` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `day_profile_json` TEXT, `night_profile_json` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `known_towers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `mcc` INTEGER NOT NULL, `mnc` INTEGER NOT NULL, `lac` INTEGER NOT NULL, `cid` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `range` INTEGER NOT NULL, `radio` TEXT NOT NULL, `source` TEXT NOT NULL, `updated` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_known_towers_mcc_mnc_lac_cid` ON `known_towers` (`mcc`, `mnc`, `lac`, `cid`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `trusted_networks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bssid` TEXT NOT NULL, `ssid` TEXT NOT NULL, `label` TEXT, `added_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fb4701e79f0ee0f938549e63cd8b7850')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cells`");
        db.execSQL("DROP TABLE IF EXISTS `alerts`");
        db.execSQL("DROP TABLE IF EXISTS `scans`");
        db.execSQL("DROP TABLE IF EXISTS `ble_devices`");
        db.execSQL("DROP TABLE IF EXISTS `baselines`");
        db.execSQL("DROP TABLE IF EXISTS `known_towers`");
        db.execSQL("DROP TABLE IF EXISTS `trusted_networks`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCells = new HashMap<String, TableInfo.Column>(12);
        _columnsCells.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("cid", new TableInfo.Column("cid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("lac_tac", new TableInfo.Column("lac_tac", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("mcc", new TableInfo.Column("mcc", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("mnc", new TableInfo.Column("mnc", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("signal_strength", new TableInfo.Column("signal_strength", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("network_type", new TableInfo.Column("network_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("first_seen", new TableInfo.Column("first_seen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("last_seen", new TableInfo.Column("last_seen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCells.put("times_seen", new TableInfo.Column("times_seen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCells = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCells = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCells = new TableInfo("cells", _columnsCells, _foreignKeysCells, _indicesCells);
        final TableInfo _existingCells = TableInfo.read(db, "cells");
        if (!_infoCells.equals(_existingCells)) {
          return new RoomOpenHelper.ValidationResult(false, "cells(com.bp22intel.edgesentinel.data.local.entity.CellTowerEntity).\n"
                  + " Expected:\n" + _infoCells + "\n"
                  + " Found:\n" + _existingCells);
        }
        final HashMap<String, TableInfo.Column> _columnsAlerts = new HashMap<String, TableInfo.Column>(9);
        _columnsAlerts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("threat_type", new TableInfo.Column("threat_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("severity", new TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("confidence", new TableInfo.Column("confidence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("details_json", new TableInfo.Column("details_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("cell_id", new TableInfo.Column("cell_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlerts.put("acknowledged", new TableInfo.Column("acknowledged", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlerts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlerts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlerts = new TableInfo("alerts", _columnsAlerts, _foreignKeysAlerts, _indicesAlerts);
        final TableInfo _existingAlerts = TableInfo.read(db, "alerts");
        if (!_infoAlerts.equals(_existingAlerts)) {
          return new RoomOpenHelper.ValidationResult(false, "alerts(com.bp22intel.edgesentinel.data.local.entity.AlertEntity).\n"
                  + " Expected:\n" + _infoAlerts + "\n"
                  + " Found:\n" + _existingAlerts);
        }
        final HashMap<String, TableInfo.Column> _columnsScans = new HashMap<String, TableInfo.Column>(5);
        _columnsScans.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScans.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScans.put("cell_count", new TableInfo.Column("cell_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScans.put("threat_level", new TableInfo.Column("threat_level", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScans.put("duration_ms", new TableInfo.Column("duration_ms", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScans = new TableInfo("scans", _columnsScans, _foreignKeysScans, _indicesScans);
        final TableInfo _existingScans = TableInfo.read(db, "scans");
        if (!_infoScans.equals(_existingScans)) {
          return new RoomOpenHelper.ValidationResult(false, "scans(com.bp22intel.edgesentinel.data.local.entity.ScanEntity).\n"
                  + " Expected:\n" + _infoScans + "\n"
                  + " Found:\n" + _existingScans);
        }
        final HashMap<String, TableInfo.Column> _columnsBleDevices = new HashMap<String, TableInfo.Column>(11);
        _columnsBleDevices.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("mac_address", new TableInfo.Column("mac_address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("advertising_data_hash", new TableInfo.Column("advertising_data_hash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("manufacturer_id", new TableInfo.Column("manufacturer_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("device_name", new TableInfo.Column("device_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("first_seen", new TableInfo.Column("first_seen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("last_seen", new TableInfo.Column("last_seen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("location_clusters", new TableInfo.Column("location_clusters", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("seen_count", new TableInfo.Column("seen_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("is_tracker_type", new TableInfo.Column("is_tracker_type", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBleDevices.put("tracker_protocol", new TableInfo.Column("tracker_protocol", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBleDevices = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBleDevices = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBleDevices = new TableInfo("ble_devices", _columnsBleDevices, _foreignKeysBleDevices, _indicesBleDevices);
        final TableInfo _existingBleDevices = TableInfo.read(db, "ble_devices");
        if (!_infoBleDevices.equals(_existingBleDevices)) {
          return new RoomOpenHelper.ValidationResult(false, "ble_devices(com.bp22intel.edgesentinel.data.local.entity.BleDeviceEntity).\n"
                  + " Expected:\n" + _infoBleDevices + "\n"
                  + " Found:\n" + _existingBleDevices);
        }
        final HashMap<String, TableInfo.Column> _columnsBaselines = new HashMap<String, TableInfo.Column>(16);
        _columnsBaselines.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("radius", new TableInfo.Column("radius", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("label", new TableInfo.Column("label", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("cell_towers_json", new TableInfo.Column("cell_towers_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("wifi_aps_json", new TableInfo.Column("wifi_aps_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("ble_count_min", new TableInfo.Column("ble_count_min", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("ble_count_max", new TableInfo.Column("ble_count_max", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("network_type_dist_json", new TableInfo.Column("network_type_dist_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("observation_count", new TableInfo.Column("observation_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("confidence", new TableInfo.Column("confidence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("day_profile_json", new TableInfo.Column("day_profile_json", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselines.put("night_profile_json", new TableInfo.Column("night_profile_json", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBaselines = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBaselines = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBaselines = new TableInfo("baselines", _columnsBaselines, _foreignKeysBaselines, _indicesBaselines);
        final TableInfo _existingBaselines = TableInfo.read(db, "baselines");
        if (!_infoBaselines.equals(_existingBaselines)) {
          return new RoomOpenHelper.ValidationResult(false, "baselines(com.bp22intel.edgesentinel.data.local.entity.BaselineEntity).\n"
                  + " Expected:\n" + _infoBaselines + "\n"
                  + " Found:\n" + _existingBaselines);
        }
        final HashMap<String, TableInfo.Column> _columnsKnownTowers = new HashMap<String, TableInfo.Column>(11);
        _columnsKnownTowers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("mcc", new TableInfo.Column("mcc", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("mnc", new TableInfo.Column("mnc", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("lac", new TableInfo.Column("lac", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("cid", new TableInfo.Column("cid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("range", new TableInfo.Column("range", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("radio", new TableInfo.Column("radio", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsKnownTowers.put("updated", new TableInfo.Column("updated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysKnownTowers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesKnownTowers = new HashSet<TableInfo.Index>(1);
        _indicesKnownTowers.add(new TableInfo.Index("index_known_towers_mcc_mnc_lac_cid", true, Arrays.asList("mcc", "mnc", "lac", "cid"), Arrays.asList("ASC", "ASC", "ASC", "ASC")));
        final TableInfo _infoKnownTowers = new TableInfo("known_towers", _columnsKnownTowers, _foreignKeysKnownTowers, _indicesKnownTowers);
        final TableInfo _existingKnownTowers = TableInfo.read(db, "known_towers");
        if (!_infoKnownTowers.equals(_existingKnownTowers)) {
          return new RoomOpenHelper.ValidationResult(false, "known_towers(com.bp22intel.edgesentinel.data.local.entity.KnownTowerEntity).\n"
                  + " Expected:\n" + _infoKnownTowers + "\n"
                  + " Found:\n" + _existingKnownTowers);
        }
        final HashMap<String, TableInfo.Column> _columnsTrustedNetworks = new HashMap<String, TableInfo.Column>(5);
        _columnsTrustedNetworks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedNetworks.put("bssid", new TableInfo.Column("bssid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedNetworks.put("ssid", new TableInfo.Column("ssid", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedNetworks.put("label", new TableInfo.Column("label", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrustedNetworks.put("added_at", new TableInfo.Column("added_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTrustedNetworks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTrustedNetworks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTrustedNetworks = new TableInfo("trusted_networks", _columnsTrustedNetworks, _foreignKeysTrustedNetworks, _indicesTrustedNetworks);
        final TableInfo _existingTrustedNetworks = TableInfo.read(db, "trusted_networks");
        if (!_infoTrustedNetworks.equals(_existingTrustedNetworks)) {
          return new RoomOpenHelper.ValidationResult(false, "trusted_networks(com.bp22intel.edgesentinel.data.local.entity.TrustedNetworkEntity).\n"
                  + " Expected:\n" + _infoTrustedNetworks + "\n"
                  + " Found:\n" + _existingTrustedNetworks);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "fb4701e79f0ee0f938549e63cd8b7850", "290bfa98a7cf6a73cabd9692872a8133");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cells","alerts","scans","ble_devices","baselines","known_towers","trusted_networks");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cells`");
      _db.execSQL("DELETE FROM `alerts`");
      _db.execSQL("DELETE FROM `scans`");
      _db.execSQL("DELETE FROM `ble_devices`");
      _db.execSQL("DELETE FROM `baselines`");
      _db.execSQL("DELETE FROM `known_towers`");
      _db.execSQL("DELETE FROM `trusted_networks`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CellDao.class, CellDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertDao.class, AlertDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScanDao.class, ScanDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BleDeviceDao.class, BleDeviceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BaselineDao.class, BaselineDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(KnownTowerDao.class, KnownTowerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TrustedNetworkDao.class, TrustedNetworkDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CellDao cellDao() {
    if (_cellDao != null) {
      return _cellDao;
    } else {
      synchronized(this) {
        if(_cellDao == null) {
          _cellDao = new CellDao_Impl(this);
        }
        return _cellDao;
      }
    }
  }

  @Override
  public AlertDao alertDao() {
    if (_alertDao != null) {
      return _alertDao;
    } else {
      synchronized(this) {
        if(_alertDao == null) {
          _alertDao = new AlertDao_Impl(this);
        }
        return _alertDao;
      }
    }
  }

  @Override
  public ScanDao scanDao() {
    if (_scanDao != null) {
      return _scanDao;
    } else {
      synchronized(this) {
        if(_scanDao == null) {
          _scanDao = new ScanDao_Impl(this);
        }
        return _scanDao;
      }
    }
  }

  @Override
  public BleDeviceDao bleDeviceDao() {
    if (_bleDeviceDao != null) {
      return _bleDeviceDao;
    } else {
      synchronized(this) {
        if(_bleDeviceDao == null) {
          _bleDeviceDao = new BleDeviceDao_Impl(this);
        }
        return _bleDeviceDao;
      }
    }
  }

  @Override
  public BaselineDao baselineDao() {
    if (_baselineDao != null) {
      return _baselineDao;
    } else {
      synchronized(this) {
        if(_baselineDao == null) {
          _baselineDao = new BaselineDao_Impl(this);
        }
        return _baselineDao;
      }
    }
  }

  @Override
  public KnownTowerDao knownTowerDao() {
    if (_knownTowerDao != null) {
      return _knownTowerDao;
    } else {
      synchronized(this) {
        if(_knownTowerDao == null) {
          _knownTowerDao = new KnownTowerDao_Impl(this);
        }
        return _knownTowerDao;
      }
    }
  }

  @Override
  public TrustedNetworkDao trustedNetworkDao() {
    if (_trustedNetworkDao != null) {
      return _trustedNetworkDao;
    } else {
      synchronized(this) {
        if(_trustedNetworkDao == null) {
          _trustedNetworkDao = new TrustedNetworkDao_Impl(this);
        }
        return _trustedNetworkDao;
      }
    }
  }
}
