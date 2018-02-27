package stackexchange;

import hackforums.Comment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.medhelp.Thread;
import org.medhelp.User;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hackforums.HackForums.scrapeThreads;
import static hackforums.HackForums.scrapeUsers;

public class SEForum extends Thread {

    public static String dir = "/Users/johnshu/Desktop/WebScraper"; // General directory root **** Be sure to CHANGE *****

    public static String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

    public static String timeStamp = dateString;

    public static String forumName = "";

    public static void main(String[] args)  throws Exception {

//        buildUserList();

//        convertTimeToSecsArray();

        System.setProperty("webdriver.chrome.driver", dir + "/Selenium/chromedriver");
        File addonpath = new File(dir + "/Selenium/AdBlock_v2.47.crx");
        ChromeOptions chrome = new ChromeOptions();
        chrome.addExtensions(addonpath);

        TimeUnit.SECONDS.sleep(4);


        ChromeDriver SEForumDriver = new ChromeDriver(chrome);
        SEForumDriver.navigate().to("https://security.stackexchange.com/search?tab=Votes&pagesize=50&q=ransomware");
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

        System.out.println("Gathering data ...\n");


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
                totalThreads = Integer.valueOf(totalThreadsStr.substring(0,3));
//              totalThreads = Integer.valueOf(Pattern.compile("(\\d+) results").matcher(totalThreadsStr).group(1));


//                List<WebElement> commentPagesInfo = SEForumDriver.findElementsByClassName("question-summary");
//                String commentPagesData = (String) ((JavascriptExecutor) SEForumDriver).executeScript("return arguments[0].innerHTML;", commentPagesInfo.get(0));

                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SEForums-" + dateString + ".csv"), "utf-8"));


                // Writing the header to the file
                writer.write(fileTextHeading);


                do {

                    List<WebElement> threadsList = SEForumDriver.findElementsByClassName("question-summary");

                    System.out.println("        //************************************************* PAGE NUMBER " + pageNumber+ "*************************************************//\n");


                    for(WebElement threadHtml: threadsList){

                        String nseUrl = (String) ((JavascriptExecutor) SEForumDriver).executeScript("return arguments[0].innerHTML;", threadHtml);
                        String Url = nseUrl.replaceAll("\\<!--.+?-->","").trim();
//                         System.out.println("\n" + Url);


                        // Extract Thread Names

                        try {

                            System.out.println(threadList.size());

                            // Instantiate a new Thread
                            Thread thread = new Thread();
                            User user = new User();


                            // Extract Thread Names and Link

                            Document doc = Jsoup.parse(Url);


                            Pattern threadNamePattern = Pattern.compile("<a href=\"(.+?)\" data-searchsession=\".+?\" title=\"(.+?)\">\n" +
                                    "Q:(.+?)<\\/a>");
                            Matcher matcher = threadNamePattern.matcher(Url);
                            matcher.find();
                            String threadLink = matcher.group(1);
                            String threadName = matcher.group(2).trim().replace("&quot;","\"");
                            System.out.print(threadName + ", " + threadLink + ", ");
                            thread.setThreadName(threadName);
                            thread.setThreadLink("https://security.stackexchange.com/" + threadLink );




                            // Extract Thread Creator
                            Pattern threadCreatorPattern = Pattern.compile("<a href=\"\\/users\\/(.+?)\">(.+?)<\\/a>");
                            matcher = threadCreatorPattern.matcher(Url);
                            matcher.find();
                            user = new User(matcher.group(2).trim());
                            user.setUserPageLink("https://security.stackexchange.com/users/" + matcher.group(1).trim());
                            System.out.print(user.userName  + ", " + user.userPageLink + " \n\n");

                            thread.setThreadCreator(user);

                            threadList.add(thread);

                            // Add user to list of users.
                            if (!doesUserExist(userList, user)){
                                userList.add(user);
                            }
//                            userList.add(user);

                            writer.write(thread.printToFileLite());


                        } catch (Exception e) {
                            System.err.println("Caught Exception: " + e.getMessage());
                            //                        continue;
                        }


                    }

                    // Increment Page number, there are usually about 20 threads on one page. For this Forum there are about 29 pages.
                    // The first run in file "Threads1.csv" has data for all of the 29 pages. About 576 Threads
                    pageNumber++;
                    try {
                        SEForumDriver.navigate().to("https://security.stackexchange.com/search?page="+pageNumber+"&tab=Votes&q=ransomware");
                        TimeUnit.SECONDS.sleep(10);

                    } catch (Exception e){

                    }

//                }while(!(newSubjectElement.isEmpty()) ); // Set the number of Pages you want to crawl here. This Forum has about 29 pages crawlable.
//                } while(pageNumber < 2  ); // Used to test Final Function
                } while(pageNumber < (totalThreads / 50) + 1 );


                try {
                    threadFileText = fileTextHeading;

                    System.out.println("Writing thread data to the file ...\n");

                    // Writing out data to file
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SEForumThread-" + dateString + ".csv"), "utf-8"));

                    for (Thread thread : threadList) {

                        threadFileText = threadFileText + thread.printToFileLite();

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


        String fileHeading = "User_Name" + ","
                + "Date_Joined" + ","
                + "Birth_Date" + ","
                + "Age" + ","
                + "Posts_Count" + ","
                + "Posts_Frequency"+ ","
                + "Posts_Percentage"+ ","
                + "Time_online"  + ","
                + "Time_in_secs"  + ","
                + "Reputation" + ","
                + "Prestige" + ","
                + "Awards" + ","
                + "Stars" + ","
                + "Profile_Page" + ","
                + "Number_of_Comments"+ ","
                + "Thread_Name"+ ","
                + "Thread_Link"  + ","
                + "Date_Created"  + ","
                + "Thread_Creator" + ","
                + "Comment_Type" + ","
                + "Commentors" + ","
                + "Comment_Date" + ","
                + "Comment_Time" + ","
                + "Comment_Text" + ","
                + "\n";

        try {

            System.out.println("Writing Fully Detailed Thread data to the file ...\n");

            threadFileText = fileHeading;

            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("HackForumFullyDetailedThread-" + dateString + ".csv"), "utf-8"));

            for (Thread thread : threadList) {

                try {
                    threadFileText = threadFileText + thread.printFullyDetailedThread(userList);
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
        SEForumDriver.navigate().to("https://security.stackexchange.com/search?tab=Votes&pagesize=50&q=ransomware");
        SEForumDriver.switchTo();

        try {
            TimeUnit.SECONDS.sleep(5);
        }catch (Exception e){

        }

//        WebElement username = SEForumDriver.findElementByName("username");
//        WebElement password = SEForumDriver.findElementByName("password");
//
//        username.sendKeys ("shujohns@gmail.com");
//        password.sendKeys("MotDePasse2017!");
//        SEForumDriver.findElementByName("submit").click();
//
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        }catch (Exception e){
//
//        }

//
        String threadfileText = "";

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ThreadsSEForumDetails-" + dateString + ".csv"), "utf-8"));

            // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
            for(Thread thread: threadList){

                String commentTimeData = "";
                String commentorNamesData = "";
                String commentBodyData = "";
                Integer commentPagesNumber = 0;
                Integer commentNumber = 0;
                List<User> threadCommentors;
                List<Comment> commentsList = new ArrayList<Comment>();

                System.out.println("\n\nThread " + threadList.indexOf(thread)+1 + " of " + threadList.size());

                try{

                    // Go to the Thread Page itself and collect data on the comments left on the page.
                    SEForumDriver.navigate().to(thread.threadLink);

                    // Click to expand more comments
                    List<WebElement> commentTimeInfo = SEForumDriver.findElementsByClassName("js-show-link");


                    //Gathering data on comments for this particular thread
                    List<WebElement> commentInfo = SEForumDriver.findElementsByClassName("question | answer");

//                    List<WebElement> commentTimeInfo = SEForumDriver.findElementsByClassName("user-action-time");
//                    List<WebElement> commentorNamesInfo = SEForumDriver.findElements(By.xpath("//td[contains(@class, 'post_author')]//strong//span[contains(@class, 'largetext')]"));
//                    List<WebElement> commentBodyInfo = SEForumDriver.findElements(By.xpath("//div[contains(@class, 'post_body')]"));


                    // Instantiate list to save all the users who commented in this thread.
                    threadCommentors = new ArrayList<User>();

                    TimeUnit.SECONDS.sleep(1); // timeout after navigating to every new page.

//                    commentPagesNumber = commentorNamesInfo.size();

//                    for ( int i=0; i < 11 ; i++){  // We expect to have 10 comments per page
//
//
//                        commentPagesNumber--; // count down the comments so you know when to move to next page or next thread
//                        commentNumber++; //count the number of comments parsed so you know that the first one is the creator
//
//                        // After all comments have been collected. Move to the next page
//                        // and collect the rest of the comment information.
//                        if( commentPagesNumber<0 ){
//
//                            // Check if there is a next page button
//                            if(commentorNamesInfo.size()==10){
//                                TimeUnit.SECONDS.sleep(1); // timeout before naviagting to a new page
//
//                                SEForumDriver.findElementByClassName("pagination_next").click();
//                                commentTimeInfo = SEForumDriver.findElements(By.xpath("//td[contains(@class, 'tcat')]//div[contains(@class, 'float_left smalltext')]"));
//                                commentorNamesInfo = SEForumDriver.findElements(By.xpath("//td[contains(@class, 'post_author')]//strong//span[contains(@class, 'largetext')]"));
//                                commentBodyInfo = SEForumDriver.findElements(By.xpath("//div[contains(@class, 'post_body')]"));
//                                commentPagesNumber = commentorNamesInfo.size();
//                                commentPagesNumber--; // count down the comments so you know when to move to next page or next thread
//                                commentNumber++; //count the number of comments parsed so you know that the first one is the creator
//                                i = 0;
//
//                            } else {
//                                break; // break out of the for loop and move to next thread
//                            }
//
//                        }

                        int i = 0;
                        commentTimeData = (String)((JavascriptExecutor)SEForumDriver).executeScript("return arguments[0].innerHTML;", commentTimeInfo.get(i));
                        commentorNamesData = (String)((JavascriptExecutor)SEForumDriver).executeScript("return arguments[0].innerHTML;", commentTimeInfo.get(i));
                        commentBodyData = (String)((JavascriptExecutor)SEForumDriver).executeScript("return arguments[0].innerHTML;", commentTimeInfo.get(i));

//                        System.out.println("commentTimeData: " + commentTimeData);
//                        System.out.println("commentorNamesDate: " + commentorNamesData);
//                        System.out.println("commentBodyData: " + commentBodyData);

                        // New user object
                        User commentor = new User();
                        Comment comment = new Comment();

                        // Extract Comment time and date
                        Pattern commentTimePattern = Pattern.compile("(.+?),(.+?)<span");
                        Matcher matcher = commentTimePattern.matcher(commentTimeData);
                        matcher.find();
                        String commentDate = matcher.group(1);
                        String commentTime = matcher.group(2);
                        System.out.print(commentTime.trim() + "\n" + commentDate.trim()+ "\n");
                        comment.setCommentTime(commentTime.trim());
                        comment.setCommentDate(commentDate.trim());


                        // Extract Commentors
                        Pattern commentorNamesPattern = Pattern.compile("<a href=\"(.+?)\"><span .+?>(.+?)<\\/span><\\/a>");
                        matcher = commentorNamesPattern.matcher(commentorNamesData);
                        matcher.find();
                        String commentorPage = matcher.group(1);
                        String commentorName = matcher.group(2);
                        commentorPage = commentorPage.replace("amp;","");
                        System.out.print(commentorName.trim() + "\n" + commentorPage.trim() + "\n");
                        commentor.setUserName(commentorName.trim()); // create a User Object
                        commentor.setUserPageLink(commentorPage.trim());
                        comment.setCommentor(commentor); // Save the user object as the commentor


                        // Extracting Comment Body.

                        String commentBody = html2textV2(commentBodyData);
                        System.out.print(commentBody.trim() + "\n\n");
                        comment.setComment(commentBody.trim());

                        // if commentNumber is 1, then we are dealing with the first coment and hence the creator. Save
                        // it under the thread information
                        if(commentNumber==1){
                            thread.setThreadCreator(commentor);
                            thread.setDateCreated(commentDate);
                            thread.setThreadCreatorLink(commentorPage.replace("amp;",""));
                            comment.setCommentType("Thread");
                        } else {
                            comment.setCommentType("Response");

                        }

                        // Add user to list of thread commentors.
                        threadCommentors.add(commentor);

                        // Add the comment to the list of comments in this thread.
                        commentsList.add(comment);

                        // Add user to list of users.
                        if (!doesUserExist(userList, commentor)){
//                            System.out.println("Adding new User");
                            userList.add(commentor);
                        }

                    //}

                }catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }

                // Save List of comments to this particular thread
                thread.setCommentList(commentsList);

                // Save list of commentors to this particluar thread
                thread.setCommentors(threadCommentors);

                writer.write(thread.printHackForumThreadsToFile());

            }

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }
        //******************************* END OF THREAD DATA EXTRACTION ************************************************//

        try {

            // Writing out data to file

            String fileTextHeading = "ThreadName" + "," + "ThreadLink" + "," + "Date_Created" + "," + "Creator"
                    + "," + "Creator_Page" + "," + "Number_of_Comments" + "," + "Date" + "," + "Time" + "," +"Comments" + "\n";

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("HackForumsThreadsFinal-" + dateString + ".csv"), "utf-8"));

            threadfileText = threadfileText + fileTextHeading;

            for(Thread thread: threadList){
                threadfileText = threadfileText + thread.printHackForumCommentors();
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


        ChromeDriver hackForumDriver = new ChromeDriver(chrome);
        hackForumDriver.navigate().to("https://hackforums.net/member.php?action=login");
        hackForumDriver.switchTo();

        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (Exception e){

        }

        WebElement username = hackForumDriver.findElementByName("username");
        WebElement password = hackForumDriver.findElementByName("password");

        username.sendKeys ("shujohns@gmail.com");
        password.sendKeys("MotDePasse2017!");
        hackForumDriver.findElementByName("submit").click();

        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (Exception e){

        }

        try{

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("HackForumUsersComplete-" + dateString + ".csv"), "utf-8"));


            String fileHeading = "User Name" + "," + "Date Joined" + "," + "Birth Date" + "," + "Age" + "," + "Posts Count" + "," + "Posts Frequency"
                    + "," + "Posts Percentage"+ "," + "Time online"  + "," + "Reputation" + "," + "Prestige"
                    + "," + "Awards" + "," + "Stars" + "," + "Profile Page" + "\n";


            // File String to save to file object
            String userFileText = "";
            String userName = "";
            String dateJoined = "";
            String birthDate = "";
            String numberOfPosts = "";
            String postFrequency = "";
            String postPercentage = "";
            String timeOnline = "";
            String reputation = "";
            String prestige = "";
            String reportedPosts = "";
            String awards = "";
            String userPageLink = "";
            Integer stars = 0;
            Integer age = 0;



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
                    hackForumDriver.navigate().to(user.userPageLink);


                    // Gathering data on comments for this particular thread
                    List<WebElement> userData = hackForumDriver.findElements(By.xpath("//div[contains(@class, 'quick_keys')]"));

                    String userVariableInfo = (String) ((JavascriptExecutor) hackForumDriver).executeScript("return arguments[0].innerHTML;", userData.get(0));
//                        System.out.println(userVariableInfo);

                    // Count number of stars
                    stars = userVariableInfo.length() - userVariableInfo.replace("*", "").length();
                    user.setStars(stars.toString().replace(",",""));

                    userVariableInfo = html2text(userVariableInfo);
//                        System.out.println(userVariableInfo);


                    // Extracting user Registration Date.
                    Pattern userRegDatePattern = Pattern.compile("Registration Date:\\s(.+?)\\s");
                    Matcher matcher = userRegDatePattern.matcher(userVariableInfo);
                    matcher.find();
                    dateJoined = matcher.group(1);
                    System.out.println("dateJoined: " + dateJoined);
                    user.setDateJoined(dateJoined);


                    try {
                        // Extracting user DOB.
                        Pattern userBirthDatePattern = Pattern.compile("Date of Birth:\\s(.+?)\\s\\((.+?)\\)\\s");
                        matcher = userBirthDatePattern.matcher(userVariableInfo);
                        matcher.find();
                        if (!(matcher.group(1).isEmpty())) {
                            birthDate = matcher.group(1);
                            String dob[] = matcher.group(2).split(" ", 2);
                            age = Integer.valueOf(dob[0].replace(",",""));
                            System.out.println("BirthDate: " + birthDate);
                            System.out.println("age: " + age);
                            user.setBirthDate(birthDate.replace(",",""));
                            user.setAge(age);
                        }
                    }
                    catch (Exception e) {

                    }



                    try{

                        // Extracting Total Posts.
                        Pattern userNumberPostsPattern = Pattern.compile("Total Posts:\\s(.+?)\\s\\((.+?)\\)");
                        matcher = userNumberPostsPattern.matcher(userVariableInfo);
                        matcher.find();
                        if (!(matcher.group(1).isEmpty())) {
                            numberOfPosts = matcher.group(1);
                            String posts[] = matcher.group(2).split(" ", 10);
                            postFrequency = posts[0].replace("(","").trim();
                            postPercentage = posts[5].trim();

                            System.out.println("numberOfPosts: " + numberOfPosts);
                            System.out.println("postFrequency: " + postFrequency);
                            System.out.println("postPercentage: " + postPercentage);

                            user.setNumberOfPosts(Integer.valueOf(numberOfPosts.replace(",","")));
                            user.setPostFrequency(Double.valueOf(postFrequency.replace(",","")));
                            user.setPostPercentage(Double.valueOf(postPercentage.replace(",","")));

                        }
                    }
                    catch (Exception e) {

                    }


                    try {
                        // Extracting user time spent online.
                        Pattern userTimeOnlinePattern = Pattern.compile("Time Spent Online: (.+? Seconds)");
                        matcher = userTimeOnlinePattern.matcher(userVariableInfo);
                        matcher.find();
                        if (!(matcher.group(1).isEmpty())) {
                            timeOnline = matcher.group(1);
                            String timeInSecs = convertTimeToSecs(timeOnline);
                            System.out.println("TimeOnline: " + timeOnline.trim() + " Time in Secs: " + timeInSecs);
                            user.setTimeOnline(timeOnline.replace(",",""));
                            user.setTimeInSecs(timeInSecs.replace(",",""));
                        }
                    }
                    catch (Exception e) {

                    }

                    try {
                        // Extracting user reputation.
                        Pattern userReputationPattern = Pattern.compile("Reputation: (.\\d*)");
                        matcher = userReputationPattern.matcher(userVariableInfo);
                        matcher.find();
                        if (!(matcher.group(1).isEmpty())) {
                            reputation = matcher.group(1);
                            System.out.println("Reputation: " + reputation.trim());
                            user.setReputation(reputation.trim().replace(",",""));
                        }
                    }
                    catch (Exception e) {

                    }

                    try {
                        // Extracting user prestige.
                        user.setPrestige("null");
                        Pattern userPrestigePattern = Pattern.compile("Prestige: (\\d*)");
                        matcher = userPrestigePattern.matcher(userVariableInfo);
                        matcher.find();
                        if (!(matcher.group(1).isEmpty())) {
                            prestige = matcher.group(1);
                            System.out.println("Prestige: " + prestige.trim());
                            user.setPrestige(prestige.trim().replace(",",""));
                        }
                    }
                    catch (Exception e) {

                    }

                    try {
                        // Extracting user Awards.
                        Pattern userAwardsPattern = Pattern.compile("Awards: (\\d*)");
                        matcher = userAwardsPattern.matcher(userVariableInfo);
                        matcher.find();
                        if (!(matcher.group(1).isEmpty())) {
                            awards = matcher.group(1);
                            System.out.println("Awards: " + awards.trim());
                            user.setAwards(awards.trim().replace(",",""));
                        }
                    }
                    catch (Exception e) {

                    }

                }catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    e.getStackTrace();
                    e.printStackTrace();
                }

                System.out.println(fileHeading + user.printToFileHackForums());

            }



            try{

                userFileText = userFileText + fileHeading;

                // Writing out data to file

                for(User user: completeUsersList){
                    userFileText = userFileText + user.printToFileHackForums();
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

        hackForumDriver.close();
        hackForumDriver.quit();

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
        return html.replaceAll("\\<.*?\\>", "");
//        return Jsoup.parse(html).text();
    }


}
