package org.khelekore.prtree;

public abstract interface MBRConverter<T>
{
  public abstract int getDimensions();

  public abstract double getMin(int paramInt, T paramT);

  public abstract double getMax(int paramInt, T paramT);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.MBRConverter
 * JD-Core Version:    0.6.2
 */