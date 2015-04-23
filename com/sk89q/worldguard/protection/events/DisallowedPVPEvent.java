/*    */ package com.sk89q.worldguard.protection.events;
/*    */ 
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.Cancellable;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*    */ 
/*    */ public class DisallowedPVPEvent extends Event
/*    */   implements Cancellable
/*    */ {
/* 35 */   private static final HandlerList handlers = new HandlerList();
/*    */ 
/* 37 */   private boolean cancelled = false;
/*    */   private final Player attacker;
/*    */   private final Player defender;
/*    */   private final EntityDamageByEntityEvent event;
/*    */ 
/*    */   public DisallowedPVPEvent(Player attacker, Player defender, EntityDamageByEntityEvent event)
/*    */   {
/* 43 */     this.attacker = attacker;
/* 44 */     this.defender = defender;
/* 45 */     this.event = event;
/*    */   }
/*    */ 
/*    */   public boolean isCancelled() {
/* 49 */     return this.cancelled;
/*    */   }
/*    */ 
/*    */   public void setCancelled(boolean cancelled) {
/* 53 */     this.cancelled = cancelled;
/*    */   }
/*    */ 
/*    */   public Player getAttacker()
/*    */   {
/* 60 */     return this.attacker;
/*    */   }
/*    */ 
/*    */   public Player getDefender()
/*    */   {
/* 67 */     return this.defender;
/*    */   }
/*    */ 
/*    */   public EntityDamageByEntityEvent getCause() {
/* 71 */     return this.event;
/*    */   }
/*    */ 
/*    */   public HandlerList getHandlers()
/*    */   {
/* 76 */     return handlers;
/*    */   }
/*    */ 
/*    */   public static HandlerList getHandlerList() {
/* 80 */     return handlers;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.events.DisallowedPVPEvent
 * JD-Core Version:    0.6.2
 */