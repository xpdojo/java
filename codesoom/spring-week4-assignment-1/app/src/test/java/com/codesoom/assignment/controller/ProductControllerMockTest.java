package com.codesoom.assignment.controller;

import com.codesoom.assignment.error.exception.ProductNotFoundException;
import com.codesoom.assignment.product.ProductFixtures;
import com.codesoom.assignment.product.domain.Product;
import com.codesoom.assignment.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("ProductController ????????????")
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerMockTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    private Product productLaser;
    private Product productHelm;

    @BeforeEach
    void prepareProduct() {
        productLaser = ProductFixtures.laser();
        productHelm = ProductFixtures.helm();
    }

    @Nested
    @DisplayName("POST /products")
    class Describe_createProduct {

        @BeforeEach
        void mocking() {
            given(productService.create(any(Product.class)))
                    .willReturn(productLaser);
        }

        @Test
        @DisplayName("????????? ?????????, ???????????? ????????? ????????????")
        void It_creates_the_product_and_returns_it() throws Exception {
            // when
            var result = mockMvc.perform(
                    post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"" + productLaser.getName() + "\"}"));

            // then
            result.andExpect(status().isCreated())
                  .andExpect(jsonPath("$.name",
                                      containsString(productLaser.getName())))
                  .andExpect(jsonPath("$.maker",
                                      containsString(productLaser.getMaker())))
                  .andExpect(jsonPath("$.price",
                                      is(productLaser.getPrice()
                                                     .intValue())));

            verify(productService).create(any(Product.class));
        }
    }

    @Nested
    @DisplayName("GET /products/{id}")
    class Describe_getProduct {

        @Nested
        @DisplayName("?????? ???????????? ?????? ?????? ???????????? ???????????????")
        class Context_with_valid_product_id {

            @BeforeEach
            void mocking() {
                given(productService.get(productLaser.getId()))
                        .willReturn(productLaser);
            }

            @Test
            @DisplayName("????????? ???????????? ????????? ????????????")
            void It_returns_one_product_by_id() throws Exception {
                // when
                var result = mockMvc.perform(get("/products/" + productLaser.getId())
                                                     .contentType(MediaType.APPLICATION_JSON));
                // then
                result.andExpect(status().isOk())
                      .andExpect(jsonPath("$.name", containsString(productLaser.getName())))
                      .andExpect(jsonPath("$.maker",
                                          containsString(productLaser.getMaker())))
                      .andExpect(jsonPath("$.price",
                                          is(productLaser.getPrice()
                                                         .intValue())));

                verify(productService).get(productLaser.getId());
            }
        }

        @Nested
        @DisplayName("?????? ???????????? ?????? ?????? ?????? ???????????? ???????????????")
        class Context_with_invalid_product_id {
            private final Long invalidProductId = -1L;

            @BeforeEach
            void mocking() {
                given(productService.get(invalidProductId))
                        .willThrow(new ProductNotFoundException());
            }

            @Test
            @DisplayName("????????? ???????????? ????????? ????????????")
            void It_responds_status_not_found() throws Exception {
                // when
                var result =
                        mockMvc.perform(
                                get("/products/" + invalidProductId));

                // then
                result.andExpect(status().isNotFound())
                      .andExpect(jsonPath("$.message",
                                          containsString("Product Not Found")));

                verify(productService).get(invalidProductId);
            }
        }
    }

    @Nested
    @DisplayName("GET /products")
    class Describe_listProduct {
        private final int totalProductCount = 2;

        @BeforeEach
        void mocking() {
            final List<Product> products = new ArrayList<>();
            for (int index = 1; index <= totalProductCount; index++) {
                final Product product = ProductFixtures.helm();
                products.add(product);
            }

            given(productService.list())
                    .willReturn(products);
        }

        @Test
        @DisplayName("???????????? ?????? ?????? ????????? ????????????")
        void It_returns_one_product_by_id() throws Exception {
            Product productHelm = ProductFixtures.helm();

            // when
            mockMvc.perform(get("/products")
                                    .contentType(MediaType.APPLICATION_JSON))
                   // then
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$[*]",
                                       hasSize(totalProductCount)))
                   .andExpect(jsonPath("$[0].name",
                                       is(productHelm.getName())))
                   .andExpect(jsonPath("$[0].maker",
                                       containsString(productHelm.getMaker())))
                   .andExpect(jsonPath("$[0].price",
                                       is(productHelm.getPrice()
                                                     .intValue())));

            verify(productService).list();
        }
    }

    @Nested
    @DisplayName("DELETE /products/{id}")
    class Describe_deleteProduct {

        @Nested
        @DisplayName("?????? ????????? ?????? ???????????? ???????????????")
        class Context_with_valid_product_id {
            private final Long validProductId = 1L;

            @BeforeEach
            void mocking() {
                doNothing().when(productService)
                           .delete(validProductId);
            }

            @Test
            @DisplayName("204 No Content ?????? ????????? ????????????")
            void It_returns_one_product_by_id() throws Exception {
                // when
                mockMvc.perform(delete("/products/" + validProductId))
                       // then
                       .andExpect(status().isNoContent());

                verify(productService).delete(validProductId);
            }
        }

        @Nested
        @DisplayName("?????? ???????????? ?????? ?????? ?????? ???????????? ???????????????")
        class Context_with_invalid_product_id {
            private final Long invalidProductId = -1L;

            @BeforeEach
            void mocking() {
                doThrow(new ProductNotFoundException()).when(productService)
                                                       .delete(invalidProductId);
            }

            @Test
            @DisplayName("404 Not Found ?????? ????????? ????????????")
            void It_reponds_status_no_content() throws Exception {
                // when
                mockMvc.perform(delete("/products/" + invalidProductId))
                       // then
                       .andExpect(status().isNotFound())
                       .andExpect(content().string(containsString("Product " +
                                                                          "Not Found")));

                verify(productService).delete(invalidProductId);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /products/{id}")
    class Describe_patchProduct {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Nested
        @DisplayName("?????? ????????? ?????? ???????????? ???????????????")
        class Context_with_valid_product_id {
            private final Long validProductId = 2L;

            @BeforeEach
            void mocking() {
                given(productService.update(eq(validProductId),
                                            any(Product.class)))
                        .willReturn(productHelm);
            }

            @Test
            @DisplayName("200 OK ?????? ????????? ????????? ????????? ????????????")
            void It_returns_200_and_one_product_by_id() throws Exception {
                // when
                var result =
                        mockMvc.perform(
                                patch("/products/" + validProductId)
                                        .content(objectMapper.writeValueAsString(productHelm))
                                        .contentType(MediaType.APPLICATION_JSON));
                // then
                result.andExpect(status().isOk())
                      .andExpect(jsonPath("$.name",
                                          containsString(productHelm.getName())))
                      .andExpect(jsonPath("$.maker",
                                          containsString(productHelm.getMaker())))
                      .andExpect(jsonPath("$.price",
                                          is(productHelm.getPrice()
                                                        .intValue())))
                      .andExpect(jsonPath("$.imageUrl",
                                          containsString(productHelm.getImageUrl())));

                verify(productService).update(eq(validProductId),
                                              any(Product.class));
            }
        }

        @Nested
        @DisplayName("?????? ???????????? ?????? ?????? ?????? ???????????? ???????????????")
        class Context_with_invalid_product_id {
            private final Long invalidProductId = -1L;

            @BeforeEach
            void mocking() {
                given(productService.update(eq(invalidProductId),
                                            any(Product.class)))
                        .willThrow(new ProductNotFoundException());
            }

            @Test
            @DisplayName("404 Not Found ?????? ????????? ????????????")
            void It_reponds_status_not_found() throws Exception {
                // when
                var result =
                        mockMvc.perform(
                                patch("/products/" + invalidProductId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(productHelm)));

                // then
                result.andExpect(status().isNotFound())
                      .andExpect(jsonPath("$.message",
                                          containsString("Product Not Found")));

                verify(productService).update(eq(invalidProductId),
                                              any(Product.class));
            }
        }
    }
}
