package com.ken.wms.exception;

/**
 * LocationRecordManageService异常
 *
 * @author Ken
 * @since 2017/3/8.
 */
public class LocationRecordManageServiceException extends BusinessException {

    public LocationRecordManageServiceException(){
        super();
    }

    public LocationRecordManageServiceException(Exception e){
        super(e);
    }

    public LocationRecordManageServiceException(Exception e, String exceptionDesc){
        super(e, exceptionDesc);
    }

    public LocationRecordManageServiceException(String exceptionDesc){
        super(exceptionDesc);
    }

}
