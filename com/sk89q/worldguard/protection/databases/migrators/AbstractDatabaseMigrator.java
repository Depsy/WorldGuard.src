/*    */ package com.sk89q.worldguard.protection.databases.migrators;
/*    */ 
/*    */ import com.sk89q.worldguard.protection.databases.ProtectionDatabase;
/*    */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*    */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public abstract class AbstractDatabaseMigrator
/*    */   implements DatabaseMigrator
/*    */ {
/* 32 */   private static HashMap<MigratorKey, Class<? extends AbstractDatabaseMigrator>> migrators = new HashMap();
/*    */ 
/*    */   public static Map<MigratorKey, Class<? extends AbstractDatabaseMigrator>> getMigrators()
/*    */   {
/* 36 */     if (!migrators.isEmpty()) return migrators;
/*    */ 
/* 38 */     migrators.put(new MigratorKey("mysql", "yaml"), MySQLToYAMLMigrator.class);
/* 39 */     migrators.put(new MigratorKey("yaml", "mysql"), YAMLToMySQLMigrator.class);
/*    */ 
/* 41 */     return migrators;
/*    */   }
/*    */ 
/*    */   protected abstract Set<String> getWorldsFromOld() throws MigrationException;
/*    */ 
/*    */   protected abstract Map<String, ProtectedRegion> getRegionsForWorldFromOld(String paramString) throws MigrationException;
/*    */ 
/*    */   protected abstract ProtectionDatabase getNewWorldStorage(String paramString) throws MigrationException;
/*    */ 
/*    */   public void migrate() throws MigrationException {
/* 51 */     for (String world : getWorldsFromOld()) {
/* 52 */       ProtectionDatabase database = getNewWorldStorage(world);
/* 53 */       database.setRegions(getRegionsForWorldFromOld(world));
/*    */       try
/*    */       {
/* 56 */         database.save();
/*    */       } catch (ProtectionDatabaseException e) {
/* 58 */         throw new MigrationException(e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.migrators.AbstractDatabaseMigrator
 * JD-Core Version:    0.6.2
 */