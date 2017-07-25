package org.onap.msb.apiroute.wrapper.dao;

import org.onap.msb.apiroute.wrapper.dao.route.IRouteDAO;
import org.onap.msb.apiroute.wrapper.dao.route.RouteDAOImpl;
import org.onap.msb.apiroute.wrapper.dao.service.IServiceDAO;
import org.onap.msb.apiroute.wrapper.dao.service.ServiceDAOImpl;

public class DAOFactory {
    private static final IRouteDAO routeDAO = new RouteDAOImpl();
    private static final IServiceDAO serviceDAO = new ServiceDAOImpl();

    public static IRouteDAO getRouteDAO(){
        return routeDAO;
    }
    public static IServiceDAO getServiceDAO(){
        return serviceDAO;
    }
}