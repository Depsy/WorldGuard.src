/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class BukkitPlayer extends LocalPlayer
/*    */ {
/*    */   private Player player;
/*    */   private WorldGuardPlugin plugin;
/*    */ 
/*    */   public BukkitPlayer(WorldGuardPlugin plugin, Player player)
/*    */   {
/* 33 */     this.plugin = plugin;
/* 34 */     this.player = player;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 39 */     return this.player.getName();
/*    */   }
/*    */ 
/*    */   public boolean hasGroup(String group)
/*    */   {
/* 44 */     return this.plugin.inGroup(this.player, group);
/*    */   }
/*    */ 
/*    */   public Vector getPosition()
/*    */   {
/* 49 */     Location loc = this.player.getLocation();
/* 50 */     return new Vector(loc.getX(), loc.getY(), loc.getZ());
/*    */   }
/*    */ 
/*    */   public void kick(String msg)
/*    */   {
/* 55 */     this.player.kickPlayer(msg);
/*    */   }
/*    */ 
/*    */   public void ban(String msg)
/*    */   {
/* 60 */     this.player.setBanned(true);
/* 61 */     this.player.kickPlayer(msg);
/*    */   }
/*    */ 
/*    */   public String[] getGroups()
/*    */   {
/* 66 */     return this.plugin.getGroups(this.player);
/*    */   }
/*    */ 
/*    */   public void printRaw(String msg)
/*    */   {
/* 71 */     this.player.sendMessage(msg);
/*    */   }
/*    */ 
/*    */   public boolean hasPermission(String perm)
/*    */   {
/* 76 */     return this.plugin.hasPermission(this.player, perm);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.BukkitPlayer
 * JD-Core Version:    0.6.2
 */