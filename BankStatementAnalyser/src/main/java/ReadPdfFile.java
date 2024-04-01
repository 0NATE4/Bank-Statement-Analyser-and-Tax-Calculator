import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * A utility class for reading PDF files and extracting text for further processing.
 */
public class ReadPdfFile {

    /**
     * Reads a PDF file from a specified path, extracts its text content,
     * and processes it to calculate financial summaries including taxable income.
     *
     * @param bankFile The name of the PDF file to be read. Assumes the file is located in a
     *                 predefined directory.
     * @param nonTaxableKeywords A list of keywords to identify non-taxable transactions within
     *                           the bank statement.
     * @return The total taxable income calculated from the extracted bank statement text,
     * or 0.0 if an error occurs.
     */
    public static double readPdf (String bankFile, List<String> nonTaxableKeywords) {

        // Constructs the full path to the PDF file based on a predefined directory.
        String bankPath = "C:\\Users\\navin\\OneDrive\\Desktop\\2024\\bank\\"+bankFile;
        File file = new File(bankPath);

        try (PDDocument document = PDDocument.load(file)) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                // Processes the extracted text for financial analysis and returns
                // the calculated taxable income.
                return BankStatementProcessor.processBankStatement(text, nonTaxableKeywords);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0; // Returns 0.0 if an error occurs or if the document is encrypted.
    }
}
