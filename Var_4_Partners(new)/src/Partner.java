package com.example.demoexamnewfour;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partner {
    private int partnerId;
    private String partnerType;
    private String partnerName;
    private String director;
    private String email;
    private String phone;
    private String legalAddress;
    private String inn;
    private int rating;
    
    // Дополнительные поля для расчетов
    private int totalSales;
    private int discount;

    public Partner(int partnerId, String partnerType, String partnerName, String director, String email, String phone, String legalAddress, String inn, int rating) {
        this.partnerId = partnerId;
        this.partnerType = partnerType;
        this.partnerName = partnerName;
        this.director = director;
        this.email = email;
        this.phone = phone;
        this.legalAddress = legalAddress;
        this.inn = inn;
        this.rating = rating;
    }
}


