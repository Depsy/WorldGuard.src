/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.databases.CSVDatabase;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ 
/*     */ public class LegacyWorldGuardMigration
/*     */ {
/*     */   public static void migrateBlacklist(WorldGuardPlugin plugin)
/*     */   {
/*  48 */     World mainWorld = (World)plugin.getServer().getWorlds().get(0);
/*  49 */     String mainWorldName = mainWorld.getName();
/*  50 */     String newPath = "worlds/" + mainWorldName + "/blacklist.txt";
/*     */ 
/*  52 */     File oldFile = new File(plugin.getDataFolder(), "blacklist.txt");
/*  53 */     File newFile = new File(plugin.getDataFolder(), newPath);
/*     */ 
/*  55 */     if ((!newFile.exists()) && (oldFile.exists())) {
/*  56 */       plugin.getLogger().warning("WorldGuard will now update your blacklist from an older version of WorldGuard.");
/*     */ 
/*  60 */       newFile.getParentFile().mkdirs();
/*     */ 
/*  62 */       if (copyFile(oldFile, newFile)) {
/*  63 */         oldFile.renameTo(new File(plugin.getDataFolder(), "blacklist.txt.old"));
/*     */       }
/*     */       else {
/*  66 */         plugin.getLogger().warning("blacklist.txt has been converted for the main world at " + newPath + "");
/*     */ 
/*  68 */         plugin.getLogger().warning("Your other worlds currently have no blacklist defined!");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void migrateRegions(WorldGuardPlugin plugin)
/*     */   {
/*     */     try
/*     */     {
/*  82 */       File oldDatabase = new File(plugin.getDataFolder(), "regions.txt");
/*  83 */       if (!oldDatabase.exists()) return;
/*     */ 
/*  85 */       plugin.getLogger().info("The regions database has changed in 5.x. Your old regions database will be converted to the new format and set as your primary world's database.");
/*     */ 
/*  89 */       World w = (World)plugin.getServer().getWorlds().get(0);
/*  90 */       RegionManager mgr = plugin.getGlobalRegionManager().get(w);
/*     */ 
/*  93 */       CSVDatabase db = new CSVDatabase(oldDatabase, plugin.getLogger());
/*  94 */       db.load();
/*     */ 
/*  97 */       mgr.setRegions(db.getRegions());
/*  98 */       mgr.save();
/*     */ 
/* 100 */       oldDatabase.renameTo(new File(plugin.getDataFolder(), "regions.txt.old"));
/*     */ 
/* 102 */       plugin.getLogger().info("Regions database converted!");
/*     */     } catch (ProtectionDatabaseException e) {
/* 104 */       plugin.getLogger().warning("Failed to load regions: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean copyFile(File from, File to)
/*     */   {
/* 117 */     InputStream in = null;
/* 118 */     OutputStream out = null;
/*     */     try
/*     */     {
/* 121 */       in = new FileInputStream(from);
/* 122 */       out = new FileOutputStream(to);
/*     */ 
/* 124 */       byte[] buf = new byte[1024];
/*     */       int len;
/* 126 */       while ((len = in.read(buf)) > 0) {
/* 127 */         out.write(buf, 0, len);
/*     */       }
/*     */ 
/* 130 */       in.close();
/* 131 */       out.close();
/*     */ 
/* 133 */       return true;
/*     */     } catch (FileNotFoundException ignore) {
/*     */     } catch (IOException ignore) {
/*     */     } finally {
/* 137 */       if (in != null)
/*     */         try {
/* 139 */           in.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         }
/* 144 */       if (out != null)
/*     */         try {
/* 146 */           out.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         }
/*     */     }
/* 152 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.LegacyWorldGuardMigration
 * JD-Core Version:    0.6.2
 */