package au.com.gramline.gramdispatch.pojo;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CollectedOrderList {
    @SerializedName("results")
    public List<CollectedOrder> data = new ArrayList<>();

    public static class CollectedOrder {
        @SerializedName("SEQNO")
        public Integer SEQNO;
        @SerializedName("HDR_SEQNO")
        public Integer HDR_SEQNO;
        @SerializedName("ACCNO")
        public Integer ACCNO;
        @SerializedName("STOCKCODE")
        public String STOCKCODE;
        @SerializedName("DESCRIPTION")
        public String DESCRIPTION;
        @SerializedName("ORD_QUANT")
        public Integer ORD_QUANT;
        @SerializedName("QtyCollected")
        public Integer QTYCollected;
        @SerializedName("Bundle")
        public String Bundle;
        @SerializedName("ORDERDATE")
        public Date ORDERDATE;

    }
}
