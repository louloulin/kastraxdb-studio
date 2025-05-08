package ai.magicdb.server.start.test.common;

import ai.magicdb.server.start.Application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic test class
 *
 * @author Jiaju Zhuang
 **/
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public abstract class BaseTest {

}
