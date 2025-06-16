package com.example.demoexam;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partner {
    private int partnerId;
    private String partner_type;
    private String partner_name;
    private String director;
    private String email;
    private String phone;
    private String legal_address;
    private String inn;
    private int rating;
    private int totalSales;
    private int discount;

    public Partner(int partnerId, String partnerType, String partnerName, String director, String email, String phone, String legalAddress, String inn, int rating) {
        this.partnerId = partnerId;
        this.partner_type = partnerType;
        this.partner_name = partnerName;
        this.director = director;
        this.email = email;
        this.phone = phone;
        this.legal_address = legalAddress;
        this.inn = inn;
        this.rating = rating;
    }
}

