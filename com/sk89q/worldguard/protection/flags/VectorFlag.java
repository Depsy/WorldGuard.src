/*     */ package com.sk89q.worldguard.protection.flags;
/*     */ 
/*     */ import com.sk89q.minecraft.util.commands.CommandException;
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.bukkit.BukkitUtil;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class VectorFlag extends Flag<Vector>
/*     */ {
/*     */   public VectorFlag(String name, RegionGroup defaultGroup)
/*     */   {
/*  38 */     super(name, defaultGroup);
/*     */   }
/*     */ 
/*     */   public VectorFlag(String name) {
/*  42 */     super(name);
/*     */   }
/*     */ 
/*     */   public Vector parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat
/*     */   {
/*  47 */     input = input.trim();
/*     */ 
/*  49 */     if ("here".equalsIgnoreCase(input)) {
/*     */       try {
/*  51 */         return BukkitUtil.toVector(plugin.checkPlayer(sender).getLocation());
/*     */       } catch (CommandException e) {
/*  53 */         throw new InvalidFlagFormat(e.getMessage());
/*     */       }
/*     */     }
/*  56 */     String[] split = input.split(",");
/*  57 */     if (split.length == 3) {
/*     */       try {
/*  59 */         return new Vector(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
/*     */       }
/*     */       catch (NumberFormatException ignored)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  68 */     throw new InvalidFlagFormat("Expected 'here' or x,y,z.");
/*     */   }
/*     */ 
/*     */   public Vector unmarshal(Object o)
/*     */   {
/*  74 */     if ((o instanceof Map)) {
/*  75 */       Map map = (Map)o;
/*     */ 
/*  77 */       Object rawX = map.get("x");
/*  78 */       Object rawY = map.get("y");
/*  79 */       Object rawZ = map.get("z");
/*     */ 
/*  81 */       if ((rawX == null) || (rawY == null) || (rawZ == null)) {
/*  82 */         return null;
/*     */       }
/*     */ 
/*  85 */       return new Vector(toNumber(rawX), toNumber(rawY), toNumber(rawZ));
/*     */     }
/*     */ 
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   public Object marshal(Vector o)
/*     */   {
/*  93 */     Map vec = new HashMap();
/*  94 */     vec.put("x", Double.valueOf(o.getX()));
/*  95 */     vec.put("y", Double.valueOf(o.getY()));
/*  96 */     vec.put("z", Double.valueOf(o.getZ()));
/*  97 */     return vec;
/*     */   }
/*     */ 
/*     */   private double toNumber(Object o) {
/* 101 */     if ((o instanceof Integer))
/* 102 */       return ((Integer)o).intValue();
/* 103 */     if ((o instanceof Long))
/* 104 */       return ((Long)o).longValue();
/* 105 */     if ((o instanceof Float))
/* 106 */       return ((Float)o).floatValue();
/* 107 */     if ((o instanceof Double)) {
/* 108 */       return ((Double)o).doubleValue();
/*     */     }
/* 110 */     return 0.0D;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.VectorFlag
 * JD-Core Version:    0.6.2
 */