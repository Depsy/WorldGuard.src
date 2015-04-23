/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class IntegerFlag extends Flag<Integer>
/*    */ {
/*    */   public IntegerFlag(String name, RegionGroup defaultGroup)
/*    */   {
/* 32 */     super(name, defaultGroup);
/*    */   }
/*    */ 
/*    */   public IntegerFlag(String name) {
/* 36 */     super(name);
/*    */   }
/*    */ 
/*    */   public Integer parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
/*    */     throws InvalidFlagFormat
/*    */   {
/* 42 */     input = input.trim();
/*    */     try
/*    */     {
/* 45 */       return Integer.valueOf(Integer.parseInt(input)); } catch (NumberFormatException e) {
/*    */     }
/* 47 */     throw new InvalidFlagFormat("Not a number: " + input);
/*    */   }
/*    */ 
/*    */   public Integer unmarshal(Object o)
/*    */   {
/* 53 */     if ((o instanceof Integer))
/* 54 */       return (Integer)o;
/* 55 */     if ((o instanceof Number)) {
/* 56 */       return Integer.valueOf(((Number)o).intValue());
/*    */     }
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(Integer o)
/*    */   {
/* 64 */     return o;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.IntegerFlag
 * JD-Core Version:    0.6.2
 */