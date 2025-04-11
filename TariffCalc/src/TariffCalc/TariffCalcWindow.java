package TariffCalc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class TariffCalcWindow extends JFrame implements ActionListener {
    final static int DE_MINIMIS = 800; // Imports tax, duty, tariff-free with
                                       // with exception to China...
    // Database stuff
    String url = getDatabaseURL();
    Connection con;
    Statement stmt;

    // Labels and maps
    Container c;
    JLabel labelIntro = new JLabel("Calculate the tariffs you might have to pay!");
    JLabel selectPrompt = new JLabel("Select a country from the list:  ");
    JLabel inputPricePrompt = new JLabel("Enter the price of your imported items:  ");
    JTextField inputPrice = new JTextField(50);
    JButton calculateButton = new JButton("Calculate");
    JLabel tariffPaidLabel = new JLabel("Tariffs to pay:   ");
    JLabel totalPaidLabel = new JLabel("Total paid:   ");
    JTextField tariffPaid = new JTextField(50);
    JTextField totalPaid = new JTextField(50);
    JComboBox countryBox;
    String[] countryArray;
    JPanel countryPanel = new JPanel();
    JPanel calcPanel = new JPanel();
    HashMap<Integer, Integer> tariffsRates = new HashMap<Integer, Integer>();
    HashMap<Integer, String> countries = new HashMap<Integer, String>();

    /*
     * The Window
     */
    public TariffCalcWindow() {
        setTitle("Tariff Calculator");
        setSize(550, 460);
        Font appFont = new Font("Dialog", Font.BOLD, 16);
        c = getContentPane();
        c.setLayout(new BorderLayout());
        c.setBackground(Color.LIGHT_GRAY);
        c.setFont(appFont);
        // get the countries and rates and place them in our hashmaps
        try {
            getCountries();
        } catch (Exception sqlerr) {
            sqlerr.printStackTrace();
        }
        setCountries();
        countryBox = new JComboBox(countryArray);

        countryPanel = new JPanel(new GridLayout(3, 1));
        countryPanel.setFont(appFont);
        labelIntro.setFont(appFont);
        selectPrompt.setFont(appFont);
        countryBox.setFont(appFont);
        countryPanel.add(labelIntro);
        countryPanel.add(selectPrompt);
        countryPanel.add(countryBox);

        //calcPanel = new JPanel(new GridLayout(3, 1));
        calcPanel = new JPanel(null);
        calcPanel.setFont(appFont);
        inputPricePrompt.setBounds(20, 20, 300, 50);
        inputPrice.setBounds(330, 20, 190, 50);
        calculateButton.setBounds(330, 250, 150, 50);

        calcPanel.add(inputPricePrompt);
        calcPanel.add(inputPrice);
        calcPanel.add(calculateButton);

        tariffPaidLabel.setBounds(20, 100, 150, 50);
        tariffPaid.setBounds(130, 100, 389, 50);
        totalPaidLabel.setBounds(20, 170, 150, 50);
        totalPaid.setBounds(130, 170, 389, 50);

        inputPrice.setFont(appFont);
        inputPricePrompt.setFont(appFont);
        calculateButton.setFont(appFont);
        tariffPaid.setFont(appFont);
        tariffPaidLabel.setFont(appFont);
        totalPaid.setFont(appFont);
        totalPaidLabel.setFont(appFont);

        calcPanel.add(tariffPaidLabel);
        calcPanel.add(tariffPaid);
        calcPanel.add(totalPaidLabel);
        calcPanel.add(totalPaid);

        tariffPaid.setEditable(false);
        totalPaid.setEditable(false);

        calculateButton.addActionListener(this);

        c.add(countryPanel, BorderLayout.NORTH);
        c.add(calcPanel, BorderLayout.CENTER);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /*
     * Put the countries from the hashmap into an array of
     * the size of our hashmap...
     */
    public void setCountries() {
        countryArray = new String[countries.size()];
        for (int i = 0; i < countries.size(); i++) {
            countryArray[i] = countries.get(i+1);
        }
    }

    /*
     * Parse through the database and put the list of countries
     * and rates into our hashmaps...
     */
    public void getCountries() throws SQLException {
        con = DriverManager.getConnection(url, "", "");
        //stmt = con.createStatement();
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String query = "select * from tariff_rates ";
        ResultSet rs = stmt.executeQuery(query);

        String name;
        int num, counter = 1;
        while(rs.next()) {
            name = rs.getString("Name");
            num = rs.getInt("Num");
            tariffsRates.put(counter, num);
            countries.put(counter, name);
            counter++;
        }
    }

    public static String getDatabaseURL() {
        URL jarLocation = TariffCalcWindow.class.getProtectionDomain().getCodeSource().getLocation();
        if (jarLocation == null) {
            System.err.println("Jar location not found!");
            return null;
        }

        File jarFile = new File(jarLocation.getPath());
        File jarDir = jarFile.getParentFile();
        File dbFile = new File(jarDir, "TariffRates.accdb");

        String url = "jdbc:ucanaccess://" + dbFile.getAbsolutePath();
        return url;
    }

    /*
     * This method contains the logic that performs the calculation
     */
    public void calculatePrice() {
        String nation = countryBox.getSelectedItem().toString();
        double rate = (double) tariffsRates.get(countryBox.getSelectedIndex() + 1) / 100;
        double tariffPrice;
        double totalPricePaid;
        double currentPrice = Double.parseDouble(inputPrice.getText());

        if(currentPrice < DE_MINIMIS && !nation.equals("China")) {
            tariffPrice = 0;
            totalPricePaid = currentPrice + tariffPrice;
            totalPricePaid = Math.round(totalPricePaid * 100.0) / 100.0;
            tariffPaid.setText("Under the De Minimis - No tariffs");
            totalPaid.setText("Total paid: $" + totalPricePaid);
        } else {
            tariffPrice = Math.round((currentPrice / rate) * 100.0) / 100.0;
            totalPricePaid = Math.round((currentPrice + tariffPrice) * 100.0) / 100.0;
            tariffPaid.setText("You'll pay $" + tariffPrice);
            totalPaid.setText("Total paid: $" + totalPricePaid);
        }
    }

    /*
     * What happens when we click the button.
     */
    @Override
    public void actionPerformed(ActionEvent e) throws IllegalArgumentException {
        try {
            calculatePrice(); // Logic calculation
        } catch (NumberFormatException nfe) { // errors
            tariffPaid.setText("Not a valid number!");
            System.out.println("Not a number!");
            nfe.printStackTrace();
        } catch (Exception err) {
            System.out.println("Unknown error!");
            err.printStackTrace();
        }
    }
}
