package com.cvmanager.utils;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

/** Respuestas JSON pequeñas sin concatenar valores de usuario sin escape. */
public final class JsonResponse {
 private JsonResponse(){}
 public static boolean requested(HttpServletRequest req){String accept=req.getHeader("Accept");return accept!=null&&accept.contains("application/json");}
 public static void send(HttpServletResponse res,int status,Map<String,String> values)throws IOException {
  res.setStatus(status);res.setContentType("application/json");res.setCharacterEncoding("UTF-8");
  var json=new StringJoiner(",","{","}");values.forEach((k,v)->json.add(quote(k)+":"+quote(v)));res.getWriter().write(json.toString());
 }
 public static String quote(String value){
  if(value==null)return "null";
  var out=new StringBuilder("\"");for(int i=0;i<value.length();i++){char c=value.charAt(i);switch(c){case '"'->out.append("\\\"");case '\\'->out.append("\\\\");case '\n'->out.append("\\n");case '\r'->out.append("\\r");case '\t'->out.append("\\t");default->{if(c<32||c=='<'||c=='>'||c=='&'||c=='\u2028'||c=='\u2029')out.append(String.format("\\u%04x",(int)c));else out.append(c);}}}return out.append('"').toString();
 }
}
