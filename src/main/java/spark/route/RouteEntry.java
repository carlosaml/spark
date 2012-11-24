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
            return matchesSpecificPaths(thisPathList, pathList, thisPathSize);
        } else {
            return matchesWildCards(path, thisPathList, pathList, thisPathSize, pathSize);
        }
    }

    private boolean matchesWildCards(String path, List<String> thisPathList, List<String> pathList, int thisPathSize, int pathSize) {
        // Number of "path parts" not the same
        // check wild card:
        if (this.path.endsWith("*")) {
            if (pathSize == (thisPathSize - 1) && (path.endsWith("/"))) {
                // Hack for making wildcards work with trailing slash
                pathList.add("");
                pathList.add("");
                pathSize += 2;
            }

            if (thisPathSize < pathSize) {
                for (int i = 0; i < thisPathSize; i++) {
                    String thisPathPart = thisPathList.get(i);
                    String pathPart = pathList.get(i);
                    if (thisPathPart.equals("*") && (i == thisPathSize -1) && this.path.endsWith("*")) {
                        // wildcard match
                        return true;
                    }
                    if (!thisPathPart.startsWith(":") && !thisPathPart.equals(pathPart)) {
                        return false;
                    }
                }
                // All parts matched
                return true;
            }
            // End check wild card
        }
        return false;
    }

    private boolean matchesSpecificPaths(List<String> thisPathList, List<String> pathList, int thisPathSize) {
        for (int i = 0; i < thisPathSize; i++) {
            String thisPathPart = thisPathList.get(i);
            String pathPart = pathList.get(i);

            if ((i == thisPathSize -1)) {
                if (thisPathPart.equals("*") && this.path.endsWith("*")) {
                    // wildcard match
                    return true;
                }
            }

            if (!thisPathPart.startsWith(":") && !thisPathPart.equals(pathPart)) {
                return false;
            }
        }
        // All parts matched
        return true;
    }

    public String toString() {
        return httpMethod.name() + ", " + path + ", " + target;
    }
}
