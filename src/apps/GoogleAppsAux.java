package apps;

import org.medhelp.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by johnshu on 5/24/16.
 */
public class GoogleAppsAux {

    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static void main(String[] args)  throws Exception {


        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriver = new ChromeDriver(chrome);
        WebDriver amazoneDriver = new ChromeDriver(chrome);


        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        //String file = "/Users/johnshu/Desktop/WebScraper/FreeFixedCSV.csv";
//        String file = "/Users/johnshu/Desktop/WebScraper/FreeGoogleAppsToExtract.csv";
        String file = "/Users/johnshu/Desktop/WebScraper/PaidGoogleAppsToExtract.csv";

        BufferedReader bufferedReader = null;
        String line = "";


        // Writing out data to file
//        File newfile = new File("/Users/johnshu/Desktop/WebScraper/Free Fixed Downloads-" + dateString + ".csv");
//
//        if (!newfile.exists()) {
//            try {
//                newfile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }

        System.out.println("\n\nPrint out of File " + file);

//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("FreeGoogleAppsFromPlayStore-"+ dateString + ".csv"), "utf-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("PaidGoogleAppsFromPlayStore-"+ dateString + ".csv"), "utf-8"));
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ExtractGoogleAppsFromPlayStore-"+ dateString + ".csv"), "utf-8"));
        String fileHeader = "Date Collected,AppName,App Creator,Number of Ratings,Star Ratings,Editor's Choice,Top Developer,5 Star,4 Star ,3 Star,2 Star,1 Star,App Price,Release Date,Last Updated,Category,Content Rating,Downloads,In App Products,Page Link\n";
        System.out.println(fileHeader);

        writer.write(fileHeader);

        // List of apps extracted and stored
        List<App> appArrayList = new ArrayList<App>();

        List<String> appList = new ArrayList<String>();
//        Integer friendNumber = 0;

        try {

//            Scanner input = new Scanner(new File(file));
//            System.out.println(input.next());


//            try {
//                File fileToRead = new File(file);
//                input = new Scanner(new File(file));
//                System.out.println(input.toString());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

            bufferedReader = new BufferedReader(new FileReader(file));

            while ((line = bufferedReader.readLine()) != null){
//            while (input.hasNextLine()) {

//                 line = input.nextLine();
                System.out.println("\n\nPrinting Line :" + line);

//                List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));
                appList.add(line);
            }

            System.out.println("\nappList has " + appList.size() + " apps\n");


            for (String value : appList) {

                //System.out.println("Value : " + value);

                String searchQuery = "https://play.google.com/store/search?q=" + value + "&c=apps&hl=en";

                String amazonQuery ="https://www.amazon.com/s/ref=nb_sb_ss_c_1_3?url=search-alias%3Dmobile-apps&field-keywords=" +value + "&sprefix=" +value + "%2Cmobile-apps%2C175";

                chromeDriver.navigate().to(searchQuery);

                List<WebElement> appData = chromeDriver.findElements(By.xpath("//div[contains(@class, 'description')]"));

                String appInfo = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                //System.out.println(appInfo);

                // Instantiate a new App
                App app = new App();

                // Extract app link
                Pattern appNamePattern = Pattern.compile("href=\"(.+?)\"");
                Matcher matcher = appNamePattern.matcher(appInfo);
                matcher.find();
                String appLinkCode = matcher.group(1);
                app.setAppPageLink("https://play.google.com" + appLinkCode);
                //System.out.println("https://play.google.com" + appLinkCode);


                chromeDriver.navigate().to("https://play.google.com" + appLinkCode);

                appData = chromeDriver.findElements(By.xpath("//div[contains(@class, 'main-content')]"));


                appInfo = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));

                    // Date Collected
                    Date date = new Date();
                    String dateCollected = new SimpleDateFormat("MM/dd/yyyy").format(date);
                    app.setDateCollected(dateCollected);


                    // Extract App Name
                    appNamePattern = Pattern.compile("<div class=\"id-app-title\" tabindex=\"0\">(.+?)<\\/div>");
                    matcher = appNamePattern.matcher(appInfo);
                    matcher.find();
                    String appName = matcher.group(1);
                    System.out.print(appName + ", ");
                    app.setAppName(appName);

                    // Extract app Creator Name
                    Pattern appCreatorPattern = Pattern.compile("<span itemprop=\"name\">(.+?)<\\/span>");
                    matcher = appCreatorPattern.matcher(appInfo);
                    matcher.find();
                    String appCreatorName = matcher.group(1);
                    System.out.print(appCreatorName + ", ");
                    app.setAppCreator(appCreatorName);


                    // Extract App Price
//                Pattern appPricePattern = Pattern.compile("<span>$(.+?) Buy<\\/span>");
//                matcher = appPricePattern.matcher(appInfo);
//                matcher.find();
//                String appPrice = matcher.group(1);
//                System.out.print(appPrice + ", ");
//                app.setAppDollarPrice(appPrice);

                    // Extract Dollar Price  ****************************
                    List<WebElement> appPrice = chromeDriver.findElements(By.xpath("//button[contains(@class, 'price buy id-track-click')]"));
                    if (appPrice.size() > 0) {
                        String appPriceInfo = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", appPrice.get(0));
                        appPriceInfo = appPriceInfo.replace("\n", " ").trim();
                        appPriceInfo = appPriceInfo.substring(Math.max(0, appPriceInfo.length() - 25)).trim();
//                    System.out.println("\n" + appPriceInfo);
//                    System.out.println("n" + appPriceInfo.replace("\n"," ") + "\n");
//                    appPricePattern = Pattern.compile("<a href=\".+?\">(.+?)<\\/a>");
//                    matcher = appPricePattern.matcher(appPriceInfo.replace("\n", " "));
//                    matcher.find();
//                    String appDollarPrice = matcher.group(1);
                        Pattern appPricePattern = Pattern.compile("<span>\\$(.+?) Buy<\\/span>");
                        matcher = appPricePattern.matcher(appPriceInfo);

                        if (matcher.find()) {
                            String appDollarPrice = matcher.group(1);
                            System.out.print(appDollarPrice + ", ");
                            app.setAppDollarPrice(appDollarPrice);
                            app.setAppPrice(appDollarPrice);
                        } else {
                            String appDollarPrice = "0.00";
                            System.out.print(appDollarPrice + ", ");
                            app.setAppDollarPrice(appDollarPrice);
                            app.setAppPrice(appDollarPrice);
                        }

                    }


                    // Extract App Ratings
                    Pattern appStarRatingsPattern = Pattern.compile("<div class=\"tiny-star star-rating-non-editable-container\" aria-label=\" Rated (.+?) stars out of five stars \">");
                    matcher = appStarRatingsPattern.matcher(appInfo);
                    matcher.find();
                    String appStarRatings = matcher.group(1);
                    System.out.print(appStarRatings + ", ");
                    app.setStarRatings(appStarRatings);

                    // Extract number of app ratings
                    Pattern appRatingsPattern = Pattern.compile("<span class=\"rating-count\" aria-label=\" .+? ratings \">(.+?)<\\/span>");
                    matcher = appRatingsPattern.matcher(appInfo);
                    matcher.find();
                    String appRatings = matcher.group(1);
                    System.out.print(appRatings.replace(",", "") + ", ");
                    app.setNumberOfRatings(appRatings);


                    // Extract App Top developer / Editor's Choice
                    Pattern appQualityPattern = Pattern.compile("<span class=\"badge-title\">(.+?)<\\/span>");
                    matcher = appQualityPattern.matcher(appInfo);
                    while (matcher.find()) {
//                    System.out.print(matcher.group(1) + ", ");
                        if (matcher.group(1).equals("Editors' Choice")) {
                            app.setEditorChoice(true);

                        } else if (matcher.group(1).equals("Top Developer")) {
                            app.setTopDeveloper(true);

                        }
                    }

                    String editorsChoice = (app.editorChoice == true) ? "Editors' Choice, " : ", ";
                    String topDeveloper = (app.topDeveloper == true) ? "Top Developer, " : ", ";

                    System.out.print(editorsChoice + topDeveloper);


                    // Extract app stars histogram
                    Pattern appHistogramPattern = Pattern.compile("<span class=\"bar-number\" aria-label=\" .+? \">(.+?)<\\/span>");
                    matcher = appHistogramPattern.matcher(appInfo);
                    HashMap stars = new HashMap();
                    Integer star = 5;
                    while (matcher.find()) {
//                    System.out.print(star + " stars :" + matcher.group(1).replace(",","") + ", ");
                        System.out.print(matcher.group(1).replace(",", "") + ", ");
                        stars.put(star, matcher.group(1).replace(",", ""));
                        star--;
                    }

                    app.setHistogram(stars);


                    // last update
                    Pattern updateDatePattern = Pattern.compile("<div class=\"content\" itemprop=\"datePublished\">(.+?)<\\/div>");
                    matcher = updateDatePattern.matcher(appInfo);
                    matcher.find();
                    String updateDate = matcher.group(1).trim().replace(",", "");

                    List<String> attributes = new ArrayList<String>(Arrays.asList(updateDate.split(" ")));

                    String year = attributes.get(2).trim();
                    String monthString = attributes.get(0).trim();
                    Integer month = Month.valueOf(monthString).getMonthValue(monthString);
                    String day = attributes.get(1).trim();

                    updateDate = month + "/" + day + "/" + year;

                    System.out.print(updateDate + ", ");
                    app.setLastUpdated(updateDate);

                    // Extract contentRating
                    Pattern appContentRatingPattern = Pattern.compile("<div class=\"content\" itemprop=\"contentRating\">(.+?)<\\/div>");
                    matcher = appContentRatingPattern.matcher(appInfo);
                    matcher.find();
                    String contentRating = matcher.group(1);
                    System.out.print(contentRating + ", ");
                    app.setContentRating(contentRating);

                    // Number of Installs/Downloads
                    Pattern installsPattern = Pattern.compile("<div class=\"content\" itemprop=\"numDownloads\">(.+?)<\\/div>");
                    matcher = installsPattern.matcher(appInfo);
                    matcher.find();
                    String installs = matcher.group(1).trim().replace(",", "");
                    System.out.print(installs + ", ");
                    app.setDownloads(installs);


                    // Extract inAppProducts
                    Pattern inAppProductsPattern = Pattern.compile("<div class=\"title\">In-app Products<\\/div> <div class=\"content\">(.+?) per item<\\/div>");
                    matcher = inAppProductsPattern.matcher(appInfo);
                    if (matcher.find()) {
                        String inAppProducts = matcher.group(1);
                        inAppProducts = inAppProducts.replace("$", "");
                        System.out.print(inAppProducts + ", ");
                        app.setInAppProducts(inAppProducts);
                    } else {
                        String inAppProducts = "0";
                        System.out.print(inAppProducts  + ", ");
                        app.setInAppProducts(inAppProducts);
                    }


                amazoneDriver.navigate().to(amazonQuery);

                List<WebElement> appSearched = amazoneDriver.findElements(By.xpath("//div[contains(@class, 'a-column a-span12 a-text-center')]"));

                try {
                    String Url = (String) ((JavascriptExecutor) amazoneDriver).executeScript("return arguments[0].innerHTML;", appSearched.get(0));

                    // Extract app link from Amazon search Page
                    Pattern appLinkPattern = Pattern.compile("<a class=\"a-link-normal a-text-normal\" href=\"(.+?)\">");
                    matcher = appLinkPattern.matcher(Url);
                    matcher.find();
                    String amazonAppLink = matcher.group(1);
//                System.out.print(amazonAppLink + ", ");

                    amazoneDriver.navigate().to(amazonAppLink);

                    // Extract Release and Updates Date
                    appData = amazoneDriver.findElements(By.xpath("//td[contains(@class, 'bucket')]"));
                    appInfo = (String) ((JavascriptExecutor) amazoneDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                    appInfo = appInfo.replace("\n", " ");
//                    System.out.println("\n" + appInfo.replace("\n"," ") + "\n");

                    //Release Date
                    Pattern releaseDatePattern = Pattern.compile("<li><b>Original Release Date:<\\/b>(.+?)<\\/li>");
                    matcher = releaseDatePattern.matcher(appInfo);
                    matcher.find();
                    String releaseDate = matcher.group(1).trim().replace(",", "");

                    List<String> releaseDateAttributes = new ArrayList<String>(Arrays.asList(releaseDate.split(" ")));

                    year = releaseDateAttributes.get(2).trim();
                    monthString = releaseDateAttributes.get(0).trim();
                    month = Month.valueOf(monthString).getMonthValue(monthString);
                    day = releaseDateAttributes.get(1).trim();

                    releaseDate = month + "/" + day + "/" + year;

                    System.out.print(releaseDate + ", ");
                    app.setReleaseDate(releaseDate);


                    // Extract App Category  ****************************  Games or not
                    List<WebElement> appCatData = amazoneDriver.findElements(By.xpath("//li[contains(@class, 'zg_hrsr_item')]"));

                    if (appCatData.size() > 0) {
                        String appCatInfo = (String) ((JavascriptExecutor) amazoneDriver).executeScript("return arguments[0].innerHTML;", appCatData.get(0));
                        appCatInfo = appCatInfo.replace("\n", "").trim();
//                        System.out.println("\n"+appCatInfo);

                        //Extract App category
                        Pattern appCatPattern = Pattern.compile(">&nbsp;(.+?)<\\/a>");
                        matcher = appCatPattern.matcher(appCatInfo);
                        matcher.find();
                        String appCategory = matcher.group(1).trim().replace(",", "");
                        System.out.print(appCategory + ", ");
                        app.setAppCategory(appCategory);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

                    System.out.println();
                    appArrayList.add(app);

                    String appText = app.printToGoogleAppsFile();
                    writer.write(appText);


            }


        } catch (Exception e){

            e.printStackTrace();

        }


        chromeDriver.close();
        writer.close();
        System.exit(0);

    }



    public enum Month {
        January(1, "January"),
        February(2, "February"),
        March(3, "March"),
        April(4, "April"),
        May(5, "May"),
        June(6, "June"),
        July(7, "July"),
        August(8, "August"),
        September(9, "September"),
        October(10, "October"),
        November(11, "November"),
        December(12, "December");

        private int monthNumber;
        private String monthString;


        Month(int monthNumber, String month) {
            this.monthNumber = monthNumber;
        }

        Month(String month) {
            this.monthString = month;
        }

        Month(int monthNumber) {
            this.monthNumber = monthNumber;
        }

        public int getMonthValue(String month){
            return monthNumber;
        }

        public String getMonthString(String month){
            return monthString;
        }

    }



}
