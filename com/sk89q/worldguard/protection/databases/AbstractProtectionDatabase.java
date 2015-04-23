/*    */ package com.sk89q.worldguard.protection.databases;
/*    */ 
/*    */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*    */ 
/*    */ public abstract class AbstractProtectionDatabase
/*    */   implements ProtectionDatabase
/*    */ {
/*    */   public void load(RegionManager manager)
/*    */     throws ProtectionDatabaseException
/*    */   {
/* 32 */     load();
/* 33 */     manager.setRegions(getRegions());
/*    */   }
/*    */ 
/*    */   public void save(RegionManager manager)
/*    */     throws ProtectionDatabaseException
/*    */   {
/* 42 */     setRegions(manager.getRegions());
/* 43 */     save();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.AbstractProtectionDatabase
 * JD-Core Version:    0.6.2
 */