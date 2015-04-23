/*    */ package com.sk89q.worldguard.blacklist.events;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ 
/*    */ public abstract class BlockBlacklistEvent extends BlacklistEvent
/*    */ {
/*    */   public BlockBlacklistEvent(LocalPlayer player, Vector pos, int type)
/*    */   {
/* 27 */     super(player, pos, type);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.events.BlockBlacklistEvent
 * JD-Core Version:    0.6.2
 */