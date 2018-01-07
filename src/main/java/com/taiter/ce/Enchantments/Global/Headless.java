package com.taiter.ce.Enchantments.Global;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Headless extends CEnchantment {

    public Headless(Application app) {
        super(app);
        triggers.add(Trigger.DAMAGE_GIVEN);
        resetMaxLevel();
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        final Player player = (Player) event.getDamager();
        final LivingEntity ent = (LivingEntity) event.getEntity();

        new BukkitRunnable() {
            @Override
            public void run() {

                if (ent.getHealth() <= 0) {
                    byte type = -1;
                    if (ent instanceof Player) {
                        type = 3;
                    } else if (ent instanceof WitherSkeleton) {
                        type = 1;
                    } else if (ent instanceof Skeleton) {
                        type = 0;
                    } else if (ent instanceof Zombie) {
                        type = 2;
                    } else if (ent instanceof Creeper) {
                        type = 4;
                    }
                    if (type < 0) {
                        return;
                    }
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, type);
                    if (type == 3) {
                        SkullMeta sm = (SkullMeta) skull.getItemMeta();
                        sm.setOwner(ent.getName());
                        skull.setItemMeta(sm);
                    }
                    ent.getWorld().dropItem(ent.getLocation(), skull);
                    EffectManager.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.1f, 1.5f);
                }
            }
        }.runTaskLater(getPlugin(), 5l);

    }

    @Override
    public void initConfigEntries() {
    }
}
