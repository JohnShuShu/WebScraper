package apps;

import org.medhelp.User;
import sun.rmi.rmic.iiop.ValueType;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * To change this template use File | Settings | File Templates.
 */
public class App {

    public String appName;
    public String appCreator;
    public String appPageLink;
    public String downloads;
    public String numberOfRatings;
    public String starRatings;
    public String numberOfRatingsAll;
    public String starRatingsAll;
    public Map<Integer, String> histogram;
    public String changeUp;
    public String daysRank;
    public String peakRank;
    public String releaseDate;
    public String lastUpdated;
    public String dateCollected;
    public String contentRating;
    public String inAppProducts;
    public String appPrice;
    public String appDollarPrice;
    public String amazonBestSellerRank;
    public String androidAppstoreRank;
    public String androidAppstoreCategoryRank;
    public String appCategory;
    public String appSize;
    public boolean editorChoice;
    public boolean appPaid;
    public boolean topDeveloper;


    public App(){

    }


    public App(String appName, String appCreator, String appPageLink, String downloads, String numberOfRatings,
               String starRatings, Map histogram, String releaseDate, String lastUpdated, String contentRating,
               String inAppProducts, String amazonBestSellerRank, String androidAppstoreRank, String appSize, boolean
                       editorChoice, boolean topDeveloper) {
        this.appName = appName;
        this.appCreator = appCreator;
        this.appPageLink = appPageLink;
        this.downloads = downloads;
        this.numberOfRatings = numberOfRatings;
        this.starRatings = starRatings;
        this.histogram = histogram;
        this.releaseDate = releaseDate;
        this.lastUpdated = lastUpdated;
        this.contentRating = contentRating;
        this.inAppProducts = inAppProducts;
        this.amazonBestSellerRank = amazonBestSellerRank;
        this.androidAppstoreRank = androidAppstoreRank;
        this.appSize = appSize;
        this.editorChoice = editorChoice;
        this.topDeveloper = topDeveloper;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCreator() {
        return appCreator;
    }

    public void setAppCreator(String appCreator) {
        this.appCreator = appCreator;
    }

    public String getAppPageLink() {
        return appPageLink;
    }

    public void setAppPageLink(String appPageLink) {
        this.appPageLink = appPageLink;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(String numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public String getStarRatings() {
        return starRatings;
    }

    public void setStarRatings(String starRatings) {
        this.starRatings = starRatings;
    }

    public String getNumberOfRatingsAll() {
        return numberOfRatingsAll;
    }

    public void setNumberOfRatingsAll(String numberOfRatingsAll) {
        this.numberOfRatingsAll = numberOfRatingsAll;
    }

    public String getStarRatingsAll() {
        return starRatingsAll;
    }

    public void setStarRatingsAll(String starRatingsAll) {
        this.starRatingsAll = starRatingsAll;
    }

    public Map<Integer, String> getHistogram() {
        return histogram;
    }

    public void setHistogram(Map<Integer, String> histogram) {
        this.histogram = histogram;
    }

    public String getChangeUp() {
        return changeUp;
    }

    public void setChangeUp(String changeUp) {
        this.changeUp = changeUp;
    }

    public String getDaysRank() {
        return daysRank;
    }

    public void setDaysRank(String daysRank) {
        this.daysRank = daysRank;
    }

    public String getPeakRank() {
        return peakRank;
    }

    public void setPeakRank(String peakRank) {
        this.peakRank = peakRank;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDateCollected() {
        return dateCollected;
    }

    public void setDateCollected(String dateCollected) {
        this.dateCollected = dateCollected;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public String getInAppProducts() {
        return inAppProducts;
    }

    public void setInAppProducts(String inAppProducts) {
        this.inAppProducts = inAppProducts;
    }

    public String getAppPrice() {
        return appPrice;
    }

    public void setAppPrice(String appPrice) {
        this.appPrice = appPrice;
    }

    public String getAppDollarPrice() {
        return appDollarPrice;
    }

    public void setAppDollarPrice(String appDollarPrice) {
        this.appDollarPrice = appDollarPrice;
    }

    public String getAmazonBestSellerRank() {
        return amazonBestSellerRank;
    }

    public void setAmazonBestSellerRank(String amazonBestSellerRank) {
        this.amazonBestSellerRank = amazonBestSellerRank;
    }

    public String getAndroidAppstoreRank() {
        return androidAppstoreRank;
    }

    public void setAndroidAppstoreRank(String androidAppstoreRank) {
        this.androidAppstoreRank = androidAppstoreRank;
    }

    public String getAndroidAppstoreCategoryRank() {
        return androidAppstoreCategoryRank;
    }

    public void setAndroidAppstoreCategoryRank(String androidAppstoreCategoryRank) {
        this.androidAppstoreCategoryRank = androidAppstoreCategoryRank;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public boolean isEditorChoice() {
        return editorChoice;
    }

    public void setEditorChoice(boolean editorChoice) {
        this.editorChoice = editorChoice;
    }

    public boolean isAppPaid() {
        return appPaid;
    }

    public void setAppPaid(boolean appPaid) {
        this.appPaid = appPaid;
    }

    public boolean isTopDeveloper() {
        return topDeveloper;
    }

    public void setTopDeveloper(boolean topDeveloper) {
        this.topDeveloper = topDeveloper;
    }



    @Override
    public String toString() {
        return "App{" +
                "appName='" + appName + '\'' +
                ", appCreator='" + appCreator + '\'' +
                ", appPageLink='" + appPageLink + '\'' +
                ", downloads='" + downloads + '\'' +
                ", numberOfRatings='" + numberOfRatings + '\'' +
                ", starRatings='" + starRatings + '\'' +
                ", histogram=" + histogram +
                ", releaseDate='" + releaseDate + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", contentRating='" + contentRating + '\'' +
                ", inAppProducts='" + inAppProducts + '\'' +
                ", amazonBestSellerRank='" + amazonBestSellerRank + '\'' +
                ", androidAppstoreRank='" + androidAppstoreRank + '\'' +
                ", appSize='" + appSize + '\'' +
                ", editorChoice=" + editorChoice +
                ", topDeveloper=" + topDeveloper +
                '}';
    }

    public String printHashMap(){
        String histogramString="";

        for (Map.Entry<Integer, String> entry : histogram.entrySet() ) {
            String value = entry.getValue();
            if(entry.getValue().trim().length()<1){
                value = "0";
            }
            histogramString = value + ", " + histogramString;
        }

//        Set<Map.Entry<Integer,String>> hashSet = histogram.entrySet();
//
//        for(Map.Entry entry:hashSet ) {
//
//            histogramString = histogramString + ", " + entry.getValue();
//        }

        return  histogramString;
    }

    public String printToFile() {
        String fileText = "";


        fileText = fileText +
                dateCollected + ", " +
                appName.replace(",","") + ", " +
                appCreator.replace(",","") + ", " +
                numberOfRatings.replace(",","") + ", " +
                starRatings + ", " +
                printHashMap() +
                releaseDate + ", " +
                lastUpdated + ", " +
                appPaid + ", " +
                inAppProducts + ", " +
                appPrice + ", " +
                //appDollarPrice + ", " +
                amazonBestSellerRank + ", " +
                androidAppstoreRank + ", " +
                //androidAppstoreCategoryRank + ", " +
                appCategory + ", " +
                appSize + ", " +
                appPageLink +"\n";

        return fileText;
    }

    public String printToGoogleAppsFile(){
        String fileText = "";

        fileText = fileText +
                dateCollected + ", " +
                appName.replace(",","") + ", " +
                appCreator.replace(",","") + ", " +
                numberOfRatings.replace(",","") + ", " +
                starRatings + ", " +
                isEditorChoice() + ", " +
                isTopDeveloper() + ", " +
                printHashMap() +
                appPrice + ", " +
                releaseDate + ", " +
                lastUpdated + ", " +
                appCategory + ", " +
                contentRating + ", " +
                downloads + ", " +
                inAppProducts + ", " +
                //appDollarPrice + ", " +
                appPageLink  +"\n";

        return fileText;
    }

    public String printToItunesAppsFile(){
        String fileText = "";

        numberOfRatingsAll = (numberOfRatingsAll == null)? numberOfRatings : numberOfRatingsAll;
        starRatingsAll = (starRatingsAll == null)? starRatings : starRatingsAll;

        fileText = fileText +
                dateCollected + ", " +
                appName.replace(",","") + ", " +
                appCreator.replace(",","") + ", " +
                numberOfRatings.replace(",","") + ", " +
                starRatings + ", " +
                numberOfRatingsAll.replace(",","") + ", " +
                starRatingsAll + ", " +
                changeUp + ", " +
                daysRank + ", " +
                peakRank + ", " +
//                printHashMap() +
                appPrice + ", " +
                appCategory + "," +
                lastUpdated + ", " +
//                contentRating + ", " +
//                downloads + ", " +
                inAppProducts + ", " +
                appSize + "," +
                //appDollarPrice + ", " +
                appPageLink +"\n";

        return fileText;
    }

//    public String printToFile() {
//        String fileText = "";
//
//        fileText = fileText + appName + ", " +
//                appLink + ", " +
//                dateCreated+ ", " +
//                appCreator + ", " +
//                threadCreatorLink+  ", " +
//                threadNumber+  ", " +
//                threadPageNumber+  ", " +
//                printCommentors()+ ", " +
//                commentsNumber+  "\n";
//
//        return fileText;
//
//    }


}


//    public String getAppName() {
//        return appName;
//    }
//
//    public void setAppName(String appName) {
//        this.appName = appName;
//    }
//
//    public String getAppCreator() {
//        return appCreator;
//    }
//
//    public void setAppCreator(String appCreator) {
//        this.appCreator = appCreator;
//    }
//
//    public String getAppPageLink() {
//        return appPageLink;
//    }
//
//    public void setAppPageLink(String appPageLink) {
//        this.appPageLink = appPageLink;
//    }
//
//    public String getDownloads() {
//        return downloads;
//    }
//
//    public void setDownloads(String downloads) {
//        this.downloads = downloads;
//    }
//
//    public String getNumberOfRatings() {
//        return numberOfRatings;
//    }
//
//    public void setNumberOfRatings(String numberOfRatings) {
//        this.numberOfRatings = numberOfRatings;
//    }
//
//    public String getStarRatings() {
//        return starRatings;
//    }
//
//    public void setStarRatings(String starRatings) {
//        this.starRatings = starRatings;
//    }
//
//    public String getNumberOfRatingsAll() {
//        return numberOfRatingsAll;
//    }
//
//    public void setNumberOfRatingsAll(String numberOfRatingsAll) {
//        this.numberOfRatingsAll = numberOfRatingsAll;
//    }
//
//    public String getStarRatingsAll() {
//        return starRatingsAll;
//    }
//
//    public void setStarRatingsAll(String starRatingsAll) {
//        this.starRatingsAll = starRatingsAll;
//    }
//
//    public Map getHistogram() {
//        return histogram;
//    }
//
//    public void setHistogram(Map histogram) {
//        this.histogram = histogram;
//    }
//
//    public String getReleaseDate() {
//        return releaseDate;
//    }
//
//    public void setReleaseDate(String releaseDate) {
//        this.releaseDate = releaseDate;
//    }
//
//    public String getLastUpdated() {
//        return lastUpdated;
//    }
//
//    public void setLastUpdated(String lastUpdated) {
//        this.lastUpdated = lastUpdated;
//    }
//
//    public String getDateCollected() {
//        return dateCollected;
//    }
//
//    public void setDateCollected(String dateCollected) {
//        this.dateCollected = dateCollected;
//    }
//
//    public String getContentRating() {
//        return contentRating;
//    }
//
//    public void setContentRating(String contentRating) {
//        this.contentRating = contentRating;
//    }
//
//    public String getInAppProducts() {
//        return inAppProducts;
//    }
//
//    public void setInAppProducts(String inAppProducts) {
//        this.inAppProducts = inAppProducts;
//    }
//
//    public String getappPrice() {
//        return appPrice;
//    }
//
//    public void setAppPrice(String appPrice) {
//        this.appPrice = appPrice;
//    }
//
//    public void setAppDollarPrice(String appDollarPrice) {
//        this.appDollarPrice = appDollarPrice;
//    }
//
//    public String getappDollarPrice() {
//        return appDollarPrice;
//    }
//
//    public String getAmazonBestSellerRank() {
//        return amazonBestSellerRank;
//    }
//
//    public void setAmazonBestSellerRank(String amazonBestSellerRank) {
//        this.amazonBestSellerRank = amazonBestSellerRank;
//    }
//
//    public String getAndroidAppstoreRank() {
//        return androidAppstoreRank;
//    }
//
//    public void setAndroidAppstoreRank(String androidAppstoreRank) {
//        this.androidAppstoreRank = androidAppstoreRank;
//    }
//
//    public String getAndroidAppstoreCategoryRank() {
//        return androidAppstoreCategoryRank;
//    }
//
//    public void setAndroidAppstoreCategoryRank(String androidAppstoreCategoryRank) {
//        this.androidAppstoreCategoryRank = androidAppstoreCategoryRank;
//    }
//
//    public String getAppSize() {
//        return appSize;
//    }
//
//    public void setAppSize(String appSize) {
//        this.appSize = appSize;
//    }
//
//    public String getChangeUp() {
//        return changeUp;
//    }
//
//    public void setChangeUp(String changeUp) {
//        this.changeUp = changeUp;
//    }
//
//    public String getAppCategory() {
//        return appCategory;
//    }
//
//    public void setAppCategory(String appCategory) {
//        this.appCategory = appCategory;
//    }
//
//    public boolean isEditorChoice() {
//        return editorChoice;
//    }
//
//    public void setEditorChoice(boolean editorChoice) {
//        this.editorChoice = editorChoice;
//    }
//
//    public boolean isAppPaid() {return appPaid;}
//
//    public void setAppPaid(boolean appPaid) {
//        this.appPaid = appPaid;
//    }
//
//    public boolean isTopDeveloper() {
//        return topDeveloper;
//    }
//
//    public void setTopDeveloper(boolean topDeveloper) {
//        this.topDeveloper = topDeveloper;
//    }