/*     */ package com.sk89q.worldguard.domains;
/*     */ 
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class DefaultDomain
/*     */   implements Domain
/*     */ {
/*     */   private final Set<String> groups;
/*     */   private final Set<String> players;
/*     */ 
/*     */   public DefaultDomain()
/*     */   {
/*  37 */     this.groups = new LinkedHashSet();
/*  38 */     this.players = new HashSet();
/*     */   }
/*     */ 
/*     */   public void addPlayer(String name) {
/*  42 */     this.players.add(name.toLowerCase());
/*     */   }
/*     */ 
/*     */   public void addPlayer(LocalPlayer player) {
/*  46 */     this.players.add(player.getName().toLowerCase());
/*     */   }
/*     */ 
/*     */   public void removePlayer(String name) {
/*  50 */     this.players.remove(name.toLowerCase());
/*     */   }
/*     */ 
/*     */   public void removePlayer(LocalPlayer player) {
/*  54 */     this.players.remove(player.getName().toLowerCase());
/*     */   }
/*     */ 
/*     */   public void addGroup(String name) {
/*  58 */     this.groups.add(name.toLowerCase());
/*     */   }
/*     */ 
/*     */   public void removeGroup(String name) {
/*  62 */     this.groups.remove(name.toLowerCase());
/*     */   }
/*     */ 
/*     */   public Set<String> getGroups() {
/*  66 */     return this.groups;
/*     */   }
/*     */ 
/*     */   public Set<String> getPlayers() {
/*  70 */     return this.players;
/*     */   }
/*     */ 
/*     */   public boolean contains(LocalPlayer player)
/*     */   {
/*  75 */     if (contains(player.getName())) {
/*  76 */       return true;
/*     */     }
/*     */ 
/*  79 */     for (String group : this.groups) {
/*  80 */       if (player.hasGroup(group)) {
/*  81 */         return true;
/*     */       }
/*     */     }
/*     */ 
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean contains(String playerName)
/*     */   {
/*  90 */     return this.players.contains(playerName.toLowerCase());
/*     */   }
/*     */ 
/*     */   public int size() {
/*  94 */     return this.groups.size() + this.players.size();
/*     */   }
/*     */ 
/*     */   public String toPlayersString() {
/*  98 */     StringBuilder str = new StringBuilder();
/*  99 */     List output = new ArrayList(this.players);
/* 100 */     Collections.sort(output, String.CASE_INSENSITIVE_ORDER);
/* 101 */     for (Iterator it = output.iterator(); it.hasNext(); ) {
/* 102 */       str.append((String)it.next());
/* 103 */       if (it.hasNext()) {
/* 104 */         str.append(", ");
/*     */       }
/*     */     }
/* 107 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public String toGroupsString() {
/* 111 */     StringBuilder str = new StringBuilder();
/* 112 */     for (Iterator it = this.groups.iterator(); it.hasNext(); ) {
/* 113 */       str.append("*");
/* 114 */       str.append((String)it.next());
/* 115 */       if (it.hasNext()) {
/* 116 */         str.append(", ");
/*     */       }
/*     */     }
/* 119 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public String toUserFriendlyString() {
/* 123 */     StringBuilder str = new StringBuilder();
/* 124 */     if (this.players.size() > 0) {
/* 125 */       str.append(toPlayersString());
/*     */     }
/*     */ 
/* 128 */     if (this.groups.size() > 0) {
/* 129 */       if (str.length() > 0) {
/* 130 */         str.append("; ");
/*     */       }
/*     */ 
/* 133 */       str.append(toGroupsString());
/*     */     }
/*     */ 
/* 136 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public void removeAll() {
/* 140 */     this.groups.clear();
/* 141 */     this.players.clear();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.domains.DefaultDomain
 * JD-Core Version:    0.6.2
 */