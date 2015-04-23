/*     */ package com.sk89q.worldguard.protection.flags;
/*     */ 
/*     */ import com.sk89q.minecraft.util.commands.CommandException;
/*     */ import com.sk89q.worldedit.LocalWorld;
/*     */ import com.sk89q.worldedit.Location;
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldedit.bukkit.BukkitUtil;
/*     */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class LocationFlag extends Flag<Location>
/*     */ {
/*     */   public LocationFlag(String name, RegionGroup defaultGroup)
/*     */   {
/*  43 */     super(name, defaultGroup);
/*     */   }
/*     */ 
/*     */   public LocationFlag(String name) {
/*  47 */     super(name);
/*     */   }
/*     */ 
/*     */   public Location parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat
/*     */   {
/*  52 */     input = input.trim();
/*     */     Player player;
/*     */     try {
/*  56 */       player = plugin.checkPlayer(sender);
/*     */     } catch (CommandException e) {
/*  58 */       throw new InvalidFlagFormat(e.getMessage());
/*     */     }
/*     */ 
/*  61 */     if ("here".equalsIgnoreCase(input))
/*  62 */       return BukkitUtil.toLocation(player.getLocation());
/*  63 */     if ("none".equalsIgnoreCase(input)) {
/*  64 */       return null;
/*     */     }
/*  66 */     String[] split = input.split(",");
/*  67 */     if (split.length >= 3) {
/*     */       try {
/*  69 */         World world = player.getWorld();
/*  70 */         double x = Double.parseDouble(split[0]);
/*  71 */         double y = Double.parseDouble(split[1]);
/*  72 */         double z = Double.parseDouble(split[2]);
/*  73 */         float yaw = split.length < 4 ? 0.0F : Float.parseFloat(split[3]);
/*  74 */         float pitch = split.length < 5 ? 0.0F : Float.parseFloat(split[4]);
/*     */ 
/*  76 */         return new Location(BukkitUtil.getLocalWorld(world), new Vector(x, y, z), yaw, pitch);
/*     */       }
/*     */       catch (NumberFormatException ignored)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  89 */     throw new InvalidFlagFormat("Expected 'here' or x,y,z.");
/*     */   }
/*     */ 
/*     */   public Location unmarshal(Object o)
/*     */   {
/*  95 */     if ((o instanceof Map)) {
/*  96 */       Map map = (Map)o;
/*     */ 
/*  98 */       Object rawWorld = map.get("world");
/*  99 */       if (rawWorld == null) return null;
/*     */ 
/* 101 */       Object rawX = map.get("x");
/* 102 */       if (rawX == null) return null;
/*     */ 
/* 104 */       Object rawY = map.get("y");
/* 105 */       if (rawY == null) return null;
/*     */ 
/* 107 */       Object rawZ = map.get("z");
/* 108 */       if (rawZ == null) return null;
/*     */ 
/* 110 */       Object rawYaw = map.get("yaw");
/* 111 */       if (rawYaw == null) return null;
/*     */ 
/* 113 */       Object rawPitch = map.get("pitch");
/* 114 */       if (rawPitch == null) return null;
/*     */ 
/* 116 */       World bukkitWorld = Bukkit.getServer().getWorld((String)rawWorld);
/* 117 */       LocalWorld world = BukkitUtil.getLocalWorld(bukkitWorld);
/* 118 */       Vector position = new Vector(toNumber(rawX), toNumber(rawY), toNumber(rawZ));
/* 119 */       float yaw = (float)toNumber(rawYaw);
/* 120 */       float pitch = (float)toNumber(rawPitch);
/*     */ 
/* 122 */       return new Location(world, position, yaw, pitch);
/*     */     }
/*     */ 
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   public Object marshal(Location o)
/*     */   {
/* 130 */     Vector position = o.getPosition();
/* 131 */     Map vec = new HashMap();
/* 132 */     vec.put("world", o.getWorld().getName());
/* 133 */     vec.put("x", Double.valueOf(position.getX()));
/* 134 */     vec.put("y", Double.valueOf(position.getY()));
/* 135 */     vec.put("z", Double.valueOf(position.getZ()));
/* 136 */     vec.put("yaw", Float.valueOf(o.getYaw()));
/* 137 */     vec.put("pitch", Float.valueOf(o.getPitch()));
/* 138 */     return vec;
/*     */   }
/*     */ 
/*     */   private double toNumber(Object o) {
/* 142 */     if ((o instanceof Number)) {
/* 143 */       return ((Number)o).doubleValue();
/*     */     }
/* 145 */     return 0.0D;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.LocationFlag
 * JD-Core Version:    0.6.2
 */