package org.dieschnittstelle.mobile.android.skeleton.model;

import android.nfc.FormatException;

import com.google.android.gms.nearby.connection.AuthenticationException;

public interface IAuthenticationOperations {

    public boolean authenticateUser(User user) throws FormatException,  AuthenticationException;

}
