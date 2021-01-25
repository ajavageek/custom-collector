package ch.frankel.blog.view;

import ch.frankel.blog.entity.Cart;
import ch.frankel.blog.entity.Product;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

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
                .collect(new PriceAndRowsCollector());
    }

    private static class PriceAndRowsCollector implements Collector<Entry<Product, Integer>, PriceAndRows, PriceAndRows> {

        @Override
        public Supplier<PriceAndRows> supplier() {
            return PriceAndRows::new;
        }

        @Override
        public BiConsumer<PriceAndRows, Entry<Product, Integer>> accumulator() {
            return (priceAndRows, entry) -> {
                var row = new CartRow(entry);
                priceAndRows.price = priceAndRows.price.add(row.getRowPrice());
                priceAndRows.rows.add(row);
            };
        }

        @Override
        public BinaryOperator<PriceAndRows> combiner() {
            return (c1, c2) -> {
                c1.price = c1.price.add(c2.price);
                var rows = new ArrayList<>(c1.rows);
                rows.addAll(c2.rows);
                return new PriceAndRows(c1.price, rows);
            };
        }

        @Override
        public Function<PriceAndRows, PriceAndRows> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.IDENTITY_FINISH);
        }
    }
}