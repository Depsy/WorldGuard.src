/*    */ package com.sk89q.worldguard.util;
/*    */ 
/*    */ public class ArrayReader<T>
/*    */ {
/*    */   private T[] arr;
/*    */ 
/*    */   public ArrayReader(T[] arr)
/*    */   {
/* 26 */     this.arr = arr;
/*    */   }
/*    */ 
/*    */   public T get(int index)
/*    */   {
/* 35 */     return get(index, null);
/*    */   }
/*    */ 
/*    */   public T get(int index, T def)
/*    */   {
/* 45 */     if ((index >= 0) && (this.arr.length > index)) {
/* 46 */       return this.arr[index];
/*    */     }
/* 48 */     return def;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.util.ArrayReader
 * JD-Core Version:    0.6.2
 */