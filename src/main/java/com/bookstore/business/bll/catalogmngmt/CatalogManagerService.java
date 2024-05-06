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
import jakarta.ejb.Local;


/**
 *
 * @author asbriglio
 */
@Local
public interface CatalogManagerService {

    Book createBook(String title, String summary, Date date, Long categoryId);
    Publisher createPublisher(Publisher publisher);
    Long createCategory(String title, String desc,Long parentId);
    List<Book> retrieveBooksFromCategory(Long catId);
    List<Category> selectCategories(Long parentId);
    Book addPublisherToBook(Long bookId, Long publisherId);
    Book addBookToCategory(Long bookId,Long catId);
    void deleteBook(Long id);
}
