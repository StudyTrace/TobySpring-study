package tobi.user.dao;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@ExtendWith(SpringExtension.class) // @Runwith 대체
@ContextConfiguration(classes = {DaoFactory.class})
 class FactoryBeanTest {
    @Autowired
    ApplicationContext context;

    @Test
    void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
//        assertThat(message, is(Message.class));
        assertThat(message,instanceOf(Message.class));
        assertThat(((Message)message).getText(), is("Factory Bean"));



    }


}
