import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/*
 * This program scrapes the stats of NBA players drafted in 2014
 * (courtesy of Kashan Madav on GitHub) and produces a ranking of each
 * player according to an algorithm designed by yours truly.
 *
 * Basically, this program creates a "Second-year Player of the Year
 * Award." It has a nice ring to it, no?
 */

public final class JsoupRun implements Comparable<Double> {

    private JsoupRun() {
    }

    /**
     * Scrapes {@String url} for stats on the NBA 2nd-years and inputs data into
     * a text file, {@PrintWriter p}.
     */
    private static void dataFile(PrintWriter p, String url) {

        try {
            /*
             * Stats to be considered. I didn't include free throw % because I
             * don't want to discriminate against big men.
             */
            List<String> playerNames = new LinkedList<String>();
            List<Integer> minutes = new LinkedList<Integer>();
            List<String> plusMinus = new LinkedList<String>();
            List<Integer> rebounds = new LinkedList<Integer>();
            List<Integer> points = new LinkedList<Integer>();
            List<String> percentage = new LinkedList<String>();
            List<Integer> assists = new LinkedList<Integer>();

            /*
             * Scrape away!
             */
            final Document doc = Jsoup.connect(url).get();
            int pickNumber = 0;
            for (Element row : doc.select(
                    "table.js-csv-data.csv-data.js-file-line-container tr")) {

                if (!row.select("td:nth-of-type(5)").text().equals("")) {
                    String player = row.select("td:nth-of-type(5)").text()
                            + " (" + row.select("td:nth-of-type(4)").text()
                            + ", pick " + pickNumber + ")";

                    String minutesStr = row.select("td:nth-of-type(9)").text();
                    if (minutesStr.equals("")) {
                        minutesStr = "0";
                    }
                    int minutesElem = Integer.parseInt(minutesStr);

                    String plusMinusStr = row.select("td:nth-of-type(21)")
                            .text();
                    if (plusMinusStr.equals("")) {
                        plusMinusStr = "0";
                    }
                    String reboundsStr = row.select("td:nth-of-type(10)")
                            .text();
                    if (reboundsStr.equals("")) {
                        reboundsStr = "0";
                    }
                    int reboundsElem = Integer.parseInt(reboundsStr);

                    String pointsStr = row.select("td:nth-of-type(9)").text();
                    if (pointsStr.equals("")) {
                        pointsStr = "0";
                    }
                    int pointsElem = Integer.parseInt(pointsStr);

                    String percentageStr = row.select("td:nth-of-type(12)")
                            .text();
                    if (percentageStr.equals("")) {
                        percentageStr = "0";
                    }

                    String assistsStr = row.select("td:nth-of-type(11)").text();
                    if (assistsStr.equals("")) {
                        assistsStr = "0";
                    }
                    int assistsElem = Integer.parseInt(assistsStr);

                    playerNames.add(player);
                    minutes.add(minutesElem);
                    plusMinus.add(plusMinusStr);
                    rebounds.add(reboundsElem);
                    points.add(pointsElem);
                    percentage.add(percentageStr);
                    assists.add(assistsElem);
                }
                pickNumber++;
            }

            int pos = 0;
            while (pos < playerNames.size()) {
                p.write(playerNames.get(pos) + "\n");
                p.write(minutes.get(pos) + "\n");
                p.write(plusMinus.get(pos) + "\n");
                p.write(rebounds.get(pos) + "\n");
                p.write(points.get(pos) + "\n");
                p.write(percentage.get(pos) + "\n");
                p.write(assists.get(pos) + "\n");
                pos++;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param p
     * @return
     */
    private static void addPlayerScore(String name, BufferedReader data,
            Map<String, Double> nameToScore, Map<Double, String> scoreToName,
            List<Double> playerScores) {

        int minutes = 0;
        try {
            minutes = Integer.parseInt(data.readLine());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double plusMinus = 0;
        try {
            plusMinus = Double.parseDouble(data.readLine());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int rebounds = 0;
        try {
            rebounds = Integer.parseInt(data.readLine());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int points = 0;
        try {
            points = Integer.parseInt(data.readLine());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double percentage = 0;
        try {
            percentage = Double.parseDouble(data.readLine());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int assists = 0;
        try {
            assists = Integer.parseInt(data.readLine());
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double score = percentage * ((minutes * 3) + (points * 4)
                + (assists * 4) + (rebounds * 3) + (plusMinus * 1000));
        nameToScore.put(name, score);
        scoreToName.put(score, name);
        playerScores.add(score);
    }

    /**
     *
     * @param html
     * @param playerScores
     * @param nameToScore
     * @param scoreToName
     */
    private static void printTable(PrintWriter html, List<Double> playerScores,
            Map<String, Double> nameToScore, Map<Double, String> scoreToName) {

        /*
         * Print header.
         */
        html.println("<html>");
        html.println("<head>");
        html.println("<title>2014 Draft Class Rankings</title>");
        html.println("</head>");
        html.println("<body>");
        html.println(
                "<h1>Ranking of the 2014 NBA Draft Class as of January 2016</h1>");

        /*
         * Print table.
         */
        html.println("<table>");
        html.println("<tr>");
        html.println("<th>Player</th>");
        html.println("<th>Score</th>");
        html.println("</tr>");

        Set<Map.Entry<Double, String>> scoreKeys = scoreToName.entrySet();
        for (double score : playerScores) {
            if (score > 0) {
                String player = scoreToName.get(score);
                html.println("<tr>");
                html.println("<td>" + player + "</td>");
                html.println("<td>" + score + "</td>");
                html.println("</tr>");
            }
        }

        /*
         * Print closing tags.
         */
        html.println("</table>");
        html.println("</body>");
        html.println("</html>");

    }

    public static void main(String[] args) {

        String url = "https://github.com/kshvmdn/nbadrafts/blob/master/datasets/2014_nbadraft.csv";

        try {

            PrintWriter p = new PrintWriter("data/NBAdata.txt", "UTF-8");
            BufferedReader data = new BufferedReader(
                    new FileReader("data/NBAdata.txt"));
            dataFile(p, url);
            p.close();

            Map<String, Double> nameToScore = new HashMap<String, Double>();
            Map<Double, String> scoreToName = new HashMap<Double, String>();
            List<Double> playerScores = new LinkedList<Double>();
            String name = null;
            try {
                name = data.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            while (name != null) {
                addPlayerScore(name, data, nameToScore, scoreToName,
                        playerScores);
                try {
                    name = data.readLine();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Collections.sort(playerScores, Collections.reverseOrder());
            PrintWriter html = new PrintWriter("data/Output.html");
            printTable(html, playerScores, nameToScore, scoreToName);
            html.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Double o) {
        // TODO Auto-generated method stub
        return 0;
    }
}
