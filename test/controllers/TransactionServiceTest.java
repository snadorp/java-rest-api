package controllers;

import models.Transaction;

import org.junit.*;
import static org.fest.assertions.Assertions.*;

import play.mvc.*;
import play.test.*;
import play.libs.Json;
import static play.test.Helpers.*;

import com.fasterxml.jackson.databind.JsonNode;

public class TransactionServiceTest {

    @Test
    public void callgetTransactionSuccess() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {

                    Transaction t1 = new Transaction(1.0, "car");
                    t1.save();
                    Transaction t2 = new Transaction(3.0, "car", t1.id);
                    t2.save();

                    Result result = callAction(controllers.routes.ref.TransactionService.getTransaction(t1.id));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"amount\":1.0,\"type\":\"car\"}");

                    result = callAction(controllers.routes.ref.TransactionService.getTransaction(t2.id));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"amount\":3.0,\"type\":\"car\",\"parentId\":" + t1.id + "}");
                }
            });
    }

    @Test
    public void callgetTransactionError() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {
                    Result result = callAction(controllers.routes.ref.TransactionService.getTransaction(99999999));
                    assertThat(status(result)).isEqualTo(BAD_REQUEST);

                    result = callAction(controllers.routes.ref.TransactionService.getTransaction(-9));
                    assertThat(status(result)).isEqualTo(BAD_REQUEST);
                }
            });
    }

    @Test
    public void callputTransactionSuccess() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {

                    String body = "{\"amount\":1.0,\"type\":\"car\"}";
                    JsonNode json = Json.parse(body);
                    FakeRequest request = new FakeRequest(PUT, "/transactionservice/transaction/4").withJsonBody(json);
                    Result result = callAction(controllers.routes.ref.TransactionService.putTransaction(4), request);

                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"status\":\"ok\"}");

                    //recall and it should be updated
                    result = callAction(controllers.routes.ref.TransactionService.putTransaction(4), request);
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"status\":\"Transaction updated\"}");
                }
            });
    }

    @Test
    public void callputTransactionParentIdError() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {

                    String body = "{\"amount\":1.0,\"type\":\"car\",\"parentId\":\"1\"}";
                    JsonNode json = Json.parse(body);
                    FakeRequest request = new FakeRequest(PUT, "/transactionservice/transaction/4").withJsonBody(json);
                    Result result = callAction(controllers.routes.ref.TransactionService.putTransaction(4), request);

                    assertThat(status(result)).isEqualTo(BAD_REQUEST);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("Parent ID not found.");
                }
            });
    }

    @Test
    public void callputTransactionJsonMissingFieldsButSuccess() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {

                    String body = "{\"amount\":1.0}";
                    JsonNode json = Json.parse(body);
                    FakeRequest request = new FakeRequest(PUT, "/transactionservice/transaction/4").withJsonBody(json);
                    Result result = callAction(controllers.routes.ref.TransactionService.putTransaction(4), request);

                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"status\":\"ok\"}");
                }
            });
    }


    @Test
    public void callgetTypesSuccess() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {

                    new Transaction(1.0, "car").save();
                    new Transaction(3.0, "car").save();
                    new Transaction(3.0, "bike").save();
                    new Transaction(3.0, "bike").save();

                    Result result = callAction(controllers.routes.ref.TransactionService.getTypes("car"));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("[1,2]");

                    result = callAction(controllers.routes.ref.TransactionService.getTypes("bike"));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("[3,4]");

                    result = callAction(controllers.routes.ref.TransactionService.getTypes("jet"));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("[]");
                }
            });
    }

    @Test
    public void callgetSumSuccess() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {

                    new Transaction(1.0, "car").save();
                    new Transaction(3.0, "car", 1L).save();
                    new Transaction(3.0, "bike").save();
                    new Transaction(3.0, "bike", 3L).save();
                    new Transaction(6.0, "car", 2L).save();

                    // all `car` transactions
                    Result result = callAction(controllers.routes.ref.TransactionService.getSum(5));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"sum\":10.0}");

                    // just 2 `car` transactions
                    result = callAction(controllers.routes.ref.TransactionService.getSum(2));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"sum\":4.0}");

                    // just 1 `car` transactions
                    result = callAction(controllers.routes.ref.TransactionService.getSum(1));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"sum\":1.0}");

                    // all `bike` transactions
                    result = callAction(controllers.routes.ref.TransactionService.getSum(4));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"sum\":6.0}");

                }
            });
    }

    @Test
    public void callgetSumCircularSuccess() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {
                    // let's build a nice little endless loop
                    new Transaction(3.0, "car", 2L).save();
                    new Transaction(6.0, "car", 3L).save();
                    new Transaction(1.0, "car", 1L).save();

                    Result result = callAction(controllers.routes.ref.TransactionService.getSum(1));
                    assertThat(status(result)).isEqualTo(OK);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("{\"sum\":10.0}");

                }
            });
    }

    @Test
    public void callgetSumError() {
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {

                    Result result = callAction(controllers.routes.ref.TransactionService.getSum(5));
                    assertThat(status(result)).isEqualTo(BAD_REQUEST);
                    assertThat(contentType(result)).isEqualTo("application/json");
                    assertThat(charset(result)).isEqualTo("utf-8");
                    assertThat(contentAsString(result)).contains("Transaction not found");
                }
            });
    }


}
