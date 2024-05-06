/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookstore.model;

import com.bookstore.business.bll.catalogmngmt.CatalogManagerServiceBean;
import com.bookstore.business.persistence.catalog.Address;
import com.bookstore.business.persistence.catalog.Publisher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author Asbriglio 
 */
@Named
@RequestScoped
public class PublisherBean{
    
    // vous auriez pu aussi utiliser @EJB. Par contre, pour injecter un session bean remote il faut utiliser @EJB
    @Inject
    private CatalogManagerServiceBean catalogManager;//ne tenez pas compte de l'avertissement NetBeans, c'est un bug de l'intellisense NetBeans
    
    private Publisher publisher;
    
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    /**
     * méthode initiant le processus de création d'un éditeur
     * @return chaine de navigation
     */
    public String createPublisher() {

        publisher = catalogManager.createPublisher(publisher);
        return "display";//chaîne définissant explicitement la navigation vers publisherEdit.xhtml
    }
        
    // méthodes de callback pour suivre le cycle de vie du bean initialiser l'objet de type Publisher
    
    /**
     * méthode permettant d'initialiser l'objet de type Publisher
     */
    @PostConstruct
    private void init(){
        publisher = new Publisher();//instanciation d'un objet Publisher
        Address ad =new Address();//instanciation d'un objet Address
        publisher.setAddress(ad);//association de l'adresse à l'éditeur
        System.out.println("initialisation de l'instance du bean");
    }
    @PreDestroy
    private void destroy(){
        System.out.println("destruction de l'instance du bean");
    }
}
