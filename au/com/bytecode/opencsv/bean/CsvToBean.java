/*     */ package au.com.bytecode.opencsv.bean;
/*     */ 
/*     */ import au.com.bytecode.opencsv.CSVReader;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.beans.PropertyEditor;
/*     */ import java.beans.PropertyEditorManager;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CsvToBean<T>
/*     */ {
/*  33 */   Map<Class<?>, PropertyEditor> editorMap = null;
/*     */ 
/*     */   public List<T> parse(MappingStrategy<T> mapper, Reader reader)
/*     */   {
/*  39 */     return parse(mapper, new CSVReader(reader));
/*     */   }
/*     */ 
/*     */   public List<T> parse(MappingStrategy<T> mapper, CSVReader csv) {
/*     */     try {
/*  44 */       mapper.captureHeader(csv);
/*     */ 
/*  46 */       List list = new ArrayList();
/*     */       String[] line;
/*  47 */       while (null != (line = csv.readNext())) {
/*  48 */         Object obj = processLine(mapper, line);
/*  49 */         list.add(obj);
/*     */       }
/*  51 */       return list;
/*     */     } catch (Exception e) {
/*  53 */       throw new RuntimeException("Error parsing CSV!", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected T processLine(MappingStrategy<T> mapper, String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
/*  58 */     Object bean = mapper.createBean();
/*  59 */     for (int col = 0; col < line.length; col++) {
/*  60 */       String value = line[col];
/*  61 */       PropertyDescriptor prop = mapper.findDescriptor(col);
/*  62 */       if (null != prop) {
/*  63 */         Object obj = convertValue(value, prop);
/*  64 */         prop.getWriteMethod().invoke(bean, new Object[] { obj });
/*     */       }
/*     */     }
/*  67 */     return bean;
/*     */   }
/*     */ 
/*     */   protected Object convertValue(String value, PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
/*  71 */     PropertyEditor editor = getPropertyEditor(prop);
/*  72 */     Object obj = value;
/*  73 */     if (null != editor) {
/*  74 */       editor.setAsText(value.trim());
/*  75 */       obj = editor.getValue();
/*     */     }
/*  77 */     return obj;
/*     */   }
/*     */ 
/*     */   private PropertyEditor getPropertyEditorValue(Class<?> cls)
/*     */   {
/*  82 */     if (this.editorMap == null)
/*     */     {
/*  84 */       this.editorMap = new HashMap();
/*     */     }
/*     */ 
/*  87 */     PropertyEditor editor = (PropertyEditor)this.editorMap.get(cls);
/*     */ 
/*  89 */     if (editor == null)
/*     */     {
/*  91 */       editor = PropertyEditorManager.findEditor(cls);
/*  92 */       addEditorToMap(cls, editor);
/*     */     }
/*     */ 
/*  95 */     return editor;
/*     */   }
/*     */ 
/*     */   private void addEditorToMap(Class<?> cls, PropertyEditor editor)
/*     */   {
/* 100 */     if (editor != null)
/*     */     {
/* 102 */       this.editorMap.put(cls, editor);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected PropertyEditor getPropertyEditor(PropertyDescriptor desc)
/*     */     throws InstantiationException, IllegalAccessException
/*     */   {
/* 111 */     Class cls = desc.getPropertyEditorClass();
/* 112 */     if (null != cls) return (PropertyEditor)cls.newInstance();
/* 113 */     return getPropertyEditorValue(desc.getPropertyType());
/*     */   }
/*     */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     au.com.bytecode.opencsv.bean.CsvToBean
 * JD-Core Version:    0.6.2
 */