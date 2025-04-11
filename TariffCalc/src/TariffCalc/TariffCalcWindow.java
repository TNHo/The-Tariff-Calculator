package TariffCalc;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class TariffCalcWindow extends JFrame {
    String url = getDatabaseURL();
    Connection con;
    Statement stmt;
    Container c;
    JLabel labelIntro = new JLabel("Calculate the tariffs you might have to pay!");
    JComboBox countryBox = new JComboBox();
    HashMap<Integer, Integer> tariffsRates = new HashMap<Integer, Integer>();
    HashMap<Integer, String> countries = new HashMap<Integer, String>();

    public TariffCalcWindow() {
        setTitle("Tariff Calculator");
        setSize(500, 500);
        c = getContentPane();
        c.setLayout(new FlowLayout());
        c.setBackground(Color.LIGHT_GRAY);
        // get the countries and rates and place them in our hashmaps
        try {
            getCountries();
        } catch (Exception sqlerr) {
            sqlerr.printStackTrace();
        }
        c.add(labelIntro);
        c.add(countryBox);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

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
            System.out.println(tariffsRates.get(counter) + " " + countries.get(counter) + " ");
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
}
