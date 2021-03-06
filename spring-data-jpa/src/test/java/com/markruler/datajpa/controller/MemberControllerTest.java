package com.markruler.datajpa.controller;

import com.markruler.datajpa.entity.Member;
import com.markruler.datajpa.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MemberController.class)
@DisplayName("MemberController")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("GET /members/{id}")
    class Describe_domain_converter_class {

        final String username = "userA";
        final Member userA = new Member(username, 10);

        @Nested
        @DisplayName("PathVariable??? Long??? ?????????")
        class Context_with_long_id {

            @BeforeEach
            void setUp() {
                given(memberRepository.findById(anyLong()))
                        .willReturn(Optional.of(userA));
            }

            @Test
            @DisplayName("Repository??? ?????? ????????????")
            void direct_call_repository() throws Exception {
                mockMvc.perform(
                                get("/members/{id}", 1)
                                        .accept(MediaType.APPLICATION_JSON_UTF8)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().string(username));

                verify(memberRepository).findById(anyLong());
            }
        }

        @Disabled("TODO: ?????? ????????? ???????????? username??? ??????????????? ??????????????? id??? ?????????")
        @Nested
        @DisplayName("PathVariable??? Entity??? ?????????")
        class Context_with_entity {

            @BeforeEach
            void setUp() {
                given(memberRepository.findById(anyLong()))
                        .willReturn(Optional.of(userA));
            }

            @Test
            @DisplayName("Domain Converter Class ????????? ????????????")
            void domain_converter_class() throws Exception {
                mockMvc.perform(
                                get("/members2/{id}", 1)
                                        .accept(MediaType.APPLICATION_JSON_UTF8)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().string(username));

                verify(memberRepository).findById(anyLong());
            }
        }
    }

    @Nested
    @DisplayName("GET /members")
    class Describe_pageable_request {
        final Pageable pageable = PageRequest.of(1, 3)
                .withSort(Sort.by("age").ascending());

        @BeforeEach
        void setUp() {
            given(memberRepository.findAll(any(PageRequest.class)))
                    .willReturn(
                            new PageImpl<>(
                                    Arrays.asList(
                                            new Member("user1", 1),
                                            new Member("user2", 2),
                                            new Member("user3", 3),
                                            new Member("user4", 4),
                                            new Member("user5", 5),
                                            new Member("user6", 6),
                                            new Member("user7", 7),
                                            new Member("user8", 8),
                                            new Member("user9", 9),
                                            new Member("user10", 10),
                                            new Member("user11", 11)),
                                    pageable,
                                    11));
        }

        @Test
        @DisplayName("???????????? ????????? ???????????? ?????? DTO??? ????????????")
        void pageable_request() throws Exception {

            mockMvc.perform(
                            get("/members/dto")
                                    .param("page", "1")
                                    .param("size", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(11)))
                    .andExpect(jsonPath("$.number").value(1))
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$.totalElements").value(11))
                    .andExpect(jsonPath("$.totalPages").value(4));

            verify(memberRepository).findAll(any(PageRequest.class));
        }

        @Test
        @DisplayName("@Qualifier??? ???????????? ??????????????? ?????? ???????????? ?????????")
        void qualifier_pageable_request() throws Exception {

            mockMvc.perform(
                            get("/members")
                                    .param("member_page", "1")
                                    .param("member_size", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(11)))
                    .andExpect(jsonPath("$.number").value(1))
                    .andExpect(jsonPath("$.size").value(3))
                    .andExpect(jsonPath("$.totalElements").value(11))
                    .andExpect(jsonPath("$.totalPages").value(4));

            verify(memberRepository).findAll(any(PageRequest.class));
        }
    }
}
