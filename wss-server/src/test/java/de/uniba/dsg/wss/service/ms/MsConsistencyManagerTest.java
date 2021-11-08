package de.uniba.dsg.wss.service.ms;

import de.uniba.dsg.wss.data.access.ms.DataConsistencyManager;
import de.uniba.dsg.wss.data.model.ms.CustomerData;
import de.uniba.dsg.wss.data.model.ms.DistrictData;
import de.uniba.dsg.wss.data.model.ms.MsDataRoot;
import de.uniba.dsg.wss.data.model.ms.WarehouseData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MsConsistencyManagerTest extends MicroStreamServiceTest{

    @Autowired
    private DataConsistencyManager consistencyManager;
    @Autowired
    private MsDataRoot dataRoot;

    @BeforeEach
    public void setUp() {
        prepareTestStorage();
    }

    @Test
    public void checkDifferentCustomerDataObjectsAfterStoring(){
        String customerId = "C0";
        CustomerData originalCustomer = this.dataRoot.getCustomers().get(customerId);
        WarehouseData warehouse = this.dataRoot.getWarehouses().get("W0");
        DistrictData district = warehouse.getDistricts().get("D0");

        CustomerData copy = this.consistencyManager.storePaymentAndUpdateDependentObjects(warehouse, district, originalCustomer, originalCustomer.getPaymentRefs().get(0));
        // important that we get a copy of the customer object
        Assertions.assertNotEquals(originalCustomer.hashCode() , copy.hashCode());
    }
}
