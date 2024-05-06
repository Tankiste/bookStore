/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookstore.business.bll.catalogmngmt;




import com.bookstore.business.persistence.catalog.Book;
import com.bookstore.business.persistence.catalog.Category;
import com.bookstore.business.persistence.catalog.Publisher;
import java.util.Date;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;

/**
 *
 * service facade (application service).<br>
 * Fait le pont entre la présentation et la logique métier<br>
 * Composant coarse grained (forte granularité) chargé de la réalisation des processus métiers<br>
 * Expose une vue locale au travers d'une interface métier annotée @Local
 */
@Stateless(name="CatalogManager")
//options par défaut - on peut se passer de cette anotation - convention over configuration
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)//on démarrage une nouvelle transaction si le client n'en n'a pas démarrée.
@LocalBean //obligatoire si on veut que le SB expose d'autres vues (locales ou "remote")
public class CatalogManagerServiceBean implements CatalogManagerService {
     //on aurait pu utiliser @Inject vu qu'on injecte des composants co-localisés dans la même instance de JVM (car dans la même archive)
    @EJB
    private PublisherManagerServiceBean publisherManager;
    @EJB
    private BookManagerServiceBean bookManager;
    @EJB
    private CategoryManagerServiceBean categoryManager;


    /**
     *
     * Les paramètres permettent de créer un livre associé à une catégorie existant dans la base
     * Le livre créé est enregistré dans la base<br>
     * Cette méthode utilise CategoryManager pour retrouver une catégorie<br>
     * Cette méthode utilise BookManager pour persister le livre nouvellement créé
     * @param title titre du livre
     * @param summary résumé du livre
     * @param date date de publication
     * @param categoryId permet de retrouver une catégorie persistée dans la BDD
     * @return livre nouvellement créé ou null si aucune catégorie n'est associable
     */
    @Override
    public Book createBook(String title, String summary, Date date, Long categoryId) {

        Book b = new Book();
        b.setTitle(title);
        b.setDate(date);//date de création pour simplifier
        b.setSummary(summary);

        //on recupère la catégorie
        //cat est toujours attachée car la transaction n'est pas encore validée.
        //le contexte de persistance est actif dans que la transaction englobante est active
        Category cat = categoryManager.findCategoryById(categoryId);
        if(cat==null){//si la catégorie n'a pas été trouvée
            return null; // la méthode retourne null (le livre n'a pas été inséré en base)
        }
       
        //rappel : Category est la classe propriétaire de la relation.
        //donc si on veut persister la relation Category/Book on doit exécuter l'instruction ci-dessous
        cat.addBook(b);//relation persistée au commit de la transaction

        //optionnel :
        //pour ajouter la relation book->category. utile si on veut manipuler les catgories de Book b
        //sans avoir à recharger b depuis la base.
        b.addCategory(cat);
        
        b= bookManager.saveBook(b); 
        
        return b;
    }//lorsque la transaction sera terminée, l'entité Book sera persistée en base. l'Id (clé) générée lors de l'insertion sera assigné à l'objet référencé par b.
    
    /**
     * 
     * @param catId id de la catégorie pour laquelle on veut afficher les livre
     * @return la liste des livres associées à la catégorie ou null si l'id passé ne correspond à aucune catégorie
     */
    @Override
    public List<Book> retrieveBooksFromCategory(Long catId) {
        //récupération de la catégorie. la catégorie est toujours attaché au contexte de persistance et donc managée
        Category cat = categoryManager.findCategoryById(catId);
        //obtention d'une référence à la liste des livres associés.
        if(cat==null){//si aucune catégorie ne correspond à l'id
            return null;
        }
        List<Book> books = cat.getBooks();
        //la liste ayant un chargement différé (LAZY), il faut invoquer une opération (ici size()) dessus pour que les éléments soient chargés 
        books.size();
        return books;
    }
    
    /**
     *
     * Retourner une liste de catégories<br>
     * Cette méthode utilise le session bean CategoryManager
     * @param parentId id de la catégorie racine pour laquelle on veut retrouver les enfants.
     * @return une liste de catégories filles ayant pour parent la catégorie d'identité parentId<br>
     * Si parentId est null la méthode retourne les catégories racines n'ayant pas de parent.
     * Si parentId ne correspond à aucune catégorie alors null est retourné.
     */
    @Override
    public List<Category> selectCategories(Long parentId) {
      List<Category> cats;
        if(parentId==null) {
            cats = categoryManager.getRootCategories();
        }else{
          cats =  categoryManager.getchildrenCategories(parentId);
        }

      return cats;
    }

    /**
     * Créer un nouvel éditeur<br>
     * Délègue le traitement à PublisherManager
     * @param publisher éditeur à insérer en base.<br>
     * @return éditeur créé ou null si l'éditeur passé en argument est null
     */
    @Override
    public Publisher createPublisher(Publisher publisher) {
        if(publisher!=null){
         return publisherManager.savePublisher(publisher);
        }else return null;
      
    }

    /**
     * associer un livre à un éditeur<br>
     * Cette méthode utilise BookManager et PublisherManager
     * @param bookId identité du livre auquel on veut associer un éditeur
     * @param publisherId id de l'éditeur du livre
     * @return le livre auquel on a associé un éditeur ou null si l'id du livre ou de l'éditeur ne correspond à aucune entité sauvegardée
     */
    @Override
    public Book addPublisherToBook(Long bookId, Long publisherId) {
    //les 2 entitiés sont managées au sein du PC (persistence context)
    Book  b =  bookManager.findBookById(bookId);
    Publisher p = publisherManager.findPublisherById(publisherId);
    //on retourne null si aucun livre ou éditeur ne correspond aux id passés en argument
    //on aurait pu lever aussi une exception
    if(b==null || p ==null) return null;
    p.getBooks().add(b);//instruction optionnelle
    b.setPublisher(p);//la modification de b va être automatiquement synchronisée avec la base
    return b;
    }//l'association est persistée lorsque la transaction est validée (commit) après que la méthode a fini de s'exécuter.

    /**
     * Créer et sauvegarder une nouvelle catégorie fille  ou une nouvelle catégorie racine
     * @param title titre de la catégorie
     * @param desc description de la catégorie
     * @param parentId categorie racine de la catégorie créée. Peut être null. Dans ce cas on crée et sauvegarde donc une catégorie dite racine.
     * Si parentId ne correspond à aucune catégorie en base, alors la catégorie créée est racine.<br>
     * @return l'id de la catégorie sauvegardée en base
     */
    @Override
    public Long createCategory(String title, String desc, Long parentId) {
        Category cat =new Category();
        cat.setTitle(title);
        cat.setDescription(desc);
        if(parentId!=null){
            //remarque à ce niveau, parent reste managée car le PC est actif du fait que la transaction est toujours active
            Category parent = categoryManager.findCategoryById(parentId);
            cat.setParentCategory(parent);
       
        }//fin if
       Long catId = categoryManager.saveCategory(cat);
      
       return catId;
    }

    /**
     *
     * Associer un livre existant à une catégorie existant<br>
     * Une exception Système (EJBException) est levée si l'association existe déjà dans la table appartient.
     * A noter que ce type d'exception entraine le rollback de la transaction et la destruction de l'instance du session bean au sein
     * de laquelle l'exception a été levée.
     * Utilise BookManager et CategoryManager
     * @param bookId id du livre à associer à une catégorie
     * @param catId id de la catégorie à laquelle le livre est associé
     * @return le livre auquel une catégorie a été associée. null est retourné si les id ne correspondent pas à des entités en base.
     */
    @Override
    public Book addBookToCategory(Long bookId, Long catId) {
      Book b;
      b = bookManager.findBookById(bookId);
        System.out.println("Book found by ID");

     Category c = categoryManager.findCategoryById(catId);
        System.out.println("Category found by ID");
    //si les identités passées en argument ne correspondent pas à un livre et/ou une catégorie existant dans la base
     if(b==null || c ==null) return null;//on return null.
    
     c.addBook(b);//Category est propriétaire de la relation
     //l'association ci-dessous n'est pas nécessaire pour persister la relation.
     //b est managé. Donc lorsqu'on invoque une opération sur la liste des catégories (ici addCategory),
     //la liste des catégories associées est chargée (de manière différée - lazy loading) par l'EntityManager
     b.addCategory(c);
    
    //remarque : b et c sont managées . donc la création d'une relation entre eux (modification)
    //sera repercutée en base quand le CP se synchronisera avec la base sous-jacente.
    //cette synchro aura lieu dés que la tsx est validée aprés qu'addBookToCategory() a fini de s'exécuter
     return b;
    }

    /**
     *
     * Supprimer un livre<br>
     * Avant de supprimer le livre de la base,il faut penser à suprimer au préalable
     * le lien entre le livre et ses catégories associées.<br>
     * La méthode utilise BookManager
     * @param id identité du livre à supprimer
     */
    @Override
    public void deleteBook(Long id) {
        Book book = bookManager.findBookById(id);
        if(book!=null){//si une entité a été retrouvé
            List<Category>categories=book.getCategories();
            //pour chaque catégorie associé au livre à supprimer
            for(Category c : categories){
                //on supprime la relation unissant la catégorie avec le livre
               c.getBooks().remove(book);
            }
             //enfin on supprime le livre
            bookManager.deleteBook(book);
        }//fin if
    } //transaction "commitée". le persistence context est détruit. c'est ici que l'objet est supprimé de la base

}//fin classe
