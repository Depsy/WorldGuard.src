/*    */ package au.com.bytecode.opencsv.bean;
/*    */ 
/*    */ import au.com.bytecode.opencsv.CSVReader;
/*    */ import java.beans.BeanInfo;
/*    */ import java.beans.IntrospectionException;
/*    */ import java.beans.Introspector;
/*    */ import java.beans.PropertyDescriptor;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class HeaderColumnNameMappingStrategy<T>
/*    */   implements MappingStrategy<T>
/*    */ {
/*    */   protected String[] header;
/* 30 */   protected Map<String, PropertyDescriptor> descriptorMap = null;
/*    */   protected Class<T> type;
/*    */ 
/*    */   public void captureHeader(CSVReader reader)
/*    */     throws IOException
/*    */   {
/* 34 */     this.header = reader.readNext();
/*    */   }
/*    */ 
/*    */   public PropertyDescriptor findDescriptor(int col) throws IntrospectionException {
/* 38 */     String columnName = getColumnName(col);
/* 39 */     return (null != columnName) && (columnName.trim().length() > 0) ? findDescriptor(columnName) : null;
/*    */   }
/*    */ 
/*    */   protected String getColumnName(int col) {
/* 43 */     return (null != this.header) && (col < this.header.length) ? this.header[col] : null;
/*    */   }
/*    */   protected PropertyDescriptor findDescriptor(String name) throws IntrospectionException {
/* 46 */     if (null == this.descriptorMap) this.descriptorMap = loadDescriptorMap(getType());
/* 47 */     return (PropertyDescriptor)this.descriptorMap.get(name.toUpperCase().trim());
/*    */   }
/*    */   protected boolean matches(String name, PropertyDescriptor desc) {
/* 50 */     return desc.getName().equals(name.trim());
/*    */   }
/*    */ 
/*    */   protected Map<String, PropertyDescriptor> loadDescriptorMap(Class<T> cls) throws IntrospectionException
/*    */   {
/* 55 */     Map map = new HashMap();
/*    */ 
/* 58 */     PropertyDescriptor[] descriptors = loadDescriptors(getType());
/* 59 */     for (PropertyDescriptor descriptor : descriptors)
/*    */     {
/* 61 */       map.put(descriptor.getName().toUpperCase().trim(), descriptor);
/*    */     }
/*    */ 
/* 64 */     return map;
/*    */   }
/*    */ 
/*    */   private PropertyDescriptor[] loadDescriptors(Class<T> cls) throws IntrospectionException {
/* 68 */     BeanInfo beanInfo = Introspector.getBeanInfo(cls);
/* 69 */     return beanInfo.getPropertyDescriptors();
/*    */   }
/*    */   public T createBean() throws InstantiationException, IllegalAccessException {
/* 72 */     return this.type.newInstance();
/*    */   }
/*    */   public Class<T> getType() {
/* 75 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(Class<T> type) {
/* 79 */     this.type = type;
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy
 * JD-Core Version:    0.6.2
 */