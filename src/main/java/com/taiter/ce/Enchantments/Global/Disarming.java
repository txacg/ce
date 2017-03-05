package com.taiter.ce.Enchantments.Global;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;

public class Disarming extends CEnchantment {
    Boolean pvpOnly = true;

    public Disarming(Application app) {
        super(app);
        configEntries.put("PvpOnly", true);
        triggers.add(Trigger.DAMAGE_GIVEN);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        LivingEntity target = (LivingEntity) event.getEntity();
        if(!(target instanceof Player) && pvpOnly){
            return;
        }
        ItemStack inHand = target.getEquipment().getItemInMainHand();
        if (inHand != null && !inHand.getType().equals(Material.AIR)) {
            target.getWorld().dropItem(target.getLocation(), inHand).setPickupDelay(40);
            target.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        }
    }

    @Override
    public void initConfigEntries() {
        pvpOnly = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".PvpOnly"));
    }
}
