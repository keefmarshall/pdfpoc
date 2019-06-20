package net.hmcts.sscs.pdfpoc.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PDFWatermarker {

    private final static float SCALE_PERCENTAGE = .95f;
    private final static int margin = 50;

    public void shrinkAndWatermarkPDF(String inputPath, String outputPath, String watermarkText) throws Exception {
        try(PDDocument document = PDDocument.load(new File(inputPath))) {

            int count = 1;
            for (PDPage page : document.getPages()) {
                scaleContent(document, page, SCALE_PERCENTAGE);
                addFooterText(document, page, watermarkText + "        " + count++);
            }

            PDFACompliance p1a = new PDFACompliance();
            p1a.makeCompliant(document);

            document.save(outputPath);
        }
    }

    // TODO amend this to be appropriately styled / designed / spaced - also bear in mind lower margins
    // need to be in place for bulk print support which will define the text positioning.
    private void addFooterText(PDDocument document, PDPage page, String text)  throws IOException {

        // NB we need to embed the font for PDF/A compliance.
        // Loading it here causes it to be embedded into the resulting PDF doc.
        // Note this TTF file is included in pdfbox 2.x, there don't seem to be any others available
        // - the running code can load form the deployed system but if this is likely to be a Docker image there's
        // no guarantee it will have any TTF fonts present. If we want something else we'll likely have to embed
        // it into the code base, and be really careful about licensing.
        InputStream fontStream = this.getClass().getResourceAsStream(
                "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf");
        PDFont font = PDType0Font.load(document, fontStream, true);

        int fontSize = 12;

        float textWidth = (font.getStringWidth(text) / 1000.0f) * fontSize;
        float xOffset = page.getMediaBox().getWidth() - margin - textWidth;

        try (PDPageContentStream contentStream =
                     new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(0);
            contentStream.beginText();
            contentStream.newLineAtOffset(xOffset, margin);
            contentStream.showText(text);
            contentStream.endText();
        }

    }

    /**
     * This solution duplicates what the examples do in iText, which is to write a raw scaling command
     * directly into the PDF, and then write a final command to cancel the scaling.
     *
     * However, PDFBox has actual calls to replicate this, you don't have to write raw commands.
     *
     * @param document
     * @param page
     * @param percentage
     * @throws IOException
     */
    private void scaleContent(PDDocument document, PDPage page, float percentage) throws IOException {
        float offsetX = (page.getCropBox().getWidth() * (1 - percentage)) / 2; // centre horizontally
        float offsetY = (page.getCropBox().getHeight() * (1 - percentage)); // anchor the top, creates space for footer

        // First create a content stream before all others, add a matrix transformation to scale:
        try (PDPageContentStream contentStream =
                new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND, false)) {
            contentStream.saveGraphicsState(); // 'q' in PDF commands
            contentStream.transform(new Matrix(percentage, 0, 0, percentage, offsetX, offsetY));
            contentStream.saveGraphicsState();
        }

        // Now add a closing command to remove the scale effect by restoring the graphics states:
        try (PDPageContentStream contentStream =
                    new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false)) {
            // In raw PDF this equates to: "\nQ\nQ\n" - we saved it twice so we have to restore twice
            contentStream.restoreGraphicsState();
            contentStream.restoreGraphicsState();
        }
    }

}
