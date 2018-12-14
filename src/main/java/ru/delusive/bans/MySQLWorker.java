package ru.delusive.bans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import ru.delusive.bans.config.Config;
import ru.delusive.bans.config.ConfigManager;

public class MySQLWorker {
	private SqlService sql;
	private ConfigManager cfgManager;
	private Config.BansParams bansParams;

	MySQLWorker(){
		this.cfgManager = MainClass.cfgManager;
		bansParams = cfgManager.getBanFields();
	}
	
	public DataSource getDataSource() throws SQLException {
		sql = Sponge.getServiceManager().provide(SqlService.class).get();
		String alias = sql.getConnectionUrlFromAlias(cfgManager.getAliasName()).orElseThrow(() -> new IllegalArgumentException("JDBC alias not found"));
		return sql.getDataSource(alias);
	}
	
	public boolean executeStatement(String stmt) {
		try(Connection con = getDataSource().getConnection()){
			con.prepareStatement(stmt).executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public ResultSet executeStatement(String stmt, String ...args) {
		try (Connection con = getDataSource().getConnection()){
			
			PreparedStatement st = con.prepareStatement(stmt);
			for(int i = 1; i <= args.length; i++) {
				st.setString(i, args[i-1]);
			}
			return st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet executeStatement(String stmt, CustomData data) {
		try (Connection con = getDataSource().getConnection()){
			PreparedStatement ps = con.prepareStatement(stmt);
			for(int i = 0; i < data.size; i++) {
				Entry<String, Object> temp = data.get(i);
				switch(temp.getKey()) {
					case "int":
						ps.setInt(i+1, (int)temp.getValue());
						break;
					case "str":
						ps.setString(i+1, (String)temp.getValue());
						break;
					case "bool":
						ps.setBoolean(i+1, (boolean)temp.getValue());
						break;
					case "long":
						ps.setLong(i+1, (long)temp.getValue());
						break;
				}
			}
			return ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean executeUpdateStatement(String stmt, CustomData data) {
		try (Connection con = getDataSource().getConnection()){
			PreparedStatement ps = con.prepareStatement(stmt);
			for(int i = 0; i < data.size; i++) {
				Entry<String, Object> temp = data.get(i);
				switch(temp.getKey()) {
					case "int":
						ps.setInt(i+1, (int)temp.getValue());
						break;
					case "str":
						ps.setString(i+1, (String)temp.getValue());
						break;
					case "bool":
						ps.setBoolean(i+1, (boolean)temp.getValue());
						break;
					case "long":
						ps.setLong(i+1, (long)temp.getValue());
						break;
				}
			}
			return ps.executeUpdate() >= 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	} 
	
	public boolean executeUpdateStatement(String stmt, String arg) {
		try (Connection con = getDataSource().getConnection()){
			PreparedStatement ps = con.prepareStatement(stmt);
			ps.setString(1, arg);
			return ps.executeUpdate() >= 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean createTable() {
		boolean isUUIDEn = MainClass.utils.isUUIDEnabled();
		String stmt = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` (" + 
				(isUUIDEn ? "`%s` VARCHAR(50) NOT NULL," : "%s") + 
				"	`%s` VARCHAR(50) NOT NULL," + 
				"	`%s` BIGINT(20) NOT NULL," + 
				"	`%s` BIGINT(20) NOT NULL," + 
				"	`%s` VARCHAR(50) NOT NULL," + 
				"	`%s` VARCHAR(50) NOT NULL" +  
				") " + 
				"COLLATE='utf8_general_ci'",
				bansParams.getTableName(), isUUIDEn ? bansParams.getUuid() : " ", bansParams.getUsername(),
				bansParams.getBantime(), bansParams.getExpires(), bansParams.getAdmin(), bansParams.getReason());
		return this.executeStatement(stmt);
	}
	
}
