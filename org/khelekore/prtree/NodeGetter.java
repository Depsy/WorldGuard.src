package org.khelekore.prtree;

import java.util.List;

abstract interface NodeGetter<N>
{
  public abstract N getNextNode(int paramInt);

  public abstract boolean hasMoreNodes();

  public abstract boolean hasMoreData();

  public abstract List<? extends NodeGetter<N>> split(int paramInt1, int paramInt2);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.NodeGetter
 * JD-Core Version:    0.6.2
 */