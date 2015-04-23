/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldguard.blacklist.Blacklist;
/*     */ import com.sk89q.worldguard.blacklist.events.BlockBreakBlacklistEvent;
/*     */ import com.sk89q.worldguard.blacklist.events.ItemUseBlacklistEvent;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Creeper;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Hanging;
/*     */ import org.bukkit.entity.ItemFrame;
/*     */ import org.bukkit.entity.Painting;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Projectile;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.hanging.HangingBreakByEntityEvent;
/*     */ import org.bukkit.event.hanging.HangingBreakEvent;
/*     */ import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
/*     */ import org.bukkit.event.hanging.HangingPlaceEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEntityEvent;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class WorldGuardHangingListener
/*     */   implements Listener
/*     */ {
/*     */   private WorldGuardPlugin plugin;
/*     */ 
/*     */   public WorldGuardHangingListener(WorldGuardPlugin plugin)
/*     */   {
/*  63 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   public void registerEvents()
/*     */   {
/*  70 */     this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onHangingingBreak(HangingBreakEvent event) {
/*  75 */     Hanging hanging = event.getEntity();
/*  76 */     World world = hanging.getWorld();
/*  77 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/*  78 */     WorldConfiguration wcfg = cfg.get(world);
/*     */ 
/*  80 */     if ((event instanceof HangingBreakByEntityEvent)) {
/*  81 */       HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent)event;
/*  82 */       Entity removerEntity = entityEvent.getRemover();
/*  83 */       if ((removerEntity instanceof Projectile)) {
/*  84 */         Projectile projectile = (Projectile)removerEntity;
/*  85 */         removerEntity = projectile.getShooter() != null ? projectile.getShooter() : removerEntity;
/*     */       }
/*     */ 
/*  88 */       if ((removerEntity instanceof Player)) {
/*  89 */         Player player = (Player)removerEntity;
/*     */ 
/*  91 */         if (wcfg.getBlacklist() != null) {
/*  92 */           if (((hanging instanceof Painting)) && (!wcfg.getBlacklist().check(new BlockBreakBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), 321), false, false)))
/*     */           {
/*  96 */             event.setCancelled(true);
/*  97 */             return;
/*  98 */           }if (((hanging instanceof ItemFrame)) && (!wcfg.getBlacklist().check(new BlockBreakBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), 389), false, false)))
/*     */           {
/* 102 */             event.setCancelled(true);
/* 103 */             return;
/*     */           }
/*     */         }
/*     */ 
/* 107 */         if ((wcfg.useRegions) && 
/* 108 */           (!this.plugin.getGlobalRegionManager().canBuild(player, hanging.getLocation()))) {
/* 109 */           player.sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
/* 110 */           event.setCancelled(true);
/* 111 */           return;
/*     */         }
/*     */       }
/*     */       else {
/* 115 */         if ((removerEntity instanceof Creeper)) {
/* 116 */           if ((wcfg.blockCreeperBlockDamage) || (wcfg.blockCreeperExplosions)) {
/* 117 */             event.setCancelled(true);
/* 118 */             return;
/*     */           }
/* 120 */           if ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.CREEPER_EXPLOSION, hanging.getLocation()))) {
/* 121 */             event.setCancelled(true);
/* 122 */             return;
/*     */           }
/*     */         }
/*     */ 
/* 126 */         if (((hanging instanceof Painting)) && ((wcfg.blockEntityPaintingDestroy) || ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.ENTITY_PAINTING_DESTROY, hanging.getLocation())))))
/*     */         {
/* 130 */           event.setCancelled(true);
/* 131 */         } else if (((hanging instanceof ItemFrame)) && ((wcfg.blockEntityItemFrameDestroy) || ((wcfg.useRegions) && (!this.plugin.getGlobalRegionManager().allows(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, hanging.getLocation())))))
/*     */         {
/* 135 */           event.setCancelled(true);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 140 */     else if (((hanging instanceof Painting)) && (wcfg.blockEntityPaintingDestroy) && (event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION))
/*     */     {
/* 142 */       event.setCancelled(true);
/* 143 */     } else if (((hanging instanceof ItemFrame)) && (wcfg.blockEntityItemFrameDestroy) && (event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION))
/*     */     {
/* 145 */       event.setCancelled(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onHangingPlace(HangingPlaceEvent event)
/*     */   {
/* 152 */     Block placedOn = event.getBlock();
/* 153 */     Player player = event.getPlayer();
/* 154 */     World world = placedOn.getWorld();
/*     */ 
/* 156 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 157 */     WorldConfiguration wcfg = cfg.get(world);
/*     */ 
/* 159 */     if (wcfg.getBlacklist() != null)
/*     */     {
/* 161 */       if (((event.getEntity() instanceof Painting)) && (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), 321), false, false)))
/*     */       {
/* 165 */         event.setCancelled(true);
/* 166 */         return;
/* 167 */       }if (((event.getEntity() instanceof ItemFrame)) && (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(this.plugin.wrapPlayer(player), BukkitUtil.toVector(player.getLocation()), 389), false, false)))
/*     */       {
/* 171 */         event.setCancelled(true);
/* 172 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 176 */     if ((wcfg.useRegions) && 
/* 177 */       (!this.plugin.getGlobalRegionManager().canBuild(player, placedOn.getRelative(event.getBlockFace())))) {
/* 178 */       player.sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
/* 179 */       event.setCancelled(true);
/* 180 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
/*     */   public void onEntityInteract(PlayerInteractEntityEvent event)
/*     */   {
/* 187 */     Player player = event.getPlayer();
/* 188 */     Entity entity = event.getRightClicked();
/*     */ 
/* 190 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 191 */     WorldConfiguration wcfg = cfg.get(entity.getWorld());
/*     */ 
/* 193 */     if ((wcfg.useRegions) && (((entity instanceof ItemFrame)) || ((entity instanceof Painting))) && 
/* 194 */       (!this.plugin.getGlobalRegionManager().canBuild(player, entity.getLocation()))) {
/* 195 */       player.sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
/* 196 */       event.setCancelled(true);
/* 197 */       return;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardHangingListener
 * JD-Core Version:    0.6.2
 */