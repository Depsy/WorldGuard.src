/*      */ package com.sk89q.worldguard.bukkit;
/*      */ 
/*      */ import com.sk89q.worldedit.Vector;
/*      */ import com.sk89q.worldedit.blocks.BlockType;
/*      */ import com.sk89q.worldguard.LocalPlayer;
/*      */ import com.sk89q.worldguard.blacklist.Blacklist;
/*      */ import com.sk89q.worldguard.blacklist.events.BlockBreakBlacklistEvent;
/*      */ import com.sk89q.worldguard.blacklist.events.BlockInteractBlacklistEvent;
/*      */ import com.sk89q.worldguard.blacklist.events.BlockPlaceBlacklistEvent;
/*      */ import com.sk89q.worldguard.blacklist.events.ItemAcquireBlacklistEvent;
/*      */ import com.sk89q.worldguard.blacklist.events.ItemDropBlacklistEvent;
/*      */ import com.sk89q.worldguard.blacklist.events.ItemUseBlacklistEvent;
/*      */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*      */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*      */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*      */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.logging.Logger;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.GameMode;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.block.Block;
/*      */ import org.bukkit.block.BlockFace;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Item;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.event.Event.Result;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.Listener;
/*      */ import org.bukkit.event.block.Action;
/*      */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*      */ import org.bukkit.event.player.PlayerBedEnterEvent;
/*      */ import org.bukkit.event.player.PlayerBucketEmptyEvent;
/*      */ import org.bukkit.event.player.PlayerBucketFillEvent;
/*      */ import org.bukkit.event.player.PlayerCommandPreprocessEvent;
/*      */ import org.bukkit.event.player.PlayerDropItemEvent;
/*      */ import org.bukkit.event.player.PlayerFishEvent;
/*      */ import org.bukkit.event.player.PlayerGameModeChangeEvent;
/*      */ import org.bukkit.event.player.PlayerInteractEntityEvent;
/*      */ import org.bukkit.event.player.PlayerInteractEvent;
/*      */ import org.bukkit.event.player.PlayerItemHeldEvent;
/*      */ import org.bukkit.event.player.PlayerJoinEvent;
/*      */ import org.bukkit.event.player.PlayerLoginEvent;
/*      */ import org.bukkit.event.player.PlayerLoginEvent.Result;
/*      */ import org.bukkit.event.player.PlayerMoveEvent;
/*      */ import org.bukkit.event.player.PlayerPickupItemEvent;
/*      */ import org.bukkit.event.player.PlayerQuitEvent;
/*      */ import org.bukkit.event.player.PlayerRespawnEvent;
/*      */ import org.bukkit.event.player.PlayerTeleportEvent;
/*      */ import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.inventory.PlayerInventory;
/*      */ import org.bukkit.material.MaterialData;
/*      */ import org.bukkit.plugin.PluginManager;
/*      */ import org.bukkit.potion.Potion;
/*      */ import org.bukkit.potion.PotionEffect;
/*      */ import org.bukkit.potion.PotionEffectType;
/*      */ 
/*      */ public class WorldGuardPlayerListener
/*      */   implements Listener
/*      */ {
/*   88 */   private Pattern opPattern = Pattern.compile("^/op(?:\\s.*)?$", 2);
/*      */   private WorldGuardPlugin plugin;
/*      */ 
/*      */   public WorldGuardPlayerListener(WorldGuardPlugin plugin)
/*      */   {
/*   97 */     this.plugin = plugin;
/*      */   }
/*      */ 
/*      */   public void registerEvents()
/*      */   {
/*  104 */     PluginManager pm = this.plugin.getServer().getPluginManager();
/*  105 */     pm.registerEvents(this, this.plugin);
/*      */ 
/*  107 */     if (this.plugin.getGlobalStateManager().usePlayerMove)
/*  108 */       pm.registerEvents(new PlayerMoveHandler(), this.plugin);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static boolean checkMove(WorldGuardPlugin plugin, Player player, World world, org.bukkit.Location from, org.bukkit.Location to)
/*      */   {
/*  115 */     return checkMove(plugin, player, from, to);
/*      */   }
/*      */ 
/*      */   public static boolean checkMove(WorldGuardPlugin plugin, Player player, org.bukkit.Location from, org.bukkit.Location to)
/*      */   {
/*  126 */     FlagStateManager.PlayerFlagState state = plugin.getFlagStateManager().getState(player);
/*      */ 
/*  129 */     if ((state.lastWorld != null) && (!state.lastWorld.equals(to.getWorld()))) {
/*  130 */       plugin.getFlagStateManager().forget(player);
/*  131 */       state = plugin.getFlagStateManager().getState(player);
/*      */     }
/*      */ 
/*  134 */     World world = from.getWorld();
/*  135 */     World toWorld = to.getWorld();
/*      */ 
/*  137 */     LocalPlayer localPlayer = plugin.wrapPlayer(player);
/*  138 */     boolean hasBypass = plugin.getGlobalRegionManager().hasBypass(player, world);
/*  139 */     boolean hasRemoteBypass = plugin.getGlobalRegionManager().hasBypass(player, toWorld);
/*      */ 
/*  141 */     RegionManager mgr = plugin.getGlobalRegionManager().get(toWorld);
/*  142 */     Vector pt = new Vector(to.getBlockX(), to.getBlockY(), to.getBlockZ());
/*  143 */     ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/*  183 */     boolean entryAllowed = set.allows(DefaultFlag.ENTRY, localPlayer);
/*  184 */     if ((!hasRemoteBypass) && (!entryAllowed)) {
/*  185 */       String message = "You are not permitted to enter this area.";
/*      */ 
/*  187 */       player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append(message).toString());
/*  188 */       return true;
/*      */     }
/*      */ 
/*  192 */     if (state.lastExitAllowed == null) {
/*  193 */       state.lastExitAllowed = Boolean.valueOf(plugin.getGlobalRegionManager().get(world).getApplicableRegions(BukkitUtil.toVector(from)).allows(DefaultFlag.EXIT, localPlayer));
/*      */     }
/*      */ 
/*  198 */     boolean exitAllowed = set.allows(DefaultFlag.EXIT, localPlayer);
/*  199 */     if ((!hasBypass) && (exitAllowed) && (!state.lastExitAllowed.booleanValue())) {
/*  200 */       player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You are not permitted to leave this area.").toString());
/*  201 */       return true;
/*      */     }
/*      */ 
/*  207 */     String greeting = (String)set.getFlag(DefaultFlag.GREET_MESSAGE);
/*  208 */     String farewell = (String)set.getFlag(DefaultFlag.FAREWELL_MESSAGE);
/*  209 */     Boolean notifyEnter = (Boolean)set.getFlag(DefaultFlag.NOTIFY_ENTER);
/*  210 */     Boolean notifyLeave = (Boolean)set.getFlag(DefaultFlag.NOTIFY_LEAVE);
/*  211 */     GameMode gameMode = (GameMode)set.getFlag(DefaultFlag.GAME_MODE);
/*      */ 
/*  213 */     if ((state.lastFarewell != null) && ((farewell == null) || (!state.lastFarewell.equals(farewell))))
/*      */     {
/*  215 */       String replacedFarewell = plugin.replaceMacros(player, BukkitUtil.replaceColorMacros(state.lastFarewell));
/*      */ 
/*  217 */       player.sendMessage(replacedFarewell.replaceAll("\\\\n", "\n").split("\\n"));
/*      */     }
/*      */ 
/*  220 */     if ((greeting != null) && ((state.lastGreeting == null) || (!state.lastGreeting.equals(greeting))))
/*      */     {
/*  222 */       String replacedGreeting = plugin.replaceMacros(player, BukkitUtil.replaceColorMacros(greeting));
/*      */ 
/*  224 */       player.sendMessage(replacedGreeting.replaceAll("\\\\n", "\n").split("\\n"));
/*      */     }
/*      */ 
/*  227 */     if (((notifyLeave == null) || (!notifyLeave.booleanValue())) && (state.notifiedForLeave != null) && (state.notifiedForLeave.booleanValue()))
/*      */     {
/*  229 */       plugin.broadcastNotification(new StringBuilder().append(ChatColor.GRAY).append("WG: ").append(ChatColor.LIGHT_PURPLE).append(player.getName()).append(ChatColor.GOLD).append(" left NOTIFY region").toString());
/*      */     }
/*      */ 
/*  234 */     if ((notifyEnter != null) && (notifyEnter.booleanValue()) && ((state.notifiedForEnter == null) || (!state.notifiedForEnter.booleanValue())))
/*      */     {
/*  236 */       StringBuilder regionList = new StringBuilder();
/*      */ 
/*  238 */       for (ProtectedRegion region : set) {
/*  239 */         if (regionList.length() != 0) {
/*  240 */           regionList.append(", ");
/*      */         }
/*  242 */         regionList.append(region.getId());
/*      */       }
/*      */ 
/*  245 */       plugin.broadcastNotification(new StringBuilder().append(ChatColor.GRAY).append("WG: ").append(ChatColor.LIGHT_PURPLE).append(player.getName()).append(ChatColor.GOLD).append(" entered NOTIFY region: ").append(ChatColor.WHITE).append(regionList).toString());
/*      */     }
/*      */ 
/*  252 */     if ((!hasBypass) && (gameMode != null)) {
/*  253 */       if (player.getGameMode() != gameMode) {
/*  254 */         state.lastGameMode = player.getGameMode();
/*  255 */         player.setGameMode(gameMode);
/*  256 */       } else if (state.lastGameMode == null) {
/*  257 */         state.lastGameMode = player.getServer().getDefaultGameMode();
/*      */       }
/*      */     }
/*  260 */     else if (state.lastGameMode != null) {
/*  261 */       GameMode mode = state.lastGameMode;
/*  262 */       state.lastGameMode = null;
/*  263 */       player.setGameMode(mode);
/*      */     }
/*      */ 
/*  267 */     state.lastGreeting = greeting;
/*  268 */     state.lastFarewell = farewell;
/*  269 */     state.notifiedForEnter = notifyEnter;
/*  270 */     state.notifiedForLeave = notifyLeave;
/*  271 */     state.lastExitAllowed = Boolean.valueOf(exitAllowed);
/*  272 */     state.lastWorld = to.getWorld();
/*  273 */     state.lastBlockX = to.getBlockX();
/*  274 */     state.lastBlockY = to.getBlockY();
/*  275 */     state.lastBlockZ = to.getBlockZ();
/*  276 */     return false;
/*      */   }
/*      */ 
/*      */   @EventHandler
/*      */   public void onPlayerGameModeChange(PlayerGameModeChangeEvent event)
/*      */   {
/*  311 */     Player player = event.getPlayer();
/*  312 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(player.getWorld());
/*  313 */     if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().hasBypass(player, player.getWorld()))) {
/*  314 */       GameMode gameMode = (GameMode)this.plugin.getGlobalRegionManager().get(player.getWorld()).getApplicableRegions(player.getLocation()).getFlag(DefaultFlag.GAME_MODE);
/*      */ 
/*  316 */       if ((this.plugin.getFlagStateManager().getState(player).lastGameMode != null) && (gameMode != null) && (event.getNewGameMode() != gameMode))
/*      */       {
/*  318 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler
/*      */   public void onPlayerJoin(PlayerJoinEvent event) {
/*  325 */     Player player = event.getPlayer();
/*  326 */     World world = player.getWorld();
/*      */ 
/*  328 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  329 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  331 */     if (cfg.activityHaltToggle) {
/*  332 */       player.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Intensive server activity has been HALTED.").toString());
/*      */ 
/*  335 */       int removed = 0;
/*      */ 
/*  337 */       for (Entity entity : world.getEntities()) {
/*  338 */         if (BukkitUtil.isIntensiveEntity(entity)) {
/*  339 */           entity.remove();
/*  340 */           removed++;
/*      */         }
/*      */       }
/*      */ 
/*  344 */       if (removed > 10) {
/*  345 */         this.plugin.getLogger().info(new StringBuilder().append("Halt-Act: ").append(removed).append(" entities (>10) auto-removed from ").append(player.getWorld().toString()).toString());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  350 */     if (wcfg.fireSpreadDisableToggle) {
/*  351 */       player.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Fire spread is currently globally disabled for this world.").toString());
/*      */     }
/*      */ 
/*  355 */     if ((!cfg.hasCommandBookGodMode()) && (cfg.autoGodMode) && ((this.plugin.inGroup(player, "wg-invincible")) || (this.plugin.hasPermission(player, "worldguard.auto-invincible"))))
/*      */     {
/*  357 */       cfg.enableGodMode(player);
/*      */     }
/*      */ 
/*  360 */     if (this.plugin.inGroup(player, "wg-amphibious")) {
/*  361 */       cfg.enableAmphibiousMode(player);
/*      */     }
/*      */ 
/*  364 */     if (wcfg.useRegions) {
/*  365 */       FlagStateManager.PlayerFlagState state = this.plugin.getFlagStateManager().getState(player);
/*  366 */       org.bukkit.Location loc = player.getLocation();
/*  367 */       state.lastWorld = loc.getWorld();
/*  368 */       state.lastBlockX = loc.getBlockX();
/*  369 */       state.lastBlockY = loc.getBlockY();
/*  370 */       state.lastBlockZ = loc.getBlockZ();
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(ignoreCancelled=true)
/*      */   public void onPlayerChat(AsyncPlayerChatEvent event) {
/*  376 */     Player player = event.getPlayer();
/*  377 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(player.getWorld());
/*  378 */     if (wcfg.useRegions) {
/*  379 */       if (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.SEND_CHAT, player.getLocation())) {
/*  380 */         player.sendMessage(new StringBuilder().append(ChatColor.RED).append("You don't have permission to chat in this region!").toString());
/*  381 */         event.setCancelled(true);
/*  382 */         return;
/*      */       }
/*      */ 
/*  385 */       for (Iterator i = event.getRecipients().iterator(); i.hasNext(); ) {
/*  386 */         if (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.RECEIVE_CHAT, ((Player)i.next()).getLocation())) {
/*  387 */           i.remove();
/*      */         }
/*      */       }
/*  390 */       if (event.getRecipients().size() == 0)
/*  391 */         event.setCancelled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(ignoreCancelled=true)
/*      */   public void onPlayerLogin(PlayerLoginEvent event)
/*      */   {
/*  398 */     Player player = event.getPlayer();
/*  399 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*      */ 
/*  401 */     String hostKey = (String)cfg.hostKeys.get(player.getName().toLowerCase());
/*  402 */     if (hostKey != null) {
/*  403 */       String hostname = event.getHostname();
/*  404 */       int colonIndex = hostname.indexOf(58);
/*  405 */       if (colonIndex != -1) {
/*  406 */         hostname = hostname.substring(0, colonIndex);
/*      */       }
/*      */ 
/*  409 */       if (!hostname.equals(hostKey)) {
/*  410 */         event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You did not join with the valid host key!");
/*      */ 
/*  412 */         this.plugin.getLogger().warning(new StringBuilder().append("WorldGuard host key check: ").append(player.getName()).append(" joined with '").append(hostname).append("' but '").append(hostKey).append("' was expected. Kicked!").toString());
/*      */ 
/*  415 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  419 */     if (cfg.deopOnJoin)
/*  420 */       player.setOp(false);
/*      */   }
/*      */ 
/*      */   @EventHandler
/*      */   public void onPlayerQuit(PlayerQuitEvent event)
/*      */   {
/*  426 */     Player player = event.getPlayer();
/*  427 */     World world = player.getWorld();
/*      */ 
/*  429 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  430 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  436 */     if (wcfg.useRegions) {
/*  437 */       boolean hasBypass = this.plugin.getGlobalRegionManager().hasBypass(player, world);
/*  438 */       FlagStateManager.PlayerFlagState state = this.plugin.getFlagStateManager().getState(player);
/*      */ 
/*  440 */       if ((state.lastWorld != null) && (!hasBypass)) {
/*  441 */         LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*  442 */         RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/*  443 */         org.bukkit.Location loc = player.getLocation();
/*  444 */         Vector pt = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
/*  445 */         ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/*  447 */         if (state.lastExitAllowed == null) {
/*  448 */           state.lastExitAllowed = Boolean.valueOf(set.allows(DefaultFlag.EXIT, localPlayer));
/*      */         }
/*      */ 
/*  451 */         if ((!state.lastExitAllowed.booleanValue()) || (!set.allows(DefaultFlag.ENTRY, localPlayer)))
/*      */         {
/*  453 */           if (state.lastWorld.equals(world)) {
/*  454 */             org.bukkit.Location newLoc = new org.bukkit.Location(world, state.lastBlockX + 0.5D, state.lastBlockY, state.lastBlockZ + 0.5D);
/*      */ 
/*  456 */             player.teleport(newLoc);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  462 */     cfg.forgetPlayer(this.plugin.wrapPlayer(player));
/*  463 */     this.plugin.forgetPlayer(player);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
/*      */   {
/*  469 */     Player player = event.getPlayer();
/*  470 */     World world = player.getWorld();
/*  471 */     ItemStack item = player.getItemInHand();
/*      */ 
/*  473 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  474 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  476 */     if ((wcfg.getBlacklist() != null) && 
/*  477 */       (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), item.getTypeId()), false, false)))
/*      */     {
/*  481 */       event.setCancelled(true);
/*  482 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onPlayerInteract(PlayerInteractEvent event)
/*      */   {
/*  489 */     Player player = event.getPlayer();
/*  490 */     World world = player.getWorld();
/*      */ 
/*  492 */     if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
/*  493 */       handleBlockRightClick(event);
/*  494 */     else if (event.getAction() == Action.RIGHT_CLICK_AIR)
/*  495 */       handleAirRightClick(event);
/*  496 */     else if (event.getAction() == Action.LEFT_CLICK_BLOCK)
/*  497 */       handleBlockLeftClick(event);
/*  498 */     else if (event.getAction() == Action.LEFT_CLICK_AIR)
/*  499 */       handleAirLeftClick(event);
/*  500 */     else if (event.getAction() == Action.PHYSICAL) {
/*  501 */       handlePhysicalInteract(event);
/*      */     }
/*      */ 
/*  504 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  505 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  507 */     if ((wcfg.removeInfiniteStacks) && (!this.plugin.hasPermission(player, "worldguard.override.infinite-stack")))
/*      */     {
/*  509 */       int slot = player.getInventory().getHeldItemSlot();
/*  510 */       ItemStack heldItem = player.getInventory().getItem(slot);
/*  511 */       if ((heldItem != null) && (heldItem.getAmount() < 0)) {
/*  512 */         player.getInventory().setItem(slot, null);
/*  513 */         player.sendMessage(new StringBuilder().append(ChatColor.RED).append("Infinite stack removed.").toString());
/*      */       }
/*      */     }
/*      */ 
/*  517 */     if (wcfg.blockPotions.size() > 0) {
/*  518 */       ItemStack item = event.getItem();
/*  519 */       if ((item != null) && (item.getType() == Material.POTION) && (!BukkitUtil.isWaterPotion(item))) {
/*  520 */         PotionEffect blockedEffect = null;
/*      */ 
/*  522 */         Potion potion = Potion.fromDamage(BukkitUtil.getPotionEffectBits(item));
/*  523 */         for (PotionEffect effect : potion.getEffects()) {
/*  524 */           if (wcfg.blockPotions.contains(effect.getType())) {
/*  525 */             blockedEffect = effect;
/*  526 */             break;
/*      */           }
/*      */         }
/*      */ 
/*  530 */         if (blockedEffect != null)
/*  531 */           if (this.plugin.hasPermission(player, "worldguard.override.potions")) {
/*  532 */             if ((potion.isSplash()) && (wcfg.blockPotionsAlways)) {
/*  533 */               player.sendMessage(new StringBuilder().append(ChatColor.RED).append("Sorry, potions with ").append(blockedEffect.getType().getName()).append(" can't be thrown, ").append("even if you have a permission to bypass it, ").append("due to limitations (and because overly-reliable potion blocking is on).").toString());
/*      */ 
/*  537 */               event.setUseItemInHand(Event.Result.DENY);
/*      */             }
/*      */           }
/*      */           else {
/*  541 */             player.sendMessage(new StringBuilder().append(ChatColor.RED).append("Sorry, potions with ").append(blockedEffect.getType().getName()).append(" are presently disabled.").toString());
/*      */ 
/*  543 */             event.setUseItemInHand(Event.Result.DENY);
/*  544 */             return;
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void handleAirLeftClick(PlayerInteractEvent event)
/*      */   {
/*      */   }
/*      */ 
/*      */   private void handleBlockLeftClick(PlayerInteractEvent event)
/*      */   {
/*  568 */     if (event.isCancelled()) return;
/*      */ 
/*  570 */     Player player = event.getPlayer();
/*  571 */     Block block = event.getClickedBlock();
/*  572 */     int type = block.getTypeId();
/*  573 */     World world = player.getWorld();
/*      */ 
/*  575 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  576 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  578 */     if (wcfg.useRegions) {
/*  579 */       Vector pt = BukkitUtil.toVector(block);
/*  580 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/*  581 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*  582 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*      */ 
/*  599 */       if ((type == 122) && 
/*  600 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */       {
/*  602 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to move dragon eggs here!").toString());
/*  603 */         event.setUseInteractedBlock(Event.Result.DENY);
/*  604 */         event.setCancelled(true);
/*  605 */         return;
/*      */       }
/*      */ 
/*  609 */       if ((block.getRelative(event.getBlockFace()).getTypeId() == 51) && 
/*  610 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!mgr.getApplicableRegions(block.getRelative(event.getBlockFace()).getLocation()).canBuild(localPlayer)))
/*      */       {
/*  613 */         event.setUseInteractedBlock(Event.Result.DENY);
/*  614 */         event.setCancelled(true);
/*  615 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  621 */     if ((type == 46) && (player.getItemInHand().getTypeId() == 259) && 
/*  622 */       (wcfg.getBlacklist() != null) && 
/*  623 */       (!wcfg.getBlacklist().check(new BlockBreakBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(event.getClickedBlock()), event.getClickedBlock().getTypeId()), false, false)))
/*      */     {
/*  627 */       event.setUseInteractedBlock(Event.Result.DENY);
/*  628 */       event.setUseItemInHand(Event.Result.DENY);
/*  629 */       event.setCancelled(true);
/*  630 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void handleAirRightClick(PlayerInteractEvent event)
/*      */   {
/*  642 */     Player player = event.getPlayer();
/*  643 */     World world = player.getWorld();
/*  644 */     ItemStack item = player.getItemInHand();
/*      */ 
/*  646 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  647 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  649 */     if ((wcfg.getBlacklist() != null) && 
/*  650 */       (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), item.getTypeId()), false, false)))
/*      */     {
/*  654 */       event.setCancelled(true);
/*  655 */       event.setUseItemInHand(Event.Result.DENY);
/*  656 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void handleBlockRightClick(PlayerInteractEvent event)
/*      */   {
/*  667 */     if (event.isCancelled()) {
/*  668 */       return;
/*      */     }
/*      */ 
/*  671 */     Block block = event.getClickedBlock();
/*  672 */     World world = block.getWorld();
/*  673 */     int type = block.getTypeId();
/*  674 */     Player player = event.getPlayer();
/*  675 */     ItemStack item = player.getItemInHand();
/*      */ 
/*  677 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  678 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  681 */     if (((type == 54) || (type == 84) || (type == 23) || (type == 61) || (type == 62) || (type == 117) || (type == 116)) && (wcfg.removeInfiniteStacks) && (!this.plugin.hasPermission(player, "worldguard.override.infinite-stack")))
/*      */     {
/*  690 */       for (int slot = 0; slot < 40; slot++) {
/*  691 */         ItemStack heldItem = player.getInventory().getItem(slot);
/*  692 */         if ((heldItem != null) && (heldItem.getAmount() < 0)) {
/*  693 */           player.getInventory().setItem(slot, null);
/*  694 */           player.sendMessage(new StringBuilder().append(ChatColor.RED).append("Infinite stack in slot #").append(slot).append(" removed.").toString());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  699 */     if (wcfg.useRegions) {
/*  700 */       Vector pt = BukkitUtil.toVector(block);
/*  701 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/*  702 */       Block placedIn = block.getRelative(event.getBlockFace());
/*  703 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*  704 */       ApplicableRegionSet placedInSet = mgr.getApplicableRegions(placedIn.getLocation());
/*  705 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*      */ 
/*  707 */       if ((item.getTypeId() == wcfg.regionWand) && (this.plugin.hasPermission(player, "worldguard.region.wand"))) {
/*  708 */         if (set.size() > 0) {
/*  709 */           player.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Can you build? ").append(set.canBuild(localPlayer) ? "Yes" : "No").toString());
/*      */ 
/*  712 */           StringBuilder str = new StringBuilder();
/*  713 */           for (Iterator it = set.iterator(); it.hasNext(); ) {
/*  714 */             str.append(((ProtectedRegion)it.next()).getId());
/*  715 */             if (it.hasNext()) {
/*  716 */               str.append(", ");
/*      */             }
/*      */           }
/*      */ 
/*  720 */           player.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("Applicable regions: ").append(str.toString()).toString());
/*      */         } else {
/*  722 */           player.sendMessage(new StringBuilder().append(ChatColor.YELLOW).append("WorldGuard: No defined regions here!").toString());
/*      */         }
/*      */ 
/*  725 */         event.setCancelled(true);
/*  726 */         return;
/*      */       }
/*      */ 
/*  729 */       if (item.getTypeId() == 46)
/*      */       {
/*  733 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!placedInSet.allows(DefaultFlag.TNT, localPlayer)))
/*      */         {
/*  735 */           event.setUseItemInHand(Event.Result.DENY);
/*  736 */           event.setCancelled(true);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  742 */       if ((item.getTypeId() == 44) || (item.getTypeId() == 126))
/*      */       {
/*  744 */         if (!this.plugin.getGlobalRegionManager().hasBypass(localPlayer, world)) {
/*  745 */           boolean cancel = false;
/*  746 */           if ((block.getTypeId() == item.getTypeId()) && (!set.canBuild(localPlayer)))
/*      */           {
/*  749 */             cancel = true;
/*  750 */           } else if (!placedInSet.canBuild(localPlayer))
/*      */           {
/*  752 */             cancel = true;
/*      */           }
/*  754 */           if (cancel) {
/*  755 */             player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission for this area.").toString());
/*  756 */             event.setCancelled(true);
/*  757 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  762 */       if (item.getTypeId() == 355)
/*      */       {
/*  764 */         double yaw = player.getLocation().getYaw() * 4.0F / 360.0F + 0.5D;
/*  765 */         int i = (int)yaw;
/*  766 */         int i1 = (yaw < i ? i - 1 : i) & 0x3;
/*  767 */         byte b0 = 0;
/*  768 */         byte b1 = 0;
/*  769 */         if (i1 == 0) {
/*  770 */           b1 = 1;
/*      */         }
/*  772 */         if (i1 == 1) {
/*  773 */           b0 = -1;
/*      */         }
/*  775 */         if (i1 == 2) {
/*  776 */           b1 = -1;
/*      */         }
/*  778 */         if (i1 == 3) {
/*  779 */           b0 = 1;
/*      */         }
/*      */ 
/*  782 */         org.bukkit.Location headLoc = placedIn.getRelative(b0, 0, b1).getLocation();
/*  783 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(localPlayer, world)) && (!mgr.getApplicableRegions(headLoc).canBuild(localPlayer)))
/*      */         {
/*  787 */           player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission for this area.").toString());
/*  788 */           event.setCancelled(true);
/*  789 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  793 */       if ((block.getTypeId() == 36) && 
/*  794 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */       {
/*  796 */         event.setUseInteractedBlock(Event.Result.DENY);
/*      */       }
/*      */ 
/*  800 */       if (((item.getTypeId() == 324) || (item.getTypeId() == 330)) && 
/*  801 */         (!this.plugin.getGlobalRegionManager().hasBypass(localPlayer, world)) && (!placedInSet.canBuild(localPlayer)))
/*      */       {
/*  805 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission for this area.").toString());
/*  806 */         event.setCancelled(true);
/*  807 */         return;
/*      */       }
/*      */ 
/*  811 */       if (((item.getTypeId() == 385) || (item.getTypeId() == 259)) && 
/*  812 */         (!this.plugin.getGlobalRegionManager().hasBypass(localPlayer, world)) && (!this.plugin.canBuild(player, placedIn)) && (!placedInSet.allows(DefaultFlag.LIGHTER)))
/*      */       {
/*  815 */         event.setCancelled(true);
/*  816 */         event.setUseItemInHand(Event.Result.DENY);
/*  817 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to use that here.").toString());
/*  818 */         return;
/*      */       }
/*      */ 
/*  822 */       if ((item.getTypeId() == 381) && (block.getTypeId() == 120) && 
/*  823 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */       {
/*  825 */         event.setCancelled(true);
/*  826 */         event.setUseItemInHand(Event.Result.DENY);
/*  827 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to use that here.").toString());
/*  828 */         return;
/*      */       }
/*      */ 
/*  832 */       if ((item.getTypeId() == 351) && (item.getData() != null))
/*      */       {
/*  834 */         if ((item.getData().getData() == 15) && ((type == 2) || (type == 6) || (type == 59) || (type == 39) || (type == 40) || (type == 104) || (type == 105) || (type == 142) || (type == 141) || (type == 127)))
/*      */         {
/*  845 */           if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */           {
/*  847 */             event.setCancelled(true);
/*  848 */             event.setUseItemInHand(Event.Result.DENY);
/*  849 */             player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to use that here.").toString());
/*      */           }
/*      */         }
/*  852 */         else if (item.getData().getData() == 3)
/*      */         {
/*  854 */           if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */           {
/*  856 */             if ((event.getBlockFace() != BlockFace.DOWN) && (event.getBlockFace() != BlockFace.UP)) {
/*  857 */               event.setCancelled(true);
/*  858 */               event.setUseItemInHand(Event.Result.DENY);
/*  859 */               player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to plant that here.").toString());
/*  860 */               return;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  866 */       if ((type == 140) && (
/*  867 */         (item.getTypeId() == 38) || (item.getTypeId() == 37) || (item.getTypeId() == 6) || (item.getTypeId() == 40) || (item.getTypeId() == 39) || (item.getTypeId() == 81) || (item.getTypeId() == 31) || (item.getTypeId() == 32)))
/*      */       {
/*  875 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */         {
/*  877 */           event.setUseItemInHand(Event.Result.DENY);
/*  878 */           event.setCancelled(true);
/*  879 */           player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to plant that here.").toString());
/*  880 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  885 */       if ((type == 26) && 
/*  886 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.allows(DefaultFlag.SLEEP, localPlayer)))
/*      */       {
/*  888 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to use that bed.").toString());
/*  889 */         event.setUseInteractedBlock(Event.Result.DENY);
/*  890 */         event.setCancelled(true);
/*  891 */         return;
/*      */       }
/*      */ 
/*  895 */       if ((type == 54) || (type == 84) || (type == 23) || (type == 61) || (type == 62) || (type == 117) || (type == 146) || (type == 154) || (type == 158))
/*      */       {
/*  904 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)) && (!set.allows(DefaultFlag.CHEST_ACCESS, localPlayer)))
/*      */         {
/*  907 */           player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission to open that in this area.").toString());
/*  908 */           event.setUseInteractedBlock(Event.Result.DENY);
/*  909 */           event.setCancelled(true);
/*  910 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  914 */       if ((type == 122) && 
/*  915 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */       {
/*  917 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to move dragon eggs here!").toString());
/*  918 */         event.setUseInteractedBlock(Event.Result.DENY);
/*  919 */         event.setCancelled(true);
/*  920 */         return;
/*      */       }
/*      */ 
/*  924 */       if ((type == 69) || (type == 77) || (type == 143) || (type == 25) || (type == 93) || (type == 94) || (type == 64) || (type == 96) || (type == 107) || (type == 84) || (type == 23) || (type == 61) || (type == 62) || (type == 58) || (type == 117) || (type == 116) || (type == 118) || (type == 130) || (type == 138) || (type == 145) || (type == 154) || (type == 158))
/*      */       {
/*  946 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)) && (!set.allows(DefaultFlag.USE, localPlayer)))
/*      */         {
/*  949 */           player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission to use that in this area.").toString());
/*  950 */           event.setUseInteractedBlock(Event.Result.DENY);
/*  951 */           event.setCancelled(true);
/*  952 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  956 */       if ((type == 93) || (type == 94) || (type == 149) || (type == 150))
/*      */       {
/*  960 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)))
/*      */         {
/*  963 */           player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission to use that in this area.").toString());
/*  964 */           event.setUseInteractedBlock(Event.Result.DENY);
/*  965 */           event.setCancelled(true);
/*  966 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  970 */       if ((type == 92) && 
/*  971 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)) && (!set.allows(DefaultFlag.USE, localPlayer)))
/*      */       {
/*  974 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not invited to this tea party!").toString());
/*  975 */         event.setUseInteractedBlock(Event.Result.DENY);
/*  976 */         event.setCancelled(true);
/*  977 */         return;
/*      */       }
/*      */ 
/*  981 */       if ((BlockType.isRailBlock(type)) && ((item.getTypeId() == 328) || (item.getTypeId() == 343) || (item.getTypeId() == 342) || (item.getTypeId() == 407) || (item.getTypeId() == 408)))
/*      */       {
/*  987 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!placedInSet.canBuild(localPlayer)) && (!placedInSet.allows(DefaultFlag.PLACE_VEHICLE, localPlayer)))
/*      */         {
/*  990 */           player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission to place vehicles here.").toString());
/*  991 */           event.setUseItemInHand(Event.Result.DENY);
/*  992 */           event.setCancelled(true);
/*  993 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  997 */       if ((item.getTypeId() == 333) && 
/*  998 */         (!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!placedInSet.canBuild(localPlayer)) && (!placedInSet.allows(DefaultFlag.PLACE_VEHICLE, localPlayer)))
/*      */       {
/* 1001 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission to place vehicles here.").toString());
/* 1002 */         event.setUseItemInHand(Event.Result.DENY);
/* 1003 */         event.setCancelled(true);
/* 1004 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1009 */     if (wcfg.getBlacklist() != null) {
/* 1010 */       if ((player.isSneaking()) || ((type != 54) && (type != 23) && (type != 61) && (type != 62) && (type != 117) && (type != 116) && (type != 145) && (type != 130) && (type != 146) && (type != 154) && (type != 158)))
/*      */       {
/* 1022 */         if (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(block), item.getTypeId()), false, false))
/*      */         {
/* 1025 */           event.setUseItemInHand(Event.Result.DENY);
/* 1026 */           event.setCancelled(true);
/* 1027 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 1031 */       if (!wcfg.getBlacklist().check(new BlockInteractBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(block), block.getTypeId()), false, false))
/*      */       {
/* 1034 */         event.setUseInteractedBlock(Event.Result.DENY);
/* 1035 */         event.setCancelled(true);
/* 1036 */         return;
/*      */       }
/*      */ 
/* 1040 */       if (item.getTypeId() == 46) {
/* 1041 */         Block placedOn = block.getRelative(event.getBlockFace());
/* 1042 */         if (!wcfg.getBlacklist().check(new BlockPlaceBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(placedOn), item.getTypeId()), false, false))
/*      */         {
/* 1045 */           event.setUseItemInHand(Event.Result.DENY);
/* 1046 */           event.setCancelled(true);
/* 1047 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1052 */     if ((type == 54) || (type == 23) || (type == 61) || (type == 62) || (type == 116) || (type == 117) || (type == 146) || (type == 154) || (type == 158))
/*      */     {
/* 1062 */       if (wcfg.isChestProtected(block, player)) {
/* 1063 */         player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("The chest is protected.").toString());
/* 1064 */         event.setUseInteractedBlock(Event.Result.DENY);
/* 1065 */         event.setCancelled(true);
/* 1066 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void handlePhysicalInteract(PlayerInteractEvent event)
/*      */   {
/* 1126 */     if (event.isCancelled()) return;
/*      */ 
/* 1128 */     Player player = event.getPlayer();
/* 1129 */     Block block = event.getClickedBlock();
/* 1130 */     int type = block.getTypeId();
/* 1131 */     World world = player.getWorld();
/*      */ 
/* 1133 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1134 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/* 1136 */     if ((block.getTypeId() == 60) && (wcfg.disablePlayerCropTrampling)) {
/* 1137 */       event.setCancelled(true);
/* 1138 */       return;
/*      */     }
/*      */ 
/* 1141 */     if (wcfg.useRegions) {
/* 1142 */       Vector pt = BukkitUtil.toVector(block);
/* 1143 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 1144 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/* 1145 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*      */ 
/* 1147 */       if ((type == 70) || (type == 72) || (type == 132) || (type == 147) || (type == 148))
/*      */       {
/* 1150 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)) && (!set.allows(DefaultFlag.USE, localPlayer)))
/*      */         {
/* 1153 */           event.setUseInteractedBlock(Event.Result.DENY);
/* 1154 */           event.setCancelled(true);
/* 1155 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onPlayerDropItem(PlayerDropItemEvent event)
/*      */   {
/* 1230 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1231 */     WorldConfiguration wcfg = cfg.get(event.getPlayer().getWorld());
/* 1232 */     Player player = event.getPlayer();
/*      */ 
/* 1234 */     if ((wcfg.useRegions) && 
/* 1235 */       (!this.plugin.getGlobalRegionManager().hasBypass(player, player.getWorld())) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.ITEM_DROP, player.getLocation())))
/*      */     {
/* 1237 */       event.setCancelled(true);
/* 1238 */       player.sendMessage(new StringBuilder().append(ChatColor.RED).append("You don't have permission to do that in this area.").toString());
/*      */     }
/*      */ 
/* 1242 */     if (wcfg.getBlacklist() != null) {
/* 1243 */       Item ci = event.getItemDrop();
/*      */ 
/* 1245 */       if (!wcfg.getBlacklist().check(new ItemDropBlacklistEvent(this.plugin.wrapPlayer(event.getPlayer()), BukkitUtil.toVector(ci.getLocation()), ci.getItemStack().getTypeId()), false, false))
/*      */       {
/* 1248 */         event.setCancelled(true);
/* 1249 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onPlayerPickupItem(PlayerPickupItemEvent event) {
/* 1256 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1257 */     WorldConfiguration wcfg = cfg.get(event.getPlayer().getWorld());
/*      */ 
/* 1259 */     if (wcfg.getBlacklist() != null) {
/* 1260 */       Item ci = event.getItem();
/*      */ 
/* 1262 */       if (!wcfg.getBlacklist().check(new ItemAcquireBlacklistEvent(this.plugin.wrapPlayer(event.getPlayer()), BukkitUtil.toVector(ci.getLocation()), ci.getItemStack().getTypeId()), false, true))
/*      */       {
/* 1265 */         event.setCancelled(true);
/* 1266 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onPlayerBucketFill(PlayerBucketFillEvent event)
/*      */   {
/* 1274 */     Player player = event.getPlayer();
/* 1275 */     World world = player.getWorld();
/*      */ 
/* 1277 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1278 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/* 1280 */     if ((!this.plugin.getGlobalRegionManager().canBuild(player, event.getBlockClicked().getRelative(event.getBlockFace()))) && (event.getItemStack().getTypeId() != 335))
/*      */     {
/* 1283 */       player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission for this area.").toString());
/* 1284 */       event.setCancelled(true);
/* 1285 */       return;
/*      */     }
/*      */ 
/* 1288 */     if ((wcfg.getBlacklist() != null) && 
/* 1289 */       (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), event.getBucket().getId()), false, false)))
/*      */     {
/* 1292 */       event.setCancelled(true);
/* 1293 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onPlayerFish(PlayerFishEvent event)
/*      */   {
/* 1300 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(event.getPlayer().getWorld());
/*      */ 
/* 1302 */     if ((wcfg.disableExpDrops) || (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.EXP_DROPS, event.getPlayer().getLocation())))
/*      */     {
/* 1304 */       event.setExpToDrop(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
/* 1310 */     Player player = event.getPlayer();
/* 1311 */     World world = player.getWorld();
/*      */ 
/* 1313 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1314 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/* 1316 */     if (!this.plugin.getGlobalRegionManager().canBuild(player, event.getBlockClicked().getRelative(event.getBlockFace())))
/*      */     {
/* 1318 */       player.sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You don't have permission for this area.").toString());
/* 1319 */       event.setCancelled(true);
/* 1320 */       return;
/*      */     }
/*      */ 
/* 1323 */     if ((wcfg.getBlacklist() != null) && 
/* 1324 */       (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), event.getBucket().getId()), false, false)))
/*      */     {
/* 1327 */       event.setCancelled(true);
/* 1328 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGHEST)
/*      */   public void onPlayerRespawn(PlayerRespawnEvent event)
/*      */   {
/* 1335 */     Player player = event.getPlayer();
/* 1336 */     org.bukkit.Location location = player.getLocation();
/*      */ 
/* 1338 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1339 */     WorldConfiguration wcfg = cfg.get(player.getWorld());
/*      */ 
/* 1341 */     if (wcfg.useRegions) {
/* 1342 */       Vector pt = BukkitUtil.toVector(location);
/* 1343 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/* 1344 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/* 1346 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/* 1347 */       com.sk89q.worldedit.Location spawn = (com.sk89q.worldedit.Location)set.getFlag(DefaultFlag.SPAWN_LOC, localPlayer);
/*      */ 
/* 1349 */       if (spawn != null)
/* 1350 */         event.setRespawnLocation(com.sk89q.worldedit.bukkit.BukkitUtil.toLocation(spawn));
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onItemHeldChange(PlayerItemHeldEvent event)
/*      */   {
/* 1357 */     Player player = event.getPlayer();
/*      */ 
/* 1359 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1360 */     WorldConfiguration wcfg = cfg.get(player.getWorld());
/*      */ 
/* 1362 */     if ((wcfg.removeInfiniteStacks) && (!this.plugin.hasPermission(player, "worldguard.override.infinite-stack")))
/*      */     {
/* 1364 */       int newSlot = event.getNewSlot();
/* 1365 */       ItemStack heldItem = player.getInventory().getItem(newSlot);
/* 1366 */       if ((heldItem != null) && (heldItem.getAmount() < 0)) {
/* 1367 */         player.getInventory().setItem(newSlot, null);
/* 1368 */         player.sendMessage(new StringBuilder().append(ChatColor.RED).append("Infinite stack removed.").toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onPlayerBedEnter(PlayerBedEnterEvent event) {
/* 1375 */     Player player = event.getPlayer();
/* 1376 */     org.bukkit.Location location = player.getLocation();
/*      */ 
/* 1378 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1379 */     WorldConfiguration wcfg = cfg.get(player.getWorld());
/*      */ 
/* 1381 */     if (wcfg.useRegions) {
/* 1382 */       Vector pt = BukkitUtil.toVector(location);
/* 1383 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/* 1384 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/* 1386 */       if ((!this.plugin.getGlobalRegionManager().hasBypass(player, player.getWorld())) && (!set.allows(DefaultFlag.SLEEP, this.plugin.wrapPlayer(player))))
/*      */       {
/* 1388 */         event.setCancelled(true);
/* 1389 */         player.sendMessage("This bed doesn't belong to you!");
/* 1390 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
/*      */   public void onPlayerTeleport(PlayerTeleportEvent event) {
/* 1397 */     World world = event.getFrom().getWorld();
/* 1398 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1399 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/* 1401 */     if (wcfg.useRegions) {
/* 1402 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(event.getFrom().getWorld());
/* 1403 */       Vector pt = new Vector(event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ());
/* 1404 */       Vector ptFrom = new Vector(event.getFrom().getBlockX(), event.getFrom().getBlockY(), event.getFrom().getBlockZ());
/* 1405 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/* 1406 */       ApplicableRegionSet setFrom = mgr.getApplicableRegions(ptFrom);
/* 1407 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(event.getPlayer());
/*      */ 
/* 1409 */       if (cfg.usePlayerTeleports) {
/* 1410 */         boolean result = checkMove(this.plugin, event.getPlayer(), event.getFrom(), event.getTo());
/* 1411 */         if (result) {
/* 1412 */           event.setCancelled(true);
/* 1413 */           return;
/*      */         }
/*      */       }
/*      */ 
/* 1417 */       if ((event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && 
/* 1418 */         (!this.plugin.getGlobalRegionManager().hasBypass(localPlayer, world)) && (
/* 1418 */         (!set.allows(DefaultFlag.ENDERPEARL, localPlayer)) || (!setFrom.allows(DefaultFlag.ENDERPEARL, localPlayer))))
/*      */       {
/* 1421 */         event.getPlayer().sendMessage(new StringBuilder().append(ChatColor.DARK_RED).append("You're not allowed to go there.").toString());
/* 1422 */         event.setCancelled(true);
/* 1423 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
/*      */   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
/*      */   {
/* 1431 */     Player player = event.getPlayer();
/* 1432 */     LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/* 1433 */     World world = player.getWorld();
/* 1434 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1435 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/* 1437 */     if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().hasBypass(player, world))) {
/* 1438 */       Vector pt = BukkitUtil.toVector(player.getLocation());
/* 1439 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 1440 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/* 1442 */       String usedCommand = event.getMessage().toLowerCase();
/*      */ 
/* 1444 */       Set allowedCommands = (Set)set.getFlag(DefaultFlag.ALLOWED_CMDS, localPlayer);
/* 1445 */       Set blockedCommands = (Set)set.getFlag(DefaultFlag.BLOCKED_CMDS, localPlayer);
/*      */ 
/* 1460 */       String result = "";
/* 1461 */       String[] usedParts = usedCommand.split(" ");
/* 1462 */       if (blockedCommands != null)
/*      */       {
/* 1464 */         for (String blockedCommand : blockedCommands) {
/* 1465 */           String[] blockedParts = blockedCommand.split(" ");
/* 1466 */           for (int i = 0; ; i++) { if ((i >= blockedParts.length) || (i >= usedParts.length)) break label267;
/* 1467 */             if (blockedParts[i].equalsIgnoreCase(usedParts[i]))
/*      */             {
/* 1469 */               if (i + 1 == blockedParts.length)
/*      */               {
/* 1471 */                 result = blockedCommand;
/* 1472 */                 break label270;
/*      */               }
/*      */ 
/* 1475 */               if (i + 1 == usedParts.length)
/*      */               {
/*      */                 break;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1489 */       label267: label270: if (allowedCommands != null)
/*      */       {
/* 1491 */         for (String allowedCommand : allowedCommands) {
/* 1492 */           String[] allowedParts = allowedCommand.split(" ");
/* 1493 */           for (int i = 0; ; i++) { if ((i >= allowedParts.length) || (i >= usedParts.length)) break label399;
/* 1494 */             if (allowedParts[i].equalsIgnoreCase(usedParts[i]))
/*      */             {
/* 1496 */               if (i + 1 == allowedParts.length)
/*      */               {
/* 1499 */                 result = "";
/* 1500 */                 break label402;
/*      */               }
/*      */ 
/* 1503 */               if (i + 1 != usedParts.length) {
/*      */                 continue;
/*      */               }
/* 1506 */               result = usedCommand;
/* 1507 */               break;
/*      */             }
/*      */ 
/* 1515 */             result = usedCommand;
/* 1516 */             break;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1522 */       label399: label402: if (!result.isEmpty()) {
/* 1523 */         player.sendMessage(new StringBuilder().append(ChatColor.RED).append(result).append(" is not allowed in this area.").toString());
/* 1524 */         event.setCancelled(true);
/* 1525 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1529 */     if ((cfg.blockInGameOp) && 
/* 1530 */       (this.opPattern.matcher(event.getMessage()).matches())) {
/* 1531 */       player.sendMessage(new StringBuilder().append(ChatColor.RED).append("/op can only be used in console (as set by a WG setting).").toString());
/* 1532 */       event.setCancelled(true);
/* 1533 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   class PlayerMoveHandler
/*      */     implements Listener
/*      */   {
/*      */     PlayerMoveHandler()
/*      */     {
/*      */     }
/*      */ 
/*      */     @EventHandler(priority=EventPriority.HIGH)
/*      */     public void onPlayerMove(PlayerMoveEvent event)
/*      */     {
/*  282 */       Player player = event.getPlayer();
/*  283 */       World world = player.getWorld();
/*      */ 
/*  285 */       ConfigurationManager cfg = WorldGuardPlayerListener.this.plugin.getGlobalStateManager();
/*  286 */       WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  288 */       if (player.getVehicle() != null) {
/*  289 */         return;
/*      */       }
/*  291 */       if (wcfg.useRegions)
/*      */       {
/*  293 */         if ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockY() != event.getTo().getBlockY()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))
/*      */         {
/*  296 */           boolean result = WorldGuardPlayerListener.checkMove(WorldGuardPlayerListener.this.plugin, player, event.getFrom(), event.getTo());
/*  297 */           if (result) {
/*  298 */             org.bukkit.Location newLoc = event.getFrom();
/*  299 */             newLoc.setX(newLoc.getBlockX() + 0.5D);
/*  300 */             newLoc.setY(newLoc.getBlockY());
/*  301 */             newLoc.setZ(newLoc.getBlockZ() + 0.5D);
/*  302 */             event.setTo(newLoc);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardPlayerListener
 * JD-Core Version:    0.6.2
 */