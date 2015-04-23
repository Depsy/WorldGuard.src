/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class BooleanFlag extends Flag<Boolean>
/*    */ {
/*    */   public BooleanFlag(String name, RegionGroup defaultGroup)
/*    */   {
/* 32 */     super(name, defaultGroup);
/*    */   }
/*    */ 
/*    */   public BooleanFlag(String name) {
/* 36 */     super(name);
/*    */   }
/*    */ 
/*    */   public Boolean parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
/*    */     throws InvalidFlagFormat
/*    */   {
/* 42 */     input = input.trim();
/*    */ 
/* 44 */     if ((input.equalsIgnoreCase("true")) || (input.equalsIgnoreCase("yes")) || (input.equalsIgnoreCase("on")) || (input.equalsIgnoreCase("1")))
/*    */     {
/* 47 */       return Boolean.valueOf(true);
/* 48 */     }if ((input.equalsIgnoreCase("false")) || (input.equalsIgnoreCase("no")) || (input.equalsIgnoreCase("off")) || (input.equalsIgnoreCase("0")))
/*    */     {
/* 51 */       return Boolean.valueOf(false);
/*    */     }
/* 53 */     throw new InvalidFlagFormat("Not a yes/no value: " + input);
/*    */   }
/*    */ 
/*    */   public Boolean unmarshal(Object o)
/*    */   {
/* 59 */     if ((o instanceof Boolean)) {
/* 60 */       return (Boolean)o;
/*    */     }
/* 62 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(Boolean o)
/*    */   {
/* 68 */     return o;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.BooleanFlag
 * JD-Core Version:    0.6.2
 */