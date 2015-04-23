/*    */ package com.sk89q.worldguard.protection.databases.migrators;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import com.sk89q.worldguard.protection.databases.MySQLDatabase;
/*    */ import com.sk89q.worldguard.protection.databases.ProtectionDatabase;
/*    */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*    */ import com.sk89q.worldguard.protection.databases.YAMLDatabase;
/*    */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class MySQLToYAMLMigrator extends AbstractDatabaseMigrator
/*    */ {
/*    */   private WorldGuardPlugin plugin;
/*    */   private Set<String> worlds;
/*    */ 
/*    */   public MySQLToYAMLMigrator(WorldGuardPlugin plugin)
/*    */     throws MigrationException
/*    */   {
/* 46 */     this.plugin = plugin;
/* 47 */     this.worlds = new HashSet();
/*    */ 
/* 49 */     ConfigurationManager config = plugin.getGlobalStateManager();
/*    */     try
/*    */     {
/* 52 */       Connection conn = DriverManager.getConnection(config.sqlDsn, config.sqlUsername, config.sqlPassword);
/*    */ 
/* 54 */       ResultSet worlds = conn.prepareStatement("SELECT `name` FROM `world`;").executeQuery();
/*    */ 
/* 56 */       while (worlds.next()) {
/* 57 */         this.worlds.add(worlds.getString(1));
/*    */       }
/*    */ 
/* 60 */       conn.close();
/*    */     } catch (SQLException e) {
/* 62 */       throw new MigrationException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected Set<String> getWorldsFromOld()
/*    */   {
/* 68 */     return this.worlds;
/*    */   }
/*    */ 
/*    */   protected Map<String, ProtectedRegion> getRegionsForWorldFromOld(String world) throws MigrationException
/*    */   {
/*    */     ProtectionDatabase oldDatabase;
/*    */     try {
/* 75 */       oldDatabase = new MySQLDatabase(this.plugin.getGlobalStateManager(), world, this.plugin.getLogger());
/* 76 */       oldDatabase.load();
/*    */     } catch (ProtectionDatabaseException e) {
/* 78 */       throw new MigrationException(e);
/*    */     }
/*    */ 
/* 81 */     return oldDatabase.getRegions();
/*    */   }
/*    */ 
/*    */   protected ProtectionDatabase getNewWorldStorage(String world) throws MigrationException
/*    */   {
/*    */     try {
/* 87 */       File file = new File(this.plugin.getDataFolder(), "worlds" + File.separator + world + File.separator + "regions.yml");
/*    */ 
/* 90 */       return new YAMLDatabase(file, this.plugin.getLogger());
/*    */     } catch (FileNotFoundException e) {
/* 92 */       throw new MigrationException(e);
/*    */     } catch (ProtectionDatabaseException e) {
/* 94 */       throw new MigrationException(e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.migrators.MySQLToYAMLMigrator
 * JD-Core Version:    0.6.2
 */