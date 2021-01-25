package ch.frankel.blog.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private final Long id;
    private final String label;
    private final BigDecimal price;

    public Product(Long id, String label, BigDecimal price) {
        this.id = id;
        this.label = label;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}