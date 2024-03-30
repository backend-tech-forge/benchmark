package org.benchmarker.bmcontroller.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AgentServerManagerTest {

    @Autowired
    private AgentServerManager agentServerManager;
    private String url = "http://localhost:8080";


}