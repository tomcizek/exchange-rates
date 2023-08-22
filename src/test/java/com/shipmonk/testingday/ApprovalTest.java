package com.shipmonk.testingday;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.approvaltests.Approvals;
import org.springframework.beans.factory.annotation.Autowired;

public class ApprovalTest {
    @Autowired
    private ObjectMapper objectMapper;

    public void thenExpectedResult(Object object) {
        String orderAsJson = null;
        try {
            orderAsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String sanitizedJson = orderAsJson.replaceAll(
            "\"[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\"",
            "\"REPLACED_UUID\""
        );
        Approvals.verify(sanitizedJson);
    }

}
