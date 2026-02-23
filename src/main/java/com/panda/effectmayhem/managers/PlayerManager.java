package com.panda.effectmayhem.managers;

import com.panda.effectmayhem.Main;
import com.panda.effectmayhem.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerManager extends BukkitRunnable {
    private final Main main;
    private final BukkitTask task;
    private final Map<UUID, Integer> timers;
    private final Map<PotionEffectType.Category, List<PotionEffectType>> potionEffects;
    private final Random random;

    private int duration = 10; // duration in seconds
    private int amplifier = 0;
    private double harmfulProb = 0.33;
    private double neutralProb = 0.34;
    private double beneficialProb = 0.33;

    private double k; // tuning constant
    private int interval;

    public PlayerManager(Main main) {
        this.main = main;
        timers = new HashMap<>();
        potionEffects = new HashMap<>();
        random = new Random();

        for (PotionEffectType.Category c : PotionEffectType.Category.values()) {
            potionEffects.put(c, new ArrayList<>());
        }
        for (PotionEffectType t : PotionEffectType.values()) {
            potionEffects.get(t.getEffectCategory()).add(t);
        }

        k = main.getConfig().getDouble("k-value");
        if (k <= 0) {
            main.getLogger().warning("k-value must be strictly greater than 0! Resetting to the default value of 40.");
            k = 40.0;
            main.getConfig().set("k-value", k);
        }

        interval = main.getConfig().getInt("interval-seconds");
        if (interval <= 0) {
            interval = 3600;
            main.getLogger().warning("interval-seconds must be positive. Resetting to 3600.");
            main.getConfig().set("interval-seconds", 3600);
        }

        main.saveConfig();
        task = runTaskTimer(this.main, 5 * 20L, 20L);
    }
    
    public void addPlayer(Player player) {
        if (timers.containsKey(player.getUniqueId())) return;
        timers.put(player.getUniqueId(), interval);
    }
    
    public void removePlayer(Player player) {
        if (!timers.containsKey(player.getUniqueId())) return;
        timers.remove(player.getUniqueId());
    }
    
    public void cleanup() {
        if (task != null) task.cancel();
        timers.clear();
        potionEffects.clear();
    }

    private void applyEffect(Player player) {
        long chrono = main.getChronoManager().getChrono(player.getUniqueId());
        updateVariables(chrono);

        double pr = random.nextDouble();
        PotionEffectType.Category category;
        if (pr < harmfulProb) category = PotionEffectType.Category.HARMFUL;
        else if (pr < harmfulProb + neutralProb) category = PotionEffectType.Category.NEUTRAL;
        else category = PotionEffectType.Category.BENEFICIAL;

        List<PotionEffectType> list = potionEffects.get(category);
        PotionEffectType effect = list.get(random.nextInt(list.size()));

        if ((chrono < 0 && category == PotionEffectType.Category.BENEFICIAL) || (chrono > 0 && category == PotionEffectType.Category.HARMFUL))
            amplifier = 0;

        player.addPotionEffect(new PotionEffect(effect, duration * 20, amplifier, false, true, true));
        player.sendMessage(Utils.addHeader("&eYou have been given ")
                .append(Component.translatable(effect.translationKey()).color(TextColor.color(85, 255, 85)))
                .append(Component.text(Utils.translateColorCode(" &e for &b " + duration + " seconds&e!"))));
    }

    // updates the probabilities, duration and amplifier of the effect for a player based on their chrono amount
    private void updateVariables(long chrono) {
        double scale = chrono / (Math.abs(chrono) + k); // -1 to 1

        double shift = 0.28 * scale; // max shift is ±0.28
        double intensity = Math.abs(scale); // 0 to 1

        beneficialProb = 0.33 + shift;
        harmfulProb = 0.33 - shift;
        neutralProb = 1.0 - beneficialProb - harmfulProb;

        // duration scales mildly
        duration = (int) (20 + (40 * intensity));
        // 0 chrono - 20s
        // high positive/negative - up to 40s

        double r = random.nextDouble();
        if (r < 0.6 - 0.4 * intensity) {
            amplifier = 0;
        } else if (r < 0.9 - 0.2 * intensity) {
            amplifier = 1;
        } else {
            amplifier = 2;
        }
    }

    // =================================================================================================================
    @Override
    public void run() {
        Iterator<Map.Entry<UUID, Integer>> iterator = timers.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            UUID uuid = entry.getKey();

            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                iterator.remove();
                continue;
            }

            int timeLeft = entry.getValue() - 1;
            if (timeLeft <= 0) { // trigger the effect event
                applyEffect(player);
                timeLeft = interval;
            } else if (timeLeft <= 3600) {
                int minutes = Math.ceilDiv(timeLeft, 60);
                if (timeLeft%60 == 0 && timeLeft/60%10 == 0) {
                    player.sendMessage(Utils.addHeader("&aRandom effect in: &6" + minutes + " minutes"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
                }

                if (minutes != 1)
                    player.sendActionBar(Component.text(Utils.translateColorCode("&lNext effect in: &6&l" + minutes + " minutes")));
                else
                    player.sendActionBar(Component.text(Utils.translateColorCode("&lNext effect in: &c&l" + minutes + " minute")));
            }

            entry.setValue(timeLeft);
        }
    }
}
