package au.com.gramline.gramdispatch.pojo;

import com.google.gson.annotations.SerializedName;

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
        @SerializedName("DESCRIPTION")
        public String DESCRIPTION;
        @SerializedName("QTYREQD")
        public Integer QTYREQD;
        @SerializedName("QtyCollected")
        public Integer QTYCollected;
        @SerializedName("Bundle")
        public String Bundle;

        public CollectedOrder(Integer SEQNO, Integer HDR_SEQNO, String DESCRIPTION, Integer QTYREQD, Integer QTYCollected, String Bundle) {
            this.Bundle = Bundle;
            this.QTYREQD = QTYREQD;
            this.DESCRIPTION = DESCRIPTION;
            this.SEQNO = SEQNO;
            this.QTYCollected = QTYCollected;
            this.HDR_SEQNO = HDR_SEQNO;
        }
    }
}
