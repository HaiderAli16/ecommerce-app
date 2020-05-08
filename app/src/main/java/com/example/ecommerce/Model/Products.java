package com.example.ecommerce.Model;

public class Products {
    private String category, date, description, image, name, p_id, price, time, productState;

    public Products() {
    }

    public Products(String category, String date, String description, String image, String name, String p_id, String price, String time, String productState) {
        this.category = category;
        this.date = date;
        this.description = description;
        this.image = image;
        this.name = name;
        this.p_id = p_id;
        this.price = price;
        this.time = time;
        this.productState = productState;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }
}
