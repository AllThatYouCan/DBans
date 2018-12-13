package ru.delusive.bans;

import java.io.IOException;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigUtils {
	
	ConfigurationLoader <CommentedConfigurationNode> loader;
	public String BANS_TABLENAME;
	public String BANS_USERCOLUMN;
	//public String BANS_IDCOLUMN;
	public String BANS_UUIDCOLUMN;
	public String BANS_TIMECOLUMN;
	public String BANS_EXPIRESCOLUMN;
	public String BANS_ADMINCOLUMN;
	public String BANS_REASONCOLUMN;
	public boolean IS_PLUGIN_ENABLED;
	public String JDBC_ALIAS;
	public String DB_PASS;
	public String DB_USERNAME;
	
	public ConfigUtils(ConfigurationLoader <CommentedConfigurationNode> loader){
		this.loader = loader;
	}
	
	public void init() throws IOException {
		CommentedConfigurationNode conf = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
		
		conf.getNode("global", "isEnabled").setComment("Should it work? (true/false)");
		IS_PLUGIN_ENABLED = conf.getNode("global", "isEnabled").getBoolean(false);
		
		conf.getNode("database", "aliasName").setComment("Sql aliase name (setup it in global.conf)");
		JDBC_ALIAS = conf.getNode("database", "aliasName").getString("DBans");

		conf.getNode("database", "tables", "bans", "tablename").setComment("Table name");
		BANS_TABLENAME = conf.getNode("database", "tables", "bans", "tablename").getString("bans");
			
		//conf.getNode("database", "tables", "bans", "idColumn").setComment("ID column name");
		//BANS_IDCOLUMN = conf.getNode("database", "tables", "bans", "idColumn").getString("id");
			
		conf.getNode("database", "tables", "bans", "uuidColumn").setComment("UUID column name (type null if it should be disabled)");
		BANS_UUIDCOLUMN = conf.getNode("database", "tables", "bans", "uuidColumn").getString("null");
			
		conf.getNode("database", "tables", "bans", "usernameColumn").setComment("Name of column that contains player names");
		BANS_USERCOLUMN = conf.getNode("database", "tables", "bans", "usernameColumn").getString("name");
			
		conf.getNode("database", "tables", "bans", "timeColumn").setComment("Name of column that contains player's ban time");
		BANS_TIMECOLUMN = conf.getNode("database", "tables", "bans", "timeColumn").getString("time");
			
		conf.getNode("database", "tables", "bans", "expiresColumn").setComment("Name of column that contains expiration time of player's ban");
		BANS_EXPIRESCOLUMN = conf.getNode("database", "tables", "bans", "expiresColumn").getString("expires");
			
		conf.getNode("database", "tables", "bans", "bannerNameColumn").setComment("Name of column that contains name of the one who banned the player");
		BANS_ADMINCOLUMN = conf.getNode("database", "tables", "bans", "bannerNameColumn").getString("banner");
		
		conf.getNode("database", "tables", "bans", "reasonColumn").setComment("REASON column name");
		BANS_REASONCOLUMN = conf.getNode("database", "tables", "bans", "reasonColumn").getString("reason");
		
		loader.save(conf);
	}
}
