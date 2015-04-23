/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public abstract class Flag<T>
/*    */ {
/*    */   private String name;
/*    */   private RegionGroupFlag regionGroup;
/*    */ 
/*    */   public Flag(String name, RegionGroup defaultGroup)
/*    */   {
/* 36 */     this.name = name;
/*    */ 
/* 38 */     if (defaultGroup != null)
/* 39 */       this.regionGroup = new RegionGroupFlag(name + "-group", defaultGroup);
/*    */   }
/*    */ 
/*    */   public Flag(String name)
/*    */   {
/* 44 */     this(name, RegionGroup.NON_MEMBERS);
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 48 */     return this.name;
/*    */   }
/*    */ 
/*    */   public RegionGroupFlag getRegionGroupFlag() {
/* 52 */     return this.regionGroup;
/*    */   }
/*    */ 
/*    */   public abstract T parseInput(WorldGuardPlugin paramWorldGuardPlugin, CommandSender paramCommandSender, String paramString)
/*    */     throws InvalidFlagFormat;
/*    */ 
/*    */   public abstract T unmarshal(Object paramObject);
/*    */ 
/*    */   public abstract Object marshal(T paramT);
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.Flag
 * JD-Core Version:    0.6.2
 */