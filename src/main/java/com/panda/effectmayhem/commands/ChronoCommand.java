package com.panda.effectmayhem.commands;

import com.panda.effectmayhem.Main;
import com.panda.effectmayhem.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.InputMismatchException;
import java.util.List;

public class ChronoCommand implements CommandExecutor {
    private Main main;

    public ChronoCommand(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<String> argList = List.of("give", "take", "check");
            if (!(args.length == 1 || args.length == 3) || !argList.contains(args[0])) {
                player.sendMessage(Utils.addHeader(Utils.translateColorCode(" &cInvalid usage! Use: ")));
                player.sendMessage(Utils.translateColorCode("&c/chrono give <player> <amount>"));
                player.sendMessage(Utils.translateColorCode("&c/chrono take <player> <amount>"));
                player.sendMessage(Utils.translateColorCode("&c/chrono check"));
                return false;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("check")) {
                player.sendMessage(Utils.addHeader(Utils.translateColorCode("&eYou have &b" + main.getChronoManager().getChrono(player.getUniqueId()) + " Chrono")));
            } else {
                if (!player.isOp()) {
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&cYou must be an OP to use this command!")));
                    return false;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (!main.getChronoManager().isPresent(target.getUniqueId())) {
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&cThis player has not played before!")));
                    return false;
                }

                try {
                    int amount = Integer.parseInt(args[2]);
                    int ch = main.getChronoManager().getChrono(player.getUniqueId());

                    if (args[0].equalsIgnoreCase("give")) {
                        player.sendMessage(Utils.addHeader(Utils.translateColorCode("&eGave &b" + amount + " Chrono &eto &f" + player.getName())));
                        amount += ch;
                    } else {
                        player.sendMessage(Utils.addHeader(Utils.translateColorCode("&eTaken &b" + amount + " Chrono &efrom &f" + player.getName())));
                        amount = ch - amount;
                    }
                    main.getChronoManager().setChrono(target.getUniqueId(), amount);
                } catch (NumberFormatException e) {
                    player.sendMessage(Utils.addHeader(Utils.translateColorCode("&cPlease enter an integer for the amount!")));
                }
            }
        } else {
            sender.sendMessage(Utils.translateColorCode("&c[!] Only players can run this command!"));
        }

        return false;
    }
}
