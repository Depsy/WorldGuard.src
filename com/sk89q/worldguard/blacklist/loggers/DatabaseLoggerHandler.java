/*     */ package com.sk89q.worldguard.blacklist.loggers;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.blacklist.events.BlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockBreakBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockInteractBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockPlaceBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.DestroyWithBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.ItemAcquireBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.ItemDropBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.ItemUseBlacklistEvent;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class DatabaseLoggerHandler
/*     */   implements BlacklistLoggerHandler
/*     */ {
/*     */   private final String dsn;
/*     */   private final String user;
/*     */   private final String pass;
/*     */   private final String table;
/*     */   private final String worldName;
/*     */   private Connection conn;
/*     */   private final Logger logger;
/*     */ 
/*     */   public DatabaseLoggerHandler(String dsn, String user, String pass, String table, String worldName, Logger logger)
/*     */   {
/*  84 */     this.dsn = dsn;
/*  85 */     this.user = user;
/*  86 */     this.pass = pass;
/*  87 */     this.table = table;
/*  88 */     this.worldName = worldName;
/*  89 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   private Connection getConnection()
/*     */     throws SQLException
/*     */   {
/*  99 */     if ((this.conn == null) || (this.conn.isClosed())) {
/* 100 */       this.conn = DriverManager.getConnection(this.dsn, this.user, this.pass);
/*     */     }
/* 102 */     return this.conn;
/*     */   }
/*     */ 
/*     */   private void logEvent(String event, LocalPlayer player, Vector pos, int item, String comment)
/*     */   {
/*     */     try
/*     */     {
/* 117 */       Connection conn = getConnection();
/* 118 */       PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + this.table + "(event, world, player, x, y, z, item, time, comment) VALUES " + "(?, ?, ?, ?, ?, ?, ?, ?, ?)");
/*     */ 
/* 122 */       stmt.setString(1, event);
/* 123 */       stmt.setString(2, this.worldName);
/* 124 */       stmt.setString(3, player.getName());
/* 125 */       stmt.setInt(4, pos.getBlockX());
/* 126 */       stmt.setInt(5, pos.getBlockY());
/* 127 */       stmt.setInt(6, pos.getBlockZ());
/* 128 */       stmt.setInt(7, item);
/* 129 */       stmt.setInt(8, (int)(System.currentTimeMillis() / 1000L));
/* 130 */       stmt.setString(9, comment);
/* 131 */       stmt.executeUpdate();
/*     */     } catch (SQLException e) {
/* 133 */       this.logger.log(Level.SEVERE, "Failed to log blacklist event to database: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void logEvent(BlacklistEvent event, String comment)
/*     */   {
/* 145 */     if ((event instanceof BlockBreakBlacklistEvent)) {
/* 146 */       BlockBreakBlacklistEvent evt = (BlockBreakBlacklistEvent)event;
/* 147 */       logEvent("BREAK", evt.getPlayer(), evt.getPosition(), evt.getType(), comment);
/*     */     }
/* 151 */     else if ((event instanceof BlockPlaceBlacklistEvent)) {
/* 152 */       BlockPlaceBlacklistEvent evt = (BlockPlaceBlacklistEvent)event;
/* 153 */       logEvent("PLACE", evt.getPlayer(), evt.getPosition(), evt.getType(), comment);
/*     */     }
/* 157 */     else if ((event instanceof BlockInteractBlacklistEvent)) {
/* 158 */       BlockInteractBlacklistEvent evt = (BlockInteractBlacklistEvent)event;
/* 159 */       logEvent("INTERACT", evt.getPlayer(), evt.getPosition(), evt.getType(), comment);
/*     */     }
/* 163 */     else if ((event instanceof DestroyWithBlacklistEvent)) {
/* 164 */       DestroyWithBlacklistEvent evt = (DestroyWithBlacklistEvent)event;
/* 165 */       logEvent("DESTROY_WITH", evt.getPlayer(), evt.getPosition(), evt.getType(), comment);
/*     */     }
/* 169 */     else if ((event instanceof ItemAcquireBlacklistEvent)) {
/* 170 */       ItemAcquireBlacklistEvent evt = (ItemAcquireBlacklistEvent)event;
/* 171 */       logEvent("ACQUIRE", evt.getPlayer(), evt.getPlayer().getPosition(), evt.getType(), comment);
/*     */     }
/* 175 */     else if ((event instanceof ItemDropBlacklistEvent)) {
/* 176 */       ItemDropBlacklistEvent evt = (ItemDropBlacklistEvent)event;
/* 177 */       logEvent("DROP", evt.getPlayer(), evt.getPlayer().getPosition(), evt.getType(), comment);
/*     */     }
/* 181 */     else if ((event instanceof ItemUseBlacklistEvent)) {
/* 182 */       ItemUseBlacklistEvent evt = (ItemUseBlacklistEvent)event;
/* 183 */       logEvent("USE", evt.getPlayer(), evt.getPlayer().getPosition(), evt.getType(), comment);
/*     */     }
/*     */     else
/*     */     {
/* 188 */       logEvent("UNKNOWN", event.getPlayer(), event.getPlayer().getPosition(), -1, comment);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 198 */       if ((this.conn != null) && (!this.conn.isClosed()))
/* 199 */         this.conn.close();
/*     */     }
/*     */     catch (SQLException ignore)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.loggers.DatabaseLoggerHandler
 * JD-Core Version:    0.6.2
 */