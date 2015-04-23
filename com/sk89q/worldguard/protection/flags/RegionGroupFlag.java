/*     */ package com.sk89q.worldguard.protection.flags;
/*     */ 
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ 
/*     */ public class RegionGroupFlag extends EnumFlag<RegionGroup>
/*     */ {
/*     */   private RegionGroup def;
/*     */ 
/*     */   public RegionGroupFlag(String name, RegionGroup def)
/*     */   {
/*  34 */     super(name, RegionGroup.class, null);
/*  35 */     this.def = def;
/*     */   }
/*     */ 
/*     */   public RegionGroup getDefault() {
/*  39 */     return this.def;
/*     */   }
/*     */ 
/*     */   public RegionGroup detectValue(String input)
/*     */   {
/*  44 */     input = input.trim();
/*     */ 
/*  46 */     if ((input.equalsIgnoreCase("members")) || (input.equalsIgnoreCase("member")))
/*  47 */       return RegionGroup.MEMBERS;
/*  48 */     if ((input.equalsIgnoreCase("owners")) || (input.equalsIgnoreCase("owner")))
/*  49 */       return RegionGroup.OWNERS;
/*  50 */     if ((input.equalsIgnoreCase("nonowners")) || (input.equalsIgnoreCase("nonowner")))
/*  51 */       return RegionGroup.NON_OWNERS;
/*  52 */     if ((input.equalsIgnoreCase("nonmembers")) || (input.equalsIgnoreCase("nonmember")))
/*  53 */       return RegionGroup.NON_MEMBERS;
/*  54 */     if ((input.equalsIgnoreCase("everyone")) || (input.equalsIgnoreCase("anyone")) || (input.equalsIgnoreCase("all")))
/*  55 */       return RegionGroup.ALL;
/*  56 */     if ((input.equalsIgnoreCase("none")) || (input.equalsIgnoreCase("noone")) || (input.equalsIgnoreCase("deny"))) {
/*  57 */       return RegionGroup.NONE;
/*     */     }
/*  59 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isMember(ProtectedRegion region, RegionGroup group, LocalPlayer player)
/*     */   {
/*  64 */     if ((group == null) || (group == RegionGroup.ALL))
/*  65 */       return true;
/*  66 */     if (group == RegionGroup.OWNERS) {
/*  67 */       if (region.isOwner(player))
/*  68 */         return true;
/*     */     }
/*  70 */     else if (group == RegionGroup.MEMBERS) {
/*  71 */       if (region.isMember(player))
/*  72 */         return true;
/*     */     }
/*  74 */     else if (group == RegionGroup.NON_OWNERS) {
/*  75 */       if (!region.isOwner(player))
/*  76 */         return true;
/*     */     }
/*  78 */     else if ((group == RegionGroup.NON_MEMBERS) && 
/*  79 */       (!region.isMember(player))) {
/*  80 */       return true;
/*     */     }
/*     */ 
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isMember(ApplicableRegionSet set, RegionGroup group, LocalPlayer player)
/*     */   {
/*  89 */     if ((group == null) || (group == RegionGroup.ALL))
/*  90 */       return true;
/*  91 */     if (group == RegionGroup.OWNERS) {
/*  92 */       if (set.isOwnerOfAll(player))
/*  93 */         return true;
/*     */     }
/*  95 */     else if (group == RegionGroup.MEMBERS) {
/*  96 */       if (set.isMemberOfAll(player))
/*  97 */         return true;
/*     */     }
/*  99 */     else if (group == RegionGroup.NON_OWNERS) {
/* 100 */       if (!set.isOwnerOfAll(player))
/* 101 */         return true;
/*     */     }
/* 103 */     else if ((group == RegionGroup.NON_MEMBERS) && 
/* 104 */       (!set.isMemberOfAll(player))) {
/* 105 */       return true;
/*     */     }
/*     */ 
/* 109 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.RegionGroupFlag
 * JD-Core Version:    0.6.2
 */