/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ import java.util.PriorityQueue;
/*    */ 
/*    */ class LeafNode<T> extends NodeBase<T, T>
/*    */ {
/* 70 */   private static final Comparator<DistanceResult<?>> comp = new Comparator()
/*    */   {
/*    */     public int compare(DistanceResult<?> d1, DistanceResult<?> d2) {
/* 73 */       return Double.compare(d1.getDistance(), d2.getDistance());
/*    */     }
/* 70 */   };
/*    */ 
/*    */   public LeafNode(Object[] data)
/*    */   {
/* 11 */     super(data);
/*    */   }
/*    */ 
/*    */   public MBR getMBR(T t, MBRConverter<T> converter) {
/* 15 */     return new SimpleMBR(t, converter);
/*    */   }
/*    */ 
/*    */   public MBR computeMBR(MBRConverter<T> converter) {
/* 19 */     MBR ret = null;
/* 20 */     int i = 0; for (int s = size(); i < s; i++)
/* 21 */       ret = getUnion(ret, getMBR(get(i), converter));
/* 22 */     return ret;
/*    */   }
/*    */ 
/*    */   public void expand(MBR mbr, MBRConverter<T> converter, List<T> found, List<Node<T>> nodesToExpand)
/*    */   {
/* 27 */     find(mbr, converter, found);
/*    */   }
/*    */ 
/*    */   public void find(MBR mbr, MBRConverter<T> converter, List<T> result) {
/* 31 */     int i = 0; for (int s = size(); i < s; i++) {
/* 32 */       Object t = get(i);
/* 33 */       if (mbr.intersects(t, converter))
/* 34 */         result.add(t);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void nnExpand(DistanceCalculator<T> dc, NodeFilter<T> filter, List<DistanceResult<T>> drs, int maxHits, PriorityQueue<Node<T>> queue, MinDistComparator<T, Node<T>> mdc)
/*    */   {
/* 44 */     int i = 0; for (int s = size(); i < s; i++) {
/* 45 */       Object t = get(i);
/* 46 */       if (filter.accept(t)) {
/* 47 */         double dist = dc.distanceTo(t, mdc.p);
/* 48 */         int n = drs.size();
/* 49 */         if ((n < maxHits) || (dist < ((DistanceResult)drs.get(n - 1)).getDistance()))
/* 50 */           add(drs, new DistanceResult(t, dist), maxHits);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private void add(List<DistanceResult<T>> drs, DistanceResult<T> dr, int maxHits)
/*    */   {
/* 59 */     int n = drs.size();
/* 60 */     if (n == maxHits)
/* 61 */       drs.remove(n - 1);
/* 62 */     int pos = Collections.binarySearch(drs, dr, comp);
/* 63 */     if (pos < 0)
/*    */     {
/* 65 */       pos = -(pos + 1);
/*    */     }
/* 67 */     drs.add(pos, dr);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.LeafNode
 * JD-Core Version:    0.6.2
 */