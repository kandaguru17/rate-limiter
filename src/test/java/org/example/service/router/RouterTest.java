package org.example.service.router;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RouterTest {

    @Test
    void shouldInitializeRouter() {
        Router router = new Router();
        assertNotNull(router);
    }

    @Test
    void shouldAddRoute() {
        Router router = new Router();
        String a_handler = "A_Handler";

        router.addRoute("/a", a_handler);
        String handler = router.getHandler("/a");
        assertEquals(a_handler, handler);


        String ab_handler = "ab_handler";
        router.addRoute("/a/b", ab_handler);
        handler = router.getHandler("/a/b");
        assertEquals(ab_handler, handler);
    }


    @Test
    void shouldHandleNonExistentRoutesRoutes() {
        Router router = new Router();
        String a_handler = "A_Handler";
        assertThrows(IllegalArgumentException.class, () -> router.addRoute("/a/b", a_handler));
    }

    @Test
    void shouldGetDefaultForNonExistentRoutesRoutes() {
        Router router = new Router();
        String a_handler = "A_Handler";
        router.addRoute("/a", a_handler);
        String s = assertDoesNotThrow(() -> router.getHandler("/a/b"));
        assertEquals(s, "");
    }

    @Test
    void shouldHandleRoutesWithPlaceholders() {
        Router router = new Router();

        String a_handler = "A_Handler";

        router.addRoute("/a", a_handler);
        String handler = router.getHandler("/a");
        assertEquals(a_handler, handler);

        String a_param_handler = "A_PARAM_Handler";
        router.addRoute("/a/{123}", a_param_handler);
        handler = router.getHandler("/a/{123}");
        assertEquals(a_param_handler, handler);

        String ab_handler = "AB_Handler";
        router.addRoute("/a/{123}/b", ab_handler);
        handler = router.getHandler("/a/{123}/b");
        assertEquals(ab_handler, handler);

        String ab_param_handler = "AB_Param_Handler";
        router.addRoute("/a/{123}/b/{234}", ab_param_handler);
        handler = router.getHandler("/a/{123}/b/{234}");
        assertEquals(ab_param_handler, handler);

    }

    @Test
    void shouldAddRoutesWithWildCards(){
        Router routerV2 = new Router();

        String route = "/a";
        String handler = "a_handler";
        routerV2.addRoute(route, handler);
        String res = routerV2.getHandler(route);
        assertEquals(handler, res);

        route = "/a/{a_id}";
        handler = "a_param_handler";
        routerV2.addRoute(route, handler);
        res = routerV2.getHandler("/a/123");
        assertEquals(handler, res);

        route = "/{r_id}";
        handler = "root_param_handler";
        routerV2.addRoute(route, handler);
        res = routerV2.getHandler("/123");
        assertEquals(handler, res);
    }


}
