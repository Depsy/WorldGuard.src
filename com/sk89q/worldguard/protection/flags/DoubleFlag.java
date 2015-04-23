/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class DoubleFlag extends Flag<Double>
/*    */ {
/*    */   public DoubleFlag(String name, RegionGroup defaultGroup)
/*    */   {
/* 32 */     super(name, defaultGroup);
/*    */   }
/*    */ 
/*    */   public DoubleFlag(String name) {
/* 36 */     super(name);
/*    */   }
/*    */ 
/*    */   public Double parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
/*    */     throws InvalidFlagFormat
/*    */   {
/* 42 */     input = input.trim();
/*    */     try
/*    */     {
/* 45 */       return Double.valueOf(Double.parseDouble(input)); } catch (NumberFormatException e) {
/*    */     }
/* 47 */     throw new InvalidFlagFormat("Not a number: " + input);
/*    */   }
/*    */ 
/*    */   public Double unmarshal(Object o)
/*    */   {
/* 53 */     if ((o instanceof Double))
/* 54 */       return (Double)o;
/* 55 */     if ((o instanceof Number)) {
/* 56 */       return Double.valueOf(((Number)o).doubleValue());
/*    */     }
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(Double o)
/*    */   {
/* 64 */     return o;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.DoubleFlag
 * JD-Core Version:    0.6.2
 */