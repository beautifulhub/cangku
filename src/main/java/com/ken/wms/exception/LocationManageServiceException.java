package com.ken.wms.exception;

/**
 * LocationManageService异常
 *
 * @author Ken
 * @since 2017/3/8.
 */
public class LocationManageServiceException extends BusinessException {

    LocationManageServiceException(){
        super();
    }

    public LocationManageServiceException(Exception e){
        super(e);
    }

    LocationManageServiceException(Exception e, String exceptionDesc){
        super(e, exceptionDesc);
    }

}
