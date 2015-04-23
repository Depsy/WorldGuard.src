/*    */ package com.sk89q.worldguard.protection.databases;
/*    */ 
/*    */ import com.sk89q.worldguard.util.FatalConfigurationLoadingException;
/*    */ 
/*    */ public class InvalidTableFormatException extends FatalConfigurationLoadingException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected String updateFile;
/*    */ 
/*    */   public InvalidTableFormatException(String updateFile)
/*    */   {
/* 13 */     this.updateFile = updateFile;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 17 */     return "You need to update your database to the latest version.\n\t\tPlease see " + this.updateFile;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.InvalidTableFormatException
 * JD-Core Version:    0.6.2
 */