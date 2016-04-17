package org.medhelp;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by johnshu on 1/31/16.
 */
public class ForumData {


    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + "-ForumData";


    public static void main(String[] args)  throws Exception {

//        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
//        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
//        ChromeOptions chrome = new ChromeOptions();
//        chrome.addExtensions(addonpath);
//        WebDriver chromeDriver = new ChromeDriver(chrome);
//        chromeDriver.navigate().to("https://www.medhelp.org");

//
        FirefoxProfile firefoxprofile = new FirefoxProfile();
        File addonpath = new File(dir+"/Selenium/adblock_plus-2.7.1.xpi");
        firefoxprofile.addExtension(addonpath);
        WebDriver firefoxDriver = new FirefoxDriver(firefoxprofile);
//        WebDriver firefoxDriver = new FirefoxDriver();
//        firefoxDriver.manage().deleteAllCookies();
        firefoxDriver.navigate().to("file:///Users/johnshu/Desktop/WebScraper/Site/All%20Ask%20a%20Doctor%20Forums%20And%20Medical%20Communities%20-%20MedHelp.html");

        // Number of threads parsed
        Integer forumNumber=0;

        // WebElement from Selenium
        List<WebElement> forumLink;

//        List of Forums analyzed and stored
        List<Forum> forumsList = new ArrayList<Forum>();

        System.out.println("Gathering data ...");

        //************************************************* START SCRAPPING FORUM FOR THE THREADS *************************************************//

        do{

//            chromeDriver.manage().deleteAllCookies();
//            chromeDriver.navigate().to("http://www.medhelp.org/forums/list");
//            chromeDriver.manage().deleteAllCookies();

//            http://www.medhelp.org/forums/Relationships/show/78?page=390
//            http://www.medhelp.org/forums/Divorce--Breakups/show/155?page=58
//            http://www.medhelp.org/forums/Pregnancy-Ages-18-24-/show/152?page=16460
//            http://www.medhelp.org/forums/Exercise--Fitness/show/69?page=246
//            http://www.medhelp.org/forums/High-Blood-Pressure---Hypertension/show/1222?page=34

            // Check if this page has any data
            forumLink = firefoxDriver.findElements(By.className("forums_link"));

            System.out.println(forumLink.size());

            // Start looking through the threads
//            List<WebElement> forumDataList =  chromeDriver.findElements(By.className("subject_summary"));

//            firefoxDriver.close();

            for ( int i=0; i < forumLink.size(); i++){
//            for ( int i=0; i < 4; i++){

//                firefoxDriver.manage().deleteAllCookies();
                // Extract Forum Names

                try{

                    String Url = (String)((JavascriptExecutor)firefoxDriver).executeScript("return arguments[0].innerHTML;", forumLink.get(i));
                    Url = Url.substring(1,Url.length()-1).trim();
//                    System.out.println(Url);

                    // Instantiate a new Thread
                    Forum forum = new Forum();
                    forumNumber++;
                    forum.setForumNumber(forumNumber);

                    Pattern forumPattern = Pattern.compile("<a href=\"(.+?)\">.+?<\\/a>");
                    Matcher matcher = forumPattern.matcher(Url);
                    matcher.find();
                    String link = matcher.group(1);
//                    link = "http://www.medhelp.org" + link;
                    forum.setForumLink(link);

                    forumPattern = Pattern.compile("<a href=\".+?\">(.+?)<\\/a>");
                    matcher = forumPattern.matcher(Url);
                    matcher.find();
                    String name = matcher.group(1);
                    System.out.print(name.trim() + ",");
                    forum.setForumName(name);



//                    WebDriver forumDriver = new FirefoxDriver(firefoxprofile);
//                    firefoxDriver.findElement(By.className("forums_link")).click();

                    WebDriver forumDriver = new FirefoxDriver(firefoxprofile);
                    forumDriver.navigate().to(forum.getForumLink());
                    List<WebElement> forumData = forumDriver.findElements(By.xpath("//span[contains(@class, 'forum_subject_count p')]"));
                    String forumInfo = (String) ((JavascriptExecutor) forumDriver).executeScript("return arguments[0].innerHTML;", forumData.get(0));

                    // Extract Thread Creator
                    Pattern countPattern = Pattern.compile("\\(of (.+?)\\)");
                    matcher = countPattern.matcher(forumInfo);
                    matcher.find();
                    String count = matcher.group(1);
                    System.out.print(count + ",");
                    forum.setNumberOfQuestions(count);

                    System.out.print(link.trim());

                    forumDriver.manage().deleteAllCookies();
                    forumDriver.manage();
                    forumDriver.quit();

                    forumsList.add(forum);


                }catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }


            }

//        } while(pageNumber < 5 );
        }while(!(forumLink.isEmpty()) ); // Set the number of Pages you want to crawl here. This Forum has about 29 pages crawlable.
//        } while(pageNumber < 3 | !(forumLink.isEmpty())  );

        //******************************* END OF PAGE SCRAPPING FOR THREADS ************************************************//

        //******************************* START ACCESSING THREADS THEMSELVES TO GET DETAILED INFO **************************//

        firefoxDriver.close();
        firefoxDriver.quit();



        try{
            String fileText = "";

            System.out.println("Writing forum data to the file ...\n");

            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ForumData-" + dateString + ".csv"), "utf-8"));
            for(Forum forum: forumsList){

                fileText = fileText + forum.printToFile();

            }

            System.out.println(fileText);

            writer.write(fileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        //******************************* END OF THREAD DATA EXTRACTION ************************************************//



    }


}
