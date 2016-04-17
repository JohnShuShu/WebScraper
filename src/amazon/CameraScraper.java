/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 4/14/15
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */

package amazon;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


public class CameraScraper {

    public static void main(String[] args) throws IOException {

        WebDriver chromeDriver = new FirefoxDriver();
        //WebDriver chromeDriver = new FirefoxDriver();
        chromeDriver.navigate().to("http://www.amazon.com/gp/new-releases/electronics/281052/ref=zg_bs_tab_t_bsnr");


        List<WebElement> cameraNames=  chromeDriver.findElements(By.className("valign_wrapper"));

        List<WebElement> cameraRatings= chromeDriver.findElements(By.className("crAvgStars"));

        List<WebElement> cameraPrices =  chromeDriver.findElements(By.className("price"));

        List<WebElement> cameraListPrices =  chromeDriver.findElements(By.className("listprice"));


        String fileText = "";
        System.out.println(cameraRatings.size());

        for (int i=0; i < cameraListPrices.size(); i++){


            System.out.println("*****" + i + "*****");

            // Extract Camera Names
            String Url = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", cameraNames.get(i));
            System.out.println(Url);
            Pattern pattern = Pattern.compile("alt=\"(.+?)\"");
            Matcher matcher = pattern.matcher(Url);
            matcher.find();
            String cameraName = matcher.group(1);
            System.out.println(cameraName);

            //Extract Camera Ratings
//            String Url1 = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", cameraRatings.get(i));
//            System.out.print(Url1);
            pattern = Pattern.compile("title=(.+?)>");
            matcher = pattern.matcher(Url);
            matcher.find();
            String cameraRating = matcher.group(1);
            System.out.println(cameraRating);

            //Extract Camera Prices
            WebElement prices = cameraPrices.get(i);
            String cameraPrice = prices.getText();
            System.out.println(cameraPrice);


            //Extract Camera List Prices
            WebElement listPrices = cameraListPrices.get(i);
            String cameraListPrice = listPrices.getText();
            System.out.println(cameraListPrice);



            fileText = fileText+cameraName+","+cameraRating+","+","+cameraPrice+","+cameraListPrice+"\n";

        }


        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Cameras.csv"), "utf-8"));
        writer.write(fileText);
        writer.close();

        chromeDriver.close();
    }
}

