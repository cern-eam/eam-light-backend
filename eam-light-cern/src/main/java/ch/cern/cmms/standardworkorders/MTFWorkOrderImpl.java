package ch.cern.cmms.standardworkorders;

import javax.persistence.*;

@Entity
@NamedNativeQueries({
        @NamedNativeQuery(
                name = MTFWorkOrderImpl.GET_EQUIPMENT_SWO_MAX_STEP,
                query = "SELECT MIN(stp_evtcode) EVT_CODE" +
                        "   , MAX(STP_ID) MTF_STEP" +
                        " FROM mtf_steps m1 " +
                        " WHERE stp_object = :eqCode " +
                        "  AND stp_standwo = :swo " +
                        "  AND stp_id = (SELECT MAX(to_number(m2.stp_id)) " +
                        "                FROM mtf_steps m2 " +
                        "                WHERE m2.stp_standwo = m1.stp_standwo " +
                        "                  AND m2.stp_object = m1.stp_object)",
                resultClass = MTFWorkOrderImpl.class
        ),
})
public class MTFWorkOrderImpl implements MTFWorkOrder {
    public static final String GET_EQUIPMENT_SWO_MAX_STEP = "MTFStandardWorkOrderImpl.GET_EQUIPMENT_SWO_MAX_STEP";

    @Id
    @Column(name = "EVT_CODE")
    private String number;

    @Column(name = "MTF_STEP")
    private String step;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
