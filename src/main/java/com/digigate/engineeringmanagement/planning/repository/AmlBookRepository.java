package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.AmlBook;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * AML Book repository
 *
 * @author ashinisingha
 */
@Repository
public interface AmlBookRepository extends AbstractRepository<AmlBook> {
    @Query("SELECT amlBook from AmlBook amlBook where" +
            " amlBook.bookNo = :bookNo" +
            " and amlBook.isActive = true ")
    AmlBook findAmlBookByBookNo(String bookNo);

    @Query(" SELECT amlBook.id from AmlBook amlBook where " +
            " amlBook.aircraftId = :aircraftId and amlBook.startPageNo = :startPageNo")
    Optional<Long> findBYAircraftIdAndStartPage(
            @Param("aircraftId") Long aircraftId,
            @Param("startPageNo") Integer startPageNo
    );

    @Query("SELECT amlBook.startPageNo from AmlBook amlBook where" +
            " amlBook.aircraftId = :aircraftId" +
            " and amlBook.startPageNo <= :pageNumber and amlBook.endPageNo >= : pageNumber" +
            " and amlBook.isActive = true")
    Optional<Integer> findAmlBookByAircraftAndPageNumber(Long aircraftId, Integer pageNumber);
}
