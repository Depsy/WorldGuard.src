/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ public class MinDist2D
/*    */ {
/*    */   public static double get(double minx, double miny, double maxx, double maxy, double x, double y)
/*    */   {
/* 24 */     double rx = r(x, minx, maxx);
/* 25 */     double ry = r(y, miny, maxy);
/* 26 */     double xd = x - rx;
/* 27 */     double yd = y - ry;
/* 28 */     return xd * xd + yd * yd;
/*    */   }
/*    */ 
/*    */   private static double r(double x, double min, double max) {
/* 32 */     double r = x;
/* 33 */     if (x < min)
/* 34 */       r = min;
/* 35 */     if (x > max)
/* 36 */       r = max;
/* 37 */     return r;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.MinDist2D
 * JD-Core Version:    0.6.2
 */