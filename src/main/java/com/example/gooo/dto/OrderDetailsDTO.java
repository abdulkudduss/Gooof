package com.example.gooo.dto;
import lombok.Data;

import java.text.DecimalFormat;

@Data
public class OrderDetailsDTO {
    private String date;
    private String userName;
    private String totalPrice;

}