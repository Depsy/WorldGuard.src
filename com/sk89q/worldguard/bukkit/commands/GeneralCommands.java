/*     */ package com.sk89q.worldguard.bukkit.commands;
/*     */ 
/*     */ import com.sk89q.minecraft.util.commands.Command;
/*     */ import com.sk89q.minecraft.util.commands.CommandContext;
/*     */ import com.sk89q.minecraft.util.commands.CommandException;
/*     */ import com.sk89q.minecraft.util.commands.CommandPermissions;
/*     */ import com.sk89q.worldedit.blocks.ItemType;
/*     */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.PlayerInventory;
/*     */ 
/*     */ public class GeneralCommands
/*     */ {
/*     */   private final WorldGuardPlugin plugin;
/*     */ 
/*     */   public GeneralCommands(WorldGuardPlugin plugin)
/*     */   {
/*  39 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   @Command(aliases={"god"}, usage="[player]", desc="Enable godmode on a player", flags="s", max=1)
/*     */   public void god(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/*  46 */     ConfigurationManager config = this.plugin.getGlobalStateManager();
/*     */ 
/*  48 */     Iterable targets = null;
/*  49 */     boolean included = false;
/*     */ 
/*  52 */     if (args.argsLength() == 0) {
/*  53 */       targets = this.plugin.matchPlayers(this.plugin.checkPlayer(sender));
/*     */ 
/*  56 */       this.plugin.checkPermission(sender, "worldguard.god");
/*  57 */     } else if (args.argsLength() == 1) {
/*  58 */       targets = this.plugin.matchPlayers(sender, args.getString(0));
/*     */ 
/*  61 */       this.plugin.checkPermission(sender, "worldguard.god.other");
/*     */     }
/*     */ 
/*  64 */     for (Player player : targets) {
/*  65 */       config.enableGodMode(player);
/*  66 */       player.setFireTicks(0);
/*     */ 
/*  69 */       if (player.equals(sender)) {
/*  70 */         player.sendMessage(ChatColor.YELLOW + "God mode enabled! Use /ungod to disable.");
/*     */ 
/*  73 */         included = true;
/*     */       } else {
/*  75 */         player.sendMessage(ChatColor.YELLOW + "God enabled by " + this.plugin.toName(sender) + ".");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  83 */     if ((!included) && (args.hasFlag('s')))
/*  84 */       sender.sendMessage(ChatColor.YELLOW.toString() + "Players now have god mode.");
/*     */   }
/*     */ 
/*     */   @Command(aliases={"ungod"}, usage="[player]", desc="Disable godmode on a player", flags="s", max=1)
/*     */   public void ungod(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/*  92 */     ConfigurationManager config = this.plugin.getGlobalStateManager();
/*     */ 
/*  94 */     Iterable targets = null;
/*  95 */     boolean included = false;
/*     */ 
/*  98 */     if (args.argsLength() == 0) {
/*  99 */       targets = this.plugin.matchPlayers(this.plugin.checkPlayer(sender));
/*     */ 
/* 102 */       this.plugin.checkPermission(sender, "worldguard.god");
/* 103 */     } else if (args.argsLength() == 1) {
/* 104 */       targets = this.plugin.matchPlayers(sender, args.getString(0));
/*     */ 
/* 107 */       this.plugin.checkPermission(sender, "worldguard.god.other");
/*     */     }
/*     */ 
/* 110 */     for (Player player : targets) {
/* 111 */       config.disableGodMode(player);
/*     */ 
/* 114 */       if (player.equals(sender)) {
/* 115 */         player.sendMessage(ChatColor.YELLOW + "God mode disabled!");
/*     */ 
/* 118 */         included = true;
/*     */       } else {
/* 120 */         player.sendMessage(ChatColor.YELLOW + "God disabled by " + this.plugin.toName(sender) + ".");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 128 */     if ((!included) && (args.hasFlag('s')))
/* 129 */       sender.sendMessage(ChatColor.YELLOW.toString() + "Players no longer have god mode.");
/*     */   }
/*     */ 
/*     */   @Command(aliases={"heal"}, usage="[player]", desc="Heal a player", flags="s", max=1)
/*     */   public void heal(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/* 136 */     Iterable targets = null;
/* 137 */     boolean included = false;
/*     */ 
/* 140 */     if (args.argsLength() == 0) {
/* 141 */       targets = this.plugin.matchPlayers(this.plugin.checkPlayer(sender));
/*     */ 
/* 144 */       this.plugin.checkPermission(sender, "worldguard.heal");
/* 145 */     } else if (args.argsLength() == 1) {
/* 146 */       targets = this.plugin.matchPlayers(sender, args.getString(0));
/*     */ 
/* 149 */       this.plugin.checkPermission(sender, "worldguard.heal.other");
/*     */     }
/*     */ 
/* 152 */     for (Player player : targets) {
/* 153 */       player.setHealth(20.0D);
/* 154 */       player.setFoodLevel(20);
/*     */ 
/* 157 */       if (player.equals(sender)) {
/* 158 */         player.sendMessage(ChatColor.YELLOW + "Healed!");
/*     */ 
/* 161 */         included = true;
/*     */       } else {
/* 163 */         player.sendMessage(ChatColor.YELLOW + "Healed by " + this.plugin.toName(sender) + ".");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 171 */     if ((!included) && (args.hasFlag('s')))
/* 172 */       sender.sendMessage(ChatColor.YELLOW.toString() + "Players healed.");
/*     */   }
/*     */ 
/*     */   @Command(aliases={"slay"}, usage="[player]", desc="Slay a player", flags="s", max=1)
/*     */   public void slay(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/* 179 */     Iterable targets = null;
/* 180 */     boolean included = false;
/*     */ 
/* 183 */     if (args.argsLength() == 0) {
/* 184 */       targets = this.plugin.matchPlayers(this.plugin.checkPlayer(sender));
/*     */ 
/* 187 */       this.plugin.checkPermission(sender, "worldguard.slay");
/* 188 */     } else if (args.argsLength() == 1) {
/* 189 */       targets = this.plugin.matchPlayers(sender, args.getString(0));
/*     */ 
/* 192 */       this.plugin.checkPermission(sender, "worldguard.slay.other");
/*     */     }
/*     */ 
/* 195 */     for (Player player : targets) {
/* 196 */       player.setHealth(0.0D);
/*     */ 
/* 199 */       if (player.equals(sender)) {
/* 200 */         player.sendMessage(ChatColor.YELLOW + "Slain!");
/*     */ 
/* 203 */         included = true;
/*     */       } else {
/* 205 */         player.sendMessage(ChatColor.YELLOW + "Slain by " + this.plugin.toName(sender) + ".");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 213 */     if ((!included) && (args.hasFlag('s')))
/* 214 */       sender.sendMessage(ChatColor.YELLOW.toString() + "Players slain.");
/*     */   }
/*     */ 
/*     */   @Command(aliases={"locate"}, usage="[player]", desc="Locate a player", max=1)
/*     */   @CommandPermissions({"worldguard.locate"})
/*     */   public void locate(CommandContext args, CommandSender sender) throws CommandException
/*     */   {
/* 222 */     Player player = this.plugin.checkPlayer(sender);
/*     */ 
/* 224 */     if (args.argsLength() == 0) {
/* 225 */       player.setCompassTarget(player.getWorld().getSpawnLocation());
/*     */ 
/* 227 */       sender.sendMessage(ChatColor.YELLOW.toString() + "Compass reset to spawn.");
/*     */     } else {
/* 229 */       Player target = this.plugin.matchSinglePlayer(sender, args.getString(0));
/* 230 */       player.setCompassTarget(target.getLocation());
/*     */ 
/* 232 */       sender.sendMessage(ChatColor.YELLOW.toString() + "Compass repointed.");
/*     */     }
/*     */   }
/*     */ 
/*     */   @Command(aliases={"stack", ";"}, usage="", desc="Stack items", max=0)
/*     */   @CommandPermissions({"worldguard.stack"})
/*     */   public void stack(CommandContext args, CommandSender sender) throws CommandException {
/* 240 */     Player player = this.plugin.checkPlayer(sender);
/* 241 */     boolean ignoreMax = this.plugin.hasPermission(player, "worldguard.stack.illegitimate");
/* 242 */     boolean ignoreDamaged = this.plugin.hasPermission(player, "worldguard.stack.damaged");
/*     */ 
/* 244 */     ItemStack[] items = player.getInventory().getContents();
/* 245 */     int len = items.length;
/*     */ 
/* 247 */     int affected = 0;
/*     */ 
/* 249 */     for (int i = 0; i < len; i++) {
/* 250 */       ItemStack item = items[i];
/*     */ 
/* 253 */       if ((item != null) && (item.getAmount() > 0) && ((ignoreMax) || (item.getMaxStackSize() != 1)))
/*     */       {
/* 258 */         int max = ignoreMax ? 64 : item.getMaxStackSize();
/*     */ 
/* 260 */         if (item.getAmount() < max) {
/* 261 */           int needed = max - item.getAmount();
/*     */ 
/* 264 */           for (int j = i + 1; j < len; j++) {
/* 265 */             ItemStack item2 = items[j];
/*     */ 
/* 268 */             if ((item2 != null) && (item2.getAmount() > 0) && ((ignoreMax) || (item.getMaxStackSize() != 1)))
/*     */             {
/* 275 */               if ((item2.getTypeId() == item.getTypeId()) && (((!ItemType.usesDamageValue(item.getTypeId())) && (ignoreDamaged)) || ((item.getDurability() == item2.getDurability()) && (((item.getItemMeta() == null) && (item2.getItemMeta() == null)) || ((item.getItemMeta() != null) && (item.getItemMeta().equals(item2.getItemMeta())))))))
/*     */               {
/* 282 */                 if (item2.getAmount() > needed) {
/* 283 */                   item.setAmount(max);
/* 284 */                   item2.setAmount(item2.getAmount() - needed);
/* 285 */                   break;
/*     */                 }
/*     */ 
/* 288 */                 items[j] = null;
/* 289 */                 item.setAmount(item.getAmount() + item2.getAmount());
/* 290 */                 needed = max - item.getAmount();
/*     */ 
/* 293 */                 affected++;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 299 */     if (affected > 0) {
/* 300 */       player.getInventory().setContents(items);
/*     */     }
/*     */ 
/* 303 */     player.sendMessage(ChatColor.YELLOW + "Items compacted into stacks!");
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.GeneralCommands
 * JD-Core Version:    0.6.2
 */