/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.controllers;

import java.util.List;
import jpa.controllers.exceptions.IllegalOrphanException;
import jpa.controllers.exceptions.NonexistentEntityException;
import jpa.controllers.exceptions.PreexistingEntityException;
import jpa.controllers.exceptions.RollbackFailureException;
import com.iknow.crud.app.Product;

/**
 *
 * @author gustavo
 */
public interface ProductJpaControllerService {
    
    public int getProductCount();
    
    public List<Product> findProductEntities();
    
    public List<Product> findProductEntities(int maxResults, int firstResult);
    
    public void create(Product product) throws PreexistingEntityException, RollbackFailureException, Exception;
    
    public void edit(Product product) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception;
    
    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception;
    
    
    
}
