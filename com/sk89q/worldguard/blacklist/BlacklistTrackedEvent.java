/*    */ package com.sk89q.worldguard.blacklist;
/*    */ 
/*    */ import com.sk89q.worldguard.blacklist.events.BlacklistEvent;
/*    */ 
/*    */ public class BlacklistTrackedEvent
/*    */ {
/*    */   private BlacklistEvent event;
/*    */   private long time;
/*    */ 
/*    */   public BlacklistTrackedEvent(BlacklistEvent event, long time)
/*    */   {
/* 39 */     this.event = event;
/* 40 */     this.time = time;
/*    */   }
/*    */ 
/*    */   public boolean matches(BlacklistEvent other, long now) {
/* 44 */     return (other.getType() == this.event.getType()) && (this.time > now - 3000L) && (other.getClass() == this.event.getClass());
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.BlacklistTrackedEvent
 * JD-Core Version:    0.6.2
 */