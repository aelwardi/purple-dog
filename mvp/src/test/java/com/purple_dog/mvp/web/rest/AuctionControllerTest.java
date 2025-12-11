package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.CreateAuctionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private CreateAuctionRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateAuctionRequest();
        validRequest.setProductId(1L);
        validRequest.setDesiredPrice(new BigDecimal("100.00"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testGetAllAuctions() throws Exception {
        mockMvc.perform(get("/api/auctions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testGetAuctionById() throws Exception {
        mockMvc.perform(get("/api/auctions/1"))
                .andExpect(status().isNotFound());
    }
}
