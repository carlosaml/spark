package spark.route;

import spark.utils.SparkUtils;

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: carlosaml
* Date: 11/24/12
* Time: 5:45 PM
* To change this template use File | Settings | File Templates.
*/
class RouteEntry {

    private HttpMethod httpMethod;
    private String path;
    private Object target;
    private final WildCardMatcher wildCardMatcher = new WildCardMatcher(this);
    private final SpecificPathMatcher specificPathMatcher = new SpecificPathMatcher(this);

    RouteEntry(HttpMethod httpMethod, String path, Object target) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.target = target;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Object getTarget() {
        return target;
    }

    public boolean matches(HttpMethod httpMethod, String path) {
        if ( (httpMethod == HttpMethod.before || httpMethod == HttpMethod.after)
                        && (this.httpMethod == httpMethod)
                        && this.path.equals(SparkUtils.ALL_PATHS)) {
            // Is filter and matches all
            return true;
        }
        boolean match = false;
        if (this.httpMethod == httpMethod) {
            match = matchPath(path);
        }
        return match;
    }

    private boolean matchPath(String path) {
        if (!this.path.endsWith("*") && ((path.endsWith("/") && !this.path.endsWith("/"))
                        || (this.path.endsWith("/") && !path.endsWith("/")))) {
            // One and not both ends with slash
            return false;
        }
        if (this.path.equals(path)) {
            // Paths are the same
            return true;
        }

        // check params
        List<String> thisPathList = SparkUtils.convertRouteToList(this.path);
        List<String> pathList = SparkUtils.convertRouteToList(path);


        int thisPathSize = thisPathList.size();
        int pathSize = pathList.size();

        if (thisPathSize == pathSize) {
            return specificPathMatcher.matchesSpecificPaths(thisPathList, pathList, thisPathSize);
        } else {
            return wildCardMatcher.matchesWildCards(path, thisPathList, pathList, thisPathSize, pathSize);
        }
    }

    public String toString() {
        return httpMethod.name() + ", " + path + ", " + target;
    }
}
