package com.sk89q.worldguard.domains;

import com.sk89q.worldguard.LocalPlayer;

public abstract interface Domain
{
  public abstract boolean contains(LocalPlayer paramLocalPlayer);

  public abstract boolean contains(String paramString);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.domains.Domain
 * JD-Core Version:    0.6.2
 */