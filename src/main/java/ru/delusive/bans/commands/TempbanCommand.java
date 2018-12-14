package ru.delusive.bans.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import ru.delusive.bans.MainClass;

public class TempbanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Task.builder().async().execute(() -> {
			String playerName = args.getOne("PlayerName").get().toString();
			String reason = args.getOne("reason").get().toString();
			long time = System.currentTimeMillis() + getSecs(args.getOne("TimeUnitsName").get().toString()) * (int)args.getOne("Time").get() * 1000L;
			UserStorageService storage = Sponge.getServiceManager().provide(UserStorageService.class).get();
			if(!storage.get(playerName).isPresent()) {
				src.sendMessage(Text.builder("Player not found!").color(TextColors.RED).build());
				//return CommandResult.success();
			} else {
				User user = storage.get(playerName).get();
				if(MainClass.utils.isPlayerBanned(user)) {
					src.sendMessage(Text.builder("Player already banned!").color(TextColors.RED).build());
				} else {
					MainClass.utils.banPlayer(user, src, time, reason);
					String formattedTime = MainClass.utils.getFormattedDate(time);
					src.sendMessage(Text.builder(String.format("Player %s has been banned until %s", user.getName(), formattedTime)).color(TextColors.GREEN).build());
					if(user.isOnline()) {
						user.getPlayer().get().kick(Text.join(
							Text.builder(String.format("You were temporarily banned by %s. Reason: %s", src.getName(), reason)).color(TextColors.RED).build(),
							Text.builder("\nYou will be unbanned at "+formattedTime).color(TextColors.GOLD).build()
						));
					}
				}
			}
		}).submit(MainClass.plugin);
		
		return CommandResult.success();
	}
	
	public int getSecs(String timeUnitsName) {
		switch(timeUnitsName) {
			case "s":
			case "sec":
			case "secs":
			case "second":
			case "seconds":
				return 1;
			case "m":
			case "min":
			case "mins":
			case "minutes":
				return 60;
			case "h":
			case "hour":
			case "hours":
				return 60*60;
			case "d":
			case "day":
			case "days":
				return 60*60*24;
			case "w":
			case "week":
			case "weeks":
				return 60*60*24*7;
			case "month":
			case "months":
				return 60*60*24*30;
			case "y": 
			case "year":
			case "years": //O_o
				return 60*60*24*30*12+5;
		}
		return 0;
	}
}
