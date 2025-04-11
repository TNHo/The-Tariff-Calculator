/*--------------------------------------------------------------------
 * Tariff Calculator
 *
 * By Tan Ho (TNHo)
 *
 * -------------------------------------------------------------------
 *
 * In today's political climate, trade wars are starting and everything's
 * about to get hella alot more expensive. We are not naming names here...
 *
 * But while we're wallowing in miserie, we need a tool to calculate our
 * price increase.
 *
 * That's where I come in!
 *
 * Even though you could just use your calculator or go to a random website,
 * you can do this instead even though it ain't the optimal solution!
 *
 * We basically grab the rates from an access database to do our calculations from.
 * Hardcoding the rates ain't good, as its volatile in today's political climate.
 * Better to feed this updated extra files than update the code every week...
 *
 * -------------------------------------------------------------------
 */
package TariffCalc;

public class TariffCalcMain {
    public static void main(String[] args) {
        // The runner
        TariffCalcWindow window = new TariffCalcWindow();
    }
}
