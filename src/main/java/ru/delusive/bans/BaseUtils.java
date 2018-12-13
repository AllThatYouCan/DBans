package ru.delusive.bans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;

public class BaseUtils {
	
	MySQLWorker mysql;// = MainClass.mysql;
	ConfigUtils cfg;
	
	public BaseUtils() {
		cfg = MainClass.config;
		mysql = MainClass.mysql;
	}
	
	public boolean isPlayerBanned(User user) {
		try {
			String stmt = String.format("SELECT `%s` FROM `%s` WHERE `%s` = ?;",
					cfg.BANS_EXPIRESCOLUMN, cfg.BANS_TABLENAME, isUUIDEnabled() ? cfg.BANS_UUIDCOLUMN : cfg.BANS_USERCOLUMN);
			ResultSet rs = mysql.executeStatement(stmt, isUUIDEnabled() ? user.getUniqueId().toString() : user.getName());
			if(rs.next()) {
				long banTime = rs.getLong(cfg.BANS_EXPIRESCOLUMN);
				if(banTime > System.currentTimeMillis() || banTime == 0) {
					
					return true;
				}
				stmt = String.format("DELETE FROM `%s` WHERE `%s` = ?", 
						cfg.BANS_TABLENAME, isUUIDEnabled() ? cfg.BANS_UUIDCOLUMN : cfg.BANS_USERCOLUMN);
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
					cfg.BANS_TABLENAME, cfg.BANS_UUIDCOLUMN, cfg.BANS_USERCOLUMN, 
					cfg.BANS_TIMECOLUMN, cfg.BANS_EXPIRESCOLUMN, cfg.BANS_ADMINCOLUMN, cfg.BANS_REASONCOLUMN);
		} else {
			stmt = String.format("INSERT INTO `%s` (`%s`, `%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?, ?);", 
					cfg.BANS_TABLENAME, cfg.BANS_USERCOLUMN, 
					cfg.BANS_TIMECOLUMN, cfg.BANS_EXPIRESCOLUMN, cfg.BANS_ADMINCOLUMN, cfg.BANS_REASONCOLUMN);
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
				cfg.BANS_TIMECOLUMN, cfg.BANS_EXPIRESCOLUMN, cfg.BANS_ADMINCOLUMN, 
				cfg.BANS_REASONCOLUMN, cfg.BANS_TABLENAME, isUUIDEnabled() ? cfg.BANS_UUIDCOLUMN : cfg.BANS_USERCOLUMN);
		ResultSet res = mysql.executeStatement(stmt, isUUIDEnabled() ? user.getUniqueId().toString() : user.getName());
		try {
			res.next();
			HashMap <String, String> map = new HashMap<>();
			map.put(cfg.BANS_TIMECOLUMN, String.valueOf(res.getLong(1)));
			map.put(cfg.BANS_EXPIRESCOLUMN, String.valueOf(res.getLong(2)));
			map.put(cfg.BANS_ADMINCOLUMN, res.getString(3));
			map.put(cfg.BANS_REASONCOLUMN, res.getString(4));
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
		String stmt = String.format("DELETE FROM `%s` WHERE `%s` = ?", cfg.BANS_TABLENAME, isUUIDEnabled() ? cfg.BANS_UUIDCOLUMN : cfg.BANS_USERCOLUMN);
		return mysql.executeUpdateStatement(stmt, isUUIDEnabled()?user.getUniqueId().toString():user.getName());
	}
	
	public boolean isUUIDEnabled() {
		return !cfg.BANS_UUIDCOLUMN.equals("null");
	}
	
	
}
