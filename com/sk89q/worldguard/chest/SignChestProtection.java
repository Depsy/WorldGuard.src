/*     */ package com.sk89q.worldguard.chest;
/*     */ 
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockState;
/*     */ import org.bukkit.block.Sign;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class SignChestProtection
/*     */   implements ChestProtection
/*     */ {
/*     */   public boolean isProtected(Block block, Player player)
/*     */   {
/*  37 */     if (isChest(block.getTypeId())) {
/*  38 */       Block below = block.getRelative(0, -1, 0);
/*  39 */       return isProtectedSignAround(below, player);
/*  40 */     }if (block.getTypeId() == 63) {
/*  41 */       return isProtectedSignAndChestBinary(block, player);
/*     */     }
/*  43 */     Block above = block.getRelative(0, 1, 0);
/*  44 */     Boolean res = isProtectedSign(above, player);
/*  45 */     if (res != null) return res.booleanValue();
/*  46 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isProtectedPlacement(Block block, Player player)
/*     */   {
/*  51 */     return isProtectedSignAround(block, player);
/*     */   }
/*     */ 
/*     */   private boolean isProtectedSignAround(Block searchBlock, Player player)
/*     */   {
/*  58 */     Block side = searchBlock;
/*  59 */     Boolean res = isProtectedSign(side, player);
/*  60 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/*  62 */     side = searchBlock.getRelative(-1, 0, 0);
/*  63 */     res = isProtectedSignAndChest(side, player);
/*  64 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/*  66 */     side = searchBlock.getRelative(1, 0, 0);
/*  67 */     res = isProtectedSignAndChest(side, player);
/*  68 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/*  70 */     side = searchBlock.getRelative(0, 0, -1);
/*  71 */     res = isProtectedSignAndChest(side, player);
/*  72 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/*  74 */     side = searchBlock.getRelative(0, 0, 1);
/*  75 */     res = isProtectedSignAndChest(side, player);
/*  76 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   private Boolean isProtectedSign(Sign sign, Player player) {
/*  82 */     if (sign.getLine(0).equalsIgnoreCase("[Lock]")) {
/*  83 */       if (player == null) {
/*  84 */         return Boolean.valueOf(true);
/*     */       }
/*     */ 
/*  87 */       String name = player.getName();
/*  88 */       if ((name.equalsIgnoreCase(sign.getLine(1).trim())) || (name.equalsIgnoreCase(sign.getLine(2).trim())) || (name.equalsIgnoreCase(sign.getLine(3).trim())))
/*     */       {
/*  91 */         return Boolean.valueOf(false);
/*     */       }
/*     */ 
/*  95 */       return Boolean.valueOf(true);
/*     */     }
/*     */ 
/*  98 */     return null;
/*     */   }
/*     */ 
/*     */   private Boolean isProtectedSign(Block block, Player player) {
/* 102 */     BlockState state = block.getState();
/* 103 */     if ((state == null) || (!(state instanceof Sign))) {
/* 104 */       return null;
/*     */     }
/* 106 */     return isProtectedSign((Sign)state, player);
/*     */   }
/*     */ 
/*     */   private Boolean isProtectedSignAndChest(Block block, Player player) {
/* 110 */     if (!isChest(block.getRelative(0, 1, 0).getTypeId())) {
/* 111 */       return null;
/*     */     }
/* 113 */     return isProtectedSign(block, player);
/*     */   }
/*     */ 
/*     */   private boolean isProtectedSignAndChestBinary(Block block, Player player) {
/* 117 */     Boolean res = isProtectedSignAndChest(block, player);
/* 118 */     return (res != null) && (res.booleanValue());
/*     */   }
/*     */ 
/*     */   public boolean isAdjacentChestProtected(Block searchBlock, Player player)
/*     */   {
/* 125 */     Block side = searchBlock;
/* 126 */     Boolean res = Boolean.valueOf(isProtected(side, player));
/* 127 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/* 129 */     side = searchBlock.getRelative(-1, 0, 0);
/* 130 */     res = Boolean.valueOf(isProtected(side, player));
/* 131 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/* 133 */     side = searchBlock.getRelative(1, 0, 0);
/* 134 */     res = Boolean.valueOf(isProtected(side, player));
/* 135 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/* 137 */     side = searchBlock.getRelative(0, 0, -1);
/* 138 */     res = Boolean.valueOf(isProtected(side, player));
/* 139 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/* 141 */     side = searchBlock.getRelative(0, 0, 1);
/* 142 */     res = Boolean.valueOf(isProtected(side, player));
/* 143 */     if ((res != null) && (res.booleanValue())) return res.booleanValue();
/*     */ 
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean isChest(Material material) {
/* 150 */     return isChest(material.getId());
/*     */   }
/*     */ 
/*     */   public boolean isChest(int type) {
/* 154 */     return (type == 54) || (type == 23) || (type == 61) || (type == 62);
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.chest.SignChestProtection
 * JD-Core Version:    0.6.2
 */