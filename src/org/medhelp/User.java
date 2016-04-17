package org.medhelp;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 5/21/15
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class User {

    public String userName;
    public Integer uniqueId;
    public Integer pageId;
    public String gender;
    public String dateJoined;
    public Integer age;
    public Integer numberOfFriends;
    public String userPageLink;
    public List<Note> friendsNotes;
    public List<Post> userPosts;
    public List<User> friendsList;


    public User(){

    }

    public  User(String userName){
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateJoined() {
        return dateJoined.replace(",","");
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void getUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUserPageLink() {
        return userPageLink;
    }

    public void setUserPageLink(String userPageLink) {
        this.userPageLink = userPageLink;
    }

    public List<Post> getUserPosts() {
        return userPosts;
    }

    public void setUserPosts(List<Post> userPosts) {
        this.userPosts = userPosts;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<Note> getFriendsNotes() {
        return friendsNotes;
    }

    public void setFriendsNotes(List<Note> friendsNotes) {
        this.friendsNotes = friendsNotes;
    }

    public List<User> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<User> friendsList) {
        this.friendsList = friendsList;
    }


    public Integer getNumberOfFriends() {
        return numberOfFriends;
    }

    public void setNumberOfFriends(Integer numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
    }


    public String printToFile() {
        String fileText = "";

        fileText = fileText + userName + "\n";

        for(User  friend: friendsList){
            fileText = fileText + " , " + friend.userName  + " , " + friend.userPageLink + "\n";
        }

        return fileText;

    }
}
