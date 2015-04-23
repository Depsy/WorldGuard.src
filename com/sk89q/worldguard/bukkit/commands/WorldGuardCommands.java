/*     */ package com.sk89q.worldguard.bukkit.commands;
/*     */ 
/*     */ import com.sk89q.minecraft.util.commands.Command;
/*     */ import com.sk89q.minecraft.util.commands.CommandContext;
/*     */ import com.sk89q.minecraft.util.commands.CommandException;
/*     */ import com.sk89q.minecraft.util.commands.CommandPermissions;
/*     */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*     */ import com.sk89q.worldguard.bukkit.FlagStateManager;
/*     */ import com.sk89q.worldguard.bukkit.LoggerToChatHandler;
/*     */ import com.sk89q.worldguard.bukkit.ReportWriter;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.util.PastebinPoster;
/*     */ import com.sk89q.worldguard.util.PastebinPoster.PasteCallback;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ 
/*     */ public class WorldGuardCommands
/*     */ {
/*     */   private final WorldGuardPlugin plugin;
/*     */ 
/*     */   public WorldGuardCommands(WorldGuardPlugin plugin)
/*     */   {
/*  45 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   @Command(aliases={"version"}, desc="Get the WorldGuard version", max=0)
/*     */   public void version(CommandContext args, CommandSender sender) throws CommandException {
/*  50 */     sender.sendMessage(ChatColor.YELLOW + "WorldGuard " + this.plugin.getDescription().getVersion());
/*     */ 
/*  52 */     sender.sendMessage(ChatColor.YELLOW + "http://www.sk89q.com");
/*     */   }
/*     */ 
/*     */   @Command(aliases={"reload"}, desc="Reload WorldGuard configuration", max=0)
/*     */   @CommandPermissions({"worldguard.reload"})
/*     */   public void reload(CommandContext args, CommandSender sender) throws CommandException
/*     */   {
/*  60 */     LoggerToChatHandler handler = null;
/*  61 */     Logger minecraftLogger = null;
/*     */ 
/*  63 */     if ((sender instanceof Player)) {
/*  64 */       handler = new LoggerToChatHandler(sender);
/*  65 */       handler.setLevel(Level.ALL);
/*  66 */       minecraftLogger = Logger.getLogger("Minecraft");
/*  67 */       minecraftLogger.addHandler(handler);
/*     */     }
/*     */     try
/*     */     {
/*  71 */       this.plugin.getGlobalStateManager().unload();
/*  72 */       this.plugin.getGlobalRegionManager().unload();
/*  73 */       this.plugin.getGlobalStateManager().load();
/*  74 */       this.plugin.getGlobalRegionManager().preload();
/*     */ 
/*  76 */       sender.sendMessage("WorldGuard configuration reloaded.");
/*     */     } catch (Throwable t) {
/*  78 */       sender.sendMessage("Error while reloading: " + t.getMessage());
/*     */     }
/*     */     finally {
/*  81 */       if (minecraftLogger != null)
/*  82 */         minecraftLogger.removeHandler(handler);
/*     */     }
/*     */   }
/*     */ 
/*     */   @Command(aliases={"report"}, desc="Writes a report on WorldGuard", flags="p", max=0)
/*     */   @CommandPermissions({"worldguard.report"})
/*     */   public void report(CommandContext args, final CommandSender sender) throws CommandException
/*     */   {
/*  91 */     File dest = new File(this.plugin.getDataFolder(), "report.txt");
/*  92 */     ReportWriter report = new ReportWriter(this.plugin);
/*     */     try
/*     */     {
/*  95 */       report.write(dest);
/*  96 */       sender.sendMessage(ChatColor.YELLOW + "WorldGuard report written to " + dest.getAbsolutePath());
/*     */     }
/*     */     catch (IOException e) {
/*  99 */       throw new CommandException("Failed to write report: " + e.getMessage());
/*     */     }
/*     */ 
/* 102 */     if (args.hasFlag('p')) {
/* 103 */       this.plugin.checkPermission(sender, "worldguard.report.pastebin");
/*     */ 
/* 105 */       sender.sendMessage(ChatColor.YELLOW + "Now uploading to Pastebin...");
/* 106 */       PastebinPoster.paste(report.toString(), new PastebinPoster.PasteCallback()
/*     */       {
/*     */         public void handleSuccess(String url)
/*     */         {
/* 110 */           sender.sendMessage(ChatColor.YELLOW + "WorldGuard report (1 hour): " + url);
/*     */         }
/*     */ 
/*     */         public void handleError(String err)
/*     */         {
/* 115 */           sender.sendMessage(ChatColor.YELLOW + "WorldGuard report pastebin error: " + err);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   @Command(aliases={"flushstates", "clearstates"}, usage="[player]", desc="Flush the state manager", max=1)
/*     */   @CommandPermissions({"worldguard.flushstates"})
/*     */   public void flushStates(CommandContext args, CommandSender sender) throws CommandException
/*     */   {
/* 126 */     if (args.argsLength() == 0) {
/* 127 */       this.plugin.getFlagStateManager().forgetAll();
/* 128 */       sender.sendMessage("Cleared all states.");
/*     */     } else {
/* 130 */       Player player = this.plugin.getServer().getPlayer(args.getString(0));
/* 131 */       if (player != null) {
/* 132 */         this.plugin.getFlagStateManager().forget(player);
/* 133 */         sender.sendMessage("Cleared states for player \"" + player.getName() + "\".");
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.WorldGuardCommands
 * JD-Core Version:    0.6.2
 */