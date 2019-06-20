package net.hmcts.sscs.pdfpoc.itext5;

import org.junit.Test;

public class PDFConverterTest {

    @Test
    public void testJpegToPdf() throws Exception {
        PDFConverter conv = new PDFConverter();
        conv.jpegToPDF("src/main/resources/DL6.JPG", "DL6.PDF");
    }
}