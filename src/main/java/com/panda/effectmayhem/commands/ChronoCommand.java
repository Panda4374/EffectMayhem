package com.panda.effectmayhem.commands;

import com.panda.effectmayhem.Main;
import com.panda.effectmayhem.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChronoCommand implements CommandExecutor {
    private final Main main;

    public ChronoCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "check" -> {
                long chrono = main.getChronoManager().getChrono(player.getUniqueId());
                player.sendMessage(Utils.addHeader(Utils.translateColorCode("&eYou have &b" + chrono + " Chrono")));
                return true;
            }

            case "give", "take" -> {
                if (!player.hasPermission("effectmayhem.admin")) {
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&cYou do not have permission!")));
                    return true;
                }

                if (args.length != 3) {
                    sendUsage(player);
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (!main.getChronoManager().hasPlayed(target.getUniqueId())) {
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&cThat player has never played!")));
                    return true;
                }

                long amount;
                try {
                    amount = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&cAmount must be a number!")));
                    return true;
                }

                long current = main.getChronoManager().getChrono(target.getUniqueId());

                if (args[0].equalsIgnoreCase("give")) {
                    main.getChronoManager().setChrono(target.getUniqueId(), current + amount);
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&eGave &b" + amount + " Chrono &eto &f" + target.getName())));
                } else {
                    main.getChronoManager().setChrono(target.getUniqueId(), current - amount);
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&eTook &b" + amount + " Chrono &efrom &f" + target.getName())));
                }

                return true;
            }

            default -> {
                sendUsage(player);
                return true;
            }
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage(Utils.addHeader("&eUsage:"));
        player.sendMessage(Utils.translateColorCode("&7/chrono check"));
        player.sendMessage(Utils.translateColorCode("&7/chrono give <player> <amount>"));
        player.sendMessage(Utils.translateColorCode("&7/chrono take <player> <amount>"));
    }
}