package BL;

/**
 *
 * @author franz
 */
public class Article {
    private int articleid;
    private String artname;
    private double price;
    private int amount;

    public Article(int articleid, String artname, double price, int amount) {
        this.articleid = articleid;
        this.artname = artname;
        this.price = price;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
}
