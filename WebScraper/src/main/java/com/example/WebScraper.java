package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Product {
    private String name;
    private String price;
    private String rating;

    public Product(String name, String price, String rating) {
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }
}

public class WebScraper {

    private static List<Product> scrapeProducts(String url) throws IOException {
        List<Product> productList = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Elements products = doc.select(".product_pod");

        for (Element product : products) {
            String name = product.select("h3 > a").attr("title");
            String price = product.select(".price_color").text();
            String rating = product.select(".star-rating").attr("class").replace("star-rating", "").trim();

            if (!name.isEmpty() && !price.isEmpty() && !rating.isEmpty()) {
                productList.add(new Product(name, price, rating));
            }
        }
        return productList;
    }

    private static void writeProductsToCSV(List<Product> products, String fileName) throws IOException {
        FileWriter out = new FileWriter(fileName);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("Name", "Price", "Rating"))) {
            for (Product product : products) {
                printer.printRecord(product.getName(), product.getPrice(), product.getRating());
            }
        }
    }

    public static void main(String[] args) {
        String url = "http://books.toscrape.com/";
        try {
            List<Product> products = scrapeProducts(url);
            writeProductsToCSV(products, "products.csv");
            System.out.println("Data has been successfully scraped and stored in products.csv");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
