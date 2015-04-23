/*     */ package com.sk89q.worldguard.protection.managers;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*     */ import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabase;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class FlatRegionManager extends RegionManager
/*     */ {
/*     */   private Map<String, ProtectedRegion> regions;
/*     */ 
/*     */   public FlatRegionManager(ProtectionDatabase regionLoader)
/*     */   {
/*  50 */     super(regionLoader);
/*  51 */     this.regions = new TreeMap();
/*     */   }
/*     */ 
/*     */   public Map<String, ProtectedRegion> getRegions()
/*     */   {
/*  56 */     return this.regions;
/*     */   }
/*     */ 
/*     */   public void setRegions(Map<String, ProtectedRegion> regions)
/*     */   {
/*  61 */     this.regions = new TreeMap(regions);
/*     */   }
/*     */ 
/*     */   public void addRegion(ProtectedRegion region)
/*     */   {
/*  66 */     this.regions.put(region.getId().toLowerCase(), region);
/*     */   }
/*     */ 
/*     */   public void removeRegion(String id)
/*     */   {
/*  71 */     ProtectedRegion region = (ProtectedRegion)this.regions.get(id.toLowerCase());
/*  72 */     this.regions.remove(id.toLowerCase());
/*     */ 
/*  74 */     if (region != null) {
/*  75 */       List removeRegions = new ArrayList();
/*  76 */       for (ProtectedRegion curRegion : this.regions.values()) {
/*  77 */         if (curRegion.getParent() == region) {
/*  78 */           removeRegions.add(curRegion.getId().toLowerCase());
/*     */         }
/*     */       }
/*     */ 
/*  82 */       for (String remId : removeRegions)
/*  83 */         removeRegion(remId);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasRegion(String id)
/*     */   {
/*  90 */     return this.regions.containsKey(id.toLowerCase());
/*     */   }
/*     */ 
/*     */   public ApplicableRegionSet getApplicableRegions(Vector pt)
/*     */   {
/*  95 */     TreeSet appRegions = new TreeSet();
/*     */ 
/*  98 */     for (ProtectedRegion region : this.regions.values()) {
/*  99 */       if (region.contains(pt)) {
/* 100 */         appRegions.add(region);
/*     */ 
/* 102 */         ProtectedRegion parent = region.getParent();
/*     */ 
/* 104 */         while (parent != null) {
/* 105 */           if (!appRegions.contains(parent)) {
/* 106 */             appRegions.add(parent);
/*     */           }
/*     */ 
/* 109 */           parent = parent.getParent();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 114 */     return new ApplicableRegionSet(appRegions, (ProtectedRegion)this.regions.get("__global__"));
/*     */   }
/*     */ 
/*     */   public List<String> getApplicableRegionsIDs(Vector pt)
/*     */   {
/* 140 */     List applicable = new ArrayList();
/*     */ 
/* 142 */     for (Map.Entry entry : this.regions.entrySet()) {
/* 143 */       if (((ProtectedRegion)entry.getValue()).contains(pt)) {
/* 144 */         applicable.add(entry.getKey());
/*     */       }
/*     */     }
/*     */ 
/* 148 */     return applicable;
/*     */   }
/*     */ 
/*     */   public ApplicableRegionSet getApplicableRegions(ProtectedRegion checkRegion)
/*     */   {
/* 154 */     List appRegions = new ArrayList();
/* 155 */     appRegions.addAll(this.regions.values());
/*     */     List intersectRegions;
/*     */     try
/*     */     {
/* 160 */       intersectRegions = checkRegion.getIntersectingRegions(appRegions);
/*     */     } catch (Exception e) {
/* 162 */       intersectRegions = new ArrayList();
/*     */     }
/*     */ 
/* 165 */     return new ApplicableRegionSet(intersectRegions, (ProtectedRegion)this.regions.get("__global__"));
/*     */   }
/*     */ 
/*     */   public boolean overlapsUnownedRegion(ProtectedRegion checkRegion, LocalPlayer player)
/*     */   {
/* 170 */     List appRegions = new ArrayList();
/*     */ 
/* 172 */     for (ProtectedRegion other : this.regions.values())
/* 173 */       if (!other.getOwners().contains(player))
/*     */       {
/* 177 */         appRegions.add(other);
/*     */       }
/*     */     List intersectRegions;
/*     */     try
/*     */     {
/* 182 */       intersectRegions = checkRegion.getIntersectingRegions(appRegions);
/*     */     } catch (UnsupportedIntersectionException e) {
/* 184 */       intersectRegions = new ArrayList();
/*     */     }
/*     */ 
/* 187 */     return intersectRegions.size() > 0;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 192 */     return this.regions.size();
/*     */   }
/*     */ 
/*     */   public int getRegionCountOfPlayer(LocalPlayer player)
/*     */   {
/* 197 */     int count = 0;
/*     */ 
/* 199 */     for (ProtectedRegion region : this.regions.values()) {
/* 200 */       if (region.getOwners().contains(player)) {
/* 201 */         count++;
/*     */       }
/*     */     }
/*     */ 
/* 205 */     return count;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.managers.FlatRegionManager
 * JD-Core Version:    0.6.2
 */