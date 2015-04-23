/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import com.sk89q.commandbook.InfoComponent.PlayerWhoisEvent;
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*    */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*    */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*    */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ 
/*    */ public class WorldGuardCommandBookListener
/*    */   implements Listener
/*    */ {
/*    */   private final WorldGuardPlugin plugin;
/*    */ 
/*    */   public WorldGuardCommandBookListener(WorldGuardPlugin plugin)
/*    */   {
/* 36 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onPlayerWhois(InfoComponent.PlayerWhoisEvent event) {
/* 41 */     if ((event.getPlayer() instanceof Player)) {
/* 42 */       Player player = (Player)event.getPlayer();
/* 43 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/* 44 */       if (this.plugin.getGlobalStateManager().get(player.getWorld()).useRegions) {
/* 45 */         ApplicableRegionSet regions = this.plugin.getGlobalRegionManager().get(player.getWorld()).getApplicableRegions(player.getLocation());
/*    */ 
/* 49 */         StringBuilder regionStr = new StringBuilder();
/* 50 */         boolean first = true;
/*    */ 
/* 52 */         for (ProtectedRegion region : regions) {
/* 53 */           if (!first) {
/* 54 */             regionStr.append(", ");
/*    */           }
/*    */ 
/* 57 */           if (region.isOwner(localPlayer))
/* 58 */             regionStr.append("+");
/* 59 */           else if (region.isMemberOnly(localPlayer)) {
/* 60 */             regionStr.append("-");
/*    */           }
/*    */ 
/* 63 */           regionStr.append(region.getId());
/*    */ 
/* 65 */           first = false;
/*    */         }
/*    */ 
/* 68 */         if (regions.size() > 0) {
/* 69 */           event.addWhoisInformation("Current Regions", regionStr);
/*    */         }
/* 71 */         event.addWhoisInformation("Can build", Boolean.valueOf(regions.canBuild(localPlayer)));
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardCommandBookListener
 * JD-Core Version:    0.6.2
 */