/*     */ package com.sk89q.worldguard.blacklist.loggers;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldedit.blocks.ItemType;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.blacklist.events.BlacklistEvent;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class FileLoggerHandler
/*     */   implements BlacklistLoggerHandler
/*     */ {
/*  51 */   private static Pattern pattern = Pattern.compile("%.");
/*     */ 
/*  55 */   private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss");
/*     */ 
/*  61 */   private int cacheSize = 10;
/*     */   private String pathPattern;
/*     */   private String worldName;
/*  73 */   private TreeMap<String, FileLoggerWriter> writers = new TreeMap();
/*     */   private final Logger logger;
/*     */ 
/*     */   public FileLoggerHandler(String pathPattern, String worldName, Logger logger)
/*     */   {
/*  86 */     this.pathPattern = pathPattern;
/*  87 */     this.worldName = worldName;
/*  88 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   public FileLoggerHandler(String pathPattern, int cacheSize, String worldName, Logger logger)
/*     */   {
/* 100 */     if (cacheSize < 1) {
/* 101 */       throw new IllegalArgumentException("Cache size cannot be less than 1");
/*     */     }
/* 103 */     this.pathPattern = pathPattern;
/* 104 */     this.cacheSize = cacheSize;
/* 105 */     this.worldName = worldName;
/* 106 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   private String buildPath(String playerName)
/*     */   {
/* 116 */     GregorianCalendar calendar = new GregorianCalendar();
/*     */ 
/* 118 */     Matcher m = pattern.matcher(this.pathPattern);
/* 119 */     StringBuffer buffer = new StringBuffer();
/*     */ 
/* 122 */     while (m.find()) {
/* 123 */       String group = m.group();
/* 124 */       String rep = "?";
/*     */ 
/* 126 */       if (group.matches("%%")) {
/* 127 */         rep = "%";
/* 128 */       } else if (group.matches("%u")) {
/* 129 */         rep = playerName.toLowerCase().replaceAll("[^A-Za-z0-9_]", "_");
/* 130 */         if (rep.length() > 32) {
/* 131 */           rep = rep.substring(0, 32);
/*     */         }
/*     */       }
/* 134 */       else if (group.matches("%w")) {
/* 135 */         rep = this.worldName.toLowerCase().replaceAll("[^A-Za-z0-9_]", "_");
/* 136 */         if (rep.length() > 32) {
/* 137 */           rep = rep.substring(0, 32);
/*     */         }
/*     */ 
/*     */       }
/* 141 */       else if (group.matches("%Y")) {
/* 142 */         rep = String.valueOf(calendar.get(1));
/* 143 */       } else if (group.matches("%m")) {
/* 144 */         rep = String.format("%02d", new Object[] { Integer.valueOf(calendar.get(2)) });
/* 145 */       } else if (group.matches("%d")) {
/* 146 */         rep = String.format("%02d", new Object[] { Integer.valueOf(calendar.get(5)) });
/* 147 */       } else if (group.matches("%W")) {
/* 148 */         rep = String.format("%02d", new Object[] { Integer.valueOf(calendar.get(3)) });
/* 149 */       } else if (group.matches("%H")) {
/* 150 */         rep = String.format("%02d", new Object[] { Integer.valueOf(calendar.get(11)) });
/* 151 */       } else if (group.matches("%h")) {
/* 152 */         rep = String.format("%02d", new Object[] { Integer.valueOf(calendar.get(10)) });
/* 153 */       } else if (group.matches("%i")) {
/* 154 */         rep = String.format("%02d", new Object[] { Integer.valueOf(calendar.get(12)) });
/* 155 */       } else if (group.matches("%s")) {
/* 156 */         rep = String.format("%02d", new Object[] { Integer.valueOf(calendar.get(13)) });
/*     */       }
/*     */ 
/* 159 */       m.appendReplacement(buffer, rep);
/*     */     }
/*     */ 
/* 162 */     m.appendTail(buffer);
/*     */ 
/* 164 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   private void log(LocalPlayer player, String message, String comment)
/*     */   {
/* 175 */     String path = buildPath(player.getName());
/*     */     try {
/* 177 */       String date = dateFormat.format(new Date());
/* 178 */       String line = new StringBuilder().append("[").append(date).append("] ").append(player.getName()).append(": ").append(message).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").append("\r\n").toString();
/*     */ 
/* 181 */       FileLoggerWriter writer = (FileLoggerWriter)this.writers.get(path);
/*     */ 
/* 184 */       if (writer != null) {
/*     */         try {
/* 186 */           BufferedWriter out = writer.getWriter();
/* 187 */           out.write(line);
/* 188 */           out.flush();
/* 189 */           writer.updateLastUse();
/* 190 */           return;
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */       }
/*     */ 
/* 197 */       File file = new File(path);
/* 198 */       File parent = file.getParentFile();
/* 199 */       if ((parent != null) && (!parent.exists())) {
/* 200 */         parent.mkdirs();
/*     */       }
/*     */ 
/* 203 */       FileWriter stream = new FileWriter(path, true);
/* 204 */       BufferedWriter out = new BufferedWriter(stream);
/* 205 */       out.write(line);
/* 206 */       out.flush();
/* 207 */       writer = new FileLoggerWriter(path, out);
/* 208 */       this.writers.put(path, writer);
/*     */ 
/* 211 */       if (this.writers.size() > this.cacheSize) {
/* 212 */         Iterator it = this.writers.entrySet().iterator();
/*     */ 
/* 216 */         while (it.hasNext()) {
/* 217 */           Map.Entry entry = (Map.Entry)it.next();
/*     */           try {
/* 219 */             ((FileLoggerWriter)entry.getValue()).getWriter().close();
/*     */           } catch (IOException ignore) {
/*     */           }
/* 222 */           it.remove();
/*     */ 
/* 225 */           if (this.writers.size() <= this.cacheSize)
/*     */             break;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 232 */       this.logger.log(Level.WARNING, new StringBuilder().append("Failed to log blacklist event to '").append(path).append("': ").append(e.getMessage()).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getCoordinates(Vector pos)
/*     */   {
/* 244 */     return new StringBuilder().append("@").append(pos.getBlockX()).append(",").append(pos.getBlockY()).append(",").append(pos.getBlockZ()).toString();
/*     */   }
/*     */ 
/*     */   private void logEvent(BlacklistEvent event, String text, int id, Vector pos, String comment) {
/* 248 */     log(event.getPlayer(), new StringBuilder().append("Tried to ").append(text).append(" ").append(getFriendlyItemName(id)).append(" ").append(getCoordinates(pos)).toString(), comment);
/*     */   }
/*     */ 
/*     */   public void logEvent(BlacklistEvent event, String comment)
/*     */   {
/* 258 */     logEvent(event, event.getDescription(), event.getType(), event.getPosition(), comment);
/*     */   }
/*     */ 
/*     */   private static String getFriendlyItemName(int id)
/*     */   {
/* 268 */     ItemType type = ItemType.fromID(id);
/* 269 */     if (type != null) {
/* 270 */       return new StringBuilder().append(type.getName()).append(" (#").append(id).append(")").toString();
/*     */     }
/* 272 */     return new StringBuilder().append("#").append(id).append("").toString();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 280 */     for (Map.Entry entry : this.writers.entrySet())
/*     */       try {
/* 282 */         ((FileLoggerWriter)entry.getValue()).getWriter().close();
/*     */       }
/*     */       catch (IOException ignore)
/*     */       {
/*     */       }
/* 287 */     this.writers.clear();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.loggers.FileLoggerHandler
 * JD-Core Version:    0.6.2
 */