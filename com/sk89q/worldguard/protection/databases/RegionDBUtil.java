/*     */ package com.sk89q.worldguard.protection.databases;
/*     */ 
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class RegionDBUtil
/*     */ {
/*  35 */   private static Pattern groupPattern = Pattern.compile("(?i)^[G]:(.+)$");
/*     */ 
/*     */   public static void addToDomain(DefaultDomain domain, String[] split, int startIndex)
/*     */   {
/*  50 */     for (int i = startIndex; i < split.length; i++) {
/*  51 */       String s = split[i];
/*  52 */       Matcher m = groupPattern.matcher(s);
/*  53 */       if (m.matches())
/*  54 */         domain.addGroup(m.group(1));
/*     */       else
/*  56 */         domain.addPlayer(s);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void removeFromDomain(DefaultDomain domain, String[] split, int startIndex)
/*     */   {
/*  70 */     for (int i = startIndex; i < split.length; i++) {
/*  71 */       String s = split[i];
/*  72 */       Matcher m = groupPattern.matcher(s);
/*  73 */       if (m.matches())
/*  74 */         domain.removeGroup(m.group(1));
/*     */       else
/*  76 */         domain.removePlayer(s);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static DefaultDomain parseDomainString(String[] split, int startIndex)
/*     */   {
/*  89 */     DefaultDomain domain = new DefaultDomain();
/*     */ 
/*  91 */     for (int i = startIndex; i < split.length; i++) {
/*  92 */       String s = split[i];
/*  93 */       Matcher m = groupPattern.matcher(s);
/*  94 */       if (m.matches())
/*  95 */         domain.addGroup(m.group(1));
/*     */       else {
/*  97 */         domain.addPlayer(s);
/*     */       }
/*     */     }
/*     */ 
/* 101 */     return domain;
/*     */   }
/*     */ 
/*     */   public static String preparePlaceHolders(int length)
/*     */   {
/* 111 */     StringBuilder builder = new StringBuilder();
/* 112 */     for (int i = 0; i < length; ) {
/* 113 */       builder.append("?");
/* 114 */       i++; if (i < length) {
/* 115 */         builder.append(",");
/*     */       }
/*     */     }
/* 118 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static void setValues(PreparedStatement preparedStatement, String[] values)
/*     */     throws SQLException
/*     */   {
/* 129 */     for (int i = 0; i < values.length; i++)
/* 130 */       preparedStatement.setString(i + 1, values[i]);
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.RegionDBUtil
 * JD-Core Version:    0.6.2
 */