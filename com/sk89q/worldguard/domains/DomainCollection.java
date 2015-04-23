/*    */ package com.sk89q.worldguard.domains;
/*    */ 
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class DomainCollection
/*    */   implements Domain
/*    */ {
/*    */   private Set<Domain> domains;
/*    */ 
/*    */   public DomainCollection()
/*    */   {
/* 31 */     this.domains = new LinkedHashSet();
/*    */   }
/*    */ 
/*    */   public void add(Domain domain) {
/* 35 */     this.domains.add(domain);
/*    */   }
/*    */ 
/*    */   public void remove(Domain domain) {
/* 39 */     this.domains.remove(domain);
/*    */   }
/*    */ 
/*    */   public int size() {
/* 43 */     return this.domains.size();
/*    */   }
/*    */ 
/*    */   public boolean contains(LocalPlayer player)
/*    */   {
/* 48 */     for (Domain domain : this.domains) {
/* 49 */       if (domain.contains(player)) {
/* 50 */         return true;
/*    */       }
/*    */     }
/*    */ 
/* 54 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean contains(String playerName)
/*    */   {
/* 59 */     for (Domain domain : this.domains) {
/* 60 */       if (domain.contains(playerName)) {
/* 61 */         return true;
/*    */       }
/*    */     }
/*    */ 
/* 65 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.domains.DomainCollection
 * JD-Core Version:    0.6.2
 */