/*    */ package com.sk89q.worldguard.protection.flags;
/*    */ 
/*    */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class SetFlag<T> extends Flag<Set<T>>
/*    */ {
/*    */   private Flag<T> subFlag;
/*    */ 
/*    */   public SetFlag(String name, RegionGroup defaultGroup, Flag<T> subFlag)
/*    */   {
/* 41 */     super(name, defaultGroup);
/* 42 */     this.subFlag = subFlag;
/*    */   }
/*    */ 
/*    */   public SetFlag(String name, Flag<T> subFlag) {
/* 46 */     super(name);
/* 47 */     this.subFlag = subFlag;
/*    */   }
/*    */ 
/*    */   public Set<T> parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
/*    */     throws InvalidFlagFormat
/*    */   {
/* 53 */     Set items = new HashSet();
/*    */ 
/* 55 */     for (String str : input.split(",")) {
/* 56 */       items.add(this.subFlag.parseInput(plugin, sender, str.trim()));
/*    */     }
/*    */ 
/* 59 */     return new HashSet(items);
/*    */   }
/*    */ 
/*    */   public Set<T> unmarshal(Object o)
/*    */   {
/* 64 */     if ((o instanceof Collection)) {
/* 65 */       Collection collection = (Collection)o;
/* 66 */       Set items = new HashSet();
/*    */ 
/* 68 */       for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object sub = i$.next();
/* 69 */         Object item = this.subFlag.unmarshal(sub);
/* 70 */         if (item != null) {
/* 71 */           items.add(item);
/*    */         }
/*    */       }
/*    */ 
/* 75 */       return items;
/*    */     }
/* 77 */     return null;
/*    */   }
/*    */ 
/*    */   public Object marshal(Set<T> o)
/*    */   {
/* 83 */     List list = new ArrayList();
/* 84 */     for (Iterator i$ = o.iterator(); i$.hasNext(); ) { Object item = i$.next();
/* 85 */       list.add(this.subFlag.marshal(item));
/*    */     }
/*    */ 
/* 88 */     return list;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.protection.flags.SetFlag
 * JD-Core Version:    0.6.2
 */