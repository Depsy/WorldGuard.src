/*     */ package au.com.bytecode.opencsv;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CSVWriter
/*     */   implements Closeable
/*     */ {
/*     */   public static final int INITIAL_STRING_SIZE = 128;
/*     */   private Writer rawWriter;
/*     */   private PrintWriter pw;
/*     */   private char separator;
/*     */   private char quotechar;
/*     */   private char escapechar;
/*     */   private String lineEnd;
/*     */   public static final char DEFAULT_ESCAPE_CHARACTER = '"';
/*     */   public static final char DEFAULT_SEPARATOR = ',';
/*     */   public static final char DEFAULT_QUOTE_CHARACTER = '"';
/*     */   public static final char NO_QUOTE_CHARACTER = '\000';
/*     */   public static final char NO_ESCAPE_CHARACTER = '\000';
/*     */   public static final String DEFAULT_LINE_END = "\n";
/*     */ 
/*     */   public CSVWriter(Writer writer)
/*     */   {
/*  85 */     this(writer, ',');
/*     */   }
/*     */ 
/*     */   public CSVWriter(Writer writer, char separator)
/*     */   {
/*  97 */     this(writer, separator, '"');
/*     */   }
/*     */ 
/*     */   public CSVWriter(Writer writer, char separator, char quotechar)
/*     */   {
/* 111 */     this(writer, separator, quotechar, '"');
/*     */   }
/*     */ 
/*     */   public CSVWriter(Writer writer, char separator, char quotechar, char escapechar)
/*     */   {
/* 127 */     this(writer, separator, quotechar, escapechar, "\n");
/*     */   }
/*     */ 
/*     */   public CSVWriter(Writer writer, char separator, char quotechar, String lineEnd)
/*     */   {
/* 144 */     this(writer, separator, quotechar, '"', lineEnd);
/*     */   }
/*     */ 
/*     */   public CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd)
/*     */   {
/* 164 */     this.rawWriter = writer;
/* 165 */     this.pw = new PrintWriter(writer);
/* 166 */     this.separator = separator;
/* 167 */     this.quotechar = quotechar;
/* 168 */     this.escapechar = escapechar;
/* 169 */     this.lineEnd = lineEnd;
/*     */   }
/*     */ 
/*     */   public void writeAll(List<String[]> allLines)
/*     */   {
/* 181 */     for (String[] line : allLines)
/* 182 */       writeNext(line);
/*     */   }
/*     */ 
/*     */   protected void writeColumnNames(ResultSetMetaData metadata)
/*     */     throws SQLException
/*     */   {
/* 189 */     int columnCount = metadata.getColumnCount();
/*     */ 
/* 191 */     String[] nextLine = new String[columnCount];
/* 192 */     for (int i = 0; i < columnCount; i++) {
/* 193 */       nextLine[i] = metadata.getColumnName(i + 1);
/*     */     }
/* 195 */     writeNext(nextLine);
/*     */   }
/*     */ 
/*     */   public void writeAll(ResultSet rs, boolean includeColumnNames)
/*     */     throws SQLException, IOException
/*     */   {
/* 209 */     ResultSetMetaData metadata = rs.getMetaData();
/*     */ 
/* 212 */     if (includeColumnNames) {
/* 213 */       writeColumnNames(metadata);
/*     */     }
/*     */ 
/* 216 */     int columnCount = metadata.getColumnCount();
/*     */ 
/* 218 */     while (rs.next())
/*     */     {
/* 220 */       String[] nextLine = new String[columnCount];
/*     */ 
/* 222 */       for (int i = 0; i < columnCount; i++) {
/* 223 */         nextLine[i] = getColumnValue(rs, metadata.getColumnType(i + 1), i + 1);
/*     */       }
/*     */ 
/* 226 */       writeNext(nextLine);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getColumnValue(ResultSet rs, int colType, int colIndex)
/*     */     throws SQLException, IOException
/*     */   {
/* 233 */     String value = "";
/*     */ 
/* 235 */     switch (colType)
/*     */     {
/*     */     case -7:
/* 238 */       Object bit = rs.getObject(colIndex);
/* 239 */       if (bit != null)
/* 240 */         value = String.valueOf(bit); break;
/*     */     case 16:
/* 244 */       boolean b = rs.getBoolean(colIndex);
/* 245 */       if (!rs.wasNull())
/* 246 */         value = Boolean.valueOf(b).toString(); break;
/*     */     case 2005:
/* 250 */       Clob c = rs.getClob(colIndex);
/* 251 */       if (c != null)
/* 252 */         value = read(c); break;
/*     */     case -5:
/* 256 */       long lv = rs.getLong(colIndex);
/* 257 */       if (!rs.wasNull())
/* 258 */         value = Long.toString(lv); break;
/*     */     case 2:
/*     */     case 3:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 266 */       BigDecimal bd = rs.getBigDecimal(colIndex);
/* 267 */       if (bd != null)
/* 268 */         value = bd.toString(); break;
/*     */     case -6:
/*     */     case 4:
/*     */     case 5:
/* 274 */       int intValue = rs.getInt(colIndex);
/* 275 */       if (!rs.wasNull())
/* 276 */         value = Integer.toString(intValue); break;
/*     */     case 2000:
/* 280 */       Object obj = rs.getObject(colIndex);
/* 281 */       if (obj != null)
/* 282 */         value = String.valueOf(obj); break;
/*     */     case 91:
/* 286 */       Date date = rs.getDate(colIndex);
/* 287 */       if (date != null) {
/* 288 */         SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
/* 289 */         value = dateFormat.format(date);
/* 290 */       }break;
/*     */     case 92:
/* 293 */       Time t = rs.getTime(colIndex);
/* 294 */       if (t != null)
/* 295 */         value = t.toString(); break;
/*     */     case 93:
/* 299 */       Timestamp tstamp = rs.getTimestamp(colIndex);
/* 300 */       if (tstamp != null) {
/* 301 */         SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
/* 302 */         value = timeFormat.format(tstamp);
/* 303 */       }break;
/*     */     case -1:
/*     */     case 1:
/*     */     case 12:
/* 308 */       value = rs.getString(colIndex);
/* 309 */       break;
/*     */     default:
/* 311 */       value = "";
/*     */     }
/*     */ 
/* 315 */     if (value == null)
/*     */     {
/* 317 */       value = "";
/*     */     }
/*     */ 
/* 320 */     return value;
/*     */   }
/*     */ 
/*     */   private static String read(Clob c)
/*     */     throws SQLException, IOException
/*     */   {
/* 326 */     StringBuilder sb = new StringBuilder((int)c.length());
/* 327 */     Reader r = c.getCharacterStream();
/* 328 */     char[] cbuf = new char[2048];
/* 329 */     int n = 0;
/* 330 */     while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
/* 331 */       if (n > 0) {
/* 332 */         sb.append(cbuf, 0, n);
/*     */       }
/*     */     }
/* 335 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public void writeNext(String[] nextLine)
/*     */   {
/* 347 */     if (nextLine == null) {
/* 348 */       return;
/*     */     }
/* 350 */     StringBuilder sb = new StringBuilder(128);
/* 351 */     for (int i = 0; i < nextLine.length; i++)
/*     */     {
/* 353 */       if (i != 0) {
/* 354 */         sb.append(this.separator);
/*     */       }
/*     */ 
/* 357 */       String nextElement = nextLine[i];
/* 358 */       if (nextElement != null)
/*     */       {
/* 360 */         if (this.quotechar != 0) {
/* 361 */           sb.append(this.quotechar);
/*     */         }
/* 363 */         sb.append(stringContainsSpecialCharacters(nextElement) ? processLine(nextElement) : nextElement);
/*     */ 
/* 365 */         if (this.quotechar != 0)
/* 366 */           sb.append(this.quotechar);
/*     */       }
/*     */     }
/* 369 */     sb.append(this.lineEnd);
/* 370 */     this.pw.write(sb.toString());
/*     */   }
/*     */ 
/*     */   private boolean stringContainsSpecialCharacters(String line)
/*     */   {
/* 375 */     return (line.indexOf(this.quotechar) != -1) || (line.indexOf(this.escapechar) != -1);
/*     */   }
/*     */ 
/*     */   private StringBuilder processLine(String nextElement)
/*     */   {
/* 380 */     StringBuilder sb = new StringBuilder(128);
/* 381 */     for (int j = 0; j < nextElement.length(); j++) {
/* 382 */       char nextChar = nextElement.charAt(j);
/* 383 */       if ((this.escapechar != 0) && (nextChar == this.quotechar))
/* 384 */         sb.append(this.escapechar).append(nextChar);
/* 385 */       else if ((this.escapechar != 0) && (nextChar == this.escapechar))
/* 386 */         sb.append(this.escapechar).append(nextChar);
/*     */       else {
/* 388 */         sb.append(nextChar);
/*     */       }
/*     */     }
/*     */ 
/* 392 */     return sb;
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 402 */     this.pw.flush();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 413 */     this.pw.flush();
/* 414 */     this.pw.close();
/* 415 */     this.rawWriter.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     au.com.bytecode.opencsv.CSVWriter
 * JD-Core Version:    0.6.2
 */