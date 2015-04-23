/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ 
/*     */ public class SpongeUtil
/*     */ {
/*     */   public static void clearSpongeWater(WorldGuardPlugin plugin, World world, int ox, int oy, int oz)
/*     */   {
/*  39 */     ConfigurationManager cfg = plugin.getGlobalStateManager();
/*  40 */     WorldConfiguration wcfg = cfg.get(world);
/*     */ 
/*  42 */     for (int cx = -wcfg.spongeRadius; cx <= wcfg.spongeRadius; cx++)
/*  43 */       for (int cy = -wcfg.spongeRadius; cy <= wcfg.spongeRadius; cy++)
/*  44 */         for (int cz = -wcfg.spongeRadius; cz <= wcfg.spongeRadius; cz++)
/*  45 */           if (BukkitUtil.isBlockWater(world, ox + cx, oy + cy, oz + cz))
/*  46 */             world.getBlockAt(ox + cx, oy + cy, oz + cz).setTypeId(0);
/*     */   }
/*     */ 
/*     */   public static void addSpongeWater(WorldGuardPlugin plugin, World world, int ox, int oy, int oz)
/*     */   {
/*  63 */     ConfigurationManager cfg = plugin.getGlobalStateManager();
/*  64 */     WorldConfiguration wcfg = cfg.get(world);
/*     */ 
/*  67 */     int cx = ox - wcfg.spongeRadius - 1;
/*  68 */     for (int cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; cy++) {
/*  69 */       for (int cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; cz++) {
/*  70 */         if (BukkitUtil.isBlockWater(world, cx, cy, cz)) {
/*  71 */           BukkitUtil.setBlockToWater(world, cx + 1, cy, cz);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  77 */     cx = ox + wcfg.spongeRadius + 1;
/*  78 */     for (int cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; cy++) {
/*  79 */       for (int cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; cz++) {
/*  80 */         if (BukkitUtil.isBlockWater(world, cx, cy, cz)) {
/*  81 */           BukkitUtil.setBlockToWater(world, cx - 1, cy, cz);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  87 */     int cy = oy - wcfg.spongeRadius - 1;
/*  88 */     for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; cx++) {
/*  89 */       for (int cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; cz++) {
/*  90 */         if (BukkitUtil.isBlockWater(world, cx, cy, cz)) {
/*  91 */           BukkitUtil.setBlockToWater(world, cx, cy + 1, cz);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  97 */     cy = oy + wcfg.spongeRadius + 1;
/*  98 */     for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; cx++) {
/*  99 */       for (int cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; cz++) {
/* 100 */         if (BukkitUtil.isBlockWater(world, cx, cy, cz)) {
/* 101 */           BukkitUtil.setBlockToWater(world, cx, cy - 1, cz);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 107 */     int cz = oz - wcfg.spongeRadius - 1;
/* 108 */     for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; cx++) {
/* 109 */       for (cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; cy++) {
/* 110 */         if (BukkitUtil.isBlockWater(world, cx, cy, cz)) {
/* 111 */           BukkitUtil.setBlockToWater(world, cx, cy, cz + 1);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 117 */     cz = oz + wcfg.spongeRadius + 1;
/* 118 */     for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; cx++)
/* 119 */       for (cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; cy++)
/* 120 */         if (BukkitUtil.isBlockWater(world, cx, cy, cz))
/* 121 */           BukkitUtil.setBlockToWater(world, cx, cy, cz - 1);
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.SpongeUtil
 * JD-Core Version:    0.6.2
 */