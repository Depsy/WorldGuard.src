/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.util.yaml.YAMLFormat;
/*     */ import com.sk89q.util.yaml.YAMLProcessor;
/*     */ import com.sk89q.worldguard.blacklist.Blacklist;
/*     */ import com.sk89q.worldguard.blacklist.BlacklistLogger;
/*     */ import com.sk89q.worldguard.blacklist.loggers.ConsoleLoggerHandler;
/*     */ import com.sk89q.worldguard.blacklist.loggers.DatabaseLoggerHandler;
/*     */ import com.sk89q.worldguard.blacklist.loggers.FileLoggerHandler;
/*     */ import com.sk89q.worldguard.chest.ChestProtection;
/*     */ import com.sk89q.worldguard.chest.SignChestProtection;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.potion.PotionEffectType;
/*     */ 
/*     */ public class WorldConfiguration
/*     */ {
/*     */   public static final String CONFIG_HEADER = "#\r\n# WorldGuard's world configuration file\r\n#\r\n# This is a world configuration file. Anything placed into here will only\r\n# affect this world. If you don't put anything in this file, then the\r\n# settings will be inherited from the main configuration file.\r\n#\r\n# If you see {} below, that means that there are NO entries in this file.\r\n# Remove the {} and add your own entries.\r\n#\r\n";
/*     */   private WorldGuardPlugin plugin;
/*     */   private String worldName;
/*     */   private YAMLProcessor parentConfig;
/*     */   private YAMLProcessor config;
/*     */   private File blacklistFile;
/*     */   private Blacklist blacklist;
/*  75 */   private ChestProtection chestProtection = new SignChestProtection();
/*     */   public boolean summaryOnStart;
/*     */   public boolean opPermissions;
/*     */   public boolean fireSpreadDisableToggle;
/*     */   public boolean itemDurability;
/*     */   public boolean simulateSponge;
/*     */   public int spongeRadius;
/*     */   public boolean disableExpDrops;
/*     */   public Set<PotionEffectType> blockPotions;
/*     */   public boolean blockPotionsAlways;
/*     */   public boolean pumpkinScuba;
/*     */   public boolean redstoneSponges;
/*     */   public boolean noPhysicsGravel;
/*     */   public boolean noPhysicsSand;
/*     */   public boolean ropeLadders;
/*     */   public boolean allowPortalAnywhere;
/*     */   public Set<Integer> preventWaterDamage;
/*     */   public boolean blockLighter;
/*     */   public boolean disableFireSpread;
/*     */   public Set<Integer> disableFireSpreadBlocks;
/*     */   public boolean preventLavaFire;
/*     */   public Set<Integer> allowedLavaSpreadOver;
/*     */   public boolean blockTNTExplosions;
/*     */   public boolean blockTNTBlockDamage;
/*     */   public boolean blockCreeperExplosions;
/*     */   public boolean blockCreeperBlockDamage;
/*     */   public boolean blockWitherExplosions;
/*     */   public boolean blockWitherBlockDamage;
/*     */   public boolean blockWitherSkullExplosions;
/*     */   public boolean blockWitherSkullBlockDamage;
/*     */   public boolean blockEnderDragonBlockDamage;
/*     */   public boolean blockEnderDragonPortalCreation;
/*     */   public boolean blockFireballExplosions;
/*     */   public boolean blockFireballBlockDamage;
/*     */   public boolean blockOtherExplosions;
/*     */   public boolean blockEntityPaintingDestroy;
/*     */   public boolean blockEntityItemFrameDestroy;
/*     */   public boolean blockPluginSpawning;
/*     */   public boolean blockGroundSlimes;
/*     */   public boolean blockZombieDoorDestruction;
/*     */   public boolean disableContactDamage;
/*     */   public boolean disableFallDamage;
/*     */   public boolean disableLavaDamage;
/*     */   public boolean disableFireDamage;
/*     */   public boolean disableLightningDamage;
/*     */   public boolean disableDrowningDamage;
/*     */   public boolean disableSuffocationDamage;
/*     */   public boolean teleportOnSuffocation;
/*     */   public boolean disableVoidDamage;
/*     */   public boolean teleportOnVoid;
/*     */   public boolean disableExplosionDamage;
/*     */   public boolean disableMobDamage;
/*     */   public boolean useRegions;
/*     */   public boolean highFreqFlags;
/*     */   public int regionWand;
/*     */   public Set<EntityType> blockCreatureSpawn;
/*     */   public boolean allowTamedSpawns;
/*     */   public int maxClaimVolume;
/*     */   public boolean claimOnlyInsideExistingRegions;
/*     */   public int maxRegionCountPerPlayer;
/*     */   public boolean antiWolfDumbness;
/*     */   public boolean signChestProtection;
/*     */   public boolean disableSignChestProtectionCheck;
/*     */   public boolean removeInfiniteStacks;
/*     */   public boolean disableCreatureCropTrampling;
/*     */   public boolean disablePlayerCropTrampling;
/*     */   public boolean preventLightningFire;
/*     */   public Set<Integer> disallowedLightningBlocks;
/*     */   public boolean disableThunder;
/*     */   public boolean disableWeather;
/*     */   public boolean alwaysRaining;
/*     */   public boolean alwaysThundering;
/*     */   public boolean disablePigZap;
/*     */   public boolean disableCreeperPower;
/*     */   public boolean disableHealthRegain;
/*     */   public boolean disableMushroomSpread;
/*     */   public boolean disableIceMelting;
/*     */   public boolean disableSnowMelting;
/*     */   public boolean disableSnowFormation;
/*     */   public boolean disableIceFormation;
/*     */   public boolean disableLeafDecay;
/*     */   public boolean disableGrassGrowth;
/*     */   public boolean disableMyceliumSpread;
/*     */   public boolean disableVineGrowth;
/*     */   public boolean disableEndermanGriefing;
/*     */   public boolean disableSnowmanTrails;
/*     */   public boolean disableSoilDehydration;
/*     */   public Set<Integer> allowedSnowFallOver;
/*     */   public boolean regionInvinciblityRemovesMobs;
/*     */   public boolean explosionFlagCancellation;
/*     */   public boolean disableDeathMessages;
/*     */   public boolean disableObsidianGenerators;
/*     */   private Map<String, Integer> maxRegionCounts;
/*     */ 
/*     */   public WorldConfiguration(WorldGuardPlugin plugin, String worldName, YAMLProcessor parentConfig)
/*     */   {
/* 186 */     File baseFolder = new File(plugin.getDataFolder(), "worlds/" + worldName);
/* 187 */     File configFile = new File(baseFolder, "config.yml");
/* 188 */     this.blacklistFile = new File(baseFolder, "blacklist.txt");
/*     */ 
/* 190 */     this.plugin = plugin;
/* 191 */     this.worldName = worldName;
/* 192 */     this.parentConfig = parentConfig;
/*     */ 
/* 194 */     plugin.createDefaultConfiguration(configFile, "config_world.yml");
/* 195 */     plugin.createDefaultConfiguration(this.blacklistFile, "blacklist.txt");
/*     */ 
/* 197 */     this.config = new YAMLProcessor(configFile, true, YAMLFormat.EXTENDED);
/* 198 */     loadConfiguration();
/*     */ 
/* 200 */     if (this.summaryOnStart)
/* 201 */       plugin.getLogger().info("Loaded configuration for world '" + worldName + "'");
/*     */   }
/*     */ 
/*     */   private boolean getBoolean(String node, boolean def)
/*     */   {
/* 206 */     boolean val = this.parentConfig.getBoolean(node, def);
/*     */ 
/* 208 */     if (this.config.getProperty(node) != null) {
/* 209 */       return this.config.getBoolean(node, def);
/*     */     }
/* 211 */     return val;
/*     */   }
/*     */ 
/*     */   private String getString(String node, String def)
/*     */   {
/* 216 */     String val = this.parentConfig.getString(node, def);
/*     */ 
/* 218 */     if (this.config.getProperty(node) != null) {
/* 219 */       return this.config.getString(node, def);
/*     */     }
/* 221 */     return val;
/*     */   }
/*     */ 
/*     */   private int getInt(String node, int def)
/*     */   {
/* 226 */     int val = this.parentConfig.getInt(node, def);
/*     */ 
/* 228 */     if (this.config.getProperty(node) != null) {
/* 229 */       return this.config.getInt(node, def);
/*     */     }
/* 231 */     return val;
/*     */   }
/*     */ 
/*     */   private double getDouble(String node, double def)
/*     */   {
/* 237 */     double val = this.parentConfig.getDouble(node, def);
/*     */ 
/* 239 */     if (this.config.getProperty(node) != null) {
/* 240 */       return this.config.getDouble(node, def);
/*     */     }
/* 242 */     return val;
/*     */   }
/*     */ 
/*     */   private List<Integer> getIntList(String node, List<Integer> def)
/*     */   {
/* 247 */     List res = this.parentConfig.getIntList(node, def);
/*     */ 
/* 249 */     if ((res == null) || (res.size() == 0)) {
/* 250 */       this.parentConfig.setProperty(node, new ArrayList());
/*     */     }
/*     */ 
/* 253 */     if (this.config.getProperty(node) != null) {
/* 254 */       res = this.config.getIntList(node, def);
/*     */     }
/*     */ 
/* 257 */     return res;
/*     */   }
/*     */ 
/*     */   private List<String> getStringList(String node, List<String> def) {
/* 261 */     List res = this.parentConfig.getStringList(node, def);
/*     */ 
/* 263 */     if ((res == null) || (res.size() == 0)) {
/* 264 */       this.parentConfig.setProperty(node, new ArrayList());
/*     */     }
/*     */ 
/* 267 */     if (this.config.getProperty(node) != null) {
/* 268 */       res = this.config.getStringList(node, def);
/*     */     }
/*     */ 
/* 271 */     return res;
/*     */   }
/*     */ 
/*     */   private List<String> getKeys(String node) {
/* 275 */     List res = this.parentConfig.getKeys(node);
/*     */ 
/* 277 */     if ((res == null) || (res.size() == 0)) {
/* 278 */       res = this.config.getKeys(node);
/*     */     }
/* 280 */     if (res == null) {
/* 281 */       res = new ArrayList();
/*     */     }
/*     */ 
/* 284 */     return res;
/*     */   }
/*     */ 
/*     */   private Object getProperty(String node) {
/* 288 */     Object res = this.parentConfig.getProperty(node);
/*     */ 
/* 290 */     if (this.config.getProperty(node) != null) {
/* 291 */       res = this.config.getProperty(node);
/*     */     }
/*     */ 
/* 294 */     return res;
/*     */   }
/*     */ 
/*     */   private void loadConfiguration()
/*     */   {
/*     */     try
/*     */     {
/* 302 */       this.config.load();
/*     */     } catch (IOException e) {
/* 304 */       this.plugin.getLogger().severe("Error reading configuration for world " + this.worldName + ": ");
/* 305 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 308 */     this.summaryOnStart = getBoolean("summary-on-start", true);
/* 309 */     this.opPermissions = getBoolean("op-permissions", true);
/*     */ 
/* 311 */     this.itemDurability = getBoolean("protection.item-durability", true);
/* 312 */     this.removeInfiniteStacks = getBoolean("protection.remove-infinite-stacks", false);
/* 313 */     this.disableExpDrops = getBoolean("protection.disable-xp-orb-drops", false);
/* 314 */     this.disableObsidianGenerators = getBoolean("protection.disable-obsidian-generators", false);
/*     */ 
/* 316 */     this.blockPotions = new HashSet();
/* 317 */     for (String potionName : getStringList("gameplay.block-potions", null)) {
/* 318 */       PotionEffectType effect = PotionEffectType.getByName(potionName);
/*     */ 
/* 320 */       if (effect == null)
/* 321 */         this.plugin.getLogger().warning("Unknown potion effect type '" + potionName + "'");
/*     */       else {
/* 323 */         this.blockPotions.add(effect);
/*     */       }
/*     */     }
/* 326 */     this.blockPotionsAlways = getBoolean("gameplay.block-potions-overly-reliably", false);
/*     */ 
/* 328 */     this.simulateSponge = getBoolean("simulation.sponge.enable", true);
/* 329 */     this.spongeRadius = (Math.max(1, getInt("simulation.sponge.radius", 3)) - 1);
/* 330 */     this.redstoneSponges = getBoolean("simulation.sponge.redstone", false);
/*     */ 
/* 332 */     this.pumpkinScuba = getBoolean("default.pumpkin-scuba", false);
/* 333 */     this.disableHealthRegain = getBoolean("default.disable-health-regain", false);
/*     */ 
/* 335 */     this.noPhysicsGravel = getBoolean("physics.no-physics-gravel", false);
/* 336 */     this.noPhysicsSand = getBoolean("physics.no-physics-sand", false);
/* 337 */     this.ropeLadders = getBoolean("physics.vine-like-rope-ladders", false);
/* 338 */     this.allowPortalAnywhere = getBoolean("physics.allow-portal-anywhere", false);
/* 339 */     this.preventWaterDamage = new HashSet(getIntList("physics.disable-water-damage-blocks", null));
/*     */ 
/* 341 */     this.blockTNTExplosions = getBoolean("ignition.block-tnt", false);
/* 342 */     this.blockTNTBlockDamage = getBoolean("ignition.block-tnt-block-damage", false);
/* 343 */     this.blockLighter = getBoolean("ignition.block-lighter", false);
/*     */ 
/* 345 */     this.preventLavaFire = getBoolean("fire.disable-lava-fire-spread", true);
/* 346 */     this.disableFireSpread = getBoolean("fire.disable-all-fire-spread", false);
/* 347 */     this.disableFireSpreadBlocks = new HashSet(getIntList("fire.disable-fire-spread-blocks", null));
/* 348 */     this.allowedLavaSpreadOver = new HashSet(getIntList("fire.lava-spread-blocks", null));
/*     */ 
/* 350 */     this.blockCreeperExplosions = getBoolean("mobs.block-creeper-explosions", false);
/* 351 */     this.blockCreeperBlockDamage = getBoolean("mobs.block-creeper-block-damage", false);
/* 352 */     this.blockWitherExplosions = getBoolean("mobs.block-wither-explosions", false);
/* 353 */     this.blockWitherBlockDamage = getBoolean("mobs.block-wither-block-damage", false);
/* 354 */     this.blockWitherSkullExplosions = getBoolean("mobs.block-wither-skull-explosions", false);
/* 355 */     this.blockWitherSkullBlockDamage = getBoolean("mobs.block-wither-skull-block-damage", false);
/* 356 */     this.blockEnderDragonBlockDamage = getBoolean("mobs.block-enderdragon-block-damage", false);
/* 357 */     this.blockEnderDragonPortalCreation = getBoolean("mobs.block-enderdragon-portal-creation", false);
/* 358 */     this.blockFireballExplosions = getBoolean("mobs.block-fireball-explosions", false);
/* 359 */     this.blockFireballBlockDamage = getBoolean("mobs.block-fireball-block-damage", false);
/* 360 */     this.antiWolfDumbness = getBoolean("mobs.anti-wolf-dumbness", false);
/* 361 */     this.allowTamedSpawns = getBoolean("mobs.allow-tamed-spawns", true);
/* 362 */     this.disableEndermanGriefing = getBoolean("mobs.disable-enderman-griefing", false);
/* 363 */     this.disableSnowmanTrails = getBoolean("mobs.disable-snowman-trails", false);
/* 364 */     this.blockEntityPaintingDestroy = getBoolean("mobs.block-painting-destroy", false);
/* 365 */     this.blockEntityItemFrameDestroy = getBoolean("mobs.block-item-frame-destroy", false);
/* 366 */     this.blockPluginSpawning = getBoolean("mobs.block-plugin-spawning", true);
/* 367 */     this.blockGroundSlimes = getBoolean("mobs.block-above-ground-slimes", false);
/* 368 */     this.blockOtherExplosions = getBoolean("mobs.block-other-explosions", false);
/* 369 */     this.blockZombieDoorDestruction = getBoolean("mobs.block-zombie-door-destruction", false);
/*     */ 
/* 371 */     this.disableFallDamage = getBoolean("player-damage.disable-fall-damage", false);
/* 372 */     this.disableLavaDamage = getBoolean("player-damage.disable-lava-damage", false);
/* 373 */     this.disableFireDamage = getBoolean("player-damage.disable-fire-damage", false);
/* 374 */     this.disableLightningDamage = getBoolean("player-damage.disable-lightning-damage", false);
/* 375 */     this.disableDrowningDamage = getBoolean("player-damage.disable-drowning-damage", false);
/* 376 */     this.disableSuffocationDamage = getBoolean("player-damage.disable-suffocation-damage", false);
/* 377 */     this.disableContactDamage = getBoolean("player-damage.disable-contact-damage", false);
/* 378 */     this.teleportOnSuffocation = getBoolean("player-damage.teleport-on-suffocation", false);
/* 379 */     this.disableVoidDamage = getBoolean("player-damage.disable-void-damage", false);
/* 380 */     this.teleportOnVoid = getBoolean("player-damage.teleport-on-void-falling", false);
/* 381 */     this.disableExplosionDamage = getBoolean("player-damage.disable-explosion-damage", false);
/* 382 */     this.disableMobDamage = getBoolean("player-damage.disable-mob-damage", false);
/* 383 */     this.disableDeathMessages = getBoolean("player-damage.disable-death-messages", false);
/*     */ 
/* 385 */     this.signChestProtection = getBoolean("chest-protection.enable", false);
/* 386 */     this.disableSignChestProtectionCheck = getBoolean("chest-protection.disable-off-check", false);
/*     */ 
/* 388 */     this.disableCreatureCropTrampling = getBoolean("crops.disable-creature-trampling", false);
/* 389 */     this.disablePlayerCropTrampling = getBoolean("crops.disable-player-trampling", false);
/*     */ 
/* 391 */     this.disallowedLightningBlocks = new HashSet(getIntList("weather.prevent-lightning-strike-blocks", null));
/* 392 */     this.preventLightningFire = getBoolean("weather.disable-lightning-strike-fire", false);
/* 393 */     this.disableThunder = getBoolean("weather.disable-thunderstorm", false);
/* 394 */     this.disableWeather = getBoolean("weather.disable-weather", false);
/* 395 */     this.disablePigZap = getBoolean("weather.disable-pig-zombification", false);
/* 396 */     this.disableCreeperPower = getBoolean("weather.disable-powered-creepers", false);
/* 397 */     this.alwaysRaining = getBoolean("weather.always-raining", false);
/* 398 */     this.alwaysThundering = getBoolean("weather.always-thundering", false);
/*     */ 
/* 400 */     this.disableMushroomSpread = getBoolean("dynamics.disable-mushroom-spread", false);
/* 401 */     this.disableIceMelting = getBoolean("dynamics.disable-ice-melting", false);
/* 402 */     this.disableSnowMelting = getBoolean("dynamics.disable-snow-melting", false);
/* 403 */     this.disableSnowFormation = getBoolean("dynamics.disable-snow-formation", false);
/* 404 */     this.disableIceFormation = getBoolean("dynamics.disable-ice-formation", false);
/* 405 */     this.disableLeafDecay = getBoolean("dynamics.disable-leaf-decay", false);
/* 406 */     this.disableGrassGrowth = getBoolean("dynamics.disable-grass-growth", false);
/* 407 */     this.disableMyceliumSpread = getBoolean("dynamics.disable-mycelium-spread", false);
/* 408 */     this.disableVineGrowth = getBoolean("dynamics.disable-vine-growth", false);
/* 409 */     this.disableSoilDehydration = getBoolean("dynamics.disable-soil-dehydration", false);
/* 410 */     this.allowedSnowFallOver = new HashSet(getIntList("dynamics.snow-fall-blocks", null));
/*     */ 
/* 412 */     this.useRegions = getBoolean("regions.enable", true);
/* 413 */     this.regionInvinciblityRemovesMobs = getBoolean("regions.invincibility-removes-mobs", false);
/* 414 */     this.explosionFlagCancellation = getBoolean("regions.explosion-flags-block-entity-damage", true);
/* 415 */     this.highFreqFlags = getBoolean("regions.high-frequency-flags", false);
/* 416 */     this.regionWand = getInt("regions.wand", 334);
/* 417 */     this.maxClaimVolume = getInt("regions.max-claim-volume", 30000);
/* 418 */     this.claimOnlyInsideExistingRegions = getBoolean("regions.claim-only-inside-existing-regions", false);
/*     */ 
/* 420 */     this.maxRegionCountPerPlayer = getInt("regions.max-region-count-per-player.default", 7);
/* 421 */     this.maxRegionCounts = new HashMap();
/* 422 */     this.maxRegionCounts.put(null, Integer.valueOf(this.maxRegionCountPerPlayer));
/*     */ 
/* 424 */     for (String key : getKeys("regions.max-region-count-per-player")) {
/* 425 */       if (!key.equalsIgnoreCase("default")) {
/* 426 */         Object val = getProperty("regions.max-region-count-per-player." + key);
/* 427 */         if ((val != null) && ((val instanceof Number))) {
/* 428 */           this.maxRegionCounts.put(key, Integer.valueOf(((Number)val).intValue()));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 437 */     this.blockCreatureSpawn = new HashSet();
/* 438 */     for (String creatureName : getStringList("mobs.block-creature-spawn", null)) {
/* 439 */       EntityType creature = EntityType.fromName(creatureName);
/*     */ 
/* 441 */       if (creature == null)
/* 442 */         this.plugin.getLogger().warning("Unknown mob type '" + creatureName + "'");
/* 443 */       else if (!creature.isAlive())
/* 444 */         this.plugin.getLogger().warning("Entity type '" + creatureName + "' is not a creature");
/*     */       else {
/* 446 */         this.blockCreatureSpawn.add(creature);
/*     */       }
/*     */     }
/*     */ 
/* 450 */     boolean useBlacklistAsWhitelist = getBoolean("blacklist.use-as-whitelist", false);
/*     */ 
/* 453 */     boolean logConsole = getBoolean("blacklist.logging.console.enable", true);
/*     */ 
/* 456 */     boolean logDatabase = getBoolean("blacklist.logging.database.enable", false);
/* 457 */     String dsn = getString("blacklist.logging.database.dsn", "jdbc:mysql://localhost:3306/minecraft");
/* 458 */     String user = getString("blacklist.logging.database.user", "root");
/* 459 */     String pass = getString("blacklist.logging.database.pass", "");
/* 460 */     String table = getString("blacklist.logging.database.table", "blacklist_events");
/*     */ 
/* 463 */     boolean logFile = getBoolean("blacklist.logging.file.enable", false);
/* 464 */     String logFilePattern = getString("blacklist.logging.file.path", "worldguard/logs/%Y-%m-%d.log");
/* 465 */     int logFileCacheSize = Math.max(1, getInt("blacklist.logging.file.open-files", 10));
/*     */     try
/*     */     {
/* 470 */       if (this.blacklist != null) {
/* 471 */         this.blacklist.getLogger().close();
/*     */       }
/*     */ 
/* 475 */       Blacklist blist = new BukkitBlacklist(Boolean.valueOf(useBlacklistAsWhitelist), this.plugin);
/* 476 */       blist.load(this.blacklistFile);
/*     */ 
/* 480 */       if (blist.isEmpty()) {
/* 481 */         this.blacklist = null;
/*     */       } else {
/* 483 */         this.blacklist = blist;
/* 484 */         if (this.summaryOnStart) {
/* 485 */           this.plugin.getLogger().log(Level.INFO, "Blacklist loaded.");
/*     */         }
/*     */ 
/* 488 */         BlacklistLogger blacklistLogger = blist.getLogger();
/*     */ 
/* 490 */         if (logDatabase) {
/* 491 */           blacklistLogger.addHandler(new DatabaseLoggerHandler(dsn, user, pass, table, this.worldName, this.plugin.getLogger()));
/*     */         }
/*     */ 
/* 494 */         if (logConsole) {
/* 495 */           blacklistLogger.addHandler(new ConsoleLoggerHandler(this.worldName, this.plugin.getLogger()));
/*     */         }
/*     */ 
/* 498 */         if (logFile) {
/* 499 */           FileLoggerHandler handler = new FileLoggerHandler(logFilePattern, logFileCacheSize, this.worldName, this.plugin.getLogger());
/*     */ 
/* 501 */           blacklistLogger.addHandler(handler);
/*     */         }
/*     */       }
/*     */     } catch (FileNotFoundException e) {
/* 505 */       this.plugin.getLogger().log(Level.WARNING, "WorldGuard blacklist does not exist.");
/*     */     } catch (IOException e) {
/* 507 */       this.plugin.getLogger().log(Level.WARNING, "Could not load WorldGuard blacklist: " + e.getMessage());
/*     */     }
/*     */ 
/* 512 */     if (this.summaryOnStart) {
/* 513 */       this.plugin.getLogger().log(Level.INFO, "(" + this.worldName + ") TNT ignition is PERMITTED.");
/*     */ 
/* 516 */       this.plugin.getLogger().log(Level.INFO, "(" + this.worldName + ") Lighters are PERMITTED.");
/*     */ 
/* 519 */       this.plugin.getLogger().log(Level.INFO, "(" + this.worldName + ") Lava fire is PERMITTED.");
/*     */ 
/* 523 */       if (this.disableFireSpread) {
/* 524 */         this.plugin.getLogger().log(Level.INFO, "(" + this.worldName + ") All fire spread is disabled.");
/*     */       }
/* 526 */       else if (this.disableFireSpreadBlocks.size() > 0) {
/* 527 */         this.plugin.getLogger().log(Level.INFO, "(" + this.worldName + ") Fire spread is limited to " + this.disableFireSpreadBlocks.size() + " block types.");
/*     */       }
/*     */       else
/*     */       {
/* 531 */         this.plugin.getLogger().log(Level.INFO, "(" + this.worldName + ") Fire spread is UNRESTRICTED.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 537 */     this.config.setHeader("#\r\n# WorldGuard's world configuration file\r\n#\r\n# This is a world configuration file. Anything placed into here will only\r\n# affect this world. If you don't put anything in this file, then the\r\n# settings will be inherited from the main configuration file.\r\n#\r\n# If you see {} below, that means that there are NO entries in this file.\r\n# Remove the {} and add your own entries.\r\n#\r\n");
/*     */ 
/* 539 */     this.config.save();
/*     */   }
/*     */ 
/*     */   public Blacklist getBlacklist() {
/* 543 */     return this.blacklist;
/*     */   }
/*     */ 
/*     */   public String getWorldName() {
/* 547 */     return this.worldName;
/*     */   }
/*     */ 
/*     */   public boolean isChestProtected(Block block, Player player) {
/* 551 */     if (!this.signChestProtection) {
/* 552 */       return false;
/*     */     }
/* 554 */     if ((this.plugin.hasPermission(player, "worldguard.chest-protection.override")) || (this.plugin.hasPermission(player, "worldguard.override.chest-protection")))
/*     */     {
/* 556 */       return false;
/*     */     }
/* 558 */     return this.chestProtection.isProtected(block, player);
/*     */   }
/*     */ 
/*     */   public boolean isChestProtected(Block block)
/*     */   {
/* 563 */     return (this.signChestProtection) && (this.chestProtection.isProtected(block, null));
/*     */   }
/*     */ 
/*     */   public boolean isChestProtectedPlacement(Block block, Player player) {
/* 567 */     if (!this.signChestProtection) {
/* 568 */       return false;
/*     */     }
/* 570 */     if ((this.plugin.hasPermission(player, "worldguard.chest-protection.override")) || (this.plugin.hasPermission(player, "worldguard.override.chest-protection")))
/*     */     {
/* 572 */       return false;
/*     */     }
/* 574 */     return this.chestProtection.isProtectedPlacement(block, player);
/*     */   }
/*     */ 
/*     */   public boolean isAdjacentChestProtected(Block block, Player player) {
/* 578 */     if (!this.signChestProtection) {
/* 579 */       return false;
/*     */     }
/* 581 */     if ((this.plugin.hasPermission(player, "worldguard.chest-protection.override")) || (this.plugin.hasPermission(player, "worldguard.override.chest-protection")))
/*     */     {
/* 583 */       return false;
/*     */     }
/* 585 */     return this.chestProtection.isAdjacentChestProtected(block, player);
/*     */   }
/*     */ 
/*     */   public ChestProtection getChestProtection() {
/* 589 */     return this.chestProtection;
/*     */   }
/*     */ 
/*     */   public int getMaxRegionCount(Player player) {
/* 593 */     int max = -1;
/* 594 */     for (String group : this.plugin.getGroups(player)) {
/* 595 */       if (this.maxRegionCounts.containsKey(group)) {
/* 596 */         int groupMax = ((Integer)this.maxRegionCounts.get(group)).intValue();
/* 597 */         if (max < groupMax) {
/* 598 */           max = groupMax;
/*     */         }
/*     */       }
/*     */     }
/* 602 */     if (max <= -1) {
/* 603 */       max = this.maxRegionCountPerPlayer;
/*     */     }
/* 605 */     return max;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldConfiguration
 * JD-Core Version:    0.6.2
 */