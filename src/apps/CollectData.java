package apps;

/**
 * Created by johnshu on 4/14/16.
 */
public class CollectData {

    public static void main(String[] args) throws Exception {

        AmazonTopAppScraper.main(args);

        AppStoreScraper.main(args);

        AmazonAppScraper.main(args);

        ItunesStoreScraper.main(args);

    }
}
