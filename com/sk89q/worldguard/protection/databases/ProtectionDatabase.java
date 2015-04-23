package com.sk89q.worldguard.protection.databases;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;

public abstract interface ProtectionDatabase
{
  public abstract void load()
    throws ProtectionDatabaseException;

  public abstract void save()
    throws ProtectionDatabaseException;

  public abstract void load(RegionManager paramRegionManager)
    throws ProtectionDatabaseException;

  public abstract void save(RegionManager paramRegionManager)
    throws ProtectionDatabaseException;

  public abstract Map<String, ProtectedRegion> getRegions();

  public abstract void setRegions(Map<String, ProtectedRegion> paramMap);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.ProtectionDatabase
 * JD-Core Version:    0.6.2
 */