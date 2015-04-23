package org.khelekore.prtree;

public abstract interface MBR
{
  public abstract int getDimensions();

  public abstract double getMin(int paramInt);

  public abstract double getMax(int paramInt);

  public abstract MBR union(MBR paramMBR);

  public abstract boolean intersects(MBR paramMBR);

  public abstract <T> boolean intersects(T paramT, MBRConverter<T> paramMBRConverter);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.MBR
 * JD-Core Version:    0.6.2
 */