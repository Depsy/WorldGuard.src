/*     */ package com.sk89q.worldguard.protection.flags;
/*     */ 
/*     */ import org.bukkit.GameMode;
/*     */ import org.bukkit.entity.EntityType;
/*     */ 
/*     */ public final class DefaultFlag
/*     */ {
/*  31 */   public static final StateFlag PASSTHROUGH = new StateFlag("passthrough", false, RegionGroup.ALL);
/*  32 */   public static final StateFlag BUILD = new StateFlag("build", true, RegionGroup.NON_MEMBERS);
/*  33 */   public static final RegionGroupFlag CONSTRUCT = new RegionGroupFlag("construct", RegionGroup.MEMBERS);
/*  34 */   public static final StateFlag PVP = new StateFlag("pvp", true, RegionGroup.ALL);
/*  35 */   public static final StateFlag MOB_DAMAGE = new StateFlag("mob-damage", true, RegionGroup.ALL);
/*  36 */   public static final StateFlag MOB_SPAWNING = new StateFlag("mob-spawning", true, RegionGroup.ALL);
/*  37 */   public static final StateFlag CREEPER_EXPLOSION = new StateFlag("creeper-explosion", true, RegionGroup.ALL);
/*  38 */   public static final StateFlag ENDERDRAGON_BLOCK_DAMAGE = new StateFlag("enderdragon-block-damage", true);
/*  39 */   public static final StateFlag GHAST_FIREBALL = new StateFlag("ghast-fireball", true, RegionGroup.ALL);
/*  40 */   public static final StateFlag OTHER_EXPLOSION = new StateFlag("other-explosion", true);
/*  41 */   public static final StateFlag SLEEP = new StateFlag("sleep", true);
/*  42 */   public static final StateFlag TNT = new StateFlag("tnt", true, RegionGroup.ALL);
/*  43 */   public static final StateFlag LIGHTER = new StateFlag("lighter", true);
/*  44 */   public static final StateFlag FIRE_SPREAD = new StateFlag("fire-spread", true);
/*  45 */   public static final StateFlag LAVA_FIRE = new StateFlag("lava-fire", true);
/*  46 */   public static final StateFlag LIGHTNING = new StateFlag("lightning", true);
/*  47 */   public static final StateFlag CHEST_ACCESS = new StateFlag("chest-access", false);
/*  48 */   public static final StateFlag WATER_FLOW = new StateFlag("water-flow", true);
/*  49 */   public static final StateFlag LAVA_FLOW = new StateFlag("lava-flow", true);
/*  50 */   public static final StateFlag USE = new StateFlag("use", true);
/*  51 */   public static final StateFlag PLACE_VEHICLE = new StateFlag("vehicle-place", false);
/*  52 */   public static final StateFlag DESTROY_VEHICLE = new StateFlag("vehicle-destroy", false);
/*  53 */   public static final StateFlag PISTONS = new StateFlag("pistons", true);
/*  54 */   public static final StateFlag SNOW_FALL = new StateFlag("snow-fall", true);
/*  55 */   public static final StateFlag SNOW_MELT = new StateFlag("snow-melt", true);
/*  56 */   public static final StateFlag ICE_FORM = new StateFlag("ice-form", true);
/*  57 */   public static final StateFlag ICE_MELT = new StateFlag("ice-melt", true);
/*  58 */   public static final StateFlag MUSHROOMS = new StateFlag("mushroom-growth", true);
/*  59 */   public static final StateFlag LEAF_DECAY = new StateFlag("leaf-decay", true);
/*  60 */   public static final StateFlag GRASS_SPREAD = new StateFlag("grass-growth", true);
/*  61 */   public static final StateFlag MYCELIUM_SPREAD = new StateFlag("mycelium-spread", true);
/*  62 */   public static final StateFlag VINE_GROWTH = new StateFlag("vine-growth", true);
/*  63 */   public static final StateFlag SOIL_DRY = new StateFlag("soil-dry", true);
/*  64 */   public static final StateFlag ENDER_BUILD = new StateFlag("enderman-grief", true);
/*  65 */   public static final StateFlag INVINCIBILITY = new StateFlag("invincible", false, RegionGroup.ALL);
/*  66 */   public static final StateFlag EXP_DROPS = new StateFlag("exp-drops", true, RegionGroup.ALL);
/*  67 */   public static final StateFlag SEND_CHAT = new StateFlag("send-chat", true);
/*  68 */   public static final StateFlag RECEIVE_CHAT = new StateFlag("receive-chat", true);
/*  69 */   public static final StateFlag ENTRY = new StateFlag("entry", true);
/*  70 */   public static final StateFlag EXIT = new StateFlag("exit", true);
/*  71 */   public static final StateFlag ITEM_DROP = new StateFlag("item-drop", true);
/*  72 */   public static final StateFlag ENDERPEARL = new StateFlag("enderpearl", true);
/*  73 */   public static final StateFlag ENTITY_PAINTING_DESTROY = new StateFlag("entity-painting-destroy", true);
/*  74 */   public static final StateFlag ENTITY_ITEM_FRAME_DESTROY = new StateFlag("entity-item-frame-destroy", true);
/*  75 */   public static final StateFlag POTION_SPLASH = new StateFlag("potion-splash", true);
/*  76 */   public static final StringFlag GREET_MESSAGE = new StringFlag("greeting", RegionGroup.ALL);
/*  77 */   public static final StringFlag FAREWELL_MESSAGE = new StringFlag("farewell", RegionGroup.ALL);
/*  78 */   public static final BooleanFlag NOTIFY_ENTER = new BooleanFlag("notify-enter", RegionGroup.ALL);
/*  79 */   public static final BooleanFlag NOTIFY_LEAVE = new BooleanFlag("notify-leave", RegionGroup.ALL);
/*  80 */   public static final SetFlag<EntityType> DENY_SPAWN = new SetFlag("deny-spawn", RegionGroup.ALL, new EntityTypeFlag(null));
/*  81 */   public static final EnumFlag<GameMode> GAME_MODE = new EnumFlag("game-mode", GameMode.class, RegionGroup.ALL);
/*  82 */   public static final IntegerFlag HEAL_DELAY = new IntegerFlag("heal-delay", RegionGroup.ALL);
/*  83 */   public static final IntegerFlag HEAL_AMOUNT = new IntegerFlag("heal-amount", RegionGroup.ALL);
/*  84 */   public static final DoubleFlag MIN_HEAL = new DoubleFlag("heal-min-health", RegionGroup.ALL);
/*  85 */   public static final DoubleFlag MAX_HEAL = new DoubleFlag("heal-max-health", RegionGroup.ALL);
/*  86 */   public static final IntegerFlag FEED_DELAY = new IntegerFlag("feed-delay", RegionGroup.ALL);
/*  87 */   public static final IntegerFlag FEED_AMOUNT = new IntegerFlag("feed-amount", RegionGroup.ALL);
/*  88 */   public static final IntegerFlag MIN_FOOD = new IntegerFlag("feed-min-hunger", RegionGroup.ALL);
/*  89 */   public static final IntegerFlag MAX_FOOD = new IntegerFlag("feed-max-hunger", RegionGroup.ALL);
/*     */ 
/*  92 */   public static final LocationFlag TELE_LOC = new LocationFlag("teleport", RegionGroup.MEMBERS);
/*  93 */   public static final LocationFlag SPAWN_LOC = new LocationFlag("spawn", RegionGroup.MEMBERS);
/*  94 */   public static final StateFlag ENABLE_SHOP = new StateFlag("allow-shop", false);
/*  95 */   public static final BooleanFlag BUYABLE = new BooleanFlag("buyable");
/*  96 */   public static final DoubleFlag PRICE = new DoubleFlag("price");
/*  97 */   public static final SetFlag<String> BLOCKED_CMDS = new SetFlag("blocked-cmds", RegionGroup.ALL, new CommandStringFlag(null));
/*  98 */   public static final SetFlag<String> ALLOWED_CMDS = new SetFlag("allowed-cmds", RegionGroup.ALL, new CommandStringFlag(null));
/*     */ 
/* 100 */   public static final Flag<?>[] flagsList = { PASSTHROUGH, BUILD, CONSTRUCT, PVP, CHEST_ACCESS, PISTONS, TNT, LIGHTER, USE, PLACE_VEHICLE, DESTROY_VEHICLE, SLEEP, MOB_DAMAGE, MOB_SPAWNING, DENY_SPAWN, INVINCIBILITY, EXP_DROPS, CREEPER_EXPLOSION, OTHER_EXPLOSION, ENDERDRAGON_BLOCK_DAMAGE, GHAST_FIREBALL, ENDER_BUILD, GREET_MESSAGE, FAREWELL_MESSAGE, NOTIFY_ENTER, NOTIFY_LEAVE, EXIT, ENTRY, LIGHTNING, ENTITY_PAINTING_DESTROY, ENDERPEARL, ENTITY_ITEM_FRAME_DESTROY, ITEM_DROP, HEAL_AMOUNT, HEAL_DELAY, MIN_HEAL, MAX_HEAL, FEED_DELAY, FEED_AMOUNT, MIN_FOOD, MAX_FOOD, SNOW_FALL, SNOW_MELT, ICE_FORM, ICE_MELT, SOIL_DRY, GAME_MODE, MUSHROOMS, LEAF_DECAY, GRASS_SPREAD, MYCELIUM_SPREAD, VINE_GROWTH, SEND_CHAT, RECEIVE_CHAT, FIRE_SPREAD, LAVA_FIRE, LAVA_FLOW, WATER_FLOW, TELE_LOC, SPAWN_LOC, POTION_SPLASH, BLOCKED_CMDS, ALLOWED_CMDS, PRICE, BUYABLE, ENABLE_SHOP };
/*     */ 
/*     */   public static Flag<?>[] getFlags()
/*     */   {
/* 121 */     return flagsList;
/*     */   }
/*     */ 
/*     */   public static Flag<?> fuzzyMatchFlag(String id)
/*     */   {
/* 131 */     for (Flag flag : getFlags()) {
/* 132 */       if (flag.getName().replace("-", "").equalsIgnoreCase(id.replace("-", ""))) {
/* 133 */         return flag;
/*     */       }
/*     */     }
/*     */ 
/* 137 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.DefaultFlag
 * JD-Core Version:    0.6.2
 */