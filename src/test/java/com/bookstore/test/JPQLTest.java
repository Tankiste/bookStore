/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookstore.test;

import com.bookstore.business.persistence.catalog.*;
import java.util.List;
import jakarta.persistence.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author TANKWA PRINCE JORDAN
 */
public class JPQLTest {
    
    private static EntityManagerFactory emf;
    private EntityManager em;
    
    public JPQLTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("testPU");
    }
    
    @AfterClass
    public static void tearDownClass() {    
        emf.close();
    }
    
    @Before
    public void setUp() {    
        em = emf.createEntityManager();
    }
    
    @After
    public void tearDown() {    
        em.close();
    }

    @Test
    public void checkSimpleRequest(){
        String jpql = "SELECT b FROM Category b WHERE b.parentCategory IS null";
        try{
        Query query = em.createQuery(jpql);
        List result = query.getResultList();
        assertTrue("votre requête ["+jpql+"] renvoie une liste vide",!result.isEmpty());
        }catch(IllegalArgumentException e){
            throw new AssertionError("requête ["+jpql+"] est mal formée");
        }
        
    }
    
    @Test
    public void checkRequestWithStringParameter(){
        String jpql = "SELECT c FROM Book c WHERE c.title LIKE :pattern"; //remplacez par votre requête
        String param="%C#%"; //remplacez par la valeur de type %votreChaine% (votre motif de recherche encadré par %)
        String paramName = "pattern";//remplacez par le nom du paramètre tel qu'il est défini dans votre requête JPQL
        
        try{
        Query query = em.createQuery(jpql);
        query.setParameter(paramName, param);
        List result = query.getResultList();
        assertTrue("votre requête ["+jpql+"] renvoie une liste vide",!result.isEmpty());
        }catch(IllegalArgumentException e){
            throw new AssertionError("requête ["+jpql+"] est mal formée");
        }
    }
    
    @Test
    public void checkRequestWithCategoryParameter(){
        
        String jpql = "SELECT c FROM Category c WHERE c.parentCategory = :parent";//remplacez par votre requête
        try{       
            Long catId = 56L; //"ID DE LA CATEGORIE" - remplacez par un id valide
            Category cat = em.find(Category.class,catId);
        
            String paramName = "parent"; //remplacez par le nom du paramètre tel qu'il est défini dans votre requête JPQL

            Query query = em.createQuery(jpql);
            query.setParameter(paramName, cat);
        
            List result = query.getResultList();
            
            assertTrue("votre requête ["+jpql+"] renvoie une liste vide",!result.isEmpty());
            
        }catch(IllegalArgumentException e){
            throw new AssertionError("requête ["+jpql+"] est mal formée");
        }
    }
    
}
