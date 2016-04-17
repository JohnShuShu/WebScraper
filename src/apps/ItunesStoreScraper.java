package apps;

import org.medhelp.*;
import org.medhelp.Thread;
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

public class ItunesStoreScraper {

    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static void main(String[] args)  throws Exception {


//        WebDriver chromeDriver = new ChromeDriver();
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

        // List of thread commentors. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> threadCommentors;
        List<User> threadCommentors = new ArrayList<User>();


        // List of all users both commentors and thread creators. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> userList = new ArrayList<User>();
        List<User> userList = new ArrayList<User>();

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


        String address1 = "http://appshopper.com/bestsellers/paid/?device=iphone";
        String address2 = "http://appshopper.com/bestsellers/gros/?device=iphone";
        String address3 = "http://appshopper.com/bestsellers/free/?device=iphone";

        addressList[0] = address1;
        addressList[1] = address2;
        addressList[2] = address3;


//        String fileNameString = dateString + "-ItunesTopPaidApps";
//        String fileNameString = dateString + "-ItunesTopGrossingApps";
//        String fileNameString = dateString + "-ItunesTopFreeApps";

        for(int n=0; n<4; n++) {


            File file = new File("ItuneApps-" + fileNameList[n] + ".csv");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ItuneApps-" + fileNameList[n] + ".csv"), "utf-8"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));


//         do{
            // pageNumber is automatically incremented at the end of this loop so next page can be crawled. This happens until pages have
            // no more data i.e. threads.

            chromeDriver.navigate().to(addressList[n]);
//        chromeDriver.navigate().to("http://appshopper.com/bestsellers/free/?device=iphone");
//        chromeDriver.navigate().to("http://appshopper.com/bestsellers/paid/?device=iphone");
//        chromeDriver.navigate().to("http://appshopper.com/bestsellers/gros/?device=iphone");


            // Start looking through the apps
            List<WebElement> appList = chromeDriver.findElements(By.xpath("//div[contains(@class, 'row')]"));
//        System.out.println(appList.size());

            appList.remove(0);

            String fileHeader = "Date Collected,AppName,App Creator,Number of Ratings,Star Ratings,Number of Ratings All Versions,Star Ratings All Versions,Position Change,Days Rank,Peak Rank,Price,App Category,Last Updated,In App Products,Size,App Link,\n";
            System.out.println(fileHeader);

            writer.write(fileHeader);
            WebDriver appDriver = new ChromeDriver(chrome);


            for (int i = 0; i < appList.size()-1; i++) {
//            for ( int i=0; i < 3; i++){


                // Extract Thread Names

                try {

                    String Url = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", appList.get(i));
//                System.out.println(Url);

                    // Instantiate a new App
                    App app = new App();

                    // Date Collected
                    String dateCollected = new SimpleDateFormat("MM/dd/yyyy").format(date);
                    app.setDateCollected(dateCollected);

                    // Extract Change up of rank
                    Pattern changeUpPattern = Pattern.compile("<div class=\"change change .+?\">(.+?)<\\/div>");
                    Matcher matcher = changeUpPattern.matcher(Url);
                    matcher.find();
                    String changeUp = matcher.group(1);
                    System.out.print(changeUp + ", ");
                    app.setChangeUp(changeUp);

                    // Extract days rank has been maintained
                    Pattern daysRankPattern = Pattern.compile("<div class=\"days\">(.+?)<\\/div>");
                    matcher = daysRankPattern.matcher(Url);
                    matcher.find();
                    String daysRank = matcher.group(1);
                    System.out.print(daysRank + ", ");
                    app.setDaysRank(daysRank);

                    // Extract days rank has been maintained
                    Pattern peakRankPattern = Pattern.compile("<div class=\"peak\">(.+?)<\\/div>");
                    matcher = peakRankPattern.matcher(Url);
                    matcher.find();
                    String peakRank = matcher.group(1);
                    System.out.print(peakRank + ", ");
                    app.setPeakRank(peakRank);

                    // Extract app link
                    Pattern appLinkPattern = Pattern.compile("<div class=\"price\"><a href=\"(.+?)\">.+?<\\/a><\\/div>");
                    matcher = appLinkPattern.matcher(Url);
                    matcher.find();
                    String appLink = matcher.group(1);
                    appLink = "http://appshopper.com" + appLink; // For online collection
                    System.out.print(appLink + ", ");
                    app.setAppPageLink(appLink);


                    appDriver.navigate().to(appLink);

                    List<WebElement> appData = appDriver.findElements(By.xpath("//div[contains(@id, 'desktopContentBlockId')]"));
                    String appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
//                System.out.println(appInfo);


                    // Extract App Name
                    List<WebElement> appNameData = appDriver.findElements(By.xpath("//h1[contains(@itemprop, 'name')]"));
                    String appName = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appNameData.get(0));
                    System.out.print(appName + ", ");
                    app.setAppName(appName);
//                Pattern appNamePattern = Pattern.compile("<h1 itemprop=\"name\">(.+?)</h1>");
//                matcher = appNamePattern.matcher(appInfo);
//                matcher.find();

                    // Extract app Creator Name
                    Pattern appCreatorPattern = Pattern.compile("<h2>By (.+?)</h2>");
                    matcher = appCreatorPattern.matcher(appInfo);
                    matcher.find();
                    String appCreatorName = matcher.group(1);
                    System.out.print(appCreatorName + ", ");
                    app.setAppCreator(appCreatorName);


                    // Extract Dollar Price  ****************************
                    List<WebElement> appPrice = appDriver.findElements(By.xpath("//div[contains(@class, 'price')]"));
                    if (appPrice.size() > 0) {
                        String appPriceInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appPrice.get(0));
                        appPriceInfo = appPriceInfo.replace("\n", " ").trim();
//
//                    Pattern appPricePattern = Pattern.compile("<span>\\$(.+?) Buy<\\/span>");
//                    matcher = appPricePattern.matcher(appPriceInfo);

                        if (appPriceInfo.equals("Free")) {
                            String appDollarPrice = "0.00";
                            System.out.print(appDollarPrice + ", ");
                            app.setAppDollarPrice(appDollarPrice);
                            app.setAppPrice(appDollarPrice);
                        } else {
                            String appDollarPrice = appPriceInfo.replace("$", "");
                            System.out.print(appDollarPrice + ", ");
                            app.setAppDollarPrice(appDollarPrice);
                            app.setAppPrice(appDollarPrice);
                        }

                    }


                    //Extract App Category
                    List<WebElement> appCategoryData = appDriver.findElements(By.xpath("//span[contains(@itemprop, 'applicationCategory')]"));
                    String appCategory = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appCategoryData.get(0));
                    appCategory = appCategory.replace("&amp:", "&");
                    System.out.print(appCategory + ", ");
                    app.setAppCategory(appCategory);


                    //==================== RATINGS EXTRACTION ====================//
                    List<WebElement> ratings = appDriver.findElements(By.xpath("//div[contains(@class, 'extra-list customer-ratings')]"));
                    String ratingsInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", ratings.get(0));

                    // Extract App Top developer / Editor's Choice
                    Pattern ratingPattern = Pattern.compile("aria-label=\"(.+?)\"");
                    matcher = ratingPattern.matcher(ratingsInfo);
                    String starRating = "";
                    String reviewCount = "";
                    int loop = 0;
                    while (matcher.find()) {

//                    System.out.print(matcher.group(1) + ", ");

                        String[] ratingsData = matcher.group(1).split(",");

                        starRating = ratingsData[0].replace("stars", "").trim();
                        reviewCount = ratingsData[1].replace("Ratings", "").trim();

                        if (starRating.contains("and a half")) {
                            starRating = starRating.replace("and a half", "").trim() + ".5";
                        }

                        if (loop < 1) {
                            app.setStarRatings(starRating);
                            app.setNumberOfRatings(reviewCount);

                        } else if (loop > 0) {
                            app.setStarRatingsAll(starRating);
                            app.setNumberOfRatingsAll(reviewCount);
                        }
                        loop++;
                    }


                    // Extract App Ratings Current Version
//                List<WebElement> ratingValue = appDriver.findElements(By.xpath("//span[contains(@itemprop, 'ratingValue')]"));
//                Pattern appStarRatingsPattern = Pattern.compile("<div class=\"tiny-star star-rating-non-editable-container\" aria-label=\" Rated (.+?) stars out of five stars \">");
//                matcher = appStarRatingsPattern.matcher(appInfo);
//                matcher.find();
//                String appStarRatings = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", ratingValue.get(0));
//                        matcher.group(1);
//                System.out.print(appStarRatings + ", ");
//                app.setStarRatings(appStarRatings);

                    // Extract number of app ratings
//                List<WebElement> reviewCountData = appDriver.findElements(By.xpath("//span[contains(@itemprop, 'reviewCount')]"));
//                Pattern appRatingsPattern = Pattern.compile("<span class=\"rating-count\" aria-label=\" .+? ratings \">(.+?)<\\/span>");
//                matcher = appRatingsPattern.matcher(appInfo);
//                matcher.find();
//                String appRatings = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", reviewCountData.get(0));
//                        matcher.group(1);
//                System.out.print(appRatings.replace("Ratings","") + ", ");
//                app.setNumberOfRatings(appRatings);


//                // Extract App Ratings All Version
//                List<WebElement> ratingValueAll = appDriver.findElements(By.xpath("//span[contains(@itemprop, 'ratingValue')]"));
//                String appStarRatingsAll = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", ratingValueAll.get(0));
//                System.out.print(appStarRatingsAll + ", ");
//                app.setStarRatingsAll(appStarRatingsAll);
//
//                // Extract number of app ratings
//                List<WebElement> reviewCountAll = appDriver.findElements(By.xpath("//span[contains(@class, 'rating-count')]"));
//                String appRatingsAll = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", reviewCountAll.get(0));
//                System.out.print(appRatingsAll.replace("Ratings","") + ", ");
//                app.setNumberOfRatingsAll(appRatingsAll);


                    // Extract App Top developer / Editor's Choice
//                Pattern appQualityPattern = Pattern.compile("<span class=\"badge-title\">(.+?)<\\/span>");
//                matcher = appQualityPattern.matcher(appInfo);
//                while(matcher.find()){
////                    System.out.print(matcher.group(1) + ", ");
//                    if(matcher.group(1).equals("Editors' Choice")){
//                        app.setEditorChoice(true);
//
//                    } else if (matcher.group(1).equals("Top Developer")){
//                        app.setTopDeveloper(true);
//
//                    }
//                }
//
//                String editorsChoice  = (app.editorChoice == true)? "Editors' Choice, " : ", ";
//                String topDeveloper  = (app.topDeveloper == true)? "Top Developer, " : ", ";
//
//                System.out.print(editorsChoice + topDeveloper);


//                // Extract app stars histogram
//                Pattern appHistogramPattern = Pattern.compile("<span class=\"bar-number\" aria-label=\" .+? \">(.+?)<\\/span>");
//                matcher = appHistogramPattern.matcher(appInfo);
//                HashMap stars = new HashMap();
//                Integer star = 5;
//                while(matcher.find()){
////                    System.out.print(star + " stars :" + matcher.group(1).replace(",","") + ", ");
//                    System.out.print(matcher.group(1).replace(",","") + ", ");
//                    stars.put(star,matcher.group(1));
//                    star--;
//                }
//
//                app.setHistogram(stars);


                    // last update
                    List<WebElement> lasUpdateData = appDriver.findElements(By.xpath("//span[contains(@itemprop, 'datePublished')]"));
                    String updateDate = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", lasUpdateData.get(0));
                    updateDate = updateDate.trim().replace(",", "");

                    List<String> attributes = new ArrayList<String>(Arrays.asList(updateDate.split(" ")));

                    String year = attributes.get(2).trim();
                    String monthString = attributes.get(0).trim();
                    Integer month = Month.valueOf(monthString).getMonthValue(monthString);
                    String day = attributes.get(1).trim();

                    updateDate = month + "/" + day + "/" + year;

                    System.out.print(updateDate + ", ");
                    app.setLastUpdated(updateDate);

//                Pattern updateDatePattern = Pattern.compile("<div class=\"content\" itemprop=\"datePublished\">(.+?)<\\/div>");
//                matcher = updateDatePattern.matcher(appInfo);
//                matcher.find();

                    // Extract Size
                    Pattern appSizePattern = Pattern.compile("<li><span class=\"label\">Size: <\\/span>(.+?) [MG]B<\\/li>");
                    matcher = appSizePattern.matcher(appInfo);
                    matcher.find();
                    String appSize = matcher.group(1);
                    System.out.print(appSize + ", ");
                    app.setAppSize(appSize);

//                // Number of Installs/Downloads
//                Pattern installsPattern = Pattern.compile("<div class=\"content\" itemprop=\"numDownloads\">(.+?)<\\/div>");
//                matcher = installsPattern.matcher(appInfo);
//                matcher.find();
//                String installs = matcher.group(1).trim().replace(",", "");
//                System.out.print(installs + ", ");
//                app.setDownloads(installs);


                    // Extract inAppProducts
                    List<WebElement> inAppProductsData = appDriver.findElements(By.xpath("//span[starts-with(@class, 'in-app-price')]"));
                    List<String> inAppProductsList = new ArrayList<String>();
                    String inAppProducts = "";
                    for (WebElement webElement : inAppProductsData) {
                        inAppProducts = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", webElement);
                        inAppProducts = inAppProducts.replace("$", "");
                        inAppProducts = (inAppProducts.contains("Free")) ? "0.00" : inAppProducts;
//                    System.out.println(inAppProducts);
                        inAppProductsList.add(inAppProducts);
                    }

                    if (inAppProductsList.size() > 0) {
                        Integer lastItem = inAppProductsList.size();
                        Collections.sort(inAppProductsList);
                        inAppProducts = inAppProductsList.get(0) + "-" + inAppProductsList.get(lastItem - 1);
                        System.out.println(inAppProducts);
                        app.setInAppProducts(inAppProducts);
                    } else {
                        inAppProducts = "0.00";
                        System.out.println(inAppProducts);
                        app.setInAppProducts(inAppProducts);
                    }


//                Pattern inAppProductsPattern = Pattern.compile("<div class=\"title\">In-app Products<\\/div> <div class=\"content\">(.+?) per item<\\/div>");
//                matcher = inAppProductsPattern.matcher(appInfo);
////                matcher.find();
//                if(matcher.find()){
//                    String inAppProducts = matcher.group(1);
//                    inAppProducts = inAppProducts.replace("$","");
//                    System.out.print(inAppProducts);
//                    app.setInAppProducts(inAppProducts);
//                }else{
//                    String inAppProducts = "0";
//                    System.out.print(inAppProducts);
//                    app.setInAppProducts(inAppProducts);
//                }

                    System.out.println();
                    appArrayList.add(app);

                    String appText = app.printToItunesAppsFile();
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
        Jan(1, "Jan"),
        Feb(2, "Feb"),
        Mar(3, "Mar"),
        Apr(4, "Apr"),
        May(5, "May"),
        Jun(6, "Jun"),
        Jul(7, "Jul"),
        Aug(8, "Aug"),
        Sep(9, "Sep"),
        Oct(10, "Oct"),
        Nov(11, "Nov"),
        Dec(12, "Dec");

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







