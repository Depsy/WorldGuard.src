/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.bukkit.util.CommandsManagerRegistration;
/*     */ import com.sk89q.minecraft.util.commands.CommandException;
/*     */ import com.sk89q.minecraft.util.commands.CommandPermissionsException;
/*     */ import com.sk89q.minecraft.util.commands.CommandUsageException;
/*     */ import com.sk89q.minecraft.util.commands.CommandsManager;
/*     */ import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
/*     */ import com.sk89q.minecraft.util.commands.SimpleInjector;
/*     */ import com.sk89q.minecraft.util.commands.WrappedCommandException;
/*     */ import com.sk89q.wepif.PermissionsResolverManager;
/*     */ import com.sk89q.worldedit.bukkit.WorldEditPlugin;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.bukkit.commands.GeneralCommands;
/*     */ import com.sk89q.worldguard.bukkit.commands.ProtectionCommands;
/*     */ import com.sk89q.worldguard.bukkit.commands.ToggleCommands;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import com.sk89q.worldguard.util.FatalConfigurationLoadingException;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.zip.ZipEntry;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.World.Environment;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.command.ConsoleCommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class WorldGuardPlugin extends JavaPlugin
/*     */ {
/*     */   private static WorldGuardPlugin inst;
/*     */   private final CommandsManager<CommandSender> commands;
/*     */   private final GlobalRegionManager globalRegionManager;
/*     */   private final ConfigurationManager configuration;
/*     */   private FlagStateManager flagStateManager;
/*     */ 
/*     */   public WorldGuardPlugin()
/*     */   {
/* 105 */     this.configuration = new ConfigurationManager(this);
/* 106 */     this.globalRegionManager = new GlobalRegionManager(this);
/*     */ 
/* 108 */     inst = this; final WorldGuardPlugin plugin = this;
/* 109 */     this.commands = new CommandsManager()
/*     */     {
/*     */       public boolean hasPermission(CommandSender player, String perm) {
/* 112 */         return plugin.hasPermission(player, perm);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static WorldGuardPlugin inst()
/*     */   {
/* 122 */     return inst;
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/* 133 */     this.commands.setInjector(new SimpleInjector(new Object[] { this }));
/*     */ 
/* 136 */     final CommandsManagerRegistration reg = new CommandsManagerRegistration(this, this.commands);
/* 137 */     reg.register(ToggleCommands.class);
/* 138 */     reg.register(ProtectionCommands.class);
/*     */ 
/* 140 */     getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
/*     */     {
/*     */       public void run() {
/* 143 */         if (!WorldGuardPlugin.this.getGlobalStateManager().hasCommandBookGodMode())
/* 144 */           reg.register(GeneralCommands.class);
/*     */       }
/*     */     }
/*     */     , 0L);
/*     */ 
/* 150 */     getDataFolder().mkdirs();
/*     */ 
/* 152 */     PermissionsResolverManager.initialize(this);
/*     */ 
/* 155 */     LegacyWorldGuardMigration.migrateBlacklist(this);
/*     */     try
/*     */     {
/* 159 */       this.configuration.load();
/* 160 */       this.globalRegionManager.preload();
/*     */     } catch (FatalConfigurationLoadingException e) {
/* 162 */       e.printStackTrace();
/* 163 */       getServer().shutdown();
/*     */     }
/*     */ 
/* 168 */     LegacyWorldGuardMigration.migrateRegions(this);
/*     */ 
/* 170 */     this.flagStateManager = new FlagStateManager(this);
/*     */ 
/* 172 */     if (this.configuration.useRegionsScheduler) {
/* 173 */       getServer().getScheduler().scheduleSyncRepeatingTask(this, this.flagStateManager, 20L, 20L);
/*     */     }
/*     */ 
/* 178 */     new WorldGuardPlayerListener(this).registerEvents();
/* 179 */     new WorldGuardBlockListener(this).registerEvents();
/* 180 */     new WorldGuardEntityListener(this).registerEvents();
/* 181 */     new WorldGuardWeatherListener(this).registerEvents();
/* 182 */     new WorldGuardVehicleListener(this).registerEvents();
/* 183 */     new WorldGuardServerListener(this).registerEvents();
/* 184 */     new WorldGuardHangingListener(this).registerEvents();
/* 185 */     this.configuration.updateCommandBookGodMode();
/*     */ 
/* 187 */     if (getServer().getPluginManager().isPluginEnabled("CommandBook")) {
/* 188 */       getServer().getPluginManager().registerEvents(new WorldGuardCommandBookListener(this), this);
/*     */     }
/*     */ 
/* 192 */     WorldGuardWorldListener worldListener = new WorldGuardWorldListener(this);
/* 193 */     for (World world : getServer().getWorlds()) {
/* 194 */       worldListener.initWorld(world);
/*     */     }
/* 196 */     worldListener.registerEvents();
/*     */ 
/* 198 */     if (!this.configuration.hasCommandBookGodMode())
/*     */     {
/* 200 */       for (Player player : getServer().getOnlinePlayers())
/* 201 */         if ((inGroup(player, "wg-invincible")) || ((this.configuration.autoGodMode) && (hasPermission(player, "worldguard.auto-invincible"))))
/*     */         {
/* 203 */           this.configuration.enableGodMode(player);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/* 214 */     this.globalRegionManager.unload();
/* 215 */     this.configuration.unload();
/* 216 */     getServer().getScheduler().cancelTasks(this);
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 226 */       this.commands.execute(cmd.getName(), args, sender, new Object[] { sender });
/*     */     } catch (CommandPermissionsException e) {
/* 228 */       sender.sendMessage(ChatColor.RED + "You don't have permission.");
/*     */     } catch (MissingNestedCommandException e) {
/* 230 */       sender.sendMessage(ChatColor.RED + e.getUsage());
/*     */     } catch (CommandUsageException e) {
/* 232 */       sender.sendMessage(ChatColor.RED + e.getMessage());
/* 233 */       sender.sendMessage(ChatColor.RED + e.getUsage());
/*     */     } catch (WrappedCommandException e) {
/* 235 */       if ((e.getCause() instanceof NumberFormatException)) {
/* 236 */         sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
/*     */       } else {
/* 238 */         sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
/* 239 */         e.printStackTrace();
/*     */       }
/*     */     } catch (CommandException e) {
/* 242 */       sender.sendMessage(ChatColor.RED + e.getMessage());
/*     */     }
/*     */ 
/* 245 */     return true;
/*     */   }
/*     */ 
/*     */   public GlobalRegionManager getGlobalRegionManager()
/*     */   {
/* 254 */     return this.globalRegionManager;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ConfigurationManager getGlobalConfiguration()
/*     */   {
/* 265 */     return getGlobalStateManager();
/*     */   }
/*     */ 
/*     */   public FlagStateManager getFlagStateManager()
/*     */   {
/* 274 */     return this.flagStateManager;
/*     */   }
/*     */ 
/*     */   public ConfigurationManager getGlobalStateManager()
/*     */   {
/* 283 */     return this.configuration;
/*     */   }
/*     */ 
/*     */   public boolean inGroup(Player player, String group)
/*     */   {
/*     */     try
/*     */     {
/* 296 */       return PermissionsResolverManager.getInstance().inGroup(player, group);
/*     */     } catch (Throwable t) {
/* 298 */       t.printStackTrace();
/* 299 */     }return false;
/*     */   }
/*     */ 
/*     */   public String[] getGroups(Player player)
/*     */   {
/*     */     try
/*     */     {
/* 311 */       return PermissionsResolverManager.getInstance().getGroups(player);
/*     */     } catch (Throwable t) {
/* 313 */       t.printStackTrace();
/* 314 */     }return new String[0];
/*     */   }
/*     */ 
/*     */   public String toUniqueName(CommandSender sender)
/*     */   {
/* 326 */     if ((sender instanceof ConsoleCommandSender)) {
/* 327 */       return "*Console*";
/*     */     }
/* 329 */     return sender.getName();
/*     */   }
/*     */ 
/*     */   public String toName(CommandSender sender)
/*     */   {
/* 340 */     if ((sender instanceof ConsoleCommandSender))
/* 341 */       return "*Console*";
/* 342 */     if ((sender instanceof Player)) {
/* 343 */       return ((Player)sender).getDisplayName();
/*     */     }
/* 345 */     return sender.getName();
/*     */   }
/*     */ 
/*     */   public boolean hasPermission(CommandSender sender, String perm)
/*     */   {
/* 357 */     if (sender.isOp()) {
/* 358 */       if ((sender instanceof Player)) {
/* 359 */         if (getGlobalStateManager().get(((Player)sender).getWorld()).opPermissions)
/*     */         {
/* 361 */           return true;
/*     */         }
/*     */       }
/* 364 */       else return true;
/*     */ 
/*     */     }
/*     */ 
/* 369 */     if ((sender instanceof Player)) {
/* 370 */       Player player = (Player)sender;
/* 371 */       return PermissionsResolverManager.getInstance().hasPermission(player.getWorld().getName(), player.getName(), perm);
/*     */     }
/*     */ 
/* 374 */     return false;
/*     */   }
/*     */ 
/*     */   public void checkPermission(CommandSender sender, String perm)
/*     */     throws CommandPermissionsException
/*     */   {
/* 386 */     if (!hasPermission(sender, perm))
/* 387 */       throw new CommandPermissionsException();
/*     */   }
/*     */ 
/*     */   public Player checkPlayer(CommandSender sender)
/*     */     throws CommandException
/*     */   {
/* 400 */     if ((sender instanceof Player)) {
/* 401 */       return (Player)sender;
/*     */     }
/* 403 */     throw new CommandException("A player is expected.");
/*     */   }
/*     */ 
/*     */   public List<Player> matchPlayerNames(String filter)
/*     */   {
/* 419 */     Player[] players = getServer().getOnlinePlayers();
/*     */ 
/* 421 */     filter = filter.toLowerCase();
/*     */ 
/* 424 */     if ((filter.charAt(0) == '@') && (filter.length() >= 2)) {
/* 425 */       filter = filter.substring(1);
/*     */ 
/* 427 */       for (Player player : players) {
/* 428 */         if (player.getName().equalsIgnoreCase(filter)) {
/* 429 */           List list = new ArrayList();
/* 430 */           list.add(player);
/* 431 */           return list;
/*     */         }
/*     */       }
/*     */ 
/* 435 */       return new ArrayList();
/*     */     }
/* 437 */     if ((filter.charAt(0) == '*') && (filter.length() >= 2)) {
/* 438 */       filter = filter.substring(1);
/*     */ 
/* 440 */       List list = new ArrayList();
/*     */ 
/* 442 */       for (Player player : players) {
/* 443 */         if (player.getName().toLowerCase().contains(filter)) {
/* 444 */           list.add(player);
/*     */         }
/*     */       }
/*     */ 
/* 448 */       return list;
/*     */     }
/*     */ 
/* 452 */     List list = new ArrayList();
/*     */ 
/* 454 */     for (Player player : players) {
/* 455 */       if (player.getName().toLowerCase().startsWith(filter)) {
/* 456 */         list.add(player);
/*     */       }
/*     */     }
/*     */ 
/* 460 */     return list;
/*     */   }
/*     */ 
/*     */   protected Iterable<Player> checkPlayerMatch(List<Player> players)
/*     */     throws CommandException
/*     */   {
/* 475 */     if (players.size() == 0) {
/* 476 */       throw new CommandException("No players matched query.");
/*     */     }
/*     */ 
/* 479 */     return players;
/*     */   }
/*     */ 
/*     */   public Iterable<Player> matchPlayers(CommandSender source, String filter)
/*     */     throws CommandException
/*     */   {
/* 500 */     if (getServer().getOnlinePlayers().length == 0) {
/* 501 */       throw new CommandException("No players matched query.");
/*     */     }
/*     */ 
/* 504 */     if (filter.equals("*")) {
/* 505 */       return checkPlayerMatch(Arrays.asList(getServer().getOnlinePlayers()));
/*     */     }
/*     */ 
/* 509 */     if (filter.charAt(0) == '#')
/*     */     {
/* 512 */       if (filter.equalsIgnoreCase("#world")) {
/* 513 */         List players = new ArrayList();
/* 514 */         Player sourcePlayer = checkPlayer(source);
/* 515 */         World sourceWorld = sourcePlayer.getWorld();
/*     */ 
/* 517 */         for (Player player : getServer().getOnlinePlayers()) {
/* 518 */           if (player.getWorld().equals(sourceWorld)) {
/* 519 */             players.add(player);
/*     */           }
/*     */         }
/*     */ 
/* 523 */         return checkPlayerMatch(players);
/*     */       }
/*     */ 
/* 526 */       if (filter.equalsIgnoreCase("#near")) {
/* 527 */         List players = new ArrayList();
/* 528 */         Player sourcePlayer = checkPlayer(source);
/* 529 */         World sourceWorld = sourcePlayer.getWorld();
/* 530 */         Vector sourceVector = sourcePlayer.getLocation().toVector();
/*     */ 
/* 533 */         for (Player player : getServer().getOnlinePlayers()) {
/* 534 */           if ((player.getWorld().equals(sourceWorld)) && (player.getLocation().toVector().distanceSquared(sourceVector) < 900.0D))
/*     */           {
/* 537 */             players.add(player);
/*     */           }
/*     */         }
/*     */ 
/* 541 */         return checkPlayerMatch(players);
/*     */       }
/*     */ 
/* 544 */       throw new CommandException("Invalid group '" + filter + "'.");
/*     */     }
/*     */ 
/* 548 */     List players = matchPlayerNames(filter);
/*     */ 
/* 550 */     return checkPlayerMatch(players);
/*     */   }
/*     */ 
/*     */   public Player matchSinglePlayer(CommandSender sender, String filter)
/*     */     throws CommandException
/*     */   {
/* 565 */     Iterator players = matchPlayers(sender, filter).iterator();
/*     */ 
/* 567 */     Player match = (Player)players.next();
/*     */ 
/* 572 */     if (players.hasNext()) {
/* 573 */       throw new CommandException("More than one player found! Use @<name> for exact matching.");
/*     */     }
/*     */ 
/* 577 */     return match;
/*     */   }
/*     */ 
/*     */   public CommandSender matchPlayerOrConsole(CommandSender sender, String filter)
/*     */     throws CommandException
/*     */   {
/* 595 */     if ((filter.equalsIgnoreCase("#console")) || (filter.equalsIgnoreCase("*console*")) || (filter.equalsIgnoreCase("!")))
/*     */     {
/* 598 */       return getServer().getConsoleSender();
/*     */     }
/*     */ 
/* 601 */     return matchSinglePlayer(sender, filter);
/*     */   }
/*     */ 
/*     */   public Iterable<Player> matchPlayers(Player player)
/*     */   {
/* 611 */     return Arrays.asList(new Player[] { player });
/*     */   }
/*     */ 
/*     */   public World matchWorld(CommandSender sender, String filter)
/*     */     throws CommandException
/*     */   {
/* 630 */     List worlds = getServer().getWorlds();
/*     */ 
/* 633 */     if (filter.charAt(0) == '#')
/*     */     {
/* 635 */       if (filter.equalsIgnoreCase("#main")) {
/* 636 */         return (World)worlds.get(0);
/*     */       }
/*     */ 
/* 639 */       if (filter.equalsIgnoreCase("#normal")) {
/* 640 */         for (World world : worlds) {
/* 641 */           if (world.getEnvironment() == World.Environment.NORMAL) {
/* 642 */             return world;
/*     */           }
/*     */         }
/*     */ 
/* 646 */         throw new CommandException("No normal world found.");
/*     */       }
/*     */ 
/* 649 */       if (filter.equalsIgnoreCase("#nether")) {
/* 650 */         for (World world : worlds) {
/* 651 */           if (world.getEnvironment() == World.Environment.NETHER) {
/* 652 */             return world;
/*     */           }
/*     */         }
/*     */ 
/* 656 */         throw new CommandException("No nether world found.");
/*     */       }
/*     */ 
/* 659 */       if (filter.matches("^#player$")) {
/* 660 */         String[] parts = filter.split(":", 2);
/*     */ 
/* 663 */         if (parts.length == 1) {
/* 664 */           throw new CommandException("Argument expected for #player.");
/*     */         }
/*     */ 
/* 667 */         return ((Player)matchPlayers(sender, parts[1]).iterator().next()).getWorld();
/*     */       }
/* 669 */       throw new CommandException("Invalid identifier '" + filter + "'.");
/*     */     }
/*     */ 
/* 673 */     for (World world : worlds) {
/* 674 */       if (world.getName().equals(filter)) {
/* 675 */         return world;
/*     */       }
/*     */     }
/*     */ 
/* 679 */     throw new CommandException("No world by that exact name found.");
/*     */   }
/*     */ 
/*     */   public WorldEditPlugin getWorldEdit()
/*     */     throws CommandException
/*     */   {
/* 689 */     Plugin worldEdit = getServer().getPluginManager().getPlugin("WorldEdit");
/* 690 */     if (worldEdit == null) {
/* 691 */       throw new CommandException("WorldEdit does not appear to be installed.");
/*     */     }
/*     */ 
/* 694 */     if ((worldEdit instanceof WorldEditPlugin)) {
/* 695 */       return (WorldEditPlugin)worldEdit;
/*     */     }
/* 697 */     throw new CommandException("WorldEdit detection failed (report error).");
/*     */   }
/*     */ 
/*     */   public LocalPlayer wrapPlayer(Player player)
/*     */   {
/* 708 */     return new BukkitPlayer(this, player);
/*     */   }
/*     */ 
/*     */   public void createDefaultConfiguration(File actual, String defaultName)
/*     */   {
/* 721 */     File parent = actual.getParentFile();
/* 722 */     if (!parent.exists()) {
/* 723 */       parent.mkdirs();
/*     */     }
/*     */ 
/* 726 */     if (actual.exists()) {
/* 727 */       return;
/*     */     }
/*     */ 
/* 730 */     InputStream input = null;
/*     */     try
/*     */     {
/* 733 */       JarFile file = new JarFile(getFile());
/* 734 */       ZipEntry copy = file.getEntry("defaults/" + defaultName);
/* 735 */       if (copy == null) throw new FileNotFoundException();
/* 736 */       input = file.getInputStream(copy);
/*     */     } catch (IOException e) {
/* 738 */       getLogger().severe("Unable to read default configuration: " + defaultName);
/*     */     }
/*     */ 
/* 741 */     if (input != null) {
/* 742 */       FileOutputStream output = null;
/*     */       try
/*     */       {
/* 745 */         output = new FileOutputStream(actual);
/* 746 */         byte[] buf = new byte[8192];
/* 747 */         int length = 0;
/* 748 */         while ((length = input.read(buf)) > 0) {
/* 749 */           output.write(buf, 0, length);
/*     */         }
/*     */ 
/* 752 */         getLogger().info("Default configuration file written: " + actual.getAbsolutePath());
/*     */       }
/*     */       catch (IOException e) {
/* 755 */         e.printStackTrace();
/*     */       } finally {
/*     */         try {
/* 758 */           if (input != null)
/* 759 */             input.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         }
/*     */         try {
/* 765 */           if (output != null)
/* 766 */             output.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void broadcastNotification(String msg)
/*     */   {
/* 782 */     getServer().broadcast(msg, "worldguard.notify");
/* 783 */     Set subs = getServer().getPluginManager().getPermissionSubscriptions("worldguard.notify");
/* 784 */     for (Player player : getServer().getOnlinePlayers()) {
/* 785 */       if (((!subs.contains(player)) || (!player.hasPermission("worldguard.notify"))) && (hasPermission(player, "worldguard.notify")))
/*     */       {
/* 787 */         player.sendMessage(msg);
/*     */       }
/*     */     }
/* 790 */     getLogger().info(msg);
/*     */   }
/*     */ 
/*     */   public void forgetPlayer(Player player)
/*     */   {
/* 799 */     this.flagStateManager.forget(player);
/*     */   }
/*     */ 
/*     */   public boolean canBuild(Player player, Location loc)
/*     */   {
/* 812 */     return getGlobalRegionManager().canBuild(player, loc);
/*     */   }
/*     */ 
/*     */   public boolean canBuild(Player player, Block block)
/*     */   {
/* 825 */     return getGlobalRegionManager().canBuild(player, block);
/*     */   }
/*     */ 
/*     */   public RegionManager getRegionManager(World world)
/*     */   {
/* 835 */     if (!getGlobalStateManager().get(world).useRegions) {
/* 836 */       return null;
/*     */     }
/*     */ 
/* 839 */     return getGlobalRegionManager().get(world);
/*     */   }
/*     */ 
/*     */   public String replaceMacros(CommandSender sender, String message)
/*     */   {
/* 858 */     Player[] online = getServer().getOnlinePlayers();
/*     */ 
/* 860 */     message = message.replace("%name%", toName(sender));
/* 861 */     message = message.replace("%id%", toUniqueName(sender));
/* 862 */     message = message.replace("%online%", String.valueOf(online.length));
/*     */ 
/* 864 */     if ((sender instanceof Player)) {
/* 865 */       Player player = (Player)sender;
/* 866 */       World world = player.getWorld();
/*     */ 
/* 868 */       message = message.replace("%world%", world.getName());
/* 869 */       message = message.replace("%health%", String.valueOf(player.getHealth()));
/*     */     }
/*     */ 
/* 872 */     return message;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardPlugin
 * JD-Core Version:    0.6.2
 */