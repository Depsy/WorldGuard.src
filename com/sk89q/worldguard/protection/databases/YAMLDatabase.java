/*     */ package com.sk89q.worldguard.protection.databases;
/*     */ 
/*     */ import com.sk89q.util.yaml.YAMLFormat;
/*     */ import com.sk89q.util.yaml.YAMLNode;
/*     */ import com.sk89q.util.yaml.YAMLProcessor;
/*     */ import com.sk89q.worldedit.BlockVector;
/*     */ import com.sk89q.worldedit.BlockVector2D;
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.flags.Flag;
/*     */ import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
/*     */ import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class YAMLDatabase extends AbstractProtectionDatabase
/*     */ {
/*     */   private YAMLProcessor config;
/*     */   private Map<String, ProtectedRegion> regions;
/*     */   private final Logger logger;
/*     */ 
/*     */   public YAMLDatabase(File file, Logger logger)
/*     */     throws ProtectionDatabaseException, FileNotFoundException
/*     */   {
/*  55 */     this.logger = logger;
/*  56 */     if (!file.exists()) {
/*     */       try {
/*  58 */         file.createNewFile();
/*     */       } catch (IOException e) {
/*  60 */         throw new FileNotFoundException(file.getAbsolutePath());
/*     */       }
/*     */     }
/*  63 */     this.config = new YAMLProcessor(file, false, YAMLFormat.COMPACT);
/*     */   }
/*     */ 
/*     */   public void load() throws ProtectionDatabaseException {
/*     */     try {
/*  68 */       this.config.load();
/*     */     } catch (IOException e) {
/*  70 */       throw new ProtectionDatabaseException(e);
/*     */     }
/*     */ 
/*  73 */     Map regionData = this.config.getNodes("regions");
/*     */ 
/*  76 */     if (regionData == null) {
/*  77 */       this.regions = new HashMap();
/*  78 */       return;
/*     */     }
/*     */ 
/*  81 */     Map regions = new HashMap();
/*     */ 
/*  83 */     Map parentSets = new LinkedHashMap();
/*     */ 
/*  86 */     for (Map.Entry entry : regionData.entrySet()) {
/*  87 */       String id = ((String)entry.getKey()).toLowerCase().replace(".", "");
/*  88 */       YAMLNode node = (YAMLNode)entry.getValue();
/*     */ 
/*  90 */       String type = node.getString("type");
/*     */       try
/*     */       {
/*  94 */         if (type == null) {
/*  95 */           this.logger.warning("Undefined region type for region '" + id + '"');
/*     */         }
/*     */         else
/*     */         {
/*     */           ProtectedRegion region;
/*  97 */           if (type.equals("cuboid")) {
/*  98 */             Vector pt1 = (Vector)checkNonNull(node.getVector("min"));
/*  99 */             Vector pt2 = (Vector)checkNonNull(node.getVector("max"));
/* 100 */             BlockVector min = Vector.getMinimum(pt1, pt2).toBlockVector();
/* 101 */             BlockVector max = Vector.getMaximum(pt1, pt2).toBlockVector();
/* 102 */             region = new ProtectedCuboidRegion(id, min, max);
/*     */           }
/*     */           else
/*     */           {
/*     */             ProtectedRegion region;
/* 103 */             if (type.equals("poly2d")) {
/* 104 */               Integer minY = (Integer)checkNonNull(node.getInt("min-y"));
/* 105 */               Integer maxY = (Integer)checkNonNull(node.getInt("max-y"));
/* 106 */               List points = node.getBlockVector2dList("points", null);
/* 107 */               region = new ProtectedPolygonalRegion(id, points, minY.intValue(), maxY.intValue());
/*     */             }
/*     */             else
/*     */             {
/*     */               ProtectedRegion region;
/* 108 */               if (type.equals("global")) {
/* 109 */                 region = new GlobalProtectedRegion(id);
/*     */               } else {
/* 111 */                 this.logger.warning("Unknown region type for region '" + id + '"');
/* 112 */                 continue;
/*     */               }
/*     */             }
/*     */           }
/*     */           ProtectedRegion region;
/* 115 */           Integer priority = (Integer)checkNonNull(node.getInt("priority"));
/* 116 */           region.setPriority(priority.intValue());
/* 117 */           setFlags(region, node.getNode("flags"));
/* 118 */           region.setOwners(parseDomain(node.getNode("owners")));
/* 119 */           region.setMembers(parseDomain(node.getNode("members")));
/* 120 */           regions.put(id, region);
/*     */ 
/* 122 */           String parentId = node.getString("parent");
/* 123 */           if (parentId != null)
/* 124 */             parentSets.put(region, parentId);
/*     */         }
/*     */       } catch (NullPointerException e) {
/* 127 */         this.logger.warning("Missing data for region '" + id + '"');
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 132 */     for (Map.Entry entry : parentSets.entrySet()) {
/* 133 */       ProtectedRegion parent = (ProtectedRegion)regions.get(entry.getValue());
/* 134 */       if (parent != null) {
/*     */         try {
/* 136 */           ((ProtectedRegion)entry.getKey()).setParent(parent);
/*     */         } catch (ProtectedRegion.CircularInheritanceException e) {
/* 138 */           this.logger.warning("Circular inheritance detect with '" + (String)entry.getValue() + "' detected as a parent");
/*     */         }
/*     */       }
/*     */       else {
/* 142 */         this.logger.warning("Unknown region parent: " + (String)entry.getValue());
/*     */       }
/*     */     }
/*     */ 
/* 146 */     this.regions = regions;
/*     */   }
/*     */ 
/*     */   private <V> V checkNonNull(V val) throws NullPointerException {
/* 150 */     if (val == null) {
/* 151 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 154 */     return val;
/*     */   }
/*     */ 
/*     */   private void setFlags(ProtectedRegion region, YAMLNode flagsData) {
/* 158 */     if (flagsData == null) {
/* 159 */       return;
/*     */     }
/*     */ 
/* 163 */     for (Flag flag : DefaultFlag.getFlags()) {
/* 164 */       Object o = flagsData.getProperty(flag.getName());
/* 165 */       if (o != null) {
/* 166 */         setFlag(region, flag, o);
/*     */       }
/*     */ 
/* 169 */       if (flag.getRegionGroupFlag() != null) {
/* 170 */         Object o2 = flagsData.getProperty(flag.getRegionGroupFlag().getName());
/* 171 */         if (o2 != null)
/* 172 */           setFlag(region, flag.getRegionGroupFlag(), o2);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T> void setFlag(ProtectedRegion region, Flag<T> flag, Object rawValue)
/*     */   {
/* 179 */     Object val = flag.unmarshal(rawValue);
/* 180 */     if (val == null) {
/* 181 */       this.logger.warning("Failed to parse flag '" + flag.getName() + "' with value '" + rawValue.toString() + "'");
/*     */ 
/* 183 */       return;
/*     */     }
/* 185 */     region.setFlag(flag, val);
/*     */   }
/*     */ 
/*     */   private DefaultDomain parseDomain(YAMLNode node) {
/* 189 */     if (node == null) {
/* 190 */       return new DefaultDomain();
/*     */     }
/*     */ 
/* 193 */     DefaultDomain domain = new DefaultDomain();
/*     */ 
/* 195 */     for (String name : node.getStringList("players", null)) {
/* 196 */       domain.addPlayer(name);
/*     */     }
/*     */ 
/* 199 */     for (String name : node.getStringList("groups", null)) {
/* 200 */       domain.addGroup(name);
/*     */     }
/*     */ 
/* 203 */     return domain;
/*     */   }
/*     */ 
/*     */   public void save() throws ProtectionDatabaseException {
/* 207 */     this.config.clear();
/*     */ 
/* 209 */     for (Map.Entry entry : this.regions.entrySet()) {
/* 210 */       ProtectedRegion region = (ProtectedRegion)entry.getValue();
/* 211 */       YAMLNode node = this.config.addNode("regions." + (String)entry.getKey());
/*     */ 
/* 213 */       if ((region instanceof ProtectedCuboidRegion)) {
/* 214 */         ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion)region;
/* 215 */         node.setProperty("type", "cuboid");
/* 216 */         node.setProperty("min", cuboid.getMinimumPoint());
/* 217 */         node.setProperty("max", cuboid.getMaximumPoint());
/* 218 */       } else if ((region instanceof ProtectedPolygonalRegion)) {
/* 219 */         ProtectedPolygonalRegion poly = (ProtectedPolygonalRegion)region;
/* 220 */         node.setProperty("type", "poly2d");
/* 221 */         node.setProperty("min-y", Integer.valueOf(poly.getMinimumPoint().getBlockY()));
/* 222 */         node.setProperty("max-y", Integer.valueOf(poly.getMaximumPoint().getBlockY()));
/*     */ 
/* 224 */         List points = new ArrayList();
/* 225 */         for (BlockVector2D point : poly.getPoints()) {
/* 226 */           Map data = new HashMap();
/* 227 */           data.put("x", Integer.valueOf(point.getBlockX()));
/* 228 */           data.put("z", Integer.valueOf(point.getBlockZ()));
/* 229 */           points.add(data);
/*     */         }
/*     */ 
/* 232 */         node.setProperty("points", points);
/* 233 */       } else if ((region instanceof GlobalProtectedRegion)) {
/* 234 */         node.setProperty("type", "global");
/*     */       } else {
/* 236 */         node.setProperty("type", region.getClass().getCanonicalName());
/*     */       }
/*     */ 
/* 239 */       node.setProperty("priority", Integer.valueOf(region.getPriority()));
/* 240 */       node.setProperty("flags", getFlagData(region));
/* 241 */       node.setProperty("owners", getDomainData(region.getOwners()));
/* 242 */       node.setProperty("members", getDomainData(region.getMembers()));
/* 243 */       ProtectedRegion parent = region.getParent();
/* 244 */       if (parent != null) {
/* 245 */         node.setProperty("parent", parent.getId());
/*     */       }
/*     */     }
/*     */ 
/* 249 */     this.config.setHeader("#\r\n# WorldGuard regions file\r\n#\r\n# WARNING: THIS FILE IS AUTOMATICALLY GENERATED. If you modify this file by\r\n# hand, be aware that A SINGLE MISTYPED CHARACTER CAN CORRUPT THE FILE. If\r\n# WorldGuard is unable to parse the file, your regions will FAIL TO LOAD and\r\n# the contents of this file will reset. Please use a YAML validator such as\r\n# http://yaml-online-parser.appspot.com (for smaller files).\r\n#\r\n# REMEMBER TO KEEP PERIODICAL BACKUPS.\r\n#");
/*     */ 
/* 260 */     this.config.save();
/*     */   }
/*     */ 
/*     */   private Map<String, Object> getFlagData(ProtectedRegion region) {
/* 264 */     Map flagData = new HashMap();
/*     */ 
/* 266 */     for (Map.Entry entry : region.getFlags().entrySet()) {
/* 267 */       Flag flag = (Flag)entry.getKey();
/* 268 */       addMarshalledFlag(flagData, flag, entry.getValue());
/*     */     }
/*     */ 
/* 271 */     return flagData;
/*     */   }
/*     */ 
/*     */   private <V> void addMarshalledFlag(Map<String, Object> flagData, Flag<V> flag, Object val)
/*     */   {
/* 277 */     if (val == null) {
/* 278 */       return;
/*     */     }
/* 280 */     flagData.put(flag.getName(), flag.marshal(val));
/*     */   }
/*     */ 
/*     */   private Map<String, Object> getDomainData(DefaultDomain domain) {
/* 284 */     Map domainData = new HashMap();
/*     */ 
/* 286 */     setDomainData(domainData, "players", domain.getPlayers());
/* 287 */     setDomainData(domainData, "groups", domain.getGroups());
/*     */ 
/* 289 */     return domainData;
/*     */   }
/*     */ 
/*     */   private void setDomainData(Map<String, Object> domainData, String key, Set<String> domain)
/*     */   {
/* 294 */     if (domain.size() == 0) {
/* 295 */       return;
/*     */     }
/*     */ 
/* 298 */     List list = new ArrayList();
/*     */ 
/* 300 */     for (String str : domain) {
/* 301 */       list.add(str);
/*     */     }
/*     */ 
/* 304 */     domainData.put(key, list);
/*     */   }
/*     */ 
/*     */   public Map<String, ProtectedRegion> getRegions() {
/* 308 */     return this.regions;
/*     */   }
/*     */ 
/*     */   public void setRegions(Map<String, ProtectedRegion> regions) {
/* 312 */     this.regions = regions;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.YAMLDatabase
 * JD-Core Version:    0.6.2
 */