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
    public String bestAnswer;
    public Integer age;
    public Integer numberOfFriends;
    public Integer numberOfPosts;
    public Integer numberOfNotes;
    public Integer numberOfStatus;
    public Integer numberOfPhotos;
    public Integer numberOfTrackers;
    public Integer numberOfTickers;
    public Integer numberOfCommunities;
    public Integer numberOfJournals;
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

    public String getBestAnswer() {
        return bestAnswer;
    }

    public void setBestAnswer(String bestAnswer) {
        this.bestAnswer = bestAnswer;
    }

    public Integer getNumberOfPosts() {
        return numberOfPosts;
    }

    public void setNumberOfPosts(Integer numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }

    public Integer getNumberOfNotes() {
        return numberOfNotes;
    }

    public void setNumberOfNotes(Integer numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }

    public Integer getNumberOfStatus() {
        return numberOfStatus;
    }

    public void setNumberOfStatus(Integer numberOfStatus) {
        this.numberOfStatus = numberOfStatus;
    }

    public Integer getNumberOfCommunities() {
        return numberOfCommunities;
    }

    public void setNumberOfCommunities(Integer numberOfCommunities) {
        this.numberOfCommunities = numberOfCommunities;
    }

    public Integer getNumberOfJournals() {
        return numberOfJournals;
    }

    public void setNumberOfJournals(Integer numberOfJournals) {
        this.numberOfJournals = numberOfJournals;
    }

    public Integer getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public void setNumberOfPhotos(Integer numberOfPhotos) {
        this.numberOfPhotos = numberOfPhotos;
    }

    public Integer getNumberOfTrackers() {
        return numberOfTrackers;
    }

    public void setNumberOfTrackers(Integer numberOfTrackers) {
        this.numberOfTrackers = numberOfTrackers;
    }

    public Integer getNumberOfTickers() {
        return numberOfTickers;
    }

    public void setNumberOfTickers(Integer numberOfTickers) {
        this.numberOfTickers = numberOfTickers;
    }

    public String printToFile() {
        String fileText = "";

        fileText = fileText + "," + userName + "," + gender + "," + dateJoined + "," + numberOfStatus
                + "," + numberOfPosts + "," + numberOfJournals + "," + numberOfNotes + "," + numberOfCommunities
                + "," + numberOfFriends + "," + numberOfPhotos + "," + numberOfTrackers  + "," + numberOfTickers
                + "," + userPageLink + "\n";

//        for(User  friend: friendsList){
//            fileText = fileText + "," + friend.userName  + "," + friend.userPageLink + "\n";
//        }

        return fileText;

    }
}
