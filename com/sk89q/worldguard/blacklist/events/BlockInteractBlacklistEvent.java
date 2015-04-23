/*    */ package com.sk89q.worldguard.blacklist.events;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ 
/*    */ public class BlockInteractBlacklistEvent extends BlockBlacklistEvent
/*    */ {
/*    */   public BlockInteractBlacklistEvent(LocalPlayer player, Vector pos, int type)
/*    */   {
/* 27 */     super(player, pos, type);
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 32 */     return "interact with";
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.events.BlockInteractBlacklistEvent
 * JD-Core Version:    0.6.2
 */