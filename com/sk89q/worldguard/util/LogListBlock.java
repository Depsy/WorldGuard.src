/*     */ package com.sk89q.worldguard.util;
/*     */ 
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class LogListBlock
/*     */ {
/*  26 */   private LinkedHashMap<String, Object> items = new LinkedHashMap();
/*     */ 
/*  28 */   private int maxKeyLength = 0;
/*     */ 
/*     */   private void updateKey(String key) {
/*  31 */     if (key.length() > this.maxKeyLength)
/*  32 */       this.maxKeyLength = key.length();
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, String value)
/*     */   {
/*  37 */     updateKey(key);
/*  38 */     this.items.put(key, String.valueOf(value));
/*  39 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, LogListBlock value) {
/*  43 */     updateKey(key);
/*  44 */     this.items.put(key, value);
/*  45 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, Object value) {
/*  49 */     put(key, String.valueOf(value));
/*  50 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, String value, Object[] args) {
/*  54 */     put(key, String.format(value, args));
/*  55 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, int value) {
/*  59 */     put(key, String.valueOf(value));
/*  60 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, byte value) {
/*  64 */     put(key, String.valueOf(value));
/*  65 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, double value) {
/*  69 */     put(key, String.valueOf(value));
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, float value) {
/*  74 */     put(key, String.valueOf(value));
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, short value) {
/*  79 */     put(key, String.valueOf(value));
/*  80 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, long value) {
/*  84 */     put(key, String.valueOf(value));
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock put(String key, boolean value) {
/*  89 */     put(key, String.valueOf(value));
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */   public LogListBlock putChild(String key) {
/*  94 */     updateKey(key);
/*  95 */     LogListBlock block = new LogListBlock();
/*  96 */     this.items.put(key, block);
/*  97 */     return block;
/*     */   }
/*     */ 
/*     */   private String padKey(String key, int len) {
/* 101 */     return String.format(new StringBuilder().append("%-").append(len).append("s").toString(), new Object[] { key });
/*     */   }
/*     */ 
/*     */   protected String getOutput(String prefix) {
/* 105 */     StringBuilder out = new StringBuilder();
/* 106 */     for (Map.Entry entry : this.items.entrySet()) {
/* 107 */       Object val = entry.getValue();
/* 108 */       if ((val instanceof LogListBlock)) {
/* 109 */         out.append(prefix);
/* 110 */         out.append(padKey((String)entry.getKey(), this.maxKeyLength));
/* 111 */         out.append(":\r\n");
/* 112 */         out.append(((LogListBlock)val).getOutput(new StringBuilder().append(prefix).append("    ").toString()));
/*     */       } else {
/* 114 */         out.append(prefix);
/* 115 */         out.append(padKey((String)entry.getKey(), this.maxKeyLength));
/* 116 */         out.append(": ");
/* 117 */         out.append(val.toString());
/* 118 */         out.append("\r\n");
/*     */       }
/*     */     }
/* 121 */     return out.toString();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 126 */     return getOutput("");
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.util.LogListBlock
 * JD-Core Version:    0.6.2
 */