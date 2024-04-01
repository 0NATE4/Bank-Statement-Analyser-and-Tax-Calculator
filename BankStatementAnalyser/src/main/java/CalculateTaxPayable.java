/**
 * Provides functionality to calculate tax payable based on Australian tax brackets.
 */
public class CalculateTaxPayable {

    /**
     * Defines tax brackets for calculating tax payable.
     */
    public interface TaxBrackets {
        double TAX_FREE_THRESHOLD = 18200.0;
        double BRACKET_ONE = 45000.0;
        double BRACKET_TWO = 120000.0;
        double BRACKET_THREE = 180000.0;
    }

    /**
     * Defines the tax amount at the upper limit of each tax bracket.
     */
    public interface TaxAmounts {
        double TAX_ONE = 5092.0;
        double TAX_TWO = 29467.0;
        double TAX_THREE = 51667.0;
    }

    /**
     * Calculates the tax payable based on the given taxable income and the defined tax brackets.
     *
     * @param taxableIncome The taxable income for which to calculate tax.
     * @return The amount of tax payable.
     */
    public static double taxCalculator(double taxableIncome) {
        if(isOverThreshold(taxableIncome)) {
            if(isOverFirstBracket(taxableIncome)) {
                if(isOverSecondBracket(taxableIncome)) {
                    if(isOverThirdBracket(taxableIncome)) {
                        return (taxableIncome - TaxBrackets.BRACKET_THREE) * 0.45 + TaxAmounts.TAX_THREE;
                    }
                    return (taxableIncome - TaxBrackets.BRACKET_TWO) * 0.37 + TaxAmounts.TAX_TWO;
                }
                return (taxableIncome - TaxBrackets.BRACKET_ONE) * 0.325 + TaxAmounts.TAX_ONE;
            }
            return (taxableIncome - TaxBrackets.TAX_FREE_THRESHOLD) * 0.19;
        }
        return 0.0;
    }

    /**
     * Checks if taxable income is over the tax-free threshold.
     *
     * @param taxableIncome The taxable income to check.
     * @return true if taxable income is over the tax-free threshold; false otherwise.
     */
    public static boolean isOverThreshold(double taxableIncome) {
        return taxableIncome > TaxBrackets.TAX_FREE_THRESHOLD;
    }

    /**
     * Checks if taxable income is over the first bracket limit.
     *
     * @param taxableIncome The taxable income to check.
     * @return true if taxable income is over the first bracket limit; false otherwise.
     */
    public static boolean isOverFirstBracket(double taxableIncome) {
        return taxableIncome > TaxBrackets.BRACKET_ONE;
    }

    /**
     * Checks if taxable income is over the second bracket limit.
     *
     * @param taxableIncome The taxable income to check.
     * @return true if taxable income is over the second bracket limit; false otherwise.
     */
    public static boolean isOverSecondBracket(double taxableIncome) {
        return taxableIncome > TaxBrackets.BRACKET_TWO;
    }

    /**
     * Checks if taxable income is over the third bracket limit.
     *
     * @param taxableIncome The taxable income to check.
     * @return true if taxable income is over the third bracket limit; false otherwise.
     */
    public static boolean isOverThirdBracket(double taxableIncome) {
        return taxableIncome > TaxBrackets.BRACKET_THREE;
    }

}
