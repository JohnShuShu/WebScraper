package org.medhelp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 5/21/15
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class User {
//
//    String fileHeading = "User Name" + "," + "," + "Date Joined" + "Birth Date" + "," + "Posts Count" + "," + "Posts Frequency"
//            + "," + "Posts Percentage"+ "," + "Time online"  + "," + "Reputation" + "," + "Prestige"
//            + "," + "Reported Posts" + "," + "Awards" + "," + "Profile Page" + "\n";

    public String userName;
    public Integer uniqueId;
    public Integer pageId;
    public String gender;
    public String dateJoined;
    public String bestAnswer;
    public Integer numberOfFriends;
    public Integer numberOfPosts;
    public Integer numberOfNotes;
    public Integer numberOfStatus;
    public Integer numberOfPhotos;
    public Integer numberOfTrackers;
    public Integer numberOfTickers;
    public Integer numberOfCommunities;
    public Integer numberOfJournals;
    public Double postFrequency;
    public Double postPercentage;
    public String timeOnline;
    public String timeInSecs;
    public String reputation;
    public String prestige;
    public Integer reportedPosts;
    public String stars;
    public Integer age;
    public String birthDate;
    public String awards;
    public String userPageLink;
    public List<Note> friendsNotes;
    public List<Post> userPosts;
    public List<User> friendsList;
    public String country;
    public String tenure;


    public User(){

    }

    public  User(String userName){
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName.replace(",","");
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender.replace(",","");
    }

    public String getDateJoined() {
        return dateJoined.replace(",","");
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined.replace(",","");
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
        return bestAnswer.replace(",","");
    }

    public void setBestAnswer(String bestAnswer) {
        this.bestAnswer = bestAnswer.replace(",","");
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

    public Double getPostFrequency() {
        return postFrequency;
    }

    public void setPostFrequency(Double postFrequency) {
        this.postFrequency = postFrequency;
    }

    public Double getPostPercentage() {
        return postPercentage;
    }

    public void setPostPercentage(Double postPercentage) {
        this.postPercentage = postPercentage;
    }

    public String getTimeOnline() {
        return timeOnline;
    }

    public void setTimeOnline(String timeOnline) {
        this.timeOnline = timeOnline.replace(",","");
    }

    public String getReputation() {
        return reputation;
    }

    public void setReputation(String reputation) {
        this.reputation = reputation.replace(",","");
    }

    public String getPrestige() {
        return prestige;
    }

    public void setPrestige(String prestige) {
        this.prestige = prestige.replace(",","");
    }

    public Integer getReportedPosts() {
        return reportedPosts;
    }

    public void setReportedPosts(Integer reportedPosts) {
        this.reportedPosts = reportedPosts;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate.replace(",","");
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards.replace(",","");
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars.replace(",","");
    }

    public String getTimeInSecs() {
        return timeInSecs;
    }

    public void setTimeInSecs(String timeInSecs) {
        this.timeInSecs = timeInSecs;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
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

    public String printToFileHackForums() {
        String fileText = "";

        String fileHeading = "User_Name" + "," + "Date_Joined" + "," + "Birth_Date" + "," + "Age" + "," + "Posts_Count" + "," + "Posts_Frequency"
                + "," + "Posts_Percentage"+ "," + "Time_online"  + "," + "Time_in_Secs"  + "," + "Reputation" + "," + "Prestige"
                + "," + "Awards" + "," + "Stars" + "," + "Profile_Page" + "\n";


        fileText = userName + "," + dateJoined + "," + birthDate + "," + age + "," + numberOfPosts + "," +  postFrequency
                + "," + postPercentage + "," + timeOnline + "," + timeInSecs + "," + reputation + "," + prestige
                + "," + awards + "," + stars + "," + userPageLink  + "\n";


        return fileText;

    }

    public String printToFileSEForums() {
        String fileText = "";

        fileText = userName + "," + country + "," + tenure + "," + reputation + "," + prestige + "," + awards + ", " + userPageLink  + "\n";


        return fileText;

    }

    @Override
    public String toString() {
        return "User{" +
                ", userName='" + userName + '\'' +
                ", uniqueId=" + uniqueId +
                ", pageId=" + pageId +
                ", gender='" + gender + '\'' +
                ", dateJoined='" + dateJoined + '\'' +
                ", bestAnswer='" + bestAnswer + '\'' +
                ", numberOfFriends=" + numberOfFriends +
                ", numberOfPosts=" + numberOfPosts +
                ", numberOfNotes=" + numberOfNotes +
                ", numberOfStatus=" + numberOfStatus +
                ", numberOfPhotos=" + numberOfPhotos +
                ", numberOfTrackers=" + numberOfTrackers +
                ", numberOfTickers=" + numberOfTickers +
                ", numberOfCommunities=" + numberOfCommunities +
                ", numberOfJournals=" + numberOfJournals +
                ", postFrequency=" + postFrequency +
                ", postPercentage=" + postPercentage +
                ", timeOnline='" + timeOnline + '\'' +
                ", timeInSecs='" + timeInSecs + '\'' +
                ", reputation='" + reputation + '\'' +
                ", prestige='" + prestige + '\'' +
                ", reportedPosts=" + reportedPosts +
                ", stars=" + stars +
                ", age=" + age +
                ", birthDate='" + birthDate + '\'' +
                ", awards='" + awards + '\'' +
                ", userPageLink='" + userPageLink + '\'' +
                ", friendsNotes=" + friendsNotes +
                ", userPosts=" + userPosts +
                ", friendsList=" + friendsList +
                '}';
    }
}
