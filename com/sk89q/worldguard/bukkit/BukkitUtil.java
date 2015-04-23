/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldedit.BlockVector;
/*     */ import com.sk89q.worldedit.blocks.BlockType;
/*     */ import java.util.List;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.ExperienceOrb;
/*     */ import org.bukkit.entity.FallingBlock;
/*     */ import org.bukkit.entity.Item;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.TNTPrimed;
/*     */ import org.bukkit.entity.Tameable;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ public class BukkitUtil
/*     */ {
/*     */   public static BlockVector toVector(Block block)
/*     */   {
/*  56 */     return new BlockVector(block.getX(), block.getY(), block.getZ());
/*     */   }
/*     */ 
/*     */   public static com.sk89q.worldedit.Vector toVector(Location loc)
/*     */   {
/*  66 */     return new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
/*     */   }
/*     */ 
/*     */   public static com.sk89q.worldedit.Vector toVector(org.bukkit.util.Vector vector)
/*     */   {
/*  76 */     return new com.sk89q.worldedit.Vector(vector.getX(), vector.getY(), vector.getZ());
/*     */   }
/*     */ 
/*     */   public static Location toLocation(World world, com.sk89q.worldedit.Vector vec)
/*     */   {
/*  87 */     return new Location(world, vec.getX(), vec.getY(), vec.getZ());
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static Player matchSinglePlayer(Server server, String name)
/*     */   {
/* 100 */     List players = server.matchPlayer(name);
/* 101 */     if (players.size() == 0) {
/* 102 */       return null;
/*     */     }
/* 104 */     return (Player)players.get(0);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void dropSign(Block block)
/*     */   {
/* 115 */     block.setTypeId(0);
/* 116 */     block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(323, 1));
/*     */   }
/*     */ 
/*     */   public static void setBlockToWater(World world, int ox, int oy, int oz)
/*     */   {
/* 130 */     Block block = world.getBlockAt(ox, oy, oz);
/* 131 */     int id = block.getTypeId();
/* 132 */     if (id == 0)
/* 133 */       block.setTypeId(8);
/*     */   }
/*     */ 
/*     */   public static boolean isBlockWater(World world, int ox, int oy, int oz)
/*     */   {
/* 147 */     Block block = world.getBlockAt(ox, oy, oz);
/* 148 */     int id = block.getTypeId();
/* 149 */     return (id == 8) || (id == 9);
/*     */   }
/*     */ 
/*     */   public static boolean isWaterPotion(ItemStack item)
/*     */   {
/* 159 */     return (item.getDurability() & 0x3F) == 0;
/*     */   }
/*     */ 
/*     */   public static int getPotionEffectBits(ItemStack item)
/*     */   {
/* 170 */     return item.getDurability() & 0x3F;
/*     */   }
/*     */ 
/*     */   public static void findFreePosition(Player player)
/*     */   {
/* 182 */     Location loc = player.getLocation();
/* 183 */     int x = loc.getBlockX();
/* 184 */     int y = Math.max(0, loc.getBlockY());
/* 185 */     int origY = y;
/* 186 */     int z = loc.getBlockZ();
/* 187 */     World world = player.getWorld();
/*     */ 
/* 189 */     byte free = 0;
/*     */ 
/* 191 */     while (y <= world.getMaxHeight() + 1) {
/* 192 */       if (BlockType.canPassThrough(world.getBlockTypeIdAt(x, y, z)))
/* 193 */         free = (byte)(free + 1);
/*     */       else {
/* 195 */         free = 0;
/*     */       }
/*     */ 
/* 198 */       if (free == 2) {
/* 199 */         if ((y - 1 != origY) || (y == 1)) {
/* 200 */           loc.setX(x + 0.5D);
/* 201 */           loc.setY(y);
/* 202 */           loc.setZ(z + 0.5D);
/* 203 */           if ((y <= 2) && (world.getBlockAt(x, 0, z).getTypeId() == 0)) {
/* 204 */             world.getBlockAt(x, 0, z).setTypeId(20);
/* 205 */             loc.setY(2.0D);
/*     */           }
/* 207 */           player.setFallDistance(0.0F);
/* 208 */           player.teleport(loc);
/*     */         }
/* 210 */         return;
/*     */       }
/*     */ 
/* 213 */       y++;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String replaceColorMacros(String str)
/*     */   {
/* 229 */     str = str.replace("&r", ChatColor.RED.toString());
/* 230 */     str = str.replace("&R", ChatColor.DARK_RED.toString());
/*     */ 
/* 232 */     str = str.replace("&y", ChatColor.YELLOW.toString());
/* 233 */     str = str.replace("&Y", ChatColor.GOLD.toString());
/*     */ 
/* 235 */     str = str.replace("&g", ChatColor.GREEN.toString());
/* 236 */     str = str.replace("&G", ChatColor.DARK_GREEN.toString());
/*     */ 
/* 238 */     str = str.replace("&c", ChatColor.AQUA.toString());
/* 239 */     str = str.replace("&C", ChatColor.DARK_AQUA.toString());
/*     */ 
/* 241 */     str = str.replace("&b", ChatColor.BLUE.toString());
/* 242 */     str = str.replace("&B", ChatColor.DARK_BLUE.toString());
/*     */ 
/* 244 */     str = str.replace("&p", ChatColor.LIGHT_PURPLE.toString());
/* 245 */     str = str.replace("&P", ChatColor.DARK_PURPLE.toString());
/*     */ 
/* 247 */     str = str.replace("&0", ChatColor.BLACK.toString());
/* 248 */     str = str.replace("&1", ChatColor.DARK_GRAY.toString());
/* 249 */     str = str.replace("&2", ChatColor.GRAY.toString());
/* 250 */     str = str.replace("&w", ChatColor.WHITE.toString());
/*     */ 
/* 252 */     str = str.replace("&k", ChatColor.MAGIC.toString());
/* 253 */     str = str.replace("&l", ChatColor.BOLD.toString());
/* 254 */     str = str.replace("&m", ChatColor.STRIKETHROUGH.toString());
/* 255 */     str = str.replace("&n", ChatColor.UNDERLINE.toString());
/* 256 */     str = str.replace("&o", ChatColor.ITALIC.toString());
/*     */ 
/* 258 */     str = str.replace("&x", ChatColor.RESET.toString());
/*     */ 
/* 260 */     return str;
/*     */   }
/*     */ 
/*     */   public static boolean isIntensiveEntity(Entity entity)
/*     */   {
/* 270 */     return ((entity instanceof Item)) || ((entity instanceof TNTPrimed)) || ((entity instanceof ExperienceOrb)) || ((entity instanceof FallingBlock)) || (((entity instanceof LivingEntity)) && (!(entity instanceof Tameable)) && (!(entity instanceof Player)));
/*     */   }
/*     */ 
/*     */   public static <T extends Enum<T>> T tryEnum(Class<T> enumType, String[] values)
/*     */   {
/* 288 */     for (String val : values)
/*     */       try {
/* 290 */         return Enum.valueOf(enumType, val);
/*     */       }
/*     */       catch (IllegalArgumentException e)
/*     */       {
/*     */       }
/* 295 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.BukkitUtil
 * JD-Core Version:    0.6.2
 */