package de.uniba.dsg.jpb.data.access.jpa;

import de.uniba.dsg.jpb.data.model.jpa.StockEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<StockEntity, String> {

  Optional<StockEntity> findByProductIdAndWarehouseId(String productId, String warehouseId);

  @Query(
      value =
          "SELECT * FROM stocks WHERE warehouse_id = :warehouseId AND product_id IN :productIds AND quantity < :quantityThreshold",
      nativeQuery = true)
  List<StockEntity> findByWarehouseIdAndProductIdAndQuantityThreshold(
      String warehouseId, Collection<String> productIds, int quantityThreshold);

  List<StockEntity> findByWarehouseId(String warehouseId);
}
