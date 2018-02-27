package org.medhelp;

import hackforums.Comment;

import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 5/16/15
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Thread {


    public String threadName;
    public String threadLink;
    public String dateCreated;
    public User threadCreator;
    public String threadCreatorLink;
    public List<User> commentors;
    public List<Comment> commentList;
    public Integer commentsNumber;
    public Integer threadNumber;
    public Integer threadPageNumber;


    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<User> getCommentors() {
        return commentors;
    }

    public void setCommentors(List<User> commentors) {
        this.commentors = commentors;
    }

    public String printCommentors(){
        String commentorsList = "";

        try{
            if (commentors == null){
                commentorsList= "";
            } else {
                for(User commentor: commentors){
                    commentorsList = commentorsList + ", "+  commentor.userName;
                }
                return commentorsList;
            }
        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        return commentorsList;
    }

    public Integer getCommentsNumber() {
        return commentList.size();
    }

    public void setCommentsNumber(Integer commentsNumber) {
        this.commentsNumber = commentsNumber;
    }

    public void setCommentsNumber() {
        this.commentsNumber = this.commentors.size();
    }

    public User getThreadCreator() {
        return threadCreator;
    }

    public void setThreadCreator(User threadCreator) {
        this.threadCreator = threadCreator;
    }

    public String getThreadCreatorLink() {
        return threadCreatorLink;
    }

    public void setThreadCreatorLink(String threadCreatorLink) {
        this.threadCreatorLink = threadCreatorLink;
    }


    public Integer getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(Integer threadNumber) {
        this.threadNumber = threadNumber;
    }

    public Integer getThreadPageNumber() {
        return threadPageNumber;
    }

    public void setThreadPageNumber(Integer threadPageNumber) {
        this.threadNumber = threadPageNumber;
    }


    public String getThreadName() {
        return threadName.replace(",","");
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName.replace(",","");
    }

    public String getThreadLink() {
        return threadLink;
    }

    public void setThreadLink(String threadLink) {
        this.threadLink = threadLink;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Override
    public String toString() {
        return "Thread{" +
                "appName='" + threadName + '\'' +
                ", appLink='" + threadLink + '\'' +
                ", dateCreated=" + dateCreated +
                ", appCreator='" + threadCreator + '\'' +
                ", threadNumber=" + threadNumber +
                ", threadPageNumber=" + threadPageNumber +
                ", commentsNumber=" + commentsNumber +
                ", threadCreatorLink='" + threadCreatorLink + '\'' +
                ", threadCommentor='" + commentors + '\'' +
                '}';
    }

    public String printToFileQuotes() {
        String fileText = "";

        fileText = fileText + "\"" + threadName+ "\"" + ", " +
                "\"" + threadLink+ "\"" + ", " +
                "\"" + dateCreated+ "\"" + ", " +
                "\"" + threadCreator+ "\"" + ", " +
                "\"" + threadCreatorLink+ "\"" + ", " +
                "\"" + threadNumber+ "\"" + ", " +
                "\"" + threadPageNumber+ "\"" + ", " +
                "\"" + printCommentors()+ "\"" + ", " +
                "\"" + commentsNumber+ "\"" + ", " + "\n\n";

        return fileText;

    }


    public String printToFile() {
        String fileText = "";

        fileText = fileText + threadName.replace(",","")  + "," +
                  threadLink+ "," +
                  dateCreated.replace(",","")+ "," +
                  threadCreator.userName.replace(",","")+ "," +
                  threadCreator.getUniqueId()+  "," +
                  threadCreator.getPageId()+  "," +
                  threadCreatorLink+  "," +
                  threadNumber+  "," +
                  commentsNumber+ "," +
                  printCommentors()+ "\n";

        return fileText;

    }

    public String printHackForumsCommentsToFile() {
        String fileText = "";

        fileText = fileText + threadName.replace(",","")  + "," +
                threadLink+ "," +
                dateCreated.replace(",","")+ "," +
                threadCreator.userName.replace(",","")+ "," +
                threadCreatorLink+  "," +
                commentList.size()+  "," +
                commentsNumber+ "," +
                printCommentors()+ "\n";

        return fileText;

    }

    public String printHackForumThreadsToFile() {
        String fileText = "";

        fileText = fileText + threadName.replace(",","")  + "," +
                threadLink+ "," +
                dateCreated.replace(",","")+ "," +
                threadCreator.userName.replace(",","")+ "," +
                threadCreatorLink+  "," +
                commentList.size() + ",\n" +
                printHackForumCommentors()+ "\n\n";

        return fileText;

    }

    public String printHackForumCommentors(){
        String commentorsList = "";

        try{
            if (commentList == null){
                commentorsList= "";
            } else {
                for(Comment comment: commentList){
                    commentorsList = commentorsList + ",,,,,,," + comment.getCommentor().getUserName() + "," +  comment.getCommentDate() + "," + comment.getCommentTime() + "," + comment.getComment().replace(",",";").replace("\n", " ") + "\n";
                }
                return commentorsList;
            }
        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        return commentorsList;
    }

    public String printToFileLite() {
        String fileText = "";

        fileText = fileText + threadName.replace(",","")  + "," +
                threadLink+ "," +
                threadCreator.userName.replace(",","")+ "," +
                threadCreator.userPageLink+  "," + "\n";

        return fileText;

    }

    public String printFullyDetailedThread(List<User> userList){
        String finalText = "";

        String fileText = "";

//        System.out.println("Printing User before update: " + threadCreator.toString());

        for(User user: userList){
            if((threadCreator.userName).equals(user.userName)){

                threadCreator = user;
            }
        }

//        System.out.println("Printing User after update: " + threadCreator.toString());

        fileText = threadCreator.userName + ","
                + threadCreator.dateJoined + ","
                + threadCreator.birthDate + ","
                + threadCreator.age + ","
                + threadCreator.numberOfPosts + ","
                + threadCreator.postFrequency + ","
                + threadCreator.postPercentage + ","
                + threadCreator.timeOnline + ","
                + threadCreator.timeInSecs + ","
                + threadCreator.reputation + ","
                + threadCreator.prestige + ","
                + threadCreator.awards + ","
                + threadCreator.stars + ","
                + threadCreatorLink + ","
                + commentList.size() + ","
                + threadName.replace(",","")  + ","
                + threadLink + ","
                + dateCreated.replace(",","").trim() + ","
                + threadCreator.userName +  ",";


        String commentorsList = "";

        try{
            if (commentList == null){
                commentorsList= "";
            } else {
                for(Comment comment: commentList){
                    commentorsList = commentorsList +
                            fileText +
                            comment.getCommentType() + "," +
                            comment.getCommentor().getUserName() + "," +
                            comment.getCommentDate() + "," +
                            comment.getCommentTime() + "," +
                            comment.getComment().replace(","," ").replace("\n", " ") + "\n";
                }
                return commentorsList;
            }
        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        return commentorsList; // This is actually the final list of everything.
    }

}
