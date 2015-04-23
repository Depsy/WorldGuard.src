package com.sk89q.worldguard.protection.databases.migrators;

public abstract interface DatabaseMigrator
{
  public abstract void migrate()
    throws MigrationException;
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.databases.migrators.DatabaseMigrator
 * JD-Core Version:    0.6.2
 */