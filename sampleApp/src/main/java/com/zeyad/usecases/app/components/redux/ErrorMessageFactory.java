package com.zeyad.usecases.app.components.redux;

/**
 * @author by ZIaDo on 5/4/17.
 */

public interface ErrorMessageFactory {

    String getErrorMessage(Throwable throwable);
}
