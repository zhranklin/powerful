package zhranklin.powerful.controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ConditionalOnProperty(name = "framew.type", havingValue = "dubbo")
@RestController
public class DubboDebugController {

    @RequestMapping("/powerful/configs")
    public Object getDebugConfig(@RequestParam(name = "key", required = false, defaultValue = "") String preFix,
                                 @RequestParam(name = "type", required = false, defaultValue = "html") String type) {
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass("zhranklin.agent.configdebuginfo.ConfigDebugManager");
            Method getConfig = clazz.getDeclaredMethod("getDebugConfig", String.class, String.class);
            Object obj = getConfig.invoke(null, preFix, type);
            return obj;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "zhranklin.agent.configdebuginfo.ConfigDebugManager not find";
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "method getDebugConfig not find";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "error !!!";
    }
}
