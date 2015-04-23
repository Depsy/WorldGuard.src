/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*    */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*    */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*    */ import com.sk89q.worldguard.protection.flags.StateFlag.State;
/*    */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class RegionQueryUtil
/*    */ {
/*    */   public static boolean isInvincible(WorldGuardPlugin plugin, Player player)
/*    */   {
/* 35 */     return isInvincible(plugin, player, null);
/*    */   }
/*    */ 
/*    */   public static boolean isInvincible(WorldGuardPlugin plugin, Player player, ApplicableRegionSet set)
/*    */   {
/* 40 */     Location loc = player.getLocation();
/* 41 */     World world = player.getWorld();
/*    */ 
/* 43 */     FlagStateManager.PlayerFlagState state = plugin.getFlagStateManager().getState(player);
/*    */ 
/* 45 */     if ((state.lastInvincibleWorld == null) || (!state.lastInvincibleWorld.equals(world)) || (state.lastInvincibleX != loc.getBlockX()) || (state.lastInvincibleY != loc.getBlockY()) || (state.lastInvincibleZ != loc.getBlockZ()))
/*    */     {
/* 50 */       state.lastInvincibleX = loc.getBlockX();
/* 51 */       state.lastInvincibleY = loc.getBlockY();
/* 52 */       state.lastInvincibleZ = loc.getBlockZ();
/* 53 */       state.lastInvincibleWorld = world;
/*    */ 
/* 55 */       if (set == null) {
/* 56 */         Vector vec = new Vector(state.lastInvincibleX, state.lastInvincibleY, state.lastInvincibleZ);
/*    */ 
/* 58 */         RegionManager mgr = plugin.getGlobalRegionManager().get(world);
/* 59 */         set = mgr.getApplicableRegions(vec);
/*    */       }
/*    */ 
/* 62 */       state.wasInvincible = set.allows(DefaultFlag.INVINCIBILITY, plugin.wrapPlayer(player));
/*    */     }
/*    */ 
/* 65 */     return state.wasInvincible;
/*    */   }
/*    */ 
/*    */   public static Boolean isAllowedInvinciblity(WorldGuardPlugin plugin, Player player) {
/* 69 */     World world = player.getWorld();
/* 70 */     FlagStateManager.PlayerFlagState state = plugin.getFlagStateManager().getState(player);
/* 71 */     Vector vec = new Vector(state.lastInvincibleX, state.lastInvincibleY, state.lastInvincibleZ);
/*    */ 
/* 73 */     StateFlag.State regionState = (StateFlag.State)plugin.getGlobalRegionManager().get(world).getApplicableRegions(vec).getFlag(DefaultFlag.INVINCIBILITY, plugin.wrapPlayer(player));
/*    */ 
/* 75 */     if (regionState == StateFlag.State.ALLOW)
/* 76 */       return Boolean.TRUE;
/* 77 */     if (regionState == StateFlag.State.DENY) {
/* 78 */       return Boolean.FALSE;
/*    */     }
/* 80 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.RegionQueryUtil
 * JD-Core Version:    0.6.2
 */