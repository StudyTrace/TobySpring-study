package tobi.user.dao;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {

    String text;

    public void setText(String text) {
        this.text = text;
    }

    /**
     *  오브젝트를 생성할때 필요한정보를 팩토리빈의 프로퍼티로설정, 대산DI받을수있다.
     */


    public Message getObject() throws Exception {
        System.out.println("getObject가뽑아낸 값 :" + Message.newMessage(this.text));
        return Message.newMessage(this.text);
    }

    /**
     *  실제 빈으로 사용될 오브젝트를 직접 생성한다.
     */


    public Class<? extends Message> getObjectType() {
        return Message.class;
    }


    public boolean isSingleton() {
        return true;
    }
}
