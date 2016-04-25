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
import java.net.URL;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 5/16/15
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */


public class MedHelpScraper extends Thread{

    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

    public static String timeStamp = dateString;

    public static void main(String[] args)  throws Exception {

        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);
        WebDriver chromeDriverGen = new ChromeDriver(chrome);


//        FirefoxProfile firefoxprofile = new FirefoxProfile();
//        File addonpath = new File(dir+"/Selenium/adblock_plus-2.7.1.xpi");
//        firefoxprofile.addExtension(addonpath);
//        WebDriver chromeDriver = new FirefoxDriver(firefoxprofile);
//        chromeDriver.navigate().to("https://www.medhelp.org");

        // File String to save to file object
        String fileText = "";

        // different page numbers to parse
        Integer pageNumber =1;

        // Number of threads parsed
        Integer threadNumber=0;

        // WebElement from Selenium
        List<WebElement> newSubjectElement;

        // List of threads analyzed and stored
        List<Thread> threadList = new ArrayList<Thread>();

        // List of thread commentors. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> threadCommentors;
        List<User> threadCommentors = new ArrayList<User>();


        // List of all users both commentors and thread creators. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> userList = new ArrayList<User>();
        List<User> userList = new ArrayList<User>();

        System.out.println("Gathering data ...\n");


        //************************************************* START SCRAPPING FORUM FOR THE THREADS *************************************************//
        List<String> forumList = new ArrayList<String>();
        forumList.add("http://www.medhelp.org/forums/Depression/show/57");
//        forumList.add("http://www.medhelp.org/forums/Anxiety/show/71");
//        forumList.add("http://www.medhelp.org/forums/Relationships/show/78");
//        forumList.add("http://www.medhelp.org/forums/Stress/show/162");
        forumList.add("http://www.medhelp.org/forums/Nutrition/show/58");
//        forumList.add("http://www.medhelp.org/forums/Undiagnosed-Symptoms/show/95");
//        forumList.add("http://www.medhelp.org/forums/Heart-Disease/show/72");


        //Depression
        //Anxeity
        //Relationships
        //Stress
        //Nutrition
        //Undiagnosed
        //Heart Disease


        WebDriver chromeDriver = chromeDriverGen;
        chromeDriver.navigate().to("http://disney.com/");

        for (String forumlink: forumList) {

            fileText = "";
            pageNumber =1;
            threadNumber=0;

            threadList = new ArrayList<Thread>();
            threadCommentors = new ArrayList<User>();

            String forumName = forumlink.substring(30).replaceAll("/show/\\d+","");

            System.out.print("//************************************************* START SCRAPPING " + forumName + " *************************************************//\n");


            dateString = timeStamp + "-" + forumName;

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("InitialThreads-" + dateString + ".csv"), "utf-8"));

            do {
                // pageNumber is automatically incremented at the end of this loop so next page can be crawled. This happens until pages have
                // no more data i.e. threads.

                chromeDriver = chromeDriverGen;
                chromeDriver.manage().deleteAllCookies();
                chromeDriver.manage().deleteAllCookies();
                chromeDriver.navigate().to(forumlink + "?page=" + pageNumber);


                // Check if this page has any data
                newSubjectElement = chromeDriver.findElements(By.className("new_subject_element"));

                // Start looking through the threads
                List<WebElement> threadsList = chromeDriver.findElements(By.className("subject_summary"));


                for (int i = 0; i < threadsList.size(); i++) {
//            for ( int i=0; i < 4; i++){

                    String nseUrl = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", newSubjectElement.get(i));
                    nseUrl = nseUrl.substring(1, nseUrl.length() - 1).replace("\n","").trim();
//                    System.out.println(nseUrl);

                    // Extract Thread Names

                    try {

                        String Url = (String) ((JavascriptExecutor) chromeDriver).executeScript("return arguments[0].innerHTML;", threadsList.get(i));
                        Url = Url.substring(1, Url.length() - 1).trim();
//                    System.out.println(Url);

                        // Instantiate a new Thread
                        Thread thread = new Thread();
                        User user = new User();

                        thread.setThreadPageNumber(pageNumber);
                        thread.setThreadNumber(threadNumber++);
                        System.out.print("\nPage number: " + pageNumber + " Thread number: " + threadNumber  + ", ");


                        // Extract Thread Date
                        Pattern threadDatePattern = Pattern.compile("<span class=\"post_date\">(.+?)<\\/span>");
                        Matcher matcher;
                        try{

                            if (Url.contains("<span class=\"post_date\">")) {
                                matcher = threadDatePattern.matcher(Url);
                            } else {
                                Pattern threadDatePattern1 = Pattern.compile("<span class=\".+? mh_timestamp\" data-timestamp=\".+?\">(.+?)<\\/span>");
                                matcher = threadDatePattern1.matcher(Url);
                            }
                            matcher.find();
                            String threadDate = matcher.group(1);

                            System.out.print(threadDate  + ", ");
                            thread.setDateCreated(threadDate);

                        } catch (Exception e) {
                            System.err.print("Caught Exception: " + e.getMessage());
                            String threadDate = new SimpleDateFormat("MMM dd, yyyy").format(new Date());
                            thread.setDateCreated(threadDate);
                        }

                        // Extract Thread Names
                        Pattern threadNamePattern = Pattern.compile("<a href=\".+?\">(.+?)<\\/a>");
                        matcher = threadNamePattern.matcher(Url);
                        matcher.find();
                        String threadName = matcher.group(1);
                        System.out.print(threadName + ", ");
                        thread.setThreadName(threadName);

                        // Extract Thread Links
                        Pattern threadLinkPattern = Pattern.compile("<a href=\"(.+?)\">");
                        matcher = threadLinkPattern.matcher(Url);
                        matcher.find();
                        String threadLink = matcher.group(1);
                        threadLink = "http://www.medhelp.org" + threadLink;
//                        System.out.print(threadLink  + ", ");
                        thread.setThreadLink(threadLink);

                        // Extract Thread Creator
                        Pattern threadCreatorPattern = Pattern.compile("<span><a href=\".+?\">(.+?)<\\/a><\\/span>");
                        if (threadCreatorPattern.matcher(Url).matches()) {
                            matcher = threadCreatorPattern.matcher(Url);
                        } else {
                            Pattern threadCreatorPattern1 = Pattern.compile("<a href=\".+?\" id=\"user_.+?\">(.+?)<\\/a>");
                            matcher = threadCreatorPattern1.matcher(Url);
                        }

                        matcher.find();
                        User threadCreator = new User(matcher.group(1));
                        System.out.print(threadCreator.userName  + ", ");
                        thread.setThreadCreator(threadCreator);


                        // Extract Link to Thread Creator's Page
                        Pattern threadCreatorLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\".+?\">.+?<\\/a>");
                        matcher = threadCreatorLinkPattern.matcher(Url);
                        matcher.find();
                        String threadCreatorLink = matcher.group(1);
                        threadCreatorLink = "http://www.medhelp.org" + threadCreatorLink;
//                        System.out.print(threadCreatorLink + ", ");
                        thread.setThreadCreatorLink(threadCreatorLink);

                        user.setUserPageLink(threadCreatorLink);


                        // Extract Thread numberComments
                        Pattern numberCommentsPattern = Pattern.compile("<div class=\"se_text_replies\">(.+?)<\\/div>");
                        Pattern numberCommentsPattern1 = Pattern.compile("<span class=\"fa-stack-1x count\">(.+?)<\\/span>");
                        Integer numberComments = 0;
//                        TimeUnit.SECONDS.sleep(1);

                        try {


                            if (numberCommentsPattern.matcher(nseUrl).find()) {
                                matcher = numberCommentsPattern.matcher(nseUrl);
                                matcher.find();
                                numberComments = Integer.parseInt(matcher.group(1).trim());
                            } else if (numberCommentsPattern1.matcher(nseUrl).find()) {
                                matcher = numberCommentsPattern1.matcher(nseUrl);

                                while (matcher.find()) {
                                    numberComments = numberComments + Integer.parseInt(matcher.group(1).trim());
//                                    System.out.println(numberComments);

                                }
                            }


                        } catch (Exception e) {
                            System.err.println("Caught Exception: " + e.getMessage());
                        }

                        System.out.print(numberComments + ", ");
                        thread.setCommentsNumber(numberComments);


                        // Extracting thread creator (user) unique Id.
                        Pattern threadCreatorUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"(.+?)\">.+?<\\/a>");
                        matcher = threadCreatorUniqueIdPattern.matcher(Url);
                        matcher.find();
                        String userUniqueId = matcher.group(1);
                        String arr[] = userUniqueId.split("_");
                        userUniqueId = arr[1];
//                        System.out.print(userUniqueId);
                        user.setUniqueId(Integer.valueOf(userUniqueId));
                        user.setUserName(threadCreator.getUserName());


                        if (!doesUserExist(userList, user)) {
                            System.out.print("Adding new User\n");
                            userList.add(user);
                        }


                        threadList.add(thread);

//                        System.out.println();
                        writer.write(thread.printToFile());


                    } catch (Exception e) {
                        System.err.println("Caught Exception: " + e.getMessage());
//                        continue;
                    }


                }

                // Increment Page number, there are usually about 20 threads on one page. For this Forum there are about 29 pages.
                // The first run in file "Threads1.csv" has data for all of the 29 pages. About 576 Threads
                pageNumber++;

//            } while (pageNumber < 11);
            }while(!(newSubjectElement.isEmpty()) ); // Set the number of Pages you want to crawl here. This Forum has about 29 pages crawlable.
//        } while(pageNumber < 3 | !(newSubjectElement.isEmpty())  );


            //******************************* END OF PAGE SCRAPPING FOR THREADS ************************************************//

            //******************************* START ACCESSING THREADS THEMSELVES TO GET DETAILED INFO **************************//


            userList = scrapeThreads(threadList, userList);

            userList = scrapeUsers(userList);


//        System.out.println("*************USERSCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

//        userList = createUserListFromFile(dir+"/UsersComplete-2016-04-17-Heart-Disease.csv");

            System.out.println("userList size after thread scrape : " + userList.size());


            userList = scrapeNotes(userList);

//        System.out.println("*************NOTESCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

            System.out.println("\n\nuserList size after notes scrape : " + userList.size());


            userList = scrapePosts(userList);

//        System.out.println("*************POSTCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

            System.out.println("userList size after posts scrape : " + userList.size());


            userList = scrapeFriends(userList);

//        System.out.println("*************USERSCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

            System.out.println("userList size after friends scrape : " + userList.size());


            try {
                String userFileText = "";

//            String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

                System.out.println("Writing final user data to the file ...\n");

                // Writing out data to file
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ThreadUsers-" + dateString + ".csv"), "utf-8"));

                for (User user : userList) {
//                userFileText = userFileText + user.userName + "\n";

                    userFileText = userFileText + user.printToFile();

                }

                System.out.println(userFileText);

                writer.write(userFileText);
                writer.close();

            } catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
            }

            //******************************* END OF THREAD DATA EXTRACTION ************************************************//

        }

        chromeDriver.close();
        chromeDriver.quit();

    }


















    public static Boolean doesUserExist(List<User> userList, User newUser){
        Boolean value = Boolean.FALSE;

        for(User user: userList){
            if((newUser.userName).equals(user.userName)){
                value = Boolean.TRUE;
                if((newUser.toString()).length() > (user.toString()).length()){
                    value = Boolean.FALSE;
                }
                System.out.println("***** User <" + user.userName + "> already exists *****");
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
        WebDriver chromeDriverGen = new ChromeDriver(chrome);
        WebDriver chromeDriver = chromeDriverGen;
        chromeDriver.navigate().to("http://disney.com/");
        chromeDriver.manage().deleteAllCookies();

//        WebDriver chromeDriver = new FirefoxDriver();
        String threadfileText = "";

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Threads-" + dateString + ".csv"), "utf-8"));


            // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
            for(Thread thread: threadList){

                String threadCommentorsData = "";
                String numberOfComments = "";
                String commentsNumber = "";
                Integer commentPagesNumber = 0;
                List<User> threadCommentors;

                System.out.println("\n\nThread " + thread.getThreadNumber() + " of " + threadList.size());

                try{

//                System.out.println(thread.appLink);


                    // Go to the Thread Page itself and collect data on the comments left on the page.
                    chromeDriver = chromeDriverGen;
                    chromeDriver.navigate().to(thread.threadLink);
                    chromeDriver.manage().deleteAllCookies();

                    List<WebElement> commentPagesData = new ArrayList<WebElement>();
                    // Check if the thread has multiple pages.
                    try{                                                           //<div id="msg_current_page" class="msg_page_1">2</div>

                        commentPagesData = chromeDriver.findElements(By.xpath("//a[contains(@class, 'page_nav')]"));
                        if(commentPagesData.size()>0){
                            commentPagesNumber = commentPagesData.size();
//                            Boolean isAdded = Boolean.FALSE;
//                            if(commentPagesNumber>1){
//
//                                WebDriver chromePageDriver = chromeDriverGen; // Create a new page driver
//                                commentPagesData = new ArrayList<WebElement>(); // Re-initialize the WebElement List
//
//                                    for(int i=1; i<commentPagesNumber+1; i++){
//                                    chromePageDriver.navigate().to(thread.threadLink+"?page="+i);
//                                    isAdded = commentPagesData.addAll(chromePageDriver.findElements(By.className("post_entry")));
//                                    if(!isAdded){
//                                        commentPagesData.addAll(chromePageDriver.findElements(By.className("question_by")));
//                                    }
//                                }
//
//                            }

                        } else if(thread.getCommentsNumber()>200){
                            commentPagesData = chromeDriver.findElements(By.xpath("//div[contains(@id, 'msg_current_page')]"));
                            String commentPages = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", commentPagesData.get(0));
                            commentPagesNumber =Integer.parseInt(commentPages);
//                            Boolean isAdded = Boolean.FALSE;
//
//                            if(commentPagesNumber>1){
//
//                                WebDriver chromePageDriver = chromeDriverGen; // Create a new page driver
//                                commentPagesData = new ArrayList<WebElement>(); // Re-initialize the WebElement List
//
//                                for(int i=1; i<commentPagesNumber+1; i++){
//                                    chromePageDriver.navigate().to(thread.threadLink+"?page="+i);
//                                    isAdded = commentPagesData.addAll(chromePageDriver.findElements(By.className("question_by")));
//                                    if(!isAdded){
//                                        commentPagesData.addAll(chromePageDriver.findElements(By.className("post_entry")));
//                                    }
//                                }
//
//                            }
                        }



                    }catch (Exception e){
                    }
                    System.out.println(thread.threadLink);

//                 Gathering data on comments for this particular thread
                    try {
                        List<WebElement> subjectCommentsNumber = chromeDriver.findElements(By.className("comments_num"));
                        if(subjectCommentsNumber.size()>0){
                            numberOfComments = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", subjectCommentsNumber.get(0));
                            numberOfComments = numberOfComments.trim().replace("\n","");
                        }

                    }catch (Exception e){
                        System.err.println("Caught Exception: " + e.getMessage());
                    }

                    try {
                        List<WebElement> answerCount = chromeDriver.findElements(By.className("answer_count"));
                        if(answerCount.size()>0){
                            numberOfComments = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", answerCount.get(0));
                            numberOfComments = numberOfComments.trim().replace("\n","");
                        }

                    }catch (Exception e){
                        System.err.println("Caught Exception: " + e.getMessage());
                    }


//                System.out.println(numberOfComments);


                    // Extracting number of comments in this thread.
                    Pattern numberOfCommentsPattern = Pattern.compile("<a href=\"#comments_header\">(.+?) .+?<\\/a>");
                    Matcher matcher;
                    if(numberOfCommentsPattern.matcher(numberOfComments).matches()){
                        matcher = numberOfCommentsPattern.matcher(numberOfComments);
                        matcher.find();
                        commentsNumber = matcher.group(1);
                    } else if(numberOfComments.trim().contains("Answer")) {
                        Pattern numberOfCommentsPattern1 = Pattern.compile("<div class=\"answer_count\">(.+?) .+?<\\/div>");
                        matcher = numberOfCommentsPattern1.matcher(numberOfComments);
                        String arr[] = numberOfComments.split(" ");
                        commentsNumber = arr[0];
                    }


                    //Check if comments number is "" an empty string
                    if(commentsNumber.length()>1) {
                        commentsNumber = commentsNumber;
                    } else {
                        commentsNumber = "0";
                    }

                    try {
                        commentsNumber = (thread.getCommentsNumber() > Integer.parseInt(commentsNumber)) ? thread.getCommentsNumber().toString() : commentsNumber;

                    }catch (Exception e){
                        System.err.println("Empty commentsNumber: " + e.getMessage());
                    }

                    System.out.println("Number of comments: " + commentsNumber);
                    thread.setCommentsNumber(Integer.valueOf(commentsNumber));


                    // Instantiate list to save all the users who commented in this thread.
                    threadCommentors = new ArrayList<User>();

                    // Loop to find all the thread commentors
                    if(Integer.valueOf(commentsNumber) > 0){

                        // If this page has any data


                        List<WebElement> threadData = new ArrayList<WebElement>();
                        List<WebElement> postMessage = new ArrayList<WebElement>();
                        try {
                            threadData = chromeDriver.findElements(By.className("question_by"));

                        }catch (Exception e){
                            System.err.println("Caught Exception: " + e.getMessage());
                        }

                        try {
                            postMessage = chromeDriver.findElements(By.className("post_entry"));

                        }catch (Exception e){
                            System.err.println("Caught Exception: " + e.getMessage());
                        }


                        threadData = threadData.size() > postMessage.size() ? threadData : postMessage;

                        Integer cueToNextPage = threadData.size();

                        for ( int i=0; i < threadData.size(); i++){

                            // After all comments have been collected. Move to the next page
                            // and collect the rest of the comment information.
                            if( commentPagesNumber>0 && (i+1) == threadData.size()){

                                Boolean isAdded = Boolean.FALSE;
                                if(commentPagesNumber>0){
                                    chromeDriver.navigate().to(thread.threadLink+"?page="+commentPagesNumber);
                                    threadData = chromeDriver.findElements(By.className("post_entry"));
                                    isAdded = commentPagesData.addAll(chromeDriver.findElements(By.className("post_entry")));

                                    if(!isAdded){
                                            threadData = chromeDriver.findElements(By.className("question_by"));
                                        }
                                    i = 0;
                                    commentPagesNumber--;
                                }

                            }

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
                            if(arr.length>3){
                                userUniqueId = arr[4];
                                int endIndex = userUniqueId.indexOf("\"");
                                userUniqueId = arr[4].substring(0,endIndex);
                            } else{
                                userUniqueId = arr[1];
                            }
                            System.out.println(userUniqueId);
                            commentor.setUniqueId(Integer.valueOf(userUniqueId));


                            // Add user to list of thread commentors.
                            threadCommentors.add(commentor);

                            // Add user to list of users.
//                            if (!doesUserExist(userList, commentor)){
//                                System.out.println("Adding new User");
//                                userList.add(commentor);
//                            }

                        }
                    }

                }catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }

                // Save list of commentors to this particluar thread
                thread.setCommentors(threadCommentors);

                writer.write(thread.printToFile());



            }

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }
        //******************************* END OF THREAD DATA EXTRACTION ************************************************//

        try {
//            String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());


            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ThreadsFinal-" + dateString + ".csv"), "utf-8"));
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
        chromeDriver.navigate().to("http://disney.com/");
        chromeDriver.manage().deleteAllCookies();



//        WebDriver chromeDriver = new FirefoxDriver();
        try{


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
            Integer index = 0;
            for(User user: userList){

                index++;

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
                    System.out.println("userPageId: " + userPageId);
                    user.setPageId(Integer.valueOf(userPageId));


                    // Extracting user gender.
                    Pattern userGenderPattern = Pattern.compile("<span>(.+?)<\\/span>");
                    matcher = userGenderPattern.matcher(userInfo);
                    matcher.find();
                    String userGender = matcher.group(1);
                    String gender[] = userGender.split(" ", 2);
                    userGender = gender[0];
                    System.out.println("userGender: " + userGender.replace(",",""));
                    user.setGender(userGender);

                    // Extracting user date joined.
                    Pattern userDateJoinedPattern = Pattern.compile("since( .+? .+.?)");
                    matcher = userDateJoinedPattern.matcher(userInfo);
                    matcher.find();
                    String userDateJoined = matcher.group(1);
                    System.out.println("DateJoined: " + userDateJoined.trim());
                    user.setDateJoined(userDateJoined.trim());


                    //******************************* GATHER USER's FRIEND LIST ************************************************//
                    System.out.println("\n\n\t\t\t\t**************** Staring to parse user number " + index + ": " + user.getUserName() + "'s friends list ... **************** \n\n");

//                chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );
//                    chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber);
                    chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId());
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

//                            if (!doesUserExist(completeUsersList, friend)){
//                                System.out.println("Adding new User ...");
//                                completeUsersList.add(friend); // *************************** REPLACE WITH friend
//                            }
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
//                    continue;
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


        }catch (Exception e) {
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
        chromeDriver.navigate().to("http://wikipedia.com/");
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

            System.out.println("\n\n User number: " + userList.indexOf(user)+ " and User name: " + user.userName + " with link " + "http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber);

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
                        String noteDate = "";

                        while (matcher.find()) {
//                    System.out.print(matcher.group(1) + ", ");
                            if ( matcher.group(1).length() > 10) {
                                noteDate = "";
                            } else {
                                noteDate = matcher.group(1);
                                System.out.println(noteDate);
                            }
                        }

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
//                continue;
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
        chromeDriver.navigate().to("http://wikipedia.com/");
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
//                continue;
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
        chromeDriver.navigate().to("http://disney.com/");
        chromeDriver.manage().deleteAllCookies();


//        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String postFileText = "";

        String postEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> postEntryData;
        List<WebElement> postPagesData = new ArrayList<WebElement>();
        Integer paginationNumber;
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
            String postsNumber = "";
            Integer postPagesNumber = 0;


            postsList = new ArrayList<Post>();

//            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());
            System.out.println("\n\nUser number: " + userList.indexOf(user)+ " and User name: " + user.userName + " with link " + "http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber);

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
//                postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                postEntryData = chromeDriver.findElements(By.xpath("//div[contains(@class, 'stats')]")); // postEntryData.size() # of post
                String numberOfPosts = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", postEntryData.get(0));

                // Extract post number
                if(numberOfPosts.contains("of")){
                    numberOfPosts = numberOfPosts.replace("-","").replace("of","").replace("(","").replace(")","").replace("posts","").replace("post","").trim();
                    String arr[] = numberOfPosts.split("  ");
                    numberOfPosts = arr[2];

                } else {
                    numberOfPosts = numberOfPosts.replace("posts","").replace("post","").trim();
                }

                Integer pagination = Integer.parseInt(numberOfPosts);

                if(pagination>10){
                    paginationNumber = (pagination / 10) + (pagination % 10);

                }else{
                    paginationNumber = 1;
                }

                System.out.println("Total number of pages: " + paginationNumber);
                System.out.println("Total number of posts: " + numberOfPosts);
                System.out.println("Number of posts on 1st page: " + postEntryData.size());


                // Check if friends list spans multiple pages
//                paginationNumber = chromeDriver.findElements(By.xpath("//a[contains(@class, 'page_nav')]")); // # of pages
                System.out.println(paginationNumber + " pages of posts");
                postPagesNumber = pageNumber;


                //******************************* OBTAIN NOTES DATA ************************************************//

                System.out.println("\n\nOn " + user.getUserName() +"'s posts list page ");


                do {

                    System.out.println("\n\nPost Page number is: " + pageNumber);
//                    chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );
                    chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber);
                    chromeDriver.manage().deleteAllCookies();

                    postEntryData = chromeDriver.findElements(By.xpath("//div[contains(@class, 'user_post')]")); // postEntryData.size() # of post on that page.
                    Integer postPerPage = postEntryData.size();

                    // Loop to find all the friends

                    for ( int i=0; i < postPerPage; i++){


                        // After all comments have been collected. Move to the next page
                        // and collect the rest of the comment information.
                        if( postPagesNumber < paginationNumber && (i+1) == postPerPage){

                            postPagesNumber++;

                            Boolean isAdded = Boolean.FALSE;
//                            if(postPagesNumber < paginationNumber){
                                chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + postPagesNumber);
                                postEntryData = chromeDriver.findElements(By.className("user_post"));
                                isAdded = postPagesData.addAll(chromeDriver.findElements(By.className("user_post")));

//                                if(!isAdded){
//                                    postEntryData = chromeDriver.findElements(By.className("question_by"));
//                                }
                                i = 0;
//                            }

                        }

//                        postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                        postEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", postEntryData.get(i));
                        postEntryInfo = postEntryInfo.replace("\t","").replace("\n","").trim();

                        // New note object
                        Post post = new Post();

                        // Extract post name
                        Pattern postNamePattern = Pattern.compile("<a href=\"\\/posts\\/.+?\">(.+?)<\\/a>");
                        Matcher matcher = postNamePattern.matcher(postEntryInfo);
                        matcher.find();
                        String postName = matcher.group(1).trim();
                        System.out.println(postName);
                        post.setPostName(postName);


                        // Extracting date note was left.
//                        Pattern postDatePattern = Pattern.compile("<span class=\"date\">(.+?) in the<\\/span>");
                        Pattern postDatePattern = Pattern.compile("<div class=\"time\">(.+?)<\\/div>");
                        matcher = postDatePattern.matcher(postEntryInfo);
                        matcher.find();
                        String postDate = matcher.group(1).replace(",","").trim();
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

                }
                while (pageNumber < (paginationNumber+1));

                user.setUserPosts(postsList);

                postFileText = "";

                postFileText = postFileText + user.getUserName()  + ", " + postsList.size() + "\n"; // Last comma means on newline in Excel skip one cell

                for(Post post: postsList){
                    postFileText = postFileText + " , , " + post.printToFile();
                }

                System.out.println("Writing post data to the file... " + user.getUserName()  + ", and size" + postsList.size() );

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


        }


        chromeDriver.close();
        chromeDriver.quit();

        return userList;
    }











    public static List<User> buildUserList(){

        List<User> userList = new ArrayList<User>();


        // ***
        userList = createUserListFromFile("/Users/johnshu/Desktop/WebScraper/UsersComplete-2016-04-17-Heart-Disease.csv");

        System.out.println("userList size after thread scrape : " + userList.size());

        userList = scrapeUsers(userList);

        userList = scrapeNotes(userList);

//        System.out.println("*************NOTESCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

        System.out.println("userList size after notes scrape : " + userList.size());


        userList = scrapePosts(userList);

//        System.out.println("*************POSTCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

        System.out.println("userList size after posts scrape : " + userList.size());


        userList = scrapeFriends(userList);

// ***
        return userList;

    }


}





