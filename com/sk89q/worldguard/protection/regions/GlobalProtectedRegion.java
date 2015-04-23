/*    */ package com.sk89q.worldguard.protection.regions;
/*    */ 
/*    */ import com.sk89q.worldedit.BlockVector;
/*    */ import com.sk89q.worldedit.BlockVector2D;
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class GlobalProtectedRegion extends ProtectedRegion
/*    */ {
/*    */   public GlobalProtectedRegion(String id)
/*    */   {
/* 33 */     super(id);
/* 34 */     this.min = new BlockVector(0, 0, 0);
/* 35 */     this.max = new BlockVector(0, 0, 0);
/*    */   }
/*    */ 
/*    */   public List<BlockVector2D> getPoints() {
/* 39 */     List pts = new ArrayList();
/* 40 */     pts.add(new BlockVector2D(this.min.getBlockX(), this.min.getBlockZ()));
/* 41 */     return pts;
/*    */   }
/*    */ 
/*    */   public int volume()
/*    */   {
/* 46 */     return 0;
/*    */   }
/*    */ 
/*    */   public boolean contains(Vector pt)
/*    */   {
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   public String getTypeName()
/*    */   {
/* 56 */     return "global";
/*    */   }
/*    */ 
/*    */   public List<ProtectedRegion> getIntersectingRegions(List<ProtectedRegion> regions)
/*    */     throws UnsupportedIntersectionException
/*    */   {
/* 63 */     return new ArrayList();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.regions.GlobalProtectedRegion
 * JD-Core Version:    0.6.2
 */