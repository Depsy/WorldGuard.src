/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ public class SimpleMBR2D
/*    */   implements MBR2D
/*    */ {
/*    */   private final double xmin;
/*    */   private final double ymin;
/*    */   private final double xmax;
/*    */   private final double ymax;
/*    */ 
/*    */   public SimpleMBR2D(double xmin, double ymin, double xmax, double ymax)
/*    */   {
/* 22 */     this.xmin = xmin;
/* 23 */     this.ymin = ymin;
/* 24 */     this.xmax = xmax;
/* 25 */     this.ymax = ymax;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 31 */     return getClass().getSimpleName() + "{xmin: " + this.xmin + ", ymin: " + this.ymin + ", xmax: " + this.xmax + ", ymax: " + this.ymax + "}";
/*    */   }
/*    */ 
/*    */   public double getMinX()
/*    */   {
/* 37 */     return this.xmin;
/*    */   }
/*    */ 
/*    */   public double getMinY() {
/* 41 */     return this.ymin;
/*    */   }
/*    */ 
/*    */   public double getMaxX() {
/* 45 */     return this.xmax;
/*    */   }
/*    */ 
/*    */   public double getMaxY() {
/* 49 */     return this.ymax;
/*    */   }
/*    */ 
/*    */   public MBR2D union(MBR2D other) {
/* 53 */     double uxmin = Math.min(this.xmin, other.getMinX());
/* 54 */     double uymin = Math.min(this.ymin, other.getMinY());
/* 55 */     double uxmax = Math.max(this.xmax, other.getMaxX());
/* 56 */     double uymax = Math.max(this.ymax, other.getMaxY());
/* 57 */     return new SimpleMBR2D(uxmin, uymin, uxmax, uymax);
/*    */   }
/*    */ 
/*    */   public boolean intersects(MBR2D other) {
/* 61 */     return (other.getMaxX() >= this.xmin) && (other.getMinX() <= this.xmax) && (other.getMaxY() >= this.ymin) && (other.getMinY() <= this.ymax);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.SimpleMBR2D
 * JD-Core Version:    0.6.2
 */