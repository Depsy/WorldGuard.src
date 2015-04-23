/*      */ package com.sk89q.worldguard.bukkit;
/*      */ 
/*      */ import com.sk89q.worldedit.Vector;
/*      */ import com.sk89q.worldguard.LocalPlayer;
/*      */ import com.sk89q.worldguard.blacklist.Blacklist;
/*      */ import com.sk89q.worldguard.blacklist.events.ItemUseBlacklistEvent;
/*      */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*      */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*      */ import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;
/*      */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*      */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.block.Block;
/*      */ import org.bukkit.entity.Creeper;
/*      */ import org.bukkit.entity.EnderDragon;
/*      */ import org.bukkit.entity.EnderPearl;
/*      */ import org.bukkit.entity.Enderman;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.EntityType;
/*      */ import org.bukkit.entity.Fireball;
/*      */ import org.bukkit.entity.ItemFrame;
/*      */ import org.bukkit.entity.LivingEntity;
/*      */ import org.bukkit.entity.Pig;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.entity.Projectile;
/*      */ import org.bukkit.entity.TNTPrimed;
/*      */ import org.bukkit.entity.Tameable;
/*      */ import org.bukkit.entity.ThrownExpBottle;
/*      */ import org.bukkit.entity.ThrownPotion;
/*      */ import org.bukkit.entity.Wither;
/*      */ import org.bukkit.entity.WitherSkull;
/*      */ import org.bukkit.entity.Wolf;
/*      */ import org.bukkit.entity.minecart.ExplosiveMinecart;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.Listener;
/*      */ import org.bukkit.event.entity.CreatureSpawnEvent;
/*      */ import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
/*      */ import org.bukkit.event.entity.CreeperPowerEvent;
/*      */ import org.bukkit.event.entity.EntityBreakDoorEvent;
/*      */ import org.bukkit.event.entity.EntityChangeBlockEvent;
/*      */ import org.bukkit.event.entity.EntityCombustEvent;
/*      */ import org.bukkit.event.entity.EntityCreatePortalEvent;
/*      */ import org.bukkit.event.entity.EntityDamageByBlockEvent;
/*      */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*      */ import org.bukkit.event.entity.EntityDamageEvent;
/*      */ import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
/*      */ import org.bukkit.event.entity.EntityDeathEvent;
/*      */ import org.bukkit.event.entity.EntityExplodeEvent;
/*      */ import org.bukkit.event.entity.EntityInteractEvent;
/*      */ import org.bukkit.event.entity.EntityRegainHealthEvent;
/*      */ import org.bukkit.event.entity.ExpBottleEvent;
/*      */ import org.bukkit.event.entity.ExplosionPrimeEvent;
/*      */ import org.bukkit.event.entity.FoodLevelChangeEvent;
/*      */ import org.bukkit.event.entity.PigZapEvent;
/*      */ import org.bukkit.event.entity.PlayerDeathEvent;
/*      */ import org.bukkit.event.entity.PotionSplashEvent;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.inventory.PlayerInventory;
/*      */ import org.bukkit.plugin.PluginManager;
/*      */ import org.bukkit.potion.PotionEffect;
/*      */ 
/*      */ public class WorldGuardEntityListener
/*      */   implements Listener
/*      */ {
/*      */   private WorldGuardPlugin plugin;
/*      */ 
/*      */   public WorldGuardEntityListener(WorldGuardPlugin plugin)
/*      */   {
/*   99 */     this.plugin = plugin;
/*      */   }
/*      */ 
/*      */   public void registerEvents()
/*      */   {
/*  106 */     this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onEntityInteract(EntityInteractEvent event) {
/*  111 */     Entity entity = event.getEntity();
/*  112 */     Block block = event.getBlock();
/*      */ 
/*  114 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  115 */     WorldConfiguration wcfg = cfg.get(entity.getWorld());
/*      */ 
/*  117 */     if ((block.getTypeId() == 60) && 
/*  118 */       (wcfg.disableCreatureCropTrampling))
/*      */     {
/*  120 */       event.setCancelled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onExpBottle(ExpBottleEvent event)
/*      */   {
/*  127 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(event.getEntity().getWorld());
/*      */ 
/*  129 */     if ((wcfg.disableExpDrops) || (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.EXP_DROPS, event.getEntity().getLocation())))
/*      */     {
/*  131 */       event.setExperience(0);
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onEntityDeath(EntityDeathEvent event)
/*      */   {
/*  138 */     WorldConfiguration wcfg = this.plugin.getGlobalStateManager().get(event.getEntity().getWorld());
/*      */ 
/*  140 */     if ((wcfg.disableExpDrops) || (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.EXP_DROPS, event.getEntity().getLocation())))
/*      */     {
/*  142 */       event.setDroppedExp(0);
/*      */     }
/*      */ 
/*  145 */     if (((event instanceof PlayerDeathEvent)) && (wcfg.disableDeathMessages))
/*  146 */       ((PlayerDeathEvent)event).setDeathMessage("");
/*      */   }
/*      */ 
/*      */   private void onEntityDamageByBlock(EntityDamageByBlockEvent event)
/*      */   {
/*  151 */     Entity defender = event.getEntity();
/*  152 */     EntityDamageEvent.DamageCause type = event.getCause();
/*      */ 
/*  154 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  155 */     WorldConfiguration wcfg = cfg.get(defender.getWorld());
/*      */ 
/*  157 */     if (((defender instanceof Wolf)) && (((Wolf)defender).isTamed())) {
/*  158 */       if ((wcfg.antiWolfDumbness) && (type != EntityDamageEvent.DamageCause.VOID)) {
/*  159 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*  162 */     else if ((defender instanceof Player)) {
/*  163 */       Player player = (Player)defender;
/*      */ 
/*  165 */       if (isInvincible(player)) {
/*  166 */         event.setCancelled(true);
/*  167 */         return;
/*      */       }
/*      */ 
/*  170 */       if ((wcfg.disableLavaDamage) && (type == EntityDamageEvent.DamageCause.LAVA)) {
/*  171 */         event.setCancelled(true);
/*  172 */         player.setFireTicks(0);
/*  173 */         return;
/*      */       }
/*      */ 
/*  176 */       if ((wcfg.disableContactDamage) && (type == EntityDamageEvent.DamageCause.CONTACT)) {
/*  177 */         event.setCancelled(true);
/*  178 */         return;
/*      */       }
/*      */ 
/*  181 */       if ((wcfg.teleportOnVoid) && (type == EntityDamageEvent.DamageCause.VOID)) {
/*  182 */         BukkitUtil.findFreePosition(player);
/*  183 */         event.setCancelled(true);
/*  184 */         return;
/*      */       }
/*      */ 
/*  187 */       if ((wcfg.disableVoidDamage) && (type == EntityDamageEvent.DamageCause.VOID)) {
/*  188 */         event.setCancelled(true);
/*  189 */         return;
/*      */       }
/*      */ 
/*  192 */       if ((type == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) && ((wcfg.disableExplosionDamage) || (wcfg.blockOtherExplosions) || ((wcfg.explosionFlagCancellation) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.OTHER_EXPLOSION, player.getLocation())))))
/*      */       {
/*  196 */         event.setCancelled(true);
/*  197 */         return;
/*      */       }
/*      */ 
/*      */     }
/*  203 */     else if ((type == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) && ((wcfg.blockOtherExplosions) || ((wcfg.explosionFlagCancellation) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.OTHER_EXPLOSION, defender.getLocation())))))
/*      */     {
/*  207 */       event.setCancelled(true);
/*  208 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void onEntityDamageByEntity(EntityDamageByEntityEvent event)
/*      */   {
/*  215 */     if ((event.getDamager() instanceof Projectile)) {
/*  216 */       onEntityDamageByProjectile(event);
/*  217 */       return;
/*      */     }
/*      */ 
/*  220 */     Entity attacker = event.getDamager();
/*  221 */     Entity defender = event.getEntity();
/*      */ 
/*  223 */     if ((attacker instanceof Player)) {
/*  224 */       Player player = (Player)attacker;
/*      */ 
/*  226 */       ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  227 */       WorldConfiguration wcfg = cfg.get(player.getWorld());
/*      */ 
/*  229 */       ItemStack held = player.getInventory().getItemInHand();
/*      */ 
/*  231 */       if ((held != null) && 
/*  232 */         (wcfg.getBlacklist() != null) && 
/*  233 */         (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), held.getTypeId()), false, false)))
/*      */       {
/*  236 */         event.setCancelled(true);
/*  237 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  243 */     if (((defender instanceof ItemFrame)) && 
/*  244 */       (checkItemFrameProtection(attacker, (ItemFrame)defender))) {
/*  245 */       event.setCancelled(true);
/*  246 */       return;
/*      */     }
/*      */ 
/*  250 */     if ((defender instanceof Player)) {
/*  251 */       Player player = (Player)defender;
/*  252 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*      */ 
/*  254 */       ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  255 */       WorldConfiguration wcfg = cfg.get(player.getWorld());
/*      */ 
/*  257 */       if (isInvincible(player)) {
/*  258 */         if ((wcfg.regionInvinciblityRemovesMobs) && ((attacker instanceof LivingEntity)) && (!(attacker instanceof Player)) && ((!(attacker instanceof Tameable)) || (!((Tameable)attacker).isTamed())))
/*      */         {
/*  261 */           attacker.remove();
/*      */         }
/*      */ 
/*  264 */         event.setCancelled(true);
/*  265 */         return;
/*      */       }
/*      */ 
/*  268 */       if ((wcfg.disableLightningDamage) && (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)) {
/*  269 */         event.setCancelled(true);
/*  270 */         return;
/*      */       }
/*      */ 
/*  273 */       if (wcfg.disableExplosionDamage) {
/*  274 */         switch (event.getCause()) {
/*      */         case BLOCK_EXPLOSION:
/*      */         case ENTITY_EXPLOSION:
/*  277 */           event.setCancelled(true);
/*  278 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  282 */       if (attacker != null) {
/*  283 */         if (((attacker instanceof Player)) && 
/*  284 */           (wcfg.useRegions)) {
/*  285 */           Vector pt = BukkitUtil.toVector(defender.getLocation());
/*  286 */           Vector pt2 = BukkitUtil.toVector(attacker.getLocation());
/*  287 */           RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*      */ 
/*  289 */           if (!mgr.getApplicableRegions(pt2).allows(DefaultFlag.PVP, this.plugin.wrapPlayer((Player)attacker)))
/*  290 */             tryCancelPVPEvent((Player)attacker, player, event, true);
/*  291 */           else if (!mgr.getApplicableRegions(pt).allows(DefaultFlag.PVP, localPlayer)) {
/*  292 */             tryCancelPVPEvent((Player)attacker, player, event, false);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  297 */         if (((attacker instanceof TNTPrimed)) || ((attacker instanceof ExplosiveMinecart)))
/*      */         {
/*  300 */           if (wcfg.blockTNTExplosions) {
/*  301 */             event.setCancelled(true);
/*  302 */             return;
/*      */           }
/*  304 */           if ((wcfg.useRegions) && (wcfg.explosionFlagCancellation)) {
/*  305 */             Vector pt = BukkitUtil.toVector(defender.getLocation());
/*  306 */             RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*  307 */             ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*  308 */             if (!set.allows(DefaultFlag.TNT, localPlayer)) {
/*  309 */               event.setCancelled(true);
/*  310 */               return;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  315 */         if ((attacker instanceof Fireball)) {
/*  316 */           if ((attacker instanceof WitherSkull)) {
/*  317 */             if (wcfg.blockWitherSkullExplosions) {
/*  318 */               event.setCancelled(true);
/*      */             }
/*      */ 
/*      */           }
/*  322 */           else if (wcfg.blockFireballExplosions) {
/*  323 */             event.setCancelled(true);
/*  324 */             return;
/*      */           }
/*      */ 
/*  327 */           if (wcfg.useRegions) {
/*  328 */             Fireball fireball = (Fireball)attacker;
/*  329 */             Vector pt = BukkitUtil.toVector(defender.getLocation());
/*  330 */             RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*  331 */             ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*  332 */             if ((fireball.getShooter() instanceof Player)) {
/*  333 */               Vector pt2 = BukkitUtil.toVector(fireball.getShooter().getLocation());
/*  334 */               if (!mgr.getApplicableRegions(pt2).allows(DefaultFlag.PVP, this.plugin.wrapPlayer((Player)fireball.getShooter())))
/*  335 */                 tryCancelPVPEvent((Player)fireball.getShooter(), player, event, true);
/*  336 */               else if (!set.allows(DefaultFlag.PVP, localPlayer)) {
/*  337 */                 tryCancelPVPEvent((Player)fireball.getShooter(), player, event, false);
/*      */               }
/*      */             }
/*  340 */             else if ((!set.allows(DefaultFlag.GHAST_FIREBALL, localPlayer)) && (wcfg.explosionFlagCancellation)) {
/*  341 */               event.setCancelled(true);
/*  342 */               return;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  349 */         if (((attacker instanceof LivingEntity)) && (!(attacker instanceof Player))) {
/*  350 */           if (((attacker instanceof Creeper)) && (wcfg.blockCreeperExplosions)) {
/*  351 */             event.setCancelled(true);
/*  352 */             return;
/*      */           }
/*      */ 
/*  355 */           if (wcfg.disableMobDamage) {
/*  356 */             event.setCancelled(true);
/*  357 */             return;
/*      */           }
/*      */ 
/*  360 */           if (wcfg.useRegions) {
/*  361 */             Vector pt = BukkitUtil.toVector(defender.getLocation());
/*  362 */             RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*  363 */             ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/*  365 */             if ((!set.allows(DefaultFlag.MOB_DAMAGE, localPlayer)) && (!(attacker instanceof Tameable))) {
/*  366 */               event.setCancelled(true);
/*  367 */               return;
/*      */             }
/*      */ 
/*  370 */             if (((attacker instanceof Creeper)) && 
/*  371 */               (!set.allows(DefaultFlag.CREEPER_EXPLOSION, localPlayer)) && (wcfg.explosionFlagCancellation)) {
/*  372 */               event.setCancelled(true);
/*  373 */               return;
/*      */             }
/*      */ 
/*  376 */             if ((attacker instanceof Tameable)) {
/*  377 */               if ((((Tameable)attacker).getOwner() == null) && 
/*  378 */                 (!set.allows(DefaultFlag.MOB_DAMAGE, localPlayer))) {
/*  379 */                 event.setCancelled(true);
/*  380 */                 return;
/*      */               }
/*      */ 
/*  383 */               if (!(((Tameable)attacker).getOwner() instanceof Player)) {
/*  384 */                 return;
/*      */               }
/*  386 */               Player beastMaster = (Player)((Tameable)attacker).getOwner();
/*  387 */               Vector pt2 = BukkitUtil.toVector(attacker.getLocation());
/*  388 */               if (!mgr.getApplicableRegions(pt2).allows(DefaultFlag.PVP, this.plugin.wrapPlayer(beastMaster)))
/*  389 */                 tryCancelPVPEvent(beastMaster, player, event, true);
/*  390 */               else if (!set.allows(DefaultFlag.PVP, localPlayer))
/*  391 */                 tryCancelPVPEvent(beastMaster, player, event, false);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void onEntityDamageByProjectile(EntityDamageByEntityEvent event)
/*      */   {
/*  401 */     Entity defender = event.getEntity();
/*  402 */     Entity attacker = ((Projectile)event.getDamager()).getShooter();
/*      */ 
/*  404 */     if ((defender instanceof Player)) {
/*  405 */       Player player = (Player)defender;
/*  406 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*      */ 
/*  408 */       ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  409 */       WorldConfiguration wcfg = cfg.get(player.getWorld());
/*      */ 
/*  412 */       if (isInvincible(player)) {
/*  413 */         event.setCancelled(true);
/*  414 */         return;
/*      */       }
/*      */ 
/*  418 */       if ((attacker != null) && (!(attacker instanceof Player))) {
/*  419 */         if (wcfg.disableMobDamage) {
/*  420 */           event.setCancelled(true);
/*  421 */           return;
/*      */         }
/*  423 */         if (wcfg.useRegions) {
/*  424 */           Vector pt = BukkitUtil.toVector(defender.getLocation());
/*  425 */           RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*      */ 
/*  427 */           if (!mgr.getApplicableRegions(pt).allows(DefaultFlag.MOB_DAMAGE, localPlayer)) {
/*  428 */             event.setCancelled(true);
/*  429 */             return;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  436 */       if ((attacker != null) && ((attacker instanceof Player))) {
/*  437 */         if (((event.getDamager() instanceof EnderPearl)) && (attacker == player)) return;
/*  438 */         if (wcfg.useRegions) {
/*  439 */           Vector pt = BukkitUtil.toVector(defender.getLocation());
/*  440 */           Vector pt2 = BukkitUtil.toVector(attacker.getLocation());
/*  441 */           RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*      */ 
/*  443 */           if (!mgr.getApplicableRegions(pt2).allows(DefaultFlag.PVP, this.plugin.wrapPlayer((Player)attacker)))
/*  444 */             tryCancelPVPEvent((Player)attacker, player, event, true);
/*  445 */           else if (!mgr.getApplicableRegions(pt).allows(DefaultFlag.PVP, localPlayer))
/*  446 */             tryCancelPVPEvent((Player)attacker, player, event, false);
/*      */         }
/*      */       }
/*      */     }
/*  450 */     else if (((defender instanceof ItemFrame)) && 
/*  451 */       (checkItemFrameProtection(attacker, (ItemFrame)defender))) {
/*  452 */       event.setCancelled(true);
/*  453 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onEntityDamage(EntityDamageEvent event)
/*      */   {
/*  462 */     if ((event instanceof EntityDamageByEntityEvent)) {
/*  463 */       onEntityDamageByEntity((EntityDamageByEntityEvent)event);
/*  464 */       return;
/*  465 */     }if ((event instanceof EntityDamageByBlockEvent)) {
/*  466 */       onEntityDamageByBlock((EntityDamageByBlockEvent)event);
/*  467 */       return;
/*      */     }
/*      */ 
/*  470 */     Entity defender = event.getEntity();
/*  471 */     EntityDamageEvent.DamageCause type = event.getCause();
/*      */ 
/*  473 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  474 */     WorldConfiguration wcfg = cfg.get(defender.getWorld());
/*      */ 
/*  476 */     if (((defender instanceof Wolf)) && (((Wolf)defender).isTamed())) {
/*  477 */       if (wcfg.antiWolfDumbness) {
/*  478 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*  481 */     else if ((defender instanceof Player)) {
/*  482 */       Player player = (Player)defender;
/*      */ 
/*  484 */       if (isInvincible(player)) {
/*  485 */         event.setCancelled(true);
/*  486 */         player.setFireTicks(0);
/*  487 */         return;
/*      */       }
/*      */ 
/*  490 */       if (type == EntityDamageEvent.DamageCause.WITHER)
/*      */       {
/*  492 */         if (wcfg.disableMobDamage) {
/*  493 */           event.setCancelled(true);
/*  494 */           return;
/*      */         }
/*      */ 
/*  497 */         if (wcfg.useRegions) {
/*  498 */           Vector pt = BukkitUtil.toVector(defender.getLocation());
/*  499 */           RegionManager mgr = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*  500 */           ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/*  502 */           if (!set.allows(DefaultFlag.MOB_DAMAGE, this.plugin.wrapPlayer(player))) {
/*  503 */             event.setCancelled(true);
/*  504 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  509 */       if ((type == EntityDamageEvent.DamageCause.DROWNING) && (cfg.hasAmphibiousMode(player))) {
/*  510 */         player.setRemainingAir(player.getMaximumAir());
/*  511 */         event.setCancelled(true);
/*  512 */         return;
/*      */       }
/*      */ 
/*  515 */       ItemStack helmet = player.getInventory().getHelmet();
/*      */ 
/*  517 */       if ((type == EntityDamageEvent.DamageCause.DROWNING) && (wcfg.pumpkinScuba) && (helmet != null) && ((helmet.getTypeId() == 86) || (helmet.getTypeId() == 91)))
/*      */       {
/*  521 */         player.setRemainingAir(player.getMaximumAir());
/*  522 */         event.setCancelled(true);
/*  523 */         return;
/*      */       }
/*      */ 
/*  526 */       if ((wcfg.disableFallDamage) && (type == EntityDamageEvent.DamageCause.FALL)) {
/*  527 */         event.setCancelled(true);
/*  528 */         return;
/*      */       }
/*      */ 
/*  531 */       if ((wcfg.disableFireDamage) && ((type == EntityDamageEvent.DamageCause.FIRE) || (type == EntityDamageEvent.DamageCause.FIRE_TICK)))
/*      */       {
/*  533 */         event.setCancelled(true);
/*  534 */         return;
/*      */       }
/*      */ 
/*  537 */       if ((wcfg.disableDrowningDamage) && (type == EntityDamageEvent.DamageCause.DROWNING)) {
/*  538 */         player.setRemainingAir(player.getMaximumAir());
/*  539 */         event.setCancelled(true);
/*  540 */         return;
/*      */       }
/*      */ 
/*  543 */       if ((wcfg.teleportOnSuffocation) && (type == EntityDamageEvent.DamageCause.SUFFOCATION)) {
/*  544 */         BukkitUtil.findFreePosition(player);
/*  545 */         event.setCancelled(true);
/*  546 */         return;
/*      */       }
/*      */ 
/*  549 */       if ((wcfg.disableSuffocationDamage) && (type == EntityDamageEvent.DamageCause.SUFFOCATION)) {
/*  550 */         event.setCancelled(true);
/*  551 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onEntityCombust(EntityCombustEvent event) {
/*  558 */     Entity entity = event.getEntity();
/*      */ 
/*  560 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  561 */     WorldConfiguration wcfg = cfg.get(entity.getWorld());
/*      */ 
/*  563 */     if ((entity instanceof Player)) {
/*  564 */       Player player = (Player)entity;
/*      */ 
/*  566 */       if ((cfg.hasGodMode(player)) || ((wcfg.useRegions) && (RegionQueryUtil.isInvincible(this.plugin, player)))) {
/*  567 */         event.setCancelled(true);
/*  568 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onEntityExplode(EntityExplodeEvent event)
/*      */   {
/*  578 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  579 */     Location l = event.getLocation();
/*  580 */     World world = l.getWorld();
/*  581 */     WorldConfiguration wcfg = cfg.get(world);
/*  582 */     Entity ent = event.getEntity();
/*      */ 
/*  584 */     if (cfg.activityHaltToggle) {
/*  585 */       if (ent != null) {
/*  586 */         ent.remove();
/*      */       }
/*  588 */       event.setCancelled(true);
/*      */       return;
/*      */     }
/*      */     RegionManager mgr;
/*      */     RegionManager mgr;
/*  592 */     if ((ent instanceof Creeper)) {
/*  593 */       if (wcfg.blockCreeperExplosions) {
/*  594 */         event.setCancelled(true);
/*  595 */         return;
/*      */       }
/*  597 */       if (wcfg.blockCreeperBlockDamage) {
/*  598 */         event.blockList().clear();
/*  599 */         return;
/*      */       }
/*      */ 
/*  602 */       if (wcfg.useRegions) {
/*  603 */         mgr = this.plugin.getGlobalRegionManager().get(world);
/*      */ 
/*  605 */         for (Block block : event.blockList())
/*  606 */           if (!mgr.getApplicableRegions(BukkitUtil.toVector(block)).allows(DefaultFlag.CREEPER_EXPLOSION)) {
/*  607 */             event.blockList().clear();
/*  608 */             if (wcfg.explosionFlagCancellation) event.setCancelled(true);
/*  609 */             return;
/*      */           }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       RegionManager mgr;
/*  613 */       if ((ent instanceof EnderDragon)) {
/*  614 */         if (wcfg.blockEnderDragonBlockDamage) {
/*  615 */           event.blockList().clear();
/*  616 */           return;
/*      */         }
/*      */ 
/*  619 */         if (wcfg.useRegions) {
/*  620 */           mgr = this.plugin.getGlobalRegionManager().get(world);
/*      */ 
/*  622 */           for (Block block : event.blockList())
/*  623 */             if (!mgr.getApplicableRegions(BukkitUtil.toVector(block)).allows(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE)) {
/*  624 */               event.blockList().clear();
/*  625 */               if (wcfg.explosionFlagCancellation) event.setCancelled(true);
/*  626 */               return;
/*      */             }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*      */         RegionManager mgr;
/*  630 */         if (((ent instanceof TNTPrimed)) || ((ent instanceof ExplosiveMinecart))) {
/*  631 */           if (wcfg.blockTNTExplosions) {
/*  632 */             event.setCancelled(true);
/*  633 */             return;
/*      */           }
/*  635 */           if (wcfg.blockTNTBlockDamage) {
/*  636 */             event.blockList().clear();
/*  637 */             return;
/*      */           }
/*      */ 
/*  640 */           if (wcfg.useRegions) {
/*  641 */             mgr = this.plugin.getGlobalRegionManager().get(world);
/*      */ 
/*  643 */             for (Block block : event.blockList())
/*  644 */               if (!mgr.getApplicableRegions(BukkitUtil.toVector(block)).allows(DefaultFlag.TNT)) {
/*  645 */                 event.blockList().clear();
/*  646 */                 if (wcfg.explosionFlagCancellation) event.setCancelled(true);
/*  647 */                 return;
/*      */               }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*      */           RegionManager mgr;
/*  651 */           if ((ent instanceof Fireball)) {
/*  652 */             if ((ent instanceof WitherSkull)) {
/*  653 */               if (wcfg.blockWitherSkullExplosions) {
/*  654 */                 event.setCancelled(true);
/*  655 */                 return;
/*      */               }
/*  657 */               if (wcfg.blockWitherSkullBlockDamage)
/*  658 */                 event.blockList().clear();
/*      */             }
/*      */             else
/*      */             {
/*  662 */               if (wcfg.blockFireballExplosions) {
/*  663 */                 event.setCancelled(true);
/*  664 */                 return;
/*      */               }
/*  666 */               if (wcfg.blockFireballBlockDamage) {
/*  667 */                 event.blockList().clear();
/*  668 */                 return;
/*      */               }
/*      */             }
/*      */ 
/*  672 */             if (wcfg.useRegions) {
/*  673 */               mgr = this.plugin.getGlobalRegionManager().get(world);
/*      */ 
/*  675 */               for (Block block : event.blockList())
/*  676 */                 if (!mgr.getApplicableRegions(BukkitUtil.toVector(block)).allows(DefaultFlag.GHAST_FIREBALL)) {
/*  677 */                   event.blockList().clear();
/*  678 */                   if (wcfg.explosionFlagCancellation) event.setCancelled(true);
/*  679 */                   return;
/*      */                 }
/*      */             }
/*      */           }
/*  683 */           else if ((ent instanceof Wither)) {
/*  684 */             if (wcfg.blockWitherExplosions) {
/*  685 */               event.setCancelled(true);
/*  686 */               return;
/*      */             }
/*  688 */             if (wcfg.blockWitherBlockDamage) {
/*  689 */               event.blockList().clear();
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  694 */             if (wcfg.blockOtherExplosions) {
/*  695 */               event.setCancelled(true);
/*  696 */               return;
/*      */             }
/*  698 */             if (wcfg.useRegions) {
/*  699 */               mgr = this.plugin.getGlobalRegionManager().get(world);
/*  700 */               for (Block block : event.blockList())
/*  701 */                 if (!mgr.getApplicableRegions(BukkitUtil.toVector(block)).allows(DefaultFlag.OTHER_EXPLOSION)) {
/*  702 */                   event.blockList().clear();
/*  703 */                   if (wcfg.explosionFlagCancellation) event.setCancelled(true);
/*  704 */                   return;
/*      */                 }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  711 */     if (wcfg.signChestProtection)
/*  712 */       for (Block block : event.blockList())
/*  713 */         if (wcfg.isChestProtected(block)) {
/*  714 */           event.blockList().clear();
/*  715 */           return;
/*      */         }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onExplosionPrime(ExplosionPrimeEvent event)
/*      */   {
/*  727 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  728 */     WorldConfiguration wcfg = cfg.get(event.getEntity().getWorld());
/*  729 */     Entity ent = event.getEntity();
/*      */ 
/*  731 */     if (cfg.activityHaltToggle) {
/*  732 */       ent.remove();
/*  733 */       event.setCancelled(true);
/*  734 */       return;
/*      */     }
/*      */ 
/*  737 */     if (event.getEntityType() == EntityType.WITHER) {
/*  738 */       if (wcfg.blockWitherExplosions) {
/*  739 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*  742 */     else if (event.getEntityType() == EntityType.WITHER_SKULL) {
/*  743 */       if (wcfg.blockWitherSkullExplosions) {
/*  744 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*  747 */     else if (event.getEntityType() == EntityType.FIREBALL) {
/*  748 */       if (wcfg.blockFireballExplosions) {
/*  749 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*  752 */     else if (event.getEntityType() == EntityType.CREEPER) {
/*  753 */       if (wcfg.blockCreeperExplosions) {
/*  754 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*  757 */     else if ((event.getEntityType() == EntityType.PRIMED_TNT) || (event.getEntityType() == EntityType.MINECART_TNT))
/*      */     {
/*  759 */       if (wcfg.blockTNTExplosions) {
/*  760 */         event.setCancelled(true);
/*  761 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onCreatureSpawn(CreatureSpawnEvent event) {
/*  768 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*      */ 
/*  770 */     if (cfg.activityHaltToggle) {
/*  771 */       event.setCancelled(true);
/*  772 */       return;
/*      */     }
/*      */ 
/*  775 */     WorldConfiguration wcfg = cfg.get(event.getEntity().getWorld());
/*      */ 
/*  778 */     if ((!wcfg.blockPluginSpawning) && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)) {
/*  779 */       return;
/*      */     }
/*      */ 
/*  782 */     if ((wcfg.allowTamedSpawns) && ((event.getEntity() instanceof Tameable)) && (((Tameable)event.getEntity()).isTamed()))
/*      */     {
/*  785 */       return;
/*      */     }
/*      */ 
/*  788 */     EntityType entityType = event.getEntityType();
/*      */ 
/*  790 */     if (wcfg.blockCreatureSpawn.contains(entityType)) {
/*  791 */       event.setCancelled(true);
/*  792 */       return;
/*      */     }
/*      */ 
/*  795 */     Location eventLoc = event.getLocation();
/*      */ 
/*  797 */     if ((wcfg.useRegions) && (cfg.useRegionsCreatureSpawnEvent)) {
/*  798 */       Vector pt = BukkitUtil.toVector(eventLoc);
/*  799 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(eventLoc.getWorld());
/*      */ 
/*  801 */       if (mgr == null) return;
/*  802 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/*      */ 
/*  804 */       if (!set.allows(DefaultFlag.MOB_SPAWNING)) {
/*  805 */         event.setCancelled(true);
/*  806 */         return;
/*      */       }
/*      */ 
/*  809 */       Set entityTypes = (Set)set.getFlag(DefaultFlag.DENY_SPAWN);
/*  810 */       if ((entityTypes != null) && (entityTypes.contains(entityType))) {
/*  811 */         event.setCancelled(true);
/*  812 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  816 */     if ((wcfg.blockGroundSlimes) && (entityType == EntityType.SLIME) && (eventLoc.getY() >= 60.0D) && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL))
/*      */     {
/*  819 */       event.setCancelled(true);
/*  820 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onCreatePortal(EntityCreatePortalEvent event) {
/*  826 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  827 */     WorldConfiguration wcfg = cfg.get(event.getEntity().getWorld());
/*      */ 
/*  829 */     switch (event.getEntityType()) {
/*      */     case ENDER_DRAGON:
/*  831 */       if (wcfg.blockEnderDragonPortalCreation) event.setCancelled(true); break;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onPigZap(PigZapEvent event)
/*      */   {
/*  838 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  839 */     WorldConfiguration wcfg = cfg.get(event.getEntity().getWorld());
/*      */ 
/*  841 */     if (wcfg.disablePigZap)
/*  842 */       event.setCancelled(true);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onCreeperPower(CreeperPowerEvent event)
/*      */   {
/*  848 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  849 */     WorldConfiguration wcfg = cfg.get(event.getEntity().getWorld());
/*      */ 
/*  851 */     if (wcfg.disableCreeperPower)
/*  852 */       event.setCancelled(true);
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onEntityRegainHealth(EntityRegainHealthEvent event)
/*      */   {
/*  859 */     Entity ent = event.getEntity();
/*  860 */     World world = ent.getWorld();
/*      */ 
/*  862 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  863 */     WorldConfiguration wcfg = cfg.get(world);
/*      */ 
/*  865 */     if (wcfg.disableHealthRegain) {
/*  866 */       event.setCancelled(true);
/*  867 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onEntityChangeBlock(EntityChangeBlockEvent event)
/*      */   {
/*  878 */     Entity ent = event.getEntity();
/*  879 */     Block block = event.getBlock();
/*  880 */     Location location = block.getLocation();
/*      */ 
/*  882 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  883 */     WorldConfiguration wcfg = cfg.get(ent.getWorld());
/*  884 */     if ((ent instanceof Enderman)) {
/*  885 */       if (wcfg.disableEndermanGriefing) {
/*  886 */         event.setCancelled(true);
/*  887 */         return;
/*      */       }
/*      */ 
/*  890 */       if ((wcfg.useRegions) && 
/*  891 */         (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.ENDER_BUILD, location))) {
/*  892 */         event.setCancelled(true);
/*      */       }
/*      */ 
/*      */     }
/*  896 */     else if (ent.getType() == EntityType.WITHER) {
/*  897 */       if ((wcfg.blockWitherBlockDamage) || (wcfg.blockWitherExplosions)) {
/*  898 */         event.setCancelled(true);
/*      */       }
/*      */     }
/*  901 */     else if (((event instanceof EntityBreakDoorEvent)) && 
/*  902 */       (wcfg.blockZombieDoorDestruction)) {
/*  903 */       event.setCancelled(true);
/*  904 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*      */   public void onFoodLevelChange(FoodLevelChangeEvent event)
/*      */   {
/*  911 */     if ((event.getEntity() instanceof Player)) {
/*  912 */       Player player = (Player)event.getEntity();
/*  913 */       if ((event.getFoodLevel() < player.getFoodLevel()) && (isInvincible(player)))
/*  914 */         event.setCancelled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   @EventHandler(ignoreCancelled=true)
/*      */   public void onPotionSplash(PotionSplashEvent event)
/*      */   {
/*  921 */     Entity entity = event.getEntity();
/*  922 */     ThrownPotion potion = event.getPotion();
/*      */ 
/*  924 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  925 */     WorldConfiguration wcfg = cfg.get(entity.getWorld());
/*      */ 
/*  927 */     if ((wcfg.blockPotionsAlways) && (wcfg.blockPotions.size() > 0)) {
/*  928 */       boolean blocked = false;
/*      */ 
/*  930 */       for (PotionEffect effect : potion.getEffects()) {
/*  931 */         if (wcfg.blockPotions.contains(effect.getType())) {
/*  932 */           blocked = true;
/*  933 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  937 */       if (blocked) {
/*  938 */         event.setCancelled(true);
/*  939 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  943 */     GlobalRegionManager regionMan = this.plugin.getGlobalRegionManager();
/*      */ 
/*  945 */     int blockedEntities = 0;
/*  946 */     for (LivingEntity e : event.getAffectedEntities()) {
/*  947 */       if (!regionMan.allows(DefaultFlag.POTION_SPLASH, e.getLocation(), (e instanceof Player) ? this.plugin.wrapPlayer((Player)e) : null))
/*      */       {
/*  949 */         event.setIntensity(e, 0.0D);
/*  950 */         blockedEntities++;
/*      */       }
/*      */     }
/*      */ 
/*  954 */     if (blockedEntities == event.getAffectedEntities().size())
/*  955 */       event.setCancelled(true);
/*      */   }
/*      */ 
/*      */   private boolean isInvincible(Player player)
/*      */   {
/*  968 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  969 */     WorldConfiguration wcfg = cfg.get(player.getWorld());
/*      */ 
/*  971 */     boolean god = cfg.hasGodMode(player);
/*  972 */     if (wcfg.useRegions) {
/*  973 */       Boolean flag = RegionQueryUtil.isAllowedInvinciblity(this.plugin, player);
/*  974 */       boolean allowed = (flag == null) || (flag.booleanValue());
/*  975 */       boolean invincible = RegionQueryUtil.isInvincible(this.plugin, player);
/*      */ 
/*  977 */       if (allowed) {
/*  978 */         return (god) || (invincible);
/*      */       }
/*  980 */       return ((god) && (this.plugin.hasPermission(player, "worldguard.god.override-regions"))) || (invincible);
/*      */     }
/*      */ 
/*  984 */     return god;
/*      */   }
/*      */ 
/*      */   public void tryCancelPVPEvent(Player attackingPlayer, Player defendingPlayer, EntityDamageByEntityEvent event, boolean aggressorTriggered)
/*      */   {
/*  999 */     DisallowedPVPEvent disallowedPVPEvent = new DisallowedPVPEvent(attackingPlayer, defendingPlayer, event);
/* 1000 */     this.plugin.getServer().getPluginManager().callEvent(disallowedPVPEvent);
/* 1001 */     if (!disallowedPVPEvent.isCancelled()) {
/* 1002 */       if (aggressorTriggered) attackingPlayer.sendMessage(ChatColor.DARK_RED + "You are in a no-PvP area."); else
/* 1003 */         attackingPlayer.sendMessage(ChatColor.DARK_RED + "That player is in a no-PvP area.");
/* 1004 */       event.setCancelled(true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean checkItemFrameProtection(Entity attacker, ItemFrame defender)
/*      */   {
/* 1016 */     World world = attacker.getWorld();
/* 1017 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 1018 */     WorldConfiguration wcfg = cfg.get(world);
/* 1019 */     if (wcfg.useRegions)
/*      */     {
/* 1021 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 1022 */       if ((attacker instanceof Player)) {
/* 1023 */         Player player = (Player)attacker;
/* 1024 */         LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/* 1025 */         if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!mgr.getApplicableRegions(defender.getLocation()).canBuild(localPlayer)))
/*      */         {
/* 1028 */           player.sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
/* 1029 */           return true;
/*      */         }
/*      */       }
/* 1032 */       else if (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, defender.getLocation()))
/*      */       {
/* 1034 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 1038 */     if ((wcfg.blockEntityItemFrameDestroy) && (!(attacker instanceof Player))) {
/* 1039 */       return true;
/*      */     }
/* 1041 */     return false;
/*      */   }
/*      */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardEntityListener
 * JD-Core Version:    0.6.2
 */