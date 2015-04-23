package org.khelekore.prtree;

public abstract interface MBR2D
{
  public abstract double getMinX();

  public abstract double getMinY();

  public abstract double getMaxX();

  public abstract double getMaxY();

  public abstract MBR2D union(MBR2D paramMBR2D);

  public abstract boolean intersects(MBR2D paramMBR2D);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.MBR2D
 * JD-Core Version:    0.6.2
 */