/*    */ package com.sk89q.worldguard.util;
/*    */ 
/*    */ import com.sk89q.worldguard.domains.DefaultDomain;
/*    */ import com.sk89q.worldguard.protection.databases.RegionDBUtil;
/*    */ 
/*    */ @Deprecated
/*    */ public class RegionUtil
/*    */ {
/*    */   @Deprecated
/*    */   public static void addToDomain(DefaultDomain domain, String[] split, int startIndex)
/*    */   {
/* 47 */     RegionDBUtil.addToDomain(domain, split, startIndex);
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public static void removeFromDomain(DefaultDomain domain, String[] split, int startIndex)
/*    */   {
/* 61 */     RegionDBUtil.removeFromDomain(domain, split, startIndex);
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public static DefaultDomain parseDomainString(String[] split, int startIndex)
/*    */   {
/* 74 */     return RegionDBUtil.parseDomainString(split, startIndex);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.util.RegionUtil
 * JD-Core Version:    0.6.2
 */