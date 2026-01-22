package vti.group10.football_booking.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vti.group10.football_booking.model.FootballField;

@Repository
public interface FootballFieldRepository extends JpaRepository<FootballField, Integer> {

    // Lấy tất cả sân theo clusterId
    List<FootballField> findByClusterId(int clusterId);
    List<FootballField> findByClusterIdAndVisible(int clusterId, FootballField.YesNo visible);
    // Search sân theo tên sân hoặc tên cluster
    @Query("SELECT f FROM FootballField f " +
            "WHERE (LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(f.cluster.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND f.visible = 'YES'")
    List<FootballField> searchFields(@Param("keyword") String keyword);

    // Filter sân con theo giá và cluster
    @Query("SELECT f FROM FootballField f " +
            "WHERE (:minPrice IS NULL OR f.pricePerHour >= :minPrice) " +
            "AND (:maxPrice IS NULL OR f.pricePerHour <= :maxPrice) " +
            "AND (:clusterName IS NULL OR LOWER(f.cluster.name) LIKE LOWER(CONCAT('%', :clusterName, '%'))) " +
            "AND f.visible = 'YES'")
    List<FootballField> filterFieldsByPriceAndCluster(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("clusterName") String clusterName
    );

    // Filter theo địa điểm cluster + giá
    @Query("SELECT f FROM FootballField f " +
            "WHERE (:city IS NULL OR LOWER(f.cluster.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
            "AND (:district IS NULL OR LOWER(f.cluster.district) LIKE LOWER(CONCAT('%', :district, '%'))) " +
            "AND (:address IS NULL OR LOWER(f.cluster.address) LIKE LOWER(CONCAT('%', :address, '%'))) " +
            "AND (:minPrice IS NULL OR f.pricePerHour >= :minPrice) " +
            "AND (:maxPrice IS NULL OR f.pricePerHour <= :maxPrice)")
    Page<FootballField> filterFootballFields(
            @Param("city") String city,
            @Param("district") String district,
            @Param("address") String address,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // Filter theo khoảng giá cố định (rangeType: 1,2,3)
    @Query("SELECT f FROM FootballField f " +
            "WHERE ((:rangeType = 1 AND f.pricePerHour BETWEEN 1000 AND 200000) " +
            "   OR (:rangeType = 2 AND f.pricePerHour BETWEEN 201000 AND 400000) " +
            "   OR (:rangeType = 3 AND f.pricePerHour BETWEEN 401000 AND 600000)) " +
            "AND (:clusterName IS NULL OR LOWER(f.cluster.name) LIKE LOWER(CONCAT('%', :clusterName, '%')))")
    List<FootballField> filterByPriceRange(
            @Param("rangeType") int rangeType,
            @Param("clusterName") String clusterName
    );
}
