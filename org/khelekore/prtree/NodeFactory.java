package org.khelekore.prtree;

abstract interface NodeFactory<N>
{
  public abstract N create(Object[] paramArrayOfObject);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.NodeFactory
 * JD-Core Version:    0.6.2
 */