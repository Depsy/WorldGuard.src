/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ class Circle<T>
/*    */ {
/*    */   private final List<T> data;
/*    */   private int currentPos;
/*    */ 
/*    */   public Circle(int size)
/*    */   {
/* 15 */     this.data = new ArrayList(size);
/*    */   }
/*    */ 
/*    */   public void add(T t) {
/* 19 */     this.data.add(t);
/*    */   }
/*    */ 
/*    */   public T get(int pos) {
/* 23 */     pos %= this.data.size();
/* 24 */     return this.data.get(pos);
/*    */   }
/*    */ 
/*    */   public int getNumElements() {
/* 28 */     return this.data.size();
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 32 */     this.currentPos = 0;
/*    */   }
/*    */ 
/*    */   public T getNext() {
/* 36 */     Object ret = this.data.get(this.currentPos++);
/* 37 */     this.currentPos %= this.data.size();
/* 38 */     return ret;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.Circle
 * JD-Core Version:    0.6.2
 */