package org.medhelp;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 6/1/15
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Post {

    public String postDate;
    public String postCommunity;
    public String postName;

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostCommunity() {
        return postCommunity;
    }

    public void setPostCommunity(String postCommunity) {
        this.postCommunity = postCommunity;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String printToFile() {
        String fileText = "";

        fileText = fileText + postName.replace(",","") + ", " +
                  postDate.replace(",","")+ ", " +
                  postCommunity.replace(",","")+ "\n";


        return fileText;

    }
}
