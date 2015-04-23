/*     */ package com.sk89q.worldguard.protection.regions;
/*     */ 
/*     */ import com.sk89q.worldedit.BlockVector;
/*     */ import com.sk89q.worldedit.BlockVector2D;
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ProtectedCuboidRegion extends ProtectedRegion
/*     */ {
/*     */   public ProtectedCuboidRegion(String id, BlockVector pt1, BlockVector pt2)
/*     */   {
/*  44 */     super(id);
/*  45 */     setMinMaxPoints(pt1, pt2);
/*     */   }
/*     */ 
/*     */   private void setMinMaxPoints(BlockVector pt1, BlockVector pt2)
/*     */   {
/*  55 */     List points = new ArrayList();
/*  56 */     points.add(pt1);
/*  57 */     points.add(pt2);
/*  58 */     setMinMaxPoints(points);
/*     */   }
/*     */ 
/*     */   public void setMinimumPoint(BlockVector pt)
/*     */   {
/*  67 */     setMinMaxPoints(pt, this.max);
/*     */   }
/*     */ 
/*     */   public void setMaximumPoint(BlockVector pt)
/*     */   {
/*  76 */     setMinMaxPoints(this.min, pt);
/*     */   }
/*     */ 
/*     */   public List<BlockVector2D> getPoints() {
/*  80 */     List pts = new ArrayList();
/*  81 */     int x1 = this.min.getBlockX();
/*  82 */     int x2 = this.max.getBlockX();
/*  83 */     int z1 = this.min.getBlockZ();
/*  84 */     int z2 = this.max.getBlockZ();
/*     */ 
/*  86 */     pts.add(new BlockVector2D(x1, z1));
/*  87 */     pts.add(new BlockVector2D(x2, z1));
/*  88 */     pts.add(new BlockVector2D(x1, z2));
/*  89 */     pts.add(new BlockVector2D(x2, z2));
/*     */ 
/*  91 */     return pts;
/*     */   }
/*     */ 
/*     */   public boolean contains(Vector pt)
/*     */   {
/*  96 */     double x = pt.getX();
/*  97 */     double y = pt.getY();
/*  98 */     double z = pt.getZ();
/*  99 */     return (x >= this.min.getBlockX()) && (x < this.max.getBlockX() + 1) && (y >= this.min.getBlockY()) && (y < this.max.getBlockY() + 1) && (z >= this.min.getBlockZ()) && (z < this.max.getBlockZ() + 1);
/*     */   }
/*     */ 
/*     */   public List<ProtectedRegion> getIntersectingRegions(List<ProtectedRegion> regions)
/*     */     throws UnsupportedIntersectionException
/*     */   {
/* 132 */     List intersectingRegions = new ArrayList();
/*     */ 
/* 134 */     for (ProtectedRegion region : regions) {
/* 135 */       if (intersectsBoundingBox(region))
/*     */       {
/* 138 */         if ((region instanceof ProtectedCuboidRegion)) {
/* 139 */           intersectingRegions.add(region);
/*     */         }
/* 141 */         else if ((region instanceof ProtectedPolygonalRegion))
/*     */         {
/* 144 */           if ((containsAny(region.getPoints())) || (region.containsAny(getPoints())) || (intersectsEdges(region)))
/*     */           {
/* 147 */             intersectingRegions.add(region);
/*     */           }
/*     */         }
/*     */         else
/* 151 */           throw new UnsupportedOperationException("Not supported yet.");
/*     */       }
/*     */     }
/* 154 */     return intersectingRegions;
/*     */   }
/*     */ 
/*     */   public String getTypeName()
/*     */   {
/* 159 */     return "cuboid";
/*     */   }
/*     */ 
/*     */   public int volume()
/*     */   {
/* 164 */     int xLength = this.max.getBlockX() - this.min.getBlockX() + 1;
/* 165 */     int yLength = this.max.getBlockY() - this.min.getBlockY() + 1;
/* 166 */     int zLength = this.max.getBlockZ() - this.min.getBlockZ() + 1;
/*     */ 
/* 168 */     return xLength * yLength * zLength;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
 * JD-Core Version:    0.6.2
 */