/*     */ package com.sk89q.worldguard.bukkit.commands;
/*     */ 
/*     */ import com.sk89q.minecraft.util.commands.Command;
/*     */ import com.sk89q.minecraft.util.commands.CommandContext;
/*     */ import com.sk89q.minecraft.util.commands.CommandException;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*     */ import com.sk89q.worldguard.bukkit.WorldConfiguration;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*     */ import com.sk89q.worldguard.protection.databases.RegionDBUtil;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class RegionMemberCommands
/*     */ {
/*     */   private final WorldGuardPlugin plugin;
/*     */ 
/*     */   public RegionMemberCommands(WorldGuardPlugin plugin)
/*     */   {
/*  45 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   @Command(aliases={"addmember", "addmember"}, usage="<id> <members...>", flags="w:", desc="Add a member to a region", min=2)
/*     */   public void addMember(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/*  55 */     Player player = null;
/*  56 */     LocalPlayer localPlayer = null;
/*  57 */     if ((sender instanceof Player)) {
/*  58 */       player = (Player)sender;
/*  59 */       localPlayer = this.plugin.wrapPlayer(player);
/*     */     }
/*     */     World world;
/*  61 */     if (args.hasFlag('w')) {
/*  62 */       world = this.plugin.matchWorld(sender, args.getFlag('w'));
/*     */     }
/*     */     else
/*     */     {
/*     */       World world;
/*  64 */       if (player != null)
/*  65 */         world = player.getWorld();
/*     */       else
/*  67 */         throw new CommandException("No world specified. Use -w <worldname>.");
/*     */     }
/*     */     World world;
/*  71 */     String id = args.getString(0);
/*     */ 
/*  73 */     RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/*  74 */     ProtectedRegion region = mgr.getRegion(id);
/*     */ 
/*  76 */     if (region == null) {
/*  77 */       throw new CommandException("Could not find a region by that ID.");
/*     */     }
/*     */ 
/*  80 */     id = region.getId();
/*     */ 
/*  82 */     if (localPlayer != null) {
/*  83 */       if (region.isOwner(localPlayer))
/*  84 */         this.plugin.checkPermission(sender, "worldguard.region.addmember.own." + id.toLowerCase());
/*  85 */       else if (region.isMember(localPlayer))
/*  86 */         this.plugin.checkPermission(sender, "worldguard.region.addmember.member." + id.toLowerCase());
/*     */       else {
/*  88 */         this.plugin.checkPermission(sender, "worldguard.region.addmember." + id.toLowerCase());
/*     */       }
/*     */     }
/*     */ 
/*  92 */     RegionDBUtil.addToDomain(region.getMembers(), args.getParsedPaddedSlice(1, 0), 0);
/*     */ 
/*  94 */     sender.sendMessage(ChatColor.YELLOW + "Region '" + id + "' updated.");
/*     */     try
/*     */     {
/*  98 */       mgr.save();
/*     */     } catch (ProtectionDatabaseException e) {
/* 100 */       throw new CommandException("Failed to write regions: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   @Command(aliases={"addowner", "addowner"}, usage="<id> <owners...>", flags="w:", desc="Add an owner to a region", min=2)
/*     */   public void addOwner(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/* 112 */     Player player = null;
/* 113 */     LocalPlayer localPlayer = null;
/* 114 */     if ((sender instanceof Player)) {
/* 115 */       player = (Player)sender;
/* 116 */       localPlayer = this.plugin.wrapPlayer(player);
/*     */     }
/*     */     World world;
/* 118 */     if (args.hasFlag('w')) {
/* 119 */       world = this.plugin.matchWorld(sender, args.getFlag('w'));
/*     */     }
/*     */     else
/*     */     {
/*     */       World world;
/* 121 */       if (player != null)
/* 122 */         world = player.getWorld();
/*     */       else
/* 124 */         throw new CommandException("No world specified. Use -w <worldname>.");
/*     */     }
/*     */     World world;
/* 128 */     String id = args.getString(0);
/*     */ 
/* 130 */     RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 131 */     ProtectedRegion region = mgr.getRegion(id);
/*     */ 
/* 133 */     if (region == null) {
/* 134 */       throw new CommandException("Could not find a region by that ID.");
/*     */     }
/*     */ 
/* 137 */     id = region.getId();
/*     */ 
/* 139 */     Boolean flag = (Boolean)region.getFlag(DefaultFlag.BUYABLE);
/* 140 */     DefaultDomain owners = region.getOwners();
/* 141 */     if (localPlayer != null) {
/* 142 */       if ((flag != null) && (flag.booleanValue()) && (owners != null) && (owners.size() == 0)) {
/* 143 */         if (!this.plugin.hasPermission(player, "worldguard.region.unlimited")) {
/* 144 */           int maxRegionCount = this.plugin.getGlobalStateManager().get(world).getMaxRegionCount(player);
/* 145 */           if ((maxRegionCount >= 0) && (mgr.getRegionCountOfPlayer(localPlayer) >= maxRegionCount))
/*     */           {
/* 147 */             throw new CommandException("You already own the maximum allowed amount of regions.");
/*     */           }
/*     */         }
/* 150 */         this.plugin.checkPermission(sender, "worldguard.region.addowner.unclaimed." + id.toLowerCase());
/*     */       }
/* 152 */       else if (region.isOwner(localPlayer)) {
/* 153 */         this.plugin.checkPermission(sender, "worldguard.region.addowner.own." + id.toLowerCase());
/* 154 */       } else if (region.isMember(localPlayer)) {
/* 155 */         this.plugin.checkPermission(sender, "worldguard.region.addowner.member." + id.toLowerCase());
/*     */       } else {
/* 157 */         this.plugin.checkPermission(sender, "worldguard.region.addowner." + id.toLowerCase());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 162 */     RegionDBUtil.addToDomain(region.getOwners(), args.getParsedPaddedSlice(1, 0), 0);
/*     */ 
/* 164 */     sender.sendMessage(ChatColor.YELLOW + "Region '" + id + "' updated.");
/*     */     try
/*     */     {
/* 168 */       mgr.save();
/*     */     } catch (ProtectionDatabaseException e) {
/* 170 */       throw new CommandException("Failed to write regions: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   @Command(aliases={"removemember", "remmember", "removemem", "remmem"}, usage="<id> <owners...>", flags="aw:", desc="Remove an owner to a region", min=1)
/*     */   public void removeMember(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/* 182 */     Player player = null;
/* 183 */     LocalPlayer localPlayer = null;
/* 184 */     if ((sender instanceof Player)) {
/* 185 */       player = (Player)sender;
/* 186 */       localPlayer = this.plugin.wrapPlayer(player);
/*     */     }
/*     */     World world;
/* 188 */     if (args.hasFlag('w')) {
/* 189 */       world = this.plugin.matchWorld(sender, args.getFlag('w'));
/*     */     }
/*     */     else
/*     */     {
/*     */       World world;
/* 191 */       if (player != null)
/* 192 */         world = player.getWorld();
/*     */       else
/* 194 */         throw new CommandException("No world specified. Use -w <worldname>.");
/*     */     }
/*     */     World world;
/* 198 */     String id = args.getString(0);
/*     */ 
/* 200 */     RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 201 */     ProtectedRegion region = mgr.getRegion(id);
/*     */ 
/* 203 */     if (region == null) {
/* 204 */       throw new CommandException("Could not find a region by that ID.");
/*     */     }
/*     */ 
/* 207 */     id = region.getId();
/*     */ 
/* 209 */     if (localPlayer != null) {
/* 210 */       if (region.isOwner(localPlayer))
/* 211 */         this.plugin.checkPermission(sender, "worldguard.region.removemember.own." + id.toLowerCase());
/* 212 */       else if (region.isMember(localPlayer))
/* 213 */         this.plugin.checkPermission(sender, "worldguard.region.removemember.member." + id.toLowerCase());
/*     */       else {
/* 215 */         this.plugin.checkPermission(sender, "worldguard.region.removemember." + id.toLowerCase());
/*     */       }
/*     */     }
/*     */ 
/* 219 */     if (args.hasFlag('a')) {
/* 220 */       region.getMembers().removeAll();
/*     */     } else {
/* 222 */       if (args.argsLength() < 2) {
/* 223 */         throw new CommandException("List some names to remove, or use -a to remove all.");
/*     */       }
/* 225 */       RegionDBUtil.removeFromDomain(region.getMembers(), args.getParsedPaddedSlice(1, 0), 0);
/*     */     }
/*     */ 
/* 228 */     sender.sendMessage(ChatColor.YELLOW + "Region '" + id + "' updated.");
/*     */     try
/*     */     {
/* 232 */       mgr.save();
/*     */     } catch (ProtectionDatabaseException e) {
/* 234 */       throw new CommandException("Failed to write regions: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   @Command(aliases={"removeowner", "remowner"}, usage="<id> <owners...>", flags="aw:", desc="Remove an owner to a region", min=1)
/*     */   public void removeOwner(CommandContext args, CommandSender sender)
/*     */     throws CommandException
/*     */   {
/* 247 */     Player player = null;
/* 248 */     LocalPlayer localPlayer = null;
/* 249 */     if ((sender instanceof Player)) {
/* 250 */       player = (Player)sender;
/* 251 */       localPlayer = this.plugin.wrapPlayer(player);
/*     */     }
/*     */     World world;
/* 253 */     if (args.hasFlag('w')) {
/* 254 */       world = this.plugin.matchWorld(sender, args.getFlag('w'));
/*     */     }
/*     */     else
/*     */     {
/*     */       World world;
/* 256 */       if (player != null)
/* 257 */         world = player.getWorld();
/*     */       else
/* 259 */         throw new CommandException("No world specified. Use -w <worldname>.");
/*     */     }
/*     */     World world;
/* 263 */     String id = args.getString(0);
/*     */ 
/* 265 */     RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 266 */     ProtectedRegion region = mgr.getRegion(id);
/*     */ 
/* 268 */     if (region == null) {
/* 269 */       throw new CommandException("Could not find a region by that ID.");
/*     */     }
/*     */ 
/* 272 */     id = region.getId();
/*     */ 
/* 274 */     if (localPlayer != null) {
/* 275 */       if (region.isOwner(localPlayer))
/* 276 */         this.plugin.checkPermission(sender, "worldguard.region.removeowner.own." + id.toLowerCase());
/* 277 */       else if (region.isMember(localPlayer))
/* 278 */         this.plugin.checkPermission(sender, "worldguard.region.removeowner.member." + id.toLowerCase());
/*     */       else {
/* 280 */         this.plugin.checkPermission(sender, "worldguard.region.removeowner." + id.toLowerCase());
/*     */       }
/*     */     }
/*     */ 
/* 284 */     if (args.hasFlag('a')) {
/* 285 */       region.getOwners().removeAll();
/*     */     } else {
/* 287 */       if (args.argsLength() < 2) {
/* 288 */         throw new CommandException("List some names to remove, or use -a to remove all.");
/*     */       }
/* 290 */       RegionDBUtil.removeFromDomain(region.getOwners(), args.getParsedPaddedSlice(1, 0), 0);
/*     */     }
/*     */ 
/* 293 */     sender.sendMessage(ChatColor.YELLOW + "Region '" + id + "' updated.");
/*     */     try
/*     */     {
/* 297 */       mgr.save();
/*     */     } catch (ProtectionDatabaseException e) {
/* 299 */       throw new CommandException("Failed to write regions: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.RegionMemberCommands
 * JD-Core Version:    0.6.2
 */