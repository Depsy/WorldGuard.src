/*    */ package com.sk89q.worldguard.protection.databases.migrators;
/*    */ 
/*    */ public class MigrationException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public MigrationException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MigrationException(String message)
/*    */   {
/* 30 */     super(message);
/*    */   }
/*    */ 
/*    */   public MigrationException(String message, Throwable cause) {
/* 34 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public MigrationException(Throwable cause) {
/* 38 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.migrators.MigrationException
 * JD-Core Version:    0.6.2
 */