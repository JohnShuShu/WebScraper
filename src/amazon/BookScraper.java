package amazon;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * Date: 4/14/15
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class BookScraper {

        public static void main(String[] args) throws IOException {

            WebDriver driver = new FirefoxDriver();
            driver.navigate().to("http://www.amazon.com/tag/center%20right?ref_=tag_dpp_cust_itdp_s_t&store=1");

            List<WebElement> allAuthors =  driver.findElements(By.className("tgProductAuthor"));
            List<WebElement> allTitles =  driver.findElements(By.className("tgProductTitleText"));


            int i=0;
            String fileText = "";
            for (WebElement author : allAuthors){
                String authorName = author.getText();
                System.out.println(authorName);
                String Url = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", allTitles.get(i++));
                System.out.println(Url);
                final Pattern pattern = Pattern.compile("title=(.+?)>");
                final Matcher matcher = pattern.matcher(Url);
                matcher.find();
                String title = matcher.group(1);
                fileText = fileText+authorName+","+title+"\n";
            }



            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("books.csv"), "utf-8"));
            writer.write(fileText);
            writer.close();

            driver.close();
        }

}