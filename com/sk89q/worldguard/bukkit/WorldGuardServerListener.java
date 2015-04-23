/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.server.PluginDisableEvent;
/*    */ import org.bukkit.event.server.PluginEnableEvent;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.plugin.PluginDescriptionFile;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ 
/*    */ public class WorldGuardServerListener
/*    */   implements Listener
/*    */ {
/*    */   private final WorldGuardPlugin plugin;
/*    */ 
/*    */   public WorldGuardServerListener(WorldGuardPlugin plugin)
/*    */   {
/* 17 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public void registerEvents() {
/* 21 */     PluginManager pm = this.plugin.getServer().getPluginManager();
/* 22 */     pm.registerEvents(this, this.plugin);
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onPluginEnable(PluginEnableEvent event) {
/* 27 */     if (event.getPlugin().getDescription().getName().equalsIgnoreCase("CommandBook"))
/* 28 */       this.plugin.getGlobalStateManager().updateCommandBookGodMode();
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onPluginDisable(PluginDisableEvent event)
/*    */   {
/* 34 */     if (event.getPlugin().getDescription().getName().equalsIgnoreCase("CommandBook"))
/* 35 */       this.plugin.getGlobalStateManager().updateCommandBookGodMode();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardServerListener
 * JD-Core Version:    0.6.2
 */