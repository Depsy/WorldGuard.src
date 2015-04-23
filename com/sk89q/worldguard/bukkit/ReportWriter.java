/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldguard.blacklist.Blacklist;
/*     */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.flags.Flag;
/*     */ import com.sk89q.worldguard.protection.flags.StateFlag;
/*     */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import com.sk89q.worldguard.util.LogListBlock;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.World.Environment;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ public class ReportWriter
/*     */ {
/*  48 */   private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd kk:mm Z");
/*     */ 
/*  51 */   private Date date = new Date();
/*  52 */   private StringBuilder output = new StringBuilder();
/*     */ 
/*     */   public ReportWriter(WorldGuardPlugin plugin) {
/*  55 */     appendReportHeader(plugin);
/*  56 */     appendServerInformation(plugin.getServer());
/*  57 */     appendPluginInformation(plugin.getServer().getPluginManager().getPlugins());
/*  58 */     appendWorldInformation(plugin.getServer().getWorlds());
/*  59 */     appendGlobalConfiguration(plugin.getGlobalStateManager());
/*  60 */     appendWorldConfigurations(plugin, plugin.getServer().getWorlds(), plugin.getGlobalRegionManager(), plugin.getGlobalStateManager());
/*     */ 
/*  62 */     appendln("-------------");
/*  63 */     appendln("END OF REPORT");
/*  64 */     appendln();
/*     */   }
/*     */ 
/*     */   protected static String repeat(String str, int n) {
/*  68 */     if (str == null) {
/*  69 */       return null;
/*     */     }
/*     */ 
/*  72 */     StringBuilder sb = new StringBuilder();
/*  73 */     for (int i = 0; i < n; i++) {
/*  74 */       sb.append(str);
/*     */     }
/*     */ 
/*  77 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected void appendln(String text) {
/*  81 */     this.output.append(text);
/*  82 */     this.output.append("\r\n");
/*     */   }
/*     */ 
/*     */   protected void appendln(String text, Object[] args) {
/*  86 */     this.output.append(String.format(text, args));
/*  87 */     this.output.append("\r\n");
/*     */   }
/*     */ 
/*     */   protected void append(LogListBlock log) {
/*  91 */     this.output.append(log.toString());
/*     */   }
/*     */ 
/*     */   protected void appendln() {
/*  95 */     this.output.append("\r\n");
/*     */   }
/*     */ 
/*     */   protected void appendHeader(String text) {
/*  99 */     String rule = repeat("-", text.length());
/* 100 */     this.output.append(rule);
/* 101 */     this.output.append("\r\n");
/* 102 */     appendln(text);
/* 103 */     this.output.append(rule);
/* 104 */     this.output.append("\r\n");
/* 105 */     appendln();
/*     */   }
/*     */ 
/*     */   private void appendReportHeader(WorldGuardPlugin plugin) {
/* 109 */     appendln("WorldGuard Configuration Report");
/* 110 */     appendln(new StringBuilder().append("Generated ").append(dateFmt.format(this.date)).toString());
/* 111 */     appendln();
/* 112 */     appendln(new StringBuilder().append("Version: ").append(plugin.getDescription().getVersion()).toString());
/* 113 */     appendln();
/*     */   }
/*     */ 
/*     */   private void appendGlobalConfiguration(ConfigurationManager config) {
/* 117 */     appendHeader("Global Configuration");
/*     */ 
/* 119 */     LogListBlock log = new LogListBlock();
/* 120 */     LogListBlock configLog = log.putChild("Configuration");
/*     */ 
/* 122 */     Class cls = config.getClass();
/* 123 */     for (Field field : cls.getFields())
/*     */       try {
/* 125 */         if (!field.getName().equalsIgnoreCase("CONFIG_HEADER")) {
/* 126 */           Object val = field.get(config);
/* 127 */           configLog.put(field.getName(), val);
/*     */         }
/*     */       } catch (IllegalArgumentException e) { e.printStackTrace(); }
/*     */       catch (IllegalAccessException ignore)
/*     */       {
/*     */       }
/* 134 */     append(log);
/* 135 */     appendln();
/*     */   }
/*     */ 
/*     */   private void appendServerInformation(Server server) {
/* 139 */     appendHeader("Server Information");
/*     */ 
/* 141 */     LogListBlock log = new LogListBlock();
/*     */ 
/* 143 */     Runtime runtime = Runtime.getRuntime();
/*     */ 
/* 145 */     log.put("Java", "%s %s (%s)", new Object[] { System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("java.vendor.url") });
/*     */ 
/* 149 */     log.put("Operating system", "%s %s (%s)", new Object[] { System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch") });
/*     */ 
/* 153 */     log.put("Available processors", runtime.availableProcessors());
/* 154 */     log.put("Free memory", new StringBuilder().append(runtime.freeMemory() / 1024L / 1024L).append(" MB").toString());
/* 155 */     log.put("Max memory", new StringBuilder().append(runtime.maxMemory() / 1024L / 1024L).append(" MB").toString());
/* 156 */     log.put("Total memory", new StringBuilder().append(runtime.totalMemory() / 1024L / 1024L).append(" MB").toString());
/* 157 */     log.put("Server ID", server.getServerId());
/* 158 */     log.put("Server name", server.getServerName());
/* 159 */     log.put("Implementation", server.getVersion());
/*     */ 
/* 161 */     log.put("Player count", "%d/%d", new Object[] { Integer.valueOf(server.getOnlinePlayers().length), Integer.valueOf(server.getMaxPlayers()) });
/*     */ 
/* 164 */     append(log);
/* 165 */     appendln();
/*     */   }
/*     */ 
/*     */   private void appendPluginInformation(Plugin[] plugins) {
/* 169 */     appendHeader(new StringBuilder().append("Plugins (").append(plugins.length).append(")").toString());
/*     */ 
/* 171 */     LogListBlock log = new LogListBlock();
/*     */ 
/* 173 */     for (Plugin plugin : plugins) {
/* 174 */       log.put(plugin.getDescription().getName(), plugin.getDescription().getVersion());
/*     */     }
/*     */ 
/* 177 */     append(log);
/* 178 */     appendln();
/*     */   }
/*     */ 
/*     */   private void appendWorldInformation(List<World> worlds)
/*     */   {
/* 196 */     appendHeader("Worlds");
/*     */ 
/* 198 */     LogListBlock log = new LogListBlock();
/*     */ 
/* 200 */     int i = 0;
/* 201 */     for (World world : worlds) {
/* 202 */       int loadedChunkCount = world.getLoadedChunks().length;
/*     */ 
/* 204 */       LogListBlock worldLog = log.putChild(new StringBuilder().append(world.getName()).append(" (").append(i).append(")").toString());
/* 205 */       LogListBlock infoLog = worldLog.putChild("Information");
/* 206 */       LogListBlock entitiesLog = worldLog.putChild("Entities");
/*     */ 
/* 208 */       infoLog.put("Seed", world.getSeed());
/* 209 */       infoLog.put("Environment", world.getEnvironment().toString());
/* 210 */       infoLog.put("Player count", world.getPlayers().size());
/* 211 */       infoLog.put("Entity count", world.getEntities().size());
/* 212 */       infoLog.put("Loaded chunk count", loadedChunkCount);
/* 213 */       infoLog.put("Spawn location", world.getSpawnLocation());
/* 214 */       infoLog.put("Raw time", world.getFullTime());
/*     */ 
/* 216 */       Map entityCounts = new HashMap();
/*     */ 
/* 220 */       for (Entity entity : world.getEntities()) {
/* 221 */         Class cls = entity.getClass();
/*     */ 
/* 223 */         if (entityCounts.containsKey(cls))
/* 224 */           entityCounts.put(cls, Integer.valueOf(((Integer)entityCounts.get(cls)).intValue() + 1));
/*     */         else {
/* 226 */           entityCounts.put(cls, Integer.valueOf(1));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 232 */       for (Map.Entry entry : entityCounts.entrySet()) {
/* 233 */         entitiesLog.put(((Class)entry.getKey()).getSimpleName(), "%d [%f/chunk]", new Object[] { entry.getValue(), Float.valueOf((float)(((Integer)entry.getValue()).intValue() / loadedChunkCount)) });
/*     */       }
/*     */ 
/* 239 */       i++;
/*     */     }
/*     */ 
/* 242 */     append(log);
/* 243 */     appendln();
/*     */   }
/*     */ 
/*     */   private void appendWorldConfigurations(WorldGuardPlugin plugin, List<World> worlds, GlobalRegionManager regionMgr, ConfigurationManager mgr)
/*     */   {
/* 248 */     appendHeader("World Configurations");
/*     */ 
/* 250 */     LogListBlock log = new LogListBlock();
/*     */ 
/* 252 */     int i = 0;
/* 253 */     for (World world : worlds) {
/* 254 */       LogListBlock worldLog = log.putChild(new StringBuilder().append(world.getName()).append(" (").append(i).append(")").toString());
/* 255 */       LogListBlock infoLog = worldLog.putChild("Information");
/* 256 */       LogListBlock configLog = worldLog.putChild("Configuration");
/* 257 */       LogListBlock blacklistLog = worldLog.putChild("Blacklist");
/* 258 */       LogListBlock regionsLog = worldLog.putChild("Region manager");
/*     */ 
/* 260 */       infoLog.put("Configuration file", new File(plugin.getDataFolder(), new StringBuilder().append("worlds/").append(world.getName()).append("/config.yml").toString()).getAbsoluteFile());
/*     */ 
/* 263 */       infoLog.put("Blacklist file", new File(plugin.getDataFolder(), new StringBuilder().append("worlds/").append(world.getName()).append("/blacklist.txt").toString()).getAbsoluteFile());
/*     */ 
/* 265 */       infoLog.put("Regions file", new File(plugin.getDataFolder(), new StringBuilder().append("worlds/").append(world.getName()).append("/regions.yml").toString()).getAbsoluteFile());
/*     */ 
/* 268 */       WorldConfiguration config = mgr.get(world);
/*     */ 
/* 270 */       Class cls = config.getClass();
/* 271 */       for (Field field : cls.getFields())
/*     */         try {
/* 273 */           Object val = field.get(config);
/* 274 */           configLog.put(field.getName(), String.valueOf(val));
/*     */         } catch (IllegalArgumentException e) {
/* 276 */           e.printStackTrace();
/*     */         }
/*     */         catch (IllegalAccessException ignore)
/*     */         {
/*     */         }
/* 281 */       if (config.getBlacklist() == null) {
/* 282 */         blacklistLog.put("State", "DISABLED");
/*     */       } else {
/* 284 */         blacklistLog.put("State", "Enabled");
/* 285 */         blacklistLog.put("Number of items", config.getBlacklist().getItemCount());
/*     */ 
/* 287 */         blacklistLog.put("Is whitelist", config.getBlacklist().isWhitelist());
/*     */       }
/*     */ 
/* 291 */       RegionManager worldRegions = regionMgr.get(world);
/*     */ 
/* 293 */       regionsLog.put("Type", worldRegions.getClass().getCanonicalName());
/* 294 */       regionsLog.put("Number of regions", worldRegions.getRegions().size());
/* 295 */       LogListBlock globalRegionLog = regionsLog.putChild("Global region");
/*     */ 
/* 297 */       ProtectedRegion globalRegion = worldRegions.getRegion("__global__");
/* 298 */       if (globalRegion == null)
/* 299 */         globalRegionLog.put("Status", "UNDEFINED");
/*     */       else {
/* 301 */         for (Flag flag : DefaultFlag.getFlags()) {
/* 302 */           if ((flag instanceof StateFlag)) {
/* 303 */             globalRegionLog.put(flag.getName(), globalRegion.getFlag(flag));
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 310 */     append(log);
/* 311 */     appendln();
/*     */   }
/*     */ 
/*     */   public void write(File file) throws IOException {
/* 315 */     FileWriter writer = null;
/*     */     try
/*     */     {
/* 319 */       writer = new FileWriter(file);
/* 320 */       BufferedWriter out = new BufferedWriter(writer);
/* 321 */       out.write(this.output.toString());
/* 322 */       out.close();
/*     */     } finally {
/* 324 */       if (writer != null)
/*     */         try {
/* 326 */           writer.close();
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 335 */     return this.output.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.ReportWriter
 * JD-Core Version:    0.6.2
 */