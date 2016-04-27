package apps;

import org.medhelp.Note;
import org.medhelp.Post;
import org.medhelp.Thread;
import org.medhelp.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by johnshu on 1/25/16.
 */
public class AmazonTopAppScraper {


    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + "-UndergroundApps";


    public static void main(String[] args) throws Exception {

        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
            File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
            ChromeOptions chrome = new ChromeOptions();
            chrome.addExtensions(addonpath);
            WebDriver chromeDriver = new ChromeDriver(chrome);
//            chromeDriver.navigate().to("https://www.amazon.com");


//        WebDriver chromeDriver = new ChromeDriver();
        WebDriver appDriver = new ChromeDriver();

//
//        FirefoxProfile firefoxprofile = new FirefoxProfile();
//        File addonpath = new File(dir+"/Selenium/adblock_plus-2.7.1.xpi");
//        firefoxprofile.addExtension(addonpath);
//        WebDriver chromeDriver = new FirefoxDriver(firefoxprofile);
//        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String fileText = "";

        // different page numbers to parse
        Integer pageNumber = 1;

        // Number of apps parsed
        Integer appNumber = 0;

        //page link to navigate
        String pageLink = "http://www.amazon.com/s/ref=sr_pg_" + pageNumber + "?fst=as%3Aon&rh=n%3A2350149011%2Ck%3Aapp&page=" + pageNumber + "&keywords=app&ie=UTF8&qid=1457211528";

        // List of apps analyzed and stored
        List<App> appArrayList = new ArrayList<App>();

        System.out.println("Gathering data ...\n");

        //************************************************* START SCRAPPING FORUM FOR THE THREADS *************************************************//


        // pageNumber is automatically incremented at the end of this loop so next page can be crawled. This happens until pages have
        // no more data i.e. threads.
        chromeDriver.manage().deleteAllCookies();

//        chromeDriver.navigate().to("http://www.amazon.com/s/ref=s9_hps_bw_clnk?node=2350149011,!2445993011," +
//                "11350978011&search-alias=banjo-apps&field-review-rating=2479575011&bbn=11350978011&pf_rd_m=" +
//                "ATVPDKIKX0DER&pf_rd_s=merchandised-search-1&pf_rd_r=12QMT9KHXA5TXCGMYM4R&pf_rd_t=101&pf_rd_p=2382153642&pf_rd_i=2350149011");
////        chromeDriver.navigate().to("http://www.amazon.com/s/ref=sr_pg_2?rh=n%3A2350149011%2Ck%3Aapps%2Cp_36%3A1-99999999&page=" + pageNumber + "&keywords=apps&ie=UTF8&qid=1454793056");
//        chromeDriver.navigate().to("http://www.amazon.com/s/ref=sr_pg_2?rh=n%3A2350149011%2Cn%3A%212445993011%2Cn%3A11350978011%2Ck%3Aapps&page=" + pageNumber + "&keywords=apps&ie=UTF8&qid=1455227365");
//
        // Underground apps
//        chromeDriver.navigate().to("http://www.amazon.com/s/ref=sr_pg_2?fst=as%3Aoff&rh=" +
//                "n%3A2350149011%2Ck%3Aapp%2Cp_n_program_participation%3A11335409011&page=" + pageNumber +
//                "&keywords=app&ie=UTF8&qid=1455313174");


        // All apps Scraping
        chromeDriver.navigate().to(pageLink);

        chromeDriver.manage().deleteAllCookies();

        List<WebElement> appList = chromeDriver.findElements(By.className("s-result-item"));

        String appFileText = "Name, Creator, Ratings, Stars, 5 stars, 4 stars, 3 stars, 2 stars, 1 star, Release date, Last updated,Content Rating, In app purchases,appPrice,appDollarPrice,, Best seller rank, App store rank,Category rank,Category,Size,Link\n";

        Date date = new Date();
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(date);

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("AmazonApps-" + dateString + ".csv"), "utf-8"));

        do {

            System.out.println(appList.size());

//            System.out.println("\nPage Link,App Name,Company,Av. Rating,No. Ratings,Editors' Choice,Top Developer,5 STARS,4 STARS,3 STARS,2 STARS,1 STAR,Content Rating,In-app Products/item \n");
//                WebDriver appDriver = new FirefoxDriver();
            for (int i = 0; i < appList.size(); i++) {
//            for ( int i=0; i < 3; i++){


                try {

                    String Url = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", appList.get(i));
//                    System.out.println("\n");


                    // Instantiate a new App
                    App app = new App();
                    appNumber++;

                    String dateCollected = new SimpleDateFormat("MM/dd/yyyy").format(date);
                    app.setDateCollected(dateCollected);

                    // Extract app link
                    Pattern appLinkPattern = Pattern.compile("<a class=\"a-link-normal s-access-detail-page  a-text-normal\" title=\"(.+?)\" href=\"(.+?)\">");
                    Matcher matcher = appLinkPattern.matcher(Url);
                    matcher.find();
                    String appName = matcher.group(1);
                    String appLink = matcher.group(2);
                    System.out.print(appName + ", ");
//                    System.out.print(appLink + ", ");
                    app.setAppPageLink(appLink);
                    app.setAppName(appName);


                    // Extract App Star Ratings
                    Pattern appStarRatingsPattern = Pattern.compile("<span class=\"a-icon-alt\">(.+?) out of 5 stars<\\/span>");
                    matcher = appStarRatingsPattern.matcher(Url);
                    matcher.find();
                    String appStarRatings = matcher.group(1);
                    System.out.print(appStarRatings + ", ");
                    app.setStarRatings(appStarRatings);


                    // Extract number of app ratings/reviews
                    Pattern appRatingsPattern = Pattern.compile("<a class=\"a-size-small a-link-normal a-text-normal\" href=\".+?#customerReviews\">(.+?)<\\/a>");
                    matcher = appRatingsPattern.matcher(Url);
                    matcher.find();
                    String appRatings = matcher.group(1);
                    System.out.print(appRatings.replace(",", "") + ", ");
                    app.setNumberOfRatings(appRatings);


                    appDriver.navigate().to(appLink);

//                List<WebElement> appData = appDriver.findElements(By.xpath("//div[contains(@class, 'a-container')]"));
//
//                String appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
//                System.out.println(appInfo + "\n");

                    // Extract Underground or Not
                    List<WebElement> paidData = appDriver.findElements(By.xpath("//a[contains(@class, 'banjoLogoText')]"));
                    Boolean appPaid = true;
                    if(paidData.size()>0){
                        String appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", paidData.get(0));
//                      System.out.println("\n" + appInfo.replace("\n"," ") + "\n");

                        if(appInfo.trim().equals("Amazon Underground")){
                            appPaid = false;
                        }
                    }
                    System.out.print(appPaid + ", ");
                    app.setAppPaid(appPaid);



                    // Extract app Creator Name
                    List<WebElement> appData = appDriver.findElements(By.xpath("//div[contains(@class, 'buying')]"));
                    String appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
//                System.out.println("\n" + appInfo.replace("\n"," ") + "\n");

                    Pattern appCreatorPattern = Pattern.compile("<a href=\".+?\">(.+?)<\\/a>");
                    matcher = appCreatorPattern.matcher(appInfo.replace("\n", " "));
                    matcher.find();
                    String appCreatorName = matcher.group(1);
                    System.out.print(appCreatorName + ", ");
                    app.setAppCreator(appCreatorName);



                    // Extract App price and inAppProducts
                    appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(1));

                    Pattern appPricePattern = Pattern.compile("<span class=\"banjoPrice\">(.+?)<\\/span>");
                    matcher = appPricePattern.matcher(appInfo.replace("\n", ""));
                    while (matcher.find()) {
                        if (matcher.group(1).contains("$")) {
                            String appPrice = matcher.group(1);
                            System.out.print(appPrice.trim() + ", ");
                            app.setAppPrice(appPrice.trim());
                        } else {
                            String inAppProducts = matcher.group(1);
                            System.out.print(inAppProducts.trim() + ", ");
                            app.setInAppProducts(inAppProducts.trim());
                        }
                    }

                    // Extract Price  ****************************
                    List<WebElement> appPrice = appDriver.findElements(By.xpath("//strong[contains(@class, 'priceLarge')]"));
                    if(appPrice.size()>0){
                        String appPriceInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appPrice.get(0));
                        appPriceInfo = appPriceInfo.replace("\n"," ").trim();
//                    System.out.println("n" + appPriceInfo.replace("\n"," ") + "\n");
//                    appPricePattern = Pattern.compile("<a href=\".+?\">(.+?)<\\/a>");
//                    matcher = appPricePattern.matcher(appPriceInfo.replace("\n", " "));
//                    matcher.find();
//                    String appDollarPrice = matcher.group(1);
                        String appDollarPrice = appPriceInfo;
                        appDollarPrice = (appDollarPrice.contains("Free Download")? "$0.00" : appDollarPrice);
                        System.out.print(appDollarPrice + ", ");
                        app.setAppPrice(appDollarPrice);
                    }



                    // Extract Release and Updates Date
                    appData = appDriver.findElements(By.xpath("//td[contains(@class, 'bucket')]"));
                    appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                    appInfo = appInfo.replace("\n"," ");
//                    System.out.println("\n" + appInfo.replace("\n"," ") + "\n");

                    //Release Date
                    Pattern releaseDatePattern = Pattern.compile("<li><b>Original Release Date:<\\/b>(.+?)<\\/li>");
                    matcher = releaseDatePattern.matcher(appInfo);
                    matcher.find();
                    String releaseDate = matcher.group(1).trim().replace(",", "");
                    System.out.print(releaseDate + ", ");
                    app.setReleaseDate(releaseDate);

                     // last update
                    Pattern updateDatePattern = Pattern.compile("<li>   <b> Latest Developer Update:<\\/b>(.+?)<\\/li>");
                    matcher = updateDatePattern.matcher(appInfo);
                    matcher.find();
                    String updateDate = matcher.group(1).trim().replace(",", "");
                    System.out.print(updateDate + ", ");
                    app.setLastUpdated(updateDate);

                    // Extract App Category  ****************************  Games or not
                    List<WebElement> appCatData = appDriver.findElements(By.xpath("//li[contains(@class, 'zg_hrsr_item')]"));

                    if(appCatData.size()>0){
                        String appCatInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appCatData.get(0));
                        appCatInfo = appCatInfo.replace("\n","").trim();
//                        System.out.println("\n"+appCatInfo);

                        //Extract content ratings
                        Pattern appCatPattern = Pattern.compile(">&nbsp;(.+?)<\\/a>");
                        matcher = appCatPattern.matcher(appCatInfo);
                        matcher.find();
                        String appCategory = matcher.group(1).trim().replace(",","") ;
                        System.out.print(appCategory + ", ");
                        app.setAppCategory(appCategory);
                    }


                    // Extract Amazon Best Seller Rank
                    appData = appDriver.findElements(By.xpath("//li[contains(@id, 'SalesRank')]"));
                    appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                    appInfo = appInfo.replace("<b>Amazon Best Sellers Rank:</b>", " ").replace("\n", "").trim();
//                  System.out.println("\n" + appInfo + "\n");

                    Pattern amazonBestSellerRankPattern = Pattern.compile("#(.+?) .+?");
                    matcher = amazonBestSellerRankPattern.matcher(appInfo);
                    matcher.find();
                    String amazonBestSellerRank = matcher.group(1);
                    System.out.print(amazonBestSellerRank + ", ");
                    app.setAmazonBestSellerRank(amazonBestSellerRank.replace(",",""));


//                  <span class="zg_hrsr_rank">#18</span>
//                  <span class="zg_hrsr_rank">#19</span>  zg_hrsr_item


                    Pattern appRankPattern = Pattern.compile("<span class=\"zg_hrsr_rank\">(.+?)<\\/span>");
                    matcher = appRankPattern.matcher(appInfo);
                    String androidAppstoreRank = "";
                    String androidAppstoreCategoryRank = "";
                    while (matcher.find()) {
//                    System.out.print(matcher.group(1) + ", ");
                        if (androidAppstoreRank == "" & matcher.group(1).contains("#")) {

                            androidAppstoreRank = matcher.group(1);
                            androidAppstoreRank = androidAppstoreRank.replace("#","");
                            System.out.print(androidAppstoreRank + ", ");
                            app.setAndroidAppstoreRank(androidAppstoreRank);

                        } else if (!(androidAppstoreRank == "") & matcher.group(1).contains("#")) {

                            androidAppstoreCategoryRank = matcher.group(1);
                            System.out.print(androidAppstoreCategoryRank + ", ");
                            app.setAndroidAppstoreCategoryRank(androidAppstoreCategoryRank);
                        }
                    }

//                  String editorsChoice = (app.editorChoice == true) ? "Editors' Choice, " : ", ";
//                  String topDeveloper = (app.topDeveloper == true) ? "Top Developer, " : ", ";

//                  System.out.print(editorsChoice + topDeveloper);

                    // Extract app Size                          <strong>Size:</strong> 86.5MB</div>
                    appData = appDriver.findElements(By.xpath("//div[contains(@id, 'mas-technical-details')]"));
                    appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                    appInfo = appInfo.replace("\n", "").trim();
//                  System.out.println("\n" + appInfo + "\n");

                    Pattern appSizePattern = Pattern.compile("<strong>Size:<\\/strong>(.+?)<\\/div>");
                    matcher = appSizePattern.matcher(appInfo);
                    matcher.find();
                    String appSize = matcher.group(1).replace("\"", "");
                    System.out.print(appSize.trim() + ", ");
                    app.setAppSize(appSize.trim());

                    // Extract app stars histogram
                    appData = appDriver.findElements(By.xpath("//table[contains(@id, 'histogramTable')]"));
                    appInfo = (String) ((JavascriptExecutor) appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                    appInfo = appInfo.replace("\n", "").trim();
//                  System.out.println("\n" + appInfo + "\n");

                    Pattern appHistogramPattern = Pattern.compile("<\\/span><a class=\"a-link-normal\" title=\".+?\" href=\".+?\">(.+?)%<\\/a>");
                    matcher = appHistogramPattern.matcher(appInfo);
                    Map stars = new HashMap<Integer, String>();
                    stars.put(1,"0");
                    stars.put(2,"0");
                    stars.put(3,"0");
                    stars.put(4,"0");
                    stars.put(5,"0");

                    Integer star = 5;
                    while (matcher.find()) {
                        System.out.print(matcher.group(1).replace(",", "") + ", ");

                        stars.put(star, matcher.group(1));
                        star--;
                    }

                    app.setHistogram(stars);

                    String appText = app.printToFile();
                    System.out.println(appText);
                    appArrayList.add(app);
                    System.out.println();
                    writer.write(appText);

                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }

                if((appNumber/appList.size())==pageNumber){
                    System.out.println("************************************************************************* NAVIGATING TO PAGE " + pageNumber + " *************************************************************************");
                    pageNumber++;
                    pageLink = "http://www.amazon.com/s/ref=sr_pg_" + pageNumber + "?fst=as%3Aon&rh=n%3A2350149011%2Ck%3Aapp&page=" + pageNumber + "&keywords=app&ie=UTF8&qid=1457211528";

                    chromeDriver.navigate().to(pageLink);
                    appList = chromeDriver.findElements(By.className("s-result-item"));
                }



            }
//        }while (appList.size()>1);
        }while (pageNumber<61);


        chromeDriver.close();
        chromeDriver.quit();
        //******************************* END OF APP DATA EXTRACTION ************************************************//

        try{
//            String appFileText = "Name, Creator, Ratings, Stars, 5 stars, 4 stars, 3 stars, 2 stars, 1 star, Release date, Content Rating, In app purchases,appPrice,appDollarPrice,, Best seller rank, App store rank,Category rank,Category,Size,Link";
                    //"Name, Creator, Ratings, Stars, 5 stars, 4 stars, 3 stars, 2 stars, 1 star, Release date, Content Rating, In app purchases, Best seller rank, App store rank, app category rank\n";

//            String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

            System.out.println("Writing final user data to the file ...\n");

            // Writing out data to file
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("AmazonAppsFinal-" + dateString + ".csv"), "utf-8"));
            for(App app: appArrayList){

                appFileText = appFileText + app.printToFile();

            }


            System.out.println(appFileText);
            writer.write(appFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

    }
















    public static Boolean doesUserExist(List<User> userList, User newUser){
        Boolean value = Boolean.FALSE;

        for(User user: userList){
            if((newUser.userName).equals(user.userName)){
                value = Boolean.TRUE;
                if((newUser.toString()).length() > (user.toString()).length()){
//                    System.out.println("NEWUSER: " + newUser.toString() + " ++++++++ is greater than +++++++++ " + " USER: " + user.toString() );
                    value = Boolean.FALSE;
                }
                System.out.println("Condition to Add is : " + value);
            }
        }

        return value;
    }


















    public static List<User> createUserListFromFile (String file){

        List<User> userList = new ArrayList<User>();

        System.out.println("\n\n" + file);

        Integer count = 0;
        Integer added = 0;


        try {
            Scanner input = new Scanner (new File(file));
            while (input.hasNextLine()) {

                try {
                    String line = input.nextLine();

                    if(!(line == "")){
                        count++;
                        System.out.println("Printing Line :" + line);
                        List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));
                        for(String value: attributes){

                            System.out.println("Value : " + value.trim());

                        }

                        System.out.println("Value : " + count );
                        User user = new User();
                        user.setUserName(attributes.get(0).trim());
                        user.setUserPageLink(attributes.get(1).trim());

                        if (isInt(attributes.get(2).trim())) {
                            user.setPageId(Integer.parseInt(attributes.get(2).trim()));
                        }   else {
                            user.setPageId(0);

                        }

                        if (isInt(attributes.get(3).trim())) {
                            user.setUniqueId(Integer.parseInt(attributes.get(3).trim()));
                        }   else {
                            user.setUniqueId(0);

                        }

                        if (!doesUserExist(userList, user)){
                            System.out.println("\n\nAdding new User");
                            userList.add(user);
                            added++;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        System.out.println("\n\n\n\n\n*****************\n" + "Total number of users is: " + userList.size() + "\n*****************\n");
        System.out.println("\n*****************\n" + "Total number of users added: " + added + "\n*****************\n\n\n\n\n");
        return userList;
    }




    static boolean isInt(String s)
    {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }
















    public static List<User> scrapeThreads(List<Thread> threadList, List<User> userList){

        System.out.println("\n\n********************************** THREADS: GATHERING DATA ***************************************");


//                FirefoxProfile firefoxprofile = new FirefoxProfile();
//                File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
//                firefoxprofile.addExtension(addonpath);
//                chromeDriver = new FirefoxDriver(firefoxprofile);

        System.setProperty("webdriver.chrome.driver", dir+"/Selenium/chromedriver");
        File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriver = new ChromeDriver(chrome);
        chromeDriver.navigate().to("https://www.medhelp.org");
        chromeDriver.manage().deleteAllCookies();

//        WebDriver chromeDriver = new FirefoxDriver();
        String threadfileText = "";

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(Thread thread: threadList){

            String threadCommentorsData = "";
            String numberOfComments = "";
            List<User> threadCommentors;

            System.out.println("\n\nThread " + thread.getThreadNumber() + " of " + threadList.size());

            try{

//                System.out.println(thread.appLink);


                // Go to the Thread Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to(thread.threadLink);
                chromeDriver.manage().deleteAllCookies();

                System.out.println(thread.threadLink);

                // Gathering data on comments for this particular thread
                try {
                    List<WebElement> subjectCommentsNumber = chromeDriver.findElements(By.className("comments_num"));
                    if(subjectCommentsNumber.size()>0){
                        numberOfComments = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", subjectCommentsNumber.get(0));
                        numberOfComments = numberOfComments.trim().replace("\n","");
                    }

                }catch (Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }

                try {
                    List<WebElement> answerCount = chromeDriver.findElements(By.className("answer_count"));
                    if(answerCount.size()>0){
                        numberOfComments = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", answerCount.get(0));
                        numberOfComments = numberOfComments.trim().replace("\n","");
                    }

                }catch (Exception e){
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }


                System.out.println(numberOfComments);


                // Extracting number of comments in this thread.
                Pattern numberOfCommentsPattern = Pattern.compile("<a href=\"#comments_header\">(.+?) .+?<\\/a>");
                Matcher matcher;
                String commentsNumber="";
                if(numberOfCommentsPattern.matcher(numberOfComments).matches()){
                    matcher = numberOfCommentsPattern.matcher(numberOfComments);
                    matcher.find();
                    commentsNumber = matcher.group(1);
                } else if(numberOfComments.trim().contains("Answer")) {
//                    Pattern numberOfCommentsPattern1 = Pattern.compile("<div class=\"answer_count\">(.+?) .+?<\\/div>");
//                    matcher = numberOfCommentsPattern1.matcher(numberOfComments);
                    String arr[] = numberOfComments.split(" ");
                    commentsNumber = arr[0];
                }

                System.out.println("Number of comments: " + commentsNumber);
                thread.setCommentsNumber(Integer.valueOf(commentsNumber));

                // Instantiate list to save all the users who commented in this thread.
                threadCommentors = new ArrayList<User>();

                // Loop to find all the thread commentors
                if(Integer.valueOf(commentsNumber) > 0){

                    // If this page has any data

                    List<WebElement> threadData;
                    List<WebElement> postMessage;
                    try {
                        threadData = chromeDriver.findElements(By.className("question_by"));

                    }catch (Exception e){
                        System.err.println("Caught Exception: " + e.getMessage());
                        continue;
                    }

                    try {
                        postMessage = chromeDriver.findElements(By.className("post_message_container"));

                    }catch (Exception e){
                        System.err.println("Caught Exception: " + e.getMessage());
                        continue;
                    }


                    threadData = threadData.size() > postMessage.size() ? threadData : postMessage;

                    for ( int i=0; i < threadData.size(); i++){


                        threadCommentorsData = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", threadData.get(i));
                        threadCommentorsData = threadCommentorsData.trim();


                        // New user object
                        User commentor = new User();

                        // Extract Commentor Names
                        Pattern threadCommentorsPattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        matcher = threadCommentorsPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String commentorName = matcher.group(1);
                        System.out.println(commentorName);
                        commentor.setUserName(commentorName);


                        // Extract Commentors page Links
                        Pattern threadCommentorLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\".+?\">");
                        matcher = threadCommentorLinkPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String threadCommentorLink = matcher.group(1);
                        threadCommentorLink = "http://www.medhelp.org" + threadCommentorLink;
                        System.out.println(threadCommentorLink);
                        commentor.setUserPageLink(threadCommentorLink);

                        // Extracting user unique Id.
                        Pattern threadCommentorUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"(.+?)\">");
                        matcher = threadCommentorUniqueIdPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String userUniqueId = matcher.group(1);
                        String arr[] = userUniqueId.split("_");
                        userUniqueId = arr[1];
                        System.out.println(userUniqueId);
                        commentor.setUniqueId(Integer.valueOf(userUniqueId));


                        // Add user to list of thread commentors.
                        threadCommentors.add(commentor);

                        // Add user to list of users.
                        if (!doesUserExist(userList, commentor)){
                            System.out.println("Adding new User");
                            userList.add(commentor);
                        }

                    }
                }

            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            // Save list of commentors to this particluar thread
            thread.setCommentors(threadCommentors);

        }

        //******************************* END OF THREAD DATA EXTRACTION ************************************************//

        try {
//            String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());


            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Threads-" + dateString + ".csv"), "utf-8"));
            for(Thread thread: threadList){
                threadfileText = threadfileText + thread.printToFile();
            }

            System.out.println(threadfileText);
            writer.write(threadfileText);
            writer.close();


        }catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        chromeDriver.close();
        chromeDriver.quit();

        return userList;
    }















    public static List<User> scrapeUsers(List<User> userList){

        System.out.println("\n\n********************************** IN USER/FRIENDS FUNCTION ***************************************");

        System.setProperty("webdriver.chrome.driver", dir+"/Selenium/chromedriver");
        File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriver = new ChromeDriver(chrome);
        chromeDriver.navigate().to("http://www.medhelp.org");
        chromeDriver.manage().deleteAllCookies();


//        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String userFileText = "";

        String userIds = "";
        String userInfo = "";
        String userNameInfo = "";

        // WebElement from Selenium
        List<WebElement> userPageIdData;
        List<WebElement> userInfoData;
        List<WebElement> userNameData;
        List<WebElement> paginationNumber;


        // List to hold new found users.
        List<User> completeUsersList = new ArrayList<User>();

        // Add existing users to completeUserList
        completeUsersList.addAll(userList);

//        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

        // Writing out data to file
        File file = new File(dir+"/UsersThreads&Friends" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<User> friendList = new ArrayList<User>();



            System.out.println("\n\n" + user.getUserName()+ " with link " + user.userPageLink);

            try{

//                FirefoxProfile firefoxprofile = new FirefoxProfile();
//                File addonpath = new File("dir+/Selenium/AdBlock_v2.47.crx");
//                firefoxprofile.addExtension(addonpath);
//                chromeDriver = new FirefoxDriver(firefoxprofile);

                // Go to the Thread Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to(user.userPageLink);
                chromeDriver.manage().deleteAllCookies();

                // Gathering data on comments for this particular thread
                userNameData = chromeDriver.findElements(By.className("page_title"));
                userPageIdData = chromeDriver.findElements(By.className("pp_r_txt_sel"));
                userInfoData = chromeDriver.findElements(By.xpath("//div[contains(@class, 'bottom float_fix')]//div[contains(@class,'section')]"));

                userNameInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userNameData.get(0));
                userIds = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userPageIdData.get(0));
                userInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userInfoData.get(0));

                // Extracting user page Id.
                Pattern userPageIdPattern = Pattern.compile("<a href=\".+?personal_page_id=(.+?)\">");
                Matcher matcher = userPageIdPattern.matcher(userIds);
                matcher.find();
                String userPageId = matcher.group(1);
                System.out.println(userPageId);
                user.setPageId(Integer.valueOf(userPageId));


                // Extracting user gender.
                Pattern userGenderPattern = Pattern.compile("<span>(.+?)<\\/span>");
                matcher = userGenderPattern.matcher(userInfo);
                matcher.find();
                String userGender = matcher.group(1);
                String gender[] = userGender.split(" ", 2);
                userGender = gender[0];
                System.out.println(userGender.replace(",",""));
                user.setGender(userGender);

                // Extracting user date joined.
                Pattern userDateJoinedPattern = Pattern.compile("since( .+? .+.?)");
                matcher = userDateJoinedPattern.matcher(userInfo);
                matcher.find();
                String userDateJoined = matcher.group(1);
                System.out.println(userDateJoined.trim());
                user.setDateJoined(userDateJoined.trim());


                //******************************* GATHER USER's FRIEND LIST ************************************************//
                System.out.println("\n\nStaring to parse " + user.getUserName() + "'s friends list ...");

//                chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );
                chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber);
                chromeDriver.manage().deleteAllCookies();

                // Check if user has any friends ?
                List<WebElement> anyFriends = chromeDriver.findElements(By.xpath("//div[starts-with(@class, 'friend_box')]"));

                System.out.println("Number of friends : " + anyFriends.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg')]"));
                System.out.println(paginationNumber.size() + " pages of friends");


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber);
                    chromeDriver.manage().deleteAllCookies();

                    // Loop to find all the friends

                    for ( int i=0; i < anyFriends.size(); i++){

                        anyFriends = chromeDriver.findElements(By.xpath("//div[starts-with(@class, 'friend_box')]"));
                        String friendsData = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", anyFriends.get(i));

                        // New user object
                        User friend = new User();

                        // Extract Friend Names
                        Pattern friendNamePattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        matcher = friendNamePattern.matcher(friendsData);
                        matcher.find();
                        String friendName = matcher.group(1);
                        System.out.println(friendName);
                        friend.setUserName(friendName);

                        // Extract Friend page Links
                        Pattern friendPageLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\"user.+?\">");
                        matcher = friendPageLinkPattern.matcher(friendsData);
                        matcher.find();
                        String friendPageLink = matcher.group(1);
                        friendPageLink = "http://www.medhelp.org" + friendPageLink;
                        System.out.println(friendPageLink);
                        friend.setUserPageLink(friendPageLink);

                        // Extracting Friend unique Id.
//                        Pattern friendUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"user_(.+?)_.+?\">");
//                        matcher = friendUniqueIdPattern.matcher(friendsData);
//                        matcher.find();
//                        String friendUniqueId = matcher.group(1);
//                        System.out.println(friendUniqueId);
//                        friend.setUniqueId(Integer.valueOf(friendUniqueId));

                        Pattern friendUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"(.+?)\">");
                        matcher = friendUniqueIdPattern.matcher(friendsData);
                        matcher.find();
                        String friendUniqueId = matcher.group(1);
                        String arr[] = friendUniqueId.split("_");
                        friendUniqueId = arr[1];
                        System.out.println(friendUniqueId);
                        friend.setUniqueId(Integer.valueOf(friendUniqueId));

                        // Add user to Set of Friends.
                        friendList.add(friend);

                        if (!doesUserExist(completeUsersList, friend)){
                            System.out.println("Adding new User ...");
                            completeUsersList.add(friend); // *************************** REPLACE WITH friend
                        }
                    }

                    pageNumber++;
                    System.out.println("\n\nUser Page number is: " + pageNumber);

                }while (pageNumber < (paginationNumber.size()+1));

                user.setFriendsList(friendList);

                System.out.println("User :" + user.getUserName() + " has " + user.friendsList.size() + " Friends");

                System.out.println("Writing user friend data to the file");

                userFileText = user.getUserName() +  ", " + user.getUserPageLink() + ", " + user.getPageId() + ", " + user.getFriendsList().size() + "\n";

                for(User friend: friendList){
                    userFileText = userFileText + " , , , , " + friend.getUserName() + ", " + friend.getUserPageLink() + "\n";
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                bufferedWriter.write(userFileText);
                bufferedWriter.close();

                System.out.println(userFileText);

            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                e.getStackTrace();
                e.printStackTrace();
                continue;
            }


        }

        // Adding new found users to existing list of users
        // userList.addAll(newUsers);

        try{
//             dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());


            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("UsersComplete-" + dateString + ".csv"), "utf-8"));
            for(User user: completeUsersList){
                userFileText = userFileText + user.getUserName() + ", " + user.getUserPageLink() + "," + user.getPageId() + "\n";
            }

            System.out.println(userFileText);

            writer.write(userFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        chromeDriver.close();
        chromeDriver.quit();

        return userList;
    }













    public static List<User> scrapeNotes(List<User> userList){

        System.out.println("\n\n********************************** IN NOTES FUNCTION ***************************************");


        System.setProperty("webdriver.chrome.driver", dir+"/Selenium/chromedriver");
        File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriver = new ChromeDriver(chrome);
        chromeDriver.navigate().to("http://www.medhelp.org");
        chromeDriver.manage().deleteAllCookies();


//        WebDriver chromeDriver = new FirefoxDriver();

        String noteEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> noteEntryData;
        List<WebElement> paginationNumber;

//        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

        // Writing out data to file
        File file = new File(dir+"/UsersNotes" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<Note> notesList = new ArrayList<Note>();

            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber);

            try{


//                FirefoxProfile firefoxprofile = new FirefoxProfile();
//                File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
//                firefoxprofile.addExtension(addonpath);
//                chromeDriver = new FirefoxDriver(firefoxprofile);
//                 Go to the Notes Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() + "?page=" + pageNumber);
                chromeDriver.manage().deleteAllCookies();

                String pageSource = chromeDriver.getPageSource();

                if(pageSource.contains("File Not Found")){
                    continue;
                }

                noteEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'note_') and contains(@class, 'note_entry float_fix')]")); // noteEntryData.size() # of notes
//                System.out.println("Number of notes on 1st page: " + noteEntryData.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_page')]")); // # of pages
                Integer paginationIndex = paginationNumber.size();
                System.out.println(paginationNumber.size() + " Notes pages");



                //******************************* OBTAIN NOTES DATA ************************************************//
                System.out.println("\n\nOn " + user.getUserName() +"'s notes list page ");

//                chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber);
                    chromeDriver.manage().deleteAllCookies();
                    paginationNumber.clear();

                    // Loop to find all the friends

                    for ( int i=0; i < noteEntryData.size(); i++){

                        // Gathering data on comments for this particular thread
                        noteEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'note_') and contains(@class, 'note_entry float_fix')]")); // noteEntryData.size() # of notes
                        noteEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", noteEntryData.get(i));

                        // New note object
                        Note note = new Note();

                        // Extract note author name
                        Pattern authorNamePattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        Matcher matcher = authorNamePattern.matcher(noteEntryInfo);
                        matcher.find();
                        String authorName = matcher.group(1);
                        System.out.println(authorName);
                        note.setNoteOriginator(authorName);


                        // Extracting date note was left.
                        Pattern noteDatePattern = Pattern.compile("<div>(.+?)<\\/div>");
                        matcher = noteDatePattern.matcher(noteEntryInfo);
                        matcher.find();
                        String noteDate = matcher.group(1);
                        System.out.println(noteDate);
                        note.setNoteDate(noteDate);

                        // Add note to list of Notes.
                        notesList.add(note);


                    }


                    pageNumber++;
                    System.out.println("\n\nNotes Page number is: " + pageNumber);

                    //Update number of pages
                    // Check if friends list spans multiple pages
                    paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_next_page')]")); // # of pages
//                    paginationIndex = paginationNumber.size();
                    System.out.println(paginationNumber.size() + " Notes pages updated");


//                }while (pageNumber < (Integer.parseInt(paginationNumber.get(paginationIndex).getText()) +1));
                }while (paginationNumber.size()>0);


                System.out.println("Writing " + notesList.size() + " notes to the file");

                // Writing out data to file
                // Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Notes-" + dateString + ".csv"), "utf-8"));

                String noteFileText = user.getUserName() + " , " +  notesList.size() + "\n"; // Last comma means on newline in Excel skip one cell

                for(Note note: notesList){
                    noteFileText = noteFileText + " , , " + note.printToFile();
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                System.out.println(noteFileText);

                bufferedWriter.write(noteFileText);
                bufferedWriter.close();

            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            user.setFriendsNotes(notesList);
            System.out.println("User : " + user.getUserName() + " has " + user.friendsNotes.size() + " notes" );

        }

        chromeDriver.close();
        chromeDriver.quit();

        return userList;
    }




    public static List<User> scrapeFriends(List<User> userList){

        System.out.println("\n\n********************************** IN FRIENDS FUNCTION ***************************************");

        System.setProperty("webdriver.chrome.driver", dir+"/Selenium/chromedriver");
        File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriver = new ChromeDriver(chrome);
        chromeDriver.navigate().to("http://www.medhelp.org");
        chromeDriver.manage().deleteAllCookies();


//        WebDriver chromeDriver = new FirefoxDriver();

        String friendEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> friendEntryData;
        List<WebElement> paginationNumber;

//        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

        // Writing out data to file
        File file = new File(dir+"/UsersFriends" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<User> friendList = new ArrayList<User>();

            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber);

            try{


//                FirefoxProfile firefoxprofile = new FirefoxProfile();
//                File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
//                firefoxprofile.addExtension(addonpath);
//                chromeDriver = new FirefoxDriver(firefoxprofile);
                // Go to the Friend Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() + "?page=" + pageNumber);
                chromeDriver.manage().deleteAllCookies();

                String pageSource = chromeDriver.getPageSource();

                if(pageSource.contains("File Not Found")){
                    continue;
                }

                friendEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'friend_') and contains(@class, 'friend_box th_border')]")); // friendEntryData.size() # of friends
                //                System.out.println("Number of friends on 1st page: " + friendEntryData.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_page')]")); // # of pages
                Integer paginationIndex = paginationNumber.size();
                System.out.println(paginationNumber.size() + " Friend pages");



                //******************************* OBTAIN FRIEND DATA ************************************************//
                System.out.println("\n\nOn " + user.getUserName() +"'s friends list page ");

                //                chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber);
                    chromeDriver.manage().deleteAllCookies();
                    paginationNumber.clear();

                    // Loop to find all the friends

                    for ( int i=0; i < friendEntryData.size(); i++){

                        // Gathering data on comments for this particular thread
                        friendEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'friend_') and contains(@class, 'friend_box th_border')]")); // friendEntryData.size() # of notes
                        friendEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", friendEntryData.get(i));
//                        System.out.println("URL :" + friendEntryInfo);

                        // New note object
                        User friend = new User();

                        // Extract Friend Names
                        Pattern friendNamePattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        Matcher matcher = friendNamePattern.matcher(friendEntryInfo);
                        matcher.find();
                        String friendName = matcher.group(1);
                        System.out.println(friendName);
                        friend.setUserName(friendName);

                        // Extract Friend page Links
                        Pattern friendPageLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\"user.+?\">");
                        matcher = friendPageLinkPattern.matcher(friendEntryInfo);
                        matcher.find();
                        String friendPageLink = matcher.group(1);
                        friendPageLink = "http://www.medhelp.org" + friendPageLink;
                        System.out.println(friendPageLink);
                        friend.setUserPageLink(friendPageLink);

                        // Extracting Friend unique Id.
//                        Pattern friendUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"user_(.+?)_.+?\">");
//                        matcher = friendUniqueIdPattern.matcher(friendEntryInfo);
//                        matcher.find();
//                        String friendUniqueId = matcher.group(1);
//                        System.out.println(friendUniqueId);
//                        friend.setUniqueId(Integer.valueOf(friendUniqueId));

                        Pattern friendUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"(.+?)\">");
                        matcher = friendUniqueIdPattern.matcher(friendEntryInfo);
                        matcher.find();
                        String friendUniqueId = matcher.group(1);
                        String arr[] = friendUniqueId.split("_");
                        friendUniqueId = arr[1];
                        System.out.println(friendUniqueId);
                        friend.setUniqueId(Integer.valueOf(friendUniqueId));

                        // Add user to Set of Friends.
                        friendList.add(friend);

//                        if (!doesUserExist(completeUsersList, friend)){
//                            System.out.println("Adding new User ...");
//                            completeUsersList.add(friend); // *************************** REPLACE WITH friend
//                        }
                    }



                    pageNumber++;
                    System.out.println("\n\nNotes Page number is: " + pageNumber);

                    // Update number of pages
                    // Check if friends list spans multiple pages
                    paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_next_page')]")); // # of pages
                    //                    paginationIndex = paginationNumber.size();

                    //                 }while (pageNumber < (paginationNumber.size()+1));

                }while (paginationNumber.size()>0);


                user.setFriendsList(friendList);

                System.out.println("User :" + user.getUserName() + " has " + user.friendsList.size() + " Friends");

                System.out.println("Writing " + friendList.size() + " user friend data to the file");

                String userFileText = user.getUserName() +  ", " + user.getUserPageLink() + ", " + user.getUniqueId() + ", " + user.getFriendsList().size() + "\n";

                if(!(friendList==null)){
                    for(User friend: friendList){
                        userFileText = userFileText + " , , , , " + friend.getUserName() + ", " + friend.getUserPageLink() + "\n";
                    }
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                bufferedWriter.write(userFileText);
                bufferedWriter.close();

                System.out.println(userFileText);


            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            user.setFriendsList(friendList);
            System.out.println("User : " + user.getUserName() + " has " + user.getFriendsList().size() + " friends" );

        }

        chromeDriver.close();
        chromeDriver.quit();

        return userList;
    }











    public static List<User> scrapePosts(List<User> userList){

        System.out.println("\n\n********************************** IN POST FUNCTION ***************************************");

        System.setProperty("webdriver.chrome.driver", dir+"/Selenium/chromedriver");
        File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriver = new ChromeDriver(chrome);
        chromeDriver.navigate().to("http://www.medhelp.org");
        chromeDriver.manage().deleteAllCookies();


//        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String postFileText = "";

        String postEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> postEntryData;
        List<WebElement> paginationNumber;
        List<Post> postsList;


//        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

        File file = new File(dir+"/Posts-" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            postsList = new ArrayList<Post>();

//            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());
            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber);

            try{

//                FirefoxProfile firefoxprofile = new FirefoxProfile();
//                File addonpath = new File(dir+"/Selenium/AdBlock_v2.47.crx");
//                firefoxprofile.addExtension(addonpath);
//                chromeDriver = new FirefoxDriver(firefoxprofile);
//                 Go to the Posts Page itself and collect data on the posts left on the page.
//                chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());
                chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber);
                chromeDriver.manage().deleteAllCookies();

                // Gathering data on comments for this particular thread
                postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                System.out.println("Number of posts on 1st page: " + postEntryData.size());


                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg')]")); // # of pages
                System.out.println(paginationNumber.size() + " pages of posts");



                //******************************* OBTAIN NOTES DATA ************************************************//

                System.out.println("\n\nOn " + user.getUserName() +"'s posts list page ");


                do {

                    System.out.println("\n\nPost Page number is: " + pageNumber);
//                    chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );
                    chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber);
                    chromeDriver.manage().deleteAllCookies();

                    // Loop to find all the friends

                    for ( int i=0; i < postEntryData.size(); i++){

                        postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                        postEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", postEntryData.get(i));

                        // New note object
                        Post post = new Post();

                        // Extract post name
                        Pattern postNamePattern = Pattern.compile("<a href=\"\\/posts\\/.+?\">(.+?)<\\/a>");
                        Matcher matcher = postNamePattern.matcher(postEntryInfo);
                        matcher.find();
                        String postName = matcher.group(1);
                        System.out.println(postName);
                        post.setPostName(postName);


                        // Extracting date note was left.
                        Pattern postDatePattern = Pattern.compile("<span class=\"date\">(.+?) in the<\\/span>");
                        matcher = postDatePattern.matcher(postEntryInfo);
                        matcher.find();
                        String postDate = matcher.group(1);
                        System.out.println(postDate);
                        post.setPostDate(postDate);


                        // Extracting community posted in
                        Pattern postInCommunityPattern = Pattern.compile("<a href=\"\\/forums\\/.+?\">(.+?)<\\/a>");
                        matcher = postInCommunityPattern.matcher(postEntryInfo);
                        matcher.find();
                        String postInCommunity = matcher.group(1);
                        System.out.println(postInCommunity);
                        post.setPostCommunity(postInCommunity);

                        // Add post to postList
                        postsList.add(post);

                    }

                    pageNumber++;

                }while (pageNumber < (paginationNumber.size()+1));

                user.setUserPosts(postsList);

                postFileText = "";

                postFileText = postFileText + user.getUserName()  + ", " + postsList.size() + "\n"; // Last comma means on newline in Excel skip one cell

                for(Post post: postsList){
                    postFileText = postFileText + " , , " + post.printToFile();
                }

                System.out.println("Writing post data to the file");

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                System.out.println(postFileText);

                bufferedWriter.write(postFileText);
                bufferedWriter.close();


            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            System.out.println("User :" + user.getUserName() + " has " + user.userPosts.size() + "\n\n");


//            try{
////            dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
//
//                System.out.println("Writing post data to the file");
//
//                // Writing out data to file
//                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Posts-" + dateString + ".csv"), "utf-8"));
//                //for(User user: userList){
//
//                    postFileText = postFileText + user.getUserName() + ", " + user.getUserPosts().size() + "\n"; // Last comma means on newline in Excel skip one cell
//
//                    for(Post post: user.getUserPosts()){
//                        postFileText = postFileText + " , , " + post.printToFile();
//                    }
//               // }
//
//                System.out.println(postFileText);
//
//                writer.write(postFileText);
//                writer.close();
//
//            } catch (Exception e) {
//                System.err.println("Caught Exception: " + e.getMessage());
//            }

        }


        chromeDriver.close();
        chromeDriver.quit();

        return userList;
    }




}
