<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%--
    This file is an entry point for JavaServer Faces application.
--%>
<f:view>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
<link rel="stylesheet" type="text/css" href="/JsfJpaCrud/faces/jsfcrud.css" />
    </head>
    <body>
        <h:form>
<h1><h:outputText value="JavaServer Faces" /></h1>
    <br/>
<h:commandLink action="#{purchaseOrder.listSetup}" value="Show All PurchaseOrder Items"/>

    <br/>
<h:commandLink action="#{productCode.listSetup}" value="Show All ProductCode Items"/>

    <br/>
<h:commandLink action="#{product.listSetup}" value="Show All Product Items"/>

    <br/>
<h:commandLink action="#{microMarket.listSetup}" value="Show All MicroMarket Items"/>

    <br/>
<h:commandLink action="#{manufacturer.listSetup}" value="Show All Manufacturer Items"/>

    <br/>
<h:commandLink action="#{discountCode.listSetup}" value="Show All DiscountCode Items"/>

    <br/>
<h:commandLink action="#{customer.listSetup}" value="Show All Customer Items"/>
</h:form>

    </body>
</html>
</f:view>
