/*     */ package com.sk89q.worldguard.protection;
/*     */ 
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.bukkit.BukkitUtil;
/*     */ import com.sk89q.worldguard.bukkit.ConfigurationManager;
/*     */ import com.sk89q.worldguard.bukkit.WorldConfiguration;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import com.sk89q.worldguard.protection.databases.MySQLDatabase;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabase;
/*     */ import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
/*     */ import com.sk89q.worldguard.protection.databases.YAMLDatabase;
/*     */ import com.sk89q.worldguard.protection.flags.StateFlag;
/*     */ import com.sk89q.worldguard.protection.managers.PRTreeRegionManager;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class GlobalRegionManager
/*     */ {
/*     */   private WorldGuardPlugin plugin;
/*     */   private ConfigurationManager config;
/*     */   private ConcurrentHashMap<String, RegionManager> managers;
/*     */   private HashMap<String, Long> lastModified;
/*     */ 
/*     */   public GlobalRegionManager(WorldGuardPlugin plugin)
/*     */   {
/*  83 */     this.plugin = plugin;
/*  84 */     this.config = plugin.getGlobalStateManager();
/*  85 */     this.managers = new ConcurrentHashMap();
/*  86 */     this.lastModified = new HashMap();
/*     */   }
/*     */ 
/*     */   public void unload()
/*     */   {
/*  93 */     this.managers.clear();
/*  94 */     this.lastModified.clear();
/*     */   }
/*     */ 
/*     */   protected File getPath(String name)
/*     */   {
/* 104 */     return new File(this.plugin.getDataFolder(), "worlds" + File.separator + name + File.separator + "regions.yml");
/*     */   }
/*     */ 
/*     */   public void unload(String name)
/*     */   {
/* 114 */     RegionManager manager = (RegionManager)this.managers.remove(name);
/*     */ 
/* 116 */     if (manager != null)
/* 117 */       this.lastModified.remove(name);
/*     */   }
/*     */ 
/*     */   public void unloadAll()
/*     */   {
/* 125 */     this.managers.clear();
/* 126 */     this.lastModified.clear();
/*     */   }
/*     */ 
/*     */   public RegionManager load(World world) {
/* 130 */     RegionManager manager = create(world);
/* 131 */     this.managers.put(world.getName(), manager);
/* 132 */     return manager;
/*     */   }
/*     */ 
/*     */   public RegionManager create(World world)
/*     */   {
/* 142 */     String name = world.getName();
/* 143 */     boolean sql = this.config.useSqlDatabase;
/*     */ 
/* 145 */     File file = null;
/*     */     try
/*     */     {
/*     */       ProtectionDatabase database;
/* 148 */       if (!sql) {
/* 149 */         file = getPath(name);
/* 150 */         ProtectionDatabase database = new YAMLDatabase(file, this.plugin.getLogger());
/*     */ 
/* 153 */         this.lastModified.put(name, Long.valueOf(file.lastModified()));
/*     */       } else {
/* 155 */         database = new MySQLDatabase(this.config, name, this.plugin.getLogger());
/*     */       }
/*     */ 
/* 159 */       RegionManager manager = new PRTreeRegionManager(database);
/* 160 */       manager.load();
/*     */ 
/* 162 */       if (this.plugin.getGlobalStateManager().get(world).summaryOnStart) {
/* 163 */         this.plugin.getLogger().info(manager.getRegions().size() + " regions loaded for '" + name + "'");
/*     */       }
/*     */ 
/* 167 */       return manager;
/*     */     } catch (ProtectionDatabaseException e) {
/* 169 */       String logStr = "Failed to load regions from ";
/* 170 */       if (sql)
/* 171 */         logStr = logStr + "SQL Database <" + this.config.sqlDsn + "> ";
/*     */       else {
/* 173 */         logStr = logStr + "file \"" + file + "\" ";
/*     */       }
/*     */ 
/* 176 */       this.plugin.getLogger().log(Level.SEVERE, logStr + " : " + e.getMessage());
/* 177 */       e.printStackTrace();
/*     */     } catch (FileNotFoundException e) {
/* 179 */       this.plugin.getLogger().log(Level.SEVERE, "Error loading regions for world \"" + name + "\": " + e.toString() + "\n\t" + e.getMessage());
/*     */ 
/* 181 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 185 */     return null;
/*     */   }
/*     */ 
/*     */   public void preload()
/*     */   {
/* 193 */     for (World world : this.plugin.getServer().getWorlds())
/* 194 */       load(world);
/*     */   }
/*     */ 
/*     */   public void reloadChanged()
/*     */   {
/* 203 */     if (this.config.useSqlDatabase) return;
/*     */ 
/* 205 */     for (String name : this.managers.keySet()) {
/* 206 */       File file = getPath(name);
/*     */ 
/* 208 */       Long oldDate = (Long)this.lastModified.get(name);
/*     */ 
/* 210 */       if (oldDate == null) {
/* 211 */         oldDate = Long.valueOf(0L);
/*     */       }
/*     */       try
/*     */       {
/* 215 */         if (file.lastModified() > oldDate.longValue()) {
/* 216 */           World world = this.plugin.getServer().getWorld(name);
/*     */ 
/* 218 */           if (world != null)
/* 219 */             load(world);
/*     */         }
/*     */       }
/*     */       catch (Exception ignore)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public RegionManager get(World world)
/*     */   {
/* 234 */     RegionManager manager = (RegionManager)this.managers.get(world.getName());
/* 235 */     RegionManager newManager = null;
/*     */ 
/* 237 */     while (manager == null) {
/* 238 */       if (newManager == null) {
/* 239 */         newManager = create(world);
/*     */       }
/* 241 */       this.managers.putIfAbsent(world.getName(), newManager);
/* 242 */       manager = (RegionManager)this.managers.get(world.getName());
/*     */     }
/*     */ 
/* 245 */     return manager;
/*     */   }
/*     */ 
/*     */   public boolean hasBypass(LocalPlayer player, World world)
/*     */   {
/* 256 */     return player.hasPermission("worldguard.region.bypass." + world.getName());
/*     */   }
/*     */ 
/*     */   public boolean hasBypass(Player player, World world)
/*     */   {
/* 268 */     return this.plugin.hasPermission(player, "worldguard.region.bypass." + world.getName());
/*     */   }
/*     */ 
/*     */   public boolean canBuild(Player player, Block block)
/*     */   {
/* 280 */     return canBuild(player, block.getLocation());
/*     */   }
/*     */ 
/*     */   public boolean canBuild(Player player, Location loc)
/*     */   {
/* 291 */     World world = loc.getWorld();
/* 292 */     WorldConfiguration worldConfig = this.config.get(world);
/*     */ 
/* 294 */     if (!worldConfig.useRegions) {
/* 295 */       return true;
/*     */     }
/*     */ 
/* 298 */     LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*     */ 
/* 300 */     if (!hasBypass(player, world)) {
/* 301 */       RegionManager mgr = get(world);
/*     */ 
/* 303 */       if (!mgr.getApplicableRegions(BukkitUtil.toVector(loc)).canBuild(localPlayer))
/*     */       {
/* 305 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 309 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean canConstruct(Player player, Block block) {
/* 313 */     return canConstruct(player, block.getLocation());
/*     */   }
/*     */ 
/*     */   public boolean canConstruct(Player player, Location loc) {
/* 317 */     World world = loc.getWorld();
/* 318 */     WorldConfiguration worldConfig = this.config.get(world);
/*     */ 
/* 320 */     if (!worldConfig.useRegions) {
/* 321 */       return true;
/*     */     }
/*     */ 
/* 324 */     LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*     */ 
/* 326 */     if (!hasBypass(player, world)) {
/* 327 */       RegionManager mgr = get(world);
/*     */ 
/* 329 */       ApplicableRegionSet applicableRegions = mgr.getApplicableRegions(BukkitUtil.toVector(loc));
/* 330 */       if (!applicableRegions.canBuild(localPlayer)) {
/* 331 */         return false;
/*     */       }
/* 333 */       if (!applicableRegions.canConstruct(localPlayer)) {
/* 334 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 338 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean allows(StateFlag flag, Location loc)
/*     */   {
/* 350 */     return allows(flag, loc, null);
/*     */   }
/*     */ 
/*     */   public boolean allows(StateFlag flag, Location loc, LocalPlayer player)
/*     */   {
/* 362 */     World world = loc.getWorld();
/* 363 */     WorldConfiguration worldConfig = this.config.get(world);
/*     */ 
/* 365 */     if (!worldConfig.useRegions) {
/* 366 */       return true;
/*     */     }
/*     */ 
/* 369 */     RegionManager mgr = get(world);
/* 370 */     return mgr.getApplicableRegions(BukkitUtil.toVector(loc)).allows(flag, player);
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.GlobalRegionManager
 * JD-Core Version:    0.6.2
 */