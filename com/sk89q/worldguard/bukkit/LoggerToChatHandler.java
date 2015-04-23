/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import java.util.logging.Handler;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.LogRecord;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class LoggerToChatHandler extends Handler
/*    */ {
/*    */   private CommandSender player;
/*    */ 
/*    */   public LoggerToChatHandler(CommandSender player)
/*    */   {
/* 45 */     this.player = player;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void publish(LogRecord record)
/*    */   {
/* 67 */     this.player.sendMessage(ChatColor.GRAY + record.getLevel().getName() + ": " + ChatColor.WHITE + record.getMessage());
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.LoggerToChatHandler
 * JD-Core Version:    0.6.2
 */