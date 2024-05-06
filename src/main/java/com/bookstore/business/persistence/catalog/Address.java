/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookstore.business.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 * 
 * classe embarquée  n'ayant pas d'identité propre<br>
 * Embarquée dans Publisher
 */

@Embeddable
public class Address implements Serializable {
    
    @Column(name = "RUE")
    private String street;

    @Column(name = "CODE_POSTAL")
    private Long zp;

    @Column(name = "VILLE")
    private String city;

    @Column(name = "PAYS")
    private String country;


    public Address(){}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    
    
    public Long getZp() {
        return zp;
    }

    public void setZp(Long zp) {
        this.zp = zp;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}
