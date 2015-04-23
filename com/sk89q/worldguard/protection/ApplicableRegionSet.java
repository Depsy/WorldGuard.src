/*     */ package com.sk89q.worldguard.protection;
/*     */ 
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.flags.Flag;
/*     */ import com.sk89q.worldguard.protection.flags.RegionGroup;
/*     */ import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
/*     */ import com.sk89q.worldguard.protection.flags.StateFlag;
/*     */ import com.sk89q.worldguard.protection.flags.StateFlag.State;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ApplicableRegionSet
/*     */   implements Iterable<ProtectedRegion>
/*     */ {
/*     */   private Collection<ProtectedRegion> applicable;
/*     */   private ProtectedRegion globalRegion;
/*     */ 
/*     */   public ApplicableRegionSet(Collection<ProtectedRegion> applicable, ProtectedRegion globalRegion)
/*     */   {
/*  51 */     this.applicable = applicable;
/*  52 */     this.globalRegion = globalRegion;
/*     */   }
/*     */ 
/*     */   public boolean canBuild(LocalPlayer player)
/*     */   {
/*  62 */     return internalGetState(DefaultFlag.BUILD, player, null);
/*     */   }
/*     */ 
/*     */   public boolean canConstruct(LocalPlayer player) {
/*  66 */     RegionGroup flag = (RegionGroup)getFlag(DefaultFlag.CONSTRUCT, player);
/*  67 */     return RegionGroupFlag.isMember(this, flag, player);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean canUse(LocalPlayer player)
/*     */   {
/*  79 */     return (!allows(DefaultFlag.USE, player)) && (!canBuild(player));
/*     */   }
/*     */ 
/*     */   public boolean allows(StateFlag flag)
/*     */   {
/*  91 */     if (flag == DefaultFlag.BUILD) {
/*  92 */       throw new IllegalArgumentException("Can't use build flag with allows()");
/*     */     }
/*  94 */     return internalGetState(flag, null, null);
/*     */   }
/*     */ 
/*     */   public boolean allows(StateFlag flag, LocalPlayer player)
/*     */   {
/* 106 */     if (flag == DefaultFlag.BUILD) {
/* 107 */       throw new IllegalArgumentException("Can't use build flag with allows()");
/*     */     }
/* 109 */     return internalGetState(flag, null, player);
/*     */   }
/*     */ 
/*     */   public boolean isOwnerOfAll(LocalPlayer player)
/*     */   {
/* 119 */     for (ProtectedRegion region : this.applicable) {
/* 120 */       if (!region.isOwner(player)) {
/* 121 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 125 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isMemberOfAll(LocalPlayer player)
/*     */   {
/* 136 */     for (ProtectedRegion region : this.applicable) {
/* 137 */       if (!region.isMember(player)) {
/* 138 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 142 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean internalGetState(StateFlag flag, LocalPlayer player, LocalPlayer groupPlayer)
/*     */   {
/* 155 */     boolean found = false;
/* 156 */     boolean hasFlagDefined = false;
/* 157 */     boolean allowed = false;
/* 158 */     boolean def = flag.getDefault();
/*     */ 
/* 161 */     if (this.globalRegion != null) {
/* 162 */       StateFlag.State globalState = (StateFlag.State)this.globalRegion.getFlag(flag);
/*     */ 
/* 165 */       if (globalState != null)
/*     */       {
/* 167 */         if ((player != null) && (this.globalRegion.hasMembersOrOwners()))
/* 168 */           def = (this.globalRegion.isMember(player)) && (globalState == StateFlag.State.ALLOW);
/*     */         else {
/* 170 */           def = globalState == StateFlag.State.ALLOW;
/*     */         }
/*     */ 
/*     */       }
/* 174 */       else if ((player != null) && (this.globalRegion.hasMembersOrOwners())) {
/* 175 */         def = this.globalRegion.isMember(player);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 185 */     if (player == null) {
/* 186 */       allowed = def;
/*     */     }
/*     */ 
/* 189 */     int lastPriority = -2147483648;
/*     */ 
/* 206 */     Set needsClear = new HashSet();
/* 207 */     Set hasCleared = new HashSet();
/*     */ 
/* 209 */     for (ProtectedRegion region : this.applicable)
/*     */     {
/* 211 */       if ((hasFlagDefined) && (region.getPriority() < lastPriority))
/*     */       {
/*     */         break;
/*     */       }
/* 215 */       lastPriority = region.getPriority();
/*     */ 
/* 218 */       if ((player == null) || (region.getFlag(DefaultFlag.PASSTHROUGH) != StateFlag.State.ALLOW))
/*     */       {
/* 224 */         if ((groupPlayer != null) && (flag.getRegionGroupFlag() != null)) {
/* 225 */           RegionGroup group = (RegionGroup)region.getFlag(flag.getRegionGroupFlag());
/* 226 */           if (group == null) {
/* 227 */             group = flag.getRegionGroupFlag().getDefault();
/*     */           }
/*     */ 
/* 229 */           if (!RegionGroupFlag.isMember(region, group, groupPlayer));
/*     */         }
/*     */         else
/*     */         {
/* 234 */           StateFlag.State v = (StateFlag.State)region.getFlag(flag);
/*     */ 
/* 237 */           if (v == StateFlag.State.DENY) {
/* 238 */             return false;
/*     */           }
/*     */ 
/* 243 */           if (v == StateFlag.State.ALLOW) {
/* 244 */             allowed = true;
/* 245 */             found = true;
/* 246 */             hasFlagDefined = true;
/*     */           }
/*     */           else
/*     */           {
/* 253 */             if (player != null) {
/* 254 */               hasFlagDefined = true;
/*     */ 
/* 256 */               if (!hasCleared.contains(region))
/*     */               {
/* 259 */                 if (!region.isMember(player)) {
/* 260 */                   needsClear.add(region);
/*     */                 }
/*     */                 else {
/* 263 */                   clearParents(needsClear, hasCleared, region);
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/* 268 */             found = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 271 */     return (allowed) || ((player != null) && (needsClear.size() == 0)) ? true : !found ? def : false;
/*     */   }
/*     */ 
/*     */   private void clearParents(Set<ProtectedRegion> needsClear, Set<ProtectedRegion> hasCleared, ProtectedRegion region)
/*     */   {
/* 284 */     ProtectedRegion parent = region.getParent();
/*     */ 
/* 286 */     while (parent != null) {
/* 287 */       if (!needsClear.remove(parent)) {
/* 288 */         hasCleared.add(parent);
/*     */       }
/*     */ 
/* 291 */       parent = parent.getParent();
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T extends Flag<V>, V> V getFlag(T flag)
/*     */   {
/* 301 */     return getFlag(flag, null);
/*     */   }
/*     */ 
/*     */   public <T extends Flag<V>, V> V getFlag(T flag, LocalPlayer groupPlayer)
/*     */   {
/* 320 */     int lastPriority = 0;
/* 321 */     boolean found = false;
/*     */ 
/* 323 */     Map needsClear = new HashMap();
/* 324 */     Set hasCleared = new HashSet();
/*     */ 
/* 326 */     for (ProtectedRegion region : this.applicable)
/*     */     {
/* 328 */       if ((found) && (region.getPriority() < lastPriority))
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 333 */       if ((groupPlayer != null) && (flag.getRegionGroupFlag() != null)) {
/* 334 */         RegionGroup group = (RegionGroup)region.getFlag(flag.getRegionGroupFlag());
/* 335 */         if (group == null) {
/* 336 */           group = flag.getRegionGroupFlag().getDefault();
/*     */         }
/*     */ 
/* 338 */         if (!RegionGroupFlag.isMember(region, group, groupPlayer));
/*     */       }
/*     */       else
/*     */       {
/* 343 */         if (!hasCleared.contains(region))
/*     */         {
/* 345 */           if (region.getFlag(flag) != null) {
/* 346 */             clearParents(needsClear, hasCleared, region);
/*     */ 
/* 348 */             needsClear.put(region, region.getFlag(flag));
/*     */ 
/* 350 */             found = true;
/*     */           }
/*     */         }
/* 353 */         lastPriority = region.getPriority();
/*     */       }
/*     */     }
/* 356 */     if (!needsClear.isEmpty()) {
/* 357 */       return needsClear.values().iterator().next();
/*     */     }
/* 359 */     if (this.globalRegion != null) {
/* 360 */       Object gFlag = this.globalRegion.getFlag(flag);
/* 361 */       if (gFlag != null) return gFlag;
/*     */     }
/* 363 */     return null;
/*     */   }
/*     */ 
/*     */   private void clearParents(Map<ProtectedRegion, ?> needsClear, Set<ProtectedRegion> hasCleared, ProtectedRegion region)
/*     */   {
/* 376 */     ProtectedRegion parent = region.getParent();
/*     */ 
/* 378 */     while (parent != null) {
/* 379 */       if (needsClear.remove(parent) == null) {
/* 380 */         hasCleared.add(parent);
/*     */       }
/*     */ 
/* 383 */       parent = parent.getParent();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 393 */     return this.applicable.size();
/*     */   }
/*     */ 
/*     */   public Iterator<ProtectedRegion> iterator()
/*     */   {
/* 400 */     return this.applicable.iterator();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.ApplicableRegionSet
 * JD-Core Version:    0.6.2
 */