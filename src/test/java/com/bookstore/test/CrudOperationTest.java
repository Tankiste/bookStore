/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookstore.test;

import com.bookstore.business.persistence.catalog.*;
import jakarta.persistence.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Asbriglio
 */
public class CrudOperationTest {
    
    private static EntityManagerFactory emf;
    private EntityManager em;
    
    public CrudOperationTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void ckeckPublisherRetrieving() {
        Long id = 40L;
        try{
        Publisher pub = em.find(Publisher.class,id);
        assertNotNull("l'entité Publisher d'id "+id+ " n'est pas trouvé. Avez-vous modifié cet éditeur dans la base ?",pub);
        
        String errorMessage="l'adresse de l'éditeur d'id 40 n'est pas retrouvé, "
                             + "il y a un problème dans la modélisation JPA Publisher ou de la classe embarquée..."
                                + "ou alors votre jeu de données ne correspond pas au jeu original";
        
        assertEquals(errorMessage,new Long(3000),pub.getAddress().getZp());
        
        }catch(IllegalArgumentException e){
            throw new AssertionError(Publisher.class.getName()+" n'est pas correctement configuré");
        }
    
    }
    
    @Test
    public void checkPubFromBookRetrieving(){
       Long id = 106L;
       try{
        Book b = em.find(Book.class, id);
        Publisher p = b.getPublisher();
        assertEquals("votre relation Bidirectionnelle one to many Publisher-Book n'est pas correcte","Microsoft press",p.getName());
        }catch(IllegalArgumentException e){
            throw new AssertionError(Book.class.getName()+" n'est pas correctement configuré");
        }
    }
    
    @Test
    public void checkNbOfBooksFromCategoryRetrieving(){
       Long catId = 161L;
       try{
       em.getTransaction().begin();
        Category cat = em.find(Category.class,catId);
        int nbOfBooks =  cat.getBooks().size();
       em.getTransaction().commit();
       
       String erroMessage = "Il y a un problème dans votre modélisation many to many "
                + "ou alors vous avez modifié la liste des livre asssociés à la catégorie Décoration";
       assertEquals(erroMessage,3,nbOfBooks);    
       }catch(IllegalArgumentException e){
            throw new AssertionError("Category et/ou Book ne sont pas correctement configurés");
        }
    }
    
    @Test
    public void checkParentCatFromChildRetrieving(){
        Long childCatId = 149L;
        try{
        Category cat = em.find(Category.class,childCatId);
        Category parent = cat.getParentCategory();
        String erroMessage = "Il y a un problème dans votre modélisation du mapping réflexif many to one "
                + "ou alors vous avez modifié la table categories";
        assertEquals(erroMessage,"Informatique",parent.getTitle()); 
        }catch(IllegalArgumentException | NullPointerException e){
            throw new AssertionError(Category.class.getName()+" n'est pas correctement configurée");
        }
    }
}