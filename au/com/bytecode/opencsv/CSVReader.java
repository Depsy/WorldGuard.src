/*     */ package au.com.bytecode.opencsv;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CSVReader
/*     */   implements Closeable
/*     */ {
/*     */   private BufferedReader br;
/*  36 */   private boolean hasNext = true;
/*     */   private final char separator;
/*     */   private final char quotechar;
/*     */   private final char escape;
/*     */   private int skipLines;
/*     */   private boolean linesSkiped;
/*     */   public static final char DEFAULT_SEPARATOR = ',';
/*     */   public static final int INITIAL_READ_SIZE = 64;
/*     */   public static final char DEFAULT_QUOTE_CHARACTER = '"';
/*     */   public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
/*     */   public static final int DEFAULT_SKIP_LINES = 0;
/*     */ 
/*     */   public CSVReader(Reader reader)
/*     */   {
/*  78 */     this(reader, ',');
/*     */   }
/*     */ 
/*     */   public CSVReader(Reader reader, char separator)
/*     */   {
/*  90 */     this(reader, separator, '"', '\\');
/*     */   }
/*     */ 
/*     */   public CSVReader(Reader reader, char separator, char quotechar)
/*     */   {
/* 106 */     this(reader, separator, quotechar, '\\', 0);
/*     */   }
/*     */ 
/*     */   public CSVReader(Reader reader, char separator, char quotechar, char escape)
/*     */   {
/* 111 */     this(reader, separator, quotechar, escape, 0);
/*     */   }
/*     */ 
/*     */   public CSVReader(Reader reader, char separator, char quotechar, int line)
/*     */   {
/* 127 */     this(reader, separator, quotechar, '\\', line);
/*     */   }
/*     */ 
/*     */   public CSVReader(Reader reader, char separator, char quotechar, char escape, int line)
/*     */   {
/* 145 */     this.br = new BufferedReader(reader);
/* 146 */     this.separator = separator;
/* 147 */     this.quotechar = quotechar;
/* 148 */     this.escape = escape;
/* 149 */     this.skipLines = line;
/*     */   }
/*     */ 
/*     */   public List<String[]> readAll()
/*     */     throws IOException
/*     */   {
/* 165 */     List allElements = new ArrayList();
/* 166 */     while (this.hasNext) {
/* 167 */       String[] nextLineAsTokens = readNext();
/* 168 */       if (nextLineAsTokens != null)
/* 169 */         allElements.add(nextLineAsTokens);
/*     */     }
/* 171 */     return allElements;
/*     */   }
/*     */ 
/*     */   public String[] readNext()
/*     */     throws IOException
/*     */   {
/* 186 */     String nextLine = getNextLine();
/* 187 */     return this.hasNext ? parseLine(nextLine) : null;
/*     */   }
/*     */ 
/*     */   private String getNextLine()
/*     */     throws IOException
/*     */   {
/* 198 */     if (!this.linesSkiped) {
/* 199 */       for (int i = 0; i < this.skipLines; i++) {
/* 200 */         this.br.readLine();
/*     */       }
/* 202 */       this.linesSkiped = true;
/*     */     }
/* 204 */     String nextLine = this.br.readLine();
/* 205 */     if (nextLine == null) {
/* 206 */       this.hasNext = false;
/*     */     }
/* 208 */     return this.hasNext ? nextLine : null;
/*     */   }
/*     */ 
/*     */   private String[] parseLine(String nextLine)
/*     */     throws IOException
/*     */   {
/* 221 */     if (nextLine == null) {
/* 222 */       return null;
/*     */     }
/*     */ 
/* 225 */     List tokensOnThisLine = new ArrayList();
/* 226 */     StringBuilder sb = new StringBuilder(64);
/* 227 */     boolean inQuotes = false;
/*     */     do {
/* 229 */       if (inQuotes)
/*     */       {
/* 231 */         sb.append("\n");
/* 232 */         nextLine = getNextLine();
/* 233 */         if (nextLine == null)
/*     */           break;
/*     */       }
/* 236 */       for (int i = 0; i < nextLine.length(); i++)
/*     */       {
/* 238 */         char c = nextLine.charAt(i);
/* 239 */         if (c == this.escape) {
/* 240 */           if (isEscapable(nextLine, inQuotes, i)) {
/* 241 */             sb.append(nextLine.charAt(i + 1));
/* 242 */             i++;
/*     */           } else {
/* 244 */             i++;
/*     */           }
/* 246 */         } else if (c == this.quotechar) {
/* 247 */           if (isEscapedQuote(nextLine, inQuotes, i)) {
/* 248 */             sb.append(nextLine.charAt(i + 1));
/* 249 */             i++;
/*     */           } else {
/* 251 */             inQuotes = !inQuotes;
/*     */ 
/* 253 */             if ((i > 2) && (nextLine.charAt(i - 1) != this.separator) && (nextLine.length() > i + 1) && (nextLine.charAt(i + 1) != this.separator))
/*     */             {
/* 258 */               sb.append(c);
/*     */             }
/*     */           }
/* 261 */         } else if ((c == this.separator) && (!inQuotes)) {
/* 262 */           tokensOnThisLine.add(sb.toString());
/* 263 */           sb = new StringBuilder(64);
/*     */         } else {
/* 265 */           sb.append(c);
/*     */         }
/*     */       }
/*     */     }
/* 268 */     while (inQuotes);
/* 269 */     tokensOnThisLine.add(sb.toString());
/* 270 */     return (String[])tokensOnThisLine.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   private boolean isEscapedQuote(String nextLine, boolean inQuotes, int i)
/*     */   {
/* 282 */     return (inQuotes) && (nextLine.length() > i + 1) && (nextLine.charAt(i + 1) == this.quotechar);
/*     */   }
/*     */ 
/*     */   private boolean isEscapable(String nextLine, boolean inQuotes, int i)
/*     */   {
/* 295 */     return (inQuotes) && (nextLine.length() > i + 1) && ((nextLine.charAt(i + 1) == this.quotechar) || (nextLine.charAt(i + 1) == this.escape));
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 306 */     this.br.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     au.com.bytecode.opencsv.CSVReader
 * JD-Core Version:    0.6.2
 */