/*     */ package com.sk89q.worldguard.bukkit.commands;
/*     */ 
/*     */ import com.sk89q.worldedit.BlockVector;
/*     */ import com.sk89q.worldguard.domains.DefaultDomain;
/*     */ import com.sk89q.worldguard.protection.flags.DefaultFlag;
/*     */ import com.sk89q.worldguard.protection.flags.Flag;
/*     */ import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
/*     */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ 
/*     */ public class RegionPrintoutBuilder
/*     */ {
/*     */   private final ProtectedRegion region;
/*  42 */   private final StringBuilder builder = new StringBuilder();
/*     */ 
/*     */   public RegionPrintoutBuilder(ProtectedRegion region)
/*     */   {
/*  50 */     this.region = region;
/*     */   }
/*     */ 
/*     */   private void newLine()
/*     */   {
/*  57 */     this.builder.append("\n");
/*     */   }
/*     */ 
/*     */   public void appendBasics()
/*     */   {
/*  64 */     this.builder.append(ChatColor.BLUE);
/*  65 */     this.builder.append("Region: ");
/*  66 */     this.builder.append(ChatColor.YELLOW);
/*  67 */     this.builder.append(this.region.getId());
/*     */ 
/*  69 */     this.builder.append(ChatColor.GRAY);
/*  70 */     this.builder.append(" (type=");
/*  71 */     this.builder.append(this.region.getTypeName());
/*     */ 
/*  73 */     this.builder.append(ChatColor.GRAY);
/*  74 */     this.builder.append(", priority=");
/*  75 */     this.builder.append(this.region.getPriority());
/*  76 */     this.builder.append(")");
/*     */ 
/*  78 */     newLine();
/*     */   }
/*     */ 
/*     */   public void appendFlags()
/*     */   {
/*  85 */     this.builder.append(ChatColor.BLUE);
/*  86 */     this.builder.append("Flags: ");
/*     */ 
/*  88 */     appendFlagsList(true);
/*     */ 
/*  90 */     newLine();
/*     */   }
/*     */ 
/*     */   public void appendFlagsList(boolean useColors)
/*     */   {
/*  99 */     boolean hasFlags = false;
/*     */ 
/* 101 */     for (Flag flag : DefaultFlag.getFlags()) {
/* 102 */       Object val = this.region.getFlag(flag); Object group = null;
/*     */ 
/* 105 */       if (val != null)
/*     */       {
/* 109 */         if (hasFlags) {
/* 110 */           this.builder.append(", ");
/*     */         }
/* 112 */         else if (useColors) {
/* 113 */           this.builder.append(ChatColor.YELLOW);
/*     */         }
/*     */ 
/* 117 */         RegionGroupFlag groupFlag = flag.getRegionGroupFlag();
/* 118 */         if (groupFlag != null) {
/* 119 */           group = this.region.getFlag(groupFlag);
/*     */         }
/*     */ 
/* 122 */         if (group == null) {
/* 123 */           this.builder.append(flag.getName()).append(": ").append(String.valueOf(val));
/*     */         }
/*     */         else {
/* 126 */           this.builder.append(flag.getName()).append(" -g ").append(String.valueOf(group)).append(": ").append(String.valueOf(val));
/*     */         }
/*     */ 
/* 131 */         hasFlags = true;
/*     */       }
/*     */     }
/* 134 */     if (!hasFlags) {
/* 135 */       if (useColors) {
/* 136 */         this.builder.append(ChatColor.RED);
/*     */       }
/* 138 */       this.builder.append("(none)");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void appendParents()
/*     */   {
/* 146 */     appendParentTree(true);
/*     */   }
/*     */ 
/*     */   public void appendParentTree(boolean useColors)
/*     */   {
/* 155 */     if (this.region.getParent() == null) {
/* 156 */       return;
/*     */     }
/*     */ 
/* 159 */     List inheritance = new ArrayList();
/*     */ 
/* 161 */     ProtectedRegion r = this.region;
/* 162 */     inheritance.add(r);
/* 163 */     while (r.getParent() != null) {
/* 164 */       r = r.getParent();
/* 165 */       inheritance.add(r);
/*     */     }
/*     */ 
/* 168 */     ListIterator it = inheritance.listIterator(inheritance.size());
/*     */ 
/* 171 */     int indent = 0;
/* 172 */     while (it.hasPrevious()) {
/* 173 */       ProtectedRegion cur = (ProtectedRegion)it.previous();
/* 174 */       if (useColors) {
/* 175 */         this.builder.append(ChatColor.GREEN);
/*     */       }
/*     */ 
/* 179 */       if (indent != 0) {
/* 180 */         for (int i = 0; i < indent; i++) {
/* 181 */           this.builder.append("  ");
/*     */         }
/* 183 */         this.builder.append("┗");
/*     */       }
/*     */ 
/* 187 */       this.builder.append(cur.getId());
/*     */ 
/* 190 */       if (!cur.equals(this.region)) {
/* 191 */         if (useColors) {
/* 192 */           this.builder.append(ChatColor.GRAY);
/*     */         }
/* 194 */         this.builder.append(" (parent, priority=" + cur.getPriority() + ")");
/*     */       }
/*     */ 
/* 197 */       indent++;
/* 198 */       newLine();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void appendDomain()
/*     */   {
/* 206 */     this.builder.append(ChatColor.BLUE);
/* 207 */     this.builder.append("Owners: ");
/* 208 */     DefaultDomain owners = this.region.getOwners();
/* 209 */     if (owners.size() != 0) {
/* 210 */       this.builder.append(ChatColor.YELLOW);
/* 211 */       this.builder.append(owners.toUserFriendlyString());
/*     */     } else {
/* 213 */       this.builder.append(ChatColor.RED);
/* 214 */       this.builder.append("(no owners)");
/*     */     }
/*     */ 
/* 217 */     newLine();
/*     */ 
/* 219 */     this.builder.append(ChatColor.BLUE);
/* 220 */     this.builder.append("Members: ");
/* 221 */     DefaultDomain members = this.region.getMembers();
/* 222 */     if (members.size() != 0) {
/* 223 */       this.builder.append(ChatColor.YELLOW);
/* 224 */       this.builder.append(members.toUserFriendlyString());
/*     */     } else {
/* 226 */       this.builder.append(ChatColor.RED);
/* 227 */       this.builder.append("(no members)");
/*     */     }
/*     */ 
/* 230 */     newLine();
/*     */   }
/*     */ 
/*     */   public void appendBounds()
/*     */   {
/* 237 */     BlockVector min = this.region.getMinimumPoint();
/* 238 */     BlockVector max = this.region.getMaximumPoint();
/* 239 */     this.builder.append(ChatColor.BLUE);
/* 240 */     this.builder.append("Bounds:");
/* 241 */     this.builder.append(ChatColor.YELLOW);
/* 242 */     this.builder.append(" (" + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + ")");
/* 243 */     this.builder.append(" -> (" + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + ")");
/*     */ 
/* 245 */     newLine();
/*     */   }
/*     */ 
/*     */   public void appendRegionInfo()
/*     */   {
/* 252 */     this.builder.append(ChatColor.GRAY);
/* 253 */     this.builder.append("══════════════");
/* 254 */     this.builder.append(" Region Info ");
/* 255 */     this.builder.append("══════════════");
/* 256 */     newLine();
/* 257 */     appendBasics();
/* 258 */     appendFlags();
/* 259 */     appendParents();
/* 260 */     appendDomain();
/* 261 */     appendBounds();
/*     */   }
/*     */ 
/*     */   public void send(CommandSender sender)
/*     */   {
/* 270 */     sender.sendMessage(toString());
/*     */   }
/*     */ 
/*     */   public StringBuilder append(boolean b) {
/* 274 */     return this.builder.append(b);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(char c) {
/* 278 */     return this.builder.append(c);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(char[] str, int offset, int len) {
/* 282 */     return this.builder.append(str, offset, len);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(char[] str) {
/* 286 */     return this.builder.append(str);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(CharSequence s, int start, int end) {
/* 290 */     return this.builder.append(s, start, end);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(CharSequence s) {
/* 294 */     return this.builder.append(s);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(double d) {
/* 298 */     return this.builder.append(d);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(float f) {
/* 302 */     return this.builder.append(f);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(int i) {
/* 306 */     return this.builder.append(i);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(long lng) {
/* 310 */     return this.builder.append(lng);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(Object obj) {
/* 314 */     return this.builder.append(obj);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(String str) {
/* 318 */     return this.builder.append(str);
/*     */   }
/*     */ 
/*     */   public StringBuilder append(StringBuffer sb) {
/* 322 */     return this.builder.append(sb);
/*     */   }
/*     */ 
/*     */   public StringBuilder appendCodePoint(int codePoint) {
/* 326 */     return this.builder.appendCodePoint(codePoint);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 331 */     return this.builder.toString().trim();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.bukkit.commands.RegionPrintoutBuilder
 * JD-Core Version:    0.6.2
 */