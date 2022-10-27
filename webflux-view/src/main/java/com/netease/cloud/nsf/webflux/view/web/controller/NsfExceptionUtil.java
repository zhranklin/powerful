package com.netease.cloud.nsf.webflux.view.web.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author Weng Yanghui (wengyanghui@corp.netease.com)
 * @version $Id: Const.java, v 1.0 2018/7/12
 */
class NsfExceptionUtil {

    private static final String NSF_LIMIT_ERROR = "com.netease.cloud.nsf.agent.core.exception.NsfRateLimiterException";
    private static final String NSF_HYSTRIX_TIMEOUT = "com.netflix.hystrix.exception.HystrixTimeoutException";
    private static final String NSF_HYSTRIX_RUNTIME = "com.netflix.hystrix.exception.HystrixRuntimeException";
    private static final String NSF_HYSTRIX_BAD_REQUEST = "com.netflix.hystrix.exception.HystrixBadRequestException";

    private static final String HYSTRIX_TIMEOUT = "TIMEOUT";
    private static final String HYSTRIX_SHORTCIRCUIT = "SHORTCIRCUIT";
    private static final String HYSTRIX_REJECTED_THREAD_EXECUTION = "REJECTED_THREAD_EXECUTION";
    private static final String HYSTRIX_BAD_REQUEST_EXCEPTION = "BAD_REQUEST_EXCEPTION";
    private static final String HYSTRIX_COMMAND_EXCEPTION = "COMMAND_EXCEPTION";
    private static final String HYSTRIX_REJECTED_SEMAPHORE_EXECUTION = "REJECTED_SEMAPHORE_EXECUTION";
    private static final String HYSTRIX_REJECTED_SEMAPHORE_FALLBACK = "REJECTED_SEMAPHORE_FALLBACK";

    static class NsfExceptionWrapper{
        private NsfExceptionType type;
        private Throwable throwable;
        NsfExceptionWrapper(NsfExceptionType type, Throwable throwable) {
            this.type = type;
            this.throwable = throwable;
        }

        NsfExceptionType getType() {
            return type;
        }

        Throwable getThrowable() {
            return throwable;
        }
    }

    public enum NsfExceptionType {
        /**
         * NSF 异常定义枚举
         */
        RATE_LIMITED("请求被流控"),
        HYSTRIX_BAD_REQUEST("Hystrix错误请求"),
        HYSTRIX_TIMEOUT("Hystrix执行超时"),
        HYSTRIX_RUNTIME_RATE_LIMITED("Hystrix运行时被流控异常"),
        HYSTRIX_RUNTIME_TIMEOUT("Hystrix运行时超时异常"),
        HYSTRIX_RUNTIME_SHORTCIRCUIT("Hystrix运行时熔断异常"),
        HYSTRIX_RUNTIME_REJECTED_THREAD_EXECUTION("Hystrix运行时线程池拒绝异常"),
        HYSTRIX_RUNTIME_BAD_REQUEST_EXCEPTION("Hystrix运行时错误请求异常"),
        HYSTRIX_RUNTIME_COMMAND_EXCEPTION("Hystrix运行时命令执行异常"),
        HYSTRIX_RUNTIME_REJECTED_SEMAPHORE_EXECUTION("Hystrix运行时信号量执行异常"),
        HYSTRIX_RUNTIME_REJECTED_SEMAPHORE_FALLBACK("Hystrix运行时信号量拒绝降级"),
        HYSTRIX_RUNTIME_OTHER("Hystrix运行时其它异常"),
        NORMAL_EXCEPTION("异常");

        public String getDesc() {
            return desc;
        }

        private String desc;
        NsfExceptionType(String desc) {
            this.desc = desc;
        }
    }

    private static String parseFailureType(Exception exception){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(classLoader.toString());
        try {
            Class<?> clazz = classLoader.loadClass("com.netflix.hystrix.exception.HystrixRuntimeException");
            Method mGetFailureType = clazz.getDeclaredMethod("getFailureType");
            Object failure = mGetFailureType.invoke(exception);
            return failure.toString();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    static NsfExceptionWrapper parseException(Exception e) {
        String exClassName = e.getClass().getName();
        switch (exClassName) {
            case NSF_LIMIT_ERROR:
                return new NsfExceptionWrapper(NsfExceptionType.RATE_LIMITED, e);
            case NSF_HYSTRIX_RUNTIME:
                if (e.getCause() != null && e.getCause().toString().contains(NSF_LIMIT_ERROR)) {
                    return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_RATE_LIMITED, e.getCause());
                }
                String failureType = parseFailureType(e);
                if (failureType != null) {
                    if (HYSTRIX_TIMEOUT.equalsIgnoreCase(failureType)) {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_TIMEOUT, e.getCause());
                    } else if (HYSTRIX_SHORTCIRCUIT.equalsIgnoreCase(failureType)) {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_SHORTCIRCUIT, e.getCause());
                    } else if (HYSTRIX_REJECTED_THREAD_EXECUTION.equalsIgnoreCase(failureType)) {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_REJECTED_THREAD_EXECUTION, e.getCause());
                    } else if (HYSTRIX_BAD_REQUEST_EXCEPTION.equalsIgnoreCase(failureType)) {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_BAD_REQUEST_EXCEPTION, e.getCause());
                    } else if (HYSTRIX_COMMAND_EXCEPTION.equalsIgnoreCase(failureType)) {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_COMMAND_EXCEPTION, e.getCause());
                    } else if (HYSTRIX_REJECTED_SEMAPHORE_EXECUTION.equalsIgnoreCase(failureType)) {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_REJECTED_SEMAPHORE_EXECUTION, e.getCause());
                    } else if (HYSTRIX_REJECTED_SEMAPHORE_FALLBACK.equalsIgnoreCase(failureType)) {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_REJECTED_SEMAPHORE_FALLBACK, e.getCause());
                    } else {
                        return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_OTHER, e.getCause());
                    }
                }
                return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_RUNTIME_OTHER, new Exception());
            case NSF_HYSTRIX_BAD_REQUEST:
                return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_BAD_REQUEST, new Exception());
            case NSF_HYSTRIX_TIMEOUT:
                return new NsfExceptionWrapper(NsfExceptionType.HYSTRIX_TIMEOUT, new Exception());
            default:
                return new NsfExceptionWrapper(NsfExceptionType.NORMAL_EXCEPTION, e);
        }
    }

}
