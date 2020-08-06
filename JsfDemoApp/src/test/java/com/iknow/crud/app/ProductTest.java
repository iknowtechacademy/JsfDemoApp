package com.iknow.crud.app;

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class ProductTest {
    
    
    private Product product;
    
    @Before
    public final void before() {
        product = new Product();
    }
    
    @Test
    public void comparaProductos(){
        product = new Product(1);
        
        Product productB = new Product(1);
        
        assertEquals(product, productB);
        
    }
    
    @Test
    public void crearProducto(){
        product = new Product(1);
        product.setDescription("Product Test");
        product.setPurchaseCost(new BigDecimal(100.0));
        
        ProductController controller = new ProductController();
        controller.setIsWeb(false);
        
        controller.setProduct(product);
        
        
        controller.create();
        
        assertEquals(product, controller.getProductItems().get(0));
        
    }
    
}
