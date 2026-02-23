package com.panda.effectmayhem;

import com.panda.effectmayhem.commands.ChronoCommand;
import com.panda.effectmayhem.commands.ChronoCompleter;
import com.panda.effectmayhem.managers.ChronoManager;
import com.panda.effectmayhem.managers.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin {
    private File chrono;

    private ChronoManager chronoManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        saveDefaultConfig();

        chrono = new File(getDataFolder(), "chrono.yml");
        if (!chrono.exists()) {
            try {
                chrono.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        getServer().getPluginManager().registerEvents(new MayhemListener(this), this);

        getCommand("chrono").setExecutor(new ChronoCommand(this));
        getCommand("chrono").setTabCompleter(new ChronoCompleter());

        chronoManager = new ChronoManager(this);
        playerManager = new PlayerManager(this);
    }

    @Override
    public void onDisable() {
        chronoManager.saveAll();
        playerManager.cleanup();
    }

    public ChronoManager getChronoManager() {
        return chronoManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public File getChronoFile() {
        return chrono;
    }
}