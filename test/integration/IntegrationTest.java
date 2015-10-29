package integration;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.Result;

import static play.libs.F.Function;
import static play.libs.F.Promise;

import java.util.HashMap;
import models.Transaction;
import play.libs.Json;

public class IntegrationTest {

    @Test
    public void callGetTransactionError() {
        running(testServer(3333), new Runnable() {
                public void run() {
                    String path = "/transactionservice/transaction/1";
                    Response r = WS.url("http://localhost:3333" + path).get().get();
                    assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(r.getBody()).contains("Couldn't find transaction.");
                }
            });
    }

    @Test
    public void callPutTransactionSuccess() {
        running(testServer(3333), new Runnable() {
                public void run() {
                    String path = "/transactionservice/transaction/1";
                    String payload = "{\"amount\":1.0,\"type\":\"car\"}";

                    Response r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(payload)
                        .get();
                    assertThat(r.getStatus()).isEqualTo(OK);
                    assertThat(r.getBody()).contains("{\"status\":\"ok\"}");

                    // add another transaction with parent
                    path = "/transactionservice/transaction/2";
                    payload = "{\"amount\":2.0,\"type\":\"car\",\"parentId\":1}";

                    r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(payload)
                        .get();
                    assertThat(r.getStatus()).isEqualTo(OK);
                    assertThat(r.getBody()).contains("{\"status\":\"ok\"}");

                    // update transaction 1
                    path = "/transactionservice/transaction/1";
                    payload = "{\"amount\":5.0,\"type\":\"car\"}";

                    r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(payload)
                        .get();
                    assertThat(r.getStatus()).isEqualTo(OK);
                    assertThat(r.getBody()).contains("Transaction updated");
                }
            });
    }

    @Test
    public void callPutTransactionError() {
        running(testServer(3333), new Runnable() {
                public void run() {
                    String path = "/transactionservice/transaction/1";
                    //invalid json
                    String payload = "{\"amount\":1.0,\"type";

                    Response r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(payload)
                        .get();
                    assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(r.getBody()).contains("Invalid Json");
                }
            });
    }

    @Test
    public void callgetTypesSuccess() {
        running(testServer(3333), new Runnable() {
                public void run() {
                    //Set up a couple of transactions
                    String path = "/transactionservice/transaction/1";
                    WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(Json.toJson(new Transaction(2.0, "car")))
                        .get();

                    path = "/transactionservice/transaction/2";
                    WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(Json.toJson(new Transaction(2.0, "car", 1L)))
                        .get();

                    path = "/transactionservice/types/car";

                    Response r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .get()
                        .get();
                    assertThat(r.getStatus()).isEqualTo(OK);
                    assertThat(r.getBody()).contains("[1,2]");
                }
            });
    }

    @Test
    public void callgetTypesError() {
        running(testServer(3333), new Runnable() {
                public void run() {
                    String path = "/transactionservice/types/-1";
                    Response r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .get()
                        .get();
                    assertThat(r.getStatus()).isEqualTo(OK);
                    assertThat(r.getBody()).contains("[]");
                }
            });
    }

    @Test
    public void callgetSumSuccess() {
        running(testServer(3333), new Runnable() {
                public void run() {
                    //Set up a couple of transactions
                    String path = "/transactionservice/transaction/1";
                    WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(Json.toJson(new Transaction(2.0, "car")))
                        .get();

                    path = "/transactionservice/transaction/2";
                    WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .put(Json.toJson(new Transaction(2.0, "car", 1L)))
                        .get();

                    path = "/transactionservice/sum/2";
                    Response r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .get()
                        .get();
                    assertThat(r.getStatus()).isEqualTo(OK);
                    assertThat(r.getBody()).contains("{\"sum\":4.0}");
                }
            });
    }

    @Test
   public void callgetSumError() {
        running(testServer(3333), new Runnable() {
                public void run() {
                    String path = "/transactionservice/sum/12";
                    Response r = WS.url("http://localhost:3333" + path)
                        .setHeader("Content-Type", "application/json")
                        .get()
                        .get();
                    assertThat(r.getStatus()).isEqualTo(BAD_REQUEST);
                    assertThat(r.getBody()).contains("Transaction not found");
                }
            });
    }

}
