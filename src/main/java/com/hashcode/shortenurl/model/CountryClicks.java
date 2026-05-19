package com.hashcode.shortenurl.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CountryClicks {
    private String country;
    private long clicks;
}