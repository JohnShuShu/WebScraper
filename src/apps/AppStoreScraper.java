package apps;

import org.medhelp.*;
import org.medhelp.Thread;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 */

public class AppStoreScraper {

    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static void main(String[] args)  throws Exception {


//        WebDriver chromeDriver = new FirefoxDriver();
        //WebDriver chromeDriver = new FirefoxDriver();

        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriver = new ChromeDriver(chrome);

        // File String to save to file object
        String fileText = "";

        String[] fileNameList = new String[4];

        String address = "";

        String[] addressList = new String[4];

        // different page numbers to parse
        Integer pageNumber =1;

        // Number of threads parsed
        Integer threadNumber=0;

        // WebElement from Selenium
        List<WebElement> newSubjectElement;

        // List of threads analyzed and stored
        List<App> appArrayList = new ArrayList<App>();


        System.out.println("Gathering data ...");

        //************************************************* START SCRAPPING FORUM FOR THE THREADS *************************************************//
        Date date = new Date();
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(date);

        String fileNameString1 = dateString + "-TopPaidApps";
        String fileNameString2 = dateString + "-TopGrossingApps";
        String fileNameString3 = dateString + "-TopFreeApps";

        fileNameList[0] = fileNameString1;
        fileNameList[1] = fileNameString2;
        fileNameList[2] = fileNameString3;


        String address1 = "file:///Users/johnshu/Desktop/WebScraper/Top%20Paid%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play/Top%20Paid%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play.htm";
        String address2 = "file:///Users/johnshu/Desktop/WebScraper/Top%20Grossing%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play/Top%20Grossing%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play.htm";
        String address3 = "file:///Users/johnshu/Desktop/WebScraper/Top%20Free%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play/Top%20Free%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play.htm";

        addressList[0] = address1;
        addressList[1] = address2;
        addressList[2] = address3;

        for(int n=1; n<4; n++) {


            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("GoogleApps-" + fileNameList[n] + ".csv"), "utf-8"));


//         do{
            // pageNumber is automatically incremented at the end of this loop so next page can be crawled. This happens until pages have
            // no more data i.e. threads.
            chromeDriver.navigate().to(addressList[n]);

//        chromeDriver.navigate().to("file:///Users/johnshu/Desktop/WebScraper/Top%20Paid%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play/Top%20Paid%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play.htm");
//        chromeDriver.navigate().to("file:///Users/johnshu/Desktop/WebScraper/Top%20Grossing%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play/Top%20Grossing%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play.htm");
//        chromeDriver.navigate().to("file:///Users/johnshu/Desktop/WebScraper/Top%20Free%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play/Top%20Free%20in%20Android%20Apps%20-%20Android%20Apps%20on%20Google%20Play.htm");



            // Start looking through the threads
            List<WebElement> appList = chromeDriver.findElements(By.xpath("//div[starts-with(@class, 'card-content')]"));
            System.out.println("\n\n\t\t\t\t\t\t App List Size " + appList.size() + " **************** " + fileNameList[n] + " ****************\n\n");



            String fileHeader = "Date Collected,AppName,App Creator,Number of Ratings,Star Ratings,Editor's Choice,Top Developer,5 Star,4 Star ,3 Star,2 Star,1 Star,App Price,Last Updated,Content Rating,Downloads,In App Products,Page Link\n";
            System.out.println(fileHeader);

            writer.write(fileHeader);

//            WebDriver appDriver = new FirefoxDriver();
            WebDriver appDriver = new ChromeDriver(chrome);


            for (int i = 0; i < appList.size(); i++) {
//            for ( int i=0; i < 3; i++){


                // Extract Thread Names

                try {

                    String Url = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", appList.get(i));
                    //System.out.println(Url);

                    System.out.println(i + ", ");
                    // Instantiate a new App
                    App app = new App();

                    // Date Collected
                    String dateCollected = new SimpleDateFormat("MM/dd/yyyy").format(date);
                    app.setDateCollected(dateCollected);


                    // Extract app link
                    Pattern appLinkPattern = Pattern.compile("<a class=\"card-click-target\" href=\"(.+?)\"");
                    Matcher matcher = appLinkPattern.matcher(Url);
                    matcher.find();
                    String appLink = matcher.group(1);
//                appLink = "https://play.google.com" + appLink; // For online collection
                    System.out.print(appLink + ", ");
                    app.setAppPageLink(appLink);


                    appDriver.navigate().to(appLink);

                    List<WebElement> appData = appDriver.findElements(By.xpath("//div[contains(@class, 'main-content')]"));

                    String appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                    //System.out.println(appInfo);


                    // Extract App Name
                    Pattern appNamePattern = Pattern.compile("<div class=\"id-app-title\" tabindex=\"0\">(.+?)<\\/div>");
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
                    List<WebElement> appPrice = appDriver.findElements(By.xpath("//button[contains(@class, 'price buy id-track-click')]"));
                    if (appPrice.size() > 0) {
                        String appPriceInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appPrice.get(0));
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
//                matcher.find();
                    if (matcher.find()) {
                        String inAppProducts = matcher.group(1);
                        inAppProducts = inAppProducts.replace("$", "");
                        System.out.print(inAppProducts);
                        app.setInAppProducts(inAppProducts);
                    } else {
                        String inAppProducts = "0";
                        System.out.print(inAppProducts);
                        app.setInAppProducts(inAppProducts);
                    }

                    System.out.println();
                    appArrayList.add(app);

                    String appText = app.printToGoogleAppsFile();
//                System.out.println(appText);
//                System.out.println();
                    writer.write(appText);


                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }


            }


            appDriver.close();
            writer.close();
        }


    }

    public enum Month {
        January(1, "January"),
        February(2, "February"),
        March(3, "March"),
        April(4, "April"),
        May(5, "May"),
        Jun(6, "June"),
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







