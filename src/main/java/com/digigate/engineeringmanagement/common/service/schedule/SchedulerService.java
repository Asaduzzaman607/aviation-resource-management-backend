package com.digigate.engineeringmanagement.common.service.schedule;

import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.VendorService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedulerService {
    private final VendorService vendorService;
    private final StorePartSerialService storePartSerialService;

    public SchedulerService(VendorService vendorService, StorePartSerialService storePartSerialService) {
        this.vendorService = vendorService;
        this.storePartSerialService = storePartSerialService;
    }

    /**
     * Run at 12:01 AM every day
     */
    @Scheduled(cron = "0 1 18 * * *")
    @Transactional
    public void updateStoreExpiredItems() {
        vendorService.updateExpiredVendors();
        storePartSerialService.updateExpiredStorePartSerials();
    }

}
