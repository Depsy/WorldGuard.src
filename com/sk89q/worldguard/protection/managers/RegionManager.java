/*     */ package com.sk89q.worldguard.protection.managers;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldedit.bukkit.BukkitUtil;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabase;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.bukkit.Location;
/*     */ 
/*     */ public abstract class RegionManager
/*     */ {
/*     */   protected ProtectionDatabase loader;
/*     */ 
/*     */   public RegionManager(ProtectionDatabase loader)
/*     */   {
/*  50 */     this.loader = loader;
/*     */   }
/*     */ 
/*     */   public void load()
/*     */     throws ProtectionDatabaseException
/*     */   {
/*  60 */     this.loader.load(this);
/*     */   }
/*     */ 
/*     */   public void save()
/*     */     throws ProtectionDatabaseException
/*     */   {
/*  69 */     this.loader.save(this);
/*     */   }
/*     */ 
/*     */   public abstract Map<String, ProtectedRegion> getRegions();
/*     */ 
/*     */   public abstract void setRegions(Map<String, ProtectedRegion> paramMap);
/*     */ 
/*     */   public abstract void addRegion(ProtectedRegion paramProtectedRegion);
/*     */ 
/*     */   public abstract boolean hasRegion(String paramString);
/*     */ 
/*     */   public ProtectedRegion getRegion(String id)
/*     */   {
/* 111 */     if (id.startsWith("#")) {
/*     */       int index;
/*     */       try {
/* 114 */         index = Integer.parseInt(id.substring(1)) - 1;
/*     */       } catch (NumberFormatException e) {
/* 116 */         return null;
/*     */       }
/* 118 */       for (ProtectedRegion region : getRegions().values()) {
/* 119 */         if (index == 0) {
/* 120 */           return region;
/*     */         }
/* 122 */         index--;
/*     */       }
/* 124 */       return null;
/*     */     }
/*     */ 
/* 127 */     return getRegionExact(id);
/*     */   }
/*     */ 
/*     */   public ProtectedRegion getRegionExact(String id)
/*     */   {
/* 137 */     return (ProtectedRegion)getRegions().get(id.toLowerCase());
/*     */   }
/*     */ 
/*     */   public abstract void removeRegion(String paramString);
/*     */ 
/*     */   public ApplicableRegionSet getApplicableRegions(Location loc)
/*     */   {
/* 155 */     return getApplicableRegions(BukkitUtil.toVector(loc));
/*     */   }
/*     */ 
/*     */   public abstract ApplicableRegionSet getApplicableRegions(Vector paramVector);
/*     */ 
/*     */   public abstract ApplicableRegionSet getApplicableRegions(ProtectedRegion paramProtectedRegion);
/*     */ 
/*     */   public abstract List<String> getApplicableRegionsIDs(Vector paramVector);
/*     */ 
/*     */   public abstract boolean overlapsUnownedRegion(ProtectedRegion paramProtectedRegion, LocalPlayer paramLocalPlayer);
/*     */ 
/*     */   public abstract int size();
/*     */ 
/*     */   public abstract int getRegionCountOfPlayer(LocalPlayer paramLocalPlayer);
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.managers.RegionManager
 * JD-Core Version:    0.6.2
 */