package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.payload.response.ModelExcelResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Model Repository
 *
 * @author Asifur Rahman
 */
@Repository
public interface ModelRepository extends AbstractRepository<Model> {
    Optional<Model> findByModelName(String modelName);

    Optional<Model> findByModelNameAndIdNot(String modelName, Long id);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto (" +
            "m.id, " +
            "m.modelName, " +
            "m.modelType" + ") " +
            "from Model m where m.aircraftModelId = :aircraftModelId and  m.isActive = true ")
    List<ModelResponseByAircraftDto> findAllByAircraftModelId(Long aircraftModelId);

    @Query("select model.modelName from Model model where model.aircraftModelId=:aircraftModelId " +
            " and model.isActive = true ")
    Set<String> findModelNamesByAircraftModelId(@Param("aircraftModelId") Long aircraftModelId);

    Set<Model> findAllByAircraftModelIdAndIsActiveTrue(Long aircraftModelId);

    @Query("select m.id from Model m where m.aircraftModelId = :aircraftModelId and m.isActive = true")
    Set<Long> findModelIdsByAircraftModelId(Long aircraftModelId);

    @Query("select m from Model m where m.aircraftModelId = :aircraftModelId and m.isActive = true")
    List<Model> findAllModelByAircraftModelId(Long aircraftModelId);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.ModelExcelResponseDto(" +
            "m.modelName, " +
            "m.modelType, " +
            "m.description, " +
            "a.aircraftModelName " +
            ")" +
            "from Model m " +
            "join AircraftModel a on a.id = m.aircraftModelId " +
            "where m.isActive = true and a.isActive = true ")
    List<ModelExcelResponseDto> findAllActiveModel();

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto (" +
            "m.id, " +
            "m.modelName, " +
            "m.modelType" + ") " +
            "from Model m where m.aircraftModelId = :aircraftModelId and " +
            "m.modelType = :consumableModelType  and  m.isActive = true ")
    List<ModelResponseByAircraftDto> findModelByAircraftModelAAndModelType(Long aircraftModelId, ModelType consumableModelType);
}
