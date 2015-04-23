/*     */ package com.sk89q.worldguard.protection.managers;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabase;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegionMBRConverter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeMap;
/*     */ import org.khelekore.prtree.MBR;
/*     */ import org.khelekore.prtree.MBRConverter;
/*     */ import org.khelekore.prtree.PRTree;
/*     */ import org.khelekore.prtree.SimpleMBR;
/*     */ 
/*     */ public class PRTreeRegionManager extends RegionManager
/*     */ {
/*     */   private static final int BRANCH_FACTOR = 30;
/*     */   private Map<String, ProtectedRegion> regions;
/*  44 */   private MBRConverter<ProtectedRegion> converter = new ProtectedRegionMBRConverter();
/*     */   private PRTree<ProtectedRegion> tree;
/*     */ 
/*     */   public PRTreeRegionManager(ProtectionDatabase regionLoader)
/*     */   {
/*  56 */     super(regionLoader);
/*  57 */     this.regions = new TreeMap();
/*  58 */     this.tree = new PRTree(this.converter, 30);
/*     */   }
/*     */ 
/*     */   public Map<String, ProtectedRegion> getRegions()
/*     */   {
/*  63 */     return this.regions;
/*     */   }
/*     */ 
/*     */   public void setRegions(Map<String, ProtectedRegion> regions)
/*     */   {
/*  68 */     this.regions = new TreeMap(regions);
/*  69 */     this.tree = new PRTree(this.converter, 30);
/*  70 */     this.tree.load(regions.values());
/*     */   }
/*     */ 
/*     */   public void addRegion(ProtectedRegion region)
/*     */   {
/*  75 */     this.regions.put(region.getId().toLowerCase(), region);
/*  76 */     this.tree = new PRTree(this.converter, 30);
/*  77 */     this.tree.load(this.regions.values());
/*     */   }
/*     */ 
/*     */   public boolean hasRegion(String id)
/*     */   {
/*  82 */     return this.regions.containsKey(id.toLowerCase());
/*     */   }
/*     */ 
/*     */   public void removeRegion(String id)
/*     */   {
/*  87 */     ProtectedRegion region = (ProtectedRegion)this.regions.get(id.toLowerCase());
/*     */ 
/*  89 */     this.regions.remove(id.toLowerCase());
/*     */ 
/*  91 */     if (region != null) {
/*  92 */       List removeRegions = new ArrayList();
/*  93 */       for (ProtectedRegion curRegion : this.regions.values()) {
/*  94 */         if (curRegion.getParent() == region) {
/*  95 */           removeRegions.add(curRegion.getId().toLowerCase());
/*     */         }
/*     */       }
/*     */ 
/*  99 */       for (String remId : removeRegions) {
/* 100 */         removeRegion(remId);
/*     */       }
/*     */     }
/*     */ 
/* 104 */     this.tree = new PRTree(this.converter, 30);
/* 105 */     this.tree.load(this.regions.values());
/*     */   }
/*     */ 
/*     */   public ApplicableRegionSet getApplicableRegions(Vector pt)
/*     */   {
/* 112 */     pt = pt.floor();
/*     */ 
/* 114 */     List appRegions = new ArrayList();
/* 115 */     MBR pointMBR = new SimpleMBR(new double[] { pt.getX(), pt.getX(), pt.getY(), pt.getY(), pt.getZ(), pt.getZ() });
/*     */ 
/* 117 */     for (ProtectedRegion region : this.tree.find(pointMBR)) {
/* 118 */       if ((region.contains(pt)) && (!appRegions.contains(region))) {
/* 119 */         appRegions.add(region);
/*     */ 
/* 121 */         ProtectedRegion parent = region.getParent();
/*     */ 
/* 123 */         while (parent != null) {
/* 124 */           if (!appRegions.contains(parent)) {
/* 125 */             appRegions.add(parent);
/*     */           }
/*     */ 
/* 128 */           parent = parent.getParent();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 133 */     Collections.sort(appRegions);
/*     */ 
/* 135 */     return new ApplicableRegionSet(appRegions, (ProtectedRegion)this.regions.get("__global__"));
/*     */   }
/*     */ 
/*     */   public ApplicableRegionSet getApplicableRegions(ProtectedRegion checkRegion) {
/* 140 */     List appRegions = new ArrayList();
/* 141 */     appRegions.addAll(this.regions.values());
/*     */     List intersectRegions;
/*     */     try {
/* 145 */       intersectRegions = checkRegion.getIntersectingRegions(appRegions);
/*     */     } catch (Exception e) {
/* 147 */       intersectRegions = new ArrayList();
/*     */     }
/*     */ 
/* 150 */     return new ApplicableRegionSet(intersectRegions, (ProtectedRegion)this.regions.get("__global__"));
/*     */   }
/*     */ 
/*     */   public List<String> getApplicableRegionsIDs(Vector pt)
/*     */   {
/* 157 */     pt = pt.floor();
/*     */ 
/* 159 */     List applicable = new ArrayList();
/* 160 */     MBR pointMBR = new SimpleMBR(new double[] { pt.getX(), pt.getX(), pt.getY(), pt.getY(), pt.getZ(), pt.getZ() });
/*     */ 
/* 162 */     for (ProtectedRegion region : this.tree.find(pointMBR)) {
/* 163 */       if ((region.contains(pt)) && (!applicable.contains(region.getId()))) {
/* 164 */         applicable.add(region.getId());
/*     */ 
/* 166 */         ProtectedRegion parent = region.getParent();
/*     */ 
/* 168 */         while (parent != null) {
/* 169 */           if (!applicable.contains(parent.getId())) {
/* 170 */             applicable.add(parent.getId());
/*     */           }
/*     */ 
/* 173 */           parent = parent.getParent();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 178 */     return applicable;
/*     */   }
/*     */ 
/*     */   public boolean overlapsUnownedRegion(ProtectedRegion checkRegion, LocalPlayer player)
/*     */   {
/* 183 */     List appRegions = new ArrayList();
/*     */ 
/* 185 */     for (ProtectedRegion other : this.regions.values())
/* 186 */       if (!other.getOwners().contains(player))
/*     */       {
/* 190 */         appRegions.add(other);
/*     */       }
/*     */     List intersectRegions;
/*     */     try
/*     */     {
/* 195 */       intersectRegions = checkRegion.getIntersectingRegions(appRegions);
/*     */     } catch (Exception e) {
/* 197 */       intersectRegions = new ArrayList();
/*     */     }
/*     */ 
/* 200 */     return intersectRegions.size() > 0;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 205 */     return this.regions.size();
/*     */   }
/*     */ 
/*     */   public int getRegionCountOfPlayer(LocalPlayer player)
/*     */   {
/* 210 */     int count = 0;
/*     */ 
/* 212 */     for (Map.Entry entry : this.regions.entrySet()) {
/* 213 */       if (((ProtectedRegion)entry.getValue()).getOwners().contains(player)) {
/* 214 */         count++;
/*     */       }
/*     */     }
/*     */ 
/* 218 */     return count;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.managers.PRTreeRegionManager
 * JD-Core Version:    0.6.2
 */