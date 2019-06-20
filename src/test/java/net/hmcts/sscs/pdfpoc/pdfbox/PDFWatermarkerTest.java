package net.hmcts.sscs.pdfpoc.pdfbox;

import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.junit.Test;

import static org.junit.Assert.*;

public class PDFWatermarkerTest {

    @Test
    public void shrinkAndWatermarkPDF() throws Exception {
        PDFWatermarker pw = new PDFWatermarker();
        pw.shrinkAndWatermarkPDF(
                "DL6-pdfbox.PDF",
                "DL6-pdfbox-wm.PDF",
                "Appellant Further Evidence | Appendix  A");
    }
}