package ru.delusive.bans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import ru.delusive.bans.config.Config;
import ru.delusive.bans.config.ConfigManager;

public class BaseUtils {
	
	private MySQLWorker mysql;
	private ConfigManager cfgManager;
	private Config.BansParams bansParams;
	
	public BaseUtils() {
		cfgManager = MainClass.cfgManager;
		mysql = MainClass.mysql;
		bansParams = cfgManager.getBanFields();
	}
	
	public boolean isPlayerBanned(User user) {
		try {
			String stmt = String.format("SELECT `%s` FROM `%s` WHERE `%s` = ?;",
					bansParams.getExpires(), bansParams.getTableName(), isUUIDEnabled() ? bansParams.getUuid() : bansParams.getUsername());
			ResultSet rs = mysql.executeStatement(stmt, isUUIDEnabled() ? user.getUniqueId().toString() : user.getName());
			if(rs.next()) {
				long banTime = rs.getLong(bansParams.getExpires());
				if(banTime > System.currentTimeMillis() || banTime == 0) {
					
					return true;
				}
				stmt = String.format("DELETE FROM `%s` WHERE `%s` = ?", 
						bansParams.getTableName(), isUUIDEnabled() ? bansParams.getUuid() : bansParams.getUsername());
				mysql.executeUpdateStatement(stmt, isUUIDEnabled() ? user.getUniqueId().toString() : user.getName());
			}
			return false;
		} catch(SQLException e) {e.printStackTrace();}
		return false; 
	}
	
	public boolean banPlayer(User user, CommandSource src, long expires, String reason) {
		String stmt;
		if(isUUIDEnabled()) {
			stmt = String.format("INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?, ?);", 
					bansParams.getTableName(), bansParams.getUuid(), bansParams.getUsername(),
					bansParams.getBantime(), bansParams.getExpires(), bansParams.getAdmin(), bansParams.getReason());
		} else {
			stmt = String.format("INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?);", 
					bansParams.getTableName(), bansParams.getUsername(),
					bansParams.getBantime(), bansParams.getExpires(), bansParams.getAdmin(), bansParams.getReason());
		}
		String bannerName = src.getName();
		CustomData data = new CustomData();
		if(isUUIDEnabled()) data.add(user.getUniqueId().toString());
		data.add(user.getName());
		data.add(System.currentTimeMillis());
		data.add(expires);
		data.add(bannerName);
		data.add(reason);
		
		return mysql.executeUpdateStatement(stmt, data);
	}
	
	public HashMap<String, String> getBanDetails(User user) {
		String stmt = String.format("SELECT `%s`, `%s`, `%s`, `%s` FROM `%s` WHERE `%s` = ? LIMIT 1", 
				bansParams.getBantime(), bansParams.getExpires(), bansParams.getAdmin(), 
				bansParams.getReason(), bansParams.getTableName(), isUUIDEnabled() ? bansParams.getUuid() : bansParams.getUsername());
		ResultSet res = mysql.executeStatement(stmt, isUUIDEnabled() ? user.getUniqueId().toString() : user.getName());
		try {
			res.next();
			HashMap <String, String> map = new HashMap<>();
			map.put(bansParams.getBantime(), String.valueOf(res.getLong(1)));
			map.put(bansParams.getExpires(), String.valueOf(res.getLong(2)));
			map.put(bansParams.getAdmin(), res.getString(3));
			map.put(bansParams.getReason(), res.getString(4));
			return map;
		} catch (SQLException e) {e.printStackTrace(); return null;}
		
	}
	
	public String getFormattedDate(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
		//sdf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
		return sdf.format(date);
	}
	
	public boolean unbanPlayer(User user) {
		String stmt = String.format("DELETE FROM `%s` WHERE `%s` = ?", bansParams.getTableName(), isUUIDEnabled() ? bansParams.getUuid() : bansParams.getUsername());
		return mysql.executeUpdateStatement(stmt, isUUIDEnabled()?user.getUniqueId().toString():user.getName());
	}
	
	public boolean isUUIDEnabled() {
		return !bansParams.getUuid().equals("null");
	}
	
	
}
