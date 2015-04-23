/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*    */ import org.bukkit.World;
/*    */ 
/*    */ public class WGBukkit
/*    */ {
/*    */   public static WorldGuardPlugin getPlugin()
/*    */   {
/* 44 */     return WorldGuardPlugin.inst();
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public static void cleanCache()
/*    */   {
/*    */   }
/*    */ 
/*    */   public static RegionManager getRegionManager(World world)
/*    */   {
/* 64 */     if (getPlugin() == null) {
/* 65 */       return null;
/*    */     }
/* 67 */     return WorldGuardPlugin.inst().getRegionManager(world);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WGBukkit
 * JD-Core Version:    0.6.2
 */