/*    */ package com.sk89q.worldguard.blacklist.events;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ 
/*    */ public class ItemUseBlacklistEvent extends ItemBlacklistEvent
/*    */ {
/*    */   public ItemUseBlacklistEvent(LocalPlayer player, Vector pos, int type)
/*    */   {
/* 27 */     super(player, pos, type);
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 32 */     return "use";
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.events.ItemUseBlacklistEvent
 * JD-Core Version:    0.6.2
 */