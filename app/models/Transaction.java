package models;

import java.util.*;
import javax.persistence.*;

import play.data.format.*;
import play.data.validation.*;
import play.db.ebean.*;
import play.libs.Json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Transaction extends Model{

    @Id
    @Constraints.Required
    @JsonIgnore(true)
    public Long id = null;

    @Constraints.Required
    public Double amount = 0.0;

    @Constraints.Required
    public String type = "";

    @JsonInclude(Include.NON_NULL)
    public Long parentId = null;

    // Empty constructor is needed for json bindings
    public Transaction() {}

    public Transaction(Double amount, String type) {
        this.amount = amount;
        this.type = type;
    }

    public Transaction(Double amount, String type, Long parentId) {
        this.amount = amount;
        this.type = type;
        this.parentId = parentId;
    }

    public JsonNode toJson() {
        return Json.toJson(this);
    }

    protected void mergeTransaction(Transaction other) {

    }

    public static Transaction fromJson(JsonNode json) {
        if(json == null) {
            return null; //uh yes, Java land!
        } else {
            try {
                return Json.fromJson(json, Transaction.class);
            }
            catch (Exception e) {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();
                return null;
            }

        }
    }

    public static List<Transaction> findByType(String type) {
        return find.where()
            .ieq("type", type)
            .findList();
    }

    public static Finder<Long,Transaction> find = new Finder<Long,Transaction>(Long.class, Transaction.class);

}
