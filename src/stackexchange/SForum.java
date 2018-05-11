package stackexchange;

import hackforums.Comment;
import org.apache.xerces.impl.xpath.regex.Match;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.medhelp.Thread;
import org.medhelp.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import sun.org.mozilla.javascript.internal.ast.SwitchCase;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hackforums.HackForums.scrapeThreads;
import static hackforums.HackForums.scrapeUsers;

public class SForum extends Thread {

    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

    public static String timeStamp = dateString;

    public static String forumName = "";

    public static void main(String[] args)  throws Exception {

        //        List<User> userList1 = new ArrayList<User>();

        //        scrapeUsers(userList1);

        //    buildUserList();

        //        convertTimeToSecsArray();

        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);

        TimeUnit.SECONDS.sleep(4);


        ChromeDriver SEForumDriver = new ChromeDriver(chrome);
        //        SEForumDriver.navigate().to("https://security.stackexchange.com/search?tab=Votes&pagesize=50&q=ransomware");
//        SEForumDriver.navigate().to("https://stackexchange.com/search?q=ransomware&pagesize=15");
        SEForumDriver.navigate().to("https://stackexchange.com/search?q=ransomware&page=98");
        SEForumDriver.switchTo();


        Document fullDoc = Jsoup.parse(SEForumDriver.getPageSource());


        // File String to save to file object
        String fileText = "";

        String threadFileText = "";

        // File String to save to file object
        String fileTextHeading = "ThreadName," + "ThreadLink," + "Creator," + "Creator_Page" + "\n";

        // different page numbers to parse
        Integer pageNumber =1;

        // Number of threads parsed
        Integer threadNumber=0;

        // Total number of threads in forum
        Integer totalThreads=0;

        // WebElement from Selenium
        List<WebElement> newSubjectElement;

        // List of threads analyzed and stored
        List<Thread> threadList = new ArrayList<Thread>();

        // List of thread commentors. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> threadCommentors;
        List<User> threadCommentors = new ArrayList<User>();

        List<WebElement> totalThreadsData = new ArrayList<WebElement>();
        // List of all users both commentors and thread creators. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> userList = new ArrayList<User>();
        List<User> userList = new ArrayList<User>();

        System.out.println("\n\nGathering data ...\n");


        //        scrapeThreads(threadList,userList);


        //************************************************* START SCRAPPING FORUM FOR THE THREADS *************************************************//
        List<String> forumList = new ArrayList<String>();
        //        forumList.add(SEForumDriver.getCurrentUrl());

        //        TimeUnit.SECONDS.sleep(3);


        try{

            //            for (String forumlink: forumList) {


            fileText = "";
            pageNumber =1;
            threadNumber=0;

            threadList = new ArrayList<Thread>();
            threadCommentors = new ArrayList<User>();


            // Extract number of threads on this page
            String totalThreadsStr = fullDoc.select("div.subheader.results-header > h2 "). first().text();
            String [] totalThreadsArr = totalThreadsStr.split(" ");
            //            totalThreadsStr.substring(0,3);
            totalThreads = Integer.valueOf(totalThreadsArr[0].replace(",",""));
            //              totalThreads = Integer.valueOf(Pattern.compile("(\\d+) results").matcher(totalThreadsStr).group(1));


            //                List<WebElement> commentPagesInfo = SEForumDriver.findElementsByClassName("question-summary");
            //                String commentPagesData = (String) ((JavascriptExecutor) SEForumDriver).executeScript("return arguments[0].innerHTML;", commentPagesInfo.get(0));

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("StackExchageForums-" + dateString + ".csv"), "utf-8"));


            // Writing the header to the file
            writer.write(fileTextHeading);


            do {

                List<WebElement> threadsList = SEForumDriver.findElementsByClassName("search-result");

                System.out.println("        //************************************************* PAGE NUMBER " + pageNumber+ "*************************************************//\n");


                for(WebElement threadHtml: threadsList){

                    String nseUrl = (String) ((JavascriptExecutor) SEForumDriver).executeScript("return arguments[0].innerHTML;", threadHtml);
                    String Url = nseUrl.replaceAll("\\<!--.+?-->","").trim();

//                                                                 System.out.println("\n" + Url);


                    // Extract Thread Names

                    try {

                        System.out.println(threadList.size());

                        // Instantiate a new Thread
                        Thread thread = new Thread();
                        User user = new User();


                        // Extract Thread Names and Link
                        Document doc = Jsoup.parse(Url);


                        Pattern threadNamePattern = Pattern.compile("<span><a href=\"https:\\/\\/(.+?).com\\/questions\\/(.+?)\">(.+?)<\\/a><\\/span>");
                        Matcher matcher = threadNamePattern.matcher(Url);
                        matcher.find();
                        String website = matcher.group(1);
                        String threadLink = matcher.group(2);
                        String threadName = matcher.group(3).trim().replace("&quot;","\"").replace("," ," ");
                        String FullthreadLink = "https://" + website + ".com/questions/" + threadLink;
                        thread.setThreadName(threadName);
                        thread.setThreadLink(FullthreadLink );
                        System.out.print(website + ", " + threadName + ", " + FullthreadLink + ", ");


                        // Extract Thread Date of Creation Asked
                        try {
                            Pattern threadDatePattern = Pattern.compile("asked <span title=\"(.+?) .+?\" class=\"relativetime\">.+?<\\/span> on");
                            matcher = threadDatePattern.matcher(Url);
                            matcher.find();
                            String threadDate = matcher.group(1);
                            thread.setDateCreated(threadDate);
                            System.out.print(thread.dateCreated + ", ");
                        } catch (Exception e){
//                            System.err.print("Caught Exception: " + "Ask Date not found ");

                        }

                        try {
                            Pattern threadDatePattern = Pattern.compile("answered <span title=\"(.+?) .+?\" class=\"relativetime\">.+?<\\/span> on");
                            matcher = threadDatePattern.matcher(Url);
                            matcher.find();
                            String threadDate = matcher.group(1);
                            thread.setDateCreated(threadDate);
                            System.out.print(thread.dateCreated + ", ");
                        } catch (Exception e){
//                            System.err.print("Caught Exception: " + "Answered Date not found ");

                        }

                        // Extract Thread Keywords
                        Pattern threadKeywordsPattern = Pattern.compile("<div class=\"tags user-tags\">\n" +
                                "(.+?)<\\/div>");
                        matcher = threadKeywordsPattern.matcher(Url);
                        matcher.find();
                        String htmlKeywords = matcher.group(0).trim();
                        String keywords = html2textV2(htmlKeywords);
                        thread.setKeywords(keywords);
                        System.out.print(keywords);

                        thread.setThreadCreator(user);

                        threadList.add(thread);

                        // Add user to list of users.
                        //                        if (!doesUserExist(userList, user)){
                        //                            userList.add(user);
                        //                        }
                        //                            userList.add(user);

                        //writer.write(thread.printToFileLite());


                    } catch (Exception e) {
                        System.err.println("Caught Exception: " + e.getMessage());
                        //                        continue;
                    }


                }

                // Increment Page number, there are usually about 20 threads on one page. For this Forum there are about 29 pages.
                // The first run in file "Threads1.csv" has data for all of the 29 pages. About 576 Threads
                pageNumber++;
                try {
                    //                    SEForumDriver.navigate().to("https://security.stackexchange.com/search?page="+pageNumber+"&tab=Votes&q=ransomware");
                    SEForumDriver.navigate().to("https://stackexchange.com/search?q=ransomware&page="+pageNumber+"");
                    TimeUnit.SECONDS.sleep(20);

                } catch (Exception e){

                }

                //                }while(!(newSubjectElement.isEmpty()) ); // Set the number of Pages you want to crawl here. This Forum has about 29 pages crawlable.
            } while(pageNumber < 1  ); // Used to test Final Function
//                        } while(pageNumber < (totalThreads / 15) + 1 );


            try {
                threadFileText = fileTextHeading;

                System.out.println("\nWriting thread data to the file ...\n");

                // Writing out data to file
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("StackExchangeForumThread-" + dateString + ".csv"), "utf-8"));

                for (Thread thread : threadList) {

                    threadFileText = threadFileText + thread.printSEForumThreadsToFile();

                }

                System.out.println(threadFileText);

                writer.write(threadFileText);
                writer.close();

            } catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
            }

            //******************************* END OF THREAD DATA EXTRACTION ************************************************//

            //            }


        } catch (Exception e) {
            System.err.println("Caught Exception in Main: " + e.getMessage());
        }



        HashMap<String, List> map;

        // *******************************REMOVING THAT LONGEST THREAD TO SHORTEN TESTING ************************************************//
        //        List<Thread> threadListTemp = new ArrayList<>();
        //        threadListTemp.add(threadList.get(2)); ; // *******************************REMOVING THAT LONGEST THREAD TO SHORTEN TESTING ************************************************//
        //        threadList = threadListTemp;
        // *******************************REMOVING THAT LONGEST THREAD TO SHORTEN TESTING ************************************************//


        // Scrape threads after getting basic list of users.
        map = scrapeThreads(threadList, userList);

        threadList = map.get("threadList");
        userList = map.get("userList");


        // Scrape user pages and fill out their information
        userList = scrapeUsers(userList);


        fileTextHeading  = "ThreadName" + "," + "ThreadLink" + "," + "Date_Created" + "," + "Creator"
                + "," + "Creator_Page" + "," + "Keywords" + "," + "Time_Active" + "," + "Times_Viewed" + "," + "Number_of_Comments" + "," + "Commentor" + "," +"Comments" + "\n";


        try {

            System.out.println("Writing Fully Detailed Thread data to the file ...\n");

            threadFileText = fileTextHeading;

            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("StackExchangeForumFullyDetailedThread-" + dateString + ".csv"), "utf-8"));

            for (Thread thread : threadList) {

                try {
                    threadFileText = threadFileText + thread.printSEForumThreadsToFile();
                }
                catch (Exception e){

                }

            }

            System.out.println(threadFileText);

            writer.write(threadFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }


        SEForumDriver.close();
        SEForumDriver.quit();

        // Shutdown and exit program when done
        System.exit(0);

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



    public static List<User> buildUserList(){

        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);


        ChromeDriver hackForumDriver = new ChromeDriver(chrome);
        hackForumDriver.navigate().to("https://hackforums.net/member.php?action=login");
        hackForumDriver.switchTo();


        WebElement username = hackForumDriver.findElementByName("username");
        WebElement password = hackForumDriver.findElementByName("password");

        username.sendKeys ("shujohns@gmail.com");
        password.sendKeys("MotDePasse2017!");
        hackForumDriver.findElementByName("submit").click();

        List<User> userList = new ArrayList<User>();
        List<Thread> threadList = new ArrayList<Thread>();

        dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

        //dateString = "2016-04-22 07-27-25-Depression 2";
        dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

        timeStamp = dateString;

        forumName = "HackForum"; // Change depending on Forum

        HashMap<String, List> map;

        // Gather user and forum information from file and return both threadlist and userlist to map
        map = createUserListFromFile("/Users/johnshu/Desktop/WebScraper/HackForums.csv");

        // Extract userlist from map function.
        userList = map.get("userList");

        // Extract threadList from map function.
        threadList = map.get("threadList");

        // Scrape threads after getting basic list of users.
        map = scrapeThreads(threadList, userList);

        userList = scrapeUsers(userList);

        System.out.println("userList size after thread scrape : " + userList.size());


        System.out.println("Starting time: " + dateString);
        String endingTime = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        System.out.println("Ending time: " + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));

        System.exit(0);
        return userList;

    }



    public static HashMap<String, List> scrapeThreads(List<Thread> threadList, List<User> userList){

        System.out.println("\n\n********************************** THREADS: GATHERING DATA ***************************************");


        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);


        ChromeDriver SEForumDriver = new ChromeDriver(chrome);
        //                ChromeDriver SEForumDriver = new ChromeDriver();
        SEForumDriver.navigate().to("https://stackexchange.com/search?q=ransomware&pagesize=50");
        SEForumDriver.switchTo();



        String threadfileText = "";

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ThreadsSForumDetails-" + dateString + ".csv"), "utf-8"));

            // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
            for(Thread thread: threadList) {

                String commentContent = "";
                Integer commentNumber = 0;
                List<Thread> subThreadsList = new ArrayList<Thread>();
                List<User> threadCommentorsList = new ArrayList<User>();
                List<Comment> commentsList = new ArrayList<Comment>();
                User originalAuthor = new User();

                System.out.println("\n\n********************************************************Thread " + threadList.indexOf(thread) + " of " + threadList.size()+"********************************************************\n");

                try {

                    // Go to the Thread Page itself and collect data on the comments left on the page.
                    SEForumDriver.navigate().to(thread.threadLink);

                    try {
                        TimeUnit.SECONDS.sleep(15); //15
                    }catch (Exception e){

                    }


                    // Find all the areas that need to be expanded and click on it.
                    List<WebElement> expandComments = SEForumDriver.findElementsByXPath("//a[contains(@title, 'expand to show all comments on this post')]");

                    for (WebElement expandComment : expandComments) {

                        try{
                            expandComment.click();
                            TimeUnit.SECONDS.sleep(5); //5

                        }catch (Exception e){
                            continue;
                        }
                        expandComments = SEForumDriver.findElementsByXPath("//a[contains(@title, 'expand to show all comments on this post')]");

                    }


                    //Gathering data on comments for this particular thread

                    List<WebElement> commentData = SEForumDriver.findElementsByXPath("//div[contains(@id, 'mainbar')]//div[contains(@id, 'question') and contains(@class, 'question')]");
                    List<WebElement> commentDataAnswers = SEForumDriver.findElementsByXPath("//div[contains(@id, 'mainbar')]//div[contains(@id, 'answer') and contains(@class, 'answer')]");

                    commentData.addAll(commentDataAnswers);

                    //                            if (commentData.isEmpty()) { // Some posts have questions some answers so if its not a question its an answer
                    //                                commentData = SEForumDriver.findElementsByXPath("//div[contains(@id, 'mainbar')]//div[contains(@id, 'answers')]");
                    //                            }

                    //                    List<WebElement> commentTimeInfo = SEForumDriver.findElementsByClassName("user-action-time");
                    //                    List<WebElement> commentorNamesInfo = SEForumDriver.findElements(By.xpath("//td[contains(@class, 'post_author')]//strong//span[contains(@class, 'largetext')]"));
                    //                    List<WebElement> commentBodyInfo = SEForumDriver.findElements(By.xpath("//div[contains(@class, 'post_body')]"));


                    // Instantiate list to save all the users who commented in this thread.
                    threadCommentorsList = new ArrayList<User>();

                    Document fullDoc = Jsoup.parse(SEForumDriver.getPageSource());
                    String questionData = fullDoc.getElementById("qinfo").text();
                    String[] splits = questionData.split("asked | viewed | active");
                    String timeActive="";


                    if(splits[1].contains("year")) {
                        if(splits[1].contains("month")) {
                            String[] timeAsked = splits[1].replace("years", "").replace("year", "").replace("months", "").replace("month", "").replace("ago", "").split(",");
                            timeActive = String.valueOf(Integer.valueOf(timeAsked[0].trim()) * 12 + Integer.valueOf(timeAsked[1].trim()));
                        } else {
                            String[] timeAsked = splits[1].replace("years", "").replace("year", "").replace("months", "").replace("month", "").replace("ago", "").split(",");
                            timeActive = String.valueOf(Integer.valueOf(timeAsked[0].trim())* 12);
                        }

                    }else if(splits[1].contains("month")){

                        String[] timeAsked = splits[1].replace("months","").replace("month","").replace("ago", "").split(",");
                        timeActive = String.valueOf(Integer.valueOf(timeAsked[0].trim()));

                    }else if(splits[1].contains("day")){
                        String[] timeAsked = splits[1].replace("days","").replace("day", "").replace("ago", "").split(",");
                        timeActive = String.valueOf(Integer.valueOf(timeAsked[0].trim())/30);

                    }

                    String timesViewed = splits[2].replace("times","").replace(",","").trim();

                    thread.setTimeActive(timeActive);
                    thread.setTimesViewed(timesViewed);
                    System.out.print(timeActive + " months, " + timesViewed + " times, ");

                    for (WebElement commentHtml : commentData) {

                        try {

                            commentContent = (String) ((JavascriptExecutor) SEForumDriver).executeScript("return arguments[0].innerHTML;", commentHtml);

                            //                                                                System.out.println("commentContent: " + commentContent);

                            // New user object

                            //User commentor = new User();
                            //Comment comment = new Comment();
                            Thread subthread = new Thread();
                            List<User> subThreadCommentor = new ArrayList<User>();
                            List<Comment> subCommentsList = new ArrayList<Comment>();
                            Comment comment = new Comment();
                            User commentor = new User();



                            // Extract Thread Creator
                            if(subThreadCommentor.isEmpty()) {
                                Pattern threadCreatorPattern = Pattern.compile("<div class=\"user-details\">\n" +
                                        "        <a href=\"\\/users(.+?)\">(.+?)<\\/a>");
                                Matcher matcher = threadCreatorPattern.matcher(commentContent);
                                matcher.find();
                                User user = new User(matcher.group(2).trim());
                                user.setUserPageLink("https://security.stackexchange.com/users" + matcher.group(1).trim());
                                System.out.print(user.userName + ", " + user.userPageLink + " \n\n");

                                thread.setThreadCreator(user);
                                subThreadCommentor.add(user);
                            }

                            // Extract Comment time and date Asked
                            try { // Two different patterns for the date and time. If the first one doesnt work the second part will do
                                Pattern commentTimePattern = Pattern.compile("asked <span title=\"(.+?) .+?\" class=\"relativetime\">(.+?) at (.+?)<\\/span>");
                                Matcher matcher = commentTimePattern.matcher(commentContent);
                                if(matcher.find()){
                                    String commentDate = matcher.group(1);
                                    String commentTime = matcher.group(3);
                                    System.out.print(commentTime.trim() + "\n" + commentDate.trim() + "\n");
                                    comment.setCommentTime(commentTime.trim());
                                    comment.setCommentDate(commentDate.trim());
                                } else{
                                    commentTimePattern = Pattern.compile("asked <span title=\"(.+?) .+?\" class=\"relativetime\">(.+?) at (.+?)<\\/span>");
                                    matcher = commentTimePattern.matcher(commentContent);
                                    if(matcher.find()){
                                        String commentDate = matcher.group(1);
                                        String commentTime = matcher.group(3);
                                        System.out.print(commentTime.trim() + "\n" + commentDate.trim() + "\n");
                                        comment.setCommentTime(commentTime.trim());
                                        comment.setCommentDate(commentDate.trim());
                                    }
                                }

                            } catch (Exception e){

                            }

                            // Extract Comment time and date Answered
                            try { // Two different patterns for the date and time. If the first one doesnt work the second part will do
                                Pattern commentTimePattern = Pattern.compile("answered <span title=\"(.+?) .+?\" class=\"relativetime\">(.+?) at (.+?)<\\/span>");
                                Matcher matcher = commentTimePattern.matcher(commentContent);
                                if(matcher.find()){
                                    String commentDate = matcher.group(1);
                                    String commentTime = matcher.group(3);
                                    System.out.print(commentTime.trim() + "\n" + commentDate.trim() + "\n");
                                    comment.setCommentTime(commentTime.trim());
                                    comment.setCommentDate(commentDate.trim());
                                } else{
                                    commentTimePattern = Pattern.compile("answered <span title=\"(.+?) .+?\" class=\"relativetime\">(.+?) at (.+?)<\\/span>");
                                    matcher = commentTimePattern.matcher(commentContent);
                                    if(matcher.find()){
                                        String commentDate = matcher.group(1);
                                        String commentTime = matcher.group(3);
                                        System.out.print(commentTime.trim() + "\n" + commentDate.trim() + "\n");
                                        comment.setCommentTime(commentTime.trim());
                                        comment.setCommentDate(commentDate.trim());
                                    }
                                }

                            } catch (Exception e){

                            }


                            // Extract First Comment Author //<a href="\/users(.+?)" title=".+?" class="comment-user">(.+?)<\/a> || <a href="\/users.+?">(.+?)<\/a>
                            Pattern firstCommentorNamePattern = Pattern.compile(" <div class=\"user-details\">\n" +
                                    "        <a href=\"\\/users.+?\">(.+?)<\\/a>");
                            Matcher matcher = firstCommentorNamePattern.matcher(commentContent);
                            String firstCommentorName = "";
                            while (matcher.find()) { // Will keep matching till it finds the last, which is the original authro of the thread e.g. Arminius and Rory McCune
                                firstCommentorName = matcher.group(1);
                                //                                    if(!(firstCommentorName.contains(">"))) // If the match does not contain any html we know its a full name we can break out of this
                                //                                        break;                              // loop and continue. If not continue till you find a full name without html
                            }
                            System.out.print(firstCommentorName + "\n");
                            User firstCommentor = new User(firstCommentorName.trim());
                            subThreadCommentor.add(firstCommentor);


                            // Extract First Comment response
                            WebElement firstCommentInfo = commentHtml.findElement(By.className("post-text"));
                            String firstCommentContent = (String) ((JavascriptExecutor) SEForumDriver).executeScript("return arguments[0].innerHTML;", firstCommentInfo);

                            //                                    System.out.println("firstCommentContent: " + firstCommentContent);

                            String firstCommentText = html2textV2(firstCommentContent);
                            firstCommentContent = html2textV2(firstCommentContent);
                            System.out.print(firstCommentText.trim() + "\n");
                            comment.setComment(firstCommentText.trim());
                            comment.setCommentType("Question");
                            subCommentsList.add(new Comment(firstCommentContent, "", "", firstCommentor, ""));

                            // Declare some variables to use.
                            Matcher dataMatcher;
                            Matcher completeMatcher;
                            Matcher commentorMatcher;

                            Pattern commentContentPattern;
                            Pattern commentorNamesPattern;
                            Pattern commentTimePattern;

                            try {
                                WebElement commentContents = commentHtml.findElement(By.className("comment-body"));
                                String commentContentText = (String) ((JavascriptExecutor) SEForumDriver).executeScript("return arguments[0].innerHTML;", commentHtml);

//                                System.out.println("commentContentText: " + commentContentText);


                                //START Extract Comment body .......................
                                commentContentPattern = Pattern.compile("<span class=\"comment-copy\">(.+?)<\\/span>");
                                completeMatcher = commentContentPattern.matcher(commentContentText);

                                commentorNamesPattern = Pattern.compile("<a href=\"\\/users(.+?)\" title=\".+?\" class=\"comment-user\">(.+?)<\\/a>");
                                commentorMatcher = commentorNamesPattern.matcher(commentContentText);

                                commentTimePattern = Pattern.compile("<span title=\"(.+?) .+?\" class=\"relativetime.+?\">(.+?) at (.+?)<\\/span>");
                                dataMatcher = commentTimePattern.matcher(commentContentText);


                                while (completeMatcher.find() && commentorMatcher.find() && dataMatcher.find()) {
                                    comment = new Comment();
                                    String commentText = completeMatcher.group(1);
                                    commentText = html2textV2(commentText);
                                    System.out.print(commentText + "\n");

                                    comment.setComment(commentText);
                                    comment.setCommentType("Response");
                                    subCommentsList.add(comment);
                                    //                                    }

                                    // START Extract Commentors ............

                                    String commentorPage = "";
                                    String commentorName = "";
                                    //                                commentorMatcher.find();
                                    //                                    while (matcher.find()) {
                                    commentor = new User();
                                    commentorPage = commentorMatcher.group(1);
                                    commentorName = commentorMatcher.group(2);
                                    commentorName = html2textV2(commentorName);
                                    commentorPage = commentorPage.replace("amp;", "");
                                    System.out.print(commentorName.trim() + "\n" + "https://security.stackexchange.com/users" + commentorPage.trim() + "\n");
                                    commentor.setUserName(commentorName.trim()); // create a User Object
                                    commentor.setUserPageLink("https://security.stackexchange.com/users" + commentorPage.trim());
                                    comment.setCommentor(commentor); // Save the user object as the commentor
                                    subThreadCommentor.add(commentor);
                                    // END Extract Commentors ............


                                    //

                                    // START of Extract Comment time and date
                                    try { // Two different patterns for the date and time. If the first one doesnt work the second part will do

                                        //                                    if(dataMatcher.find()){
                                        String commentDate = dataMatcher.group(1);
                                        String commentTime = dataMatcher.group(3);
                                        System.out.print(commentTime.trim() + "\n" + commentDate.trim() + "\n");
                                        comment.setCommentTime(commentTime.trim());
                                        comment.setCommentDate(commentDate.trim());
                                        //                                    }

                                    } catch (Exception e){

                                        commentTimePattern = Pattern.compile("<span title=\"(.+?) .+?\" class=\"relativetime\">(.+?) at (.+?)<\\/span>");
                                        dataMatcher = commentTimePattern.matcher(commentContentText);
                                        dataMatcher.find();
                                        //                                    if(dataMatcher.find()){
                                        String commentDate = dataMatcher.group(1);
                                        String commentTime = dataMatcher.group(3);
                                        System.out.print(commentTime.trim() + "\n" + commentDate.trim() + "\n");
                                        comment.setCommentTime(commentTime.trim());
                                        comment.setCommentDate(commentDate.trim());
                                        //                                    }

                                    }
                                    // END of Extract Comment time and date

                                }
                                //END Extract Comment body .......................


                            }catch (Exception e){
                                System.err.println("Caught Exception: " + e.getMessage());
//                                continue;
                            }



                            // ****** UPDATE THE LIST ********* //
                            subthread.setCommentors(subThreadCommentor);
                            subthread.setCommentList(subCommentsList);
                            subThreadsList.add(subthread);

                            // Add user to list of thread commentors.
                            threadCommentorsList.addAll(subThreadCommentor);

                            // Add the comment to the list of comments in this thread.
                            commentsList.addAll(subCommentsList);

                            // Add the comment to the list of comments in this thread.
                            subThreadsList.add(subthread);


                            // Add user to list of users.
                            for (User commenter : subThreadCommentor) {

                                if (!doesUserExist(userList, commenter)) {
                                    userList.add(commenter);
                                }
                            }
                        }
                        catch (Exception e) {
                            System.err.println("Caught Exception: " + " Inside commentData try");
                            continue;
                        }

                        System.out.println("\n\n ************************************************************************ NEXT SUB THREAD ************************************************************************\n\n");
                    }

                    //                            thread.subThread.addAll(subThreadsList);
                    //                            thread.commentList.addAll(commentsList);
                    //                            thread.commentors.addAll(threadCommentorsList);


                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }

                // Save List of comments to this particular thread
                thread.setCommentList(commentsList);

                // Save list of commentors to this particluar thread
                thread.setCommentors(threadCommentorsList);

                originalAuthor = threadCommentorsList.get(0);

                thread.setThreadCreator(originalAuthor);

                writer.write(thread.printSEForumThreadsToFile());

            }



        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }


        //******************************* END OF THREAD DATA EXTRACTION ************************************************//

        try {

            // Writing out data to file

            String fileTextHeading = "ThreadName" + "," + "ThreadLink" + "," + "Date_Created" + "," + "Creator"
                    + "," + "Creator_Page" + "," + "Keywords" + "," + "Time_Active" + "," + "Times_Viewed" + "," + "Number_of_Comments" + "," + "Commentor" + "," +"Comments" + "\n";

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ThreadsSForumDetailsFinal-" + dateString + ".csv"), "utf-8"));

            threadfileText = threadfileText + fileTextHeading;

            for(Thread thread: threadList){
                threadfileText = threadfileText + thread.printSEForumThreadsToFile();
            }

            System.out.println(threadfileText);
            writer.write(threadfileText);
            writer.close();


        }catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        SEForumDriver.close();
        SEForumDriver.quit();

        HashMap<String, List> map =new HashMap();
        map.put("threadList", threadList);
        map.put("userList", userList);

        return map;
    }


    public static List<User> scrapeUsers(List<User> userList){

        System.out.println("\n\n********************************** IN USER FUNCTION ***************************************");

        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);


        ChromeDriver SEForumDriver = new ChromeDriver(chrome);
//        SEForumDriver.navigate().to("https://hackforums.net/member.php?action=login");
//        SEForumDriver.switchTo();

//        HashMap<String, List> map = createUserListFromFile("/Users/johnshu/Desktop/WebScraper/StackExchangeUserDetails.csv");

//        userList = map.get("userList");

        try{

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("StackExchangeForumUsersComplete-" + dateString + ".csv"), "utf-8"));

            String fileHeading = "User_Name" + "," + "Country" + "," + "Tenure" + "," + "Reputation" + "," + "Prestige" + "," + "Awards" + "," + "Profile Page" + "\n";


            // File String to save to file object
            String userFileText = "";
            String userName = "";
            String tenure = "";
            String reputation = "";
            String reach = "";
            String awards = "";
            String userPageLink = "";
            String stars = "";
            String country = "";



            // List to hold new found users.
            List<User> completeUsersList = new ArrayList<User>();

            // Add existing users to completeUserList
            completeUsersList.addAll(userList);

            //        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

            // Writing out data to file
            File file = new File(dir+"/UsersDetails" + dateString + ".csv");

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

                user.setUserPageLink(user.getUserPageLink().replace("amp;",""));

                System.out.println("\n\n" + user.getUserName()+ " with link " + user.userPageLink);

                try{


                    // Go to the Thread Page itself and collect data on the comments left on the page.
                    SEForumDriver.navigate().to(user.userPageLink);

                    Document fullDoc = Jsoup.parse(SEForumDriver.getPageSource());
                    String userData = fullDoc.getElementsByClass("list-unstyled").text();
                    System.out.println(userData);

                    String[] data = userData.split("Member for | Last");

                    country = "missing"; //Default condition
                    if(!(data[0].isEmpty())) {
                        String[] countryArray = data[0].split(" ");
                        country = countryArray[0] + " " + countryArray[1];
                        System.out.println(country.replace(",",""));
                        user.setCountry(country.replace(",",""));
                    }

                    String tenureData = data[1].substring(0, 17);

                    if(tenureData.contains("year")) {
                        if(tenureData.contains("month")) {
                            String[] timeAsked = tenureData.replace(",","").split(" ");
                            tenure = String.valueOf(Integer.valueOf(timeAsked[0].trim()) * 12 + Integer.valueOf(timeAsked[2].trim()));
                        } else {
                            String[] timeAsked = data[1].split(" ");
                            tenure = String.valueOf(Integer.valueOf(timeAsked[0].trim())* 12);
                        }

                    }else if(tenureData.contains("month")){
                        String[] timeAsked = tenureData.split(" ");
                        tenure = String.valueOf(Integer.valueOf(timeAsked[0].trim()));

                    }else if(tenureData.contains("day")){
                        String[] timeAsked = data[1].split(" ");
                        tenure = String.valueOf(Integer.valueOf(timeAsked[0].trim())/30);

                    }

                    System.out.println(tenure);
                    user.setTenure(tenure);

                    // Gathering data on comments for this particular thread
                    SEForumDriver.navigate().to(user.userPageLink.toString() + "?tab=topactivity");

                    fullDoc = Jsoup.parse(SEForumDriver.getPageSource());
                    reputation = fullDoc.getElementsByClass("g-col fl-none -rep").text();
                    System.out.println(reputation);
                    user.setReputation(reputation);

                    awards = fullDoc.getElementsByClass("g-row _gutters ai-start fl-none -row-first").text();
                    if(awards.length()>2) {
                        Integer total=0;
                        String[] awardsData = awards.split(" ");
                        for(String awardArrVal: awardsData){
                            total = total + Integer.valueOf(awardArrVal);
                        }
                        awards = String.valueOf(total);
                    }
                    System.out.println(awards);
                    user.setAwards(awards);

                    reach = fullDoc.getElementsByClass("g-col -reach-number").text().replace("~","");
                    System.out.println(reach);
                    user.setPrestige(reach.replace("~",""));

                }catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    e.getStackTrace();
                    e.printStackTrace();
                }

                System.out.println(user.printToFileSEForums());

            }



            try{

                userFileText = userFileText + fileHeading;

                // Writing out data to file

                for(User user: completeUsersList){
                    userFileText = userFileText + user.printToFileSEForums();
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

        SEForumDriver.close();
        SEForumDriver.quit();

        return userList;
    }



    public static HashMap<String, List> createUserListFromFile(String file){


        List<User> userList = new ArrayList<User>();
        List<Thread> threadList = new ArrayList<Thread>();


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
                        Thread thread = new Thread();
                        thread.setThreadName(attributes.get(0).trim());
                        thread.setThreadLink(attributes.get(1).trim());
                        user.setUserName(attributes.get(2).trim());
                        user.setUserPageLink(attributes.get(3).trim());



                        if (!doesUserExist(userList, user)){
                            System.out.println("\n\nAdding new User");
                            userList.add(user);
                            added++;
                        }

                        threadList.add(thread);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }


        HashMap<String, List> map =new HashMap();
        map.put("threadList", threadList);
        map.put("userList", userList);

        System.out.println("\n\n\n\n\n*****************\n" + "Total number of users is: " + userList.size() + "\n*****************\n");
        System.out.println("\n*****************\n" + "Total number of users added: " + added + "\n*****************\n\n\n\n\n");
        return map;
    }



    static boolean isInt(String s) {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }



    public static String convertTimeToSecs(String timeOnline){


        Integer finalTime = 0;

        try {
            if (timeOnline.matches("null")) {

                return "";
            } else {

                // 6 Months, 1 Week, 4 Days, 2 Hours, 3 Minutes, 17 Seconds

                String timeArray[] = timeOnline.split(",");

                for (String timeValue : timeArray) {

                    System.out.print(timeValue);

                    String timeTemp[] = timeValue.trim().split(" ", 2); // 2 limits the number of returning parts, so just the first space is used to split.
                    String condition = timeTemp[1].trim();
                    Integer timeVar = 0;

                    switch (condition) {
                        case "Year":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 31556952);
                                finalTime = finalTime + timeVar;

                            } catch (Exception e) {

                            }
                            break;
                        case "Years":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 31556952);
                                finalTime = finalTime + timeVar;

                            } catch (Exception e) {

                            }
                            break;
                        case "Month":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 2629746);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Months":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 2629746);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Week":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 604800);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Weeks":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 604800);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Day":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 86400);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Days":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 86400);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Hours":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 3600);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Hour":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 3600);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Minutes":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 60);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Minute":
                            try {
                                timeVar = (Integer.valueOf(timeTemp[0].trim()) * 60);
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Seconds":
                            try {
                                timeVar = Integer.valueOf(timeTemp[0].trim());
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        case "Second":
                            try {
                                timeVar = Integer.valueOf(timeTemp[0].trim());
                                finalTime = finalTime + timeVar;
                            } catch (Exception e) {

                            }
                            break;
                        default:
                            System.out.println("Default: No matching Value");
                    }

                }

                return finalTime.toString();
            }
        } catch (Exception e) {

        }

        return finalTime.toString();


    }



    public static List<String> convertTimeToSecsArray(){


        List<String> TimeOnline = readFile();

        List<String> TimeInSecs = new ArrayList<>();

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

        for(String timeOnline: TimeOnline) { // Loop through the Array List of Time Values

            Integer finalTime = 0;

            try {
                if (timeOnline.matches("null")) {

                    TimeInSecs.add("null");
                    //                    System.out.println("null");


                } else {

                    // timeOnline e.g. 1 Year, 6 Months, 1 Week, 4 Days, 2 Hours, 3 Minutes, 17 Seconds


                    Pattern pattern = Pattern.compile("\\w+ \\w+");
                    Matcher matcher = pattern.matcher(timeOnline);
                    String result="";
                    while(matcher.find()){
                        result = result + matcher.group() + ",";
                    }

                    //                    System.out.print(result);

                    List<String> timeArray = Arrays.asList(result.replaceFirst(".$","").split(",")); // replace last character (a comma) and then split by commas.

                    for (String timeValue : timeArray) {

                        String timeTemp[] = timeValue.split(" ", 2); // 2 limits the number of returning parts, so just the first space is used to split.
                        String condition = timeTemp[1].trim();
                        Integer timeVar = 0;

                        switch (condition) {
                            case "Year":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 31556952);
                                    finalTime = finalTime + timeVar;

                                } catch (Exception e) {

                                }
                                break;
                            case "Years":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 31556952);
                                    finalTime = finalTime + timeVar;

                                } catch (Exception e) {

                                }
                                break;
                            case "Month":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 2629746);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Months":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 2629746);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Week":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 604800);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Weeks":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 604800);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Day":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 86400);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Days":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 86400);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Hours":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 3600);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Hour":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 3600);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Minutes":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 60);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Minute":
                                try {
                                    timeVar = (Integer.valueOf(timeTemp[0].trim()) * 60);
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Seconds":
                                try {
                                    timeVar = Integer.valueOf(timeTemp[0].trim());
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            case "Second":
                                try {
                                    timeVar = Integer.valueOf(timeTemp[0].trim());
                                    finalTime = finalTime + timeVar;
                                } catch (Exception e) {

                                }
                                break;
                            default:
                                System.out.println("Default: No matching Value");
                        }

                    }

                    //                    System.out.println(finalTime.toString());
                    TimeInSecs.add(finalTime.toString());

                }
            } catch (Exception e) {

            }

        }

        for(String time: TimeInSecs)
            System.out.println(time);

        return TimeInSecs;

    }



    public static List<String> readFile(){

        File file = new File("/Users/johnshu/Desktop/WebScraper/timeOnline.csv");

        Integer count=0;

        List<String> TimeOnline = new ArrayList<>();

        try {
            Scanner input = new Scanner (file);
            while (input.hasNextLine()) {

                try {
                    String line = input.nextLine();

                    if(!(line == "")){
                        count++;
                        System.out.println("Printing Line :" + line);
                        TimeOnline.add(line);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return TimeOnline;
    }



    public static String html2text(String html) {
        //          return html.replaceAll("\\<.*?\\>", "");
        return Jsoup.parse(html).text();
    }



    public static String html2textV2(String html) {
        return html.replaceAll("\\<.*?\\>", "")
                   .replaceAll("\r","")
                   .replaceAll("\n","")
                   .trim();
        //        return Jsoup.parse(html).text();
    }


}
