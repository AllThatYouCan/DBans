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

public class BanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Task.builder().async().execute(()->{
			String playerName = args.getOne("PlayerName").get().toString();
			String reason = args.getOne("reason").get().toString();
			UserStorageService storage = Sponge.getServiceManager().provide(UserStorageService.class).get();
			
			if(!storage.get(playerName).isPresent()){
				src.sendMessage(Text.builder("Player not found!").color(TextColors.RED).build());
			} else {
				User user = storage.get(playerName).get();
				if(MainClass.utils.isPlayerBanned(user)) {
					src.sendMessage(Text.builder("Player already banned!").color(TextColors.RED).build());
				} else {
					MainClass.utils.banPlayer(user, src, 0, reason);
					if(user.isOnline()) {
						user.getPlayer().get().kick(Text.builder(String.format("You were permanently banned by %s. Reason: %s.", src.getName(), reason)).color(TextColors.RED).build());
					}
					src.sendMessage(Text.builder(String.format("Player %s has been banned successfully!", user.getName())).color(TextColors.GREEN).build());
				}
			}
		}).submit(MainClass.plugin);
		return CommandResult.success();
	}

}
