package ch.frankel.blog.view;

import ch.frankel.blog.entity.Cart;
import ch.frankel.blog.entity.Product;
import ch.frankel.blog.view.PriceAndRows.CartRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceAndRowsCollectorTest {

    @Nested
    public class EmptyCartTest {

        private Cart cart;

        @BeforeEach
        protected void setUp() {
            cart = new Cart();
        }

        @Test
        public void rowsCollectionShouldBeEmptyWhenCartIsEmpty() {
            var priceAndRows = PriceAndRows.getPriceAndRows(cart);
            assertThat(priceAndRows.getRows()).isEmpty();
        }

        @Test
        public void priceShouldBeZeroWhenCartIsEmpty() {
            var priceAndRows = PriceAndRows.getPriceAndRows(cart);
            assertThat(priceAndRows.getPrice()).isZero();
        }
    }

    @Nested
    public class SingleProductInCartTest {

        private Product product;
        private int quantity;
        private Cart cart;

        @BeforeEach
        protected void setUp() {
            cart = new Cart();
            product = new Product(1L, "Product", BigDecimal.TEN);
            quantity = 3;
            cart.add(product, quantity);
        }

        @Test
        public void rowCollectionShouldContainSingleRowWhenCartContainsSingleProduct() {
            var priceAndRows = PriceAndRows.getPriceAndRows(cart);
            assertThat(priceAndRows.getRows())
                    .isNotEmpty()
                    .hasSize(1);
        }

        @Test
        public void rowShouldContainProductWhenCartContainsSingleProduct() {
            var priceAndRows = PriceAndRows.getPriceAndRows(cart);
            var entry = new AbstractMap.SimpleEntry<>(product, quantity);
            assertThat(priceAndRows.getRows())
                    .containsExactly(new CartRow(entry));
        }

        @Test
        public void priceShouldBeEqualToSingleProductPriceTimesQuantity() {
            var priceAndRows = PriceAndRows.getPriceAndRows(cart);
            assertThat(priceAndRows.getPrice())
                    .isEqualByComparingTo(
                            product.getPrice().multiply(new BigDecimal(quantity))
                    );
        }
    }

    @Nested
    public class MultipleProductsInCartTest {

        private Collection<Product> products;
        private Cart cart;

        @BeforeEach
        protected void setUp() {
            cart = new Cart();
            products = new ArrayList<>();
            products.add(new Product(1L, "One", BigDecimal.ONE));
            products.add(new Product(2L, "Two", new BigDecimal(2)));
            products.add(new Product(3L, "Three", new BigDecimal(3)));
            products.forEach(
                    p -> cart.add(p, p.getPrice().toBigInteger().intValue())
            );
        }

        @Test
        public void rowsShouldBeEqualToTheNumberOfProductsInCart() {
            var priceAndRows = PriceAndRows.getPriceAndRows(cart);
            assertThat(priceAndRows.getRows())
                    .hasSize(products.size());
        }

        @Test
        public void priceShouldBeEqualToTheSumOfProductPriceTimesQuantity() {
            var priceAndRows = PriceAndRows.getPriceAndRows(cart);
            assertThat(priceAndRows.getPrice())
                    .isEqualByComparingTo(new BigDecimal(14));
        }
    }
}