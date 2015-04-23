/*    */ package org.khelekore.prtree;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ class InternalNodeComparators<T>
/*    */   implements NodeComparators<Node<T>>
/*    */ {
/*    */   private final MBRConverter<T> converter;
/*    */ 
/*    */   public InternalNodeComparators(MBRConverter<T> converter)
/*    */   {
/*  9 */     this.converter = converter;
/*    */   }
/*    */ 
/*    */   public Comparator<Node<T>> getMinComparator(final int axis) {
/* 13 */     return new Comparator() {
/*    */       public int compare(Node<T> n1, Node<T> n2) {
/* 15 */         double d1 = n1.getMBR(InternalNodeComparators.this.converter).getMin(axis);
/* 16 */         double d2 = n2.getMBR(InternalNodeComparators.this.converter).getMin(axis);
/* 17 */         return Double.compare(d1, d2);
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public Comparator<Node<T>> getMaxComparator(final int axis) {
/* 23 */     return new Comparator() {
/*    */       public int compare(Node<T> n1, Node<T> n2) {
/* 25 */         double d1 = n1.getMBR(InternalNodeComparators.this.converter).getMax(axis);
/* 26 */         double d2 = n2.getMBR(InternalNodeComparators.this.converter).getMax(axis);
/* 27 */         return Double.compare(d1, d2);
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.InternalNodeComparators
 * JD-Core Version:    0.6.2
 */