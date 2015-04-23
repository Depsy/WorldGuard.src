/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class StringFlag extends Flag<String>
/*    */ {
/*    */   public StringFlag(String name, RegionGroup defaultGroup)
/*    */   {
/* 32 */     super(name, defaultGroup);
/*    */   }
/*    */ 
/*    */   public StringFlag(String name) {
/* 36 */     super(name);
/*    */   }
/*    */ 
/*    */   public String parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
/*    */     throws InvalidFlagFormat
/*    */   {
/* 42 */     return input.replaceAll("(?!\\\\)\\\\n", "\n").replaceAll("\\\\\\\\n", "\\n");
/*    */   }
/*    */ 
/*    */   public String unmarshal(Object o)
/*    */   {
/* 47 */     if ((o instanceof String)) {
/* 48 */       return (String)o;
/*    */     }
/* 50 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(String o)
/*    */   {
/* 56 */     return o;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.StringFlag
 * JD-Core Version:    0.6.2
 */