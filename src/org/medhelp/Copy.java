package org.medhelp;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 5/16/15
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Copy {


    public static void main(String[] args) throws Exception {

        WebDriver chromeDriver = new FirefoxDriver();
        //WebDriver chromeDriver = new FirefoxDriver();

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

        System.out.println("Gathering data ...");

        //************************************************* START SCRAPPING FORUM FOR THE THREADS *************************************************//

        do{
            // pageNumber is automatically incremented at the end of this loop so next page can be crawled. This happens until pages have
            // no more data i.e. threads.
            chromeDriver.navigate().to("http://www.medhelp.org/forums/High-Blood-Pressure---Hypertension/show/1222?page=" + pageNumber);


            // Check if this page has any data
            newSubjectElement = chromeDriver.findElements(By.className("new_subject_element"));

            //System.out.println(newSubjectElement.isEmpty());

            // Start looking through the threads
            List<WebElement> threadsList =  chromeDriver.findElements(By.className("subject_summary"));


            for ( int i=0; i < threadsList.size(); i++){
//          for ( int i=0; i < 3; i++){


                // Extract Thread Names

                try{

                    String Url = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", threadsList.get(i));
//                    System.out.println(Url);

                    // Instantiate a new Thread
                    Thread thread = new Thread();
                    User user = new User();

                    thread.setThreadPageNumber(pageNumber);
                    thread.setThreadNumber(threadNumber++);
                    System.out.println("\n\nPage number: "+ pageNumber + "\nThread number: "+ threadNumber);


                    // Extract Thread Date
                    Pattern threadDatePattern = Pattern.compile("<span class=\".+?\">(.+?)<\\/span>");
                    Matcher matcher = threadDatePattern.matcher(Url);
                    matcher.find();
                    String threadDate = matcher.group(1);
                    System.out.println(threadDate);
                    thread.setDateCreated(threadDate);

                    // Extract Thread Names
                    Pattern threadNamePattern = Pattern.compile("<a href=\".+?\">(.+?)<\\/a>");
                    matcher = threadNamePattern.matcher(Url);
                    matcher.find();
                    String threadName = matcher.group(1);
                    System.out.println(threadName);
                    thread.setThreadName(threadName);

                    // Extract Thread Links
                    Pattern threadLinkPattern = Pattern.compile("<a href=\"(.+?)\">");
                    matcher = threadLinkPattern.matcher(Url);
                    matcher.find();
                    String threadLink = matcher.group(1);
                    threadLink = "http://www.medhelp.org" + threadLink;
                    System.out.println(threadLink);
                    thread.setThreadLink(threadLink);

                    // Extract Thread Creator
                    Pattern threadCreatorPattern = Pattern.compile("<span><a href=\".+?\">(.+?)<\\/a><\\/span>");
                    matcher = threadCreatorPattern.matcher(Url);
                    matcher.find();
                    User threadCreator = new User(matcher.group(1));
                    System.out.println(threadCreator);
                    thread.setThreadCreator(threadCreator);


                    // Extract Link to Thread Creator's Page
                    Pattern threadCreatorLinkPattern = Pattern.compile("<span><a href=\"(.+?)\"");
                    matcher = threadCreatorLinkPattern.matcher(Url);
                    matcher.find();
                    String threadCreatorLink = matcher.group(1);
                    threadCreatorLink = "http://www.medhelp.org" + threadCreatorLink;
                    System.out.println(threadCreatorLink);
                    thread.setThreadCreatorLink(threadCreatorLink);

                    user.setUserPageLink(threadCreatorLink);

                    // Extracting thread creator (user) unique Id.
                    Pattern threadCreatorUniqueIdPattern = Pattern.compile("<span><a href=\".+?\" id=\"user_(.+?)_.+?\">");
                    matcher = threadCreatorUniqueIdPattern.matcher(Url);
                    matcher.find();
                    String userUniqueId = matcher.group(1);
                    System.out.println(userUniqueId);
                    user.setUniqueId(Integer.valueOf(userUniqueId));
                    user.setUserName(threadCreator.getUserName());

                    if (!doesUserExist(userList, user)){
                        System.out.println("Adding new User");
                        userList.add(user);
                    }


                    threadList.add(thread);

                }catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }


            }

            // Increment Page number, there are usually about 20 threads on one page. For this Forum there are about 29 pages.
            // The first run in file "Threads1.csv" has data for all of the 29 pages. About 576 Threads
            pageNumber++;

//        } while(pageNumber < 2 );
        }while(!(newSubjectElement.isEmpty()) ); // Set the number of Pages you want to crawl here. This Forum has about 29 pages crawlable.
//        } while(pageNumber < 3 | !(newSubjectElement.isEmpty())  );

        //******************************* END OF PAGE SCRAPPING FOR THREADS ************************************************//

        //******************************* START ACCESSING THREADS THEMSELVES TO GET DETAILED INFO **************************//


        userList = scrapeThreads(threadList, userList);

//        System.out.println("*************USERSCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

        userList = scrapeUsers(userList);

//        System.out.println("*************USERSCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

        userList = scrapeNotes(userList);

//        System.out.println("*************NOTESCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

        userList = scrapePosts(userList);
//        System.out.println("*************POSTCRAPE :NEW USER LIST **************");
//        for (User user: userList){
//            System.out.println(user.userName + ",");
//        }

        try{
            String userFileText = "";

            String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

            System.out.println("Writing final user data to the file ...\n");

            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("FinalUsers" + dateString + ".csv"), "utf-8"));
            for(User user: userList){
                userFileText = userFileText + user.userName + "\n\n";

                for(User friend : user.getFriendsList()){
                    fileText = fileText + friend.userName + ", ";
                }
            }

            System.out.println(userFileText);

            writer.write(userFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        //******************************* END OF THREAD DATA EXTRACTION ************************************************//

    }


















    public static Boolean doesUserExist(List<User> userList, User newUser){
        Boolean value = Boolean.FALSE;

        for(User user: userList){
            if((newUser.userName).equals(user.userName)){
                value = Boolean.TRUE;
                System.out.println("Condition: " + value);
            }
        }

        return value;
    }





















    public static List<User> scrapeThreads(List<Thread> threadList, List<User> userList){

        System.out.println("\n\n********************************** THREADS: GATHERING DATA ***************************************");

        for(Thread thread: threadList){
            System.out.println(thread.threadName);
        }


        WebDriver chromeDriver = new FirefoxDriver();

        String threadfileText = "";

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(Thread thread: threadList){

            String threadCommentorsData = "";
            String numberOfComments = "";
            List<User> threadCommentors;

            System.out.println("\n\nThread " + thread.getThreadNumber() + " of " + threadList.size());

            try{

                System.out.println(thread.threadLink);

                // Go to the Thread Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to(thread.threadLink);

                // Gathering data on comments for this particular thread
                List<WebElement> subjectCommentsNumber = chromeDriver.findElements(By.className("subject_comments_number"));

                numberOfComments = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", subjectCommentsNumber.get(0));


                // Extracting number of comments in this thread.
                Pattern numberOfCommentsPattern = Pattern.compile("<a href=\"#comments_header\">(.+?)");
                Matcher matcher = numberOfCommentsPattern.matcher(numberOfComments);
                matcher.find();
                String commentsNumber = matcher.group(1);
                System.out.println(commentsNumber);
                thread.setCommentsNumber(Integer.valueOf(commentsNumber));

                // Instantiate list to save all the users who commented in this thread.
                threadCommentors = new ArrayList<User>();

                // Loop to find all the thread commentors
                if(Integer.valueOf(commentsNumber) > 0){

                    // If this page has any data
                    List<WebElement> threadData = chromeDriver.findElements(By.className("question_by"));


                    for ( int i=0; i < threadData.size(); i++){

                        threadCommentorsData = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", threadData.get(i));

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
                        Pattern threadCommentorLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\"user.+?\">");
                        matcher = threadCommentorLinkPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String threadCommentorLink = matcher.group(1);
                        threadCommentorLink = "http://www.medhelp.org" + threadCommentorLink;
                        System.out.println(threadCommentorLink);
                        commentor.setUserPageLink(threadCommentorLink);

                        // Extracting user unique Id.
                        Pattern threadCommentorUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"user_(.+?)_.+?\">");
                        matcher = threadCommentorUniqueIdPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String userUniqueId = matcher.group(1);
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
            String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());


            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Threads" + dateString + ".csv"), "utf-8"));
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

        return userList;
    }















    public static List<User> scrapeUsers(List<User> userList){

        System.out.println("\n\n********************************** IN USER/FRIENDS FUNCTION ***************************************");

        for(User user: userList){
            System.out.println(user.userName);
        }


        WebDriver chromeDriver = new FirefoxDriver();

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
        List<User> newUsers = new ArrayList<User>();


        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<User> friendList = new ArrayList<User>();



            System.out.println("\n\n" + user.getUserName()+ " with link " + user.userPageLink);

            try{

                // Go to the Thread Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to(user.userPageLink);

                // Gathering data on comments for this particular thread
                userNameData = chromeDriver.findElements(By.className("page_title"));
                userPageIdData = chromeDriver.findElements(By.className("pp_r_txt_sel"));
                userInfoData = chromeDriver.findElements(By.xpath("//div[contains(@class, 'bottom float_fix')]//div[contains(@class,'section')]"));

                userNameInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userNameData.get(0));
                userIds = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userPageIdData.get(0));
                userInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userInfoData.get(0));

//                System.out.println("User Id: " + userIds);
//                System.out.println("User Info: " + userInfo);
//                System.out.println("userNameInfo : " + userNameInfo);


//                if(user == null){
//                    // Extracting user Name.
//                    System.out.println("****** Adding New User Name*******");
//
//                    Pattern userNamePattern = Pattern.compile("(.+?)'s Profile");
//                    Matcher matcher = userNamePattern.matcher(userNameInfo);
//                    matcher.find();
//                    String userName = matcher.group(1);
//                    System.out.println(userName);
//                    user.setPageId(Integer.valueOf(userName));
//                }
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
                System.out.println(userGender);
                user.setGender(userGender);

                // Extracting user data joined.
                Pattern userDateJoinedPattern = Pattern.compile("since( .+? .+.?)");
                matcher = userDateJoinedPattern.matcher(userInfo);
                matcher.find();
                String userDateJoined = matcher.group(1);
                System.out.println(userDateJoined);
                user.setDateJoined(userDateJoined);


                //******************************* GATHER USER's FRIEND LIST ************************************************//
                System.out.println("\n\nStaring to parse " + user.getUserName() + "'s friends list ...");

                chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );

                System.out.println("\n\nOn " + user.getUserName() +"'s friends list page ");

                // Check if user has any friends ?
                List<WebElement> anyFriends = chromeDriver.findElements(By.xpath("//div[starts-with(@class, 'friend_box')]"));

                System.out.println("anyFriends: " + anyFriends.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg')]"));
                System.out.println(paginationNumber.size() + " pages of friends");


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );

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


                        // Extract Commentors page Links
                        Pattern friendPageLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\"user.+?\">");
                        matcher = friendPageLinkPattern.matcher(friendsData);
                        matcher.find();
                        String friendPageLink = matcher.group(1);
                        friendPageLink = "http://www.medhelp.org" + friendPageLink;
                        System.out.println(friendPageLink);
                        friend.setUserPageLink(friendPageLink);

                        // Extracting friend unique Id.
                        Pattern friendUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"user_(.+?)_.+?\">");
                        matcher = friendUniqueIdPattern.matcher(friendsData);
                        matcher.find();
                        String friendUniqueId = matcher.group(1);
                        System.out.println(friendUniqueId);
                        friend.setUniqueId(Integer.valueOf(friendUniqueId));

                        // Add user to Set of Friends.
                        friendList.add(friend);

                        if (!doesUserExist(userList, friend)){
                            System.out.println("Adding new User");
                            newUsers.add(friend); // *************************** REPLACE WITH friend
                        }
                    }

                    pageNumber++;
                    System.out.println("\n\nUser Page number is: " + pageNumber);

                }while (pageNumber < (paginationNumber.size()+1));

                user.setFriendsList(friendList);

                System.out.println("User :" + user.getUserName() + " has " + user.friendsList.size() + " Friends");


            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                e.getStackTrace();
                e.printStackTrace();
                continue;
            }


        }

        // Adding new found users to existing list of users
        userList.addAll(newUsers);

        try{
            String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

            System.out.println("Writing user data to the file ...\n");

            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Users-" + dateString + ".csv"), "utf-8"));
            for(User user: userList){
                userFileText = userFileText + user.printToFile();
            }

            System.out.println(userFileText);

            writer.write(userFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        chromeDriver.close();

        return userList;
    }













    public static List<User> scrapeNotes(List<User> userList){

        System.out.println("\n\n********************************** IN NOTES FUNCTION ***************************************");

        for(User user: userList){
            System.out.println(user.userName);
        }

        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String userFileText = "";

        String noteEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> noteEntryData;
        List<WebElement> paginationNumber;

//        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file
        File file = new File("/Users/johnshu/Java programming/WebScraper/UsersNotes.csv");

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

            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

            try{

                // Go to the Notes Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

                noteEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'note_') and contains(@class, 'note_entry float_fix')]")); // noteEntryData.size() # of notes
                System.out.println("Number of notes on 1st page: " + noteEntryData.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg')]")); // # of pages
                System.out.println(paginationNumber.size() + " Notes pages");



                //******************************* OBTAIN NOTES DATA ************************************************//
                System.out.println("\n\nOn " + user.getUserName() +"'s notes list page ");


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );

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

                }while (pageNumber < (paginationNumber.size()+1));

//                String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

                System.out.println("Writing notes data to the file");

                // Writing out data to file
//              Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Notes-" + dateString + ".csv"), "utf-8"));

                userFileText = userFileText + user.getUserName() + "\n\n";

                for(Note note: user.getFriendsNotes()){
                    userFileText = userFileText + note.printToFile();
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                System.out.println(userFileText);

                bufferedWriter.write(userFileText);
                bufferedWriter.close();

            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            user.setFriendsNotes(notesList);
            System.out.println("User :" + user.getUserName() + " has " + user.friendsNotes.size() );

        }


        return userList;
    }















    public static List<User> scrapePosts(List<User> userList){

        System.out.println("\n\n********************************** IN POST FUNCTION ***************************************");

        for(User user: userList){
            System.out.println(user.userName);
        }

        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String userFileText = "";

        String postEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> postEntryData;
        List<WebElement> paginationNumber;

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<Post> postsList = new ArrayList<Post>();

            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

            try{

                // Go to the Posts Page itself and collect data on the posts left on the page.
                chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

                // Gathering data on comments for this particular thread
                postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                System.out.println("Number of posts on 1st page: " + postEntryData.size());
//                System.out.println("List of post: " + postEntryInfo);


                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg')]")); // # of pages
                System.out.println(paginationNumber.size() + " pages of posts");



                //******************************* OBTAIN NOTES DATA ************************************************//

                System.out.println("\n\nOn " + user.getUserName() +"'s posts list page ");


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );

                    // Loop to find all the friends

                    for ( int i=0; i < postEntryData.size(); i++){

                        postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                        postEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", postEntryData.get(i));
//                        System.out.println(postEntryInfo);

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
                    System.out.println("\n\nPost Page number is: " + pageNumber);

                }while (pageNumber < (paginationNumber.size()+1));


            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            user.setUserPosts(postsList);
            System.out.println("User :" + user.getUserName() + " has " + user.userPosts.size() + "\n\n");

        }

        try{
            String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

            System.out.println("Writing post data to the file");

            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Posts" + dateString + ".csv"), "utf-8"));
            for(User user: userList){

                userFileText = userFileText + user.getUserName() + "\n\n";

                for(Post post: user.getUserPosts()){
                    userFileText = userFileText + post.printToFile();
                }
            }

            writer.write(userFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }


        return userList;
    }

}





