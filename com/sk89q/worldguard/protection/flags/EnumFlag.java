/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class EnumFlag<T extends Enum<T>> extends Flag<T>
/*    */ {
/*    */   private Class<T> enumClass;
/*    */ 
/*    */   public EnumFlag(String name, Class<T> enumClass, RegionGroup defaultGroup)
/*    */   {
/* 34 */     super(name, defaultGroup);
/* 35 */     this.enumClass = enumClass;
/*    */   }
/*    */ 
/*    */   public EnumFlag(String name, Class<T> enumClass) {
/* 39 */     super(name);
/* 40 */     this.enumClass = enumClass;
/*    */   }
/*    */ 
/*    */   private T findValue(String input) throws IllegalArgumentException {
/* 44 */     if (input != null) {
/* 45 */       input = input.toUpperCase();
/*    */     }
/*    */     try
/*    */     {
/* 49 */       return Enum.valueOf(this.enumClass, input);
/*    */     } catch (IllegalArgumentException e) {
/* 51 */       Enum val = detectValue(input);
/*    */ 
/* 53 */       if (val != null) {
/* 54 */         return val;
/*    */       }
/*    */ 
/* 57 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public T detectValue(String input)
/*    */   {
/* 68 */     return null;
/*    */   }
/*    */ 
/*    */   public T parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat
/*    */   {
/*    */     try
/*    */     {
/* 75 */       return findValue(input); } catch (IllegalArgumentException e) {
/*    */     }
/* 77 */     throw new InvalidFlagFormat("Unknown value '" + input + "' in " + this.enumClass.getName());
/*    */   }
/*    */ 
/*    */   public T unmarshal(Object o)
/*    */   {
/*    */     try
/*    */     {
/* 85 */       return Enum.valueOf(this.enumClass, String.valueOf(o)); } catch (IllegalArgumentException e) {
/*    */     }
/* 87 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(T o)
/*    */   {
/* 93 */     return o.name();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.EnumFlag
 * JD-Core Version:    0.6.2
 */