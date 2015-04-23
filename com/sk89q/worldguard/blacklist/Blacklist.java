/*     */ package com.sk89q.worldguard.blacklist;
/*     */ 
/*     */ import com.sk89q.worldedit.blocks.ItemType;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.blacklist.events.BlacklistEvent;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.ChatColor;
/*     */ 
/*     */ public abstract class Blacklist
/*     */ {
/*  48 */   private Map<Integer, List<BlacklistEntry>> blacklist = new HashMap();
/*     */ 
/*  53 */   private BlacklistLogger blacklistLogger = new BlacklistLogger();
/*     */   private BlacklistEvent lastEvent;
/*  61 */   Map<String, BlacklistTrackedEvent> lastAffected = new HashMap();
/*     */   private boolean useAsWhitelist;
/*     */   private final Logger logger;
/*     */ 
/*     */   public Blacklist(Boolean useAsWhitelist, Logger logger)
/*     */   {
/*  69 */     this.useAsWhitelist = useAsWhitelist.booleanValue();
/*  70 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  79 */     return this.blacklist.isEmpty();
/*     */   }
/*     */ 
/*     */   public List<BlacklistEntry> getEntries(int id)
/*     */   {
/*  89 */     return (List)this.blacklist.get(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   public int getItemCount()
/*     */   {
/*  98 */     return this.blacklist.size();
/*     */   }
/*     */ 
/*     */   public boolean isWhitelist()
/*     */   {
/* 107 */     return this.useAsWhitelist;
/*     */   }
/*     */ 
/*     */   public BlacklistLogger getLogger()
/*     */   {
/* 116 */     return this.blacklistLogger;
/*     */   }
/*     */ 
/*     */   public boolean check(BlacklistEvent event, boolean forceRepeat, boolean silent)
/*     */   {
/* 128 */     List entries = getEntries(event.getType());
/* 129 */     if (entries == null) {
/* 130 */       return true;
/*     */     }
/* 132 */     boolean ret = true;
/* 133 */     for (BlacklistEntry entry : entries) {
/* 134 */       if (!entry.check(this.useAsWhitelist, event, forceRepeat, silent)) {
/* 135 */         ret = false;
/*     */       }
/*     */     }
/* 138 */     return ret;
/*     */   }
/*     */ 
/*     */   public void load(File file)
/*     */     throws IOException
/*     */   {
/* 148 */     FileReader input = null;
/* 149 */     Map blacklist = new HashMap();
/*     */     try
/*     */     {
/* 153 */       input = new FileReader(file);
/* 154 */       BufferedReader buff = new BufferedReader(input);
/*     */ 
/* 157 */       List currentEntries = null;
/*     */       String line;
/* 158 */       while ((line = buff.readLine()) != null) {
/* 159 */         line = line.trim();
/*     */ 
/* 162 */         if ((line.length() != 0) && 
/* 164 */           (line.charAt(0) != ';') && (line.charAt(0) != '#'))
/*     */         {
/* 168 */           if (line.matches("^\\[.*\\]$")) {
/* 169 */             String[] items = line.substring(1, line.length() - 1).split(",");
/* 170 */             currentEntries = new ArrayList();
/*     */ 
/* 172 */             for (String item : items)
/*     */             {
/*     */               int id;
/*     */               try {
/* 176 */                 id = Integer.parseInt(item.trim());
/*     */               } catch (NumberFormatException e) {
/* 178 */                 id = getItemID(item.trim());
/* 179 */                 if (id == 0) {
/* 180 */                   this.logger.log(Level.WARNING, new StringBuilder().append("Unknown block name: ").append(item).toString());
/*     */ 
/* 182 */                   break;
/*     */                 }
/*     */               }
/*     */ 
/* 186 */               BlacklistEntry entry = new BlacklistEntry(this);
/* 187 */               if (blacklist.containsKey(Integer.valueOf(id))) {
/* 188 */                 ((List)blacklist.get(Integer.valueOf(id))).add(entry);
/*     */               } else {
/* 190 */                 List entries = new ArrayList();
/* 191 */                 entries.add(entry);
/* 192 */                 blacklist.put(Integer.valueOf(id), entries);
/*     */               }
/* 194 */               currentEntries.add(entry);
/*     */             }
/* 196 */           } else if (currentEntries != null) {
/* 197 */             String[] parts = line.split("=");
/*     */ 
/* 199 */             if (parts.length == 1) {
/* 200 */               this.logger.log(Level.WARNING, new StringBuilder().append("Found option with no value ").append(file.getName()).append(" for '").append(line).append("'").toString());
/*     */             }
/*     */             else
/*     */             {
/* 205 */               boolean unknownOption = false;
/*     */ 
/* 207 */               for (BlacklistEntry entry : currentEntries) {
/* 208 */                 if (parts[0].equalsIgnoreCase("ignore-groups"))
/* 209 */                   entry.setIgnoreGroups(parts[1].split(","));
/* 210 */                 else if (parts[0].equalsIgnoreCase("ignore-perms"))
/* 211 */                   entry.setIgnorePermissions(parts[1].split(","));
/* 212 */                 else if (parts[0].equalsIgnoreCase("on-break"))
/* 213 */                   entry.setBreakActions(parts[1].split(","));
/* 214 */                 else if (parts[0].equalsIgnoreCase("on-destroy-with"))
/* 215 */                   entry.setDestroyWithActions(parts[1].split(","));
/* 216 */                 else if (parts[0].equalsIgnoreCase("on-place"))
/* 217 */                   entry.setPlaceActions(parts[1].split(","));
/* 218 */                 else if (parts[0].equalsIgnoreCase("on-interact"))
/* 219 */                   entry.setInteractActions(parts[1].split(","));
/* 220 */                 else if (parts[0].equalsIgnoreCase("on-use"))
/* 221 */                   entry.setUseActions(parts[1].split(","));
/* 222 */                 else if (parts[0].equalsIgnoreCase("on-drop"))
/* 223 */                   entry.setDropActions(parts[1].split(","));
/* 224 */                 else if (parts[0].equalsIgnoreCase("on-acquire"))
/* 225 */                   entry.setAcquireActions(parts[1].split(","));
/* 226 */                 else if (parts[0].equalsIgnoreCase("message"))
/* 227 */                   entry.setMessage(parts[1].trim());
/* 228 */                 else if (parts[0].equalsIgnoreCase("comment"))
/* 229 */                   entry.setComment(parts[1].trim());
/*     */                 else {
/* 231 */                   unknownOption = true;
/*     */                 }
/*     */               }
/*     */ 
/* 235 */               if (unknownOption)
/* 236 */                 this.logger.log(Level.WARNING, new StringBuilder().append("Unknown option '").append(parts[0]).append("' in ").append(file.getName()).append(" for '").append(line).append("'").toString());
/*     */             }
/*     */           }
/*     */           else {
/* 240 */             this.logger.log(Level.WARNING, new StringBuilder().append("Found option with no heading ").append(file.getName()).append(" for '").append(line).append("'").toString());
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 245 */       this.blacklist = blacklist;
/*     */     } finally {
/*     */       try {
/* 248 */         if (input != null)
/* 249 */           input.close();
/*     */       }
/*     */       catch (IOException ignore)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public BlacklistEvent getLastEvent()
/*     */   {
/* 262 */     return this.lastEvent;
/*     */   }
/*     */ 
/*     */   public void notify(BlacklistEvent event, String comment)
/*     */   {
/* 272 */     this.lastEvent = event;
/*     */ 
/* 274 */     broadcastNotification(new StringBuilder().append(ChatColor.GRAY).append("WG: ").append(ChatColor.LIGHT_PURPLE).append(event.getPlayer().getName()).append(ChatColor.GOLD).append(" (").append(event.getDescription()).append(") ").append(ChatColor.WHITE).append(getFriendlyItemName(event.getType())).append(comment != null ? new StringBuilder().append(" (").append(comment).append(")").toString() : "").append(".").toString());
/*     */   }
/*     */ 
/*     */   public abstract void broadcastNotification(String paramString);
/*     */ 
/*     */   public void forgetPlayer(LocalPlayer player)
/*     */   {
/* 295 */     this.lastAffected.remove(player.getName());
/*     */   }
/*     */ 
/*     */   public void forgetAllPlayers()
/*     */   {
/* 302 */     this.lastAffected.clear();
/*     */   }
/*     */ 
/*     */   private static int getItemID(String name)
/*     */   {
/* 312 */     ItemType type = ItemType.lookup(name);
/* 313 */     if (type != null) {
/* 314 */       return type.getID();
/*     */     }
/* 316 */     return -1;
/*     */   }
/*     */ 
/*     */   private static String getFriendlyItemName(int id)
/*     */   {
/* 327 */     ItemType type = ItemType.fromID(id);
/* 328 */     if (type != null) {
/* 329 */       return new StringBuilder().append(type.getName()).append(" (#").append(id).append(")").toString();
/*     */     }
/* 331 */     return new StringBuilder().append("#").append(id).append("").toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.Blacklist
 * JD-Core Version:    0.6.2
 */