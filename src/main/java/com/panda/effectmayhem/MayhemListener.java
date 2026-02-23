package com.panda.effectmayhem;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MayhemListener implements Listener {
    private Main main;

    private int reward;
    private int penalty;

    public MayhemListener(Main main) {
        this.main = main;

        this.reward = main.getConfig().getInt("chrono.kill-reward");
        if (reward <= 0) {
            this.reward = 1;
            main.getLogger().warning("Kill reward must be greater than 0! Resetting to the default value of 1.");
            main.getConfig().set("chrono.kill-reward", this.reward);
        }

        this.penalty = main.getConfig().getInt("chrono.death-penalty");
        if (penalty <= 0) {
            this.penalty = 1;
            main.getLogger().warning("Death penalty must be greater than 0! Resetting to the default value of 1.");
            main.getConfig().set("chrono.death-penalty", this.penalty);
        }

        main.saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) main.getChronoManager().setChrono(player.getUniqueId(), 0);

        main.getPlayerManager().addPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        main.getPlayerManager().removePlayer(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        main.getPlayerManager().removePlayer(player);

        if (event.getDamageSource().getCausingEntity() instanceof Player) {
            Player killer = (Player) event.getDamageSource().getCausingEntity();
            long chrono = main.getChronoManager().getChrono(killer.getUniqueId()) + reward;
            main.getChronoManager().setChrono(killer.getUniqueId(), chrono);
            killer.sendMessage(Utils.PLUGIN_NAME.append(Component.text(Utils.translateColorCode(" &eYou received a chrono! You now have &b" + chrono + " Chrono"))));
        }

        long chrono = main.getChronoManager().getChrono(player.getUniqueId()) - penalty;
        main.getChronoManager().setChrono(player.getUniqueId(), chrono);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        main.getPlayerManager().addPlayer(player);
        player.sendMessage(Utils.PLUGIN_NAME.append(Component.text(Utils.translateColorCode(" &eYou lost a chrono! You now have &b" +
                main.getChronoManager().getChrono(player.getUniqueId()) + " Chrono"))));
    }
}
