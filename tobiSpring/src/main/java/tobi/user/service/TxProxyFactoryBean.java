package tobi.user.service;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {

    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;
    Class<?> serviceInterface;


    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }


    @Override
    public Object getObject() throws Exception {
        TransactionHandler txhandler = new TransactionHandler();
        txhandler.setTarget(target);
        txhandler.setTransactionManager(transactionManager);
        txhandler.setPattern(pattern);
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{serviceInterface}, txhandler);
    }



    @Override
    public Class<?> getObjectType() {
        return serviceInterface; // 팩토리빈이 생성하는 오브젝트타입은 DI받은 인터페이스타입에 따라 달라진다. 따라서 다양한 타입의 프록시 오브젝트 생성에 재사용할수있다.
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
