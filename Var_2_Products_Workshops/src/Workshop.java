package com.example.demoexamnewfour;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workshop {
    private int workshopId;
    private String workshopName;
    private String workshopType;
    private int workersCount;
}

