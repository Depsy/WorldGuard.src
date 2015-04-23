/*     */ package org.khelekore.prtree;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ class LeafBuilder
/*     */ {
/*     */   private final int dimensions;
/*     */   private final int branchFactor;
/*     */ 
/*     */   public LeafBuilder(int dimensions, int branchFactor)
/*     */   {
/*  21 */     this.dimensions = dimensions;
/*  22 */     this.branchFactor = branchFactor;
/*     */   }
/*     */ 
/*     */   public <T, N> void buildLeafs(Collection<? extends T> ls, NodeComparators<T> comparators, NodeFactory<N> nf, List<N> leafNodes)
/*     */   {
/*  29 */     List nodes = new ArrayList(ls.size());
/*  30 */     for (Iterator i$ = ls.iterator(); i$.hasNext(); ) { Object t = i$.next();
/*  31 */       nodes.add(new NodeUsage(t, 1));
/*     */     }
/*  33 */     Circle getters = new Circle(this.dimensions * 2);
/*     */ 
/*  36 */     for (int i = 0; i < this.dimensions; i++) {
/*  37 */       addGetterAndSplitter(nodes, comparators.getMinComparator(i), getters);
/*     */     }
/*     */ 
/*  40 */     for (int i = 0; i < this.dimensions; i++) {
/*  41 */       addGetterAndSplitter(nodes, comparators.getMaxComparator(i), getters);
/*     */     }
/*     */ 
/*  44 */     getLeafs(1, ls.size(), getters, nf, leafNodes);
/*     */   }
/*     */ 
/*     */   private <T, N> void addGetterAndSplitter(List<NodeUsage<T>> nodes, Comparator<T> tcomp, Circle<Noder<T, N>> getters)
/*     */   {
/*  50 */     Comparator comp = new NodeUsageComparator(tcomp);
/*  51 */     Collections.sort(nodes, comp);
/*  52 */     List sortedNodes = new ArrayList(nodes);
/*  53 */     getters.add(new Noder(sortedNodes, null));
/*     */   }
/*     */ 
/*     */   private <T, N> void getLeafs(int id, int totalNumberOfElements, Circle<Noder<T, N>> getters, NodeFactory<N> nf, List<N> leafNodes)
/*     */   {
/*  59 */     List partitionsToExpand = new ArrayList();
/*  60 */     int[] pos = new int[2 * this.dimensions];
/*  61 */     partitionsToExpand.add(new Partition(id, totalNumberOfElements, pos));
/*  62 */     while (!partitionsToExpand.isEmpty()) {
/*  63 */       Partition p = (Partition)partitionsToExpand.remove(0);
/*     */ 
/*  65 */       getters.reset();
/*  66 */       for (int i = 0; i < getters.getNumElements(); i++) {
/*  67 */         int nodesToGet = Math.min(p.numElementsLeft, this.branchFactor);
/*  68 */         if (nodesToGet == 0)
/*     */           break;
/*  70 */         Noder noder = (Noder)getters.getNext();
/*  71 */         leafNodes.add(noder.getNextNode(p, i, nodesToGet, nf));
/*  72 */         Partition.access$120(p, nodesToGet);
/*     */       }
/*     */ 
/*  75 */       if (p.numElementsLeft > 0) {
/*  76 */         int splitPos = getSplitPos(p.id) % getters.getNumElements();
/*  77 */         Noder s = (Noder)getters.get(splitPos);
/*  78 */         s.split(p, splitPos, p.numElementsLeft, p.id, 2 * p.id, 2 * p.id + 1, partitionsToExpand);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getSplitPos(int n)
/*     */   {
/*  88 */     int splitPos = 0;
/*  89 */     while (n >= 2) {
/*  90 */       n >>= 1;
/*  91 */       splitPos++;
/*     */     }
/*  93 */     return splitPos;
/*     */   }
/*     */ 
/*     */   private static class Partition
/*     */   {
/*     */     private final int id;
/*     */     private int numElementsLeft;
/*     */     private int[] currentPositions;
/*     */ 
/*     */     public Partition(int id, int numElementsLeft, int[] currentPositions)
/*     */     {
/* 187 */       this.id = id;
/* 188 */       this.numElementsLeft = numElementsLeft;
/* 189 */       this.currentPositions = currentPositions;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 193 */       return getClass().getSimpleName() + "{id: " + this.id + ", numElementsLeft: " + this.numElementsLeft + ", currentPositions: " + Arrays.toString(this.currentPositions) + "}";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Noder<T, N>
/*     */   {
/*     */     private final List<NodeUsage<T>> data;
/*     */ 
/*     */     private Noder(List<NodeUsage<T>> data)
/*     */     {
/* 113 */       this.data = data;
/*     */     }
/*     */ 
/*     */     private N getNextNode(LeafBuilder.Partition p, int gi, int maxObjects, NodeFactory<N> nf)
/*     */     {
/* 126 */       Object[] nodeData = new Object[maxObjects];
/* 127 */       int s = this.data.size();
/* 128 */       for (int i = 0; i < maxObjects; i++) {
/* 129 */         while ((p.currentPositions[gi] < s) && (isUsedNode(p, p.currentPositions[gi])))
/*     */         {
/* 131 */           p.currentPositions[gi] += 1;
/*     */         }
/* 133 */         NodeUsage nu = (NodeUsage)this.data.set(p.currentPositions[gi], null);
/* 134 */         nodeData[i] = nu.getData();
/* 135 */         nu.use();
/*     */       }
/*     */ 
/* 138 */       for (int i = 0; i < nodeData.length; i++) {
/* 139 */         if (nodeData[i] == null) {
/* 140 */           for (int j = 0; j < this.data.size(); j++)
/* 141 */             System.err.println(j + ": " + this.data.get(j));
/* 142 */           throw new NullPointerException("Null data found at: " + i);
/*     */         }
/*     */       }
/* 145 */       return nf.create(nodeData);
/*     */     }
/*     */ 
/*     */     private boolean isUsedNode(LeafBuilder.Partition p, int pos) {
/* 149 */       NodeUsage nu = (NodeUsage)this.data.get(pos);
/* 150 */       return (nu == null) || (nu.isUsed()) || (nu.getOwner() != p.id);
/*     */     }
/*     */ 
/*     */     private void split(LeafBuilder.Partition p, int gi, int nodesToMark, int fromId, int toId1, int toId2, List<LeafBuilder.Partition> partitionsToExpand)
/*     */     {
/* 156 */       int sizePart2 = nodesToMark / 2;
/* 157 */       int sizePart1 = nodesToMark - sizePart2;
/* 158 */       int startPos = p.currentPositions[gi];
/* 159 */       int startPos2 = markPart(sizePart1, fromId, toId1, startPos);
/* 160 */       markPart(sizePart2, fromId, toId2, startPos2);
/* 161 */       partitionsToExpand.add(0, new LeafBuilder.Partition(toId1, sizePart1, p.currentPositions));
/*     */ 
/* 163 */       int[] pos = (int[])p.currentPositions.clone();
/* 164 */       pos[gi] = startPos2;
/* 165 */       partitionsToExpand.add(1, new LeafBuilder.Partition(toId2, sizePart2, pos));
/*     */     }
/*     */ 
/*     */     private int markPart(int numToMark, int fromId, int toId, int startPos)
/*     */     {
/* 170 */       while (numToMark > 0)
/*     */       {
/*     */         NodeUsage nu;
/* 171 */         while (((nu = (NodeUsage)this.data.get(startPos)) == null) || (nu.getOwner() != fromId))
/*     */         {
/* 173 */           startPos++;
/* 174 */         }nu.changeOwner(toId);
/* 175 */         numToMark--;
/*     */       }
/* 177 */       return startPos;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class NodeUsageComparator<T>
/*     */     implements Comparator<NodeUsage<T>>
/*     */   {
/*     */     private Comparator<T> sorter;
/*     */ 
/*     */     public NodeUsageComparator(Comparator<T> sorter)
/*     */     {
/* 101 */       this.sorter = sorter;
/*     */     }
/*     */ 
/*     */     public int compare(NodeUsage<T> n1, NodeUsage<T> n2) {
/* 105 */       return this.sorter.compare(n1.getData(), n2.getData());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     org.khelekore.prtree.LeafBuilder
 * JD-Core Version:    0.6.2
 */