package com.sk89q.worldguard.blacklist.loggers;

import com.sk89q.worldguard.blacklist.events.BlacklistEvent;

public abstract interface BlacklistLoggerHandler
{
  public abstract void logEvent(BlacklistEvent paramBlacklistEvent, String paramString);

  public abstract void close();
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.blacklist.loggers.BlacklistLoggerHandler
 * JD-Core Version:    0.6.2
 */