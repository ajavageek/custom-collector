package ch.frankel.blog.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cart {

    private final Map<Product, Integer> products = new HashMap<>();

    public void add(Product product) {
        add(product, 1);
    }

    public void add(Product product, int quantity) {
        products.merge(product, quantity, Integer::sum);
    }

    public void remove(Product product) {
        products.remove(product);
    }

    public void setQuantity(Product product, int quantity) {
        products.put(product, quantity);
    }

    public Map<Product, Integer> getProducts() {
        return Collections.unmodifiableMap(products);
    }
}