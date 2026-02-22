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

    public MayhemListener(Main main) {
        this.main = main;
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
            int chrono = main.getChronoManager().getChrono(killer.getUniqueId());
            main.getChronoManager().setChrono(killer.getUniqueId(), chrono + 1);
            killer.sendMessage(Utils.PLUGIN_NAME.append(Component.text(Utils.translateColorCode(" &eYou received a chrono! You now have &b" + (chrono + 1) + " Chrono"))));
        }

        int chrono = main.getChronoManager().getChrono(player.getUniqueId());
        main.getChronoManager().setChrono(player.getUniqueId(), chrono - 1);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        main.getPlayerManager().addPlayer(player);
        player.sendMessage(Utils.PLUGIN_NAME.append(Component.text(Utils.translateColorCode(" &eYou lost a chrono! You now have &b" +
                main.getChronoManager().getChrono(player.getUniqueId()) + " Chrono"))));
    }
}
