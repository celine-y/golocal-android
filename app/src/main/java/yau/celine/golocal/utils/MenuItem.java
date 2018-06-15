package yau.celine.golocal.utils;

public class MenuItem {
    private int id;
    private String name;
    private String description;
    private String shopUrl;
    private double price;
    private String categoryUrl;

    public MenuItem() {}

    public MenuItem(int id, String name, String description, String shopUrl, double price){
        this.id = id;
        this.name = name;
        this.description = description;
        this.shopUrl = shopUrl;
        this.price = price;
    }

    public MenuItem(int id, String name, String description, String shopUrl, double price, String categoryUrl){
        this.id = id;
        this.name = name;
        this.description = description;
        this.shopUrl = shopUrl;
        this.price = price;
        this.categoryUrl = categoryUrl;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getShopUrl(){
        return shopUrl;
    }

    public void setShopUrl(String shopUrl){
        this.shopUrl = shopUrl;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl){
        this.categoryUrl = categoryUrl;
    }
}
