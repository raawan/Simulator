package com.cmsmock;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;

public class SOAPRequestMatcher extends RequestMatcherExtension {

    public MatchResult match(final Request request, final Parameters parameters) {
        System.out.println(request.getBodyAsString());
        System.out.println(request.getBodyAsString().contains("CelsiusToFahrenheit"));
        //return  MatchResult.of(request.getBodyAsString().contains("/CelsiusToFahrenheit"));
        return  MatchResult.of(true);

    }
}
