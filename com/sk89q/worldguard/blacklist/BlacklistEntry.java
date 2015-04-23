/*     */ package com.sk89q.worldguard.blacklist;
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
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.bukkit.ChatColor;
/*     */ 
/*     */ public class BlacklistEntry
/*     */ {
/*     */   private Blacklist blacklist;
/*     */   private Set<String> ignoreGroups;
/*     */   private Set<String> ignorePermissions;
/*     */   private String[] breakActions;
/*     */   private String[] destroyWithActions;
/*     */   private String[] placeActions;
/*     */   private String[] interactActions;
/*     */   private String[] useActions;
/*     */   private String[] dropActions;
/*     */   private String[] acquireActions;
/*     */   private String message;
/*     */   private String comment;
/*     */ 
/*     */   public BlacklistEntry(Blacklist blacklist)
/*     */   {
/*  76 */     this.blacklist = blacklist;
/*     */   }
/*     */ 
/*     */   public String[] getIgnoreGroups()
/*     */   {
/*  83 */     return (String[])this.ignoreGroups.toArray(new String[this.ignoreGroups.size()]);
/*     */   }
/*     */ 
/*     */   public String[] getIgnorePermissions()
/*     */   {
/*  90 */     return (String[])this.ignorePermissions.toArray(new String[this.ignorePermissions.size()]);
/*     */   }
/*     */ 
/*     */   public void setIgnoreGroups(String[] ignoreGroups)
/*     */   {
/*  97 */     Set ignoreGroupsSet = new HashSet();
/*  98 */     for (String group : ignoreGroups) {
/*  99 */       ignoreGroupsSet.add(group.toLowerCase());
/*     */     }
/* 101 */     this.ignoreGroups = ignoreGroupsSet;
/*     */   }
/*     */ 
/*     */   public void setIgnorePermissions(String[] ignorePermissions)
/*     */   {
/* 108 */     Set ignorePermissionsSet = new HashSet();
/* 109 */     Collections.addAll(ignorePermissionsSet, ignorePermissions);
/* 110 */     this.ignorePermissions = ignorePermissionsSet;
/*     */   }
/*     */ 
/*     */   public String[] getBreakActions()
/*     */   {
/* 117 */     return this.breakActions;
/*     */   }
/*     */ 
/*     */   public void setBreakActions(String[] actions)
/*     */   {
/* 124 */     this.breakActions = actions;
/*     */   }
/*     */ 
/*     */   public String[] getDestroyWithActions()
/*     */   {
/* 131 */     return this.destroyWithActions;
/*     */   }
/*     */ 
/*     */   public void setDestroyWithActions(String[] actions)
/*     */   {
/* 138 */     this.destroyWithActions = actions;
/*     */   }
/*     */ 
/*     */   public String[] getPlaceActions()
/*     */   {
/* 145 */     return this.placeActions;
/*     */   }
/*     */ 
/*     */   public void setPlaceActions(String[] actions)
/*     */   {
/* 152 */     this.placeActions = actions;
/*     */   }
/*     */ 
/*     */   public String[] getInteractActions()
/*     */   {
/* 159 */     return this.interactActions;
/*     */   }
/*     */ 
/*     */   public void setInteractActions(String[] actions)
/*     */   {
/* 166 */     this.interactActions = actions;
/*     */   }
/*     */ 
/*     */   public String[] getUseActions()
/*     */   {
/* 173 */     return this.useActions;
/*     */   }
/*     */ 
/*     */   public void setUseActions(String[] actions)
/*     */   {
/* 180 */     this.useActions = actions;
/*     */   }
/*     */ 
/*     */   public String[] getDropActions()
/*     */   {
/* 187 */     return this.dropActions;
/*     */   }
/*     */ 
/*     */   public void setDropActions(String[] actions)
/*     */   {
/* 194 */     this.dropActions = actions;
/*     */   }
/*     */ 
/*     */   public String[] getAcquireActions()
/*     */   {
/* 201 */     return this.acquireActions;
/*     */   }
/*     */ 
/*     */   public void setAcquireActions(String[] actions)
/*     */   {
/* 208 */     this.acquireActions = actions;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 215 */     return this.message;
/*     */   }
/*     */ 
/*     */   public void setMessage(String message)
/*     */   {
/* 222 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getComment()
/*     */   {
/* 229 */     return this.comment;
/*     */   }
/*     */ 
/*     */   public void setComment(String comment)
/*     */   {
/* 236 */     this.comment = comment;
/*     */   }
/*     */ 
/*     */   public boolean shouldIgnore(LocalPlayer player)
/*     */   {
/* 246 */     if (this.ignoreGroups != null) {
/* 247 */       for (String group : player.getGroups()) {
/* 248 */         if (this.ignoreGroups.contains(group.toLowerCase())) {
/* 249 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 254 */     if (this.ignorePermissions != null) {
/* 255 */       for (String perm : this.ignorePermissions) {
/* 256 */         if (player.hasPermission(perm)) {
/* 257 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 262 */     return false;
/*     */   }
/*     */ 
/*     */   private String[] getActions(BlacklistEvent event)
/*     */   {
/* 272 */     if ((event instanceof BlockBreakBlacklistEvent)) {
/* 273 */       return this.breakActions;
/*     */     }
/* 275 */     if ((event instanceof BlockPlaceBlacklistEvent)) {
/* 276 */       return this.placeActions;
/*     */     }
/* 278 */     if ((event instanceof BlockInteractBlacklistEvent)) {
/* 279 */       return this.interactActions;
/*     */     }
/* 281 */     if ((event instanceof DestroyWithBlacklistEvent)) {
/* 282 */       return this.destroyWithActions;
/*     */     }
/* 284 */     if ((event instanceof ItemAcquireBlacklistEvent)) {
/* 285 */       return this.acquireActions;
/*     */     }
/* 287 */     if ((event instanceof ItemDropBlacklistEvent)) {
/* 288 */       return this.dropActions;
/*     */     }
/* 290 */     if ((event instanceof ItemUseBlacklistEvent)) {
/* 291 */       return this.useActions;
/*     */     }
/*     */ 
/* 294 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean check(boolean useAsWhitelist, BlacklistEvent event, boolean forceRepeat, boolean silent)
/*     */   {
/* 308 */     LocalPlayer player = event.getPlayer();
/*     */ 
/* 310 */     if (shouldIgnore(player)) {
/* 311 */       return true;
/*     */     }
/*     */ 
/* 314 */     String name = player.getName();
/* 315 */     long now = System.currentTimeMillis();
/* 316 */     boolean repeating = false;
/*     */ 
/* 319 */     BlacklistTrackedEvent tracked = (BlacklistTrackedEvent)this.blacklist.lastAffected.get(name);
/* 320 */     if (tracked != null) {
/* 321 */       if (tracked.matches(event, now))
/* 322 */         repeating = true;
/*     */     }
/*     */     else {
/* 325 */       this.blacklist.lastAffected.put(name, new BlacklistTrackedEvent(event, now));
/*     */     }
/*     */ 
/* 328 */     String[] actions = getActions(event);
/*     */ 
/* 331 */     boolean ret = !useAsWhitelist;
/*     */ 
/* 334 */     if (actions == null) {
/* 335 */       return ret;
/*     */     }
/*     */ 
/* 338 */     for (String action : actions)
/*     */     {
/* 340 */       if (action.equalsIgnoreCase("deny")) {
/* 341 */         if (silent) {
/* 342 */           return false;
/*     */         }
/*     */ 
/* 345 */         ret = false;
/*     */       }
/* 348 */       else if (action.equalsIgnoreCase("allow")) {
/* 349 */         if (silent) {
/* 350 */           return true;
/*     */         }
/*     */ 
/* 353 */         ret = true;
/*     */       }
/* 356 */       else if (action.equalsIgnoreCase("kick")) {
/* 357 */         if (!silent)
/*     */         {
/* 361 */           if (this.message != null) {
/* 362 */             player.kick(String.format(this.message, new Object[] { getFriendlyItemName(event.getType()) }));
/*     */           }
/*     */           else {
/* 365 */             player.kick("You can't " + event.getDescription() + " " + getFriendlyItemName(event.getType()));
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/* 370 */       else if (action.equalsIgnoreCase("ban")) {
/* 371 */         if (!silent)
/*     */         {
/* 375 */           if (this.message != null) {
/* 376 */             player.ban("Banned: " + String.format(this.message, new Object[] { getFriendlyItemName(event.getType()) }));
/*     */           }
/*     */           else {
/* 379 */             player.ban("Banned: You can't " + event.getDescription() + " " + getFriendlyItemName(event.getType()));
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/* 384 */       else if ((!silent) && ((!repeating) || (forceRepeat)))
/*     */       {
/* 386 */         if (action.equalsIgnoreCase("notify")) {
/* 387 */           this.blacklist.notify(event, this.comment);
/*     */         }
/* 390 */         else if (action.equalsIgnoreCase("log")) {
/* 391 */           this.blacklist.getLogger().logEvent(event, this.comment);
/*     */         }
/* 394 */         else if (action.equalsIgnoreCase("tell")) {
/* 395 */           if (this.message != null) {
/* 396 */             player.printRaw(ChatColor.YELLOW + String.format(this.message, new Object[] { getFriendlyItemName(event.getType()) }) + ".");
/*     */           }
/*     */           else
/*     */           {
/* 400 */             player.printRaw(ChatColor.YELLOW + "You're not allowed to " + event.getDescription() + " " + getFriendlyItemName(event.getType()) + ".");
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 408 */     return ret;
/*     */   }
/*     */ 
/*     */   private static String getFriendlyItemName(int id)
/*     */   {
/* 417 */     ItemType type = ItemType.fromID(id);
/* 418 */     if (type != null) {
/* 419 */       return type.getName() + " (#" + id + ")";
/*     */     }
/* 421 */     return "#" + id + "";
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.BlacklistEntry
 * JD-Core Version:    0.6.2
 */