package com.iknow.crud.app;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import jpa.controllers.ProductJpaController;

/**
 *
 * @author mbohm
 */
public class ProductConverter implements Converter {

    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        Integer id = new Integer(string);
        ProductJpaController controller = (ProductJpaController) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "productJpa");
        return controller.findProduct(id);
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Product) {
            Product o = (Product) object;
            return o.getProductId() == null ? "" : o.getProductId().toString();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: jpa.entities.Product");
        }
    }

}
