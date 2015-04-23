/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldedit.blocks.BlockType;
/*     */ import com.sk89q.worldedit.blocks.ItemType;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.blacklist.Blacklist;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockBreakBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockPlaceBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.DestroyWithBlacklistEvent;
/*     */ import com.sk89q.worldguard.chest.ChestProtection;
/*     */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import java.util.Set;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockState;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Snowman;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.block.BlockBurnEvent;
/*     */ import org.bukkit.event.block.BlockDamageEvent;
/*     */ import org.bukkit.event.block.BlockDispenseEvent;
/*     */ import org.bukkit.event.block.BlockExpEvent;
/*     */ import org.bukkit.event.block.BlockFadeEvent;
/*     */ import org.bukkit.event.block.BlockFormEvent;
/*     */ import org.bukkit.event.block.BlockFromToEvent;
/*     */ import org.bukkit.event.block.BlockIgniteEvent;
/*     */ import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
/*     */ import org.bukkit.event.block.BlockPhysicsEvent;
/*     */ import org.bukkit.event.block.BlockPistonExtendEvent;
/*     */ import org.bukkit.event.block.BlockPistonRetractEvent;
/*     */ import org.bukkit.event.block.BlockPlaceEvent;
/*     */ import org.bukkit.event.block.BlockRedstoneEvent;
/*     */ import org.bukkit.event.block.BlockSpreadEvent;
/*     */ import org.bukkit.event.block.EntityBlockFormEvent;
/*     */ import org.bukkit.event.block.LeavesDecayEvent;
/*     */ import org.bukkit.event.block.SignChangeEvent;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.potion.Potion;
/*     */ import org.bukkit.potion.PotionEffect;
/*     */ 
/*     */ public class WorldGuardBlockListener
/*     */   implements Listener
/*     */ {
/*     */   private WorldGuardPlugin plugin;
/*     */ 
/*     */   public WorldGuardBlockListener(WorldGuardPlugin plugin)
/*     */   {
/*  83 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public void registerEvents()
/*     */   {
/*  90 */     this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
/*     */   }
/*     */ 
/*     */   protected WorldConfiguration getWorldConfig(World world)
/*     */   {
/* 100 */     return this.plugin.getGlobalStateManager().get(world);
/*     */   }
/*     */ 
/*     */   protected WorldConfiguration getWorldConfig(Player player)
/*     */   {
/* 110 */     return getWorldConfig(player.getWorld());
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockDamage(BlockDamageEvent event)
/*     */   {
/* 118 */     Player player = event.getPlayer();
/* 119 */     Block blockDamaged = event.getBlock();
/*     */ 
/* 123 */     if ((blockDamaged.getTypeId() == 92) && 
/* 124 */       (!this.plugin.getGlobalRegionManager().canBuild(player, blockDamaged))) {
/* 125 */       player.sendMessage(ChatColor.DARK_RED + "You're not invited to this tea party!");
/* 126 */       event.setCancelled(true);
/* 127 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockBreak(BlockBreakEvent event)
/*     */   {
/* 137 */     Player player = event.getPlayer();
/* 138 */     WorldConfiguration wcfg = getWorldConfig(player);
/*     */ 
/* 140 */     if (!wcfg.itemDurability) {
/* 141 */       ItemStack held = player.getItemInHand();
/* 142 */       if ((held.getTypeId() > 0) && (!ItemType.usesDamageValue(held.getTypeId())) && (!BlockType.usesData(held.getTypeId())))
/*     */       {
/* 145 */         held.setDurability((short)0);
/* 146 */         player.setItemInHand(held);
/*     */       }
/*     */     }
/*     */ 
/* 150 */     if ((!this.plugin.getGlobalRegionManager().canBuild(player, event.getBlock())) || (!this.plugin.getGlobalRegionManager().canConstruct(player, event.getBlock())))
/*     */     {
/* 152 */       player.sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
/* 153 */       event.setCancelled(true);
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     if (wcfg.getBlacklist() != null) {
/* 158 */       if (!wcfg.getBlacklist().check(new BlockBreakBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(event.getBlock()), event.getBlock().getTypeId()), false, false))
/*     */       {
/* 162 */         event.setCancelled(true);
/* 163 */         return;
/*     */       }
/*     */ 
/* 166 */       if (!wcfg.getBlacklist().check(new DestroyWithBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(event.getBlock()), player.getItemInHand().getTypeId()), false, false))
/*     */       {
/* 170 */         event.setCancelled(true);
/* 171 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 175 */     if (wcfg.isChestProtected(event.getBlock(), player)) {
/* 176 */       player.sendMessage(ChatColor.DARK_RED + "The chest is protected.");
/* 177 */       event.setCancelled(true);
/* 178 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true)
/*     */   public void onBlockFromTo(BlockFromToEvent event)
/*     */   {
/* 187 */     World world = event.getBlock().getWorld();
/* 188 */     Block blockFrom = event.getBlock();
/* 189 */     Block blockTo = event.getToBlock();
/*     */ 
/* 191 */     boolean isWater = (blockFrom.getTypeId() == 8) || (blockFrom.getTypeId() == 9);
/* 192 */     boolean isLava = (blockFrom.getTypeId() == 10) || (blockFrom.getTypeId() == 11);
/* 193 */     boolean isAir = blockFrom.getTypeId() == 0;
/*     */ 
/* 195 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 196 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 198 */     if (cfg.activityHaltToggle) {
/* 199 */       event.setCancelled(true);
/* 200 */       return;
/*     */     }
/*     */ 
/* 203 */     if ((wcfg.simulateSponge) && (isWater)) {
/* 204 */       int ox = blockTo.getX();
/* 205 */       int oy = blockTo.getY();
/* 206 */       int oz = blockTo.getZ();
/*     */ 
/* 208 */       for (int cx = -wcfg.spongeRadius; cx <= wcfg.spongeRadius; cx++) {
/* 209 */         for (int cy = -wcfg.spongeRadius; cy <= wcfg.spongeRadius; cy++) {
/* 210 */           for (int cz = -wcfg.spongeRadius; cz <= wcfg.spongeRadius; cz++) {
/* 211 */             Block sponge = world.getBlockAt(ox + cx, oy + cy, oz + cz);
/* 212 */             if ((sponge.getTypeId() == 19) && ((!wcfg.redstoneSponges) || (!sponge.isBlockIndirectlyPowered())))
/*     */             {
/* 214 */               event.setCancelled(true);
/* 215 */               return;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 235 */     if (wcfg.preventWaterDamage.size() > 0) {
/* 236 */       int targetId = blockTo.getTypeId();
/*     */ 
/* 238 */       if (((isAir) || (isWater)) && (wcfg.preventWaterDamage.contains(Integer.valueOf(targetId))))
/*     */       {
/* 240 */         event.setCancelled(true);
/* 241 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 245 */     if ((wcfg.allowedLavaSpreadOver.size() > 0) && (isLava)) {
/* 246 */       int targetId = blockTo.getRelative(0, -1, 0).getTypeId();
/*     */ 
/* 248 */       if (!wcfg.allowedLavaSpreadOver.contains(Integer.valueOf(targetId))) {
/* 249 */         event.setCancelled(true);
/* 250 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 254 */     if ((wcfg.highFreqFlags) && (isWater) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.WATER_FLOW, blockFrom.getLocation())))
/*     */     {
/* 257 */       event.setCancelled(true);
/* 258 */       return;
/*     */     }
/*     */ 
/* 261 */     if ((wcfg.highFreqFlags) && (isLava) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.LAVA_FLOW, blockFrom.getLocation())))
/*     */     {
/* 264 */       event.setCancelled(true);
/* 265 */       return;
/*     */     }
/*     */ 
/* 268 */     if ((wcfg.disableObsidianGenerators) && ((isAir) || (isLava)) && (blockTo.getTypeId() == 55))
/*     */     {
/* 270 */       blockTo.setTypeId(0);
/* 271 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockIgnite(BlockIgniteEvent event)
/*     */   {
/* 280 */     BlockIgniteEvent.IgniteCause cause = event.getCause();
/* 281 */     Block block = event.getBlock();
/* 282 */     World world = block.getWorld();
/*     */ 
/* 284 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 285 */     WorldConfiguration wcfg = cfg.get(world);
/*     */ 
/* 287 */     if (cfg.activityHaltToggle) {
/* 288 */       event.setCancelled(true);
/* 289 */       return;
/*     */     }
/* 291 */     boolean isFireSpread = cause == BlockIgniteEvent.IgniteCause.SPREAD;
/*     */ 
/* 293 */     if ((wcfg.preventLightningFire) && (cause == BlockIgniteEvent.IgniteCause.LIGHTNING)) {
/* 294 */       event.setCancelled(true);
/* 295 */       return;
/*     */     }
/*     */ 
/* 298 */     if ((wcfg.preventLavaFire) && (cause == BlockIgniteEvent.IgniteCause.LAVA)) {
/* 299 */       event.setCancelled(true);
/* 300 */       return;
/*     */     }
/*     */ 
/* 303 */     if ((wcfg.disableFireSpread) && (isFireSpread)) {
/* 304 */       event.setCancelled(true);
/* 305 */       return;
/*     */     }
/*     */ 
/* 308 */     if ((wcfg.blockLighter) && ((cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) || (cause == BlockIgniteEvent.IgniteCause.FIREBALL)) && (event.getPlayer() != null) && (!this.plugin.hasPermission(event.getPlayer(), "worldguard.override.lighter")))
/*     */     {
/* 311 */       event.setCancelled(true);
/* 312 */       return;
/*     */     }
/*     */ 
/* 315 */     if ((wcfg.fireSpreadDisableToggle) && (isFireSpread)) {
/* 316 */       event.setCancelled(true);
/* 317 */       return;
/*     */     }
/*     */ 
/* 320 */     if ((wcfg.disableFireSpreadBlocks.size() > 0) && (isFireSpread)) {
/* 321 */       int x = block.getX();
/* 322 */       int y = block.getY();
/* 323 */       int z = block.getZ();
/*     */ 
/* 325 */       if ((wcfg.disableFireSpreadBlocks.contains(Integer.valueOf(world.getBlockTypeIdAt(x, y - 1, z)))) || (wcfg.disableFireSpreadBlocks.contains(Integer.valueOf(world.getBlockTypeIdAt(x + 1, y, z)))) || (wcfg.disableFireSpreadBlocks.contains(Integer.valueOf(world.getBlockTypeIdAt(x - 1, y, z)))) || (wcfg.disableFireSpreadBlocks.contains(Integer.valueOf(world.getBlockTypeIdAt(x, y, z - 1)))) || (wcfg.disableFireSpreadBlocks.contains(Integer.valueOf(world.getBlockTypeIdAt(x, y, z + 1)))))
/*     */       {
/* 330 */         event.setCancelled(true);
/* 331 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 335 */     if (wcfg.useRegions) {
/* 336 */       Vector pt = BukkitUtil.toVector(block);
/* 337 */       Player player = event.getPlayer();
/* 338 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 339 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*     */ 
/* 341 */       if ((player != null) && (!this.plugin.getGlobalRegionManager().hasBypass(player, world))) {
/* 342 */         LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*     */ 
/* 346 */         if (((cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) || (cause == BlockIgniteEvent.IgniteCause.FIREBALL)) && 
/* 347 */           (!set.allows(DefaultFlag.LIGHTER)) && (!set.canBuild(localPlayer)) && (!this.plugin.hasPermission(player, "worldguard.override.lighter")))
/*     */         {
/* 350 */           event.setCancelled(true);
/* 351 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 356 */       if ((wcfg.highFreqFlags) && (isFireSpread) && (!set.allows(DefaultFlag.FIRE_SPREAD)))
/*     */       {
/* 358 */         event.setCancelled(true);
/* 359 */         return;
/*     */       }
/*     */ 
/* 362 */       if ((wcfg.highFreqFlags) && (cause == BlockIgniteEvent.IgniteCause.LAVA) && (!set.allows(DefaultFlag.LAVA_FIRE)))
/*     */       {
/* 364 */         event.setCancelled(true);
/* 365 */         return;
/*     */       }
/*     */ 
/* 368 */       if ((cause == BlockIgniteEvent.IgniteCause.FIREBALL) && (event.getPlayer() == null))
/*     */       {
/* 370 */         if (!set.allows(DefaultFlag.GHAST_FIREBALL)) {
/* 371 */           event.setCancelled(true);
/* 372 */           return;
/*     */         }
/*     */       }
/*     */ 
/* 376 */       if ((cause == BlockIgniteEvent.IgniteCause.LIGHTNING) && (!set.allows(DefaultFlag.LIGHTNING))) {
/* 377 */         event.setCancelled(true);
/* 378 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockBurn(BlockBurnEvent event)
/*     */   {
/* 388 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 389 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 391 */     if (cfg.activityHaltToggle) {
/* 392 */       event.setCancelled(true);
/* 393 */       return;
/*     */     }
/*     */ 
/* 396 */     if (wcfg.disableFireSpread) {
/* 397 */       event.setCancelled(true);
/* 398 */       return;
/*     */     }
/*     */ 
/* 401 */     if (wcfg.fireSpreadDisableToggle) {
/* 402 */       Block block = event.getBlock();
/* 403 */       event.setCancelled(true);
/* 404 */       checkAndDestroyAround(block.getWorld(), block.getX(), block.getY(), block.getZ(), 51);
/* 405 */       return;
/*     */     }
/*     */ 
/* 408 */     if (wcfg.disableFireSpreadBlocks.size() > 0) {
/* 409 */       Block block = event.getBlock();
/*     */ 
/* 411 */       if (wcfg.disableFireSpreadBlocks.contains(Integer.valueOf(block.getTypeId()))) {
/* 412 */         event.setCancelled(true);
/* 413 */         checkAndDestroyAround(block.getWorld(), block.getX(), block.getY(), block.getZ(), 51);
/* 414 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 418 */     if (wcfg.isChestProtected(event.getBlock())) {
/* 419 */       event.setCancelled(true);
/* 420 */       return;
/*     */     }
/*     */ 
/* 423 */     if (wcfg.useRegions) {
/* 424 */       Block block = event.getBlock();
/* 425 */       int x = block.getX();
/* 426 */       int y = block.getY();
/* 427 */       int z = block.getZ();
/* 428 */       Vector pt = BukkitUtil.toVector(block);
/* 429 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(block.getWorld());
/* 430 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*     */ 
/* 432 */       if (!set.allows(DefaultFlag.FIRE_SPREAD)) {
/* 433 */         checkAndDestroyAround(block.getWorld(), x, y, z, 51);
/* 434 */         event.setCancelled(true);
/* 435 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkAndDestroyAround(World world, int x, int y, int z, int required)
/*     */   {
/* 442 */     checkAndDestroy(world, x, y, z + 1, required);
/* 443 */     checkAndDestroy(world, x, y, z - 1, required);
/* 444 */     checkAndDestroy(world, x, y + 1, z, required);
/* 445 */     checkAndDestroy(world, x, y - 1, z, required);
/* 446 */     checkAndDestroy(world, x + 1, y, z, required);
/* 447 */     checkAndDestroy(world, x - 1, y, z, required);
/*     */   }
/*     */ 
/*     */   private void checkAndDestroy(World world, int x, int y, int z, int required) {
/* 451 */     if (world.getBlockTypeIdAt(x, y, z) == required)
/* 452 */       world.getBlockAt(x, y, z).setTypeId(0);
/*     */   }
/*     */ 
/*     */   @EventHandler(ignoreCancelled=true)
/*     */   public void onBlockPhysics(BlockPhysicsEvent event)
/*     */   {
/* 461 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 462 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 464 */     if (cfg.activityHaltToggle) {
/* 465 */       event.setCancelled(true);
/* 466 */       return;
/*     */     }
/*     */ 
/* 469 */     int id = event.getChangedTypeId();
/*     */ 
/* 471 */     if ((id == 13) && (wcfg.noPhysicsGravel)) {
/* 472 */       event.setCancelled(true);
/* 473 */       return;
/*     */     }
/*     */ 
/* 476 */     if ((id == 12) && (wcfg.noPhysicsSand)) {
/* 477 */       event.setCancelled(true);
/* 478 */       return;
/*     */     }
/*     */ 
/* 481 */     if ((id == 90) && (wcfg.allowPortalAnywhere)) {
/* 482 */       event.setCancelled(true);
/* 483 */       return;
/*     */     }
/*     */ 
/* 486 */     if ((wcfg.ropeLadders) && (event.getBlock().getType() == Material.LADDER) && 
/* 487 */       (event.getBlock().getRelative(0, 1, 0).getType() == Material.LADDER)) {
/* 488 */       event.setCancelled(true);
/* 489 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockPlace(BlockPlaceEvent event)
/*     */   {
/* 499 */     Block blockPlaced = event.getBlock();
/* 500 */     Player player = event.getPlayer();
/* 501 */     World world = blockPlaced.getWorld();
/*     */ 
/* 503 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 504 */     WorldConfiguration wcfg = cfg.get(world);
/*     */ 
/* 506 */     if (wcfg.useRegions) {
/* 507 */       Location location = blockPlaced.getLocation();
/* 508 */       if ((!this.plugin.getGlobalRegionManager().canBuild(player, location)) || (!this.plugin.getGlobalRegionManager().canConstruct(player, location)))
/*     */       {
/* 510 */         player.sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
/* 511 */         event.setCancelled(true);
/* 512 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 516 */     if ((wcfg.getBlacklist() != null) && 
/* 517 */       (!wcfg.getBlacklist().check(new BlockPlaceBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(blockPlaced), blockPlaced.getTypeId()), false, false)))
/*     */     {
/* 520 */       event.setCancelled(true);
/* 521 */       return;
/*     */     }
/*     */ 
/* 525 */     if ((wcfg.signChestProtection) && (wcfg.getChestProtection().isChest(blockPlaced.getTypeId())) && 
/* 526 */       (wcfg.isAdjacentChestProtected(event.getBlock(), player))) {
/* 527 */       player.sendMessage(ChatColor.DARK_RED + "This spot is for a chest that you don't have permission for.");
/* 528 */       event.setCancelled(true);
/* 529 */       return;
/*     */     }
/*     */ 
/* 533 */     if ((wcfg.simulateSponge) && (blockPlaced.getTypeId() == 19)) {
/* 534 */       if ((wcfg.redstoneSponges) && (blockPlaced.isBlockIndirectlyPowered())) {
/* 535 */         return;
/*     */       }
/*     */ 
/* 538 */       int ox = blockPlaced.getX();
/* 539 */       int oy = blockPlaced.getY();
/* 540 */       int oz = blockPlaced.getZ();
/*     */ 
/* 542 */       SpongeUtil.clearSpongeWater(this.plugin, world, ox, oy, oz);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH)
/*     */   public void onBlockRedstoneChange(BlockRedstoneEvent event)
/*     */   {
/* 551 */     Block blockTo = event.getBlock();
/* 552 */     World world = blockTo.getWorld();
/*     */ 
/* 554 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 555 */     WorldConfiguration wcfg = cfg.get(world);
/*     */ 
/* 557 */     if ((wcfg.simulateSponge) && (wcfg.redstoneSponges)) {
/* 558 */       int ox = blockTo.getX();
/* 559 */       int oy = blockTo.getY();
/* 560 */       int oz = blockTo.getZ();
/*     */ 
/* 562 */       for (int cx = -1; cx <= 1; cx++) {
/* 563 */         for (int cy = -1; cy <= 1; cy++) {
/* 564 */           for (int cz = -1; cz <= 1; cz++) {
/* 565 */             Block sponge = world.getBlockAt(ox + cx, oy + cy, oz + cz);
/* 566 */             if ((sponge.getTypeId() == 19) && (sponge.isBlockIndirectlyPowered()))
/*     */             {
/* 568 */               SpongeUtil.clearSpongeWater(this.plugin, world, ox + cx, oy + cy, oz + cz);
/* 569 */             } else if ((sponge.getTypeId() == 19) && (!sponge.isBlockIndirectlyPowered()))
/*     */             {
/* 571 */               SpongeUtil.addSpongeWater(this.plugin, world, ox + cx, oy + cy, oz + cz);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 577 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onSignChange(SignChangeEvent event)
/*     */   {
/* 586 */     Player player = event.getPlayer();
/* 587 */     WorldConfiguration wcfg = getWorldConfig(player);
/*     */ 
/* 589 */     if (wcfg.signChestProtection) {
/* 590 */       if (event.getLine(0).equalsIgnoreCase("[Lock]")) {
/* 591 */         if (wcfg.isChestProtectedPlacement(event.getBlock(), player)) {
/* 592 */           player.sendMessage(ChatColor.DARK_RED + "You do not own the adjacent chest.");
/* 593 */           event.getBlock().breakNaturally();
/* 594 */           event.setCancelled(true);
/* 595 */           return;
/*     */         }
/*     */ 
/* 598 */         if (event.getBlock().getTypeId() != 63) {
/* 599 */           player.sendMessage(ChatColor.RED + "The [Lock] sign must be a sign post, not a wall sign.");
/*     */ 
/* 602 */           event.getBlock().breakNaturally();
/* 603 */           event.setCancelled(true);
/* 604 */           return;
/*     */         }
/*     */ 
/* 607 */         if (!event.getLine(1).equalsIgnoreCase(player.getName())) {
/* 608 */           player.sendMessage(ChatColor.RED + "The first owner line must be your name.");
/*     */ 
/* 611 */           event.getBlock().breakNaturally();
/* 612 */           event.setCancelled(true);
/* 613 */           return;
/*     */         }
/*     */ 
/* 616 */         int below = event.getBlock().getRelative(0, -1, 0).getTypeId();
/*     */ 
/* 618 */         if ((below == 46) || (below == 12) || (below == 13) || (below == 63))
/*     */         {
/* 620 */           player.sendMessage(ChatColor.RED + "That is not a safe block that you're putting this sign on.");
/*     */ 
/* 623 */           event.getBlock().breakNaturally();
/* 624 */           event.setCancelled(true);
/* 625 */           return;
/*     */         }
/*     */ 
/* 628 */         event.setLine(0, "[Lock]");
/* 629 */         player.sendMessage(ChatColor.YELLOW + "A chest or double chest above is now protected.");
/*     */       }
/*     */     }
/* 632 */     else if ((!wcfg.disableSignChestProtectionCheck) && 
/* 633 */       (event.getLine(0).equalsIgnoreCase("[Lock]"))) {
/* 634 */       player.sendMessage(ChatColor.RED + "WorldGuard's sign chest protection is disabled.");
/*     */ 
/* 637 */       event.getBlock().breakNaturally();
/* 638 */       event.setCancelled(true);
/* 639 */       return;
/*     */     }
/*     */ 
/* 643 */     if (!this.plugin.getGlobalRegionManager().canBuild(player, event.getBlock())) {
/* 644 */       player.sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
/* 645 */       event.setCancelled(true);
/* 646 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onLeavesDecay(LeavesDecayEvent event) {
/* 652 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 653 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 655 */     if (cfg.activityHaltToggle) {
/* 656 */       event.setCancelled(true);
/* 657 */       return;
/*     */     }
/*     */ 
/* 660 */     if (wcfg.disableLeafDecay) {
/* 661 */       event.setCancelled(true);
/* 662 */       return;
/*     */     }
/*     */ 
/* 665 */     if ((wcfg.useRegions) && 
/* 666 */       (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.LEAF_DECAY, event.getBlock().getLocation())))
/*     */     {
/* 668 */       event.setCancelled(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockForm(BlockFormEvent event)
/*     */   {
/* 678 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 679 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 681 */     if (cfg.activityHaltToggle) {
/* 682 */       event.setCancelled(true);
/* 683 */       return;
/*     */     }
/*     */ 
/* 686 */     int type = event.getNewState().getTypeId();
/*     */ 
/* 688 */     if (type == 79) {
/* 689 */       if (wcfg.disableIceFormation) {
/* 690 */         event.setCancelled(true);
/* 691 */         return;
/*     */       }
/* 693 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.ICE_FORM, event.getBlock().getLocation())))
/*     */       {
/* 695 */         event.setCancelled(true);
/* 696 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 700 */     if (type == 78) {
/* 701 */       if (wcfg.disableSnowFormation) {
/* 702 */         event.setCancelled(true);
/* 703 */         return;
/*     */       }
/* 705 */       if (wcfg.allowedSnowFallOver.size() > 0) {
/* 706 */         int targetId = event.getBlock().getRelative(0, -1, 0).getTypeId();
/*     */ 
/* 708 */         if (!wcfg.allowedSnowFallOver.contains(Integer.valueOf(targetId))) {
/* 709 */           event.setCancelled(true);
/* 710 */           return;
/*     */         }
/*     */       }
/* 713 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.SNOW_FALL, event.getBlock().getLocation())))
/*     */       {
/* 715 */         event.setCancelled(true);
/* 716 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onEntityBlockForm(EntityBlockFormEvent event)
/*     */   {
/* 726 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 727 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 729 */     if (cfg.activityHaltToggle) {
/* 730 */       event.setCancelled(true);
/* 731 */       return;
/*     */     }
/*     */ 
/* 734 */     if (((event.getEntity() instanceof Snowman)) && 
/* 735 */       (wcfg.disableSnowmanTrails)) {
/* 736 */       event.setCancelled(true);
/* 737 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockSpread(BlockSpreadEvent event)
/*     */   {
/* 747 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 748 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 750 */     if (cfg.activityHaltToggle) {
/* 751 */       event.setCancelled(true);
/* 752 */       return;
/*     */     }
/*     */ 
/* 755 */     int fromType = event.getSource().getTypeId();
/*     */ 
/* 757 */     if ((fromType == 40) || (fromType == 39)) {
/* 758 */       if (wcfg.disableMushroomSpread) {
/* 759 */         event.setCancelled(true);
/* 760 */         return;
/*     */       }
/* 762 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.MUSHROOMS, event.getBlock().getLocation())))
/*     */       {
/* 764 */         event.setCancelled(true);
/* 765 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 769 */     if (fromType == 2) {
/* 770 */       if (wcfg.disableGrassGrowth) {
/* 771 */         event.setCancelled(true);
/* 772 */         return;
/*     */       }
/* 774 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.GRASS_SPREAD, event.getBlock().getLocation())))
/*     */       {
/* 776 */         event.setCancelled(true);
/* 777 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 781 */     if (fromType == 110) {
/* 782 */       if (wcfg.disableMyceliumSpread) {
/* 783 */         event.setCancelled(true);
/* 784 */         return;
/*     */       }
/*     */ 
/* 787 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.MYCELIUM_SPREAD, event.getBlock().getLocation())))
/*     */       {
/* 790 */         event.setCancelled(true);
/* 791 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 795 */     if (fromType == 106) {
/* 796 */       if (wcfg.disableVineGrowth) {
/* 797 */         event.setCancelled(true);
/* 798 */         return;
/*     */       }
/*     */ 
/* 801 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.VINE_GROWTH, event.getBlock().getLocation())))
/*     */       {
/* 804 */         event.setCancelled(true);
/* 805 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockFade(BlockFadeEvent event)
/*     */   {
/* 816 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 817 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 819 */     switch (event.getBlock().getTypeId()) {
/*     */     case 79:
/* 821 */       if (wcfg.disableIceMelting) {
/* 822 */         event.setCancelled(true);
/* 823 */         return;
/*     */       }
/*     */ 
/* 826 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.ICE_MELT, event.getBlock().getLocation())))
/*     */       {
/* 828 */         event.setCancelled(true);
/*     */         return;
/*     */       }
/*     */       break;
/*     */     case 78:
/* 834 */       if (wcfg.disableSnowMelting) {
/* 835 */         event.setCancelled(true);
/* 836 */         return;
/*     */       }
/*     */ 
/* 839 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.SNOW_MELT, event.getBlock().getLocation())))
/*     */       {
/* 841 */         event.setCancelled(true);
/*     */         return;
/*     */       }
/*     */       break;
/*     */     case 60:
/* 847 */       if (wcfg.disableSoilDehydration) {
/* 848 */         event.setCancelled(true);
/* 849 */         return;
/*     */       }
/* 851 */       if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.SOIL_DRY, event.getBlock().getLocation())))
/*     */       {
/* 853 */         event.setCancelled(true);
/*     */         return;
/*     */       }
/*     */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockPistonExtend(BlockPistonExtendEvent event)
/*     */   {
/* 866 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 867 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 869 */     if (wcfg.useRegions) {
/* 870 */       if (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.PISTONS, event.getBlock().getLocation())) {
/* 871 */         event.setCancelled(true);
/* 872 */         return;
/*     */       }
/* 874 */       for (Block block : event.getBlocks())
/* 875 */         if (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.PISTONS, block.getLocation())) {
/* 876 */           event.setCancelled(true);
/* 877 */           return;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockPistonRetract(BlockPistonRetractEvent event)
/*     */   {
/* 888 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 889 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */ 
/* 891 */     if ((wcfg.useRegions) && (event.isSticky()) && (
/* 892 */       (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.PISTONS, event.getRetractLocation())) || (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.PISTONS, event.getBlock().getLocation()))))
/*     */     {
/* 894 */       event.setCancelled(true);
/* 895 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onBlockDispense(BlockDispenseEvent event)
/*     */   {
/* 905 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 906 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/*     */     Potion potion;
/* 908 */     if (wcfg.blockPotions.size() > 0) {
/* 909 */       ItemStack item = event.getItem();
/* 910 */       if ((item.getType() == Material.POTION) && (!BukkitUtil.isWaterPotion(item))) {
/* 911 */         potion = Potion.fromDamage(BukkitUtil.getPotionEffectBits(item));
/* 912 */         for (PotionEffect effect : potion.getEffects())
/* 913 */           if ((potion.isSplash()) && (wcfg.blockPotions.contains(effect.getType()))) {
/* 914 */             event.setCancelled(true);
/* 915 */             return;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH)
/*     */   public void onBlockExp(BlockExpEvent event)
/*     */   {
/* 927 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 928 */     WorldConfiguration wcfg = cfg.get(event.getBlock().getWorld());
/* 929 */     if ((wcfg.disableExpDrops) || (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.EXP_DROPS, event.getBlock().getLocation())))
/*     */     {
/* 931 */       event.setExpToDrop(0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardBlockListener
 * JD-Core Version:    0.6.2
 */