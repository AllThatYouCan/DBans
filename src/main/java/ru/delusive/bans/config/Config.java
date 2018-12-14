package ru.delusive.bans.config;

import java.io.IOException;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Config {

	@Setting(value = "global")
	private GlobalParams globalParams = new GlobalParams();
    @Setting(value = "database")
    private DatabaseParams dbParams = new DatabaseParams();

	public GlobalParams getGlobalParams(){
	    return globalParams;
    }

    public DatabaseParams getDbParams(){
	    return dbParams;
    }

	@ConfigSerializable
	public static class GlobalParams {
		@Setting(value = "getIsEnabled", comment = "Should it work????")
		private boolean isEnabled = false;

		public boolean getIsEnabled() {
			return isEnabled;
		}
		public void setIsEnabled(boolean isEnabled){
            this.isEnabled = isEnabled;
        }
	}

	@ConfigSerializable
	public static class DatabaseParams {
		@Setting(value = "aliasName", comment = "Sql aliase name (from global.conf)")
		private String alias = "MyAliase";
		@Setting("tables")
		private TableParams bansTable = new TableParams();
		public String getAlias() {
			return alias;
		}
		public TableParams getBansTable() {
			return bansTable;
		}
	}

	@ConfigSerializable
	public static class TableParams {
		@Setting("bans")
		private BansParams bansVars = new BansParams();

		public BansParams getBansVars() {
			return bansVars;
		}
	}

	@ConfigSerializable
	public static class BansParams {
		@Setting(value = "bannerNameColumn", comment = "Name of column that contains name of the one who banned the player")
		private String admin = "banner";
		@Setting(value = "expiresColumn", comment = "Name of column that contains expiration time of player's ban")
		private String expires = "expires";
		@Setting(value = "reasonColumn", comment = "Name of column that contains reason of player's ban")
		private String reason = "reason";
		@Setting(value = "tablename", comment = "Table name")
		private String tableName = "bans";
		@Setting(value = "timeColumn", comment = " Name of column that contains player's ban time")
		private String bantime = "time";
		@Setting(value = "usernameColumn", comment = "Name of column that contains player names")
		private String username = "name";
		@Setting(value = "uuidColumn", comment = "UUID column name (type null if it should be disabled)")
		private String uuid = "null";
		
		public String getAdmin() {
			return admin;
		}
		public String getExpires() {
			return expires;
		}
		public String getReason() {
			return reason;
		}
		public String getTableName() {
			return tableName;
		}
		public String getBantime() {
			return bantime;
		}
		public String getUsername() {
			return username;
		}
		public String getUuid() {
			return uuid;
		}
		
	}
}
