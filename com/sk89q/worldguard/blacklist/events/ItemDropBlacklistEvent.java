/*    */ package com.sk89q.worldguard.blacklist.events;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ 
/*    */ public class ItemDropBlacklistEvent extends ItemBlacklistEvent
/*    */ {
/*    */   public ItemDropBlacklistEvent(LocalPlayer player, Vector pos, int type)
/*    */   {
/* 27 */     super(player, pos, type);
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 32 */     return "drop";
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.events.ItemDropBlacklistEvent
 * JD-Core Version:    0.6.2
 */