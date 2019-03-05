package au.com.gramline.gramdispatch.pojo;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class JobOrderList {
    @SerializedName("results")
    public List<JobOrder> results = new ArrayList<>();

    public class JobOrder {
        @SerializedName("ORDERDATE")
        public String ORDERDATE;
        @SerializedName("ACCNO")
        public Integer ACCNO;
        @SerializedName("HDR_SEQNO")
        public Integer HDR_SEQNO;
        @SerializedName("STOCKCODE")
        public String STOCKCODE;
        @SerializedName("DESCRIPTION")
        public String DESCRIPTION;
        @SerializedName("ORD_QUANT")
        public Double ORD_QUANT;
        @SerializedName("SEQNO")
        public Integer SEQNO;

    }

}