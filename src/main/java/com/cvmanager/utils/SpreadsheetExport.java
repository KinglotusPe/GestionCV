package com.cvmanager.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;

/** Libro XLSX de una hoja, sin fórmulas: el contenido de usuario siempre es texto. */
public final class SpreadsheetExport {
    private SpreadsheetExport(){}
    public static void write(OutputStream output,List<Map<String,Object>> rows)throws IOException {
        try(ZipOutputStream zip=new ZipOutputStream(output,StandardCharsets.UTF_8)) {
            entry(zip,"[Content_Types].xml","<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/></Types>");
            entry(zip,"_rels/.rels","<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>");
            entry(zip,"xl/workbook.xml","<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"Reporte\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            entry(zip,"xl/_rels/workbook.xml.rels","<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/></Relationships>");
            StringBuilder sheet=new StringBuilder("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
            if(!rows.isEmpty()){row(sheet,rows.get(0).keySet());for(var row:rows)row(sheet,row.values());}
            sheet.append("</sheetData></worksheet>");entry(zip,"xl/worksheets/sheet1.xml",sheet.toString());
        }
    }
    private static void row(StringBuilder xml,Collection<?> cells){xml.append("<row>");for(Object value:cells)xml.append("<c t=\"inlineStr\"><is><t xml:space=\"preserve\">").append(escape(Objects.toString(value,""))).append("</t></is></c>");xml.append("</row>");}
    private static String escape(String text){return text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]","").replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");}
    private static void entry(ZipOutputStream zip,String name,String xml)throws IOException{zip.putNextEntry(new ZipEntry(name));zip.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+xml).getBytes(StandardCharsets.UTF_8));zip.closeEntry();}
}
