package com.panda.effectmayhem.managers;

import com.panda.effectmayhem.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.UUID;

public class ChronoManager {
    private Main main;
    private YamlConfiguration config;

    public ChronoManager(Main main) {
        this.main = main;
        config = YamlConfiguration.loadConfiguration(main.getChronoFile());
    }

    public boolean isPresent(UUID uuid) {
        return config.contains(uuid.toString());
    }

    public void setChrono(UUID uuid, int amount) {
        config.set(uuid.toString(), amount);
        try {
            config.save(main.getChronoFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getChrono(UUID uuid) {
        if (!isPresent(uuid)) return 0;
        return config.getInt(uuid.toString());
    }
}
