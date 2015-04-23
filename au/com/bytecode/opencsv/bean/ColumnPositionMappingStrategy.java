/*    */ package au.com.bytecode.opencsv.bean;
/*    */ 
/*    */ import au.com.bytecode.opencsv.CSVReader;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ColumnPositionMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T>
/*    */ {
/* 23 */   protected String[] columnMapping = new String[0];
/*    */ 
/*    */   public void captureHeader(CSVReader reader) throws IOException {
/*    */   }
/*    */   protected String getColumnName(int col) {
/* 28 */     return (null != this.columnMapping) && (col < this.columnMapping.length) ? this.columnMapping[col] : null;
/*    */   }
/*    */   public String[] getColumnMapping() {
/* 31 */     return this.columnMapping;
/*    */   }
/*    */   public void setColumnMapping(String[] columnMapping) {
/* 34 */     this.columnMapping = columnMapping;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy
 * JD-Core Version:    0.6.2
 */