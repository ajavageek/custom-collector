package ch.frankel.blog.view;

import ch.frankel.blog.entity.Cart;
import ch.frankel.blog.entity.Product;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class PriceAndRows {

    private BigDecimal price;
    private final List<CartRow> rows = new ArrayList<>();

    PriceAndRows(BigDecimal price, List<CartRow> rows) {
        this.price = price;
        this.rows.addAll(rows);
    }

    PriceAndRows() {
        this(BigDecimal.ZERO, new ArrayList<>());
    }

    public BigDecimal getPrice() {
        return price;
    }

    public List<CartRow> getRows() {
        var copy = new ArrayList<>(rows);
        copy.sort(Comparator.comparing(row -> row.product.getLabel()));
        return Collections.unmodifiableList(copy);
    }

    public static class CartRow {

        private final Product product;
        private final int quantity;

        public CartRow(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public CartRow(Map.Entry<Product, Integer> entry) {
            this(entry.getKey(), entry.getValue());
        }

        public BigDecimal getRowPrice() {
            return product.getPrice().multiply(new BigDecimal(quantity));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var cartRow = (CartRow) o;
            return quantity == cartRow.quantity && Objects.equals(product, cartRow.product);
        }

        @Override
        public int hashCode() {
            return Objects.hash(product, quantity);
        }
    }

    public static PriceAndRows getPriceAndRows(Cart cart) {
        return cart.getProducts()
                .entrySet()
                .stream()
                .map(CartRow::new)
                .collect(Collectors.teeing(
                        Collectors.reducing(BigDecimal.ZERO, CartRow::getRowPrice, BigDecimal::add),
                        Collectors.toList(),
                        PriceAndRows::new
                ));
    }
}