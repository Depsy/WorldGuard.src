/*    */ package com.sk89q.worldguard.blacklist;
/*    */ 
/*    */ import com.sk89q.worldguard.blacklist.events.BlacklistEvent;
/*    */ import com.sk89q.worldguard.blacklist.loggers.BlacklistLoggerHandler;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class BlacklistLogger
/*    */   implements BlacklistLoggerHandler
/*    */ {
/* 36 */   private Set<BlacklistLoggerHandler> handlers = new HashSet();
/*    */ 
/*    */   public void addHandler(BlacklistLoggerHandler handler)
/*    */   {
/* 45 */     this.handlers.add(handler);
/*    */   }
/*    */ 
/*    */   public void removeHandler(BlacklistLoggerHandler handler)
/*    */   {
/* 54 */     this.handlers.remove(handler);
/*    */   }
/*    */ 
/*    */   public void clearHandlers()
/*    */   {
/* 61 */     this.handlers.clear();
/*    */   }
/*    */ 
/*    */   public void logEvent(BlacklistEvent event, String comment)
/*    */   {
/* 70 */     for (BlacklistLoggerHandler handler : this.handlers)
/* 71 */       handler.logEvent(event, comment);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 79 */     for (BlacklistLoggerHandler handler : this.handlers)
/* 80 */       handler.close();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.BlacklistLogger
 * JD-Core Version:    0.6.2
 */