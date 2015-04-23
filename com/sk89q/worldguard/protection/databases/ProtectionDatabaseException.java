/*    */ package com.sk89q.worldguard.protection.databases;
/*    */ 
/*    */ public class ProtectionDatabaseException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ProtectionDatabaseException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ProtectionDatabaseException(String message)
/*    */   {
/* 32 */     super(message);
/*    */   }
/*    */ 
/*    */   public ProtectionDatabaseException(String message, Throwable cause) {
/* 36 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public ProtectionDatabaseException(Throwable cause) {
/* 40 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.ProtectionDatabaseException
 * JD-Core Version:    0.6.2
 */