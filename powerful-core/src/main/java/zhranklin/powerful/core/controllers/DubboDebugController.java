package zhranklin.powerful.core.controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ConditionalOnProperty(name = "powerful.dubbo.enabled", havingValue = "true")
@RestController
public class DubboDebugController {

    @RequestMapping("/powerful/configs")
    public Object getDebugConfig(@RequestParam(name = "key", required = false, defaultValue = "") String preFix,
                                 @RequestParam(name = "type", required = false, defaultValue = "html") String type) {
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("zhranklin.agent.configdebuginfo.ConfigDebugManager");
            Method getConfig = clazz.getDeclaredMethod("getDebugConfig", String.class, String.class);
            return getConfig.invoke(null, preFix, type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "zhranklin.agent.configdebuginfo.ConfigDebugManager not find";
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "method getDebugConfig not find";
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "error !!!";
    }
}
