package org.example.objects;

import lombok.Data;

@Data
public class Address {
    private int id;
    private Boolean isprimary;
    private String line1;
    private String line2;
    private String city;
    private String stateprovince;
    private String postalcode;
    private String countryid;
}
