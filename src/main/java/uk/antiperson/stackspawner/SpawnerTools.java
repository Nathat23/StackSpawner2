package uk.antiperson.stackspawner;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnerTools {

    private StackSpawner ss;
    public SpawnerTools(StackSpawner ss){
        this.ss = ss;
    }

    public CreatureSpawner getNearbySpawner(CreatureSpawner newSpawner, int searchRadius){
        Location loc = newSpawner.getLocation();
        for(int x = -(searchRadius); x <= searchRadius; x++ ){
            for(int y = -(searchRadius); y <= searchRadius; y++ ){
                for(int z = -(searchRadius); z <= searchRadius; z++ ){
                    Block block = loc.getBlock().getRelative(x, y, z);
                    if(!(block.getState() instanceof CreatureSpawner)){
                        continue;
                    }
                    if(block.getLocation().equals(newSpawner.getLocation())){
                        continue;
                    }
                    CreatureSpawner nearby = (CreatureSpawner) block.getState();
                    if(newSpawner.getSpawnedType() != nearby.getSpawnedType()){
                        continue;
                    }
                    if(!SpawnerStorage.isStackedSpawner(nearby)){
                        SpawnerStorage.setSize(nearby, 1);
                    }
                    int size = SpawnerStorage.getSize(nearby);
                    if(size == ss.config.getConfig().getInt("max-size")){
                        continue;
                    }
                    return nearby;
                }
            }
        }
        return null;
    }

    public static ArmorStand getArmorStand(Location location){
        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
        for(Entity entity :  location.getWorld().getNearbyEntities(location, 0.1, 0.1, 0.1)){
            if(entity instanceof ArmorStand){
                return (ArmorStand) entity;
            }
        }
        return null;
    }

    public static ArmorStand spawnStand(Location location){
        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        return armorStand;
    }

    public void updateTag(StackedSpawner spawner){
        String original = ss.config.getConfig().getString("tag.format");
        String replace1 = original.replace("%size%", spawner.getSize() + "");
        String formattedType = WordUtils.capitalizeFully(spawner.getSpawner().getSpawnedType().toString().replaceAll("[^A-Za-z0-9]", " "));
        String replace2 = replace1.replace("%type%", formattedType);
        String replace3 = replace2.replace("%bukkit_type%", spawner.getSpawner().getSpawnedType().toString());
        String replace4 = ChatColor.translateAlternateColorCodes('&', replace3);
        spawner.getArmorStand().setCustomName(replace4);
        spawner.getArmorStand().setCustomNameVisible(true);
    }

    public static StackedSpawner getStackedSpawner(StackSpawner ss, CreatureSpawner spawner){
        if(SpawnerStorage.isStackedSpawner(spawner)){
            return new StackedSpawner(ss, spawner);
        }
        return null;
    }

    public static Entity attemptSpawn(Location spawner, EntityType type){
        int randX = ThreadLocalRandom.current().nextInt(-5,5);
        int randZ = ThreadLocalRandom.current().nextInt(-5,5);
        Location loc =  spawner.add(randX + 0.5, 0, randZ + 0.5);
        if(loc.getBlock().isEmpty()){
            LivingEntity livingEntity = (LivingEntity) spawner.getWorld().spawnEntity(loc, type);
            if(Bukkit.spigot().getConfig().getBoolean("world-settings.nerf-spawner-mobs")){
                livingEntity.setAI(false);
            }
            return livingEntity;
        }else{
            return null;
        }
    }


}
