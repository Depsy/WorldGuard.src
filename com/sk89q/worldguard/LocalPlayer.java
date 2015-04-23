/*    */ package com.sk89q.worldguard;
/*    */ 
/*    */ import com.sk89q.worldedit.Vector;
/*    */ 
/*    */ public abstract class LocalPlayer
/*    */ {
/*    */   public abstract String getName();
/*    */ 
/*    */   public abstract boolean hasGroup(String paramString);
/*    */ 
/*    */   public abstract Vector getPosition();
/*    */ 
/*    */   public abstract void kick(String paramString);
/*    */ 
/*    */   public abstract void ban(String paramString);
/*    */ 
/*    */   public abstract void printRaw(String paramString);
/*    */ 
/*    */   public abstract String[] getGroups();
/*    */ 
/*    */   public abstract boolean hasPermission(String paramString);
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 86 */     return ((obj instanceof LocalPlayer)) && (((LocalPlayer)obj).getName().equals(getName()));
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 92 */     return getName().hashCode();
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.LocalPlayer
 * JD-Core Version:    0.6.2
 */