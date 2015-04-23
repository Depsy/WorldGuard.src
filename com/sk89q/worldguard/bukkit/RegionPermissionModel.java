/*     */ package com.sk89q.worldguard.bukkit;
/*     */ 
/*     */ import com.sk89q.worldguard.LocalPlayer;
/*     */ import com.sk89q.worldguard.protection.flags.Flag;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class RegionPermissionModel extends AbstractPermissionModel
/*     */ {
/*     */   public RegionPermissionModel(WorldGuardPlugin plugin, CommandSender sender)
/*     */   {
/*  34 */     super(plugin, sender);
/*     */   }
/*     */ 
/*     */   public boolean mayForceLoadRegions() {
/*  38 */     return hasPluginPermission("region.load");
/*     */   }
/*     */ 
/*     */   public boolean mayForceSaveRegions() {
/*  42 */     return hasPluginPermission("region.save");
/*     */   }
/*     */ 
/*     */   public boolean mayMigrateRegionStore() {
/*  46 */     return hasPluginPermission("region.migratedb");
/*     */   }
/*     */ 
/*     */   public boolean mayDefine() {
/*  50 */     return hasPluginPermission("region.define");
/*     */   }
/*     */ 
/*     */   public boolean mayRedefine(ProtectedRegion region) {
/*  54 */     return hasPatternPermission("redefine", region);
/*     */   }
/*     */ 
/*     */   public boolean mayClaim() {
/*  58 */     return hasPluginPermission("region.claim");
/*     */   }
/*     */ 
/*     */   public boolean mayClaimRegionsUnbounded() {
/*  62 */     return hasPluginPermission("region.unlimited");
/*     */   }
/*     */ 
/*     */   public boolean mayDelete(ProtectedRegion region) {
/*  66 */     return hasPatternPermission("remove", region);
/*     */   }
/*     */ 
/*     */   public boolean maySetPriority(ProtectedRegion region) {
/*  70 */     return hasPatternPermission("setpriority", region);
/*     */   }
/*     */ 
/*     */   public boolean maySetParent(ProtectedRegion child, ProtectedRegion parent) {
/*  74 */     return (hasPatternPermission("setparent", child)) && ((parent == null) || (hasPatternPermission("setparent", parent)));
/*     */   }
/*     */ 
/*     */   public boolean maySelect(ProtectedRegion region)
/*     */   {
/*  80 */     return hasPatternPermission("select", region);
/*     */   }
/*     */ 
/*     */   public boolean mayLookup(ProtectedRegion region) {
/*  84 */     return hasPatternPermission("info", region);
/*     */   }
/*     */ 
/*     */   public boolean mayTeleportTo(ProtectedRegion region) {
/*  88 */     return hasPatternPermission("teleport", region);
/*     */   }
/*     */ 
/*     */   public boolean mayList() {
/*  92 */     return hasPluginPermission("region.list");
/*     */   }
/*     */ 
/*     */   public boolean mayList(String targetPlayer) {
/*  96 */     if (targetPlayer == null) {
/*  97 */       return mayList();
/*     */     }
/*     */ 
/* 100 */     if (targetPlayer.equalsIgnoreCase(getSender().getName())) {
/* 101 */       return hasPluginPermission("region.list.own");
/*     */     }
/* 103 */     return mayList();
/*     */   }
/*     */ 
/*     */   public boolean maySetFlag(ProtectedRegion region)
/*     */   {
/* 108 */     return hasPatternPermission("flag.regions", region);
/*     */   }
/*     */ 
/*     */   public boolean maySetFlag(ProtectedRegion region, Flag<?> flag)
/*     */   {
/* 113 */     return hasPatternPermission("flag.flags." + flag.getName().toLowerCase(), region);
/*     */   }
/*     */ 
/*     */   private boolean hasPatternPermission(String perm, ProtectedRegion region)
/*     */   {
/* 125 */     if (!(getSender() instanceof Player)) {
/* 126 */       return true;
/*     */     }
/*     */ 
/* 129 */     LocalPlayer localPlayer = getPlugin().wrapPlayer((Player)getSender());
/* 130 */     String idLower = region.getId().toLowerCase();
/*     */ 
/* 133 */     if (region.isOwner(localPlayer)) {
/* 134 */       return (hasPluginPermission("region." + perm + ".own." + idLower)) || (hasPluginPermission("region." + perm + ".member." + idLower));
/*     */     }
/* 136 */     if (region.isMember(localPlayer)) {
/* 137 */       return hasPluginPermission("region." + perm + ".member." + idLower);
/*     */     }
/* 139 */     String effectivePerm = "region." + perm + "." + idLower;
/*     */ 
/* 142 */     return hasPluginPermission(effectivePerm);
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.RegionPermissionModel
 * JD-Core Version:    0.6.2
 */