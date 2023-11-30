package com.digigate.engineeringmanagement.status.repository;

import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.status.dto.response.DemandStatusResponseDto;
import com.digigate.engineeringmanagement.status.entity.DemandStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandStatusRepository extends AbstractRepository<DemandStatus> {

    Optional<DemandStatus> findByPartIdAndChildId(Long partId, Long childId);

    DemandStatus findByPartIdAndChildIdAndVoucherType(Long partId, Long childId, VoucherType voucherType);

    @Query(" SELECT new com.digigate.engineeringmanagement.status.dto.response.DemandStatusResponseDto(" +
            " ds.demandId," +
            "sd.voucherNo, " +
            " ds.quantity, " +
            " ds.partId," +
            " p.partNo, " +
            " wa.name, " +
            " ds.voucherType," +
            "ds.workFlowType," +
            "ds.module," +
            "ds.isRejected," +
            "ds.isActiveStatus," +
            "ds.inputType" +
            ")from DemandStatus as ds inner join StoreDemand as sd  on sd.id = ds.demandId " +
            "inner join Part as p on p.id = ds.partId left join " +
            "WorkFlowAction as wa on ds.workFlowActionId = wa.id where  ds.demandId =:demandId ")
    List<DemandStatusResponseDto> findPartStatusByDemand(Long demandId);

    @Query(" SELECT new com.digigate.engineeringmanagement.status.dto.response.DemandStatusResponseDto(" +
            " ds.demandId," +
            "sd.voucherNo, " +
            " ds.quantity, " +
            " ds.partId," +
            " p.partNo, " +
            " wa.name, " +
            " ds.voucherType," +
            "ds.workFlowType," +
            "ds.module," +
            "ds.isRejected," +
            "ds.isActiveStatus," +
            "ds.inputType" +
            ") from DemandStatus as ds inner join StoreDemand as sd  on sd.id = ds.demandId " +
            "inner join Part as p on p.id = ds.partId inner join " +
            "WorkFlowAction as wa on ds.workFlowActionId = wa.id " +
            "where  ds.demandId =:demandId and ds.partId=:partId ")
    List<DemandStatusResponseDto> findPartStatusByDemandIdAndPartId(Long demandId, Long partId);

    @Query(value = "select * from demand_status \n" +
            "  where child_id = :childId and \n" +
            "  module = :module and \n" +
            "  voucher_type ='PO' OR voucher_type ='LO' OR voucher_type = 'RO'", nativeQuery = true)
    List<DemandStatus> findByChildIdAndModule(Long childId, String module);

    DemandStatus findByDemandIdAndChildIdAndPartIdAndVoucherType(Long demandId, Long childId, Long demandStatusPartId, VoucherType voucherType);

    void deleteAllByDemandIdAndChildIdAndVoucherType(Long demandId, Long childId, VoucherType voucherType);

    @Query(value = "delete from demand_status where \n" +
            "  demand_id = :demandId and \n" +
            "  child_id = :childId and \n" +
            "  voucher_type ='PO' OR voucher_type ='LO' OR voucher_type = 'RO'", nativeQuery = true)
    void deleteAllByDemandIdAndChildId(Long demandId, Long childId);

    @Query(value = "select * from demand_status \n" +
            "  where demand_id = :demandId and \n" +
            "  child_id = :childId and \n" +
            "  vendor_quotation_invoice_details_id = :vendorQuotationInvoiceDetailId and \n" +
            "  part_id = :partId and\n" +
            "  where voucher_type = :voucherType", nativeQuery = true)
    DemandStatus findByDemandIdAndPartIdAndChildIdAndVoucherTypeAndVendorQuotationInvoiceDetailsId(Long demandId, Long partId, Long childId, VoucherType voucherType,Long vendorQuotationInvoiceDetailId);

    DemandStatus findByPartIdAndChildIdAndVoucherTypeAndVendorQuotationInvoiceDetailsId(Long partId, Long childId, VoucherType voucherType,Long vendorQuotationInvoiceDetailId);

    @Query(value = "select * from demand_status \n" +
            "  where child_id = :childId and \n" +
            "  vendor_quotation_invoice_details_id = :vendorQuotationInvoiceDetailId and \n" +
            "  part_id = :partId and\n" +
            "  voucher_type in('PO','LO','RO','ORDER')  and module = :module", nativeQuery = true)
    DemandStatus findByPartIdAndChildIdAndVoucherTypeAndVendorQuotationInvoiceDetailsIdAndModule(Long partId, Long childId, Long vendorQuotationInvoiceDetailId, String module);
    void deleteAllByDemandIdAndChildIdAndVendorQuotationInvoiceDetailsIdAndVoucherType(Long demandId, Long childId, Long vendorQuotationInvoiceDetailId, VoucherType voucherType);

    DemandStatus findByDemandIdAndChildIdAndPartIdAndVoucherTypeAndVendorQuotationInvoiceDetailsId(Long demandId, Long childId, Long partId, VoucherType voucherType, Long vendorQuotationInvoiceDetailId);


    DemandStatus findByDemandIdAndChildIdAndPartIdAndVoucherTypeAndVendorQuotationInvoiceDetailsIdAndModule(Long demandId, Long childId, Long partId, VoucherType voucherType, Long vendorQuotationInvoiceDetailId, String module);


    List<DemandStatus> findByChildIdAndVoucherTypeAndWorkFlowType(Long piId, VoucherType voucherType, String workflowType);
}
