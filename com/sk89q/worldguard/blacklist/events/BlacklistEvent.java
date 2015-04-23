/*    */ package com.sk89q.worldguard.blacklist.events;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ 
/*    */ public abstract class BlacklistEvent
/*    */ {
/*    */   private Vector pos;
/*    */   private int type;
/*    */   private LocalPlayer player;
/*    */ 
/*    */   public BlacklistEvent(LocalPlayer player, Vector pos, int type)
/*    */   {
/* 47 */     this.player = player;
/* 48 */     this.pos = pos;
/* 49 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public LocalPlayer getPlayer()
/*    */   {
/* 58 */     return this.player;
/*    */   }
/*    */ 
/*    */   public Vector getPosition()
/*    */   {
/* 67 */     return this.pos;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 76 */     return this.type;
/*    */   }
/*    */ 
/*    */   public abstract String getDescription();
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.events.BlacklistEvent
 * JD-Core Version:    0.6.2
 */