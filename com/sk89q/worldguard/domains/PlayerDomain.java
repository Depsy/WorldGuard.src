/*    */ package com.sk89q.worldguard.domains;
/*    */ 
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class PlayerDomain
/*    */   implements Domain
/*    */ {
/*    */   private Set<String> players;
/*    */ 
/*    */   public PlayerDomain()
/*    */   {
/* 31 */     this.players = new HashSet();
/*    */   }
/*    */ 
/*    */   public PlayerDomain(String[] players) {
/* 35 */     this.players = new HashSet();
/*    */ 
/* 37 */     for (String name : players)
/* 38 */       this.players.add(name.toLowerCase());
/*    */   }
/*    */ 
/*    */   public void addPlayer(String name)
/*    */   {
/* 43 */     this.players.add(name.toLowerCase());
/*    */   }
/*    */ 
/*    */   public boolean contains(LocalPlayer player) {
/* 47 */     return contains(player.getName());
/*    */   }
/*    */ 
/*    */   public boolean contains(String playerName)
/*    */   {
/* 52 */     return this.players.contains(playerName.toLowerCase());
/*    */   }
/*    */ 
/*    */   public int size() {
/* 56 */     return this.players.size();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.domains.PlayerDomain
 * JD-Core Version:    0.6.2
 */