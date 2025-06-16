package com.example.demoexam;

import lombok.*;

import java.sql.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FMJob {
    private int memberJob_id;
    private int memberId;
    private String fio;
    private String position;
    private String org_name;
    private int salary;
    private Date start_date;
    private int total_expense;


    public FMJob(int memjobId, String fio, String position, String orgName, int salary, Date startDate) {
        this.memberJob_id = memjobId;
        this.fio = fio;
        this.position = position;
        this.org_name = orgName;
        this.salary = salary;
        this.start_date = startDate;
    }
}
