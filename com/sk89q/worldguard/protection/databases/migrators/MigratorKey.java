/*    */ package com.sk89q.worldguard.protection.databases.migrators;
/*    */ 
/*    */ public class MigratorKey
/*    */ {
/*    */   public final String from;
/*    */   public final String to;
/*    */ 
/*    */   public MigratorKey(String from, String to)
/*    */   {
/* 27 */     this.from = from;
/* 28 */     this.to = to;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o) {
/* 32 */     MigratorKey other = (MigratorKey)o;
/*    */ 
/* 34 */     return (other.from.equals(this.from)) && (other.to.equals(this.to));
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 38 */     int hash = 17;
/* 39 */     hash = hash * 31 + this.from.hashCode();
/* 40 */     hash = hash * 31 + this.to.hashCode();
/* 41 */     return hash;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.migrators.MigratorKey
 * JD-Core Version:    0.6.2
 */