/*    */ package com.sk89q.worldguard.blacklist.loggers;
/*    */ 
/*    */ import java.io.BufferedWriter;
/*    */ 
/*    */ public class FileLoggerWriter
/*    */   implements Comparable<FileLoggerWriter>
/*    */ {
/*    */   public String path;
/*    */   private BufferedWriter writer;
/*    */   private long lastUse;
/*    */ 
/*    */   public FileLoggerWriter(String path, BufferedWriter writer)
/*    */   {
/* 49 */     this.path = path;
/* 50 */     this.writer = writer;
/* 51 */     this.lastUse = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public String getPath()
/*    */   {
/* 60 */     return this.path;
/*    */   }
/*    */ 
/*    */   public BufferedWriter getWriter()
/*    */   {
/* 67 */     return this.writer;
/*    */   }
/*    */ 
/*    */   public long getLastUse()
/*    */   {
/* 74 */     return this.lastUse;
/*    */   }
/*    */ 
/*    */   public void updateLastUse()
/*    */   {
/* 81 */     this.lastUse = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public int compareTo(FileLoggerWriter other) {
/* 85 */     if (this.lastUse > other.lastUse)
/* 86 */       return 1;
/* 87 */     if (this.lastUse < other.lastUse) {
/* 88 */       return -1;
/*    */     }
/* 90 */     return 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.loggers.FileLoggerWriter
 * JD-Core Version:    0.6.2
 */