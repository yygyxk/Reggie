package com.kking.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 * 在处理工程中，涉及到的下面类的方法都属于同一个线程
 * LoginCheckFilter的doFilter方法
 * EmployeeController的Update方法
 * MyMetaObjectHandler的UpdateFill方法
 * 同一线程相当于一个数据域，可以设置和获取相对应的值，而不会和其他线程相互影响，
 * ThreadLocal会为每个使用该变量的线程提供独立的变量副本，每个线程独立地改变自己变量副本。
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 利用多线程，设置id值
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
