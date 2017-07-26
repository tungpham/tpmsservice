/**
 * 
 */
package com.ethan.morephone.http;


/**
 * (c) ANPSOFT - HANOI
 *
 * @author Ethan Nguyen
 * @since 2:06:23 PM  Aug 29, 2016
 * Skype: truong.nguyenptit
 * 
 */
public final class Response<T> {
	/*----------------------------- Constants ----------------------------*/

	/*------------------------------ Fields ------------------------------*/
	 private final T response;
	 private final int status;
	/*---------------------------- Constructors  -------------------------*/
    public Response(T response, HTTPStatus status) {
        this.response = response;
        this.status = status.getStatusCode();
    }
	/*--------------------------- Getter & Setter ------------------------*/
    public T getResponse() { return response; }
    public int getStatus() { return status; }
	/*--------------- Methods for/from SuperClass/Interfaces -------------*/

	/*------------------------------ Methods -----------------------------*/

	/*-------------------- Inner and Anonymous Classes -------------------*/

}
