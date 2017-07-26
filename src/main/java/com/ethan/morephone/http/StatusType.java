
package com.ethan.morephone.http;


/**
 * (c) ANPSOFT - HANOI
 *
 * @author Ethan Nguyen
 * @since 5:23:51 PM  Aug 10, 2016
 * Skype: truong.nguyenptit
 * 
 */
public interface StatusType {
	/**
	 * Get the associated status code
	 * 
	 * @return the status code
	 */
	public int getStatusCode();

	/**
	 * Get the class of status code
	 * 
	 * @return the class of status code
	 */
	public HTTPStatus.Family getFamily();

	/**
	 * Get the reason phrase
	 * 
	 * @return the reason phrase
	 */
	public String getReasonPhrase();
}

