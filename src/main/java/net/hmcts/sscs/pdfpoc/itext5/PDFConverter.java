package net.hmcts.sscs.pdfpoc.itext5;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;

/**
 * Take another document format e.g. JPEG and convert to PDF
 */
public class PDFConverter {

    public void jpegToPDF(String inputPath, String outputPath) throws Exception {
        Image image = Image.getInstance(inputPath);

        Document document = new Document();
        @SuppressWarnings("unused")
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();

        // NB may need to process the source image to find the right orientation
        // for the test image it needs rotating 90 degrees
        image.setRotationDegrees(-90);

        // need to make the image fit https://stackoverflow.com/a/11121655
        // NB this needs to be more sophisticated to check both width and height, depending on
        // the orientation of the image
        int indentation = 0;
        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                - document.rightMargin() - indentation) / image.getHeight()) * 100; // or image.getWidth() if not rotated

        image.scalePercent(scaler);

        document.add(image);
        document.close();
    }
}
