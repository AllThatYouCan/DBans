package ru.delusive.bans.commands;

import java.util.HashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import ru.delusive.bans.MainClass;

public class CheckbanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!MainClass.config.IS_PLUGIN_ENABLED) {
			src.sendMessage(Text.builder("Plugin is disabled!").color(TextColors.RED).build());
			return CommandResult.success();
		}
		new Thread(() -> {
			String playerName = args.getOne("PlayerName").get().toString();
			UserStorageService storage = Sponge.getServiceManager().provide(UserStorageService.class).get();
			if(!storage.get(playerName).isPresent()) {
				src.sendMessage(Text.builder("Player not found!").color(TextColors.RED).build());
				//return CommandResult.success();
			} else {
				User user = storage.get(playerName).get();
				if(MainClass.utils.isPlayerBanned(user)) {
					HashMap<String, String> map = MainClass.utils.getBanDetails(user);
					Long unbanTime = Long.valueOf(map.get(MainClass.config.BANS_EXPIRESCOLUMN));
					Long banTime = Long.valueOf(map.get(MainClass.config.BANS_TIMECOLUMN));
					String reason = map.get(MainClass.config.BANS_REASONCOLUMN);
					String bannerName = map.get(MainClass.config.BANS_ADMINCOLUMN);
					Text.Builder b = Text.builder();
					
					b.append(Text.builder("=====================================").color(TextColors.AQUA).build());
					src.sendMessage(b.build());
					b.removeAll();
					
					b.append(Text.builder("Info about player ").color(TextColors.AQUA).build());
					b.append(Text.builder(user.getName()).color(TextColors.GOLD).build());
					src.sendMessage(b.build());
					b.removeAll();
					
					b.append(Text.builder("He is banned ").color(TextColors.AQUA).build());
					b.append(Text.builder(unbanTime == 0 ? "permanently":"temporarily").color(TextColors.GOLD).build());
					b.append(Text.builder(" by ").color(TextColors.AQUA).build());
					b.append(Text.builder(bannerName).color(TextColors.GOLD).build());
					src.sendMessage(b.build());
					b.removeAll();
					
					if(unbanTime != 0) {
						b.append(Text.builder("Will be unbanned at ").color(TextColors.AQUA).build());
						b.append(Text.builder(MainClass.utils.getFormattedDate(unbanTime)).color(TextColors.GOLD).build());
						src.sendMessage(b.build());
						b.removeAll();
					}
					
					b.append(Text.builder("Ban reason - ").color(TextColors.AQUA).build());
					b.append(Text.builder(reason).color(TextColors.GOLD).build());
					src.sendMessage(b.build());
					b.removeAll();
					
					b.append(Text.builder("Was banned at ").color(TextColors.AQUA).build());
					b.append(Text.builder(MainClass.utils.getFormattedDate(banTime)).color(TextColors.GOLD).build());
					src.sendMessage(b.build());
					b.removeAll();
					
	// 				xDDD
	//				b.append(Text.builder("===================================\n").color(TextColors.AQUA).build());
	//				b.append(Text.builder("Info about player ").color(TextColors.AQUA).append(
	//						Text.builder(user.getName() + "\n").color(TextColors.GOLD).build()).build());
	//				b.append(Text.builder("He is banned ").color(TextColors.AQUA).append(
	//						Text.builder(unbanTime == 0 ? "permanently":"temporarily").color(TextColors.GOLD).build(),
	//						Text.builder(" by ").color(TextColors.GREEN).build(),
	//						Text.builder(bannerName+"\n").color(TextColors.GOLD).build()).build());
	//				if(unbanTime != 0) {
	//					b.append(Text.builder("Will be unbanned at ").color(TextColors.AQUA).append(
	//							Text.builder(MainClass.utils.getFormattedDate(unbanTime)+"\n").color(TextColors.GOLD).build()).build());
	//				}
	//				b.append(Text.builder("Ban reason - ").color(TextColors.AQUA).append(
	//						Text.builder(reason).color(TextColors.GOLD).build()).build());
	//				src.sendMessage(b.build());
				} else {
					src.sendMessage(Text.builder("Player isn't banned.").color(TextColors.GOLD).build());
				}
			}
		}).start();
		return CommandResult.success();
	}

}
