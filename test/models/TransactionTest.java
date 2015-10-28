package models;

import org.junit.*;

import java.util.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

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
}
