/*    */ package au.com.bytecode.opencsv.bean;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class HeaderColumnNameTranslateMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T>
/*    */ {
/* 22 */   private Map<String, String> columnMapping = new HashMap();
/*    */ 
/* 24 */   protected String getColumnName(int col) { return (String)getColumnMapping().get(this.header[col]); }
/*    */ 
/*    */   public Map<String, String> getColumnMapping() {
/* 27 */     return this.columnMapping;
/*    */   }
/*    */   public void setColumnMapping(Map<String, String> columnMapping) {
/* 30 */     this.columnMapping = columnMapping;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy
 * JD-Core Version:    0.6.2
 */