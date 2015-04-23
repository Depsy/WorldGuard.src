/*     */ package com.sk89q.worldguard.protection.regions;
/*     */ 
/*     */ import com.sk89q.worldedit.BlockVector;
/*     */ import com.sk89q.worldedit.BlockVector2D;
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ProtectedPolygonalRegion extends ProtectedRegion
/*     */ {
/*     */   protected List<BlockVector2D> points;
/*     */   protected int minY;
/*     */   protected int maxY;
/*     */ 
/*     */   public ProtectedPolygonalRegion(String id, List<BlockVector2D> points, int minY, int maxY)
/*     */   {
/*  35 */     super(id);
/*  36 */     this.points = points;
/*  37 */     setMinMaxPoints(points, minY, maxY);
/*  38 */     this.minY = this.min.getBlockY();
/*  39 */     this.maxY = this.max.getBlockY();
/*     */   }
/*     */ 
/*     */   private void setMinMaxPoints(List<BlockVector2D> points2D, int minY, int maxY)
/*     */   {
/*  50 */     List points = new ArrayList();
/*  51 */     int y = minY;
/*  52 */     for (BlockVector2D point2D : points2D) {
/*  53 */       points.add(new Vector(point2D.getBlockX(), y, point2D.getBlockZ()));
/*  54 */       y = maxY;
/*     */     }
/*  56 */     setMinMaxPoints(points);
/*     */   }
/*     */ 
/*     */   public List<BlockVector2D> getPoints() {
/*  60 */     return this.points;
/*     */   }
/*     */ 
/*     */   public boolean contains(Vector pt)
/*     */   {
/*  68 */     int targetX = pt.getBlockX();
/*  69 */     int targetY = pt.getBlockY();
/*  70 */     int targetZ = pt.getBlockZ();
/*     */ 
/*  72 */     if ((targetY < this.minY) || (targetY > this.maxY)) {
/*  73 */       return false;
/*     */     }
/*     */ 
/*  76 */     if ((targetX < this.min.getBlockX()) || (targetX > this.max.getBlockX()) || (targetZ < this.min.getBlockZ()) || (targetZ > this.max.getBlockZ())) {
/*  77 */       return false;
/*     */     }
/*  79 */     boolean inside = false;
/*  80 */     int npoints = this.points.size();
/*     */ 
/*  88 */     int xOld = ((BlockVector2D)this.points.get(npoints - 1)).getBlockX();
/*  89 */     int zOld = ((BlockVector2D)this.points.get(npoints - 1)).getBlockZ();
/*     */ 
/*  91 */     for (int i = 0; i < npoints; i++) {
/*  92 */       int xNew = ((BlockVector2D)this.points.get(i)).getBlockX();
/*  93 */       int zNew = ((BlockVector2D)this.points.get(i)).getBlockZ();
/*     */ 
/*  95 */       if ((xNew == targetX) && (zNew == targetZ))
/*  96 */         return true;
/*     */       int z2;
/*     */       int x1;
/*     */       int x2;
/*     */       int z1;
/*     */       int z2;
/*  98 */       if (xNew > xOld) {
/*  99 */         int x1 = xOld;
/* 100 */         int x2 = xNew;
/* 101 */         int z1 = zOld;
/* 102 */         z2 = zNew;
/*     */       } else {
/* 104 */         x1 = xNew;
/* 105 */         x2 = xOld;
/* 106 */         z1 = zNew;
/* 107 */         z2 = zOld;
/*     */       }
/* 109 */       if ((x1 <= targetX) && (targetX <= x2)) {
/* 110 */         long crossproduct = (targetZ - z1) * (x2 - x1) - (z2 - z1) * (targetX - x1);
/*     */ 
/* 112 */         if (crossproduct == 0L) {
/* 113 */           if ((z1 <= targetZ ? 1 : 0) == (targetZ <= z2 ? 1 : 0)) return true; 
/*     */         }
/* 114 */         else if ((crossproduct < 0L) && (x1 != targetX)) {
/* 115 */           inside = !inside;
/*     */         }
/*     */       }
/* 118 */       xOld = xNew;
/* 119 */       zOld = zNew;
/*     */     }
/*     */ 
/* 122 */     return inside;
/*     */   }
/*     */ 
/*     */   public List<ProtectedRegion> getIntersectingRegions(List<ProtectedRegion> regions) throws UnsupportedIntersectionException
/*     */   {
/* 127 */     List intersectingRegions = new ArrayList();
/*     */ 
/* 129 */     for (ProtectedRegion region : regions) {
/* 130 */       if (intersectsBoundingBox(region))
/*     */       {
/* 132 */         if (((region instanceof ProtectedPolygonalRegion)) || ((region instanceof ProtectedCuboidRegion)))
/*     */         {
/* 135 */           if ((containsAny(region.getPoints())) || (region.containsAny(getPoints())) || (intersectsEdges(region)))
/*     */           {
/* 138 */             intersectingRegions.add(region);
/*     */           }
/*     */         }
/*     */         else
/* 142 */           throw new UnsupportedOperationException("Not supported yet.");
/*     */       }
/*     */     }
/* 145 */     return intersectingRegions;
/*     */   }
/*     */ 
/*     */   public String getTypeName()
/*     */   {
/* 156 */     return "polygon";
/*     */   }
/*     */ 
/*     */   public int volume()
/*     */   {
/* 161 */     int volume = 0;
/*     */ 
/* 196 */     return volume;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion
 * JD-Core Version:    0.6.2
 */