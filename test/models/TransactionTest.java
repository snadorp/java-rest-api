package models;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import org.junit.*;
import static org.fest.assertions.Assertions.*;
import static play.test.Helpers.*;

public class TransactionTest {

    @Test
    public void toJsonOptionalParentIdTest() {
        Transaction t = new Transaction(1.0, "car");
        String transactionJson = t.toJson().toString();
        String expected = "{\"amount\":1.0,\"type\":\"car\"}";
        assertThat(transactionJson).isEqualTo(expected);
    }

    @Test
    public void toJsonFullTest() {
        Transaction t = new Transaction(1.0, "car", 224455L);
        String transactionJson = t.toJson().toString();
        String expected = "{\"amount\":1.0,\"type\":\"car\",\"parentId\":224455}";
        assertThat(transactionJson).isEqualTo(expected);
    }

    @Test
    public void fromJsonWithoutParentIdTest() {
        JsonNode json = new Transaction(1.0, "car").toJson();
        Transaction t = Transaction.fromJson(json);
        assertThat(t.amount).isEqualTo(1.0);
        assertThat(t.type).isEqualTo("car");
        assertThat(t.parentId).isEqualTo(null);
    }

    @Test
    public void fromJsonWithFullJsonTest() {
        JsonNode json = new Transaction(1.0, "car", 224455L).toJson();
        Transaction t = Transaction.fromJson(json);
        assertThat(t.amount).isEqualTo(1.0);
        assertThat(t.type).isEqualTo("car");
        assertThat(t.parentId).isEqualTo(224455L);
    }

    @Test
    public void fromJsonWithNullTest() {
        Transaction t = Transaction.fromJson(null);
        assertThat(t).isEqualTo(null);
    }

    @Test
    public void findByTypeTest() {
        //we need to initialize ebean
        running(fakeApplication(), new Runnable() {
                @Override
                public void run() {
                    //setup a couple of Transactions
                    Transaction t1 = new Transaction(1.0, "car");
                    t1.save();
                    Transaction t2 = new Transaction(1.0, "car");
                    t2.save();
                    Transaction t3 = new Transaction(1.0, "bike");
                    t3.save();

                    ArrayList<Transaction> expected = new ArrayList<Transaction>();
                    expected.add(t1);
                    expected.add(t2);
                    assertThat(Transaction.findByType("car")).isEqualTo(expected);

                    expected = new ArrayList<Transaction>();
                    expected.add(t3);
                    assertThat(Transaction.findByType("bike")).isEqualTo(expected);
                }
            });
    }

    // you are probably wondering why there's no test with invalid
    // JSON. Well, `JsonNode` can not be initialized with some invalid
    // stuff so the checking is already happening way earlier.
}
