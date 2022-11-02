package org.tronproject.onecall.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tron.trident.core.ApiWrapper;
import org.tronproject.onecall.AppTest;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-22 09:32
 */
@Slf4j
public class TransactionServiceTest extends AppTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ApiWrapper wrapper;

    @Test
    public void processTest() {


    }


}
