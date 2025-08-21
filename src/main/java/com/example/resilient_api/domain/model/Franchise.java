package com.example.resilient_api.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class Franchise {
    private String id;
    private String nombre;
    private List<String> sucursales;
}
