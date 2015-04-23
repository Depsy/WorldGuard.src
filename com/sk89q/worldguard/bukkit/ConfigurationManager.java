/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.commandbook.CommandBook;
/*     */ import com.sk89q.commandbook.GodComponent;
/*     */ import com.sk89q.util.yaml.YAMLFormat;
/*     */ import com.sk89q.util.yaml.YAMLProcessor;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.blacklist.Blacklist;
/*     */ import com.zachsthings.libcomponents.ComponentManager;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class ConfigurationManager
/*     */ {
/*     */   private static final String CONFIG_HEADER = "#\r\n# WorldGuard's main configuration file\r\n#\r\n# This is the global configuration file. Anything placed into here will\r\n# be applied to all worlds. However, each world has its own configuration\r\n# file to allow you to replace most settings in here for that world only.\r\n#\r\n# About editing this file:\r\n# - DO NOT USE TABS. You MUST use spaces or Bukkit will complain. If\r\n#   you use an editor like Notepad++ (recommended for Windows users), you\r\n#   must configure it to \"replace tabs with spaces.\" In Notepad++, this can\r\n#   be changed in Settings > Preferences > Language Menu.\r\n# - Don't get rid of the indents. They are indented so some entries are\r\n#   in categories (like \"enforce-single-session\" is in the \"protection\"\r\n#   category.\r\n# - If you want to check the format of this file before putting it\r\n#   into WorldGuard, paste it into http://yaml-online-parser.appspot.com/\r\n#   and see if it gives \"ERROR:\".\r\n# - Lines starting with # are comments and so they are ignored.\r\n#\r\n";
/*     */   private WorldGuardPlugin plugin;
/*     */   private ConcurrentMap<String, WorldConfiguration> worlds;
/*     */   private YAMLProcessor config;
/*     */ 
/*     */   @Deprecated
/*  87 */   private Set<String> hasGodMode = new HashSet();
/*     */ 
/*  93 */   private Set<String> hasAmphibious = new HashSet();
/*     */ 
/*  95 */   private boolean hasCommandBookGodMode = false;
/*     */   public boolean useRegionsScheduler;
/*     */   public boolean useRegionsCreatureSpawnEvent;
/*  99 */   public boolean activityHaltToggle = false;
/*     */   public boolean autoGodMode;
/*     */   public boolean usePlayerMove;
/*     */   public boolean usePlayerTeleports;
/*     */   public boolean deopOnJoin;
/*     */   public boolean blockInGameOp;
/* 105 */   public Map<String, String> hostKeys = new HashMap();
/*     */ 
/* 110 */   public boolean useSqlDatabase = false;
/*     */   public String sqlDsn;
/*     */   public String sqlUsername;
/*     */   public String sqlPassword;
/*     */ 
/*     */   public ConfigurationManager(WorldGuardPlugin plugin)
/*     */   {
/* 121 */     this.plugin = plugin;
/* 122 */     this.worlds = new ConcurrentHashMap();
/*     */   }
/*     */ 
/*     */   public void load()
/*     */   {
/* 131 */     this.plugin.createDefaultConfiguration(new File(this.plugin.getDataFolder(), "config.yml"), "config.yml");
/*     */ 
/* 134 */     this.config = new YAMLProcessor(new File(this.plugin.getDataFolder(), "config.yml"), true, YAMLFormat.EXTENDED);
/*     */     try {
/* 136 */       this.config.load();
/*     */     } catch (IOException e) {
/* 138 */       this.plugin.getLogger().severe("Error reading configuration for global config: ");
/* 139 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 142 */     this.config.removeProperty("suppress-tick-sync-warnings");
/* 143 */     this.useRegionsScheduler = this.config.getBoolean("regions.use-scheduler", true);
/* 144 */     this.useRegionsCreatureSpawnEvent = this.config.getBoolean("regions.use-creature-spawn-event", true);
/* 145 */     this.autoGodMode = this.config.getBoolean("auto-invincible", this.config.getBoolean("auto-invincible-permission", false));
/* 146 */     this.config.removeProperty("auto-invincible-permission");
/* 147 */     this.usePlayerMove = this.config.getBoolean("use-player-move-event", true);
/* 148 */     this.usePlayerTeleports = this.config.getBoolean("use-player-teleports", true);
/*     */ 
/* 150 */     this.deopOnJoin = this.config.getBoolean("security.deop-everyone-on-join", false);
/* 151 */     this.blockInGameOp = this.config.getBoolean("security.block-in-game-op-command", false);
/*     */ 
/* 153 */     this.hostKeys = new HashMap();
/* 154 */     Object hostKeysRaw = this.config.getProperty("host-keys");
/* 155 */     if ((hostKeysRaw == null) || (!(hostKeysRaw instanceof Map)))
/* 156 */       this.config.setProperty("host-keys", new HashMap());
/*     */     else {
/* 158 */       for (Map.Entry entry : ((Map)hostKeysRaw).entrySet()) {
/* 159 */         String key = String.valueOf(entry.getKey());
/* 160 */         String value = String.valueOf(entry.getValue());
/* 161 */         this.hostKeys.put(key.toLowerCase(), value);
/*     */       }
/*     */     }
/*     */ 
/* 165 */     this.useSqlDatabase = this.config.getBoolean("regions.sql.use", false);
/*     */ 
/* 168 */     this.sqlDsn = this.config.getString("regions.sql.dsn", "jdbc:mysql://localhost/worldguard");
/* 169 */     this.sqlUsername = this.config.getString("regions.sql.username", "worldguard");
/* 170 */     this.sqlPassword = this.config.getString("regions.sql.password", "worldguard");
/*     */ 
/* 173 */     for (World world : this.plugin.getServer().getWorlds()) {
/* 174 */       get(world);
/*     */     }
/*     */ 
/* 177 */     this.config.setHeader("#\r\n# WorldGuard's main configuration file\r\n#\r\n# This is the global configuration file. Anything placed into here will\r\n# be applied to all worlds. However, each world has its own configuration\r\n# file to allow you to replace most settings in here for that world only.\r\n#\r\n# About editing this file:\r\n# - DO NOT USE TABS. You MUST use spaces or Bukkit will complain. If\r\n#   you use an editor like Notepad++ (recommended for Windows users), you\r\n#   must configure it to \"replace tabs with spaces.\" In Notepad++, this can\r\n#   be changed in Settings > Preferences > Language Menu.\r\n# - Don't get rid of the indents. They are indented so some entries are\r\n#   in categories (like \"enforce-single-session\" is in the \"protection\"\r\n#   category.\r\n# - If you want to check the format of this file before putting it\r\n#   into WorldGuard, paste it into http://yaml-online-parser.appspot.com/\r\n#   and see if it gives \"ERROR:\".\r\n# - Lines starting with # are comments and so they are ignored.\r\n#\r\n");
/*     */ 
/* 179 */     if (!this.config.save())
/* 180 */       this.plugin.getLogger().severe("Error saving configuration!");
/*     */   }
/*     */ 
/*     */   public void unload()
/*     */   {
/* 188 */     this.worlds.clear();
/*     */   }
/*     */ 
/*     */   public WorldConfiguration get(World world)
/*     */   {
/* 198 */     String worldName = world.getName();
/* 199 */     WorldConfiguration config = (WorldConfiguration)this.worlds.get(worldName);
/* 200 */     WorldConfiguration newConfig = null;
/*     */ 
/* 202 */     while (config == null) {
/* 203 */       if (newConfig == null) {
/* 204 */         newConfig = new WorldConfiguration(this.plugin, worldName, this.config);
/*     */       }
/* 206 */       this.worlds.putIfAbsent(world.getName(), newConfig);
/* 207 */       config = (WorldConfiguration)this.worlds.get(world.getName());
/*     */     }
/*     */ 
/* 210 */     return config;
/*     */   }
/*     */ 
/*     */   public void forgetPlayer(LocalPlayer player)
/*     */   {
/* 220 */     for (Map.Entry entry : this.worlds.entrySet())
/*     */     {
/* 223 */       Blacklist bl = ((WorldConfiguration)entry.getValue()).getBlacklist();
/* 224 */       if (bl != null) {
/* 225 */         bl.forgetPlayer(player);
/*     */       }
/*     */     }
/*     */ 
/* 229 */     this.hasGodMode.remove(player.getName());
/* 230 */     this.hasAmphibious.remove(player.getName());
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void enableGodMode(Player player)
/*     */   {
/* 241 */     this.hasGodMode.add(player.getName());
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void disableGodMode(Player player)
/*     */   {
/* 251 */     this.hasGodMode.remove(player.getName());
/*     */   }
/*     */ 
/*     */   public boolean hasGodMode(Player player)
/*     */   {
/* 261 */     if (this.hasCommandBookGodMode) {
/* 262 */       GodComponent god = (GodComponent)CommandBook.inst().getComponentManager().getComponent(GodComponent.class);
/* 263 */       if (god != null) {
/* 264 */         return god.hasGodMode(player);
/*     */       }
/*     */     }
/* 267 */     return this.hasGodMode.contains(player.getName());
/*     */   }
/*     */ 
/*     */   public void enableAmphibiousMode(Player player)
/*     */   {
/* 276 */     this.hasAmphibious.add(player.getName());
/*     */   }
/*     */ 
/*     */   public void disableAmphibiousMode(Player player)
/*     */   {
/* 285 */     this.hasAmphibious.remove(player.getName());
/*     */   }
/*     */ 
/*     */   public boolean hasAmphibiousMode(Player player)
/*     */   {
/* 295 */     return this.hasAmphibious.contains(player.getName());
/*     */   }
/*     */ 
/*     */   public void updateCommandBookGodMode() {
/*     */     try {
/* 300 */       if (this.plugin.getServer().getPluginManager().isPluginEnabled("CommandBook")) {
/* 301 */         Class.forName("com.sk89q.commandbook.GodComponent");
/* 302 */         this.hasCommandBookGodMode = true;
/* 303 */         return;
/*     */       }
/*     */     } catch (ClassNotFoundException ignore) {  }
/*     */ 
/* 306 */     this.hasCommandBookGodMode = false;
/*     */   }
/*     */ 
/*     */   public boolean hasCommandBookGodMode() {
/* 310 */     return this.hasCommandBookGodMode;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.ConfigurationManager
 * JD-Core Version:    0.6.2
 */