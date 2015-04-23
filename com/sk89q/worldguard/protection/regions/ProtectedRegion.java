/*     */ package com.sk89q.worldguard.protection.regions;
/*     */ 
/*     */ import com.sk89q.worldedit.BlockVector;
/*     */ import com.sk89q.worldedit.BlockVector2D;
/*     */ import com.sk89q.worldedit.Vector;
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import com.sk89q.worldguard.protection.UnsupportedIntersectionException;
/*     */ import com.sk89q.worldguard.protection.flags.Flag;
/*     */ import java.awt.geom.Line2D;
/*     */ import java.awt.geom.Line2D.Double;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public abstract class ProtectedRegion
/*     */   implements Comparable<ProtectedRegion>
/*     */ {
/*     */   protected BlockVector min;
/*     */   protected BlockVector max;
/*  46 */   private static final Pattern idPattern = Pattern.compile("^[A-Za-z0-9_,'\\-\\+/]{1,}$");
/*     */   private String id;
/*  56 */   private int priority = 0;
/*     */   private ProtectedRegion parent;
/*  66 */   private DefaultDomain owners = new DefaultDomain();
/*     */ 
/*  71 */   private DefaultDomain members = new DefaultDomain();
/*     */ 
/*  76 */   private Map<Flag<?>, Object> flags = new HashMap();
/*     */ 
/*     */   public ProtectedRegion(String id)
/*     */   {
/*  84 */     this.id = id;
/*     */   }
/*     */ 
/*     */   protected void setMinMaxPoints(List<Vector> points)
/*     */   {
/*  93 */     int minX = ((Vector)points.get(0)).getBlockX();
/*  94 */     int minY = ((Vector)points.get(0)).getBlockY();
/*  95 */     int minZ = ((Vector)points.get(0)).getBlockZ();
/*  96 */     int maxX = minX;
/*  97 */     int maxY = minY;
/*  98 */     int maxZ = minZ;
/*     */ 
/* 100 */     for (Vector v : points) {
/* 101 */       int x = v.getBlockX();
/* 102 */       int y = v.getBlockY();
/* 103 */       int z = v.getBlockZ();
/*     */ 
/* 105 */       if (x < minX) minX = x;
/* 106 */       if (y < minY) minY = y;
/* 107 */       if (z < minZ) minZ = z;
/*     */ 
/* 109 */       if (x > maxX) maxX = x;
/* 110 */       if (y > maxY) maxY = y;
/* 111 */       if (z > maxZ) maxZ = z;
/*     */     }
/*     */ 
/* 114 */     this.min = new BlockVector(minX, minY, minZ);
/* 115 */     this.max = new BlockVector(maxX, maxY, maxZ);
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 124 */     return this.id;
/*     */   }
/*     */ 
/*     */   public BlockVector getMinimumPoint()
/*     */   {
/* 133 */     return this.min;
/*     */   }
/*     */ 
/*     */   public BlockVector getMaximumPoint()
/*     */   {
/* 142 */     return this.max;
/*     */   }
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 149 */     return this.priority;
/*     */   }
/*     */ 
/*     */   public void setPriority(int priority)
/*     */   {
/* 156 */     this.priority = priority;
/*     */   }
/*     */ 
/*     */   public ProtectedRegion getParent()
/*     */   {
/* 163 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public void setParent(ProtectedRegion parent)
/*     */     throws ProtectedRegion.CircularInheritanceException
/*     */   {
/* 174 */     if (parent == null) {
/* 175 */       this.parent = null;
/* 176 */       return;
/*     */     }
/*     */ 
/* 179 */     if (parent == this) {
/* 180 */       throw new CircularInheritanceException();
/*     */     }
/*     */ 
/* 183 */     ProtectedRegion p = parent.getParent();
/* 184 */     while (p != null) {
/* 185 */       if (p == this) {
/* 186 */         throw new CircularInheritanceException();
/*     */       }
/* 188 */       p = p.getParent();
/*     */     }
/*     */ 
/* 191 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public DefaultDomain getOwners()
/*     */   {
/* 200 */     return this.owners;
/*     */   }
/*     */ 
/*     */   public void setOwners(DefaultDomain owners)
/*     */   {
/* 207 */     this.owners = owners;
/*     */   }
/*     */ 
/*     */   public DefaultDomain getMembers()
/*     */   {
/* 214 */     return this.members;
/*     */   }
/*     */ 
/*     */   public void setMembers(DefaultDomain members)
/*     */   {
/* 221 */     this.members = members;
/*     */   }
/*     */ 
/*     */   public boolean hasMembersOrOwners()
/*     */   {
/* 230 */     return (this.owners.size() > 0) || (this.members.size() > 0);
/*     */   }
/*     */ 
/*     */   public boolean isOwner(LocalPlayer player)
/*     */   {
/* 240 */     if (this.owners.contains(player)) {
/* 241 */       return true;
/*     */     }
/*     */ 
/* 244 */     ProtectedRegion curParent = getParent();
/* 245 */     while (curParent != null) {
/* 246 */       if (curParent.getOwners().contains(player)) {
/* 247 */         return true;
/*     */       }
/*     */ 
/* 250 */       curParent = curParent.getParent();
/*     */     }
/*     */ 
/* 253 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isOwner(String playerName)
/*     */   {
/* 263 */     if (this.owners.contains(playerName)) {
/* 264 */       return true;
/*     */     }
/*     */ 
/* 267 */     ProtectedRegion curParent = getParent();
/* 268 */     while (curParent != null) {
/* 269 */       if (curParent.getOwners().contains(playerName)) {
/* 270 */         return true;
/*     */       }
/*     */ 
/* 273 */       curParent = curParent.getParent();
/*     */     }
/*     */ 
/* 276 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isMember(LocalPlayer player)
/*     */   {
/* 287 */     if (isOwner(player)) {
/* 288 */       return true;
/*     */     }
/*     */ 
/* 291 */     if (this.members.contains(player)) {
/* 292 */       return true;
/*     */     }
/*     */ 
/* 295 */     ProtectedRegion curParent = getParent();
/* 296 */     while (curParent != null) {
/* 297 */       if (curParent.getMembers().contains(player)) {
/* 298 */         return true;
/*     */       }
/*     */ 
/* 301 */       curParent = curParent.getParent();
/*     */     }
/*     */ 
/* 304 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isMember(String playerName)
/*     */   {
/* 315 */     if (isOwner(playerName)) {
/* 316 */       return true;
/*     */     }
/*     */ 
/* 319 */     if (this.members.contains(playerName)) {
/* 320 */       return true;
/*     */     }
/*     */ 
/* 323 */     ProtectedRegion curParent = getParent();
/* 324 */     while (curParent != null) {
/* 325 */       if (curParent.getMembers().contains(playerName)) {
/* 326 */         return true;
/*     */       }
/*     */ 
/* 329 */       curParent = curParent.getParent();
/*     */     }
/*     */ 
/* 332 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isMemberOnly(LocalPlayer player)
/*     */   {
/* 343 */     if (this.members.contains(player)) {
/* 344 */       return true;
/*     */     }
/*     */ 
/* 347 */     ProtectedRegion curParent = getParent();
/* 348 */     while (curParent != null) {
/* 349 */       if (curParent.getMembers().contains(player)) {
/* 350 */         return true;
/*     */       }
/*     */ 
/* 353 */       curParent = curParent.getParent();
/*     */     }
/*     */ 
/* 356 */     return false;
/*     */   }
/*     */ 
/*     */   public <T extends Flag<V>, V> V getFlag(T flag)
/*     */   {
/* 369 */     Object obj = this.flags.get(flag);
/*     */     Object val;
/* 371 */     if (obj != null)
/* 372 */       val = obj;
/*     */     else
/* 374 */       return null;
/*     */     Object val;
/* 376 */     return val;
/*     */   }
/*     */ 
/*     */   public <T extends Flag<V>, V> void setFlag(T flag, V val)
/*     */   {
/* 388 */     if (val == null)
/* 389 */       this.flags.remove(flag);
/*     */     else
/* 391 */       this.flags.put(flag, val);
/*     */   }
/*     */ 
/*     */   public Map<Flag<?>, Object> getFlags()
/*     */   {
/* 401 */     return this.flags;
/*     */   }
/*     */ 
/*     */   public void setFlags(Map<Flag<?>, Object> flags)
/*     */   {
/* 410 */     this.flags = flags;
/*     */   }
/*     */ 
/*     */   public abstract List<BlockVector2D> getPoints();
/*     */ 
/*     */   public abstract int volume();
/*     */ 
/*     */   public abstract boolean contains(Vector paramVector);
/*     */ 
/*     */   public boolean contains(BlockVector2D pt)
/*     */   {
/* 442 */     return contains(new Vector(pt.getBlockX(), this.min.getBlockY(), pt.getBlockZ()));
/*     */   }
/*     */ 
/*     */   public boolean contains(int x, int y, int z)
/*     */   {
/* 454 */     return contains(new Vector(x, y, z));
/*     */   }
/*     */ 
/*     */   public boolean containsAny(List<BlockVector2D> pts)
/*     */   {
/* 464 */     for (BlockVector2D pt : pts) {
/* 465 */       if (contains(pt)) {
/* 466 */         return true;
/*     */       }
/*     */     }
/* 469 */     return false;
/*     */   }
/*     */ 
/*     */   public int compareTo(ProtectedRegion other)
/*     */   {
/* 481 */     if (this.priority > other.priority)
/* 482 */       return -1;
/* 483 */     if (this.priority < other.priority) {
/* 484 */       return 1;
/*     */     }
/*     */ 
/* 487 */     return this.id.compareTo(other.id);
/*     */   }
/*     */ 
/*     */   public abstract String getTypeName();
/*     */ 
/*     */   public abstract List<ProtectedRegion> getIntersectingRegions(List<ProtectedRegion> paramList)
/*     */     throws UnsupportedIntersectionException;
/*     */ 
/*     */   protected boolean intersectsBoundingBox(ProtectedRegion region)
/*     */   {
/* 516 */     BlockVector rMaxPoint = region.getMaximumPoint();
/* 517 */     BlockVector min = getMinimumPoint();
/*     */ 
/* 519 */     if (rMaxPoint.getBlockX() < min.getBlockX()) return false;
/* 520 */     if (rMaxPoint.getBlockY() < min.getBlockY()) return false;
/* 521 */     if (rMaxPoint.getBlockZ() < min.getBlockZ()) return false;
/*     */ 
/* 523 */     BlockVector rMinPoint = region.getMinimumPoint();
/* 524 */     BlockVector max = getMaximumPoint();
/*     */ 
/* 526 */     if (rMinPoint.getBlockX() > max.getBlockX()) return false;
/* 527 */     if (rMinPoint.getBlockY() > max.getBlockY()) return false;
/* 528 */     if (rMinPoint.getBlockZ() > max.getBlockZ()) return false;
/*     */ 
/* 530 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean intersectsEdges(ProtectedRegion region)
/*     */   {
/* 540 */     List pts1 = getPoints();
/* 541 */     List pts2 = region.getPoints();
/* 542 */     BlockVector2D lastPt1 = (BlockVector2D)pts1.get(pts1.size() - 1);
/* 543 */     BlockVector2D lastPt2 = (BlockVector2D)pts2.get(pts2.size() - 1);
/* 544 */     for (BlockVector2D aPts1 : pts1) {
/* 545 */       for (BlockVector2D aPts2 : pts2)
/*     */       {
/* 547 */         Line2D line1 = new Line2D.Double(lastPt1.getBlockX(), lastPt1.getBlockZ(), aPts1.getBlockX(), aPts1.getBlockZ());
/*     */ 
/* 553 */         if (line1.intersectsLine(lastPt2.getBlockX(), lastPt2.getBlockZ(), aPts2.getBlockX(), aPts2.getBlockZ()))
/*     */         {
/* 558 */           return true;
/*     */         }
/* 560 */         lastPt2 = aPts2;
/*     */       }
/* 562 */       lastPt1 = aPts1;
/*     */     }
/* 564 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isValidId(String id)
/*     */   {
/* 575 */     return idPattern.matcher(id).matches();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 580 */     return this.id.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 588 */     if (!(obj instanceof ProtectedRegion)) {
/* 589 */       return false;
/*     */     }
/*     */ 
/* 592 */     ProtectedRegion other = (ProtectedRegion)obj;
/* 593 */     return other.getId().equals(getId());
/*     */   }
/*     */ 
/*     */   public static class CircularInheritanceException extends Exception
/*     */   {
/*     */     private static final long serialVersionUID = 7479613488496776022L;
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.regions.ProtectedRegion
 * JD-Core Version:    0.6.2
 */