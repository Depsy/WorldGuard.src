/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ public class DistanceResult<T>
/*    */ {
/*    */   private final T t;
/*    */   private final double dist;
/*    */ 
/*    */   public DistanceResult(T t, double dist)
/*    */   {
/* 15 */     this.t = t;
/* 16 */     this.dist = dist;
/*    */   }
/*    */ 
/*    */   public T get()
/*    */   {
/* 23 */     return this.t;
/*    */   }
/*    */ 
/*    */   public double getDistance()
/*    */   {
/* 30 */     return this.dist;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.DistanceResult
 * JD-Core Version:    0.6.2
 */