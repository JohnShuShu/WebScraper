package org.medhelp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 7/28/15
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataCleaner {

    public static void main(String[] args) {




        //        System.out.println("Please enter a command option: ");
        //        System.out.println("1: Threads");
        //        System.out.println("2: Users Notes");
        //        System.out.println("3: Users Posts");
        //        System.out.println("4: User Threads and Friends");
        //        System.out.println("5: User Complete");
        //        System.out.println("6: Display");
        //        System.out.println("7: Quit");
        //        Scanner scan = new Scanner(System.in);
        //
        //        Integer choice = 0;
        //
        //        do{
        //             choice = scan.nextInt();
        //
        //
        //            switch (choice){
        //                case 1:
//        cleanThreads("/Users/johnshu/Desktop/WebScraper/AnxDepRel/Relationships.csv");
          cleanThreads("/Users/johnshu/Desktop/WebScraper/Final Data Collection/Nutrition/Nutrition2016-04-06 11-48-10/Threads-NutritionToClean.csv");
        //                    break;
        //
        //                case 2:
//        fixDates("/Users/johnshu/Java programming/WebScraper/Threads-2015-06-11_ 04-10-25_LATEST.csv");
        //                    cleanUserNotes(userfile);
        //                    break;
        //
        //                case 3:
//        cleanThreadsAddDate("/Users/johnshu/Java programming/WebScraper/ThreadAuthorsAndResponsesAndDate.csv");
        //                    cleanUserPosts(userfile);
        //                    break;
        //
        //                case 4:
//        cleanUserThreadsFriends("/Users/johnshu/Java programming/WebScraper/ThreadAuthorsAndResponses.csv");
        //                    break;
        //
        //                case 5:
        //                    cleanUserComplete(userfile);
        //                    break;
        //
        //                case 6:
        //                    System.out.println("Please enter a command option: ");
        //                    System.out.println("1: Threads");
        //                    System.out.println("2: Users Notes");
        //                    System.out.println("3: Users Posts");
        //                    System.out.println("4: User Threads and Friends");
        //                    System.out.println("5: User Complete");
        //                    System.out.println("6: Display");
        //                    System.out.println("7: Quit");
        //                    break;
        //
        //                case 7:
//        eliminateDuplicates("/Users/johnshu/Java programming/WebScraper/CLEANED/RAW_ThreadAuthorsAndResponses.csv");
//                         case 8:
//        oneToOneMappingOfAuthorsAndCommentors("/Users/johnshu/Desktop/WebScraper/HypotheticalCalculation.csv");
        //                case 8:
        //                    System.exit(1);
        //            }
        //
        //        } while(choice != 9);
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

    // Fix Dates
    public static void fixDates(String file){

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file   xlsx or csv
        File newfile = new File("/Users/johnshu/Java programming/WebScraper/CLEANED/DATEFIXED_ThreadAuthorsAndResponses" + dateString + ".csv");

        if (!newfile.exists()) {
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }




        System.out.println("\n\nPrint out of File " + file);

        List<String> threadData = new ArrayList<String>();

        Integer numberOfResponses = 0;

        try {
            // Get file and write user friends to the file
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newfile.getAbsoluteFile(), true));
            String threadDetails = "";

            Scanner input = new Scanner (new File(file));

            while (input.hasNextLine()) {


                String line = input.nextLine();
                System.out.println("\n\nPrinting Line :" + line);

                List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));


                String year = attributes.get(4).trim();
                String monthString = attributes.get(5).trim();
                Integer month = Month.valueOf(monthString).getMonthValue(monthString);
                String day = attributes.get(6).trim();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                Date date = dateFormat.parse(month+"/"+day+"/"+year);

                System.out.println("New Date: " + dateFormat.format(date));


                // Re-create Database
                attributes.add(7, dateFormat.format(date));

                String threadInfo = attributes.toString();
                threadInfo = threadInfo.substring(1, threadInfo.length()-1);
                System.out.println(threadInfo);

                bufferedWriter.write(threadInfo + "\n");

            }

            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }




    // Accepts a list of Users and extracts the string usernames.
    // It then creates a hash set which eliminates the duplicates.
    // It then re-creates the list of User objects from the string
    // then returns the unique list of Users as an array list
    public static List deduplicate(List<User> threadCommentors){

        System.out.println("In deduplicate");
        List<String> usersStringList = new ArrayList<String>();
        List<User> returnList = new ArrayList<User>();

        for(User user: threadCommentors){
            usersStringList.add(user.getUserName());
        }


        Set<String> fixedList = new LinkedHashSet<String>(usersStringList);

        String list = "";
        for(String userName: fixedList) {
            list = list + ", " + userName;
            returnList.add(new User(userName));
        }
        System.out.println("In Deduplicate Function\n" + list);

        return returnList;
    }


    // Takes the list of threads created by all different users. Some times
    // one user can create multiple threads. All of the threads from a particular user
    // are combined into one and then the deduplicate function is called to remove duplicate comments
    // The end result is a unique list of user created threads with all the commentors from all the threads created
    // by that unique user.
    public static List uniqueifyThreads(List<Thread> OrigThreads){

        List<Thread> uniqueData;

        System.out.println("In Uniqueify");

        for(Integer index=0; index <= OrigThreads.size(); index++){

            Integer currentIndex = index;
            Thread currentThread = OrigThreads.get(index);
            Thread nextThread = new Thread();

//            if(index == 7733){
//                System.out.println("Reached Limit");
//            }


            if((index + 1) < OrigThreads.size()){
                nextThread = OrigThreads.get(index+1);
            } else {
                break;
            }


            if(currentThread.getThreadCreator().getUserName().equals(nextThread.getThreadCreator().getUserName())){
                currentThread.getCommentors().addAll(nextThread.getCommentors());

                List<User> tempCommentators = currentThread.getCommentors() ;
                tempCommentators = (deduplicate(tempCommentators));
                currentThread.setCommentors(tempCommentators);
                currentThread.setCommentsNumber();

                OrigThreads.remove(index);
                OrigThreads.remove(index+1);
                index--;
//                OrigThreads.add(currentIndex,currentThread);
            } else{

                List<User> tempCommentators = currentThread.getCommentors() ;
                tempCommentators = (deduplicate(tempCommentators));
                currentThread.setCommentors(tempCommentators);
                currentThread.setCommentsNumber();

            }



        }

        System.out.println("About to Deduplicate");
        uniqueData = OrigThreads;

        return uniqueData;
    }

    // Eliminates duplicates from Thread Data. Streamlines the data to make sure there exist a single author
    // no matter the number of threads they have started. It also eliminates duplicate answers or responses in
    // these different threads.
    public static void eliminateDuplicates(String file){

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file
        File newfile = new File("/Users/johnshu/Java programming/WebScraper/CLEANED/RESTRUCT_ThreadAuthorsAndResponses" + dateString + ".csv");

        if (!newfile.exists()) {
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        System.out.println("\n\nPrint out of File " + file);

        List<Thread> uniqueData = new ArrayList<Thread>();
        List<Thread> origThreads = new ArrayList<Thread>();

        Integer numberOfResponses = 0;

        try {
            Scanner input = new Scanner (new File(file));

            while (input.hasNextLine()) {


                String line = input.nextLine();
                System.out.println("\n\nPrinting Line :" + line);

                List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));

                for(String value: attributes){

                    System.out.println(value.toString());

                }

                System.out.println("New Author: " + attributes.get(0).trim());
                Thread thread = new Thread();
                thread.setThreadCreator(new User(attributes.get(0).trim()));

                // Check if 2nd value is a number
                if(isInt(attributes.get(1).trim())){

                    numberOfResponses = Integer.parseInt(attributes.get(1).trim());
                    System.out.println("Number of Responses: " + numberOfResponses);
                    thread.setCommentsNumber(numberOfResponses);
                }

                // Create a thread commentors list
                List<User> threadCommentors = new ArrayList<User>();

                for(Integer index = 2; index < numberOfResponses + 2; index ++){

                    threadCommentors.add(new User(attributes.get(index).trim()));

                }

                // set the list of commentors
                thread.setCommentors(threadCommentors);

                origThreads.add(thread);

                System.out.println("Done adding new thread");


            }

            System.out.println("About to Uniqueify");
            uniqueData = uniqueifyThreads(origThreads);

            // Get file and write user friends to the file
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newfile.getAbsoluteFile(), true));
            String threadData = "";


            for(Thread thread: uniqueData){

                threadData = threadData + thread.getThreadCreator().getUserName() + thread.printCommentors() + "\n";
            }

            bufferedWriter.write(threadData);
            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // One to One Mapping of Authors and Commentors accepts a String file with authors, number of comments
    // and list of commentors for each thread. For a single thread all of this information is presented in a
    // staight line. I.e Author, 2, Rockrose, specialmom.
    // This information is read in and Author is matched to Rockrose and to specialmom in a one to one fashion
    // this is then used for NodeXL analyis.
    //
    public static void oneToOneMappingOfAuthorsAndCommentors(String file){

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file
        File newfile = new File("/Users/johnshu/Java programming/WebScraper/CLEANED/FIXED_HypotheticalCalculation" + dateString + ".csv");

        if (!newfile.exists()) {
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        System.out.println("\n\nPrint out of File " + file);

        List<Thread> uniqueData = new ArrayList<Thread>();
        List<Thread> origThreads = new ArrayList<Thread>();

        Integer numberOfResponses = 0;

        try {
            Scanner input = new Scanner (new File(file));

            while (input.hasNextLine()) {


                String line = input.nextLine();
                System.out.println("\n\nPrinting Line :" + line);

                List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));

                for(String value: attributes){

                    System.out.println(value.toString());

                }

                System.out.println("New Author: " + attributes.get(0).trim());
                Thread thread = new Thread();
                thread.setThreadCreator(new User(attributes.get(0).trim()));

                // Check if 2nd value is a number
                if(isInt(attributes.get(1).trim())){

                    numberOfResponses = Integer.parseInt(attributes.get(1).trim());
                    System.out.println("Number of Responses: " + numberOfResponses);
                    thread.setCommentsNumber(numberOfResponses);
                }

                // Create a thread commentors list
                List<User> threadCommentors = new ArrayList<User>();

                for(Integer index = 2; index < numberOfResponses + 2; index ++){

                    threadCommentors.add(new User(attributes.get(index).trim()));

                }

                // set the list of commentors
                thread.setCommentors(threadCommentors);

                origThreads.add(thread);

                System.out.println("Done adding new thread");


            }

            //System.out.println("About to Uniqueify");
            //uniqueData = uniqueifyThreads(origThreads);

            // Get file and write user friends to the file
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newfile.getAbsoluteFile(), true));
            String threadData = "";


            for(Thread thread: origThreads){

                threadData = threadData + thread.getThreadCreator().getUserName() + thread.printCommentors() + "\n";
            }

            bufferedWriter.write(threadData);
            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    // One to One Mapping of Authors and Commentors accepts a String file with authors, number of comments
    // and list of commentors for each thread. For a single thread all of this information is presented in a
    // staight line. I.e Author, 2, Rockrose, specialmom.
    // This information is read in and Author is matched to Rockrose and to specialmom in a one to one fashion
    // this is then used for NodeXL analyis.
    //
    public static void cleanThreads(String file){

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file
//        File newfile = new File("/Users/johnshu/Desktop/WebScraper/AnxDepRel/FIXED_Relationships" + dateString + ".csv");
        File newfile = new File("/Users/johnshu/Desktop/WebScraper/Final Data Collection/Nutrition/Nutrition2016-04-06 11-48-10/FIXED_Nutrition" + dateString + ".csv");

        if (!newfile.exists()) {
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        System.out.println("\n\nPrint out of File " + file);

        List<User> userList = new ArrayList<User>();
        Integer friendNumber = 0;

        try {
            Scanner input = new Scanner (new File(file));
            while (input.hasNextLine()) {


                String line = input.nextLine();
                System.out.println("\n\nPrinting Line :" + line);

                List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));

                for(String value: attributes){

                    System.out.println("Value : " + value);

                }

                System.out.println("New User: " + attributes.get(0).trim());
                User user = new User();
                user.setUserName(attributes.get(0).trim());

                // Check if 2nd value is a number
                if(isInt(attributes.get(1).trim())){

                    friendNumber = Integer.parseInt(attributes.get(1).trim());
                    System.out.println("Friend Number set to: " + friendNumber);
                }

                String userAndFriendText = "";


                // Create a user friend list
                List<User> userFriendList = new ArrayList<User>();



                // Add those who donot have friends to the list
                if(friendNumber<1){

                    userAndFriendText = userAndFriendText + user.getUserName() + ", " + " " + "\n";

                } else {


                    for(Integer index = 2; index < friendNumber + 2; index ++){


                        User friend = new User();
                        friend.setUserName(attributes.get(index).trim());

                        userFriendList.add(friend);


                        if(!(friend.getUserName().isEmpty()))
                            userAndFriendText = userAndFriendText + user.getUserName() + ", " + friend.getUserName() + "\n";
                    }

                }


                System.out.println(userAndFriendText);

                // set the user friend list
                user.setFriendsList(userFriendList);

                userList.add(user);

                System.out.println("Done adding new user");


                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newfile.getAbsoluteFile(), true));

                bufferedWriter.write(userAndFriendText);
                bufferedWriter.close();

            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }






    public static void cleanThreadsAddDate(String file){

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file
        File newfile = new File("/Users/johnshu/Java programming/WebScraper/CLEANED/UNIT_AUTHOR_ThreadAuthorsAndResponsesAndDates" + dateString + ".csv");

        if (!newfile.exists()) {
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        System.out.println("\n\nPrint out of File " + file);

        List<User> userList = new ArrayList<User>();
        Integer friendNumber = 0;

        try {
            Scanner input = new Scanner (new File(file));
            while (input.hasNextLine()) {


                String line = input.nextLine();
                System.out.println("\n\nPrinting Line :" + line);

                List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));

                for(String value: attributes){

                    System.out.println("Value : " + value);

                }

                System.out.println("New User: " + attributes.get(0).trim());
                User user = new User();
                user.setUserName(attributes.get(0).trim());

                // Check if 2nd value is a number
                if(isInt(attributes.get(1).trim())){

                    friendNumber = Integer.parseInt(attributes.get(1).trim());
                    System.out.println("Friend Number set to: " + friendNumber);
                }

                String userAndFriendText = "";

                // Add those who donot have friends to the list
                if(friendNumber<1){

                    userAndFriendText = userAndFriendText + user.getUserName() + ", " + attributes.get(2).trim() + " " + "\n";
                    System.out.println(userAndFriendText);

                }

                // Create a user friend list
                List<User> userFriendList = new ArrayList<User>();


                for(Integer index = 3; index < friendNumber + 3; index ++){


                    User friend = new User();
                    friend.setUserName(attributes.get(index).trim());

                    userFriendList.add(friend);


//                    userAndFriendText = userAndFriendText + user.getUserName() + ", " + friend.getUserName()+ ", " + attributes.get(2).trim() + "\n";
                    System.out.println(userAndFriendText);
                }

                userAndFriendText = user.getUserName();

                for(User friend: userFriendList){

                    if(userAndFriendText.matches(user.getUserName())){
                        userAndFriendText = userAndFriendText + ", " + attributes.get(2).trim() + ", " + friend.getUserName() + "\n";
                    } else{
                        userAndFriendText = userAndFriendText + ", , " + friend.getUserName() + "\n";

                    }

                }

                // set the user friend list
                user.setFriendsList(userFriendList);

                userList.add(user);

                System.out.println("Done adding new user");


                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newfile.getAbsoluteFile(), true));

                bufferedWriter.write(userAndFriendText);
                bufferedWriter.close();

            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //
    //
    //
    public static void cleanUserNotes(String file){

    }


    //
    //
    //
    public static void cleanUserPosts(String file){

    }


    //
    //
    //
    public static void cleanUserThreadsFriends(String file){

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file
        File newfile = new File("/Users/johnshu/Java programming/WebScraper/CLEANED/CLEANED_ThreadAuthorsAndResponses" + dateString + ".csv");

        if (!newfile.exists()) {
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        System.out.println("\n\nPrint out of File " + file);

        List<User> userList = new ArrayList<User>();
        Integer friendNumber = 0;

        try {
            Scanner input = new Scanner (new File(file));
            while (input.hasNextLine()) {


                String line = input.nextLine();
                System.out.println("\n\nPrinting Line :" + line);

                List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));

                for(String value: attributes){

                    System.out.println("Value : " + value);

                }


                // Check if 2nd value is a number
                if(isInt(attributes.get(1).trim())){

                    friendNumber = Integer.parseInt(attributes.get(1).trim());
                    System.out.println("Friend Number set to: " + friendNumber);
                }

                if ((attributes.get(0).trim()).length() > 1 ){

                    System.out.println("New User: " + attributes.get(0).trim());
                    User user = new User();

                    // Check if user friends is greater than 0
                    //                            int userIndex = userList.indexOf(attributes.get(0).trim());
                    //                            User userToCheck = userList.get(userIndex);
                    if( friendNumber > 0){

                        user.setUserName(attributes.get(0).trim());

                        user.setNumberOfFriends(friendNumber);
                        System.out.println("User: " + user.userName + ", Friends: " + friendNumber);


                    } else{

                        user.setUserName(attributes.get(0).trim());

                        if (isInt(attributes.get(1).trim())) {
                            user.setNumberOfFriends(Integer.parseInt(attributes.get(1).trim()));

                        } else {
                            user.setNumberOfFriends(0);

                        }

                    } // End of else

                    String userAndFriendText = "";

                    // Add those who donot have friends to the list
                    if(friendNumber<1){

                        userAndFriendText = userAndFriendText + user.getUserName() + ", " + " " + "\n";

                    }

                    while(friendNumber > 0){

                        String friendLine = input.nextLine();
                        System.out.println("Printing Friend Line :" + friendLine);

                        List<String> friends = new ArrayList<String>(Arrays.asList(friendLine.split(",")));

                        for(String value: friends){

                            System.out.println("Value : " + value);

                        }

                        User friend = new User();
                        friend.setUserName(friends.get(2).trim());
                        List<User> userFriendList = new ArrayList<User>();
                        userFriendList.add(friend);

                        user.setFriendsList(userFriendList);

                        userAndFriendText = userAndFriendText + user.getUserName() + ", " + friend.getUserName() + "\n";
                        System.out.println(userAndFriendText);
                        friendNumber--;
                    }


                    userList.add(user);

                    System.out.println("Done adding new user");


                    // Get file and write user friends to the file
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newfile.getAbsoluteFile(), true));

                    bufferedWriter.write(userAndFriendText);
                    bufferedWriter.close();

                }



                //                    } // End of If hasNextLine




                //                }catch (Exception e){
                //                    e.printStackTrace();
                //                    break;
                //                }



            } // End of While Loop




        } catch (Exception e) {
            e.printStackTrace();

        }

    } // End of Method

    public static void cleanUserComplete(String file){

    }


    static boolean isInt(String s)
    {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }



}
