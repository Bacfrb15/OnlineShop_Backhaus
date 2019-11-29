package BL;

/**
 *
 * @author franz
 */
public class Article {
    private int articleid;
    private String artname;
    private double price;

    public Article(int articleid, String artname, double price) {
        this.articleid = articleid;
        this.artname = artname;
        this.price = price;
    }

    public int getArticleid() {
        return articleid;
    }

    public void setArticleid(int articleid) {
        this.articleid = articleid;
    }

    public String getArtname() {
        return artname;
    }

    public void setArtname(String artname) {
        this.artname = artname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
