/*     */ package com.sk89q.worldguard.blacklist.loggers;
/*     */ 
/*     */ import com.sk89q.worldedit.blocks.ItemType;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.blacklist.events.BlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockBreakBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockInteractBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockPlaceBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.DestroyWithBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.ItemAcquireBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.ItemDropBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.ItemUseBlacklistEvent;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class ConsoleLoggerHandler
/*     */   implements BlacklistLoggerHandler
/*     */ {
/*     */   private String worldName;
/*     */   private final Logger logger;
/*     */ 
/*     */   public ConsoleLoggerHandler(String worldName, Logger logger)
/*     */   {
/*  47 */     this.worldName = worldName;
/*  48 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   public void logEvent(BlacklistEvent event, String comment)
/*     */   {
/*  53 */     if ((event instanceof BlockBreakBlacklistEvent)) {
/*  54 */       BlockBreakBlacklistEvent evt = (BlockBreakBlacklistEvent)event;
/*  55 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" tried to break ").append(getFriendlyItemName(evt.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").toString());
/*     */     }
/*  60 */     else if ((event instanceof BlockPlaceBlacklistEvent)) {
/*  61 */       BlockPlaceBlacklistEvent evt = (BlockPlaceBlacklistEvent)event;
/*  62 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" tried to place ").append(getFriendlyItemName(evt.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").toString());
/*     */     }
/*  67 */     else if ((event instanceof BlockInteractBlacklistEvent)) {
/*  68 */       BlockInteractBlacklistEvent evt = (BlockInteractBlacklistEvent)event;
/*  69 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" tried to interact with ").append(getFriendlyItemName(evt.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").toString());
/*     */     }
/*  74 */     else if ((event instanceof DestroyWithBlacklistEvent)) {
/*  75 */       DestroyWithBlacklistEvent evt = (DestroyWithBlacklistEvent)event;
/*  76 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" tried to destroy with ").append(getFriendlyItemName(evt.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").toString());
/*     */     }
/*  81 */     else if ((event instanceof ItemAcquireBlacklistEvent)) {
/*  82 */       ItemAcquireBlacklistEvent evt = (ItemAcquireBlacklistEvent)event;
/*  83 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" tried to acquire ").append(getFriendlyItemName(evt.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").toString());
/*     */     }
/*  88 */     else if ((event instanceof ItemDropBlacklistEvent)) {
/*  89 */       ItemDropBlacklistEvent evt = (ItemDropBlacklistEvent)event;
/*  90 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" tried to drop ").append(getFriendlyItemName(evt.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").toString());
/*     */     }
/*  95 */     else if ((event instanceof ItemUseBlacklistEvent)) {
/*  96 */       ItemUseBlacklistEvent evt = (ItemUseBlacklistEvent)event;
/*  97 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" tried to use ").append(getFriendlyItemName(evt.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").toString());
/*     */     }
/*     */     else
/*     */     {
/* 103 */       this.logger.log(Level.INFO, new StringBuilder().append("[").append(this.worldName).append("] ").append(event.getPlayer().getName()).append(" caught unknown event: ").append(event.getClass().getCanonicalName()).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getFriendlyItemName(int id)
/*     */   {
/* 115 */     ItemType type = ItemType.fromID(id);
/* 116 */     if (type != null) {
/* 117 */       return new StringBuilder().append(type.getName()).append(" (#").append(id).append(")").toString();
/*     */     }
/* 119 */     return new StringBuilder().append("#").append(id).toString();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.loggers.ConsoleLoggerHandler
 * JD-Core Version:    0.6.2
 */