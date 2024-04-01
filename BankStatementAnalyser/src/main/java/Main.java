import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The Main class for the Bank Statement Analyzer and Tax Calculator application.
 * It interacts with the user to input file paths of bank statements and non-taxable keywords,
 * then processes these statements to calculate taxable income and tax payable.
 */
public class Main {
    public static void main(String[] args) {

        double taxableIncome = 0.0;

        // Use a loop to continuously process bank statements until the user decides to quit.
        while(true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the file path of your bank statement.");
            System.out.println("Otherwise, type q and enter to quit the program.");
            String filePath = scanner.nextLine(); // Reads the entire input line from the console
            // Check if the user wants to quit the application.
            if(filePath.equals("q")) {
                scanner.close();
                return;
            }

            // Predefined list of non-taxable keywords, which can be extended based on user input.
            List<String> nonTaxableKeywords = new ArrayList<>(Arrays.asList("Asg", "asg", "bet",
                    "Bet", "tab", "Tab", "Sport", "sport", "Azupay", "Client", "Rwwa", "lif",
                    "Lif", "uni", "Uni"));
            System.out.println("Enter keywords for non-taxable items (type 'done' to finish):");
            String input;
            while (!(input = scanner.nextLine()).equalsIgnoreCase("done")) {
                // Add both the user's input and its capitalised version to cover case variations.
                nonTaxableKeywords.add(input.substring(0,1).toUpperCase() + input.substring(1));
                nonTaxableKeywords.add(input);
            }

            // Read the PDF file specified by the user, process it, and update the taxable income.
            taxableIncome += ReadPdfFile.readPdf(filePath, nonTaxableKeywords);
            double taxPayable = CalculateTaxPayable.taxCalculator(taxableIncome);
            System.out.println("Current taxable income is: "+taxableIncome);
            System.out.println("Current tax owing: " + String.format("%.2f", taxPayable));
        }
    }
}