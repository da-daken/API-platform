package com.daken.apiclientsdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainParams {
    private String train_no;
    private String from_station_telecode;
    private String to_station_telecode;
    private String depart_date;
}
