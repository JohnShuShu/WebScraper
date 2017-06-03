package org.medhelp;

/**
 * Created by johnshu on 2/1/16.
 */
public class Forum {

    public String forumName;
    public String forumLink;
    public String numberOfQuestions;
    public Integer forumNumber;

    public Forum(){

    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public Integer getForumNumber() {
        return forumNumber;
    }

    public void setForumNumber(Integer forumNumber) {
        this.forumNumber = forumNumber;
    }


    public String getForumLink() {
        return forumLink;
    }

    public void setForumLink(String forumLink) {
        this.forumLink = forumLink;
    }

    public String getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(String numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "forumName='" + forumName + '\'' +
                ",forumLink='" + forumLink + '\'' +
                ",numberOfQuestions='" + numberOfQuestions + '\'' +
                '}';
    }


    public String printToFile(){

        String fileText = "";

        fileText = forumName.replace(",","")  + "," + numberOfQuestions.replace(",","") + "," + forumLink;

        return fileText;
    }


}
