/*      */ package com.sk89q.worldguard.bukkit.commands;
/*      */ 
/*      */ import com.sk89q.minecraft.util.commands.Command;
/*      */ import com.sk89q.minecraft.util.commands.CommandContext;
/*      */ import com.sk89q.minecraft.util.commands.CommandException;
/*      */ import com.sk89q.minecraft.util.commands.CommandPermissionsException;
/*      */ import com.sk89q.worldedit.BlockVector;
/*      */ import com.sk89q.worldedit.Location;
/*      */ import com.sk89q.worldedit.Vector;
/*      */ import com.sk89q.worldedit.bukkit.BukkitUtil;
/*      */ import com.sk89q.worldedit.bukkit.WorldEditPlugin;
/*      */ import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
/*      */ import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
/*      */ import com.sk89q.worldedit.bukkit.selections.Selection;
/*      */ import com.sk89q.worldguard.LocalPlayer;
/*      */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*      */ import com.sk89q.worldguard.bukkit.RegionPermissionModel;
/*      */ import com.sk89q.worldguard.bukkit.WorldConfiguration;
/*      */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*      */ import com.sk89q.worldguard.domains.DefaultDomain;
/*      */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*      */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*      */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*      */ import com.sk89q.worldguard.protection.databases.RegionDBUtil;
/*      */ import com.sk89q.worldguard.protection.databases.migrators.AbstractDatabaseMigrator;
/*      */ import com.sk89q.worldguard.protection.databases.migrators.MigrationException;
/*      */ import com.sk89q.worldguard.protection.databases.migrators.MigratorKey;
/*      */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*      */ import com.sk89q.worldguard.protection.flags.Flag;
/*      */ import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
/*      */ import com.sk89q.worldguard.protection.flags.RegionGroup;
/*      */ import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
/*      */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*      */ import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.command.CommandSender;
/*      */ import org.bukkit.entity.Player;
/*      */ 
/*      */ public final class RegionCommands
/*      */ {
/*      */   private final WorldGuardPlugin plugin;
/*      */   private MigratorKey migrateDBRequest;
/*      */   private Date migrateDBRequestDate;
/*      */ 
/*      */   public RegionCommands(WorldGuardPlugin plugin)
/*      */   {
/*   78 */     this.plugin = plugin;
/*      */   }
/*      */ 
/*      */   private static RegionPermissionModel getPermissionModel(CommandSender sender)
/*      */   {
/*   88 */     return new RegionPermissionModel(WorldGuardPlugin.inst(), sender);
/*      */   }
/*      */ 
/*      */   private static World getWorld(CommandContext args, CommandSender sender, char flag)
/*      */     throws CommandException
/*      */   {
/*  103 */     if (args.hasFlag(flag)) {
/*  104 */       return WorldGuardPlugin.inst().matchWorld(sender, args.getFlag(flag));
/*      */     }
/*  106 */     if ((sender instanceof Player)) {
/*  107 */       return WorldGuardPlugin.inst().checkPlayer(sender).getWorld();
/*      */     }
/*  109 */     throw new CommandException(new StringBuilder().append("Please specify the world with -").append(flag).append(" world_name.").toString());
/*      */   }
/*      */ 
/*      */   private static String validateRegionId(String id, boolean allowGlobal)
/*      */     throws CommandException
/*      */   {
/*  125 */     if (!ProtectedRegion.isValidId(id)) {
/*  126 */       throw new CommandException(new StringBuilder().append("The region name of '").append(id).append("' contains characters that are not allowed.").toString());
/*      */     }
/*      */ 
/*  130 */     if ((!allowGlobal) && (id.equalsIgnoreCase("__global__"))) {
/*  131 */       throw new CommandException("Sorry, you can't use __global__ here.");
/*      */     }
/*      */ 
/*  135 */     return id;
/*      */   }
/*      */ 
/*      */   private static ProtectedRegion findExistingRegion(RegionManager regionManager, String id, boolean allowGlobal)
/*      */     throws CommandException
/*      */   {
/*  153 */     validateRegionId(id, allowGlobal);
/*      */ 
/*  155 */     ProtectedRegion region = regionManager.getRegionExact(id);
/*      */ 
/*  158 */     if (region == null)
/*      */     {
/*  160 */       if (id.equalsIgnoreCase("__global__")) {
/*  161 */         region = new GlobalProtectedRegion(id);
/*  162 */         regionManager.addRegion(region);
/*  163 */         return region;
/*      */       }
/*      */ 
/*  166 */       throw new CommandException(new StringBuilder().append("No region could be found with the name of '").append(id).append("'.").toString());
/*      */     }
/*      */ 
/*  170 */     return region;
/*      */   }
/*      */ 
/*      */   private static ProtectedRegion findRegionStandingIn(RegionManager regionManager, Player player)
/*      */     throws CommandException
/*      */   {
/*  187 */     return findRegionStandingIn(regionManager, player, false);
/*      */   }
/*      */ 
/*      */   private static ProtectedRegion findRegionStandingIn(RegionManager regionManager, Player player, boolean allowGlobal)
/*      */     throws CommandException
/*      */   {
/*  207 */     ApplicableRegionSet set = regionManager.getApplicableRegions(player.getLocation());
/*      */ 
/*  210 */     if (set.size() == 0) {
/*  211 */       if (allowGlobal) {
/*  212 */         ProtectedRegion global = findExistingRegion(regionManager, "__global__", true);
/*  213 */         player.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("You're not standing in any ").append("regions. Using the global region for this world instead.").toString());
/*      */ 
/*  215 */         return global;
/*      */       }
/*  217 */       throw new CommandException("You're not standing in a region.Specify an ID if you want to select a specific region.");
/*      */     }
/*      */ 
/*  220 */     if (set.size() > 1) {
/*  221 */       StringBuilder builder = new StringBuilder();
/*  222 */       boolean first = true;
/*      */ 
/*  224 */       for (ProtectedRegion region : set) {
/*  225 */         if (!first) {
/*  226 */           builder.append(", ");
/*      */         }
/*  228 */         first = false;
/*  229 */         builder.append(region.getId());
/*      */       }
/*      */ 
/*  232 */       throw new CommandException(new StringBuilder().append("You're standing in several regions, and WorldGuard is not sure what you want.\nYou're in: ").append(builder.toString()).toString());
/*      */     }
/*      */ 
/*  238 */     return (ProtectedRegion)set.iterator().next();
/*      */   }
/*      */ 
/*      */   private static Selection getSelection(Player player)
/*      */     throws CommandException
/*      */   {
/*  250 */     WorldEditPlugin worldEdit = WorldGuardPlugin.inst().getWorldEdit();
/*  251 */     Selection selection = worldEdit.getSelection(player);
/*      */ 
/*  253 */     if (selection == null) {
/*  254 */       throw new CommandException("Please select an area first. Use WorldEdit to make a selection! (wiki: http://wiki.sk89q.com/wiki/WorldEdit).");
/*      */     }
/*      */ 
/*  260 */     return selection;
/*      */   }
/*      */ 
/*      */   private static ProtectedRegion createRegionFromSelection(Player player, String id)
/*      */     throws CommandException
/*      */   {
/*  274 */     Selection selection = getSelection(player);
/*      */ 
/*  277 */     if ((selection instanceof Polygonal2DSelection)) {
/*  278 */       Polygonal2DSelection polySel = (Polygonal2DSelection)selection;
/*  279 */       int minY = polySel.getNativeMinimumPoint().getBlockY();
/*  280 */       int maxY = polySel.getNativeMaximumPoint().getBlockY();
/*  281 */       return new ProtectedPolygonalRegion(id, polySel.getNativePoints(), minY, maxY);
/*  282 */     }if ((selection instanceof CuboidSelection)) {
/*  283 */       BlockVector min = selection.getNativeMinimumPoint().toBlockVector();
/*  284 */       BlockVector max = selection.getNativeMaximumPoint().toBlockVector();
/*  285 */       return new ProtectedCuboidRegion(id, min, max);
/*      */     }
/*  287 */     throw new CommandException("Sorry, you can only use cuboids and polygons for WorldGuard regions.");
/*      */   }
/*      */ 
/*      */   private static void commitChanges(CommandSender sender, RegionManager regionManager)
/*      */     throws CommandException
/*      */   {
/*  294 */     commitChanges(sender, regionManager, false);
/*      */   }
/*      */ 
/*      */   private static void reloadChanges(CommandSender sender, RegionManager regionManager) throws CommandException
/*      */   {
/*  299 */     reloadChanges(sender, regionManager, false);
/*      */   }
/*      */ 
/*      */   private static void commitChanges(CommandSender sender, RegionManager regionManager, boolean silent)
/*      */     throws CommandException
/*      */   {
/*      */     try
/*      */     {
/*  313 */       if ((!silent) && (regionManager.getRegions().size() >= 500)) {
/*  314 */         sender.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("Now saving region list to disk... (Taking too long? We're fixing it)").toString());
/*      */       }
/*      */ 
/*  317 */       regionManager.save();
/*      */     } catch (ProtectionDatabaseException e) {
/*  319 */       throw new CommandException(new StringBuilder().append("Uh oh, regions did not save: ").append(e.getMessage()).toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void reloadChanges(CommandSender sender, RegionManager regionManager, boolean silent)
/*      */     throws CommandException
/*      */   {
/*      */     try
/*      */     {
/*  334 */       if ((!silent) && (regionManager.getRegions().size() >= 500)) {
/*  335 */         sender.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("Now loading region list from disk... (Taking too long? We're fixing it)").toString());
/*      */       }
/*      */ 
/*  338 */       regionManager.load();
/*      */     } catch (ProtectionDatabaseException e) {
/*  340 */       throw new CommandException(new StringBuilder().append("Uh oh, regions did not load: ").append(e.getMessage()).toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setPlayerSelection(Player player, ProtectedRegion region)
/*      */     throws CommandException
/*      */   {
/*  353 */     WorldEditPlugin worldEdit = WorldGuardPlugin.inst().getWorldEdit();
/*      */ 
/*  355 */     World world = player.getWorld();
/*      */ 
/*  358 */     if ((region instanceof ProtectedCuboidRegion)) {
/*  359 */       ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion)region;
/*  360 */       Vector pt1 = cuboid.getMinimumPoint();
/*  361 */       Vector pt2 = cuboid.getMaximumPoint();
/*  362 */       CuboidSelection selection = new CuboidSelection(world, pt1, pt2);
/*  363 */       worldEdit.setSelection(player, selection);
/*  364 */       player.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region selected as a cuboid.").toString());
/*      */     }
/*  366 */     else if ((region instanceof ProtectedPolygonalRegion)) {
/*  367 */       ProtectedPolygonalRegion poly2d = (ProtectedPolygonalRegion)region;
/*  368 */       Polygonal2DSelection selection = new Polygonal2DSelection(world, poly2d.getPoints(), poly2d.getMinimumPoint().getBlockY(), poly2d.getMaximumPoint().getBlockY());
/*      */ 
/*  372 */       worldEdit.setSelection(player, selection);
/*  373 */       player.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region selected as a polygon.").toString());
/*      */     } else {
/*  375 */       if ((region instanceof GlobalProtectedRegion)) {
/*  376 */         throw new CommandException("Can't select global regions! That would cover the entire world.");
/*      */       }
/*      */ 
/*  381 */       throw new CommandException(new StringBuilder().append("Unknown region type: ").append(region.getClass().getCanonicalName()).toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static <V> void setFlag(ProtectedRegion region, Flag<V> flag, CommandSender sender, String value)
/*      */     throws InvalidFlagFormat
/*      */   {
/*  398 */     region.setFlag(flag, flag.parseInput(WorldGuardPlugin.inst(), sender, value));
/*      */   }
/*      */ 
/*      */   @Command(aliases={"define", "def", "d", "create"}, usage="<id> [<owner1> [<owner2> [<owners...>]]]", desc="Defines a region", min=1)
/*      */   public void define(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  414 */     Player player = this.plugin.checkPlayer(sender);
/*      */ 
/*  417 */     if (!getPermissionModel(sender).mayDefine()) {
/*  418 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  422 */     String id = validateRegionId(args.getString(0), false);
/*      */ 
/*  425 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*  426 */     if (regionManager.hasRegion(id)) {
/*  427 */       throw new CommandException(new StringBuilder().append("That region is already defined. To change the shape, use /region redefine ").append(id).toString());
/*      */     }
/*      */ 
/*  433 */     ProtectedRegion region = createRegionFromSelection(player, id);
/*      */ 
/*  436 */     if (args.argsLength() > 1) {
/*  437 */       region.setOwners(RegionDBUtil.parseDomainString(args.getSlice(1), 1));
/*      */     }
/*      */ 
/*  440 */     regionManager.addRegion(region);
/*  441 */     commitChanges(sender, regionManager);
/*      */ 
/*  444 */     int height = region.getMaximumPoint().getBlockY() - region.getMinimumPoint().getBlockY();
/*  445 */     if (height <= 2) {
/*  446 */       sender.sendMessage(new StringBuilder().append(ChatColor.GOLD).append("(Warning: The height of the region was ").append(height + 1).append(" block(s).)").toString());
/*      */     }
/*      */ 
/*  451 */     if (regionManager.getRegions().size() <= 2) {
/*  452 */       sender.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("(This region is NOW PROTECTED from modification from others. ").append("Don't want that? Use ").append(ChatColor.AQUA).append("/rg flag ").append(id).append(" passthrough allow").append(ChatColor.GRAY).append(")").toString());
/*      */     }
/*      */ 
/*  460 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("A new region has been made named '").append(id).append("'.").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"redefine", "update", "move"}, usage="<id>", desc="Re-defines the shape of a region", min=1, max=1)
/*      */   public void redefine(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  476 */     Player player = this.plugin.checkPlayer(sender);
/*  477 */     World world = player.getWorld();
/*      */ 
/*  480 */     String id = validateRegionId(args.getString(0), false);
/*      */ 
/*  483 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/*  484 */     ProtectedRegion existing = findExistingRegion(regionManager, id, false);
/*      */ 
/*  487 */     if (!getPermissionModel(sender).mayRedefine(existing)) {
/*  488 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  492 */     ProtectedRegion region = createRegionFromSelection(player, id);
/*      */ 
/*  495 */     region.setMembers(existing.getMembers());
/*  496 */     region.setOwners(existing.getOwners());
/*  497 */     region.setFlags(existing.getFlags());
/*  498 */     region.setPriority(existing.getPriority());
/*      */     try {
/*  500 */       region.setParent(existing.getParent());
/*      */     }
/*      */     catch (ProtectedRegion.CircularInheritanceException ignore)
/*      */     {
/*      */     }
/*  505 */     regionManager.addRegion(region);
/*  506 */     commitChanges(sender, regionManager);
/*      */ 
/*  509 */     int height = region.getMaximumPoint().getBlockY() - region.getMinimumPoint().getBlockY();
/*  510 */     if (height <= 2) {
/*  511 */       sender.sendMessage(new StringBuilder().append(ChatColor.GOLD).append("(Warning: The height of the region was ").append(height + 1).append(" block(s).)").toString());
/*      */     }
/*      */ 
/*  515 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region '").append(id).append("' updated with new area.").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"claim"}, usage="<id> [<owner1> [<owner2> [<owners...>]]]", desc="Claim a region", min=1)
/*      */   public void claim(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  533 */     Player player = this.plugin.checkPlayer(sender);
/*  534 */     LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*  535 */     RegionPermissionModel permModel = getPermissionModel(sender);
/*      */ 
/*  538 */     if (!permModel.mayClaim()) {
/*  539 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  543 */     String id = validateRegionId(args.getString(0), false);
/*      */ 
/*  546 */     RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*  547 */     if (mgr.hasRegion(id)) {
/*  548 */       throw new CommandException("That region already exists. Please choose a different name.");
/*      */     }
/*      */ 
/*  553 */     ProtectedRegion region = createRegionFromSelection(player, id);
/*      */ 
/*  556 */     if (args.argsLength() > 1) {
/*  557 */       region.setOwners(RegionDBUtil.parseDomainString(args.getSlice(1), 1));
/*      */     }
/*      */ 
/*  560 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(player.getWorld());
/*      */ 
/*  563 */     if (!permModel.mayClaimRegionsUnbounded()) {
/*  564 */       int maxRegionCount = wcfg.getMaxRegionCount(player);
/*  565 */       if ((maxRegionCount >= 0) && (mgr.getRegionCountOfPlayer(localPlayer) >= maxRegionCount))
/*      */       {
/*  567 */         throw new CommandException("You own too many regions, delete one first to claim a new one.");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  572 */     ProtectedRegion existing = mgr.getRegionExact(id);
/*      */ 
/*  575 */     if ((existing != null) && 
/*  576 */       (!existing.getOwners().contains(localPlayer))) {
/*  577 */       throw new CommandException("This region already exists and you don't own it.");
/*      */     }
/*      */ 
/*  583 */     ApplicableRegionSet regions = mgr.getApplicableRegions(region);
/*      */ 
/*  586 */     if (regions.size() > 0) {
/*  587 */       if (!regions.isOwnerOfAll(localPlayer)) {
/*  588 */         throw new CommandException("This region overlaps with someone else's region.");
/*      */       }
/*      */     }
/*  591 */     else if (wcfg.claimOnlyInsideExistingRegions) {
/*  592 */       throw new CommandException("You may only claim regions inside existing regions that you or your group own.");
/*      */     }
/*      */ 
/*  598 */     if ((!permModel.mayClaimRegionsUnbounded()) && 
/*  599 */       (region.volume() > wcfg.maxClaimVolume)) {
/*  600 */       player.sendMessage(new StringBuilder().append(ChatColor.RED).append("This region is too large to claim.").toString());
/*  601 */       player.sendMessage(new StringBuilder().append(ChatColor.RED).append("Max. volume: ").append(wcfg.maxClaimVolume).append(", your volume: ").append(region.volume()).toString());
/*      */ 
/*  603 */       return;
/*      */     }
/*      */ 
/*  630 */     region.getOwners().addPlayer(player.getName());
/*      */ 
/*  632 */     mgr.addRegion(region);
/*  633 */     commitChanges(sender, mgr);
/*  634 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region '").append(id).append("' updated with new area.").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"select", "sel", "s"}, usage="[id]", desc="Load a region as a WorldEdit selection", min=0, max=1)
/*      */   public void select(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  649 */     Player player = this.plugin.checkPlayer(sender);
/*  650 */     World world = player.getWorld();
/*  651 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/*      */     ProtectedRegion existing;
/*      */     ProtectedRegion existing;
/*  655 */     if (args.argsLength() == 0)
/*  656 */       existing = findRegionStandingIn(regionManager, player);
/*      */     else {
/*  658 */       existing = findExistingRegion(regionManager, args.getString(0), false);
/*      */     }
/*      */ 
/*  662 */     if (!getPermissionModel(sender).maySelect(existing)) {
/*  663 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  667 */     setPlayerSelection(player, existing);
/*      */   }
/*      */ 
/*      */   @Command(aliases={"info", "i"}, usage="[id]", flags="sw:", desc="Get information about a region", min=0, max=1)
/*      */   public void info(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  683 */     World world = getWorld(args, sender, 'w');
/*  684 */     RegionPermissionModel permModel = getPermissionModel(sender);
/*      */ 
/*  687 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/*      */     ProtectedRegion existing;
/*      */     ProtectedRegion existing;
/*  690 */     if (args.argsLength() == 0) {
/*  691 */       if (!(sender instanceof Player)) {
/*  692 */         throw new CommandException("Please specify the region with /region info -w world_name region_name.");
/*      */       }
/*      */ 
/*  696 */       existing = findRegionStandingIn(regionManager, (Player)sender, true);
/*      */     } else {
/*  698 */       existing = findExistingRegion(regionManager, args.getString(0), true);
/*      */     }
/*      */ 
/*  702 */     if (!permModel.mayLookup(existing)) {
/*  703 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  707 */     RegionPrintoutBuilder printout = new RegionPrintoutBuilder(existing);
/*  708 */     printout.appendRegionInfo();
/*  709 */     printout.send(sender);
/*      */ 
/*  712 */     if (args.hasFlag('s'))
/*      */     {
/*  714 */       if (!permModel.maySelect(existing)) {
/*  715 */         throw new CommandPermissionsException();
/*      */       }
/*      */ 
/*  718 */       setPlayerSelection(this.plugin.checkPlayer(sender), existing);
/*      */     }
/*      */   }
/*      */ 
/*      */   @Command(aliases={"list"}, usage="[page]", desc="Get a list of regions", flags="p:w:", max=1)
/*      */   public void list(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  735 */     World world = getWorld(args, sender, 'w');
/*      */ 
/*  739 */     int page = args.getInteger(0, 1) - 1;
/*  740 */     if (page < 0)
/*  741 */       page = 0;
/*      */     String ownedBy;
/*      */     String ownedBy;
/*  745 */     if (args.hasFlag('p'))
/*  746 */       ownedBy = args.getFlag('p');
/*      */     else {
/*  748 */       ownedBy = null;
/*      */     }
/*      */ 
/*  752 */     if (!getPermissionModel(sender).mayList(ownedBy)) {
/*  753 */       ownedBy = sender.getName();
/*  754 */       if (!getPermissionModel(sender).mayList(ownedBy)) {
/*  755 */         throw new CommandPermissionsException();
/*      */       }
/*      */     }
/*      */ 
/*  759 */     RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/*  760 */     Map regions = mgr.getRegions();
/*      */ 
/*  763 */     List entries = new ArrayList();
/*      */ 
/*  765 */     int index = 0;
/*  766 */     for (String id : regions.keySet()) {
/*  767 */       RegionListEntry entry = new RegionListEntry(id, index++);
/*      */ 
/*  770 */       if (ownedBy != null) {
/*  771 */         entry.isOwner = ((ProtectedRegion)regions.get(id)).isOwner(ownedBy);
/*  772 */         entry.isMember = ((ProtectedRegion)regions.get(id)).isMember(ownedBy);
/*      */ 
/*  774 */         if ((!entry.isOwner) && (!entry.isMember));
/*      */       }
/*      */       else {
/*  779 */         entries.add(entry);
/*      */       }
/*      */     }
/*  782 */     Collections.sort(entries);
/*      */ 
/*  784 */     int totalSize = entries.size();
/*  785 */     int pageSize = 10;
/*  786 */     int pages = (int)Math.ceil(totalSize / 10.0F);
/*      */ 
/*  788 */     sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(ownedBy == null ? "Regions (page " : new StringBuilder().append("Regions for ").append(ownedBy).append(" (page ").toString()).append(page + 1).append(" of ").append(pages).append("):").toString());
/*      */ 
/*  792 */     if (page < pages)
/*      */     {
/*  794 */       for (int i = page * 10; (i < page * 10 + 10) && 
/*  795 */         (i < totalSize); i++)
/*      */       {
/*  799 */         sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW.toString()).append(entries.get(i)).toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @Command(aliases={"flag", "f"}, usage="<id> <flag> [-w world] [-g group] [value]", flags="g:w:", desc="Set flags", min=2)
/*      */   public void flag(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  817 */     World world = getWorld(args, sender, 'w');
/*  818 */     String flagName = args.getString(1);
/*  819 */     String value = args.argsLength() >= 3 ? args.getJoinedStrings(2) : null;
/*  820 */     RegionGroup groupValue = null;
/*  821 */     RegionPermissionModel permModel = getPermissionModel(sender);
/*      */ 
/*  824 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/*  825 */     ProtectedRegion existing = findExistingRegion(regionManager, args.getString(0), true);
/*      */ 
/*  829 */     if (!permModel.maySetFlag(existing)) {
/*  830 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  833 */     Flag foundFlag = DefaultFlag.fuzzyMatchFlag(flagName);
/*      */ 
/*  837 */     if (foundFlag == null) {
/*  838 */       StringBuilder list = new StringBuilder();
/*      */ 
/*  841 */       for (Flag flag : DefaultFlag.getFlags())
/*      */       {
/*  843 */         if (permModel.maySetFlag(existing, flag))
/*      */         {
/*  847 */           if (list.length() > 0) {
/*  848 */             list.append(", ");
/*      */           }
/*      */ 
/*  851 */           list.append(flag.getName());
/*      */         }
/*      */       }
/*  854 */       sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Unknown flag specified: ").append(flagName).toString());
/*  855 */       sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("Available flags: ").append(list).toString());
/*      */ 
/*  857 */       return;
/*      */     }
/*      */ 
/*  863 */     if (!permModel.maySetFlag(existing, foundFlag)) {
/*  864 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  868 */     if (args.hasFlag('g')) {
/*  869 */       String group = args.getFlag('g');
/*  870 */       RegionGroupFlag groupFlag = foundFlag.getRegionGroupFlag();
/*      */ 
/*  872 */       if (groupFlag == null) {
/*  873 */         throw new CommandException(new StringBuilder().append("Region flag '").append(foundFlag.getName()).append("' does not have a group flag!").toString());
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  880 */         groupValue = (RegionGroup)groupFlag.parseInput(this.plugin, sender, group);
/*      */       } catch (InvalidFlagFormat e) {
/*  882 */         throw new CommandException(e.getMessage());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  888 */     if (value != null)
/*      */     {
/*      */       try {
/*  891 */         setFlag(existing, foundFlag, sender, value);
/*      */       } catch (InvalidFlagFormat e) {
/*  893 */         throw new CommandException(e.getMessage());
/*      */       }
/*      */ 
/*  896 */       sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region flag ").append(foundFlag.getName()).append(" set on '").append(existing.getId()).append("' to '").append(value).append("'.").toString());
/*      */     }
/*  901 */     else if (!args.hasFlag('g'))
/*      */     {
/*  903 */       existing.setFlag(foundFlag, null);
/*      */ 
/*  906 */       RegionGroupFlag groupFlag = foundFlag.getRegionGroupFlag();
/*  907 */       if (groupFlag != null) {
/*  908 */         existing.setFlag(groupFlag, null);
/*      */       }
/*      */ 
/*  911 */       sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region flag ").append(foundFlag.getName()).append(" removed from '").append(existing.getId()).append("'. (Any -g(roups) were also removed.)").toString());
/*      */     }
/*      */ 
/*  917 */     if (groupValue != null) {
/*  918 */       RegionGroupFlag groupFlag = foundFlag.getRegionGroupFlag();
/*      */ 
/*  921 */       if (groupValue == groupFlag.getDefault()) {
/*  922 */         existing.setFlag(groupFlag, null);
/*  923 */         sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region group flag for '").append(foundFlag.getName()).append("' reset to ").append("default.").toString());
/*      */       }
/*      */       else
/*      */       {
/*  927 */         existing.setFlag(groupFlag, groupValue);
/*  928 */         sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region group flag for '").append(foundFlag.getName()).append("' set.").toString());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  933 */     commitChanges(sender, regionManager);
/*      */ 
/*  936 */     RegionPrintoutBuilder printout = new RegionPrintoutBuilder(existing);
/*  937 */     printout.append(ChatColor.GRAY);
/*  938 */     printout.append("(Current flags: ");
/*  939 */     printout.appendFlagsList(false);
/*  940 */     printout.append(")");
/*  941 */     printout.send(sender);
/*      */   }
/*      */ 
/*      */   @Command(aliases={"setpriority", "priority", "pri"}, usage="<id> <priority>", flags="w:", desc="Set the priority of a region", min=2, max=2)
/*      */   public void setPriority(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  958 */     World world = getWorld(args, sender, 'w');
/*  959 */     int priority = args.getInteger(1);
/*      */ 
/*  962 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/*  963 */     ProtectedRegion existing = findExistingRegion(regionManager, args.getString(0), false);
/*      */ 
/*  967 */     if (!getPermissionModel(sender).maySetPriority(existing)) {
/*  968 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/*  971 */     existing.setPriority(priority);
/*  972 */     commitChanges(sender, regionManager);
/*      */ 
/*  974 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Priority of '").append(existing.getId()).append("' set to ").append(priority).append(" (higher numbers override).").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"setparent", "parent", "par"}, usage="<id> [parent-id]", flags="w:", desc="Set the parent of a region", min=1, max=2)
/*      */   public void setParent(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/*  992 */     World world = getWorld(args, sender, 'w');
/*      */ 
/*  997 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/*      */ 
/* 1000 */     ProtectedRegion child = findExistingRegion(regionManager, args.getString(0), false);
/*      */     ProtectedRegion parent;
/*      */     ProtectedRegion parent;
/* 1001 */     if (args.argsLength() == 2)
/* 1002 */       parent = findExistingRegion(regionManager, args.getString(1), false);
/*      */     else {
/* 1004 */       parent = null;
/*      */     }
/*      */ 
/* 1008 */     if (!getPermissionModel(sender).maySetParent(child, parent)) {
/* 1009 */       throw new CommandPermissionsException();
/*      */     }
/*      */     try
/*      */     {
/* 1013 */       child.setParent(parent);
/*      */     }
/*      */     catch (ProtectedRegion.CircularInheritanceException e) {
/* 1016 */       RegionPrintoutBuilder printout = new RegionPrintoutBuilder(parent);
/* 1017 */       printout.append(ChatColor.RED);
/* 1018 */       printout.append(new StringBuilder().append("Uh oh! Setting '").append(parent.getId()).append("' to be the parent ").append("of '").append(child.getId()).append("' would cause circular inheritance.\n").toString());
/*      */ 
/* 1020 */       printout.append(ChatColor.GRAY);
/* 1021 */       printout.append(new StringBuilder().append("(Current inheritance on '").append(parent.getId()).append("':\n").toString());
/* 1022 */       printout.appendParentTree(true);
/* 1023 */       printout.append(ChatColor.GRAY);
/* 1024 */       printout.append(")");
/* 1025 */       printout.send(sender);
/* 1026 */       return;
/*      */     }
/*      */ 
/* 1029 */     commitChanges(sender, regionManager);
/*      */ 
/* 1032 */     RegionPrintoutBuilder printout = new RegionPrintoutBuilder(child);
/* 1033 */     printout.append(ChatColor.YELLOW);
/* 1034 */     printout.append(new StringBuilder().append("Inheritance set for region '").append(child.getId()).append("'.\n").toString());
/* 1035 */     if (parent != null) {
/* 1036 */       printout.append(ChatColor.GRAY);
/* 1037 */       printout.append("(Current inheritance:\n");
/* 1038 */       printout.appendParentTree(true);
/* 1039 */       printout.append(ChatColor.GRAY);
/* 1040 */       printout.append(")");
/*      */     }
/* 1042 */     printout.send(sender);
/*      */   }
/*      */ 
/*      */   @Command(aliases={"remove", "delete", "del", "rem"}, usage="<id>", flags="w:", desc="Remove a region", min=1, max=1)
/*      */   public void remove(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/* 1059 */     World world = getWorld(args, sender, 'w');
/*      */ 
/* 1062 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/* 1063 */     ProtectedRegion existing = findExistingRegion(regionManager, args.getString(0), true);
/*      */ 
/* 1067 */     if (!getPermissionModel(sender).mayDelete(existing)) {
/* 1068 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/* 1071 */     regionManager.removeRegion(existing.getId());
/* 1072 */     commitChanges(sender, regionManager);
/*      */ 
/* 1074 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region '").append(existing.getId()).append("' removed.").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"load", "reload"}, usage="[world]", desc="Reload regions from file", flags="w:")
/*      */   public void load(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/* 1090 */     World world = null;
/*      */     try {
/* 1092 */       world = getWorld(args, sender, 'w');
/*      */     }
/*      */     catch (CommandException e)
/*      */     {
/*      */     }
/*      */ 
/* 1098 */     if (!getPermissionModel(sender).mayForceLoadRegions()) {
/* 1099 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/* 1102 */     if (world != null) {
/* 1103 */       RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/* 1104 */       if (regionManager == null) {
/* 1105 */         throw new CommandException(new StringBuilder().append("No region manager exists for world '").append(world.getName()).append("'.").toString());
/*      */       }
/* 1107 */       reloadChanges(sender, regionManager);
/*      */     } else {
/* 1109 */       sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Loading all region databases... This might take a bit.").toString());
/* 1110 */       for (World w : this.plugin.getServer().getWorlds()) {
/* 1111 */         RegionManager regionManager = this.plugin.getGlobalRegionManager().get(w);
/* 1112 */         if (regionManager != null)
/*      */         {
/* 1115 */           reloadChanges(sender, regionManager, true);
/*      */         }
/*      */       }
/*      */     }
/* 1119 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region databases loaded.").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"save", "write"}, usage="[world]", desc="Re-save regions to file", flags="w:")
/*      */   public void save(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/* 1134 */     World world = null;
/*      */     try {
/* 1136 */       world = getWorld(args, sender, 'w');
/*      */     }
/*      */     catch (CommandException e)
/*      */     {
/*      */     }
/*      */ 
/* 1142 */     if (!getPermissionModel(sender).mayForceSaveRegions()) {
/* 1143 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/* 1146 */     if (world != null) {
/* 1147 */       RegionManager regionManager = this.plugin.getGlobalRegionManager().get(world);
/* 1148 */       if (regionManager == null) {
/* 1149 */         throw new CommandException(new StringBuilder().append("No region manager exists for world '").append(world.getName()).append("'.").toString());
/*      */       }
/* 1151 */       commitChanges(sender, regionManager);
/*      */     } else {
/* 1153 */       sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Saving all region databases... This might take a bit.").toString());
/* 1154 */       for (World w : this.plugin.getServer().getWorlds()) {
/* 1155 */         RegionManager regionManager = this.plugin.getGlobalRegionManager().get(w);
/* 1156 */         if (regionManager != null)
/*      */         {
/* 1159 */           commitChanges(sender, regionManager, true);
/*      */         }
/*      */       }
/*      */     }
/* 1162 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Region databases saved.").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"migratedb"}, usage="<from> <to>", desc="Migrate from one Protection Database to another.", min=2, max=2)
/*      */   public void migrateDB(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/* 1176 */     if (!getPermissionModel(sender).mayMigrateRegionStore()) {
/* 1177 */       throw new CommandPermissionsException();
/*      */     }
/*      */ 
/* 1180 */     String from = args.getString(0).toLowerCase().trim();
/* 1181 */     String to = args.getString(1).toLowerCase().trim();
/*      */ 
/* 1183 */     if (from.equals(to)) {
/* 1184 */       throw new CommandException("Will not migrate with common source and target.");
/*      */     }
/*      */ 
/* 1187 */     Map migrators = AbstractDatabaseMigrator.getMigrators();
/*      */ 
/* 1189 */     MigratorKey key = new MigratorKey(from, to);
/*      */ 
/* 1191 */     if (!migrators.containsKey(key)) {
/* 1192 */       throw new CommandException("No migrator found for that combination and direction.");
/*      */     }
/*      */ 
/* 1195 */     long lastRequest = 10000000L;
/* 1196 */     if (this.migrateDBRequestDate != null) {
/* 1197 */       lastRequest = new Date().getTime() - this.migrateDBRequestDate.getTime();
/*      */     }
/* 1199 */     if ((this.migrateDBRequest == null) || (lastRequest > 60000L)) {
/* 1200 */       this.migrateDBRequest = key;
/* 1201 */       this.migrateDBRequestDate = new Date();
/*      */ 
/* 1203 */       throw new CommandException("This command is potentially dangerous.\nPlease ensure you have made a backup of your data, and then re-enter the command exactly to procede.");
/*      */     }
/*      */ 
/* 1207 */     Class cls = (Class)migrators.get(key);
/*      */     try
/*      */     {
/* 1210 */       AbstractDatabaseMigrator migrator = (AbstractDatabaseMigrator)cls.getConstructor(new Class[] { WorldGuardPlugin.class }).newInstance(new Object[] { this.plugin });
/*      */ 
/* 1212 */       migrator.migrate();
/*      */     } catch (IllegalArgumentException ignore) {
/*      */     } catch (SecurityException ignore) {
/*      */     } catch (InstantiationException ignore) {
/*      */     } catch (IllegalAccessException ignore) {
/*      */     } catch (InvocationTargetException ignore) {
/*      */     } catch (NoSuchMethodException ignore) {
/*      */     } catch (MigrationException e) {
/* 1220 */       throw new CommandException(new StringBuilder().append("Error migrating database: ").append(e.getMessage()).toString());
/*      */     }
/*      */ 
/* 1223 */     sender.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Regions have been migrated successfully.\n").append("If you wish to use the destination format as your new backend, please update your config and reload WorldGuard.").toString());
/*      */   }
/*      */ 
/*      */   @Command(aliases={"teleport", "tp"}, usage="<id>", flags="s", desc="Teleports you to the location associated with the region.", min=1, max=1)
/*      */   public void teleport(CommandContext args, CommandSender sender)
/*      */     throws CommandException
/*      */   {
/* 1240 */     Player player = this.plugin.checkPlayer(sender);
/*      */ 
/* 1244 */     RegionManager regionManager = this.plugin.getGlobalRegionManager().get(player.getWorld());
/* 1245 */     ProtectedRegion existing = findExistingRegion(regionManager, args.getString(0), false);
/*      */ 
/* 1249 */     if (!getPermissionModel(sender).mayTeleportTo(existing))
/* 1250 */       throw new CommandPermissionsException();
/*      */     Location teleportLocation;
/* 1254 */     if (args.hasFlag('s')) {
/* 1255 */       Location teleportLocation = (Location)existing.getFlag(DefaultFlag.SPAWN_LOC);
/*      */ 
/* 1257 */       if (teleportLocation == null)
/* 1258 */         throw new CommandException("The region has no spawn point associated.");
/*      */     }
/*      */     else
/*      */     {
/* 1262 */       teleportLocation = (Location)existing.getFlag(DefaultFlag.TELE_LOC);
/*      */ 
/* 1264 */       if (teleportLocation == null) {
/* 1265 */         throw new CommandException("The region has no teleport point associated.");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1270 */     player.teleport(BukkitUtil.toLocation(teleportLocation));
/* 1271 */     sender.sendMessage(new StringBuilder().append("Teleported you to the region '").append(existing.getId()).append("'.").toString());
/*      */   }
/*      */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.RegionCommands
 * JD-Core Version:    0.6.2
 */