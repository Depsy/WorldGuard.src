/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import com.sk89q.worldguard.blacklist.Blacklist;
/*    */ 
/*    */ public class BukkitBlacklist extends Blacklist
/*    */ {
/*    */   private WorldGuardPlugin plugin;
/*    */ 
/*    */   public BukkitBlacklist(Boolean useAsWhitelist, WorldGuardPlugin plugin)
/*    */   {
/* 28 */     super(useAsWhitelist, plugin.getLogger());
/* 29 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public void broadcastNotification(String msg)
/*    */   {
/* 34 */     this.plugin.broadcastNotification(msg);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.BukkitBlacklist
 * JD-Core Version:    0.6.2
 */