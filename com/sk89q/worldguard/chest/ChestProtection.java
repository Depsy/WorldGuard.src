package com.sk89q.worldguard.chest;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract interface ChestProtection
{
  public abstract boolean isProtected(Block paramBlock, Player paramPlayer);

  public abstract boolean isProtectedPlacement(Block paramBlock, Player paramPlayer);

  public abstract boolean isAdjacentChestProtected(Block paramBlock, Player paramPlayer);

  @Deprecated
  public abstract boolean isChest(Material paramMaterial);

  public abstract boolean isChest(int paramInt);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.chest.ChestProtection
 * JD-Core Version:    0.6.2
 */