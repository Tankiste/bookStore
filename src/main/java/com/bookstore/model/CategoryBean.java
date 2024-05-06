/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bookstore.model;

import com.bookstore.business.bll.catalogmngmt.CatalogManagerService;
import com.bookstore.business.bll.catalogmngmt.CategoryManagerServiceBean;
import com.bookstore.business.persistence.catalog.Category;
import java.util.List;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 *
 * @author asbriglio
 */
@Named
@RequestScoped
public class CategoryBean {
    
    //injection du service gérant les catégories
    @Inject
    private CategoryManagerServiceBean categoryManager;
    
    //injection de la façade de gestion du catalogue
    @Inject
    private CatalogManagerService catalogManager;
    
    private String catTitle;
    private String desc;
    private Long parentId;
    private Category cat;
    private List<Category> categories;
    
    /**
     * méthode initiant la création d'une catégorie
     */
    public void createCategory(){
        Long catId = catalogManager.createCategory(catTitle, desc, parentId);
        cat=categoryManager.findCategoryById(catId);
    }
    /**
     * trouver des catégories racines ou enfants d'une catégorie racine
     */
    public void findCategories(){
        categories = catalogManager.selectCategories(parentId);
    }

    public String getCatTitle() {
        return catTitle;
    }

    public void setCatTitle(String catTitle) {
        this.catTitle = catTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    
    public Category getCat() {
        return cat;
    }

    public List<Category> getCategories() {
        return categories;
    }
           
}
