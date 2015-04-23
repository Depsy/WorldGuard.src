/*     */ package com.sk89q.worldguard.protection.databases;
/*     */ 
/*     */ import au.com.bytecode.opencsv.CSVReader;
/*     */ import com.sk89q.worldedit.BlockVector;
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.flags.StateFlag;
/*     */ import com.sk89q.worldguard.protection.flags.StateFlag.State;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
/*     */ import com.sk89q.worldguard.util.ArrayReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class CSVDatabase extends AbstractProtectionDatabase
/*     */ {
/*  52 */   private static final Map<String, StateFlag> legacyFlagCodes = new HashMap();
/*     */   private final Logger logger;
/*     */   private final File file;
/*     */   private Map<String, ProtectedRegion> regions;
/*     */ 
/*     */   public CSVDatabase(File file, Logger logger)
/*     */   {
/*  85 */     this.file = file;
/*  86 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   public void save()
/*     */     throws ProtectionDatabaseException
/*     */   {
/*  93 */     throw new UnsupportedOperationException("CSV format is no longer implemented");
/*     */   }
/*     */ 
/*     */   public void load() throws ProtectionDatabaseException {
/*  97 */     Map regions = new HashMap();
/*     */ 
/*  99 */     Map parentSets = new LinkedHashMap();
/*     */ 
/* 102 */     CSVReader reader = null;
/*     */     try {
/* 104 */       reader = new CSVReader(new FileReader(this.file));
/*     */       String[] line;
/* 108 */       while ((line = reader.readNext()) != null)
/* 109 */         if (line.length < 2) {
/* 110 */           this.logger.warning("Invalid region definition: " + line);
/*     */         }
/*     */         else
/*     */         {
/* 114 */           String id = line[0].toLowerCase().replace(".", "");
/* 115 */           String type = line[1];
/* 116 */           ArrayReader entries = new ArrayReader(line);
/*     */ 
/* 118 */           if (type.equalsIgnoreCase("cuboid")) {
/* 119 */             if (line.length < 8) {
/* 120 */               this.logger.warning("Invalid region definition: " + line);
/*     */             }
/*     */             else
/*     */             {
/* 124 */               Vector pt1 = new Vector(Integer.parseInt(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4]));
/*     */ 
/* 128 */               Vector pt2 = new Vector(Integer.parseInt(line[5]), Integer.parseInt(line[6]), Integer.parseInt(line[7]));
/*     */ 
/* 133 */               BlockVector min = Vector.getMinimum(pt1, pt2).toBlockVector();
/* 134 */               BlockVector max = Vector.getMaximum(pt1, pt2).toBlockVector();
/*     */ 
/* 136 */               int priority = entries.get(8) == null ? 0 : Integer.parseInt((String)entries.get(8));
/* 137 */               String ownersData = (String)entries.get(9);
/* 138 */               String flagsData = (String)entries.get(10);
/*     */ 
/* 141 */               ProtectedRegion region = new ProtectedCuboidRegion(id, min, max);
/* 142 */               region.setPriority(priority);
/* 143 */               parseFlags(region, flagsData);
/* 144 */               region.setOwners(parseDomains(ownersData));
/* 145 */               regions.put(id, region);
/*     */             } } else if (type.equalsIgnoreCase("cuboid.2")) {
/* 147 */             Vector pt1 = new Vector(Integer.parseInt(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4]));
/*     */ 
/* 151 */             Vector pt2 = new Vector(Integer.parseInt(line[5]), Integer.parseInt(line[6]), Integer.parseInt(line[7]));
/*     */ 
/* 156 */             BlockVector min = Vector.getMinimum(pt1, pt2).toBlockVector();
/* 157 */             BlockVector max = Vector.getMaximum(pt1, pt2).toBlockVector();
/*     */ 
/* 159 */             int priority = entries.get(8) == null ? 0 : Integer.parseInt((String)entries.get(8));
/* 160 */             String parentId = (String)entries.get(9);
/* 161 */             String ownersData = (String)entries.get(10);
/* 162 */             String membersData = (String)entries.get(11);
/* 163 */             String flagsData = (String)entries.get(12);
/*     */ 
/* 167 */             ProtectedRegion region = new ProtectedCuboidRegion(id, min, max);
/* 168 */             region.setPriority(priority);
/* 169 */             parseFlags(region, flagsData);
/* 170 */             region.setOwners(parseDomains(ownersData));
/* 171 */             region.setMembers(parseDomains(membersData));
/* 172 */             regions.put(id, region);
/*     */ 
/* 175 */             if (parentId.length() > 0)
/* 176 */               parentSets.put(region, parentId);
/*     */           }
/*     */         }
/*     */     }
/*     */     catch (IOException e) {
/* 181 */       throw new ProtectionDatabaseException(e);
/*     */     } finally {
/*     */       try {
/* 184 */         reader.close();
/*     */       }
/*     */       catch (IOException ignored) {
/*     */       }
/*     */     }
/* 189 */     for (Map.Entry entry : parentSets.entrySet()) {
/* 190 */       ProtectedRegion parent = (ProtectedRegion)regions.get(entry.getValue());
/* 191 */       if (parent != null) {
/*     */         try {
/* 193 */           ((ProtectedRegion)entry.getKey()).setParent(parent);
/*     */         } catch (ProtectedRegion.CircularInheritanceException e) {
/* 195 */           this.logger.warning("Circular inheritance detect with '" + (String)entry.getValue() + "' detected as a parent");
/*     */         }
/*     */       }
/*     */       else {
/* 199 */         this.logger.warning("Unknown region parent: " + (String)entry.getValue());
/*     */       }
/*     */     }
/*     */ 
/* 203 */     this.regions = regions;
/*     */   }
/*     */ 
/*     */   private DefaultDomain parseDomains(String data)
/*     */   {
/* 213 */     if (data == null) {
/* 214 */       return new DefaultDomain();
/*     */     }
/*     */ 
/* 217 */     DefaultDomain domain = new DefaultDomain();
/* 218 */     Pattern pattern = Pattern.compile("^([A-Za-z]):(.*)$");
/*     */ 
/* 220 */     String[] parts = data.split(",");
/*     */ 
/* 222 */     for (String part : parts) {
/* 223 */       if (part.trim().length() != 0)
/*     */       {
/* 227 */         Matcher matcher = pattern.matcher(part);
/*     */ 
/* 229 */         if (!matcher.matches()) {
/* 230 */           this.logger.warning("Invalid owner specification: " + part);
/*     */         }
/*     */         else
/*     */         {
/* 234 */           String type = matcher.group(1);
/* 235 */           String id = matcher.group(2);
/*     */ 
/* 237 */           if (type.equals("u"))
/* 238 */             domain.addPlayer(id);
/* 239 */           else if (type.equals("g"))
/* 240 */             domain.addGroup(id);
/*     */           else
/* 242 */             this.logger.warning("Unknown owner specification: " + type);
/*     */         }
/*     */       }
/*     */     }
/* 246 */     return domain;
/*     */   }
/*     */ 
/*     */   private void parseFlags(ProtectedRegion region, String data)
/*     */   {
/* 255 */     if (data == null) {
/* 256 */       return;
/*     */     }
/*     */ 
/* 259 */     StateFlag.State curState = StateFlag.State.ALLOW;
/*     */ 
/* 261 */     for (int i = 0; i < data.length(); i++) {
/* 262 */       char k = data.charAt(i);
/* 263 */       if (k == '+') {
/* 264 */         curState = StateFlag.State.ALLOW;
/* 265 */       } else if (k == '-') {
/* 266 */         curState = StateFlag.State.DENY;
/*     */       }
/* 269 */       else if (k == '_') {
/* 270 */         if (i == data.length() - 1) {
/* 271 */           this.logger.warning("_ read ahead fail");
/* 272 */           break;
/*     */         }
/* 274 */         String flagStr = "_" + data.charAt(i + 1);
/* 275 */         i++;
/*     */ 
/* 277 */         this.logger.warning("_? custom flags are no longer supported");
/*     */       }
/*     */       else {
/* 280 */         String flagStr = String.valueOf(k);
/*     */ 
/* 283 */         StateFlag flag = (StateFlag)legacyFlagCodes.get(flagStr);
/* 284 */         if (flag != null)
/* 285 */           region.setFlag(flag, curState);
/*     */         else
/* 287 */           this.logger.warning("Legacy flag '" + flagStr + "' is unsupported");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String nullEmptyString(String str)
/*     */   {
/* 341 */     if (str == null)
/* 342 */       return null;
/* 343 */     if (str.length() == 0) {
/* 344 */       return null;
/*     */     }
/* 346 */     return str;
/*     */   }
/*     */ 
/*     */   public Map<String, ProtectedRegion> getRegions()
/*     */   {
/* 351 */     return this.regions;
/*     */   }
/*     */ 
/*     */   public void setRegions(Map<String, ProtectedRegion> regions) {
/* 355 */     this.regions = regions;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  54 */     legacyFlagCodes.put("z", DefaultFlag.PASSTHROUGH);
/*  55 */     legacyFlagCodes.put("b", DefaultFlag.BUILD);
/*  56 */     legacyFlagCodes.put("p", DefaultFlag.PVP);
/*  57 */     legacyFlagCodes.put("m", DefaultFlag.MOB_DAMAGE);
/*  58 */     legacyFlagCodes.put("c", DefaultFlag.CREEPER_EXPLOSION);
/*  59 */     legacyFlagCodes.put("t", DefaultFlag.TNT);
/*  60 */     legacyFlagCodes.put("l", DefaultFlag.LIGHTER);
/*  61 */     legacyFlagCodes.put("f", DefaultFlag.FIRE_SPREAD);
/*  62 */     legacyFlagCodes.put("F", DefaultFlag.LAVA_FIRE);
/*  63 */     legacyFlagCodes.put("C", DefaultFlag.CHEST_ACCESS);
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.CSVDatabase
 * JD-Core Version:    0.6.2
 */