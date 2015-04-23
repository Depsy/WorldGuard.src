/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ public class SimplePointND
/*    */   implements PointND
/*    */ {
/*    */   private final double[] ords;
/*    */ 
/*    */   public SimplePointND(double[] ords)
/*    */   {
/* 12 */     this.ords = ords;
/*    */   }
/*    */ 
/*    */   public int getDimensions() {
/* 16 */     return this.ords.length;
/*    */   }
/*    */ 
/*    */   public double getOrd(int axis) {
/* 20 */     return this.ords[axis];
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.SimplePointND
 * JD-Core Version:    0.6.2
 */