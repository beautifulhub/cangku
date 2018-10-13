package com.ken.wms.exception;

/**
 * LocationRecordManageService异常
 *
 * @author Ken
 * @since 2017/3/8.
 */
public class LocationStorageManageServiceException extends BusinessException {

    public LocationStorageManageServiceException(){
        super();
    }

    public LocationStorageManageServiceException(Exception e){
        super(e);
    }

    public LocationStorageManageServiceException(Exception e, String exceptionDesc){
        super(e, exceptionDesc);
    }

    public LocationStorageManageServiceException(String exceptionDesc){
        super(exceptionDesc);
    }

}
