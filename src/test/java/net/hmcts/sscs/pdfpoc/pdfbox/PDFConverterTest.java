package net.hmcts.sscs.pdfpoc.pdfbox;

import org.junit.Test;

import static org.junit.Assert.*;

public class PDFConverterTest {
    @Test
    public void testJpegToPDF() throws Exception {
        PDFConverter conv = new PDFConverter();
        conv.jpegToPDF("src/main/resources/DL6.JPG", "DL6-pdfbox.PDF");
    }

}