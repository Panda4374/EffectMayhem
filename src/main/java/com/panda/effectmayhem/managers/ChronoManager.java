package com.panda.effectmayhem.managers;

import com.panda.effectmayhem.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChronoManager {
    private final Main main;
    private final File file;
    private final YamlConfiguration config;
    private final Map<UUID, Long> chronoCache = new HashMap<>();

    public ChronoManager(Main main) {
        this.main = main;
        this.file = main.getChronoFile();
        this.config = YamlConfiguration.loadConfiguration(file);

        loadAll();
        startAutoSave();
    }

    private void loadAll() {
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long value = config.getLong(key);
                chronoCache.put(uuid, value);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public long getChrono(UUID uuid) {
        return chronoCache.getOrDefault(uuid, 0L);
    }

    public void setChrono(UUID uuid, long amount) {
        chronoCache.put(uuid, amount);
    }

    public void addChrono(UUID uuid, long amount) {
        chronoCache.put(uuid, getChrono(uuid) + amount);
    }

    public boolean hasPlayed(UUID uuid) {
        return chronoCache.containsKey(uuid);
    }

    public void saveAll() {
        for (Map.Entry<UUID, Long> entry : chronoCache.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startAutoSave() {
        Bukkit.getScheduler().runTaskTimer(main, this::saveAll, 6000L, 6000L); // autosave every 5 minutes
    }
}