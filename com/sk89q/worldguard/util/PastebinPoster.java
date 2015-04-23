/*    */ package com.sk89q.worldguard.util;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.OutputStream;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URL;
/*    */ import java.net.URLEncoder;
/*    */ 
/*    */ public class PastebinPoster
/*    */ {
/*    */   private static final int CONNECT_TIMEOUT = 5000;
/*    */   private static final int READ_TIMEOUT = 5000;
/*    */ 
/*    */   public static void paste(String code, PasteCallback callback)
/*    */   {
/* 36 */     PasteProcessor processor = new PasteProcessor(code, callback);
/* 37 */     Thread thread = new Thread(processor);
/* 38 */     thread.start();
/*    */   }
/*    */ 
/*    */   private static class PasteProcessor
/*    */     implements Runnable
/*    */   {
/*    */     private String code;
/*    */     private PastebinPoster.PasteCallback callback;
/*    */ 
/*    */     public PasteProcessor(String code, PastebinPoster.PasteCallback callback)
/*    */     {
/* 51 */       this.code = code;
/* 52 */       this.callback = callback;
/*    */     }
/*    */ 
/*    */     public void run() {
/* 56 */       HttpURLConnection conn = null;
/* 57 */       OutputStream out = null;
/* 58 */       InputStream in = null;
/*    */       try
/*    */       {
/* 61 */         URL url = new URL("http://pastebin.com/api/api_post.php");
/* 62 */         conn = (HttpURLConnection)url.openConnection();
/* 63 */         conn.setConnectTimeout(5000);
/* 64 */         conn.setReadTimeout(5000);
/* 65 */         conn.setRequestMethod("POST");
/* 66 */         conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
/*    */ 
/* 68 */         conn.setInstanceFollowRedirects(false);
/* 69 */         conn.setDoOutput(true);
/* 70 */         out = conn.getOutputStream();
/*    */ 
/* 72 */         out.write(new StringBuilder().append("api_option=paste&api_dev_key=").append(URLEncoder.encode("4867eae74c6990dbdef07c543cf8f805", "utf-8")).append("&api_paste_code=").append(URLEncoder.encode(this.code, "utf-8")).append("&api_paste_private=").append(URLEncoder.encode("0", "utf-8")).append("&api_paste_name=").append(URLEncoder.encode("", "utf-8")).append("&api_paste_expire_date=").append(URLEncoder.encode("1D", "utf-8")).append("&api_paste_format=").append(URLEncoder.encode("text", "utf-8")).append("&api_user_key=").append(URLEncoder.encode("", "utf-8")).toString().getBytes());
/*    */ 
/* 80 */         out.flush();
/* 81 */         out.close();
/*    */ 
/* 83 */         if (conn.getResponseCode() == 200) {
/* 84 */           in = conn.getInputStream();
/* 85 */           BufferedReader reader = new BufferedReader(new InputStreamReader(in));
/*    */ 
/* 87 */           StringBuilder response = new StringBuilder();
/*    */           String line;
/* 88 */           while ((line = reader.readLine()) != null) {
/* 89 */             response.append(line);
/* 90 */             response.append("\r\n");
/*    */           }
/* 92 */           reader.close();
/*    */ 
/* 94 */           String result = response.toString().trim();
/*    */ 
/* 96 */           if (result.matches("^https?://.*")) {
/* 97 */             this.callback.handleSuccess(result.trim());
/*    */           } else {
/* 99 */             String err = result.trim();
/* 100 */             if (err.length() > 100) {
/* 101 */               err = err.substring(0, 100);
/*    */             }
/* 103 */             this.callback.handleError(err);
/*    */           }
/*    */         } else {
/* 106 */           this.callback.handleError("didn't get a 200 response code!");
/*    */         }
/*    */       } catch (IOException e) {
/* 109 */         this.callback.handleError(e.getMessage());
/*    */       } finally {
/* 111 */         if (conn != null) {
/* 112 */           conn.disconnect();
/*    */         }
/* 114 */         if (in != null)
/*    */           try {
/* 116 */             in.close();
/*    */           }
/*    */           catch (IOException ignored) {
/*    */           }
/* 120 */         if (out != null)
/*    */           try {
/* 122 */             out.close();
/*    */           }
/*    */           catch (IOException ignored)
/*    */           {
/*    */           }
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static abstract interface PasteCallback
/*    */   {
/*    */     public abstract void handleSuccess(String paramString);
/*    */ 
/*    */     public abstract void handleError(String paramString);
/*    */   }
/*    */ }

/* Location:           C:\Users\Honza\Desktop\Nová složka\plugins\WorldGuard.jar
 * Qualified Name:     com.sk89q.worldguard.util.PastebinPoster
 * JD-Core Version:    0.6.2
 */