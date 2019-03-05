package au.com.gramline.gramdispatch.pojo;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CollectedOrderList {
    @SerializedName("results")
    public List<CollectedOrder> results = new ArrayList<>();
    @SerializedName("bundles")
    public List<Bundle> bundles = new ArrayList<>();

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
        @SerializedName("ORDERDATE")
        public String ORDERDATE;
        @SerializedName("QtyCollected")
        public Integer QtyCollected;
        @SerializedName("QtyPacked")
        public Integer QtyPacked;
        @SerializedName("QtyLoaded")
        public Integer QtyLoaded;
        @SerializedName("Bundle")
        public String Bundle;
    }

    public static class Bundle {
        @SerializedName("bundle_name")
        public String bundle_name;
        @SerializedName("weight")
        public Integer weight;
    }
}
