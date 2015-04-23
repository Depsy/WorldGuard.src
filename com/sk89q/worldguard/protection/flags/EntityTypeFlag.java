/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import org.bukkit.entity.EntityType;
/*    */ 
/*    */ public class EntityTypeFlag extends EnumFlag<EntityType>
/*    */ {
/*    */   public EntityTypeFlag(String name, RegionGroup defaultGroup)
/*    */   {
/* 31 */     super(name, EntityType.class, defaultGroup);
/*    */   }
/*    */ 
/*    */   public EntityTypeFlag(String name) {
/* 35 */     super(name, EntityType.class);
/*    */   }
/*    */ 
/*    */   public EntityType detectValue(String input)
/*    */   {
/* 40 */     EntityType lowMatch = null;
/*    */ 
/* 42 */     for (EntityType type : EntityType.values()) {
/* 43 */       if (type.name().equalsIgnoreCase(input.trim())) {
/* 44 */         return type;
/*    */       }
/*    */ 
/* 47 */       if (type.name().toLowerCase().startsWith(input.toLowerCase().trim())) {
/* 48 */         lowMatch = type;
/*    */       }
/*    */     }
/*    */ 
/* 52 */     return lowMatch;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.EntityTypeFlag
 * JD-Core Version:    0.6.2
 */