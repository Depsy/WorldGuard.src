/*    */ package com.sk89q.worldguard.bukkit.commands;
/*    */ 
/*    */ class RegionListEntry
/*    */   implements Comparable<RegionListEntry>
/*    */ {
/*    */   private final String id;
/*    */   private final int index;
/*    */   boolean isOwner;
/*    */   boolean isMember;
/*    */ 
/*    */   public RegionListEntry(String id, int index)
/*    */   {
/* 32 */     this.id = id;
/* 33 */     this.index = index;
/*    */   }
/*    */ 
/*    */   public int compareTo(RegionListEntry o)
/*    */   {
/* 38 */     if (this.isOwner != o.isOwner) {
/* 39 */       return this.isOwner ? 1 : -1;
/*    */     }
/* 41 */     if (this.isMember != o.isMember) {
/* 42 */       return this.isMember ? 1 : -1;
/*    */     }
/* 44 */     return this.id.compareTo(o.id);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 49 */     if (this.isOwner)
/* 50 */       return this.index + 1 + ". +" + this.id;
/* 51 */     if (this.isMember) {
/* 52 */       return this.index + 1 + ". -" + this.id;
/*    */     }
/* 54 */     return this.index + 1 + ". " + this.id;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.RegionListEntry
 * JD-Core Version:    0.6.2
 */