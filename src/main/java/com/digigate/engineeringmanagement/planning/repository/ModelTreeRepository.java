package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.ModelTree;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeExcelViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.PositionModelView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Model Tree Repository
 *
 * @author Masud Rana
 */
@Repository
public interface ModelTreeRepository extends AbstractRepository<ModelTree> {

    @Query("select mt.id from ModelTree mt " +
            "where (:modelId is null or mt.modelId=:modelId) " +
            "and (:higherModelId is null or mt.higherModelId=:higherModelId) " +
            "and (:locationId is null or mt.locationId=:locationId) " +
            "and (:positionId is null or mt.positionId=:positionId)")
    Optional<Long> findIdForUniqueEntry(@Param("modelId") Long modelId,
                                        @Param("higherModelId") Long higherModelId,
                                        @Param("locationId") Long locationId,
                                        @Param("positionId") Long positionId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response" +
            ".ModelTreeViewModel(mt.modelId, mt.model.modelName) " +
            "from ModelTree mt " +
            "where (:higherModelId is null or mt.higherModelId=:higherModelId) order by mt.model.modelName")
    List<ModelTreeViewModel> findAllByHigherModelId(@Param("higherModelId") Long higherModelId);

    @Query("select mt.id from ModelTree mt " +
            "where (:modelId is null or mt.modelId=:modelId) " +
            "and (:higherModelId is null or mt.higherModelId=:higherModelId)")
    List<Long> findReverseModelTree(@Param("modelId") Long modelId,
                                        @Param("higherModelId") Long higherModelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response" +
            ".ModelTreeViewModel(mt.locationId, mt.aircraftLocation.name , mt.positionId, mt.position.name) " +
            "from ModelTree  mt " +
            "where (:modelId is null or mt.modelId=:modelId) " +
            "and (:higherModelId is null or mt.higherModelId=:higherModelId)")
    List<ModelTreeViewModel> findLocationAndPosition(
            @Param("higherModelId") Long higherModelId, @Param("modelId") Long modelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response" +
            ".ModelTreeViewModel(mt.id, mt.locationId, mt.modelId, mt.higherModelId, mt.positionId) " +
            "from ModelTree mt " +
            "where mt.higherModelId in :higherModelIds")
    Page<ModelTreeViewModel> findAllByHigherModelIdIn(Set<Long> higherModelIds, Pageable pageable);

    @Query("select mt from ModelTree mt where mt.higherModelId in :higherModelIds or mt.modelId in :higherModelIds " +
            "and mt.isActive = true")
    Page<ModelTree> findAllModelTreeByIdIn(Set<Long> higherModelIds, Pageable pageable);

    @Query("select m.position from ModelTree m where m.modelId = :modelId and m.isActive = true")
    List<Position> getPositionListByModelId(Long modelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.PositionModelView(" +
            "m.position, m.modelId) " +
            "from ModelTree m where m.modelId in :modelIds and m.isActive = true")
    List<PositionModelView> getPositionListByModelIds(Set<Long> modelIds);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.ModelTreeExcelViewModel(" +
            "m.modelName, " +
            "ml.modelName, " +
            "al.name, " +
            "p.name " +
            ")" +
            "from ModelTree mt " +
            "left join Position p on p.id = mt.positionId " +
            "join Model m on m.id = mt.modelId " +
            "join AircraftLocation al on al.id = mt.locationId " +
            "join Model ml on ml.id = mt.higherModelId " +
            "where mt.isActive = true and " +
            "(p.isActive = true or p.id is null) and " +
            "m.isActive = true and " +
            "al.isActive = true and " +
            "ml.isActive = true ")
    List<ModelTreeExcelViewModel> findAllModelTree();
}
