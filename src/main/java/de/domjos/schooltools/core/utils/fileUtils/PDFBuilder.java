/*
 * Copyright (C) 2017-2018  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */
package de.domjos.schooltools.core.utils.fileUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.domjos.schooltools.R;
import de.domjos.schooltools.helper.Converter;
import de.domjos.schooltools.helper.Helper;

/**
 * Class to create a PDF-File
 * @author Dominic Joas
 * @version 1.0
 */
public class PDFBuilder {
    private Document document;
    private Map<String, Font> fonts;

    public PDFBuilder(String path, Context context) throws Exception {
        this.document = new Document();
        PdfWriter writer = PdfWriter.getInstance(this.document, new FileOutputStream(path));
        writer.setBoxSize("art", new Rectangle(55, 25, 550, 788));
        writer.setPageEvent(new Footer(context));
        this.document.open();
        this.fonts = new LinkedHashMap<>();
    }

    public void addFont(String key, Font.FontFamily fontFamily, float size, boolean bold, boolean italic, BaseColor color) {
        if(bold) {
            if(italic) {
                this.fonts.put(key, new Font(fontFamily, size, Font.BOLDITALIC, color));
            } else {
                this.fonts.put(key, new Font(fontFamily, size, Font.BOLD, color));
            }
        } else {
            if(italic) {
                this.fonts.put(key, new Font(fontFamily, size, Font.ITALIC, color));
            } else {
                this.fonts.put(key, new Font(fontFamily, size, Font.NORMAL, color));
            }
        }
    }

    public void addFont(String key, Font.FontFamily fontFamily, float size, BaseColor color) {
        this.addFont(key, fontFamily, size, false, false, color);
    }

    public void addFont(String key, Font.FontFamily fontFamily, float size) {
        this.addFont(key, fontFamily, size, false, false, BaseColor.BLACK);
    }

    public Map<String, Font> getFonts() {
        return this.fonts;
    }

    public void addMetaData(String title, String subject, String keywords, String author) {
        this.document.addTitle(title);
        this.document.addSubject(subject);
        this.document.addKeywords(keywords);
        this.document.addAuthor(author);
        this.document.addCreator(author);
    }

    public void addParagraph(String title, String content, String headerFontKey, String fontKey) throws Exception {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Paragraph(title, this.fonts.get(headerFontKey)));
        paragraph.add(new Paragraph(" "));
        paragraph.add(new Paragraph(content, this.fonts.get(fontKey)));
        this.document.add(paragraph);
    }

    public void addTitle(String title, String titleKey, int alignment) throws Exception {
        Paragraph paragraph = new Paragraph();
        Paragraph pg = new Paragraph(title, this.fonts.get(titleKey));
        pg.setAlignment(alignment);
        paragraph.add(pg);
        this.document.add(paragraph);
    }

    public void newPage() {
        this.document.newPage();
    }

    public void addTable(List<String> headers, float[] headerWidth, List<List<Map.Entry<String, BaseColor>>> cells) throws Exception {
        PdfPTable table = new PdfPTable(headers.size());
        if(headerWidth!=null) {
            table.setWidths(headerWidth);
        }

        for(String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 18, Font.BOLDITALIC)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for(List<Map.Entry<String, BaseColor>> row : cells) {
            for(Map.Entry<String, BaseColor> cellItem : row) {
                PdfPCell cell = new PdfPCell(new Phrase(cellItem.getKey(), new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(cellItem.getValue());
                table.addCell(cell);
            }
        }

        this.document.add(table);
    }

    public void addEmptyLine(int number) throws Exception {
        Paragraph paragraph = new Paragraph();
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
        this.document.add(paragraph);
    }

    public void close() {
        this.document.close();
    }

    public static void openPDFFile(String path, Context context) {
        try {
            File file = new File(path);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file),"application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");
            context.startActivity(intent);
        } catch (Exception ex) {
            Helper.printException(context, ex);
        }
    }
}

class Footer extends PdfPageEventHelper {
    private Context context;

    Footer(Context context) {
        this.context = context;
    }

    public void onEndPage(PdfWriter writer,Document document) {
        try {
            Rectangle rect = writer.getBoxSize("art");
            Image img = Image.getInstance(Converter.convertDrawableToByteArray(this.context, R.drawable.icon));
            Phrase phrase = new Phrase();
            phrase.add(new Chunk(img, 0, 0));
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, phrase, rect.getLeft(), rect.getBottom(), 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(String.valueOf(this.context.getString(R.string.api_page) + " " + document.getPageNumber())), rect.getRight(), rect.getBottom(), 0);
        } catch (Exception ex) {
            Helper.printException(context, ex);
        }
    }
}
