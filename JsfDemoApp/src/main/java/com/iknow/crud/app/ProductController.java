package com.iknow.crud.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import jsf.util.JsfUtil;
import jsf.util.PagingInfo;

public class ProductController {

    private Product product;
    private List<Product> productItems;

    private ProductConverter converter;
    private PagingInfo pagingInfo;
    
    private boolean isWeb;

    

    public ProductController() {
        
        isWeb = true;
        pagingInfo = new PagingInfo();
        converter = new ProductConverter();
    }

    public PagingInfo getPagingInfo() {

        if (productItems == null) {
            productItems = new ArrayList<Product>();
        }
        pagingInfo.setItemCount(productItems.size());
        return pagingInfo;
    }

    public Product getProduct() {
        if (product == null) {
            product = new Product();
        }
        return product;
    }
    
    public void setProduct(Product p){
        product = p;
    }

    public String listSetup() {
        reset(true);
        return "product_list";
    }

    public String createSetup() {
        reset(false);
        product = new Product();
        return "product_create";
    }

    public String create() {
        try {

            System.out.println("Trying to save a Product");

            if (productItems == null) {
                productItems = new ArrayList<Product>();
            }

            productItems.add(product);
            System.out.println("productItems " + productItems);
            
            if(isWeb){
                JsfUtil.addSuccessMessage("Product was successfully created.");
            }

            
        } catch (Exception e) {
            if(isWeb){
                JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            }

            return null;
        }
        return listSetup();
    }

    public String detailSetup() {
        return scalarSetup("product_detail");
    }

    public String editSetup() {
        return scalarSetup("product_edit");
    }

    private String scalarSetup(String destination) {
        reset(false);
        product = (Product) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentProduct", converter, null);
        if (product == null) {
            String requestProductString = JsfUtil.getRequestParameter("jsfcrud.currentProduct");
            JsfUtil.addErrorMessage("The product with id " + requestProductString + " no longer exists.");
            return relatedOrListOutcome();
        }
        return destination;
    }

    public String edit() {
        String productString = product.toString();
        String currentProductString = JsfUtil.getRequestParameter("jsfcrud.currentProduct");
        if (productString == null || productString.length() == 0 || !productString.equals(currentProductString)) {
            String outcome = editSetup();
            if ("product_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit product. Try again.");
            }
            return outcome;
        }
        try {
            //jpaController.edit(product);
            JsfUtil.addSuccessMessage("Product was successfully updated.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return detailSetup();
    }

    public String destroy() {
        String idAsString = JsfUtil.getRequestParameter("jsfcrud.currentProduct");
        Integer id = new Integer(idAsString);
        try {
            //jpaController.destroy(id);
            JsfUtil.addSuccessMessage("Product was successfully deleted.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
            return null;
        }
        return relatedOrListOutcome();
    }

    private String relatedOrListOutcome() {
        String relatedControllerOutcome = relatedControllerOutcome();
        if (relatedControllerOutcome != null) {
            return relatedControllerOutcome;
        }
        return listSetup();
    }

    public List<Product> getProductItems() {
        return productItems;
    }

    public String next() {
        reset(false);
        getPagingInfo().nextPage();
        return "product_list";
    }

    public String prev() {
        reset(false);
        getPagingInfo().previousPage();
        return "product_list";
    }

    private String relatedControllerOutcome() {
        String relatedControllerString = JsfUtil.getRequestParameter("jsfcrud.relatedController");
        String relatedControllerTypeString = JsfUtil.getRequestParameter("jsfcrud.relatedControllerType");
        if (relatedControllerString != null && relatedControllerTypeString != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            Object relatedController = context.getApplication().getELResolver().getValue(context.getELContext(), null, relatedControllerString);
            try {
                Class<?> relatedControllerType = Class.forName(relatedControllerTypeString);
                Method detailSetupMethod = relatedControllerType.getMethod("detailSetup");
                return (String) detailSetupMethod.invoke(relatedController);
            } catch (ClassNotFoundException e) {
                throw new FacesException(e);
            } catch (NoSuchMethodException e) {
                throw new FacesException(e);
            } catch (IllegalAccessException e) {
                throw new FacesException(e);
            } catch (InvocationTargetException e) {
                throw new FacesException(e);
            }
        }
        return null;
    }

    private void reset(boolean resetFirstItem) {
        product = null;
        //productItems = null;
        if (productItems == null) {
            productItems = new ArrayList<Product>();
        }
        
        pagingInfo.setItemCount(productItems.size());
        if (resetFirstItem) {
            pagingInfo.setFirstItem(0);
        }
    }

    public void validateCreate(FacesContext facesContext, UIComponent component, Object value) {
        Product newProduct = new Product();
        String newProductString = converter.getAsString(FacesContext.getCurrentInstance(), null, newProduct);
        String productString = converter.getAsString(FacesContext.getCurrentInstance(), null, product);
        if (!newProductString.equals(productString)) {
            createSetup();
        }
    }

    public Converter getConverter() {
        return converter;
    }
    
    public boolean isIsWeb() {
        return isWeb;
    }

    public void setIsWeb(boolean isWeb) {
        this.isWeb = isWeb;
    }

}
