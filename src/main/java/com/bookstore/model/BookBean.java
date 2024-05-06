/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bookstore.model;

import com.bookstore.business.bll.catalogmngmt.BookManagerServiceBean;
import com.bookstore.business.bll.catalogmngmt.CatalogManagerService;
import com.bookstore.business.bll.catalogmngmt.CatalogManagerServiceBean;
import com.bookstore.business.persistence.catalog.Book;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;

/**
 *
 * @author TANKWA PRINCE JORDAN
 */
@Named
@SessionScoped
public class BookBean implements Serializable{
    
    @Inject
    private CatalogManagerServiceBean catalogManager;
    
    @Inject
    private BookManagerServiceBean bookManager;

    //permet d'activer le mode édition pour un livre - permet de spécifier qu'elle balise JSF est "affichée" dans la vue
    private Boolean updatable = false;

    private Long bookId;
    private Long pubId;
    //variable utilisées pour la création d'un livre
    private Long catId;
    private String bookTitle;
    private Date publicationDate;
    private String summary;
    
    //variable utilisée pour recherchée un livre
    private String pattern;
    
    private Book book;
    
    List<Book> books;
    
    /**
     * Méthode chargée d'initier le processus de d'assignation d'un éditeur à un livre
     * @return chaine de navigation 
     */
    public String linkBookToPublisher(){
        book = catalogManager.addPublisherToBook(bookId, pubId);
        if(book==null){//si le livre n'a pas pu être associé à un éditeur car les id ne correpondent pas à un livre ou éditeur inséré en base
            //ajout d'un message d'erreur
            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Id invalide.","l'id du livre ou de l'éditeur sont erronées"));
            return null;    
        }
        bookId=null;
        pubId=null;
        return "display";
    }
    /**
     * Méthode chargée d'initier l'association d'un livre et d'une catégorie existant (stockés en base)
     * @return null donc l'action n'entrainera pas une navigation vers une nouvelle vue.
     */
    public String linkBookToCategory(){
        book = catalogManager.addBookToCategory(bookId, catId);
        if(book==null){//si le livre n'a pas pu être associé à un éditeur car les id ne correpondent pas à un livre ou éditeur inséré en base
            //ajout d'un message d'erreur
            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Id invalide.","l'id du livre ou de la catégorie sont erronées"));
            return null;
        }
        return "display";
    }
    
    /**
     * Initie la création d'un livre
     * @return chaine de navigation
     */
    public String createBook(){
        book = catalogManager.createBook(bookTitle,summary,publicationDate, catId);
        if(book==null){//si le livre n'a pas pu être associé à un éditeur car les id ne correpondent pas à un livre ou éditeur inséré en base
            //ajout d'un message d'erreur
            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Id catégorie invalide.","aucune catégorie ne correspond à l'id saisi"));
            return null;
        }
        
        //réinitialisation des variables
        bookId=null;
        catId=null;
        bookTitle=null;
        summary=null;
        publicationDate = null;
        
        return "display";
    }
    /**
     * Supprimer un livre
     */
    public void deleteBook(){
       catalogManager.deleteBook(bookId);
       FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("livre n°"+bookId+" supprimé"));//message d'information       
    }
    /**
     * rechercher une liste de livre en fonction d'un motif dans le titre
     */
    public void findMatchingPatternBooks(){
       books = null;
       books= bookManager.findByCriteria(pattern);   
    }
    /**
     * rechercher des livres appartenant à une catégorie
     */
    public void findBooksByCategory(){
        books=null;//réinitialisation de la liste
        books = catalogManager.retrieveBooksFromCategory(catId);
        if(books == null){//cas où l'id ne correspond à aucune catégorie
             FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Id catégorie invalide.","aucune catégorie ne correspond"));
        }
    }
    /**
     * méthode permettant d'activer le mode édition pour modifier un livre
     */
    public void activeUpdate(){
        updatable=true;
    }
    /**
     * méthode initiant le processus de modification d'un livre
     */
    public void update(){
        book = bookManager.updateBook(book);
        updatable=false;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getPubId() {
        return pubId;
    }

    public void setPubId(Long pubId) {
        this.pubId = pubId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
        
    //propriétés en lecture seule
    public Book getBook() {
        return book;
    }

    public List<Book> getBooks() {
        return books;
    }
    
    

    public Boolean getUpdatable() {
        return updatable;
    }
    
    
    
    
    
}
