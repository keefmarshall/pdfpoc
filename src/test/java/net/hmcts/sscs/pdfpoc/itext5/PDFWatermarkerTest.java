package net.hmcts.sscs.pdfpoc.itext5;

import org.junit.Test;

public class PDFWatermarkerTest {

    @Test
    public void testShrinkAndWatermark() throws Exception {

        PDFWatermarker pw = new PDFWatermarker();
        pw.shrinkAndWatermarkPDF("DL6.PDF", "DL6-wm.PDF", "Further Evidence A");
    }
}