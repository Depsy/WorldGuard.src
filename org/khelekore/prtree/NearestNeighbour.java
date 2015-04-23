/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.PriorityQueue;
/*    */ 
/*    */ class NearestNeighbour<T>
/*    */ {
/*    */   private final MBRConverter<T> converter;
/*    */   private final NodeFilter<T> filter;
/*    */   private final int maxHits;
/*    */   private final Node<T> root;
/*    */   private final DistanceCalculator<T> dc;
/*    */   private final PointND p;
/*    */ 
/*    */   public NearestNeighbour(MBRConverter<T> converter, NodeFilter<T> filter, int maxHits, Node<T> root, DistanceCalculator<T> dc, PointND p)
/*    */   {
/* 24 */     this.converter = converter;
/* 25 */     this.filter = filter;
/* 26 */     this.maxHits = maxHits;
/* 27 */     this.root = root;
/* 28 */     this.dc = dc;
/* 29 */     this.p = p;
/*    */   }
/*    */ 
/*    */   public List<DistanceResult<T>> find()
/*    */   {
/* 36 */     List ret = new ArrayList(this.maxHits);
/*    */ 
/* 38 */     MinDistComparator nc = new MinDistComparator(this.converter, this.p);
/*    */ 
/* 40 */     PriorityQueue queue = new PriorityQueue(20, nc);
/* 41 */     queue.add(this.root);
/* 42 */     while (!queue.isEmpty()) {
/* 43 */       Node n = (Node)queue.remove();
/* 44 */       n.nnExpand(this.dc, this.filter, ret, this.maxHits, queue, nc);
/*    */     }
/* 46 */     return ret;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.NearestNeighbour
 * JD-Core Version:    0.6.2
 */