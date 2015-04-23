package org.khelekore.prtree;

import java.util.Comparator;

abstract interface NodeComparators<T>
{
  public abstract Comparator<T> getMinComparator(int paramInt);

  public abstract Comparator<T> getMaxComparator(int paramInt);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.NodeComparators
 * JD-Core Version:    0.6.2
 */