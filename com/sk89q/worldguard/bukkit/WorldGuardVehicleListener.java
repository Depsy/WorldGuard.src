/*    */ package com.sk89q.worldguard.bukkit;
/*    */ 
/*    */ import com.sk89q.worldguard.LocalPlayer;
/*    */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*    */ import com.sk89q.worldguard.protection.GlobalRegionManager;
/*    */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*    */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.entity.Entity;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.entity.Vehicle;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.vehicle.VehicleDestroyEvent;
/*    */ import org.bukkit.event.vehicle.VehicleMoveEvent;
/*    */ import org.bukkit.plugin.PluginManager;
/*    */ 
/*    */ public class WorldGuardVehicleListener
/*    */   implements Listener
/*    */ {
/*    */   private WorldGuardPlugin plugin;
/*    */ 
/*    */   public WorldGuardVehicleListener(WorldGuardPlugin plugin)
/*    */   {
/* 35 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public void registerEvents()
/*    */   {
/* 42 */     this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onVehicleDestroy(VehicleDestroyEvent event) {
/* 47 */     Vehicle vehicle = event.getVehicle();
/* 48 */     Entity destroyer = event.getAttacker();
/*    */ 
/* 50 */     if (!(destroyer instanceof Player)) return;
/* 51 */     Player player = (Player)destroyer;
/* 52 */     World world = vehicle.getWorld();
/*    */ 
/* 54 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 55 */     WorldConfiguration wcfg = cfg.get(world);
/*    */ 
/* 57 */     if (wcfg.useRegions) {
/* 58 */       com.sk89q.worldedit.Vector pt = BukkitUtil.toVector(vehicle.getLocation());
/* 59 */       RegionManager mgr = this.plugin.getGlobalRegionManager().get(world);
/* 60 */       ApplicableRegionSet set = mgr.getApplicableRegions(pt);
/* 61 */       LocalPlayer localPlayer = this.plugin.wrapPlayer(player);
/*    */ 
/* 63 */       if ((!this.plugin.getGlobalRegionManager().hasBypass(player, world)) && (!set.canBuild(localPlayer)) && (!set.allows(DefaultFlag.DESTROY_VEHICLE, localPlayer)))
/*    */       {
/* 66 */         player.sendMessage(ChatColor.DARK_RED + "You don't have permission to destroy vehicles here.");
/* 67 */         event.setCancelled(true);
/* 68 */         return;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   @EventHandler
/*    */   public void onVehicleMove(VehicleMoveEvent event) {
/* 75 */     Vehicle vehicle = event.getVehicle();
/* 76 */     if ((vehicle.getPassenger() == null) || (!(vehicle.getPassenger() instanceof Player)))
/* 77 */       return;
/* 78 */     Player player = (Player)vehicle.getPassenger();
/* 79 */     World world = vehicle.getWorld();
/* 80 */     ConfigurationManager cfg = this.plugin.getGlobalStateManager();
/* 81 */     WorldConfiguration wcfg = cfg.get(world);
/*    */ 
/* 83 */     if (wcfg.useRegions)
/*    */     {
/* 85 */       if ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockY() != event.getTo().getBlockY()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))
/*    */       {
/* 88 */         boolean result = WorldGuardPlayerListener.checkMove(this.plugin, player, event.getFrom(), event.getTo());
/* 89 */         if (result) {
/* 90 */           vehicle.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
/* 91 */           vehicle.teleport(event.getFrom());
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.WorldGuardVehicleListener
 * JD-Core Version:    0.6.2
 */