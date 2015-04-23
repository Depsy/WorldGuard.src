/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.LightningStrike;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.weather.LightningStrikeEvent;
/*     */ import org.bukkit.event.weather.ThunderChangeEvent;
/*     */ import org.bukkit.event.weather.WeatherChangeEvent;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class WorldGuardWeatherListener
/*     */   implements Listener
/*     */ {
/*     */   private WorldGuardPlugin plugin;
/*     */ 
/*     */   public WorldGuardWeatherListener(WorldGuardPlugin plugin)
/*     */   {
/*  48 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public void registerEvents() {
/*  52 */     this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onWeatherChange(WeatherChangeEvent event) {
/*  57 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  58 */     WorldConfiguration wcfg = cfg.get(event.getWorld());
/*     */ 
/*  60 */     if (event.toWeatherState()) {
/*  61 */       if (wcfg.disableWeather) {
/*  62 */         event.setCancelled(true);
/*     */       }
/*     */     }
/*  65 */     else if ((!wcfg.disableWeather) && (wcfg.alwaysRaining))
/*  66 */       event.setCancelled(true);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onThunderChange(ThunderChangeEvent event)
/*     */   {
/*  73 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  74 */     WorldConfiguration wcfg = cfg.get(event.getWorld());
/*     */ 
/*  76 */     if (event.toThunderState()) {
/*  77 */       if (wcfg.disableThunder) {
/*  78 */         event.setCancelled(true);
/*     */       }
/*     */     }
/*  81 */     else if ((!wcfg.disableWeather) && (wcfg.alwaysThundering))
/*  82 */       event.setCancelled(true);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onLightningStrike(LightningStrikeEvent event)
/*     */   {
/*  89 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  90 */     WorldConfiguration wcfg = cfg.get(event.getWorld());
/*     */ 
/*  92 */     if (wcfg.disallowedLightningBlocks.size() > 0) {
/*  93 */       int targetId = event.getLightning().getLocation().getBlock().getTypeId();
/*  94 */       if (wcfg.disallowedLightningBlocks.contains(Integer.valueOf(targetId))) {
/*  95 */         event.setCancelled(true);
/*     */       }
/*     */     }
/*     */ 
/*  99 */     Location loc = event.getLightning().getLocation();
/* 100 */     if (wcfg.useRegions) {
/* 101 */       Vector pt = BukkitUtil.toVector(loc);
/* 102 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(loc.getWorld());
/* 103 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*     */ 
/* 105 */       if (!set.allows(DefaultFlag.LIGHTNING))
/* 106 */         event.setCancelled(true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardWeatherListener
 * JD-Core Version:    0.6.2
 */