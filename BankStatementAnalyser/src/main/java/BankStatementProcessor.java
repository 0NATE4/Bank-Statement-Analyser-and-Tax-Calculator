import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Processes bank statements to analyze financial transactions, calculate taxable income,
 * and determine total credits, debits, and overall balance.
 */
public class BankStatementProcessor {

    private static final LocalDate START_DATE = LocalDate.of(2023, 7, 1); // Start of financial
    // year.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    /**
     * Processes the given bank statement text to calculate and print financial summaries.
     *
     * @param pdfText The bank statement text.
     * @param nonTaxableKeywords List of keywords for non-taxable transactions.
     * @return The total taxable income calculated from the bank statement.
     */
    public static double processBankStatement(String pdfText, List<String> nonTaxableKeywords) {
        String[] lines = pdfText.split("\n");
        double openingBalance = findOpeningBalance(lines);
        double totalCredits = 0.0;
        double totalDebits = 0.0;
        double taxableIncome = 0.0;
        boolean shouldProcessNextNumber = false;
        boolean isCreditTransaction = false; // Additional flag to identify transaction type

        // Iterates through each line of the bank statement for processing.
        for (int i = 0; i < lines.length-1; i++) {
            String line = lines[i];
            String nextLine = lines[i+1];

            if (creditChecker(line)) {
                shouldProcessNextNumber = true; // Set flag when keyword is found
                isCreditTransaction = true; // It's a credit transaction
            } else if (debitChecker(line)) {
                shouldProcessNextNumber = true;
                isCreditTransaction = false; // It's a debit transaction
            }
            if (shouldProcessNextNumber) {
                String[] parts = line.split("\\s+");
                String[] nextLineParts = nextLine.split("\\s+");
                if (lineProcessChecker(nextLineParts[0])) {
                    for (String part : parts) { // Iterate parts to find the first matching number
                        String sanitisedPart = part.replace(",", "");
                        if (sanitisedPart.matches("\\d+\\.\\d+")) { // Matches only decimal numbers
                            double amount = Double.parseDouble(sanitisedPart);
                            if (isCreditTransaction) {
                                totalCredits += amount;
                                if(isTaxable(line, nonTaxableKeywords)) {
                                    taxableIncome += amount;
                                    printStatementLine(parts);
                                }
                            } else {
                                totalDebits += amount;
                            }
                            shouldProcessNextNumber = false; // Reset flag after processing number
                            break;
                        }
                    }
                }
            }
        }
        printStatementSummary(openingBalance, totalCredits, totalDebits, taxableIncome);
        return taxableIncome;

    }

    /**
     * Checks if a line from the bank statement represents a credit transaction.
     * Credit transactions are determined based on specific keywords and if the transaction date
     * is after a set start date.
     *
     * @param line The line from the bank statement to check.
     * @return true if the line represents a credit transaction; false otherwise.
     */
    private static boolean creditChecker(String line) {
        line = line.trim();
        if(line.isEmpty()) {
            return false;
        }
        String lowerCaseLine = line.toLowerCase();
        String[] dateCheck = lowerCaseLine.split("\\s+");
        if(!dateCheck[0].matches("^\\d{2}/\\d{2}/\\d{2}$") || !isDateAfterOrEqual(dateCheck[0])) {
            return false;
        }
        return (lowerCaseLine.contains("deposit")
                || lowerCaseLine.contains("refund")
                || lowerCaseLine.contains("credit")
                || lowerCaseLine.contains("interest"));
    }

    /**
     * Checks if a line from the bank statement represents a debit transaction.
     * Debit transactions are identified by specific keywords and if the transaction date is
     * after a set start date.
     *
     * @param line The line from the bank statement to check.
     * @return true if the line represents a debit transaction; false otherwise.
     */
    private static boolean debitChecker(String line) {
        line = line.trim();
        if(line.isEmpty()) {
            return false;
        }
        String lowerCaseLine = line.toLowerCase();
        String[] dateCheck = lowerCaseLine.split("\\s+");
        if(!dateCheck[0].matches("^\\d{2}/\\d{2}/\\d{2}$") || !isDateAfterOrEqual(dateCheck[0])) {
            return false;
        }
        return (lowerCaseLine.contains("debit") || lowerCaseLine.contains("withdrawal"));

    }

    /**
     * Determines if a line should be processed based on its content.
     * A line is eligible for processing if it starts with a date, is empty, or contains
     * specific keywords.
     *
     * @param line The line to be checked.
     * @return true if the line should be processed; false otherwise.
     */
    private static boolean lineProcessChecker(String line) {
        return line.matches("^\\d{2}/\\d{2}/\\d{2}$")
                || line.isEmpty()
                || line.equals("MR")
                || line.equals("MRS")
                || line.equals("MISS")
                || line.equals("DR")
                || line.equals("Use");
    }

    /**
     * Finds the opening balance from a list of lines from the bank statement.
     * Searches for a line containing "Opening Balance" and extracts the numerical value.
     *
     * @param lines Array of lines from the bank statement.
     * @return The opening balance as a double. Returns 0.0 if not found.
     */
    private static double findOpeningBalance(String[] lines) {
        for (String line : lines) {
            if (line.contains("Opening Balance")) {
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    if (part.contains("$")) {
                        String sanitisedOpening = part.replace("$", "");
                        sanitisedOpening = sanitisedOpening.replace(",", "");
                        return Double.parseDouble(sanitisedOpening);
                    }
                }
            }
        }
        return 0.0; // Default to 0.0 if opening balance is not found
    }

    /**
     * Checks if a line from the bank statement represents taxable income based on the presence
     * of non-taxable keywords.
     *
     * @param line The line from the bank statement to check.
     * @param nonTaxableKeywords A list of keywords that denote non-taxable transactions.
     * @return true if the line represents taxable income; false otherwise.
     */
    private static boolean isTaxable(String line, List<String> nonTaxableKeywords) {
        for (String keyword : nonTaxableKeywords) {
            if (line.contains(keyword)) {
                return false; // Line contains a non-taxable keyword, so it's not taxable
            }
        }
        return true; // No non-taxable keyword found, so it's taxable
    }


    /**
     * Compares a given date string against a predefined start date to determine if it's on or
     * after the start date.
     *
     * @param dateString The date string to compare, expected in the format "dd/MM/yy".
     * @return true if the date is on or after the start date; false if before or if the date
     * string is invalid.
     */
    private static boolean isDateAfterOrEqual(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);
            return !date.isBefore(START_DATE);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + dateString);
            return false;
        }
    }


    /**
     * Prints a single statement line, excluding the last two parts which
     * typically represent amount and balance.
     *
     * @param parts The split line of text representing a transaction.
     */
    private static void printStatementLine(String[] parts) {
        String[] allButLast2 = Arrays.copyOfRange(parts, 0,
                parts.length - 2);

        String formattedLine = String.join(" ", allButLast2);

        System.out.println(formattedLine + " $"+parts[parts.length-2]);
    }

    /**
     * Prints a summary of the financial analysis including opening balance, total credits and
     * debits, and overall balance.
     *
     * @param openingBalance The opening balance extracted from the bank statements.
     * @param totalCredits The total of credit transactions.
     * @param totalDebits The total of debit transactions.
     * @param taxableIncome The calculated taxable income.
     */
    private static void printStatementSummary(double openingBalance, double totalCredits,
                                              double totalDebits, double taxableIncome) {
        System.out.println("Opening Balance: $" + String.format("%.2f", openingBalance));
        System.out.println("Total Credits: $" + String.format("%.2f", totalCredits));
        System.out.println("Total Debits: $" + String.format("%.2f", totalDebits));
        System.out.println("Overall Balance: $" + String.format("%.2f",
                (openingBalance + totalCredits - totalDebits)));
        System.out.println("Total Taxable: $" + String.format("%.2f", taxableIncome));
    }
}
