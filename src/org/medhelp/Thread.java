package org.medhelp;

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
    public Integer commentsNumber;
    public Integer threadNumber;
    public Integer threadPageNumber;

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
        return commentsNumber;
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
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
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

        fileText = fileText + threadName.replace(",","")  + ", " +
                  threadLink+ ", " +
                  dateCreated.replace(",","")+ ", " +
                  threadCreator.userName.replace(",","")+ ", " +
                  threadCreatorLink+  ", " +
                  threadNumber+  ", " +
                  commentsNumber+ ", " +
                  printCommentors()+ "\n";

        return fileText;

    }
}
