/*     */ package org.khelekore.prtree;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class PRTree<T>
/*     */ {
/*     */   private MBRConverter<T> converter;
/*     */   private int branchFactor;
/*     */   private Node<T> root;
/*     */   private int numLeafs;
/*     */   private int height;
/*     */ 
/*     */   public PRTree(MBRConverter<T> converter, int branchFactor)
/*     */   {
/*  28 */     this.converter = converter;
/*  29 */     this.branchFactor = branchFactor;
/*     */   }
/*     */ 
/*     */   public void load(Collection<? extends T> data)
/*     */   {
/*  42 */     if (this.root != null)
/*  43 */       throw new IllegalStateException("Tree is already loaded");
/*  44 */     this.numLeafs = data.size();
/*  45 */     LeafBuilder lb = new LeafBuilder(this.converter.getDimensions(), this.branchFactor);
/*     */ 
/*  47 */     List leafNodes = new ArrayList(estimateSize(this.numLeafs));
/*     */ 
/*  49 */     lb.buildLeafs(data, new DataComparators(this.converter), new LeafNodeFactory(null), leafNodes);
/*     */ 
/*  52 */     this.height = 1;
/*  53 */     List nodes = leafNodes;
/*  54 */     while (nodes.size() > this.branchFactor) {
/*  55 */       this.height += 1;
/*  56 */       List internalNodes = new ArrayList(estimateSize(nodes.size()));
/*     */ 
/*  58 */       lb.buildLeafs(nodes, new InternalNodeComparators(this.converter), new InternalNodeFactory(null), internalNodes);
/*     */ 
/*  60 */       nodes = internalNodes;
/*     */     }
/*  62 */     setRoot(nodes);
/*     */   }
/*     */ 
/*     */   private int estimateSize(int dataSize) {
/*  66 */     return (int)(1.0D / (this.branchFactor - 1) * dataSize);
/*     */   }
/*     */ 
/*     */   private <N extends Node<T>> void setRoot(List<N> nodes) {
/*  70 */     if (nodes.size() == 0) {
/*  71 */       this.root = new InternalNode(new Object[0]);
/*  72 */     } else if (nodes.size() == 1) {
/*  73 */       this.root = ((Node)nodes.get(0));
/*     */     } else {
/*  75 */       this.height += 1;
/*  76 */       this.root = new InternalNode(nodes.toArray());
/*     */     }
/*     */   }
/*     */ 
/*     */   public MBR2D getMBR2D()
/*     */   {
/*  99 */     MBR mbr = getMBR();
/* 100 */     if (mbr == null)
/* 101 */       return null;
/* 102 */     return new SimpleMBR2D(mbr.getMin(0), mbr.getMin(1), mbr.getMax(0), mbr.getMax(1));
/*     */   }
/*     */ 
/*     */   public MBR getMBR()
/*     */   {
/* 112 */     return this.root.getMBR(this.converter);
/*     */   }
/*     */ 
/*     */   public int getNumberOfLeaves()
/*     */   {
/* 119 */     return this.numLeafs;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 126 */     return this.numLeafs == 0;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/* 133 */     return this.height;
/*     */   }
/*     */ 
/*     */   public void find(double xmin, double ymin, double xmax, double ymax, List<T> resultNodes)
/*     */   {
/* 148 */     find(new SimpleMBR(new double[] { xmin, xmax, ymin, ymax }), resultNodes);
/*     */   }
/*     */ 
/*     */   public void find(MBR query, List<T> resultNodes)
/*     */   {
/* 157 */     validateRect(query);
/* 158 */     this.root.find(query, this.converter, resultNodes);
/*     */   }
/*     */ 
/*     */   public Iterable<T> find(double xmin, double ymin, double xmax, double ymax)
/*     */   {
/* 173 */     return find(new SimpleMBR(new double[] { xmin, xmax, ymin, ymax }));
/*     */   }
/*     */ 
/*     */   public Iterable<T> find(final MBR query)
/*     */   {
/* 182 */     validateRect(query);
/* 183 */     return new Iterable() {
/*     */       public Iterator<T> iterator() {
/* 185 */         return new PRTree.Finder(PRTree.this, query);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private void validateRect(MBR query) {
/* 191 */     for (int i = 0; i < this.converter.getDimensions(); i++) {
/* 192 */       double max = query.getMax(i);
/* 193 */       double min = query.getMin(i);
/* 194 */       if (max < min)
/* 195 */         throw new IllegalArgumentException("max: " + max + " < min: " + min + ", axis: " + i + ", query: " + query);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<DistanceResult<T>> nearestNeighbour(DistanceCalculator<T> dc, NodeFilter<T> filter, int maxHits, PointND p)
/*     */   {
/* 259 */     if (isEmpty())
/* 260 */       return Collections.emptyList();
/* 261 */     NearestNeighbour nn = new NearestNeighbour(this.converter, filter, maxHits, this.root, dc, p);
/*     */ 
/* 263 */     return nn.find();
/*     */   }
/*     */ 
/*     */   private class Finder
/*     */     implements Iterator<T>
/*     */   {
/*     */     private MBR mbr;
/* 205 */     private List<T> ts = new ArrayList();
/* 206 */     private List<Node<T>> toVisit = new ArrayList();
/*     */     private T next;
/* 209 */     private int visitedNodes = 0;
/* 210 */     private int dataNodesVisited = 0;
/*     */ 
/*     */     public Finder(MBR mbr) {
/* 213 */       this.mbr = mbr;
/* 214 */       this.toVisit.add(PRTree.this.root);
/* 215 */       findNext();
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/* 219 */       return this.next != null;
/*     */     }
/*     */ 
/*     */     public T next() {
/* 223 */       Object toReturn = this.next;
/* 224 */       findNext();
/* 225 */       return toReturn;
/*     */     }
/*     */ 
/*     */     private void findNext() {
/* 229 */       while ((this.ts.isEmpty()) && (!this.toVisit.isEmpty())) {
/* 230 */         Node n = (Node)this.toVisit.remove(this.toVisit.size() - 1);
/* 231 */         this.visitedNodes += 1;
/* 232 */         n.expand(this.mbr, PRTree.this.converter, this.ts, this.toVisit);
/*     */       }
/* 234 */       if (this.ts.isEmpty()) {
/* 235 */         this.next = null;
/*     */       } else {
/* 237 */         this.next = this.ts.remove(this.ts.size() - 1);
/* 238 */         this.dataNodesVisited += 1;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 243 */       throw new UnsupportedOperationException("Not implemented");
/*     */     }
/*     */   }
/*     */ 
/*     */   private class InternalNodeFactory
/*     */     implements NodeFactory<InternalNode<T>>
/*     */   {
/*     */     private InternalNodeFactory()
/*     */     {
/*     */     }
/*     */ 
/*     */     public InternalNode<T> create(Object[] data)
/*     */     {
/*  90 */       return new InternalNode(data);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class LeafNodeFactory
/*     */     implements NodeFactory<LeafNode<T>>
/*     */   {
/*     */     private LeafNodeFactory()
/*     */     {
/*     */     }
/*     */ 
/*     */     public LeafNode<T> create(Object[] data)
/*     */     {
/*  83 */       return new LeafNode(data);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.PRTree
 * JD-Core Version:    0.6.2
 */