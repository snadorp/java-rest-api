package controllers;

import models.Transaction;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import play.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.*;

public class TransactionService extends Controller {

    // GET /transactionservice/transaction/$transaction_id
    public static Result getTransaction(Long id) {
        Transaction t = Ebean.find(Transaction.class, id);
        if(t == null) {
            HashMap<String, String> status = new HashMap<String, String>();
            status.put("status", "Couldn't find transaction.");
            return badRequest(Json.toJson(status));
        } else {
            return ok(t.toJson());
        }
    }

    // PUT /transactionservice/transaction/$transaction_id
    @BodyParser.Of(BodyParser.Json.class)
    public static Result putTransaction(Long id) {
        RequestBody body = request().body();
        Transaction t = Transaction.fromJson(body.asJson());
        HashMap<String, String> status = new HashMap<String, String>();
        if(t == null) {
            return badRequest("Json body is malformed. Or not even there.");
        } else {
            if(t.parentId != null){
                if(Transaction.find.byId(t.parentId) == null) {
                    status.put("status", "Parent ID not found.");
                    return badRequest(Json.toJson(status));
                }
            }

            Transaction stored = Transaction.find.byId(id);
            if (stored != null) {
                stored.amount = t.amount;
                stored.type = t.type;
                stored.parentId = t.parentId;
                stored.save();
                status.put("status", "Transaction updated");
                return ok(Json.toJson(status));
            } else {
                t.id = id;
                Ebean.save(t);
                status.put("status", "ok");
                return ok(Json.toJson(status));
            }
        }
    }


    // GET /transactionservice/types/$type
    public static Result getTypes(String type) {
        List<Long> ids = new ArrayList<Long>();
        for(Transaction t : Transaction.findByType(type)) {
            ids.add(t.id);
        }
        return ok(Json.toJson(ids));
    }

    // GET /transactionservice/sum/$transaction_id
    public static Result getSum(Long id) {
        Transaction t = Transaction.find.byId(id);
        HashMap<String, String> status = new HashMap<String, String>();
        if(t == null) {
            status.put("status", "Transaction not found");
            return badRequest(Json.toJson(status));
        } else {
            Double sum = t.amount;

            // This could be dangerous in case someone has the great
            // idea to put circular referencing parent IDs into a Transaction.
            while(t.parentId != null) {
                t = Transaction.find.byId(t.parentId);
                sum += t.amount;
            }
            //Savy way of creating a fitting output.
            HashMap<String, Double> result = new HashMap<String, Double>();
            result.put("sum", sum);
            return ok(Json.toJson(result));
        }
    }

}
