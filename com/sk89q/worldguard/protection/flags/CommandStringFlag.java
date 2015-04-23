/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class CommandStringFlag extends Flag<String>
/*    */ {
/*    */   public CommandStringFlag(String name, RegionGroup defaultGroup)
/*    */   {
/* 32 */     super(name, defaultGroup);
/*    */   }
/*    */ 
/*    */   public CommandStringFlag(String name) {
/* 36 */     super(name);
/*    */   }
/*    */ 
/*    */   public String parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
/*    */     throws InvalidFlagFormat
/*    */   {
/* 42 */     input = input.trim();
/* 43 */     if (!input.startsWith("/")) {
/* 44 */       input = "/" + input;
/*    */     }
/* 46 */     return input.toLowerCase();
/*    */   }
/*    */ 
/*    */   public String unmarshal(Object o)
/*    */   {
/* 51 */     if ((o instanceof String)) {
/* 52 */       return (String)o;
/*    */     }
/* 54 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(String o)
/*    */   {
/* 60 */     return o;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.CommandStringFlag
 * JD-Core Version:    0.6.2
 */