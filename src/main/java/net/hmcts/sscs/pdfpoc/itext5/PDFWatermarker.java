package net.hmcts.sscs.pdfpoc.itext5;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;

/**
 * Scale page by 95% and add watermark at base of page
 * see:
 * https://itextpdf.com/en/resources/faq/technical-support/itext-5-legacy/how-shrink-pages-existing-pdf
 * (second solution - link here:
 * https://github.com/itext/i5js-sandbox/blob/master/src/main/java/sandbox/stamper/ShrinkPdf2.java
 */
public class PDFWatermarker {

    public void shrinkAndWatermarkPDF(String inputPath, String outputPath, String watermarkText) throws Exception {
        PdfReader reader = new PdfReader(inputPath);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath));
        int n = reader.getNumberOfPages();
        float percentage = 0.95f;
        for (int p = 1; p <= n; p++) {
            float offsetX = (reader.getPageSize(p).getWidth() * (1 - percentage)) / 2;
            float offsetY = 0; // keep at top of page
//            float offsetY = (reader.getPageSize(p).getHeight() * (1 - percentage)) / 2;

            PdfContentByte content = stamper.getUnderContent(p);

            // Add watermark:
            BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ITALIC, BaseFont.CP1250, BaseFont.EMBEDDED);
            content.beginText();
            content.setFontAndSize(bf, 18);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, watermarkText, 250, 50,0);
            content.endText();

            // Add a literal PDF instruction to scale the content
            content.setLiteral(
                    String.format("\nq %s 0 0 %s %s %s cm\nq\n", percentage, percentage, offsetX, offsetY));
            // This sets the scaling back to normal for the next instruction / next page:
            stamper.getOverContent(p).setLiteral("\nQ\nQ\n");

        }
        stamper.close();
        reader.close();
    }
}
