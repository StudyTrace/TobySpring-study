package tobi.learningtest.jdk.proxy;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class DynamicProxyTest {


    @Test
    void simpleProxy() {
        Hello hello = new HelloTarget(); // 타깃은 인터페이스를 통해 접근하는 습관 들이기
        assertThat(hello.sayHello("Toby"), is("Hello Toby"));
        assertThat(hello.sayHi("Toby"), is("Hi Toby"));
        assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));

//        Hello proxiedHello = new HelloUpperCase(new HelloTarget());

        Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Hello.class}, new UppercaseHandler(new HelloTarget())); // 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 캘르스로더, 구현할 인터페이스, 부가기능,위임코드까지 담은코드



        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));

    }


    @Test
    void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));

    }


    @Test
    void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
    }


    @Test
    void classNamePointcutAdvisor() {
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
            @Override
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        };

        classMethodPointcut.setMappedName("sayH*");

        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget{};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget{};
        checkAdviced(new HelloToby(), classMethodPointcut, true);

    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if (adviced) {
            assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
            assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
        }else{
            assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));

        }
    }


    static class HelloUpperCase implements Hello {

        Hello hello; // 위임할 타겟오브젝트.


        public HelloUpperCase(Hello hello) {
            this.hello = hello;
        }


        @Override
        public String sayHello(String name) {
            return hello.sayHello(name).toUpperCase();
        }

        @Override
        public String sayHi(String name) {
            return hello.sayHi(name).toUpperCase();
        }

        @Override
        public String sayThankYou(String name) {
            return hello.sayThankYou(name).toUpperCase();
        }
    }

    static interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {

        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String sayHi(String name) {
            return "Hi " + name;
        }

        @Override
        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }


    static class UppercaseHandler implements InvocationHandler {
        Object target;

        private UppercaseHandler(Object target) {
            this.target = target;   // 타깃 오브젝트를 주입
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object ret = method.invoke(target, args); // 타깃으로 위임
            if(ret instanceof String && method.getName().startsWith("say")){
                return ((String)ret).toUpperCase(); // 부가기능 제공
            }
            else{
                return ret;
            }

        }
    }


    static class UppercaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            return ret.toUpperCase(); // 부가기능적용
        }
    }
}
