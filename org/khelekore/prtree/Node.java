package org.khelekore.prtree;

import java.util.List;
import java.util.PriorityQueue;

abstract interface Node<T>
{
  public abstract int size();

  public abstract MBR getMBR(MBRConverter<T> paramMBRConverter);

  public abstract void expand(MBR paramMBR, MBRConverter<T> paramMBRConverter, List<T> paramList, List<Node<T>> paramList1);

  public abstract void find(MBR paramMBR, MBRConverter<T> paramMBRConverter, List<T> paramList);

  public abstract void nnExpand(DistanceCalculator<T> paramDistanceCalculator, NodeFilter<T> paramNodeFilter, List<DistanceResult<T>> paramList, int paramInt, PriorityQueue<Node<T>> paramPriorityQueue, MinDistComparator<T, Node<T>> paramMinDistComparator);
}

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.Node
 * JD-Core Version:    0.6.2
 */