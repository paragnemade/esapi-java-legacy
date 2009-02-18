package org.owasp.esapi.filters.waf.rules;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.filters.waf.internal.InterceptingHTTPServletRequest;
import org.owasp.esapi.filters.waf.internal.InterceptingHTTPServletResponse;

public class AuthenticatedRule extends Rule {

	private String sessionAttribute;
	private List<Object> exceptions;

	public AuthenticatedRule(String sessionAttribute, List<Object> exceptions) {
		this.sessionAttribute = sessionAttribute;
		this.exceptions = exceptions;
	}

	public boolean check(InterceptingHTTPServletRequest request,
			InterceptingHTTPServletResponse response) {

		HttpSession session = request.getSession();

		if ( session != null && session.getAttribute(sessionAttribute) != null ) {

			return true;

		} else { /* check if it's one of the exceptions */

			Iterator it = exceptions.iterator();

			while(it.hasNext()) {
				Object o = it.next();
				if ( o instanceof Pattern ) {

					Pattern p = (Pattern)o;
					if ( p.matcher(request.getRequestURI()).matches() ) {
						return true;
					}

				} else if ( o instanceof String ) {

					if ( request.getRequestURI().equals((String)o)) {
						return true;
					}

				}
			}
		}

		return false;
	}

}
