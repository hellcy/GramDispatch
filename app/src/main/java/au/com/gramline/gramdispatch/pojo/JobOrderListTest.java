package au.com.gramline.gramdispatch.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JobOrderListTest {
    @SerializedName("results")
    public List<JobOrder> data = new ArrayList<>();

    public class JobOrder {
        @SerializedName("SEQNO")
        public Integer SEQNO;
        @SerializedName("HDR_SEQNO")
        public Integer HDR_SEQNO;
        @SerializedName("STOCKCODE")
        public String STOCKCODE;
        @SerializedName("DESCRIPTION")
        public String DESCRIPTION;
        @SerializedName("QTYREQD")
        public Double QTYREQD;
        @SerializedName("QTYUSED")
        public Double QTYUSED;
        @SerializedName("BATCHCODE")
        public String BATCHCODE;
        @SerializedName("X_LENGTH")
        public Integer X_LENGTH;
        @SerializedName("X_COLOR")
        public String X_COLOR;
        @SerializedName("X_SOLINE")
        public Integer X_SOLINE;
        @SerializedName("X_NARRATIVE")
        public Integer X_NARRATIVE;
        @SerializedName("X_LINESTATUS")
        public Integer X_LINESTATUS;
    }

}