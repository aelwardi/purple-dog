package com.purple_dog.mvp.web.rest;

import com.purple_dog.mvp.dto.CreateAuctionRequest;
import com.purple_dog.mvp.services.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuctionService auctionService;

    private CreateAuctionRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateAuctionRequest();
        validRequest.setProductId(1L);
        validRequest.setDesiredPrice(new BigDecimal("100.00"));
    }

    @Test
    void testGetAllAuctions() throws Exception {
        mockMvc.perform(get("/api/auctions"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAuctionById() throws Exception {
        mockMvc.perform(get("/api/auctions/1"))
                .andExpect(status().isNotFound());
    }
}
