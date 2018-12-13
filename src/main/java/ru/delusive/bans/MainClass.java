package ru.delusive.bans;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ru.delusive.bans.commands.BanCommand;
import ru.delusive.bans.commands.CheckbanCommand;
import ru.delusive.bans.commands.TempbanCommand;
import ru.delusive.bans.commands.UnbanCommand;

@Plugin(id = "dbans", name = "DBans", version="1.0", authors="Delusive-")
public class MainClass {
	@Inject
	Logger logger;
	@Inject
	@DefaultConfig(sharedRoot = false)
	public ConfigurationLoader <CommentedConfigurationNode> loader;
	public static ConfigUtils config;
	public static MySQLWorker mysql;
	public static BaseUtils utils;
	
	@Listener
	public void onPluginEnable(GameStartedServerEvent e) throws IOException {
		logger.info("Ok, I'll try to load myself...");
		config = new ConfigUtils(loader);
		config.init();
		mysql = new MySQLWorker();
		utils = new BaseUtils();
		if(!config.IS_PLUGIN_ENABLED) {
			logger.info("isEnabled is set to false in config file. Disabling...:(");
		}
		registerCommands();
		if(config.IS_PLUGIN_ENABLED)
			if(mysql.createTable()) {
				logger.info("I did it! I loaded!");
			} else {
				logger.warn("MySQL Error, couldn't create table. Please check the stack trace.");
				config.IS_PLUGIN_ENABLED = false;
			}
	}
	
	@Listener
	public void onPlayerJoined(ClientConnectionEvent.Login e) {
		if(!config.IS_PLUGIN_ENABLED) return; 
		User user = e.getTargetUser();
		if(utils.isPlayerBanned(user)) {
			e.setCancelled(true);
			HashMap<String, String> map = utils.getBanDetails(user);
			Long expires = Long.valueOf(map.get(config.BANS_EXPIRESCOLUMN));
			boolean isPermanent = (expires == 0) ? true : false;
			String msg = String.format("You were %s banned by %s. Reason: %s.", 
					isPermanent ? "permanently" : "temporarily", 
					map.get(config.BANS_ADMINCOLUMN), map.get(config.BANS_REASONCOLUMN));
			Text tempMsg = Text.of("");
			if(!isPermanent) tempMsg = Text.builder(String.format("\nYou will be unbanned at %s", utils.getFormattedDate(expires))).color(TextColors.GOLD).build();
			e.setMessage(Text.builder(msg).color(TextColors.RED).append(tempMsg));
		}
	}
	
	@Listener
	public void onReload(GameReloadEvent e) throws IOException {
		logger.info("Reloading config...");
		config.init();
		mysql = new MySQLWorker();
		utils = new BaseUtils();
		if(!config.IS_PLUGIN_ENABLED) {
			logger.info("I'm disabled!");
			return;
		}
		config.IS_PLUGIN_ENABLED = false;
		if(mysql.createTable()) {
			logger.info("I did it! I loaded!");
		}
		config.IS_PLUGIN_ENABLED = true;
	}
	
	public void registerCommands() {
		CommandManager cmdManager = Sponge.getCommandManager();
		CommandSpec ban = CommandSpec.builder()
				.description(Text.of("Bans a player"))
				.permission("DBans.ban")
				.executor(new BanCommand())
				.arguments(GenericArguments.string(Text.of("PlayerName")), 
						GenericArguments.remainingJoinedStrings(Text.of("reason")))
				.build();
		cmdManager.register(this, ban, "ban");
		
		CommandSpec unban = CommandSpec.builder()
				.description(Text.of("Unbans a player"))
				.permission("DBans.unban")
				.executor(new UnbanCommand())
				.arguments(GenericArguments.string(Text.of("PlayerName")))
				.build();
		cmdManager.register(this, unban, "unban");
		
		CommandSpec checkban = CommandSpec.builder()
				.description(Text.of("Gets information about player's ban."))
				.permission("DBans.checkban")
				.executor(new CheckbanCommand())
				.arguments(GenericArguments.string(Text.of("PlayerName")))
				.build();
		cmdManager.register(this, checkban, "checkban");
		
		CommandSpec tempban = CommandSpec.builder()
				.description(Text.of("Temporarily bans a player"))
				.permission("DBans.tempban")
				.executor(new TempbanCommand())
				.arguments(GenericArguments.string(Text.of("PlayerName")),
						GenericArguments.integer(Text.of("Time")),
						GenericArguments.string(Text.of("TimeUnitsName")),
						GenericArguments.remainingJoinedStrings(Text.of("reason")))
				.build();
		cmdManager.register(this, tempban, "tempban");
	}
}
