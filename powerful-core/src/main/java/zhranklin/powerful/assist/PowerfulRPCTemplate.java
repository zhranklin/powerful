package zhranklin.powerful.assist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 张武 at 2021/10/13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PowerfulRPCTemplate {

	/**
	 * mapping service name, 在调用的时候会用到
	 */
	String value();

	/**
	 * 指定子包名
	 */
	String subPackage() default "";

}
