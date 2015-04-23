/*    */ package com.sk89q.worldguard.bukkit.commands;
/*    */ 
/*    */ import com.sk89q.minecraft.util.commands.Command;
/*    */ import com.sk89q.minecraft.util.commands.CommandContext;
/*    */ import com.sk89q.minecraft.util.commands.NestedCommand;
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class ProtectionCommands
/*    */ {
/*    */   private final WorldGuardPlugin plugin;
/*    */ 
/*    */   public ProtectionCommands(WorldGuardPlugin plugin)
/*    */   {
/* 34 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   @Command(aliases={"region", "regions", "rg"}, desc="Region management commands")
/*    */   @NestedCommand({RegionCommands.class, RegionMemberCommands.class})
/*    */   public void region(CommandContext args, CommandSender sender)
/*    */   {
/*    */   }
/*    */ 
/*    */   @Command(aliases={"worldguard", "wg"}, desc="WorldGuard commands")
/*    */   @NestedCommand({WorldGuardCommands.class})
/*    */   public void worldGuard(CommandContext args, CommandSender sender)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.ProtectionCommands
 * JD-Core Version:    0.6.2
 */