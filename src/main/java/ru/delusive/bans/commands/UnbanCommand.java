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

public class UnbanCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Task.builder().async().execute(() -> {
			String playerName = args.getOne("PlayerName").get().toString();
			UserStorageService storage = Sponge.getServiceManager().provide(UserStorageService.class).get();
			User user;
			if(!storage.get(playerName).isPresent()) {
				src.sendMessage(Text.builder("Player not found!").color(TextColors.RED).build());
				//return CommandResult.success();
			} else {
				user = storage.get(playerName).get();
				if(!MainClass.utils.isPlayerBanned(user)) {
					src.sendMessage(Text.builder("Player isn't banned!").color(TextColors.RED).build());
				} else {
					MainClass.utils.unbanPlayer(user);
					src.sendMessage(Text.builder(String.format("Player %s successfully unbanned!", user.getName())).color(TextColors.GREEN).build());
				}
			}
		}).submit(MainClass.plugin);
		return CommandResult.success();
	}

}
