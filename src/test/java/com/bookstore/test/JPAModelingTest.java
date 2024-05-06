/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookstore.test;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Asbriglio
 */
public class JPAModelingTest {
    
    public JPAModelingTest() {
    }
    

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void checkPublisherV1() {
         checkAnnotatedEntityClass("Publisher", "editeurs");
         checkAnnotatedId("Publisher","ID_EDITEUR");
         checkAnnotedBasicAttribute("Publisher", "name","RAISON_SOCIALE");
     }
     
     @Test
     public void checkPublisherV2(){
         checkEmbeddedAttribute("Publisher", "address");
         checkAnnotedEmbeddableClass("Address");
         checkAnnotedBasicAttribute("Address", "street","RUE");
         checkAnnotedBasicAttribute("Address", "zp","CODE_POSTAL");
         checkAnnotedBasicAttribute("Address", "city","VILLE");
         checkAnnotedBasicAttribute("Address", "country","PAYS");
     }
     
     @Test 
     public void checkCategoryV1(){
         checkAnnotatedEntityClass("Category", "categories");
         checkAnnotatedId("Category","ID_CATEGORIE");
         checkAnnotedBasicAttribute("Category", "title","TITRE");
         checkAnnotedBasicAttribute("Category", "description","DESCRIPTION");
     }
     
     @Test
     public void checkBookV1(){
         checkAnnotatedEntityClass("Book", "livres");
         checkAnnotatedId("Book","ID_LIVRE");
         checkAnnotedBasicAttribute("Book", "title","TITRE");
         checkAnnotedBasicAttribute("Book", "summary","RESUME_LIVRE");
         checkAnnotedBasicAttribute("Book", "date","DATE_PARUTION");
     }
     
     @Test
     public void checkBidirectionalOneToManyBetweenPubBook(){
           checkOneToMany("Publisher", "books", "publisher");
           checkManyToOne("Book", "publisher");
     }
     
     @Test
     public void checkJpaBookPubRelation(){ 
         checkOneToMany("Publisher", "books", "publisher");
         checkManyToOne("Book", "publisher");
         checkJoinColumn("Book", "publisher", "ID_EDITEUR", "ID_EDITEUR");
         checkNumberOfAnnotationOnField("Book", "publisher", 2);
         checkNumberOfAnnotationOnField("Publisher", "books", 1);
     }
     
     @Test
     public void checkJpaCatBookRelation(){
         checkManyToMany("Book", "categories", "books");
         checkManyToMany("Category", "books",null);
         checkJoinTable("Category", "books", "appartient","ID_CATEGORIE", "ID_CATEGORIE", "ID_LIVRE", "ID_LIVRE");
     }
     
     @Test
     public void checkCategoryReflexiveRelation(){
         checkManyToOne("Category", "parentCategory");
         checkJoinColumn("Category", "parentCategory", "CAT_ID_CATEGORIE", "ID_CATEGORIE");
     }
          
     private void checkAnnotatedEntityClass(String baseName, String tableName){
         tableName = tableName.toLowerCase();
         try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+baseName);
            Annotation[] anns = clazz.getAnnotations();
            Annotation namedQueriesAnn = clazz.getAnnotation(NamedQueries.class);
            Annotation namedQueryAnn = clazz.getAnnotation(NamedQuery.class);
            
            int length;
            if(namedQueriesAnn != null ^ namedQueryAnn != null ){
                length = 3;
                
            }else if(namedQueriesAnn != null && namedQueryAnn != null){
                throw new AssertionError("Il faut choisir de spécifier @NamedQuery ou @NamedQueries sur la classe "+baseName);
            }else{
                length = 2;
            }
                        
            if(anns.length != length){
                throw new AssertionError("le nombre d'annotations sur la classe n'est pas bon (il en manque sûrement)");
            }
            for(Annotation ann : anns){

                Class<?> annType = ann.annotationType();
                assertTrue(annType.equals(Table.class)|| annType.equals(Entity.class)
                        ||annType.equals(NamedQueries.class)||annType.equals(NamedQuery.class));
                if(annType.equals(Table.class)){
                  Table t = (Table) ann ;
                    assertEquals("@Table.name n'a pas la bonne valeur assignée",tableName, t.name().toLowerCase());
                }
           
           }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        }
     }
     
     private void checkAnnotatedId(String baseName, String columnName){
         columnName = columnName.toUpperCase();
        try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+baseName);
            Field idField = clazz.getDeclaredField("id");
            Annotation[] anns = idField.getAnnotations();
            if(anns.length < 3){
                throw new AssertionError("il manque des annotations sur l'attribut id de "+baseName);
            }
            for(Annotation ann : anns){
                Class<?> annType = ann.annotationType();
                assertTrue(annType.equals(Id.class)||annType.equals(Column.class)||annType.equals(GeneratedValue.class));
                if(annType.equals(GeneratedValue.class)){
                    GeneratedValue gv =(GeneratedValue) ann;
                    assertEquals(GenerationType.IDENTITY,gv.strategy());
                }else if (annType.equals(Column.class)){
                    Column c = (Column) ann;
                    assertEquals(columnName, c.name().toUpperCase());
                    
                }
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "variable d'instance non trouvée", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
     private void checkAnnotedBasicAttribute(String baseName, String attributeName,String columnName){
        columnName  = columnName.toUpperCase();
        try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+baseName);
            Field attr = clazz.getDeclaredField(attributeName);
            Annotation[] anns = attr.getAnnotations();
            
            if(!attr.getName().equals("id")){
                for(Annotation a : anns){
                    Class<?> annType = a.annotationType();
                    assertTrue(annType.equals(Column.class)|| annType.equals(Basic.class)||annType.equals(Temporal.class));
                     
                }
                Column c = attr.getAnnotation(Column.class);
                if(!attributeName.equalsIgnoreCase(columnName)){    
                    assertNotNull("il manque l'annotation pour mapper l'attribut de la classe avec la colonne de la table", c);
                    assertEquals("le membre @Column.name est invalide (mauvaise valeur assignée)", columnName,c.name().toUpperCase());
                }else{
                    if(c!=null){
                         assertEquals("le membre @Column.name est invalide (mauvaise valeur assignée)", columnName,c.name().toUpperCase());
                        
                    }
                }
                
                Temporal temp = attr.getAnnotation(Temporal.class);
                if(attr.getType().equals(java.util.Date.class)){
                   assertNotNull("Il faut spécifier à JPA le mapping temporel", temp); 
                   assertEquals("la valeur de @Temporal n'est pas bonne",jakarta.persistence.TemporalType.DATE,temp.value());
                   
                }else {
                    String errorMessage = "vous ne devez pas appliquer @Temporal sur le champ "+attr.getName();
                    assertNull(errorMessage,temp);
                }
                    
            }     
           
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE,"classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "le champ n'est pas trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
          
     private void checkAnnotedEmbeddableClass(String baseName){
        try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+baseName);
           Annotation emb =  clazz.getAnnotation(Embeddable.class);
            assertNotNull("la classe embarquée (embeddable) n'est pas correctement annotée", emb);
            if(clazz.getAnnotations().length!=1){
                throw new AssertionError("il devrait y avoir une seule annotation sur la classe");
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        }
     }
     
     private void checkEmbeddedAttribute(String baseName, String attributeName){
        try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+baseName);
            Field attr = clazz.getDeclaredField(attributeName);
            Annotation embbededAnn = attr.getAnnotation(Embedded.class);
           
            assertNotNull("le champs référençant un objet embarqué n'est pas correctement annoté", embbededAnn);
            if(attr.getAnnotations().length!=1){
                throw new AssertionError("il devrait y avoir une seule annotation sur le champ "+attributeName);
            }   
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "champ de la classe non trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
        
     private void checkOneToMany(String inverseName,String attributeName,String mappedByValue) {
        try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+inverseName);
            Field attr = clazz.getDeclaredField(attributeName);
            OneToMany onemany = attr.getAnnotation(OneToMany.class);
            assertNotNull("la relation 1 Publisher->plusieurs Books n'est pas configurée ou est mal configurée", onemany);
           assertTrue("vous avez oublié l'attribut mappedBy",!onemany.mappedBy().isEmpty());
           assertEquals("la valeur de mappedBy est incorrecte", mappedByValue, onemany.mappedBy());
           
           assertEquals("le mode fetch doit LAZY (mode par défaut)",FetchType.LAZY,onemany.fetch());
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "champ non trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
     private void checkManyToOne(String ownerName,String attributeName){
        try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+ownerName);
            Field attr = clazz.getDeclaredField(attributeName);
            ManyToOne manyone= attr.getAnnotation(ManyToOne.class);
            assertNotNull("mauvaise annotation ou annotation manquante pour configurer une relation plusieurs->un",manyone);
            if(ownerName.equals("Book")) {
                    CascadeType[] ctypes =  manyone.cascade();
                    List<CascadeType> types = Arrays.asList(ctypes);
                    assertTrue(types.contains(CascadeType.MERGE));        
            }
            assertTrue("la relation doit être optionnelle",manyone.optional()== true);
            assertEquals("le mode fetch doit EAGER (mode par défaut)",FetchType.EAGER,manyone.fetch());
        
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "champ non trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
     private void checkJoinColumn(String ownerName, String attributeName, String joinColName, String refColName){
         joinColName = joinColName.toUpperCase();
         refColName = refColName.toUpperCase();
         try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+ownerName);
            Field attr = clazz.getDeclaredField(attributeName);
            JoinColumn jcol = attr.getAnnotation(JoinColumn.class);
            assertNotNull("le mapping 1 Publisher->plusieurs Book avec la colonne de jointure n'est pas ou est mal configuré", jcol);
            assertEquals("le nom de la colonne de jointure dans @JoinColumn est incorrect",joinColName, jcol.name().toUpperCase());
            if(!jcol.referencedColumnName().isEmpty()){
               assertEquals("le nom de la colonne clé primaire référencée par @JoinColumn est incorrect",refColName, jcol.referencedColumnName().toUpperCase());
           }

            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "champ non trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
     private void checkNumberOfAnnotationOnField(String baseName, String attributeName, int nbOfAnns){
         try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+baseName);
            Field attr = clazz.getDeclaredField(attributeName);
            Annotation[] anns = attr.getAnnotations();
           assertEquals("le nombre d'annotations sur "+attributeName+" n'est pas correct ",nbOfAnns,anns.length);
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "champ non trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
    
     private void checkManyToMany(String baseName,String attributeName,String mappedByValue){
         try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+baseName);
            Field attr = clazz.getDeclaredField(attributeName);
            ManyToMany manymany= attr.getAnnotation(ManyToMany.class);
            assertNotNull("mauvaise annotation ou annotation manquante pour configurer une relation plusieurs->plusieurs",manymany);
             switch (baseName) {
                 case "Book":
                     assertTrue("vous avez oublié le membre mappedBy",!manymany.mappedBy().isEmpty());
                     assertEquals("la valeur de mappedBy est incorrecte",mappedByValue,manymany.mappedBy());
                     break;
                 case "Category":
                     assertTrue("le membre mappedBy doit être placé DANS l'entité non propriétaire de la relation",manymany.mappedBy().isEmpty());
                     break;
             }
             
             assertEquals("le mode fetch doit LAZY (mode par défaut)",FetchType.LAZY,manymany.fetch());
        
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "champ non trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
     private void checkJoinTable(String ownerName,String attributeName,String tableName, String Jname, String refJname, String invJname, String refInvJname){
         tableName = tableName.toLowerCase();
         Jname = Jname.toUpperCase();
         refJname = refJname.toUpperCase();
         invJname = invJname.toUpperCase();
         refInvJname = refInvJname.toUpperCase();
         try {
            Class  clazz = Class.forName("com.bookstore.business.persistence.catalog."+ownerName);
            Field attr = clazz.getDeclaredField(attributeName);
            JoinTable jtab = attr.getAnnotation(JoinTable.class);
            assertNotNull("le mapping plusieurs Category -> plusieurs Book avec la table de jointure "+tableName+" n'est pas ou est mal configuré", jtab);
            assertTrue("il manque le nom de la table de jointure",!jtab.name().isEmpty());
            assertEquals("le nom de la table de jointure est incorrect",tableName, jtab.name().toLowerCase());
            
            JoinColumn[] joins = jtab.joinColumns();
            assertEquals("joinColumns doit contenir une seule colonne de jointure(@JoinColumn)",1,joins.length);
            JoinColumn jcol = joins[0];
            assertEquals("le nom de la colonne de jointure dans @JoinColumn est incorrect)",Jname, jcol.name().toUpperCase());
            if(!jcol.referencedColumnName().isEmpty()){
               assertEquals("le nom de la colonne clé primaire référencée par @JoinColumn est incorrect)",refJname, jcol.referencedColumnName().toUpperCase());
           }
            
           JoinColumn[] invJoins = jtab.inverseJoinColumns();
           assertEquals("inverseJoinColumns doit contenir une seule colonne de jointure(@JoinColumn)",1,invJoins.length);
           JoinColumn invJcol = invJoins[0];
           assertEquals("le nom de la colonne de jointure dans @JoinColumn est incorrect",invJname, invJcol.name().toUpperCase());
            if(!invJcol.referencedColumnName().isEmpty()){
               assertEquals("le nom de la colonne clé primaire référencée par @JoinColumn est incorrect",refInvJname, invJcol.referencedColumnName().toUpperCase());
           }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "classe non trouvée", ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, "champ non trouvé", ex);
        } catch (SecurityException ex) {
            Logger.getLogger(JPAModelingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
             
    
}
