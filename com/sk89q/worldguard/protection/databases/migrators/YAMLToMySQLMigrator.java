/*    */ package com.sk89q.worldguard.protection.databases.migrators;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import com.sk89q.worldguard.protection.databases.MySQLDatabase;
/*    */ import com.sk89q.worldguard.protection.databases.ProtectionDatabase;
/*    */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*    */ import com.sk89q.worldguard.protection.databases.YAMLDatabase;
/*    */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class YAMLToMySQLMigrator extends AbstractDatabaseMigrator
/*    */ {
/*    */   private WorldGuardPlugin plugin;
/*    */   private HashMap<String, File> regionYamlFiles;
/*    */ 
/*    */   public YAMLToMySQLMigrator(WorldGuardPlugin plugin)
/*    */   {
/* 41 */     this.plugin = plugin;
/*    */ 
/* 43 */     this.regionYamlFiles = new HashMap();
/*    */ 
/* 45 */     File[] files = new File(plugin.getDataFolder(), "worlds" + File.separator).listFiles();
/* 46 */     for (File item : files)
/* 47 */       if (item.isDirectory())
/* 48 */         for (File subItem : item.listFiles())
/* 49 */           if (subItem.getName().equals("regions.yml"))
/* 50 */             this.regionYamlFiles.put(item.getName(), subItem);
/*    */   }
/*    */ 
/*    */   protected Set<String> getWorldsFromOld()
/*    */   {
/* 59 */     return this.regionYamlFiles.keySet();
/*    */   }
/*    */ 
/*    */   protected Map<String, ProtectedRegion> getRegionsForWorldFromOld(String world) throws MigrationException
/*    */   {
/*    */     ProtectionDatabase oldDatabase;
/*    */     try {
/* 66 */       oldDatabase = new YAMLDatabase((File)this.regionYamlFiles.get(world), this.plugin.getLogger());
/* 67 */       oldDatabase.load();
/*    */     } catch (FileNotFoundException e) {
/* 69 */       throw new MigrationException(e);
/*    */     } catch (ProtectionDatabaseException e) {
/* 71 */       throw new MigrationException(e);
/*    */     }
/*    */ 
/* 74 */     return oldDatabase.getRegions();
/*    */   }
/*    */ 
/*    */   protected ProtectionDatabase getNewWorldStorage(String world) throws MigrationException
/*    */   {
/*    */     try {
/* 80 */       return new MySQLDatabase(this.plugin.getGlobalStateManager(), world, this.plugin.getLogger());
/*    */     } catch (ProtectionDatabaseException e) {
/* 82 */       throw new MigrationException(e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.migrators.YAMLToMySQLMigrator
 * JD-Core Version:    0.6.2
 */