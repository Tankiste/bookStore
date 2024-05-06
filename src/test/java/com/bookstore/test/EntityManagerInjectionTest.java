/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookstore.test;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Asbriglio
 */
public class EntityManagerInjectionTest {
    
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
     public void checkEntityManagerInjection() {
         checkInjection("CategoryManagerServiceBean");
         checkInjection("BookManagerServiceBean");
         checkInjection("PublisherManagerServiceBean");
         
     }
     
     private void checkInjection(String baseName){
        try {
            Class  clazz = Class.forName("com.bookstore.business.bll.catalogmngmt."+baseName);
            Field emField = clazz.getDeclaredField("em");
            if(emField.getAnnotations().length !=1){
                throw new AssertionError("il doit y avoir une et une seule annotation pour configurer l'injection");
            }
            PersistenceContext p = emField.getAnnotation(PersistenceContext.class);
            assertNotNull("ce n'est pas la bonne annotation pour injecter un EM",p);
            if(p.unitName().isEmpty()){
                throw new AssertionError("spécifiez l'unité de persistance");
            }else{
                assertEquals("mauvais nom d'unité de persistance", "bsPU", p.unitName());
            }
            
            if(p.synchronization().equals(SynchronizationType.UNSYNCHRONIZED)){
                throw new AssertionError("Utilisez un contexte de persistance SYNCHRONIZED");
            }
            
            if(p.type().equals(PersistenceContextType.EXTENDED)){
                throw new AssertionError("un stateless bean ne peut pas utiliser un contexte de persistance étendu");
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EntityManagerInjectionTest.class.getName()).log(Level.SEVERE, "classe du service non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(EntityManagerInjectionTest.class.getName()).log(Level.SEVERE, "le champ n'existe pas", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(EntityManagerInjectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
}
