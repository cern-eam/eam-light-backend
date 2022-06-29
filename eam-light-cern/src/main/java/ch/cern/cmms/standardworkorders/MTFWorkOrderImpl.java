package ch.cern.cmms.standardworkorders;

import javax.persistence.*;

@Entity
@NamedNativeQueries({
        @NamedNativeQuery(
                name = MTFWorkOrderImpl.GET_EQUIPMENT_SWO_MAX_STEP,
                query = "SELECT EVT_CODE, MTF_STEP" +
                        "  FROM cern_wo_mtf m1" +
                        " WHERE evt_object = :eqCode" +
                        "   AND evt_standwork = :swo" +
                        "   AND prv_code = ( SELECT MAX(to_number(m2.prv_code))" +
                        "                      FROM cern_wo_mtf m2" +
                        "                     WHERE m2.evt_standwork = m1.evt_standwork" +
                        "                       AND m2.evt_object = m1.evt_object)",
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
