/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.enginehub.util.PermissionModel;
/*    */ 
/*    */ abstract class AbstractPermissionModel
/*    */   implements PermissionModel
/*    */ {
/*    */   private final WorldGuardPlugin plugin;
/*    */   private final CommandSender sender;
/*    */ 
/*    */   public AbstractPermissionModel(WorldGuardPlugin plugin, CommandSender sender)
/*    */   {
/* 30 */     this.plugin = plugin;
/* 31 */     this.sender = sender;
/*    */   }
/*    */ 
/*    */   protected WorldGuardPlugin getPlugin() {
/* 35 */     return this.plugin;
/*    */   }
/*    */ 
/*    */   public CommandSender getSender() {
/* 39 */     return this.sender;
/*    */   }
/*    */ 
/*    */   protected boolean hasPluginPermission(String permission) {
/* 43 */     return this.plugin.hasPermission(getSender(), "worldguard." + permission);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.AbstractPermissionModel
 * JD-Core Version:    0.6.2
 */