/*     */ package com.sk89q.worldguard.bukkit.commands;
/*     */ 
/*     */ import com.sk89q.minecraft.util.commands.Command;
/*     */ import com.sk89q.minecraft.util.commands.CommandContext;
/*     */ import com.sk89q.minecraft.util.commands.CommandException;
/*     */ import com.sk89q.minecraft.util.commands.CommandPermissions;
/*     */ import com.sk89q.worldguard.bukkit.BukkitUtil;
/*     */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*     */ import com.sk89q.worldguard.bukkit.WorldConfiguration;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class ToggleCommands
/*     */ {
/*     */   private final WorldGuardPlugin plugin;
/*     */ 
/*     */   public ToggleCommands(WorldGuardPlugin plugin)
/*     */   {
/*  41 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   @Command(aliases={"stopfire"}, usage="[<world>]", desc="Disables all fire spread temporarily", max=1)
/*     */   @CommandPermissions({"worldguard.fire-toggle.stop"})
/*     */   public void stopFire(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/*     */     World world;
/*     */     World world;
/*  51 */     if (args.argsLength() == 0)
/*  52 */       world = this.plugin.checkPlayer(sender).getWorld();
/*     */     else {
/*  54 */       world = this.plugin.matchWorld(sender, args.getString(0));
/*     */     }
/*     */ 
/*  57 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(world);
/*     */ 
/*  59 */     if (!wcfg.fireSpreadDisableToggle) {
/*  60 */       this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Fire spread has been globally disabled for '" + world.getName() + "' by " + this.plugin.toName(sender) + ".");
/*     */     }
/*     */     else
/*     */     {
/*  65 */       sender.sendMessage(ChatColor.YELLOW + "Fire spread was already globally disabled.");
/*     */     }
/*     */ 
/*  70 */     wcfg.fireSpreadDisableToggle = true;
/*     */   }
/*     */ 
/*     */   @Command(aliases={"allowfire"}, usage="[<world>]", desc="Allows all fire spread temporarily", max=1)
/*     */   @CommandPermissions({"worldguard.fire-toggle.stop"})
/*     */   public void allowFire(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/*     */     World world;
/*     */     World world;
/*  80 */     if (args.argsLength() == 0)
/*  81 */       world = this.plugin.checkPlayer(sender).getWorld();
/*     */     else {
/*  83 */       world = this.plugin.matchWorld(sender, args.getString(0));
/*     */     }
/*     */ 
/*  86 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(world);
/*     */ 
/*  88 */     if (wcfg.fireSpreadDisableToggle) {
/*  89 */       this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Fire spread has been globally for '" + world.getName() + "' re-enabled by " + this.plugin.toName(sender) + ".");
/*     */     }
/*     */     else
/*     */     {
/*  93 */       sender.sendMessage(ChatColor.YELLOW + "Fire spread was already globally enabled.");
/*     */     }
/*     */ 
/*  97 */     wcfg.fireSpreadDisableToggle = false;
/*     */   }
/*     */ 
/*     */   @Command(aliases={"halt-activity", "stoplag", "haltactivity"}, desc="Attempts to cease as much activity in order to stop lag", flags="c", max=0)
/*     */   @CommandPermissions({"worldguard.halt-activity"})
/*     */   public void stopLag(CommandContext args, CommandSender sender) throws CommandException
/*     */   {
/* 105 */     ConfigurationManager configManager = this.plugin.getGlobalStateManager();
/*     */ 
/* 107 */     configManager.activityHaltToggle = (!args.hasFlag('c'));
/*     */ 
/* 109 */     if (configManager.activityHaltToggle) {
/* 110 */       if (!(sender instanceof Player)) {
/* 111 */         sender.sendMessage(ChatColor.YELLOW + "ALL intensive server activity halted.");
/*     */       }
/*     */ 
/* 115 */       this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "ALL intensive server activity halted by " + this.plugin.toName(sender) + ".");
/*     */ 
/* 119 */       for (World world : this.plugin.getServer().getWorlds()) {
/* 120 */         int removed = 0;
/*     */ 
/* 122 */         for (Entity entity : world.getEntities()) {
/* 123 */           if (BukkitUtil.isIntensiveEntity(entity)) {
/* 124 */             entity.remove();
/* 125 */             removed++;
/*     */           }
/*     */         }
/*     */ 
/* 129 */         if (removed > 10) {
/* 130 */           sender.sendMessage("" + removed + " entities (>10) auto-removed from " + world.getName());
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 136 */       if (!(sender instanceof Player)) {
/* 137 */         sender.sendMessage(ChatColor.YELLOW + "ALL intensive server activity no longer halted.");
/*     */       }
/*     */ 
/* 141 */       this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "ALL intensive server activity is now allowed.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.ToggleCommands
 * JD-Core Version:    0.6.2
 */