package org.example.service.router;

import java.util.HashMap;
import java.util.Map;

public class Router {


    // /a -> handler
    // /a/b -> handler
    // /c   -> no handler
    // /a/{a_id}/b/{b_id} -> handler_x
    static class Route {
        String route;
        Map<String, Route> children;
        String handler;

        public Route(String r, String h) {
            this.children = new HashMap<>();
            this.route = r;
            this.handler = h;
        }
    }

    Route root = new Route("/", "root");

    public void addRoute(String path, String handler) {
        String[] components = path.split("/");
        Route parentRoute = find(components, 1, components.length - 1, root);
        if (parentRoute == null) {
            throw new IllegalArgumentException();
        }
        String toInsert = components[components.length - 1];
        final Route newRoute;
        if (toInsert.startsWith("{") && toInsert.endsWith("}")) {
            newRoute = new Route("*", handler);
        } else {
            newRoute = new Route(toInsert, handler);
        }
        parentRoute.children.put(newRoute.route, newRoute);
    }

    private Route find(String[] components, int start, int end, Route cur) {
        if (start >= end) {
            return cur;
        }
        if (cur.children.containsKey(components[start])) {
            return find(components, start + 1, end, cur.children.get(components[start]));
        } else if (cur.children.containsKey("*")) {
            return find(components, start + 1, end, cur.children.get("*"));
        }
        return null;
    }

    public String getHandler(String path) {
        String[] components = path.split("/");
        Route foundRoute = find(components, 1, components.length, root);
        return foundRoute != null ? foundRoute.handler : "";
    }

}


// /a

// /a/*  - > A Handler

// /a/123