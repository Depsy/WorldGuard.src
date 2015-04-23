/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.GameMode;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class FlagStateManager
/*     */   implements Runnable
/*     */ {
/*     */   public static final int RUN_DELAY = 20;
/*     */   private WorldGuardPlugin plugin;
/*     */   private Map<String, PlayerFlagState> states;
/*     */ 
/*     */   public FlagStateManager(WorldGuardPlugin plugin)
/*     */   {
/*  55 */     this.plugin = plugin;
/*     */ 
/*  57 */     this.states = new HashMap();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  65 */     Player[] players = this.plugin.getServer().getOnlinePlayers();
/*  66 */     ConfigurationManager config = this.plugin.getGlobalStateManager();
/*     */ 
/*  68 */     for (Player player : players) {
/*  69 */       WorldConfiguration worldConfig = config.get(player.getWorld());
/*     */ 
/*  71 */       if (worldConfig.useRegions)
/*     */       {
/*     */         PlayerFlagState state;
/*  77 */         synchronized (this) {
/*  78 */           state = (PlayerFlagState)this.states.get(player.getName());
/*     */ 
/*  80 */           if (state == null) {
/*  81 */             state = new PlayerFlagState();
/*  82 */             this.states.put(player.getName(), state);
/*     */           }
/*     */         }
/*     */ 
/*  86 */         Vector playerLocation = BukkitUtil.toVector(player.getLocation());
/*  87 */         RegionManager regionManager = this.plugin.getGlobalRegionManager().get(player.getWorld());
/*  88 */         ApplicableRegionSet applicable = regionManager.getApplicableRegions(playerLocation);
/*     */ 
/*  90 */         if ((!RegionQueryUtil.isInvincible(this.plugin, player, applicable)) && (!this.plugin.getGlobalStateManager().hasGodMode(player)) && (player.getGameMode() != GameMode.CREATIVE))
/*     */         {
/*  93 */           processHeal(applicable, player, state);
/*  94 */           processFeed(applicable, player, state);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processHeal(ApplicableRegionSet applicable, Player player, PlayerFlagState state)
/*     */   {
/* 109 */     if (player.getHealth() <= 0.0D) {
/* 110 */       return;
/*     */     }
/*     */ 
/* 113 */     long now = System.currentTimeMillis();
/*     */ 
/* 115 */     Integer healAmount = (Integer)applicable.getFlag(DefaultFlag.HEAL_AMOUNT);
/* 116 */     Integer healDelay = (Integer)applicable.getFlag(DefaultFlag.HEAL_DELAY);
/* 117 */     Double minHealth = (Double)applicable.getFlag(DefaultFlag.MIN_HEAL);
/* 118 */     Double maxHealth = (Double)applicable.getFlag(DefaultFlag.MAX_HEAL);
/*     */ 
/* 120 */     if ((healAmount == null) || (healDelay == null) || (healAmount.intValue() == 0) || (healDelay.intValue() < 0)) {
/* 121 */       return;
/*     */     }
/* 123 */     if (minHealth == null) {
/* 124 */       minHealth = Double.valueOf(0.0D);
/*     */     }
/* 126 */     if (maxHealth == null) {
/* 127 */       maxHealth = Double.valueOf(player.getMaxHealth());
/*     */     }
/*     */ 
/* 131 */     minHealth = Double.valueOf(Math.min(player.getMaxHealth(), minHealth.doubleValue()));
/* 132 */     maxHealth = Double.valueOf(Math.min(player.getMaxHealth(), maxHealth.doubleValue()));
/*     */ 
/* 134 */     if ((player.getHealth() >= maxHealth.doubleValue()) && (healAmount.intValue() > 0)) {
/* 135 */       return;
/*     */     }
/*     */ 
/* 138 */     if (healDelay.intValue() <= 0) {
/* 139 */       player.setHealth((healAmount.intValue() > 0 ? maxHealth : minHealth).doubleValue());
/* 140 */       state.lastHeal = now;
/* 141 */     } else if (now - state.lastHeal > healDelay.intValue() * 1000)
/*     */     {
/* 143 */       player.setHealth(Math.min(maxHealth.doubleValue(), Math.max(minHealth.doubleValue(), player.getHealth() + healAmount.intValue())));
/* 144 */       state.lastHeal = now;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processFeed(ApplicableRegionSet applicable, Player player, PlayerFlagState state)
/*     */   {
/* 158 */     long now = System.currentTimeMillis();
/*     */ 
/* 160 */     Integer feedAmount = (Integer)applicable.getFlag(DefaultFlag.FEED_AMOUNT);
/* 161 */     Integer feedDelay = (Integer)applicable.getFlag(DefaultFlag.FEED_DELAY);
/* 162 */     Integer minHunger = (Integer)applicable.getFlag(DefaultFlag.MIN_FOOD);
/* 163 */     Integer maxHunger = (Integer)applicable.getFlag(DefaultFlag.MAX_FOOD);
/*     */ 
/* 165 */     if ((feedAmount == null) || (feedDelay == null) || (feedAmount.intValue() == 0) || (feedDelay.intValue() < 0)) {
/* 166 */       return;
/*     */     }
/* 168 */     if (minHunger == null) {
/* 169 */       minHunger = Integer.valueOf(0);
/*     */     }
/* 171 */     if (maxHunger == null) {
/* 172 */       maxHunger = Integer.valueOf(20);
/*     */     }
/*     */ 
/* 176 */     minHunger = Integer.valueOf(Math.min(20, minHunger.intValue()));
/* 177 */     maxHunger = Integer.valueOf(Math.min(20, maxHunger.intValue()));
/*     */ 
/* 179 */     if ((player.getFoodLevel() >= maxHunger.intValue()) && (feedAmount.intValue() > 0)) {
/* 180 */       return;
/*     */     }
/*     */ 
/* 183 */     if (feedDelay.intValue() <= 0) {
/* 184 */       player.setFoodLevel((feedAmount.intValue() > 0 ? maxHunger : minHunger).intValue());
/* 185 */       state.lastFeed = now;
/* 186 */     } else if (now - state.lastFeed > feedDelay.intValue() * 1000)
/*     */     {
/* 188 */       player.setFoodLevel(Math.min(maxHunger.intValue(), Math.max(minHunger.intValue(), player.getFoodLevel() + feedAmount.intValue())));
/* 189 */       state.lastFeed = now;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void forget(Player player)
/*     */   {
/* 199 */     this.states.remove(player.getName());
/*     */   }
/*     */ 
/*     */   public synchronized void forgetAll()
/*     */   {
/* 206 */     this.states.clear();
/*     */   }
/*     */ 
/*     */   public synchronized PlayerFlagState getState(Player player)
/*     */   {
/* 217 */     PlayerFlagState state = (PlayerFlagState)this.states.get(player.getName());
/*     */ 
/* 219 */     if (state == null) {
/* 220 */       state = new PlayerFlagState();
/* 221 */       this.states.put(player.getName(), state);
/*     */     }
/*     */ 
/* 224 */     return state;
/*     */   }
/*     */ 
/*     */   public static class PlayerFlagState
/*     */   {
/*     */     public long lastHeal;
/*     */     public long lastFeed;
/*     */     public String lastGreeting;
/*     */     public String lastFarewell;
/* 235 */     public Boolean lastExitAllowed = null;
/* 236 */     public Boolean notifiedForLeave = Boolean.valueOf(false);
/* 237 */     public Boolean notifiedForEnter = Boolean.valueOf(false);
/*     */     public GameMode lastGameMode;
/*     */     public World lastWorld;
/*     */     public int lastBlockX;
/*     */     public int lastBlockY;
/*     */     public int lastBlockZ;
/*     */     public World lastInvincibleWorld;
/*     */     public int lastInvincibleX;
/*     */     public int lastInvincibleY;
/*     */     public int lastInvincibleZ;
/*     */     public boolean wasInvincible;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.FlagStateManager
 * JD-Core Version:    0.6.2
 */