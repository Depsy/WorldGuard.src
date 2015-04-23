/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import java.util.logging.Logger;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.entity.Entity;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.world.ChunkLoadEvent;
/*    */ import org.bukkit.event.world.WorldLoadEvent;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ 
/*    */ public class WorldGuardWorldListener
/*    */   implements Listener
/*    */ {
/*    */   private WorldGuardPlugin plugin;
/*    */ 
/*    */   public WorldGuardWorldListener(WorldGuardPlugin plugin)
/*    */   {
/* 25 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public void registerEvents()
/*    */   {
/* 32 */     this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onChunkLoad(ChunkLoadEvent event) {
/* 37 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*    */ 
/* 39 */     if (cfg.activityHaltToggle) {
/* 40 */       int removed = 0;
/*    */ 
/* 42 */       for (Entity entity : event.getChunk().getEntities()) {
/* 43 */         if (BukkitUtil.isIntensiveEntity(entity)) {
/* 44 */           entity.remove();
/* 45 */           removed++;
/*    */         }
/*    */       }
/*    */ 
/* 49 */       if (removed > 50)
/* 50 */         this.plugin.getLogger().info("Halt-Act: " + removed + " entities (>50) auto-removed from " + event.getChunk().toString());
/*    */     }
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onWorldLoad(WorldLoadEvent event)
/*    */   {
/* 58 */     initWorld(event.getWorld());
/*    */   }
/*    */ 
/*    */   public void initWorld(World world)
/*    */   {
/* 70 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 71 */     WorldConfiguration wcfg = cfg.get(world);
/* 72 */     if ((wcfg.alwaysRaining) && (!wcfg.disableWeather))
/* 73 */       world.setStorm(true);
/* 74 */     else if ((wcfg.disableWeather) && (!wcfg.alwaysRaining)) {
/* 75 */       world.setStorm(false);
/*    */     }
/* 77 */     if ((wcfg.alwaysThundering) && (!wcfg.disableThunder))
/* 78 */       world.setThundering(true);
/* 79 */     else if ((wcfg.disableThunder) && (!wcfg.alwaysThundering))
/* 80 */       world.setStorm(false);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardWorldListener
 * JD-Core Version:    0.6.2
 */