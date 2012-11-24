/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.route;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
public class SimpleRouteMatcher implements RouteMatcher {

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleRouteMatcher.class);
    
    private List<RouteEntry> routes;

    public SimpleRouteMatcher() {
        routes = new ArrayList<RouteEntry>();
    }

    @Override
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path) {
        for (RouteEntry entry : routes) {
            if (entry.matches(httpMethod, path)) {
                return new RouteMatch(httpMethod, entry.getTarget(), entry.getPath(), path);
            }
        }
        return null;
    }
    
    @Override
    public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path) {
        List<RouteMatch> matchSet = new ArrayList<RouteMatch>();
        for (RouteEntry entry : routes) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(new RouteMatch(httpMethod, entry.getTarget(), entry.getPath(), path));
            }
        }
        return matchSet;
    }

    @Override
    public void parseValidateAddRoute(String route, Object target) {
        try {
            int singleQuoteIndex = route.indexOf(SINGLE_QUOTE);
            String httpMethod = route.substring(0, singleQuoteIndex).trim().toLowerCase();
            String url = route.substring(singleQuoteIndex + 1, route.length() - 1).trim().toLowerCase();

            // Use special enum stuff to get from value
            HttpMethod method;
            try {
                method = HttpMethod.valueOf(httpMethod);
            } catch (IllegalArgumentException e) {
                LOG.error("The @Route value: "
                                + route
                                + " has an invalid HTTP method part: "
                                + httpMethod
                                + ".");
                return;
            }
            addRoute(method, url, target);
        } catch (Exception e) {
            LOG.error("The @Route value: " + route + " is not in the correct format", e);
        }

    }

    private void addRoute(HttpMethod method, String url, Object target) {
        RouteEntry entry = new RouteEntry(method, url, target);
        LOG.debug("Adds route: " + entry);
        // Adds to end of list
        routes.add(entry);
    }

    @Override
    public void clearRoutes() {
        routes.clear();
    }

}
