/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class StateFlag extends Flag<State>
/*    */ {
/*    */   private boolean def;
/*    */ 
/*    */   public StateFlag(String name, boolean def, RegionGroup defaultGroup)
/*    */   {
/* 39 */     super(name, defaultGroup);
/* 40 */     this.def = def;
/*    */   }
/*    */ 
/*    */   public StateFlag(String name, boolean def) {
/* 44 */     super(name);
/* 45 */     this.def = def;
/*    */   }
/*    */ 
/*    */   public boolean getDefault() {
/* 49 */     return this.def;
/*    */   }
/*    */ 
/*    */   public State parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
/*    */     throws InvalidFlagFormat
/*    */   {
/* 55 */     input = input.trim();
/*    */ 
/* 57 */     if (input.equalsIgnoreCase("allow"))
/* 58 */       return State.ALLOW;
/* 59 */     if (input.equalsIgnoreCase("deny"))
/* 60 */       return State.DENY;
/* 61 */     if (input.equalsIgnoreCase("none")) {
/* 62 */       return null;
/*    */     }
/* 64 */     throw new InvalidFlagFormat("Expected none/allow/deny but got '" + input + "'");
/*    */   }
/*    */ 
/*    */   public State unmarshal(Object o)
/*    */   {
/* 70 */     String str = o.toString();
/* 71 */     if (str.equalsIgnoreCase("allow"))
/* 72 */       return State.ALLOW;
/* 73 */     if (str.equalsIgnoreCase("deny")) {
/* 74 */       return State.DENY;
/*    */     }
/* 76 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(State o)
/*    */   {
/* 82 */     if (o == State.ALLOW)
/* 83 */       return "allow";
/* 84 */     if (o == State.DENY) {
/* 85 */       return "deny";
/*    */     }
/* 87 */     return null;
/*    */   }
/*    */ 
/*    */   public static enum State
/*    */   {
/* 32 */     ALLOW, 
/* 33 */     DENY;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.StateFlag
 * JD-Core Version:    0.6.2
 */