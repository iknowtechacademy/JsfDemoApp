package jsf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import jpa.controllers.ProductJpaController;
import jpa.controllers.ProductJpaControllerService;
import jpa.entities.Product;
import jsf.util.JsfUtil;
import jpa.controllers.exceptions.NonexistentEntityException;
import jpa.controllers.exceptions.IllegalOrphanException;
import jsf.util.PagingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author mbohm
 */
@Component
public class ProductController {

    private Product product = null;
    private List<Product> productItems = null;

    @Autowired
    private ProductJpaControllerService jpaController;

    private ProductConverter converter = null;
    private PagingInfo pagingInfo = null;

    public ProductController() {
        //FacesContext facesContext = FacesContext.getCurrentInstance();
        //jpaController = (ProductJpaController) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "productJpa");
        pagingInfo = new PagingInfo();
        converter = new ProductConverter();
        
        
        //product = new Product();
    }

    public PagingInfo getPagingInfo() {
        System.out.println("jpaController:: "+jpaController);
        
        if(jpaController == null){
            pagingInfo.setItemCount(0);
        }
        
        if (pagingInfo.getItemCount() == -1) {
            pagingInfo.setItemCount(jpaController.getProductCount());
        }
        return pagingInfo;
    }

    public SelectItem[] getProductItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(jpaController.findProductEntities(), false);
    }

    public SelectItem[] getProductItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(jpaController.findProductEntities(), true);
    }

    public Product getProduct() {
        if (product == null) {
            product = (Product) JsfUtil.getObjectFromRequestParameter("jsfcrud.currentProduct", converter, null);
        }
        if (product == null) {
            product = new Product();
        }
        return product;
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
            
            if(productItems == null){
                productItems = new ArrayList<Product>();
            }
            
            System.out.println("productItems "+productItems);
            
            //jpaController.create(product);
            
            productItems.add(product);
            
            JsfUtil.addSuccessMessage("Product was successfully created.");
        } catch (Exception e) {
            
            JsfUtil.ensureAddErrorMessage(e, "A persistence error occurred.");
                        
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
        String productString = converter.getAsString(FacesContext.getCurrentInstance(), null, product);
        String currentProductString = JsfUtil.getRequestParameter("jsfcrud.currentProduct");
        if (productString == null || productString.length() == 0 || !productString.equals(currentProductString)) {
            String outcome = editSetup();
            if ("product_edit".equals(outcome)) {
                JsfUtil.addErrorMessage("Could not edit product. Try again.");
            }
            return outcome;
        }
        try {
            jpaController.edit(product);
            JsfUtil.addSuccessMessage("Product was successfully updated.");
        } catch (IllegalOrphanException oe) {
            JsfUtil.addErrorMessages(oe.getMessages());
            return null;
        } catch (NonexistentEntityException ne) {
            JsfUtil.addErrorMessage(ne.getLocalizedMessage());
            return listSetup();
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
            jpaController.destroy(id);
            JsfUtil.addSuccessMessage("Product was successfully deleted.");
        } catch (IllegalOrphanException oe) {
            JsfUtil.addErrorMessages(oe.getMessages());
            return null;
        } catch (NonexistentEntityException ne) {
            JsfUtil.addErrorMessage(ne.getLocalizedMessage());
            return relatedOrListOutcome();
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
        /*
        if (productItems == null) {
            getPagingInfo();
            productItems = jpaController.findProductEntities(pagingInfo.getBatchSize(), pagingInfo.getFirstItem());
        }*/
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
        productItems = null;
        pagingInfo.setItemCount(-1);
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

}
