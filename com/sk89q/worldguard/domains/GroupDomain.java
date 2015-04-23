/*    */ package com.sk89q.worldguard.domains;
/*    */ 
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ import java.util.Arrays;
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class GroupDomain
/*    */   implements Domain
/*    */ {
/*    */   private Set<String> groups;
/*    */ 
/*    */   public GroupDomain()
/*    */   {
/* 32 */     this.groups = new LinkedHashSet();
/*    */   }
/*    */ 
/*    */   public GroupDomain(String[] groups) {
/* 36 */     this.groups = new LinkedHashSet(Arrays.asList(groups));
/*    */   }
/*    */ 
/*    */   public void addGroup(String name) {
/* 40 */     this.groups.add(name);
/*    */   }
/*    */ 
/*    */   public boolean contains(LocalPlayer player) {
/* 44 */     for (String group : this.groups) {
/* 45 */       if (player.hasGroup(group)) {
/* 46 */         return true;
/*    */       }
/*    */     }
/*    */ 
/* 50 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean contains(String playerName)
/*    */   {
/* 55 */     return false;
/*    */   }
/*    */ 
/*    */   public int size() {
/* 59 */     return this.groups.size();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.domains.GroupDomain
 * JD-Core Version:    0.6.2
 */