package com.example.resilient_api.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Product {
    private String id;
    private String name;
    private int stock;
}
