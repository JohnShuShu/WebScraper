package org.medhelp;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 5/16/15
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class OriginalScrapper {


    public static void main(String[] args) throws IOException {

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
        List<Thread> Threads = new ArrayList<Thread>();

        // List of thread commentors
        List<User> threadCommentors;

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

            //System.out.println(threadsList.size());

            for ( int i=0; i < threadsList.size(); i++){

                //System.out.println("*****" + (i + threadNumber)+ "*****");

                // Extract Thread Names

                try{

                    String Url = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", threadsList.get(i));
                    //System.out.println(Url);

                    // Instantiate a new Thread
                    Thread thread = new Thread();

                    thread.setThreadPageNumber(pageNumber);
                    thread.setThreadNumber(threadNumber++);
                    System.out.println("Page number: "+ pageNumber + "\nThread number: "+ threadNumber);


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
                    System.out.println(threadCreatorLink + "\n");
                    thread.setThreadCreatorLink(threadCreatorLink);



                    //fileText = fileText+appName+", "+threadDate+", "+appCreator+", "+threadCreatorLink+", "+appLink+", "+", "+threadNumber+", "+pageNumber+ "\n\n";

                    Threads.add(thread);

                }catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                    continue;
                }


            }

            // Increment Page number, there are usually about 20 threads on one page. For this Forum there are about 29 pages.
            // The first run in file "Threads1.csv" has data for all of the 29 pages. About 576 Threads
            pageNumber++;

        } while(!(newSubjectElement.isEmpty()) ); // Set the number of Pages you want to crawl here. This Forum has about 29 pages crawlable.
        //} while(pageNumber < 3 | !(newSubjectElement.isEmpty())  );

        //******************************* END OF PAGE SCRAPPING FOR THREADS ************************************************//

        //******************************* START ACCESSING THREADS THEMSELVES TO GET DETAILED INFO **************************//

        System.out.println("\n\n\n\nGathering data for threads ...");


        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(Thread thread: Threads){

            String threadCommentorsData = "";
            String numberOfComments = "";

            System.out.println("\n\nThread " + thread.getThreadNumber() + " of " + Threads.size());

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

                        // Add user to list of commentors.
                        threadCommentors.add(commentor);
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

        // Writing out data to file
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Threads.csv"), "utf-8"));
        for(Thread thread: Threads){
            fileText = fileText + thread.printToFile();
        }

        writer.write(fileText);
        writer.close();

        chromeDriver.close();
    }

}





